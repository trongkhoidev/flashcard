package com.example.data.local

import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.data.local.seed.DeVocabData
import com.example.data.local.seed.EnVocabData
import com.example.data.local.seed.EsVocabData
import com.example.data.local.seed.ItVocabData
import com.example.data.local.seed.PtVocabData

object DefaultVocabData {

    fun getDefaultDecks(): List<DeckEntity> {
        return listOf(
            // English Decks
            DeckEntity(
                id = "en_basics",
                languageCode = "en",
                title = "English Basics",
                subtitle = "Các từ vựng và cụm từ tiếng Anh thiết yếu",
                iconEmoji = "🇺🇸",
                level = "A1-A2",
                colorHex = "#5B67EA",
                cardCount = 95
            ),
            DeckEntity(
                id = "en_travel",
                languageCode = "en",
                title = "Du lịch & Khám phá",
                subtitle = "Sân bay, khách sạn, hỏi đường và mua sắm",
                iconEmoji = "✈️",
                level = "Thực chiến",
                colorHex = "#3B82F6",
                cardCount = 4
            ),
            DeckEntity(
                id = "en_business",
                languageCode = "en",
                title = "Công sở & Business TOEIC",
                subtitle = "Đàm phán, họp hành, gửi email công việc",
                iconEmoji = "💼",
                level = "Trung cấp B1-B2",
                colorHex = "#8B5CF6",
                cardCount = 8
            ),

            // Spanish Deck
            DeckEntity(
                id = "es_basics",
                languageCode = "es",
                title = "Tiếng Tây Ban Nha cơ bản",
                subtitle = "Chào hỏi, giao tiếp cơ bản, từ vựng hàng ngày",
                iconEmoji = "🇪🇸",
                level = "DELE A1",
                colorHex = "#F59E0B",
                cardCount = 40
            ),

            // German Deck
            DeckEntity(
                id = "de_basics",
                languageCode = "de",
                title = "Tiếng Đức cơ bản",
                subtitle = "Giao tiếp, từ vựng thiết yếu",
                iconEmoji = "🇩🇪",
                level = "Goethe A1",
                colorHex = "#EF4444",
                cardCount = 40
            ),

            // Italian Deck
            DeckEntity(
                id = "it_basics",
                languageCode = "it",
                title = "Tiếng Ý cơ bản",
                subtitle = "Giao tiếp hàng ngày, du lịch",
                iconEmoji = "🇮🇹",
                level = "Cơ bản",
                colorHex = "#10B981",
                cardCount = 40
            ),

            // Portuguese Deck
            DeckEntity(
                id = "pt_basics",
                languageCode = "pt",
                title = "Tiếng Bồ Đào Nha cơ bản",
                subtitle = "Chào hỏi, từ vựng thông dụng",
                iconEmoji = "🇵🇹",
                level = "Cơ bản",
                colorHex = "#8B5CF6",
                cardCount = 40
            ),

            // Korean Decks
            DeckEntity(
                id = "ko_beginner",
                languageCode = "ko",
                title = "한국어 초급",
                subtitle = "Chào hỏi, cảm ơn, sinh hoạt thường nhật",
                iconEmoji = "🇰🇷",
                level = "초급 TOPIK 1",
                colorHex = "#38BDF8",
                cardCount = 76
            ),
            DeckEntity(
                id = "ko_daily",
                languageCode = "ko",
                title = "Giao tiếp tiếng Hàn cơ bản",
                subtitle = "Chào hỏi, cảm ơn, sinh hoạt thường nhật",
                iconEmoji = "🇰🇷",
                level = "TOPIK 1",
                colorHex = "#38BDF8",
                cardCount = 8
            ),

            // Japanese Decks
            DeckEntity(
                id = "ja_n5",
                languageCode = "ja",
                title = "Tiếng Nhật N5",
                subtitle = "Nền tảng sơ cấp, đồ vật, hành động quen thuộc",
                iconEmoji = "🌸",
                level = "JLPT N5",
                colorHex = "#F472B6",
                cardCount = 128
            ),
            DeckEntity(
                id = "ja_daily",
                languageCode = "ja",
                title = "Giao tiếp tiếng Nhật hàng ngày",
                subtitle = "Kính ngữ cơ bản, chào hỏi, sinh hoạt",
                iconEmoji = "🍱",
                level = "Cơ bản",
                colorHex = "#FB7185",
                cardCount = 6
            ),

            // Vietnamese for foreigners / learners
            DeckEntity(
                id = "vi_basic",
                languageCode = "vi",
                title = "Tiếng Việt",
                subtitle = "Chào hỏi, mua bán chợ búa, kết bạn",
                iconEmoji = "🇻🇳",
                level = "Bản địa",
                colorHex = "#F59E0B",
                cardCount = 54
            ),

            // Chinese Decks
            DeckEntity(
                id = "zh_hsk1",
                languageCode = "zh",
                title = "Từ vựng HSK 1 - 2 Cơ bản",
                subtitle = "Pinyin, bộ thủ, các từ nền tảng nhất",
                iconEmoji = "🏮",
                level = "HSK 1-2",
                colorHex = "#EF4444",
                cardCount = 8
            ),

            // French Decks
            DeckEntity(
                id = "fr_basic",
                languageCode = "fr",
                title = "Tiếng Pháp cơ bản",
                subtitle = "Chào hỏi thanh lịch, du lịch Paris",
                iconEmoji = "🥐",
                level = "DELF A1-A2",
                colorHex = "#14B8A6",
                cardCount = 50
            )
        )
    }

