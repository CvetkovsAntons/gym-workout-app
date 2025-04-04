package com.example.gymworkoutapp.data.mappers

import com.example.gymworkoutapp.data.database.entities.User
import com.example.gymworkoutapp.models.DateOfBirth
import com.example.gymworkoutapp.models.UserData

fun User.toUserData() = UserData(
    weight = weight,
    height = height,
    name = name,
    dateOfBirth = DateOfBirth(birthDay, birthMonth, birthYear)
)

fun UserData.toEntity() = User(
    id = 0,
    weight = weight,
    height = height,
    name = name,
    birthDay = dateOfBirth?.day,
    birthMonth = dateOfBirth?.month,
    birthYear = dateOfBirth?.year,
)

fun UserData.isValid(): Boolean {
    if (weight != null && height != null) {
        return name?.isNotBlank() == true &&
                weight > 0 &&
                height > 0 &&
                dateOfBirth?.day in 1..31 &&
                dateOfBirth?.month in 1..12 &&
                dateOfBirth?.year in 1900..2100
    }

    return true
}