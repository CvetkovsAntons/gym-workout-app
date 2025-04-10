package com.example.gymworkoutapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gymworkoutapp.data.database.entities.HistoryWeight
import com.example.gymworkoutapp.data.database.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun get(): User?

    @Update
    suspend fun update(user: User)

    @Insert
    suspend fun insertHistoryWeight(historyWeight: HistoryWeight)

    @Query("SELECT * FROM history_weight ORDER BY datetime DESC")
    suspend fun getAllHistoryWeights(): List<HistoryWeight>

}