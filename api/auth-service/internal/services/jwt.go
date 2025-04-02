package services

import (
	"context"
	"errors"
	"fmt"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/cache"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/enums"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/env"
	"github.com/golang-jwt/jwt/v4"
	"github.com/redis/go-redis/v9"
	"os"
	"strconv"
	"time"
)

type JWTService interface {
	GenerateTokens(ctx context.Context, userId string) (string, string, error)
	GetTokenClaims(tokenType enums.TokenType, tokenString string) (jwt.MapClaims, error)
	TokenExists(ctx context.Context, tokenType enums.TokenType, token string) (bool, error)
	DeleteTokens(ctx context.Context, userId string) error
}

type jwtService struct {
	redis                      *cache.Redis
	accessTokenSecret          []byte
	refreshTokenSecret         []byte
	accessTokenExpirationTime  time.Duration
	refreshTokenExpirationTime time.Duration
}

func NewJwtService(redis *cache.Redis) JWTService {
	accessTokenExpirationTime, err := strconv.Atoi(os.Getenv(env.AccessTokenExpTime))
	if err != nil {
		accessTokenExpirationTime = 60 // minutes
	}

	refreshTokenExpirationTime, err := strconv.Atoi(os.Getenv(env.RefreshTokeExpTime))
	if err != nil {
		refreshTokenExpirationTime = 60 * 24 * 30 // minutes * hours * days == 43200 minutes
	}

	return &jwtService{
		redis:                      redis,
		accessTokenSecret:          []byte(os.Getenv(env.AccessTokenSecret)),
		refreshTokenSecret:         []byte(os.Getenv(env.RefreshTokenSecret)),
		accessTokenExpirationTime:  time.Duration(accessTokenExpirationTime) * time.Minute,
		refreshTokenExpirationTime: time.Duration(refreshTokenExpirationTime) * time.Minute,
	}
}

func (s *jwtService) GenerateTokens(ctx context.Context, userId string) (string, string, error) {
	accessToken, err := s.generateToken(ctx, userId, enums.AccessToken)
	if err != nil {
		return "", "", fmt.Errorf("failed to generate access token: %w", err)
	}
	refreshToken, err := s.generateToken(ctx, userId, enums.RefreshToken)
	if err != nil {
		return "", "", fmt.Errorf("failed to generate refresh token: %w", err)
	}
	err = s.DeleteTokens(ctx, userId)
	if err != nil {
		return "", "", fmt.Errorf("failed to delete old tokens: %w", err)
	}
	err = s.redis.Client.SAdd(ctx, s.redisUserTokensKey(userId), accessToken, refreshToken).Err()
	if err != nil {
		return "", "", fmt.Errorf("failed to store user tokens in Redis: %w", err)
	}
	return accessToken, refreshToken, nil
}

func (s *jwtService) generateToken(ctx context.Context, userId string, tokenType enums.TokenType) (string, error) {
	expirationTime := s.getExpirationTime(tokenType)

	claims := jwt.MapClaims{
		"user_id": userId,
		"exp":     time.Now().Add(expirationTime).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)

	tokenSigned, err := token.SignedString(s.getSecretKey(tokenType))
	if err != nil {
		return "", fmt.Errorf("failed to sign %s token: %w", tokenType, err)
	}

	err = s.redis.Client.Set(ctx, s.redisTokenKey(tokenType, tokenSigned), userId, expirationTime).Err()
	if err != nil {
		return "", fmt.Errorf("failed to store %s token in Redis: %w", tokenType, err)
	}

	return tokenSigned, nil
}

func (s *jwtService) GetTokenClaims(tokenType enums.TokenType, tokenString string) (jwt.MapClaims, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return s.getSecretKey(tokenType), nil
	})

	if err != nil || !token.Valid {
		return nil, fmt.Errorf("failed to get JWT claims: %w", err)
	}

	if claims, ok := token.Claims.(jwt.MapClaims); ok {
		return claims, nil
	}

	return nil, fmt.Errorf("failed to get JWT claims: invalid claims")
}

func (s *jwtService) TokenExists(ctx context.Context, tokenType enums.TokenType, token string) (bool, error) {
	_, err := s.redis.Client.Get(ctx, s.redisTokenKey(tokenType, token)).Result()
	if err == nil {
		return true, nil
	}
	if errors.Is(err, redis.Nil) {
		return false, nil
	}
	return false, fmt.Errorf("failed to validate exitence of %s token in Redis: %w", tokenType, err)
}

func (s *jwtService) DeleteTokens(ctx context.Context, userId string) error {
	userTokensKey := s.redisUserTokensKey(userId)

	tokens, err := s.redis.Client.SMembers(ctx, userTokensKey).Result()
	if err != nil {
		return fmt.Errorf("failed to retrieve user tokens from Redis: %w", err)
	}

	for _, token := range tokens {
		s.redis.Client.Del(ctx, s.redisTokenKey(enums.AccessToken, token))
		s.redis.Client.Del(ctx, s.redisTokenKey(enums.RefreshToken, token))
	}

	s.redis.Client.Del(ctx, userTokensKey)

	return nil
}

func (s *jwtService) redisTokenKey(tokenType enums.TokenType, token string) string {
	return fmt.Sprintf("%s:%s", tokenType, token)
}

func (s *jwtService) redisUserTokensKey(userId string) string {
	return fmt.Sprintf("user_tokens:%s", userId)
}

func (s *jwtService) getSecretKey(tokenType enums.TokenType) []byte {
	if tokenType == enums.AccessToken {
		return s.accessTokenSecret
	}

	return s.refreshTokenSecret
}

func (s *jwtService) getExpirationTime(tokenType enums.TokenType) time.Duration {
	if tokenType == enums.AccessToken {
		return s.accessTokenExpirationTime
	}

	return s.refreshTokenExpirationTime
}
