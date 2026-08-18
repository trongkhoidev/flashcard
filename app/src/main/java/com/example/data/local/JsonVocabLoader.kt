package com.example.data.local

import android.content.Context
import android.util.Log
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object JsonVocabLoader {

    fun loadVocabFromJson(context: Context, fileName: String = "vocab_data.json"): Pair<List<DeckEntity>, List<FlashCardEntity>>? {
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            val decksJson = jsonObject.getJSONArray("decks")
            val cardsJson = jsonObject.getJSONArray("flashCards")

            val decks = mutableListOf<DeckEntity>()
            for (i in 0 until decksJson.length()) {
                val deckObj = decksJson.getJSONObject(i)
                decks.add(
                    DeckEntity(
                        id = deckObj.getString("id"),
                        languageCode = deckObj.getString("languageCode"),
                        title = deckObj.getString("title"),
                        subtitle = deckObj.getString("subtitle"),
                        iconEmoji = deckObj.getString("iconEmoji"),
                        level = deckObj.getString("level"),
                        colorHex = deckObj.getString("colorHex"),
                        cardCount = deckObj.optInt("cardCount", 0),
                        isCustom = deckObj.optBoolean("isCustom", false)
                    )
                )
            }

            val cards = mutableListOf<FlashCardEntity>()
            for (i in 0 until cardsJson.length()) {
                val cardObj = cardsJson.getJSONObject(i)
                cards.add(
                    FlashCardEntity(
                        deckId = cardObj.getString("deckId"),
                        languageCode = cardObj.getString("languageCode"),
                        frontWord = cardObj.getString("frontWord"),
                        phonetic = cardObj.getString("phonetic"),
                        partOfSpeech = cardObj.getString("partOfSpeech"),
                        frontExample = cardObj.getString("frontExample"),
                        backMeaning = cardObj.getString("backMeaning"),
                        backExampleTranslation = cardObj.getString("backExampleTranslation"),
                        memoryTip = cardObj.optString("memoryTip", ""),
                        difficulty = cardObj.optInt("difficulty", 0),
                        isStarred = cardObj.optBoolean("isStarred", false),
                        isMastered = cardObj.optBoolean("isMastered", false),
                        reviewCount = cardObj.optInt("reviewCount", 0),
                        lastReviewedTimestamp = cardObj.optLong("lastReviewedTimestamp", 0L)
                    )
                )
            }
            return Pair(decks, cards)
        } catch (e: Exception) {
            Log.e("JsonVocabLoader", "Error loading vocab from JSON", e)
            return null
        }
    }
}
