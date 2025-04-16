package com.example.gymworkoutapp.data.sync

import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.models.DateOfBirth
import com.example.gymworkoutapp.models.UserData
import com.example.gymworkoutapp.network.services.UserService

class UserDataSyncManager(
    private val repository: UserRepository,
    private val service: UserService
) {
    suspend fun merge() {
        var local = repository.getUserData()

        val localExists = local != null

        if (!localExists) {
            local = UserData(name = null, height = null, weight = null, dateOfBirth = null)
        }

        val online = service.getInfo().body()

        val localDob = local?.dateOfBirth
        val onlineDob = online?.dateOfBirth

        val dateOfBirth = DateOfBirth(
            day = mergeField(localDob?.day, onlineDob?.day),
            month = mergeField(localDob?.month, onlineDob?.month),
            year = mergeField(localDob?.year, onlineDob?.year),
        )

        val mergedUserData = UserData(
            name = mergeField(local?.name, online?.name),
            height = mergeField(local?.height, online?.height),
            weight = mergeField(local?.weight, online?.weight),
            dateOfBirth = dateOfBirth
        )

        if (localExists) {
            repository.updateUserData(mergedUserData)
        } else {
            repository.insertUserData(mergedUserData)
        }

        service.putInfo(mergedUserData)
    }

    private fun selectValue(local: Any?, online: Any?): Any? {
        return if (local == null && online != null) online else local
    }

    private fun mergeField(local: String?, online: String?): String? {
        return selectValue(local, online)?.toString()
    }

    private fun mergeField(local: Int?, online: Int?): Int? {
        return selectValue(local, online)?.toString()?.toIntOrNull()
    }

    private fun mergeField(local: Float?, online: Float?): Float? {
        return selectValue(local, online)?.toString()?.toFloatOrNull()
    }

}