    fun getDefaultFlashCards(): List<FlashCardEntity> {
        val existingLegacyCards = listOf(
            // Korean - Daily
            FlashCardEntity(
                deckId = "ko_daily",
                languageCode = "ko",
                frontWord = "안녕하세요",
                phonetic = "An-nyeong-ha-se-yo",
                partOfSpeech = "phrase",
                frontExample = "안녕하세요! 만나서 반갑습니다.",
                backMeaning = "Xin chào (Kính ngữ lịch sự phổ biến)",
                backExampleTranslation = "Xin chào! Rất vui được gặp bạn.",
                memoryTip = "Nghĩa đen: 'Bạn có đang bình an vô sự không?'"
            ),
            FlashCardEntity(
                deckId = "ko_daily",
                languageCode = "ko",
                frontWord = "감사합니다",
                phonetic = "Gam-sa-ham-ni-da",
                partOfSpeech = "phrase",
                frontExample = "도와주셔서 정말 감사합니다.",
                backMeaning = "Xin chân thành cảm ơn (Trang trọng)",
                backExampleTranslation = "Cảm ơn bạn rất nhiều vì đã giúp đỡ.",
                memoryTip = "Gam-sa (Cảm tạ) + Ham-ni-da (Làm/Thể hiện)."
            ),
            FlashCardEntity(
                deckId = "ko_daily",
                languageCode = "ko",
                frontWord = "행복해요",
                phonetic = "Haeng-bok-hae-yo",
                partOfSpeech = "adjective",
                frontExample = "오늘 하루 정말 행복해요.",
                backMeaning = "Hạnh phúc / Tôi cảm thấy hạnh phúc",
                backExampleTranslation = "Hôm nay tôi thực sự cảm thấy rất hạnh phúc.",
                memoryTip = "Haeng-bok = Hạnh phúc (Hán Hàn)."
            ),
            FlashCardEntity(
                deckId = "ko_daily",
                languageCode = "ko",
                frontWord = "열심히",
                phonetic = "Yeol-sim-hi",
                partOfSpeech = "adverb",
                frontExample = "한국어를 열심히 공부하고 있어요.",
                backMeaning = "Chăm chỉ, hết mình, nỗ lực",
                backExampleTranslation = "Tôi đang học tiếng Hàn rất chăm chỉ.",
                memoryTip = "Yeol (Nhiệt) + Sim (Tâm) = Dồn hết tâm huyết."
            ),
            FlashCardEntity(
                deckId = "ko_daily",
                languageCode = "ko",
                frontWord = "맛있어요",
                phonetic = "Mas-iss-eo-yo",
                partOfSpeech = "adjective",
                frontExample = "이 비빔밥 진짜 맛있어요!",
                backMeaning = "Ngon miệng / Rất ngon",
                backExampleTranslation = "Món cơm trộn Bibimbap này ngon thật sự!",
                memoryTip = "Mat (Vị ngon) + Iss-eo (Có) = Có hương vị tuyệt vời."
            ),
            FlashCardEntity(
                deckId = "ko_daily",
                languageCode = "ko",
                frontWord = "친구",
                phonetic = "Chin-gu",
                partOfSpeech = "noun",
                frontExample = "우리는 좋은 친구예요.",
                backMeaning = "Bạn bè / Bạn thân",
                backExampleTranslation = "Chúng tôi là những người bạn tốt của nhau.",
                memoryTip = "Chin (Thân) + Gu (Cựu) = Bạn bè thân thiết."
            ),

            // Japanese - JLPT N5
            FlashCardEntity(
                deckId = "ja_n5",
                languageCode = "ja",
                frontWord = "ありがとう",
                phonetic = "Arigatō / ありがとうございます",
                partOfSpeech = "phrase",
                frontExample = "親切にしてくれて、どうもありがとう。",
                backMeaning = "Cảm ơn bạn rất nhiều",
                backExampleTranslation = "Cảm ơn bạn đã luôn đối xử tốt bụng với tôi.",
                memoryTip = "Nghĩa gốc: 'Arigatashi' (Điều hiếm có và đáng trân quý)."
            ),
            FlashCardEntity(
                deckId = "ja_n5",
                languageCode = "ja",
                frontWord = "頑張って",
                phonetic = "Ganbatte (がんばって)",
                partOfSpeech = "phrase",
                frontExample = "明日のテスト、頑張ってください！",
                backMeaning = "Cố gắng lên nhé! / Chúc bạn may mắn!",
                backExampleTranslation = "Bài kiểm tra ngày mai hãy cố gắng lên nhé!",
                memoryTip = "Lời động viên quen thuộc và giàu năng lượng nhất trong tiếng Nhật."
            ),
            FlashCardEntity(
                deckId = "ja_n5",
                languageCode = "ja",
                frontWord = "先生",
                phonetic = "Sensei (せんせい)",
                partOfSpeech = "noun",
                frontExample = "日本語の先生はとても優しいです。",
                backMeaning = "Thầy giáo / Cô giáo / Tiên sinh",
                backExampleTranslation = "Giáo viên dạy tiếng Nhật rất đỗi dịu dàng.",
                memoryTip = "Chữ Hán: Tiên Sinh (Người sinh ra / học trước mình)."
            ),
            FlashCardEntity(
                deckId = "ja_n5",
                languageCode = "ja",
                frontWord = "桜",
                phonetic = "Sakura (さくら)",
                partOfSpeech = "noun",
                frontExample = "春になると桜が綺麗に咲きます。",
                backMeaning = "Hoa anh đào",
                backExampleTranslation = "Khi mùa xuân đến, hoa anh đào nở rộ tuyệt đẹp.",
                memoryTip = "Biểu tượng văn hóa bất hủ của đất nước Nhật Bản."
            ),
            FlashCardEntity(
                deckId = "ja_n5",
                languageCode = "ja",
                frontWord = "友達",
                phonetic = "Tomodachi (ともだち)",
                partOfSpeech = "noun",
                frontExample = "週末に友達と映画を見に行きます。",
                backMeaning = "Bạn bè / Bạn đồng hành",
                backExampleTranslation = "Cuối tuần tôi sẽ đi xem phim cùng bạn bè.",
                memoryTip = "Chữ Hán: Hữu Đạt (Người bạn hữu đồng hành)."
            ),

            // Chinese - HSK 1
            FlashCardEntity(
                deckId = "zh_hsk1",
                languageCode = "zh",
                frontWord = "你好",
                phonetic = "Nǐ hǎo",
                partOfSpeech = "phrase",
                frontExample = "你好！很高兴认识你。",
                backMeaning = "Xin chào bạn!",
                backExampleTranslation = "Xin chào! Rất vui được quen biết bạn.",
                memoryTip = "Nǐ (Bạn) + Hǎo (Tốt/An lành)."
            ),
            FlashCardEntity(
                deckId = "zh_hsk1",
                languageCode = "zh",
                frontWord = "谢谢",
                phonetic = "Xièxie",
                partOfSpeech = "phrase",
                frontExample = "太谢谢你的帮助了！",
                backMeaning = "Cảm ơn bạn rất nhiều",
                backExampleTranslation = "Cảm ơn sự giúp đỡ của bạn rất nhiều!",
                memoryTip = "Chữ Hán: Tạ Tạ (Cảm tạ)."
            ),
            FlashCardEntity(
                deckId = "zh_hsk1",
                languageCode = "zh",
                frontWord = "学习",
                phonetic = "Xuéxí",
                partOfSpeech = "verb",
                frontExample = "每天学习新的汉语生词。",
                backMeaning = "Học tập / Rèn luyện kiến thức",
                backExampleTranslation = "Mỗi ngày đều học thêm các từ mới tiếng Trung.",
                memoryTip = "Chữ Hán: Học Tập."
            ),
            FlashCardEntity(
                deckId = "zh_hsk1",
                languageCode = "zh",
                frontWord = "朋友",
                phonetic = "Péngyou",
                partOfSpeech = "noun",
                frontExample = "我们是最好的朋友。",
                backMeaning = "Bạn bè / Bằng hữu",
                backExampleTranslation = "Chúng ta là những người bạn tốt nhất.",
                memoryTip = "Chữ Hán: Bằng Hữu (Hai vầng trăng sáng bên nhau)."
            ),

            // French - Basic
            FlashCardEntity(
                deckId = "fr_basic",
                languageCode = "fr",
                frontWord = "Bonjour",
                phonetic = "/bɔ̃.ʒuʁ/",
                partOfSpeech = "phrase",
                frontExample = "Bonjour madame, comment allez-vous ?",
                backMeaning = "Xin chào (Buổi sáng / Ban ngày lịch sự)",
                backExampleTranslation = "Xin chào bà, bà có khỏe không ạ?",
                memoryTip = "Bon (Tốt đẹp) + Jour (Ngày mới)."
            ),
            FlashCardEntity(
                deckId = "fr_basic",
                languageCode = "fr",
                frontWord = "Merci beaucoup",
                phonetic = "/mɛʁ.si bo.ku/",
                partOfSpeech = "phrase",
                frontExample = "Merci beaucoup pour votre accueil chaleureux.",
                backMeaning = "Cảm ơn bạn rất nhiều",
                backExampleTranslation = "Cảm ơn bạn rất nhiều vì sự đón tiếp nồng hậu.",
                memoryTip = "Merci (Cảm ơn) + Beaucoup (Rất nhiều)."
            ),
            FlashCardEntity(
                deckId = "fr_basic",
                languageCode = "fr",
                frontWord = "Magnifique",
                phonetic = "/ma.ɲi.fik/",
                partOfSpeech = "adjective",
                frontExample = "La vue sur Paris est vraiment magnifique.",
                backMeaning = "Tuyệt mỹ, lộng lẫy, kỳ vĩ",
                backExampleTranslation = "Khung cảnh nhìn ra toàn cảnh Paris thực sự tráng lệ.",
                memoryTip = "Đồng nghĩa với Gorgeous, Splendid."
            ),
            FlashCardEntity(
                deckId = "fr_basic",
                languageCode = "fr",
                frontWord = "Enchanté",
                phonetic = "/ɑ̃.ʃɑ̃.te/",
                partOfSpeech = "phrase",
                frontExample = "Enchanté de faire votre connaissance.",
                backMeaning = "Rất hân hạnh được làm quen với bạn",
                backExampleTranslation = "Rất hân hạnh được gặp và trò chuyện cùng bạn.",
                memoryTip = "Gốc từ: Enchant (Mê hoặc, đầy vinh dự)."
            ),

            // Korean Beginner
            FlashCardEntity(
                deckId = "ko_beginner",
                languageCode = "ko",
                frontWord = "안녕하세요",
                phonetic = "An-nyeong-ha-se-yo",
                partOfSpeech = "phrase",
                frontExample = "안녕하세요! 만나서 반갑습니다.",
                backMeaning = "Xin chào (Kính ngữ tiêu chuẩn lịch sự)",
                backExampleTranslation = "Xin chào! Rất vui được gặp bạn.",
                memoryTip = "An-nyeong (Bình an, yên lành)."
            ),
            FlashCardEntity(
                deckId = "ko_beginner",
                languageCode = "ko",
                frontWord = "감사합니다",
                phonetic = "Gam-sa-ham-ni-da",
                partOfSpeech = "phrase",
                frontExample = "도와주셔서 정말 감사합니다.",
                backMeaning = "Cảm ơn bạn rất nhiều (Trang trọng)",
                backExampleTranslation = "Cảm ơn bạn rất nhiều vì đã giúp đỡ tôi.",
                memoryTip = "Gam-sa (Cảm tạ)."
            ),

            // Vietnamese Basics
            FlashCardEntity(
                deckId = "vi_basic",
                languageCode = "vi",
                frontWord = "Xin chào",
                phonetic = "Xin chào",
                partOfSpeech = "phrase",
                frontExample = "Xin chào tất cả các bạn đã đến với Việt Nam!",
                backMeaning = "Lời chào thân ái truyền thống của người Việt",
                backExampleTranslation = "Lời chào lịch sự trong mọi hoàn cảnh.",
                memoryTip = "Xin + Chào."
            ),
            FlashCardEntity(
                deckId = "vi_basic",
                languageCode = "vi",
                frontWord = "Cảm ơn",
                phonetic = "Cảm ơn",
                partOfSpeech = "phrase",
                frontExample = "Cảm ơn bạn đã luôn đồng hành cùng tôi.",
                backMeaning = "Bày tỏ lòng biết ơn chân thành",
                backExampleTranslation = "Lời cảm ơn ấm áp từ trái tim.",
                memoryTip = "Cảm (Cảm xúc) + Ơn (Ân huệ)."
            )
        )

        return existingLegacyCards +
                EnVocabData.getCards() +
                EsVocabData.getCards() +
                DeVocabData.getCards() +
                ItVocabData.getCards() +
                PtVocabData.getCards()
    }
}
