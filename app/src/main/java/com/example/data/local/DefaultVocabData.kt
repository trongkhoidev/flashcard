package com.example.data.local

import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity

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
                id = "en_daily",
                languageCode = "en",
                title = "Giao tiếp hàng ngày",
                subtitle = "Các từ vựng và cụm từ thiết yếu mỗi ngày",
                iconEmoji = "☕",
                level = "Cơ bản A1-A2",
                colorHex = "#5B67EA",
                cardCount = 8
            ),
            DeckEntity(
                id = "en_travel",
                languageCode = "en",
                title = "Du lịch & Khám phá",
                subtitle = "Sân bay, khách sạn, hỏi đường và mua sắm",
                iconEmoji = "✈️",
                level = "Thực chiến",
                colorHex = "#3B82F6",
                cardCount = 8
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
        return listOf(
            // English - Daily
            FlashCardEntity(
                deckId = "en_daily",
                languageCode = "en",
                frontWord = "Perseverance",
                phonetic = "/ˌpɜː.sɪˈvɪə.rəns/",
                partOfSpeech = "noun",
                frontExample = "Through hard work and perseverance, she succeeded.",
                backMeaning = "Sự kiên trì, bền chí vượt qua khó khăn",
                backExampleTranslation = "Nhờ làm việc chăm chỉ và kiên trì, cô ấy đã thành công.",
                memoryTip = "Gợi ý: 'Severe' (khắc nghiệt) -> Người kiên trì đi xuyên qua nghịch cảnh."
            ),
            FlashCardEntity(
                deckId = "en_daily",
                languageCode = "en",
                frontWord = "Serendipity",
                phonetic = "/ˌser.ənˈdɪp.ə.ti/",
                partOfSpeech = "noun",
                frontExample = "Finding this wonderful cafe was pure serendipity.",
                backMeaning = "Sự may mắn bất ngờ, tình cờ gặp điều tốt đẹp",
                backExampleTranslation = "Tìm thấy quán cà phê tuyệt vời này hoàn toàn là sự tình cờ may mắn.",
                memoryTip = "Mẹo: Từ ngữ đẹp nhất chỉ những cuộc gặp gỡ định mệnh."
            ),
            FlashCardEntity(
                deckId = "en_daily",
                languageCode = "en",
                frontWord = "Enthusiastic",
                phonetic = "/ɪnˈθjuː.zi.æs.tɪk/",
                partOfSpeech = "adjective",
                frontExample = "He is very enthusiastic about learning new languages.",
                backMeaning = "Nhiệt huyết, hào hứng, đầy đam mê",
                backExampleTranslation = "Anh ấy rất hào hứng với việc học các ngôn ngữ mới.",
                memoryTip = "Gốc từ: 'En-theos' (năng lượng tràn đầy bên trong)."
            ),
            FlashCardEntity(
                deckId = "en_daily",
                languageCode = "en",
                frontWord = "Resilient",
                phonetic = "/rɪˈzɪl.jənt/",
                partOfSpeech = "adjective",
                frontExample = "Children are often remarkably resilient to change.",
                backMeaning = "Kiên cường, có khả năng phục hồi nhanh chóng",
                backExampleTranslation = "Trẻ em thường kiên cường và thích nghi rất nhanh với sự thay đổi.",
                memoryTip = "Hình dung: Một cành tre uốn cong theo gió rồi bật thẳng trở lại."
            ),
            FlashCardEntity(
                deckId = "en_daily",
                languageCode = "en",
                frontWord = "Accomplish",
                phonetic = "/əˈkʌm.plɪʃ/",
                partOfSpeech = "verb",
                frontExample = "You can accomplish anything with consistent effort.",
                backMeaning = "Hoàn thành, đạt được mục tiêu xuất sắc",
                backExampleTranslation = "Bạn có thể đạt được bất kỳ điều gì bằng nỗ lực kiên định.",
                memoryTip = "Đồng nghĩa với: Achieve, complete successfully."
            ),
            FlashCardEntity(
                deckId = "en_daily",
                languageCode = "en",
                frontWord = "Ingenious",
                phonetic = "/ɪnˈdʒiː.ni.əs/",
                partOfSpeech = "adjective",
                frontExample = "It was an ingenious solution to a complex problem.",
                backMeaning = "Khéo léo, tài tình, thông minh xuất chúng",
                backExampleTranslation = "Đó là một giải pháp tài tình cho một vấn đề phức tạp.",
                memoryTip = "Gần với từ 'Genius' (thiên tài)."
            ),
            FlashCardEntity(
                deckId = "en_daily",
                languageCode = "en",
                frontWord = "Eloquent",
                phonetic = "/ˈel.ə.kwənt/",
                partOfSpeech = "adjective",
                frontExample = "She gave an eloquent speech at the conference.",
                backMeaning = "Hùng biện, lưu loát, truyền cảm",
                backExampleTranslation = "Cô ấy đã có một bài phát biểu đầy hùng biện tại hội nghị.",
                memoryTip = "Liên quan đến lời nói mượt mà, thu hút người nghe."
            ),
            FlashCardEntity(
                deckId = "en_daily",
                languageCode = "en",
                frontWord = "Grateful",
                phonetic = "/ˈɡreɪt.fəl/",
                partOfSpeech = "adjective",
                frontExample = "I am grateful for all the support I received.",
                backMeaning = "Biết ơn, trân trọng",
                backExampleTranslation = "Tôi vô cùng biết ơn mọi sự giúp đỡ mà tôi nhận được.",
                memoryTip = "Từ gốc: Gratitude (Lòng biết ơn)."
            ),

            // English - Travel
            FlashCardEntity(
                deckId = "en_travel",
                languageCode = "en",
                frontWord = "Itinerary",
                phonetic = "/aɪˈtɪn.ər.ər.i/",
                partOfSpeech = "noun",
                frontExample = "We planned an exciting 7-day travel itinerary.",
                backMeaning = "Lịch trình chuyến đi, kế hoạch tham quan",
                backExampleTranslation = "Chúng tôi đã lên lịch trình du lịch 7 ngày đầy thú vị.",
                memoryTip = "Nhớ: Bản đồ các điểm dừng chân trong chuyến đi."
            ),
            FlashCardEntity(
                deckId = "en_travel",
                languageCode = "en",
                frontWord = "Boarding Pass",
                phonetic = "/ˈbɔː.dɪŋ ˌpɑːs/",
                partOfSpeech = "noun",
                frontExample = "Please have your boarding pass ready at the gate.",
                backMeaning = "Thẻ lên máy bay / vé lên tàu",
                backExampleTranslation = "Vui lòng chuẩn bị sẵn thẻ lên máy bay tại cửa khởi hành.",
                memoryTip = "Board = lên tàu/máy bay, Pass = thẻ thông hành."
            ),
            FlashCardEntity(
                deckId = "en_travel",
                languageCode = "en",
                frontWord = "Baggage Claim",
                phonetic = "/ˈbæɡ.ɪdʒ ˌkleɪm/",
                partOfSpeech = "noun",
                frontExample = "Proceed to the baggage claim area on the ground floor.",
                backMeaning = "Khu vực nhận lại hành lý ký gửi tại sân bay",
                backExampleTranslation = "Hãy di chuyển đến khu vực nhận hành lý ở tầng trệt.",
                memoryTip = "Claim = nhận quyền sở hữu hành lý của mình."
            ),
            FlashCardEntity(
                deckId = "en_travel",
                languageCode = "en",
                frontWord = "Currency Exchange",
                phonetic = "/ˈkʌr.ən.si ɪksˈtʃeɪndʒ/",
                partOfSpeech = "noun",
                frontExample = "Where is the nearest currency exchange booth?",
                backMeaning = "Quầy đổi tiền ngoại tệ",
                backExampleTranslation = "Quầy đổi ngoại tệ gần nhất ở đâu vậy?",
                memoryTip = "Currency = tiền tệ, Exchange = trao đổi."
            ),

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

            // English Basics
            FlashCardEntity(
                deckId = "en_basics",
                languageCode = "en",
                frontWord = "Welcome",
                phonetic = "/ˈwel.kəm/",
                partOfSpeech = "phrase",
                frontExample = "Welcome to our English learning community!",
                backMeaning = "Chào mừng bạn đến với chúng tôi",
                backExampleTranslation = "Chào mừng bạn đến với cộng đồng học tiếng Anh!",
                memoryTip = "Well + Come: Lời chào thân ái."
            ),
            FlashCardEntity(
                deckId = "en_basics",
                languageCode = "en",
                frontWord = "Opportunity",
                phonetic = "/ˌɒp.əˈtʃuː.nə.ti/",
                partOfSpeech = "noun",
                frontExample = "Every mistake is an opportunity to learn.",
                backMeaning = "Cơ hội, thời cơ quý giá",
                backExampleTranslation = "Mỗi sai lầm đều là một cơ hội quý báu để học hỏi.",
                memoryTip = "Cánh cửa mở ra thành công."
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
    }
}
