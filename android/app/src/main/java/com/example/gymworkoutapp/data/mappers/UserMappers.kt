package com.example.gymworkoutapp.data.mappers

import com.example.gymworkoutapp.data.database.entities.User
import com.example.gymworkoutapp.models.DateOfBirth
import com.example.gymworkoutapp.models.UserData

fun User.toUserData() = UserData(
    weight = weight,
    height = height,
    name = name,
    surname = surname,
    dateOfBirth = DateOfBirth(birthDay, birthMonth, birthYear)
)

fun UserData.toEntity() = User(
    id = 0,
    weight = weight,
    height = height,
    name = name,
    surname = surname,
    birthDay = dateOfBirth.day,
    birthMonth = dateOfBirth.month,
    birthYear = dateOfBirth.year,
)