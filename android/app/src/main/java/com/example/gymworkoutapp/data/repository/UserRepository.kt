package com.example.gymworkoutapp.data.repository

import com.example.gymworkoutapp.data.database.dao.UserDao
import com.example.gymworkoutapp.data.mappers.toEntity
import com.example.gymworkoutapp.data.mappers.toUserData
import com.example.gymworkoutapp.models.UserData

class UserRepository(private val userDao: UserDao) {
    suspend fun getUser(): UserData? {
        return userDao.get()?.toUserData()
    }

    suspend fun insertUser(userData: UserData) {
        userDao.insert(userData.toEntity())
    }

    suspend fun updateUser(userData: UserData) {
        userDao.update(userData.toEntity())
    }

}