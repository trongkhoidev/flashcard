package com.example.data.local

import com.example.data.model.DeckEntity

/**
 * Tiến trình học theo LEVEL trước cho mỗi ngôn ngữ:
 * ```
 * Cơ bản (daily → travel) → Trung cấp (daily → travel) → Nâng cao (daily → travel)
 * ```
 * - 1 deck HOÀN THÀNH khi toàn bộ thẻ ĐÃ THUỘC (isMastered = 1, chỉ đạt qua Quiz đúng)
 * - Deck tự tạo / không đúng định dạng seed (`deck_{lang}_{topic}_{level}`) không tham gia path
 */
object LearningPath {

    // So khớp theo PREFIX để nhận diện các biến thể level trong data đa ngôn ngữ:
    // "Cơ bản A1-A2", "Cơ bản HSK 1-2", "Trung cấp B1-B2", "Nâng cao C1-C2"...
    private val LEVEL_PREFIXES = listOf(
        "mới bắt đầu" to 0,
        "cơ bản" to 1,
        "sơ cấp" to 1,
        "trung cấp" to 2,
        "nâng cao" to 3
    )

    /** ID chuẩn dạng `deck_{lang}_{topic}_{level}` -> trả về topic, ngược lại null */
    fun topicOf(deck: DeckEntity): String? {
        if (!deck.id.startsWith("deck_")) return null
        val parts = deck.id.split("_")
        if (parts.size < 4) return null
        return parts[2]
    }

    fun levelOrder(deck: DeckEntity): Int {
        val normalized = deck.level.trim().lowercase()
        return LEVEL_PREFIXES.firstOrNull { (prefix, order) -> normalized.startsWith(prefix) }?.second ?: -1
    }

    fun isPathDeck(deck: DeckEntity): Boolean =
        topicOf(deck) != null && levelOrder(deck) >= 0

    /** Đường đi học tập của 1 ngôn ngữ, sắp theo (level, chủ đề, id) */
    fun buildPath(decks: List<DeckEntity>, langCode: String): List<DeckEntity> =
        decks.asSequence()
            .filter { it.languageCode == langCode && isPathDeck(it) }
            .sortedWith(compareBy({ levelOrder(it) }, { topicOf(it)!! }, { it.id }))
            .toList()

    /** Deck hoàn thành khi có thẻ và số thẻ đã thuộc >= tổng thẻ */
    fun isCompleted(masteredCount: Int?, totalCards: Int?): Boolean =
        (totalCards ?: 0) > 0 && (masteredCount ?: 0) >= (totalCards ?: 0)

    /**
     * Deck kế tiếp trên path SAU deck hiện tại, bỏ qua các deck đã hoàn thành.
     * Trả về null nếu đã chinh phục hết path.
     */
    fun nextDeckAfter(
        current: DeckEntity,
        path: List<DeckEntity>,
        masteredCountByDeckId: Map<String, Int>,
        totalCardsByDeckId: Map<String, Int>
    ): DeckEntity? {
        val currentIndex = path.indexOfFirst { it.id == current.id }
        val remaining = if (currentIndex == -1) path else path.drop(currentIndex + 1)
        return remaining.firstOrNull { candidate ->
            !isCompleted(
                masteredCountByDeckId[candidate.id],
                totalCardsByDeckId[candidate.id]
            )
        }
    }
}
