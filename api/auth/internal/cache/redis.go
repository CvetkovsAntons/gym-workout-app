package cache

import (
	"context"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/env"
	"github.com/redis/go-redis/v9"
	"os"
)

type Redis struct {
	Client *redis.Client
}

func NewRedis() (*Redis, error) {
	client := redis.NewClient(&redis.Options{
		Addr:     os.Getenv(env.RedisAddress),
		Password: os.Getenv(env.RedisPassword),
		DB:       0,
	})

	if err := client.Ping(context.Background()).Err(); err != nil {
		return nil, err
	}

	return &Redis{Client: client}, nil
}
