package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AccessKeyEntity
import com.example.data.model.EbookEntity
import com.example.data.model.ScoreboardEntity
import com.example.data.model.StrategyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StrategyDao {
    @Query("SELECT * FROM strategies ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<StrategyEntity>>

    @Query("SELECT * FROM strategies WHERE isActive = 1")
    suspend fun getActiveStrategies(): List<StrategyEntity>

    @Query("SELECT COUNT(*) FROM strategies")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(strategy: StrategyEntity)

    @Update
    suspend fun update(strategy: StrategyEntity)

    @Delete
    suspend fun delete(strategy: StrategyEntity)

    @Query("DELETE FROM strategies WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface AccessKeyDao {
    @Query("SELECT * FROM access_keys ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<AccessKeyEntity>>

    @Query("SELECT * FROM access_keys WHERE `key` = :key LIMIT 1")
    suspend fun getByKey(key: String): AccessKeyEntity?

    @Query("SELECT COUNT(*) FROM access_keys")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: AccessKeyEntity)

    @Delete
    suspend fun delete(key: AccessKeyEntity)

    @Query("DELETE FROM access_keys WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ScoreboardDao {
    @Query("SELECT * FROM scoreboard WHERE id = 1 LIMIT 1")
    fun getScoreboardFlow(): Flow<ScoreboardEntity?>

    @Query("SELECT * FROM scoreboard WHERE id = 1 LIMIT 1")
    suspend fun getScoreboard(): ScoreboardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(scoreboard: ScoreboardEntity)

    @Query("UPDATE scoreboard SET greens = :greens, losses = :losses, empates = :empates, currentStreak = :currentStreak, maxConsecutiveGreens = :maxStreak, lastUpdated = :now WHERE id = 1")
    suspend fun updateScores(greens: Int, losses: Int, empates: Int, currentStreak: Int, maxStreak: Int, now: Long = System.currentTimeMillis())

    @Query("UPDATE scoreboard SET greens = 0, losses = 0, empates = 0, currentStreak = 0, maxConsecutiveGreens = 0, lastUpdated = :now WHERE id = 1")
    suspend fun resetScore(now: Long = System.currentTimeMillis())
}

@Dao
interface EbookDao {
    @Query("SELECT * FROM ebooks ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<EbookEntity>>

    @Query("SELECT COUNT(*) FROM ebooks")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ebook: EbookEntity)

    @Delete
    suspend fun delete(ebook: EbookEntity)

    @Query("DELETE FROM ebooks WHERE id = :id")
    suspend fun deleteById(id: String)
}
