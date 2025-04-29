package handlers

import (
	"encoding/json"
	"errors"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/cache"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/models"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/services"
	"github.com/CvetkovsAntons/gwa-api-auth-service/pkg/response"
	"go.mongodb.org/mongo-driver/mongo"
	"net/http"
)

type UserHandler interface {
	ServeHTTP(http.ResponseWriter, *http.Request)
	delete(http.ResponseWriter, *http.Request)
	getInfo(http.ResponseWriter, *http.Request)
	putInfo(http.ResponseWriter, *http.Request)
}

type userHandler struct {
	redis *cache.Redis
	user  services.UserService
	jwt   services.JWTService
}

func NewUserHandler(redis *cache.Redis) UserHandler {
	return &userHandler{
		redis: redis,
		user:  services.NewUserService(redis),
		jwt:   services.NewJwtService(redis),
	}
}

func (u *userHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	switch {
	case r.Method == http.MethodGet && r.URL.Path == "/user/isAuthenticated":
		u.isAuthenticated(w, r)
		return
	case r.Method == http.MethodDelete && r.URL.Path == "/user/delete":
		u.delete(w, r)
		return
	case r.Method == http.MethodGet && r.URL.Path == "/user/info":
		u.getInfo(w, r)
		return
	case r.Method == http.MethodPut && r.URL.Path == "/user/info":
		u.putInfo(w, r)
		return
	default:
		response.WriteError(w, http.StatusNotFound, "Page not found")
		return
	}
}

func (u *userHandler) isAuthenticated(w http.ResponseWriter, r *http.Request) {
	response.WriteMessage(w, http.StatusOK, "User is authenticated")
}

func (u *userHandler) delete(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	userId, ok := ctx.Value("user_id").(string)
	if !ok || userId == "" {
		response.WriteError(w, http.StatusUnauthorized, "Invalid user ID")
		return
	}

	if err := u.jwt.DeleteTokens(ctx, userId); err != nil {
		response.WriteInternalServerError(w, err)
		return
	}

	if err := u.user.DeleteUser(ctx, userId); err != nil {
		if errors.Is(err, mongo.ErrNoDocuments) {
			response.WriteError(w, http.StatusUnauthorized, "User doesn't exist")
			return
		}

		response.WriteInternalServerError(w, err)
		return
	}

	response.WriteMessage(w, http.StatusOK, "User deleted successfully")
}

func (u *userHandler) getInfo(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	userId, ok := ctx.Value("user_id").(string)
	if !ok || userId == "" {
		response.WriteError(w, http.StatusUnauthorized, "Invalid user ID")
		return
	}

	user, err := u.user.GetUserById(ctx, userId)
	if err != nil && !errors.Is(err, mongo.ErrNoDocuments) {
		response.WriteInternalServerError(w, err)
		return
	} else if user == nil {
		response.WriteError(w, http.StatusUnauthorized, "User doesn't exist")
		return
	}

	userData := u.user.GetUserData(user)

	response.Write(w, http.StatusOK, userData)
}

func (u *userHandler) putInfo(w http.ResponseWriter, r *http.Request) {
	var request models.UserData
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		response.WriteError(w, http.StatusBadRequest, "Invalid request body")
		return
	}

	ctx := r.Context()

	userId, ok := ctx.Value("user_id").(string)
	if !ok || userId == "" {
		response.WriteError(w, http.StatusUnauthorized, "Invalid user ID")
		return
	}

	user, err := u.user.GetUserById(ctx, userId)
	if err != nil && !errors.Is(err, mongo.ErrNoDocuments) {
		response.WriteInternalServerError(w, err)
		return
	} else if user == nil {
		response.WriteError(w, http.StatusUnauthorized, "User doesn't exist")
		return
	}

	err = u.user.UpdateUserData(ctx, user, request)
	if err != nil && !errors.Is(err, mongo.ErrNoDocuments) {
		response.WriteInternalServerError(w, err)
		return
	}

	response.WriteMessage(w, http.StatusOK, "User updated successfully")
}
