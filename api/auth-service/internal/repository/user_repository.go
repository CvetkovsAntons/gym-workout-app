package repository

import (
	"context"
	"fmt"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/database"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/models"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"time"
)

type UserRepository interface {
	Create(ctx context.Context, user *models.User) error
	GetById(ctx context.Context, id string) (*models.User, error)
	GetByEmail(ctx context.Context, email string) (*models.User, error)
	Update(ctx context.Context, user *models.User, updateFields bson.M) error
	UpdateById(ctx context.Context, id string, updateFields bson.M) (*models.User, error)
	Delete(ctx context.Context, id string) error
}

type userRepository struct {
	collection *mongo.Collection
}

func NewUserRepository() UserRepository {
	return &userRepository{
		collection: database.GetUserCollection(),
	}
}

func (repo userRepository) Create(ctx context.Context, user *models.User) error {
	var cancel context.CancelFunc

	if ctx == nil {
		ctx, cancel = context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
	}

	if _, err := repo.collection.InsertOne(ctx, user); err != nil {
		return fmt.Errorf("failed to create user: %w", err)
	}

	return nil
}

func (repo userRepository) GetById(ctx context.Context, id string) (*models.User, error) {
	var cancel context.CancelFunc

	if ctx == nil {
		ctx, cancel = context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
	}

	var user models.User
	filter := bson.M{"_id": id}

	if err := repo.collection.FindOne(ctx, filter).Decode(&user); err != nil {
		return nil, fmt.Errorf("failed to get user by ID: %w", err)
	}

	return &user, nil
}

func (repo userRepository) GetByEmail(ctx context.Context, email string) (*models.User, error) {
	var cancel context.CancelFunc

	if ctx == nil {
		ctx, cancel = context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
	}

	var user models.User
	filter := bson.M{"email": email}

	if err := repo.collection.FindOne(ctx, filter).Decode(&user); err != nil {
		return nil, fmt.Errorf("failed to get user by email: %w", err)
	}

	return &user, nil
}

func (repo userRepository) Update(ctx context.Context, user *models.User, updatedFields bson.M) error {
	user, err := repo.UpdateById(ctx, user.ID, updatedFields)
	if err != nil {
		return fmt.Errorf("failed to update user: %w", err)
	}
	return nil
}

func (repo userRepository) UpdateById(ctx context.Context, id string, updatedFields bson.M) (*models.User, error) {
	var cancel context.CancelFunc

	if ctx == nil {
		ctx, cancel = context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
	}

	filter := bson.M{"_id": id}
	update := bson.M{"$set": updatedFields}

	var user models.User
	err := repo.collection.FindOneAndUpdate(ctx, filter, update).Decode(&user)
	if err != nil {
		return nil, fmt.Errorf("failed to update user by ID: %w", err)
	}

	return &user, nil
}

func (repo userRepository) Delete(ctx context.Context, id string) error {
	var cancel context.CancelFunc

	if ctx == nil {
		ctx, cancel = context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
	}

	filter := bson.M{"_id": id}
	_, err := repo.collection.DeleteOne(ctx, filter)
	if err != nil {
		return fmt.Errorf("failed to delete user: %w", err)
	}

	return nil
}
