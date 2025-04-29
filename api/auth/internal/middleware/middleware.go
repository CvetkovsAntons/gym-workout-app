package middleware

import (
	"context"
	"errors"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/cache"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/enums"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/services"
	"github.com/CvetkovsAntons/gwa-api-auth-service/pkg/response"
	"github.com/golang-jwt/jwt/v4"
	"net/http"
	"strings"
)

type Middleware interface {
	Headers(next http.Handler) http.Handler
	Token(tokenType enums.TokenType, next http.Handler) http.Handler
}

type middleware struct {
	redis *cache.Redis
	user  services.UserService
	jwt   services.JWTService
}

func NewMiddleware(redis *cache.Redis) Middleware {
	return &middleware{
		redis: redis,
		user:  services.NewUserService(redis),
		jwt:   services.NewJwtService(redis),
	}
}

func (mw *middleware) Headers(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.Header.Get("Content-Type") != "application/json" {
			response.WriteError(w, http.StatusBadRequest, "Invalid content type")
			return
		}

		next.ServeHTTP(w, r)
	})
}

func (mw *middleware) Token(tokenType enums.TokenType, next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		token := r.Header.Get("Authorization")
		if token == "" {
			response.WriteError(w, http.StatusUnauthorized, "Missing Authorization header")
			return
		}

		tokenParts := strings.Split(token, " ")
		if len(tokenParts) != 2 || tokenParts[0] != "Bearer" {
			response.WriteError(w, http.StatusUnauthorized, "Invalid Authorization header")
			return
		}

		token = tokenParts[1]

		claims, err := mw.jwt.GetTokenClaims(tokenType, token)
		if err != nil {
			var v *jwt.ValidationError
			if errors.As(err, &v) {
				response.WriteError(w, http.StatusUnauthorized, v.Error())
				return
			}
			response.WriteError(w, http.StatusUnauthorized, "Invalid token")
			return
		}

		userId, ok := claims["user_id"].(string)
		if !ok {
			response.WriteError(w, http.StatusUnauthorized, "Invalid token claims")
			return
		}

		exists, err := mw.jwt.TokenExists(r.Context(), tokenType, token)
		if err != nil {
			response.WriteInternalServerError(w, err)
			return
		} else if !exists {
			response.WriteError(w, http.StatusUnauthorized, "Session expired or logged out")
			return
		}

		ctx := context.WithValue(r.Context(), "user_id", userId)
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}
