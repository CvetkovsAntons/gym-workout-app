package database

import (
	"context"
	"github.com/CvetkovsAntons/gwa-api-auth-service/internal/env"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"log"
	"os"
	"time"
)

var Client *mongo.Client

func InitMongoDBClient() {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	client, err := mongo.Connect(ctx, options.Client().ApplyURI(os.Getenv(env.DatabaseUri)))
	if err != nil {
		log.Fatalln("Could not connect to MongoDB: " + err.Error())
	}

	err = client.Ping(ctx, nil)
	if err != nil {
		log.Fatal("MongoDB ping failed:", err)
	}

	Client = client
}

func GetDatabase() *mongo.Database {
	return Client.Database(os.Getenv(env.DatabaseName))
}

func GetUserCollection() *mongo.Collection {
	db := GetDatabase()
	if db == nil {
		log.Fatalln("Could not connect to database")
		return nil
	}
	return db.Collection("users")
}
