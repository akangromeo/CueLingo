package com.example.cuelingo.data.local.database.dummyData

data class LeaderboardItem(
    val rank: String,
    val dictionaryName: String
)

fun generateDummyData(): List<LeaderboardItem> {
    val dummyData = listOf(
        LeaderboardItem(rank = "1", dictionaryName = "Okey"),
        LeaderboardItem(rank = "2", dictionaryName = "Apa"),
        LeaderboardItem(rank = "3", dictionaryName = "Aku")
    )

    return dummyData
}
