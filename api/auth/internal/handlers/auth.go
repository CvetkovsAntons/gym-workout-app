package handlers

import (
	"encoding/json"
	"errors"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/cache"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/models"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/services"
	"github.com/CvetkovsAntons/gwa-api-auth-service/pkg/response"
	"github.com/google/uuid"
	"go.mongodb.org/mongo-driver/mongo"
	"golang.org/x/crypto/bcrypt"
	"net/http"
	"regexp"
	"time"
)

type AuthHandler interface {
	ServeHTTP(http.ResponseWriter, *http.Request)
	login(http.ResponseWriter, *http.Request)
	register(http.ResponseWriter, *http.Request)
	refresh(http.ResponseWriter, *http.Request)
	logout(http.ResponseWriter, *http.Request)
}

type authHandler struct {
	redis *cache.Redis
	user  services.UserService
	jwt   services.JWTService
}

func NewAuthHandler(redis *cache.Redis) AuthHandler {
	return &authHandler{
		redis: redis,
		user:  services.NewUserService(redis),
		jwt:   services.NewJwtService(redis),
	}
}

func (auth *authHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	switch {
	case r.Method == http.MethodPost && r.URL.Path == "/auth/register":
		auth.register(w, r)
		return
	case r.Method == http.MethodPost && r.URL.Path == "/auth/login":
		auth.login(w, r)
		return
	case r.Method == http.MethodPost && r.URL.Path == "/auth/refresh":
		auth.refresh(w, r)
		return
	case r.Method == http.MethodPost && r.URL.Path == "/auth/logout":
		auth.logout(w, r)
		return
	default:
		response.WriteError(w, http.StatusNotFound, "Page not found")
		return
	}
}

func (auth *authHandler) register(w http.ResponseWriter, r *http.Request) {
	var request struct {
		Email           string `json:"email"`
		Password        string `json:"password"`
		PasswordConfirm string `json:"password_confirm"`
	}
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		response.WriteError(w, http.StatusBadRequest, "Invalid request body")
		return
	}
	if request.Email == "" || request.Password == "" || request.PasswordConfirm == "" {
		response.WriteError(w, http.StatusBadRequest, "Missing mandatory parameters")
		return
	}
	if !auth.isValidEmail(request.Email) {
		response.WriteError(w, http.StatusBadRequest, "Email format is incorrect")
		return
	}
	if request.Password != request.PasswordConfirm {
		response.WriteError(w, http.StatusBadRequest, "Passwords do not match")
		return
	}

	if exists, err := auth.user.UserWithEmailExists(r.Context(), request.Email); err != nil {
		response.WriteInternalServerError(w, err)
		return
	} else if exists {
		response.WriteError(w, http.StatusUnauthorized, "User with provided e-mail already exists")
		return
	}

	passwordHash, err := bcrypt.GenerateFromPassword([]byte(request.Password), bcrypt.DefaultCost)
	if err != nil {
		response.WriteInternalServerError(w, err)
		return
	}

	user := &models.User{
		ID:        uuid.New().String(),
		Email:     request.Email,
		Password:  string(passwordHash),
		CreatedAt: time.Now(),
		Data:      models.UserData{},
	}

	err = auth.user.CreateUser(r.Context(), user)
	if err != nil {
		response.WriteInternalServerError(w, err)
		return
	}

	response.WriteMessage(w, http.StatusCreated, "User registered successfully")
}

func (auth *authHandler) login(w http.ResponseWriter, r *http.Request) {
	var request struct {
		Email    string `json:"email"`
		Password string `json:"password"`
	}
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		response.WriteError(w, http.StatusBadRequest, "Invalid request body")
		return
	}
	if request.Email == "" || request.Password == "" {
		response.WriteError(w, http.StatusBadRequest, "Missing mandatory parameters")
		return
	}
	if !auth.isValidEmail(request.Email) {
		response.WriteError(w, http.StatusBadRequest, "Email format is incorrect")
		return
	}

	user, err := auth.user.GetUserByEmail(r.Context(), request.Email)
	if err != nil && !errors.Is(err, mongo.ErrNoDocuments) {
		response.WriteInternalServerError(w, err)
		return
	} else if user == nil {
		response.WriteError(w, http.StatusUnauthorized, "User with provided email doesn't exist")
		return
	}

	err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(request.Password))
	if err != nil {
		response.WriteError(w, http.StatusUnauthorized, "Password is incorrect")
		return
	}

	accessToken, refreshToken, err := auth.jwt.GenerateTokens(r.Context(), user.ID)
	if err != nil {
		response.WriteInternalServerError(w, err)
		return
	}

	response.Write(w, http.StatusOK, map[string]string{
		"access_token":  accessToken,
		"refresh_token": refreshToken,
	})
}

func (auth *authHandler) refresh(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	userId := ctx.Value("user_id").(string)

	if exists, err := auth.user.UserWithIdExists(ctx, userId); err != nil {
		response.WriteInternalServerError(w, err)
		return
	} else if !exists {
		response.WriteError(w, http.StatusUnauthorized, "User with provided ID doesn't exist")
		return
	}

	accessToken, refreshToken, err := auth.jwt.GenerateTokens(ctx, userId)
	if err != nil {
		response.WriteInternalServerError(w, err)
		return
	}

	response.Write(w, http.StatusOK, map[string]string{
		"access_token":  accessToken,
		"refresh_token": refreshToken,
	})
}

func (auth *authHandler) logout(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	userId := ctx.Value("user_id").(string)

	if exists, err := auth.user.UserWithIdExists(ctx, userId); err != nil {
		response.WriteInternalServerError(w, err)
		return
	} else if exists == false {
		response.WriteError(w, http.StatusUnauthorized, "User with provided ID doesn't exist")
		return
	}

	if err := auth.jwt.DeleteTokens(ctx, userId); err != nil {
		response.WriteInternalServerError(w, err)
		return
	}

	response.WriteMessage(w, http.StatusOK, "User logged out successfully")
}

func (auth *authHandler) isValidEmail(email string) bool {
	emailRegex := regexp.MustCompile(`^[a-z0-9._%+\-]+@[a-z0-9.\-]+\.[a-z]{2,4}$`)
	return emailRegex.MatchString(email)
}
