package main

import (
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/cache"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/database"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/enums"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/env"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/handlers"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/middleware"
	"log"
	"net/http"
	"os"
)

func main() {
	database.InitMongoDBClient()

	redisClient, err := cache.NewRedis()
	if err != nil {
		log.Fatal(err)
		return
	}

	mux := http.NewServeMux()

	mw := middleware.NewMiddleware(redisClient)
	authHandler := handlers.NewAuthHandler(redisClient)
	userHandler := handlers.NewUserHandler(redisClient)

	// auth group
	mux.Handle("/auth/register", mw.Headers(authHandler))
	mux.Handle("/auth/login", mw.Headers(authHandler))
	mux.Handle("/auth/refresh", mw.Headers(mw.Token(enums.RefreshToken, authHandler)))
	mux.Handle("/auth/logout", mw.Headers(mw.Token(enums.AccessToken, authHandler)))

	// user group
	mux.Handle("/user/delete", mw.Headers(mw.Token(enums.AccessToken, userHandler)))
	mux.Handle("/user/info", mw.Headers(mw.Token(enums.AccessToken, userHandler)))

	err = http.ListenAndServe(":"+os.Getenv(env.Port), mux)
	if err != nil {
		log.Fatalln(err)
	}
}
