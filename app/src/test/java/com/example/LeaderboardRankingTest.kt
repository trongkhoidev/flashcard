package com.example

import com.example.ui.leaderboard.LeaderboardFilterType
import com.example.ui.leaderboard.LeaderboardUser
import com.example.ui.leaderboard.RankTrend
import com.example.ui.leaderboard.computeRankedList
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LeaderboardRankingTest {

    private val mocks = listOf(
        user("A", points = 5000, streak = 20, cards = 300),
        user("B", points = 4000, streak = 15, cards = 250),
        user("C", points = 3000, streak = 10, cards = 200),
        user("D", points = 2000, streak = 5, cards = 100)
    )

    private fun user(
        name: String,
        points: Int,
        streak: Int,
        cards: Int
    ) = LeaderboardUser(
        rank = 0, name = name, points = points, streakDays = streak,
        cardsLearned = cards, trend = RankTrend.Same,
        avatarBgColor = Color.White, avatarEmoji = "🧑"
    )

    private fun me(points: Int, streak: Int, cards: Int) =
        user("Tôi", points, streak, cards).copy(isCurrentUser = true)

    @Test
    fun `user ranked by real points among mock competitors`() {
        val ranked = computeRankedList(mocks, me(points = 4500, streak = 3, cards = 10), LeaderboardFilterType.POINTS)

        assertEquals(listOf("A", "Tôi", "B", "C", "D"), ranked.map { it.name })
        assertEquals(2, ranked.first { it.isCurrentUser }.rank)
    }

    @Test
    fun `user can reach top 1 podium`() {
        val ranked = computeRankedList(mocks, me(points = 9999, streak = 1, cards = 1), LeaderboardFilterType.POINTS)

        assertEquals("Tôi", ranked[0].name)
        assertEquals(1, ranked[0].rank)
        assertTrue(ranked[0].isCurrentUser)
    }

    @Test
    fun `filter changes ranking metric`() {
        // Điểm thấp nhất nhưng streak cao nhất -> hạng 1 theo filter STREAK
        val rankedByStreak = computeRankedList(mocks, me(points = 100, streak = 99, cards = 0), LeaderboardFilterType.STREAK)
        assertEquals(1, rankedByStreak.first { it.isCurrentUser }.rank)

        // Theo CARDS thì tụt xuống cuối
        val rankedByCards = computeRankedList(mocks, me(points = 100, streak = 99, cards = 1), LeaderboardFilterType.CARDS)
        assertEquals(rankedByCards.size, rankedByCards.first { it.isCurrentUser }.rank)
    }

    @Test
    fun `tie on metric favors current user`() {
        val ranked = computeRankedList(mocks, me(points = 5000, streak = 1, cards = 1), LeaderboardFilterType.POINTS)
        assertEquals("Tôi", ranked[0].name)
        assertEquals("A", ranked[1].name)
    }

    @Test
    fun `ranks are sequential from 1 and current user marked exactly once`() {
        val ranked = computeRankedList(mocks, me(points = 1, streak = 1, cards = 1), LeaderboardFilterType.POINTS)
        assertEquals((1..ranked.size).toList(), ranked.map { it.rank })
        assertEquals(1, ranked.count { it.isCurrentUser })
    }
}
