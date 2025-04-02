package database

import (
	"context"
	"fmt"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/env"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"os"
	"time"
)

var userCollection *mongo.Collection

func InitMongoDBClient() error {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	client, err := mongo.Connect(ctx, options.Client().ApplyURI(os.Getenv(env.DatabaseUri)))
	if err != nil {
		return fmt.Errorf("could not connect to MongoDB: %v", err)
	}

	err = client.Ping(ctx, nil)
	if err != nil {
		return fmt.Errorf("mongoDB ping failed: %v", err)
	}

	dbName := os.Getenv(env.DatabaseName)
	db := client.Database(dbName)
	if db == nil {
		return fmt.Errorf("database %s not found in MongoDB", dbName)
	}

	collection := db.Collection("users")
	if collection == nil {
		return fmt.Errorf("collection users not found in MongoDB")
	}

	userCollection = collection

	return nil
}

func GetUserCollection() *mongo.Collection {
	return userCollection
}
