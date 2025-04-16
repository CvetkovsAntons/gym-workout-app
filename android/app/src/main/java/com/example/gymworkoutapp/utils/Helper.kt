package com.example.gymworkoutapp.utils

import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.models.DateOfBirth
import com.example.gymworkoutapp.models.UserData
import com.example.gymworkoutapp.network.client.ApiClient

object Helper {
    private lateinit var repository: UserRepository

    fun init(userRepository: UserRepository) {
        repository = userRepository
    }

    suspend fun setMergedData() {
        var local = repository.getUserData()
        val localExists = local != null
        if (!localExists) {
            local = UserData(name = null, height = null, weight = null, dateOfBirth = null)
        }

        val online = ApiClient.userService.getInfo().body()

        val localDob = local?.dateOfBirth
        val onlineDob = online?.dateOfBirth

        var dateOfBirth = DateOfBirth(
            day = mergeUserDataField(localDob?.day, onlineDob?.day).toString().toIntOrNull(),
            month = mergeUserDataField(localDob?.month, onlineDob?.month).toString().toIntOrNull(),
            year = mergeUserDataField(localDob?.year, onlineDob?.year).toString().toIntOrNull()
        )

        val mergedUserData = UserData(
            name = mergeUserDataField(local?.name, online?.name)?.toString(),
            height = mergeUserDataField(local?.height, online?.height).toString().toFloatOrNull(),
            weight = mergeUserDataField(local?.weight, online?.weight).toString().toFloatOrNull(),
            dateOfBirth = dateOfBirth
        )

        if (localExists) {
            repository.updateUserData(mergedUserData)
        } else {
            repository.insertUserData(mergedUserData)
        }

        ApiClient.userService.putInfo(mergedUserData)
    }

    private fun mergeUserDataField(local: Any?, online: Any ?): Any? {
        return if (local == null && online != null) online else local
    }
}