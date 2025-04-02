package models

import (
	"time"
)

type DateOfBirth struct {
	Year  *int `bson:"year,omitempty" json:"year,omitempty"`
	Month *int `bson:"month,omitempty" json:"month,omitempty"`
	Day   *int `bson:"day,omitempty" json:"day,omitempty"`
}

type UserData struct {
	Weight      *float64     `bson:"weight,omitempty" json:"weight,omitempty"`
	Height      *float64     `bson:"height,omitempty" json:"height,omitempty"`
	Name        *string      `bson:"name,omitempty" json:"name,omitempty"`
	Surname     *string      `bson:"surname,omitempty" json:"surname,omitempty"`
	DateOfBirth *DateOfBirth `bson:"date_of_birth,omitempty" json:"date_of_birth,omitempty"`
}

type User struct {
	ID        string     `bson:"_id" json:"id,omitempty"`
	Email     string     `bson:"email" json:"email"`
	Password  string     `bson:"password" json:"password"`
	CreatedAt time.Time  `bson:"created_at" json:"created_at"`
	UpdatedAt *time.Time `bson:"updated_at,omitempty" json:"updated_at,omitempty"`
	Data      UserData   `bson:"data" json:"data"`
}
