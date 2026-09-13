package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "strategies")
data class StrategyEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val sequence: String, // e.g. "B, B" or "P, P, P" or "B, P, B"
    val recommendation: String, // "BANKER" or "PLAYER"
    val confidence: Int = 95,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "access_keys")
data class AccessKeyEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val key: String,
    val durationDays: Int, // -1 for lifetime
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = if (durationDays < 0) Long.MAX_VALUE else System.currentTimeMillis() + (durationDays.toLong() * 24 * 60 * 60 * 1000L),
    val isActive: Boolean = true,
    val note: String = "Liberado para múltiplos aparelhos"
) {
    fun isExpired(): Boolean {
        return !isActive || (expiresAt != Long.MAX_VALUE && System.currentTimeMillis() > expiresAt)
    }
}

@Entity(tableName = "scoreboard")
data class ScoreboardEntity(
    @PrimaryKey
    val id: Int = 1,
    val greens: Int = 142,
    val losses: Int = 4,
    val empates: Int = 18,
    val currentStreak: Int = 14,
    val maxConsecutiveGreens: Int = 26,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val assertiveness: Double
        get() {
            val total = greens + losses
            return if (total > 0) (greens.toDouble() / total) * 100.0 else 100.0
        }
}

@Entity(tableName = "ebooks")
data class EbookEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val readTime: String,
    val summary: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class SignalTarget(val title: String) {
    BANKER("BANKER"),
    PLAYER("PLAYER")
}

enum class GalePhase {
    NO_SIGNAL,
    ANALYZING,
    ENTRY_CONFIRMED, // Sem Gale (SG)
    GALE_1,          // Gale 1 (G1)
    RESULT_GREEN,    // Green (SG or G1 or Empate)
    RESULT_LOSS      // Loss
}

data class ActiveSignal(
    val id: String = UUID.randomUUID().toString(),
    val target: SignalTarget,
    val phase: GalePhase,
    val strategyName: String,
    val confidence: Int,
    val triggeredAtTimestamp: Long = System.currentTimeMillis(),
    val triggeredAfterEventId: String,
    val sgEventId: String? = null,
    val g1EventId: String? = null,
    val resultMessage: String? = null,
    val isWin: Boolean? = null,
    val isTieWin: Boolean = false
)

data class BetHouse(
    val name: String,
    val url: String,
    val drawableRes: Int,
    val tagline: String,
    val bonusInfo: String
)
