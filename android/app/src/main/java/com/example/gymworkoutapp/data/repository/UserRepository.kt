package com.example.gymworkoutapp.data.repository

import com.example.gymworkoutapp.data.database.dao.UserDao
import com.example.gymworkoutapp.data.database.entities.HistoryWeight
import com.example.gymworkoutapp.data.mappers.toEntity
import com.example.gymworkoutapp.data.mappers.toUserData
import com.example.gymworkoutapp.models.UserData

class UserRepository(private val userDao: UserDao) {
    suspend fun getUserData(): UserData? {
        return userDao.get()?.toUserData()
    }

    suspend fun insertUserData(userData: UserData) {
        userDao.insert(userData.toEntity())
    }

    suspend fun updateUserData(userData: UserData) {
        userDao.update(userData.toEntity())
    }

    suspend fun insertHistoryWeight(weight: Float) {
        userDao.insertHistoryWeight(HistoryWeight(
            weight = weight
        ))
    }

    suspend fun getWeightHistory(): List<HistoryWeight> {
        return userDao.getAllHistoryWeights()
    }

}