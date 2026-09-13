package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BacBoEventItem(
    @Json(name = "id") val id: String,
    @Json(name = "data") val data: BacBoEventData? = null
)

@JsonClass(generateAdapter = true)
data class BacBoEventData(
    @Json(name = "id") val id: String? = null,
    @Json(name = "startedAt") val startedAt: String? = null,
    @Json(name = "settledAt") val settledAt: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "result") val result: BacBoResult? = null
)

@JsonClass(generateAdapter = true)
data class BacBoResult(
    @Json(name = "outcome") val outcome: String? = null,
    @Json(name = "multiplier") val multiplier: Double? = null,
    @Json(name = "playerDice") val playerDice: BacBoDice? = null,
    @Json(name = "bankerDice") val bankerDice: BacBoDice? = null
)

@JsonClass(generateAdapter = true)
data class BacBoDice(
    @Json(name = "first") val first: Int? = null,
    @Json(name = "second") val second: Int? = null,
    @Json(name = "score") val score: Int? = null
)

enum class GameOutcome(val code: String, val displayName: String) {
    BANKER("B", "BANKER"),
    PLAYER("P", "PLAYER"),
    TIE("T", "EMPATE"),
    UNKNOWN("?", "DESCONHECIDO");

    companion object {
        fun fromApiOutcome(outcome: String?): GameOutcome {
            return when {
                outcome.equals("BankerWon", ignoreCase = true) -> BANKER
                outcome.equals("PlayerWon", ignoreCase = true) -> PLAYER
                outcome.equals("Tie", ignoreCase = true) || outcome.equals("TieWon", ignoreCase = true) -> TIE
                else -> UNKNOWN
            }
        }
    }
}

data class ResolvedGame(
    val eventId: String,
    val settledAt: String,
    val outcome: GameOutcome,
    val playerScore: Int,
    val bankerScore: Int,
    val playerDice1: Int,
    val playerDice2: Int,
    val bankerDice1: Int,
    val bankerDice2: Int
)
