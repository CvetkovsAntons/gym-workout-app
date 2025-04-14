package services

import (
	"context"
	"errors"
	"fmt"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/cache"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/models"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/repository"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"reflect"
	"time"
)

type UserService interface {
	CreateUser(ctx context.Context, user *models.User) error
	GetUserById(ctx context.Context, id string) (*models.User, error)
	GetUserByEmail(ctx context.Context, email string) (*models.User, error)
	GetUserData(user *models.User) models.UserData
	UpdateUser(ctx context.Context, user *models.User, updateFields bson.M) error
	UpdateUserById(ctx context.Context, id string, updateFields bson.M) (*models.User, error)
	UpdateUserData(ctx context.Context, user *models.User, userData models.UserData) error
	DeleteUser(ctx context.Context, id string) error
	UserWithIdExists(ctx context.Context, id string) (bool, error)
	UserWithEmailExists(ctx context.Context, email string) (bool, error)
}

type userService struct {
	redis *cache.Redis
	repo  repository.UserRepository
}

func NewUserService(redis *cache.Redis) UserService {
	return &userService{
		redis: redis,
		repo:  repository.NewUserRepository(),
	}
}

func (s userService) CreateUser(ctx context.Context, user *models.User) error {
	return s.repo.Create(ctx, user)
}

func (s userService) GetUserById(ctx context.Context, id string) (*models.User, error) {
	return s.repo.GetById(ctx, id)
}

func (s userService) GetUserByEmail(ctx context.Context, email string) (*models.User, error) {
	return s.repo.GetByEmail(ctx, email)
}

func (s userService) GetUserData(user *models.User) models.UserData {
	if user == nil {
		return models.UserData{}
	}

	userData := models.UserData{
		Name:   user.Data.Name,
		Weight: user.Data.Weight,
		Height: user.Data.Height,
	}

	if user.Data.DateOfBirth != nil {
		userData.DateOfBirth = user.Data.DateOfBirth
	}

	return userData
}

func (s userService) UpdateUser(ctx context.Context, user *models.User, updateFields bson.M) error {
	return s.repo.Update(ctx, user, updateFields)
}

func (s userService) UpdateUserById(ctx context.Context, id string, updateFields bson.M) (*models.User, error) {
	return s.repo.UpdateById(ctx, id, updateFields)
}

func (s userService) UpdateUserData(ctx context.Context, user *models.User, userData models.UserData) error {
	updateFields := bson.M{}

	fields := map[string]interface{}{
		"data.name":   userData.Name,
		"data.weight": userData.Weight,
		"data.height": userData.Height,
	}
	if userData.DateOfBirth != nil {
		fields["data.date_of_birth.day"] = userData.DateOfBirth.Day
		fields["data.date_of_birth.month"] = userData.DateOfBirth.Month
		fields["data.date_of_birth.year"] = userData.DateOfBirth.Year
	}
	for key, value := range fields {
		reflection := reflect.ValueOf(value)
		if value != nil && !(reflection.Kind() == reflect.Ptr && reflection.IsNil()) {
			updateFields[key] = value
		}
	}

	if len(updateFields) == 0 {
		return fmt.Errorf("failed to update user data: missing fields")
	}

	updateFields["updated_at"] = time.Now()

	err := s.UpdateUser(ctx, user, updateFields)
	if err != nil {
		return fmt.Errorf("failed to update user data: %w", err)
	}

	return nil
}

func (s userService) DeleteUser(ctx context.Context, id string) error {
	return s.repo.Delete(ctx, id)
}

func (s userService) UserWithIdExists(ctx context.Context, id string) (bool, error) {
	user, err := s.GetUserById(ctx, id)
	if err != nil && !errors.Is(err, mongo.ErrNoDocuments) {
		return false, fmt.Errorf("failed to check if user with ID %s exists: %w", id, err)
	}

	return user != nil, nil
}

func (s userService) UserWithEmailExists(ctx context.Context, email string) (bool, error) {
	user, err := s.GetUserByEmail(ctx, email)
	if err != nil && !errors.Is(err, mongo.ErrNoDocuments) {
		return false, fmt.Errorf("failed to check if user with email %s exists: %w", email, err)
	}

	return user != nil, nil
}
