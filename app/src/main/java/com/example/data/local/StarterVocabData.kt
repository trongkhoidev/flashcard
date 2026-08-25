package com.example.data.local

import com.example.data.model.AppLanguage
import com.example.data.model.FlashCardEntity

/**
 * 5 bộ từ vựng khởi đầu (Starter Pack - 5 từ mỗi ngôn ngữ)
 * Được thiết kế chuẩn xác, giàu tính ứng dụng để người học trải nghiệm ngay lần đầu.
 */
object StarterVocabData {

    fun getStarterCardsForLanguage(language: AppLanguage): List<FlashCardEntity> {
        return when (language) {
            AppLanguage.ENGLISH -> listOf(
                FlashCardEntity(
                    id = 10001L,
                    deckId = "en_starter",
                    languageCode = "en",
                    frontWord = "Hello",
                    phonetic = "/həˈləʊ/",
                    partOfSpeech = "phrase",
                    frontExample = "Hello! Nice to meet you.",
                    backMeaning = "Xin chào (Lời chào thân thiện phổ biến nhất)",
                    backExampleTranslation = "Xin chào! Rất vui được gặp bạn.",
                    memoryTip = "Lời chào mở đầu mọi cuộc trò chuyện tiếng Anh."
                ),
                FlashCardEntity(
                    id = 10002L,
                    deckId = "en_starter",
                    languageCode = "en",
                    frontWord = "Thank you",
                    phonetic = "/ˈθæŋk juː/",
                    partOfSpeech = "phrase",
                    frontExample = "Thank you very much for your help.",
                    backMeaning = "Cảm ơn bạn rất nhiều",
                    backExampleTranslation = "Cảm ơn bạn rất nhiều vì sự giúp đỡ.",
                    memoryTip = "Thành ngữ thể hiện sự lịch thiệp và lòng biết ơn."
                ),
                FlashCardEntity(
                    id = 10003L,
                    deckId = "en_starter",
                    languageCode = "en",
                    frontWord = "Delicious",
                    phonetic = "/dɪˈlɪʃ.əs/",
                    partOfSpeech = "adjective",
                    frontExample = "This Vietnamese coffee is really delicious!",
                    backMeaning = "Thơm ngon, ngon miệng",
                    backExampleTranslation = "Món cà phê Việt Nam này thực sự rất ngon!",
                    memoryTip = "Khen ngợi món ăn: Deli (ngon) + cious."
                ),
                FlashCardEntity(
                    id = 10004L,
                    deckId = "en_starter",
                    languageCode = "en",
                    frontWord = "Adventure",
                    phonetic = "/ədˈven.tʃər/",
                    partOfSpeech = "noun",
                    frontExample = "Learning a new language is a great adventure.",
                    backMeaning = "Cuộc phiêu lưu, hành trình khám phá",
                    backExampleTranslation = "Học một ngôn ngữ mới là một cuộc phiêu lưu tuyệt vời.",
                    memoryTip = "Ad-venture: Bước ra ngoài khám phá thế giới."
                ),
                FlashCardEntity(
                    id = 10005L,
                    deckId = "en_starter",
                    languageCode = "en",
                    frontWord = "Perseverance",
                    phonetic = "/ˌpɜː.sɪˈvɪə.rəns/",
                    partOfSpeech = "noun",
                    frontExample = "With perseverance, you can master any skill.",
                    backMeaning = "Sự kiên trì, bền chí vượt khó khăn",
                    backExampleTranslation = "Bằng sự kiên trì, bạn có thể làm chủ bất kỳ kỹ năng nào.",
                    memoryTip = "Phẩm chất vàng của người học ngoại ngữ thành công."
                )
            )

            AppLanguage.KOREAN -> listOf(
                FlashCardEntity(
                    id = 20001L,
                    deckId = "ko_starter",
                    languageCode = "ko",
                    frontWord = "안녕하세요",
                    phonetic = "An-nyeong-ha-se-yo",
                    partOfSpeech = "phrase",
                    frontExample = "안녕하세요! 만나서 반갑습니다.",
                    backMeaning = "Xin chào (Kính ngữ tiêu chuẩn lịch sự)",
                    backExampleTranslation = "Xin chào! Rất vui được gặp bạn.",
                    memoryTip = "Nghĩa đen: 'Bạn có đang bình an yên lành không?'"
                ),
                FlashCardEntity(
                    id = 20002L,
                    deckId = "ko_starter",
                    languageCode = "ko",
                    frontWord = "감사합니다",
                    phonetic = "Gam-sa-ham-ni-da",
                    partOfSpeech = "phrase",
                    frontExample = "도와주셔서 정말 감사합니다.",
                    backMeaning = "Xin cảm ơn bạn rất nhiều (Trang trọng)",
                    backExampleTranslation = "Cảm ơn bạn rất nhiều vì đã giúp đỡ.",
                    memoryTip = "Gam-sa (Cảm tạ) + Ham-ni-da (Làm)."
                ),
                FlashCardEntity(
                    id = 20003L,
                    deckId = "ko_starter",
                    languageCode = "ko",
                    frontWord = "맛있어요",
                    phonetic = "Mas-iss-eo-yo",
                    partOfSpeech = "adjective",
                    frontExample = "이 한국 음식 진짜 맛있어요!",
                    backMeaning = "Rất ngon / Thơm ngon tuyệt vời",
                    backExampleTranslation = "Món ăn Hàn Quốc này ngon thật sự!",
                    memoryTip = "Mat (Vị ngon) + Iss-eo (Có) = Có vị rất ngon."
                ),
                FlashCardEntity(
                    id = 20004L,
                    deckId = "ko_starter",
                    languageCode = "ko",
                    frontWord = "행복해요",
                    phonetic = "Haeng-bok-hae-yo",
                    partOfSpeech = "adjective",
                    frontExample = "새로운 언어를 배워서 행복해요.",
                    backMeaning = "Tôi cảm thấy hạnh phúc",
                    backExampleTranslation = "Học được ngôn ngữ mới khiến tôi rất hạnh phúc.",
                    memoryTip = "Haeng-bok = Hạnh phúc (Hán Hàn)."
                ),
                FlashCardEntity(
                    id = 20005L,
                    deckId = "ko_starter",
                    languageCode = "ko",
                    frontWord = "사랑해요",
                    phonetic = "Sa-rang-hae-yo",
                    partOfSpeech = "phrase",
                    frontExample = "한국 문화와 언어를 사랑해요.",
                    backMeaning = "Tôi yêu bạn / Tôi yêu thương",
                    backExampleTranslation = "Tôi rất yêu văn hóa và ngôn ngữ Hàn Quốc.",
                    memoryTip = "Câu nói quen thuộc và ngọt ngào nhất trong phim ảnh Hàn."
                )
            )

            AppLanguage.JAPANESE -> listOf(
                FlashCardEntity(
                    id = 30001L,
                    deckId = "ja_starter",
                    languageCode = "ja",
                    frontWord = "こんにちは",
                    phonetic = "Konnichiwa (コンニチハ)",
                    partOfSpeech = "phrase",
                    frontExample = "皆さん、こんにちは！",
                    backMeaning = "Xin chào (Chào ban ngày lịch sự)",
                    backExampleTranslation = "Xin chào tất cả mọi người!",
                    memoryTip = "Lời chào ban ngày chuẩn mực nhất của người Nhật."
                ),
                FlashCardEntity(
                    id = 30002L,
                    deckId = "ja_starter",
                    languageCode = "ja",
                    frontWord = "ありがとう",
                    phonetic = "Arigatō (ありがとうございます)",
                    partOfSpeech = "phrase",
                    frontExample = "親切に教えてくれてありがとう。",
                    backMeaning = "Cảm ơn bạn rất nhiều",
                    backExampleTranslation = "Cảm ơn bạn đã tận tình chỉ bảo cho tôi.",
                    memoryTip = "Nghĩa gốc: Điều hiếm có và vô cùng đáng quý."
                ),
                FlashCardEntity(
                    id = 30003L,
                    deckId = "ja_starter",
                    languageCode = "ja",
                    frontWord = "おいしい",
                    phonetic = "Oishii (美味しい)",
                    partOfSpeech = "adjective",
                    frontExample = "このラーメンはとてもおいしいです。",
                    backMeaning = "Ngon miệng / Rất ngon",
                    backExampleTranslation = "Bát mì ramen này thực sự rất ngon.",
                    memoryTip = "Chữ Hán: Mỹ Vị (Vị ngon tuyệt mỹ)."
                ),
                FlashCardEntity(
                    id = 30004L,
                    deckId = "ja_starter",
                    languageCode = "ja",
                    frontWord = "頑張って",
                    phonetic = "Ganbatte (がんばって)",
                    partOfSpeech = "phrase",
                    frontExample = "日本語の勉強、頑張ってください！",
                    backMeaning = "Cố gắng lên nhé! / Chúc bạn may mắn!",
                    backExampleTranslation = "Hãy cố gắng học tiếng Nhật thật tốt nhé!",
                    memoryTip = "Lời động viên tràn đầy năng lượng tích cực."
                ),
                FlashCardEntity(
                    id = 30005L,
                    deckId = "ja_starter",
                    languageCode = "ja",
                    frontWord = "友達",
                    phonetic = "Tomodachi (ともだち)",
                    partOfSpeech = "noun",
                    frontExample = "新しい友達がたくさんできました。",
                    backMeaning = "Bạn bè / Bạn đồng hành thân thiết",
                    backExampleTranslation = "Tôi đã kết thêm được rất nhiều bạn mới.",
                    memoryTip = "Chữ Hán: Hữu Đạt (Người bạn chí cốt bè bạn)."
                )
            )

            AppLanguage.CHINESE -> listOf(
                FlashCardEntity(
                    id = 40001L,
                    deckId = "zh_starter",
                    languageCode = "zh",
                    frontWord = "你好",
                    phonetic = "Nǐ hǎo",
                    partOfSpeech = "phrase",
                    frontExample = "你好！很高兴认识你。",
                    backMeaning = "Xin chào bạn!",
                    backExampleTranslation = "Xin chào! Rất vui được làm quen với bạn.",
                    memoryTip = "Nǐ (Bạn) + Hǎo (Tốt đẹp/Bình an)."
                ),
                FlashCardEntity(
                    id = 40002L,
                    deckId = "zh_starter",
                    languageCode = "zh",
                    frontWord = "谢谢",
                    phonetic = "Xièxie",
                    partOfSpeech = "phrase",
                    frontExample = "太谢谢你的热情帮助了！",
                    backMeaning = "Cảm ơn bạn rất nhiều",
                    backExampleTranslation = "Cảm ơn sự giúp đỡ nhiệt tình của bạn rất nhiều!",
                    memoryTip = "Chữ Hán: Tạ Tạ (Bày tỏ lòng cảm kích)."
                ),
                FlashCardEntity(
                    id = 40003L,
                    deckId = "zh_starter",
                    languageCode = "zh",
                    frontWord = "很好",
                    phonetic = "Hěn hǎo",
                    partOfSpeech = "adjective",
                    frontExample = "你的汉语发音非常好！",
                    backMeaning = "Rất tốt / Rất xuất sắc",
                    backExampleTranslation = "Phát âm tiếng Trung của bạn cực kỳ chuẩn và tốt!",
                    memoryTip = "Hěn (Rất) + Hǎo (Tốt/Đẹp)."
                ),
                FlashCardEntity(
                    id = 40004L,
                    deckId = "zh_starter",
                    languageCode = "zh",
                    frontWord = "学习",
                    phonetic = "Xuéxí",
                    partOfSpeech = "verb",
                    frontExample = "我们一起快乐地学习中文吧。",
                    backMeaning = "Học tập / Rèn luyện kiến thức",
                    backExampleTranslation = "Chúng ta hãy cùng nhau vui vẻ học tiếng Trung nhé.",
                    memoryTip = "Chữ Hán: Học Tập (Học hỏi và thực hành)."
                ),
                FlashCardEntity(
                    id = 40005L,
                    deckId = "zh_starter",
                    languageCode = "zh",
                    frontWord = "朋友",
                    phonetic = "Péngyou",
                    partOfSpeech = "noun",
                    frontExample = "海内存知己，天涯若比邻，我们是好朋友。",
                    backMeaning = "Bạn bè / Bằng hữu thân thiết",
                    backExampleTranslation = "Chúng ta là những người bạn tốt của nhau.",
                    memoryTip = "Chữ Hán: Bằng Hữu (Hai vầng trăng đồng hành)."
                )
            )

            AppLanguage.FRENCH -> listOf(
                FlashCardEntity(
                    id = 50001L,
                    deckId = "fr_starter",
                    languageCode = "fr",
                    frontWord = "Bonjour",
                    phonetic = "/bɔ̃.ʒuʁ/",
                    partOfSpeech = "phrase",
                    frontExample = "Bonjour! Comment allez-vous aujourd'hui ?",
                    backMeaning = "Xin chào (Buổi sáng & ban ngày thanh lịch)",
                    backExampleTranslation = "Xin chào! Hôm nay bạn có khỏe không?",
                    memoryTip = "Bon (Tốt lành) + Jour (Ngày mới)."
                ),
                FlashCardEntity(
                    id = 50002L,
                    deckId = "fr_starter",
                    languageCode = "fr",
                    frontWord = "Merci",
                    phonetic = "/mɛʁ.si/",
                    partOfSpeech = "phrase",
                    frontExample = "Merci beaucoup pour votre gentillesse.",
                    backMeaning = "Cảm ơn bạn rất nhiều",
                    backExampleTranslation = "Cảm ơn bạn rất nhiều vì sự tử tế tốt bụng.",
                    memoryTip = "Từ cảm ơn quen thuộc và lịch sự nhất tiếng Pháp."
                ),
                FlashCardEntity(
                    id = 50003L,
                    deckId = "fr_starter",
                    languageCode = "fr",
                    frontWord = "Magnifique",
                    phonetic = "/ma.ɲi.fik/",
                    partOfSpeech = "adjective",
                    frontExample = "La tour Eiffel est absolument magnifique.",
                    backMeaning = "Tuyệt mỹ, lộng lẫy, kỳ vĩ",
                    backExampleTranslation = "Tháp Eiffel thực sự tráng lệ và tuyệt đẹp.",
                    memoryTip = "Khen ngợi vẻ đẹp xuất chúng."
                ),
                FlashCardEntity(
                    id = 50004L,
                    deckId = "fr_starter",
                    languageCode = "fr",
                    frontWord = "Enchanté",
                    phonetic = "/ɑ̃.ʃɑ̃.te/",
                    partOfSpeech = "phrase",
                    frontExample = "Enchanté de faire votre connaissance !",
                    backMeaning = "Rất hân hạnh được làm quen với bạn",
                    backExampleTranslation = "Rất hân hạnh được gặp và quen biết bạn!",
                    memoryTip = "Gốc từ: Enchant (Vinh hạnh, đầy mê hoặc)."
                ),
                FlashCardEntity(
                    id = 50005L,
                    deckId = "fr_starter",
                    languageCode = "fr",
                    frontWord = "Au revoir",
                    phonetic = "/o ʁə.vwaʁ/",
                    partOfSpeech = "phrase",
                    frontExample = "Au revoir et à bientôt !",
                    backMeaning = "Tạm biệt / Hẹn sớm gặp lại bạn",
                    backExampleTranslation = "Tạm biệt và hẹn sớm gặp lại nhé!",
                    memoryTip = "Revoir = Hẹn ngày tái ngộ."
                )
            )

            AppLanguage.SPANISH -> listOf(
                FlashCardEntity(
                    id = 60001L,
                    deckId = "es_starter",
                    languageCode = "es",
                    frontWord = "¡Hola!",
                    phonetic = "/ˈo.la/",
                    partOfSpeech = "phrase",
                    frontExample = "¡Hola! ¿Cómo estás hoy?",
                    backMeaning = "Xin chào bạn!",
                    backExampleTranslation = "Xin chào! Hôm nay bạn thế nào rồi?",
                    memoryTip = "Lời chào ấm áp, thân tình của vùng đất Tây Ban Nha."
                ),
                FlashCardEntity(
                    id = 60002L,
                    deckId = "es_starter",
                    languageCode = "es",
                    frontWord = "Gracias",
                    phonetic = "/ˈɡɾa.sjas/",
                    partOfSpeech = "phrase",
                    frontExample = "Muchas gracias por tu valiosa ayuda.",
                    backMeaning = "Cảm ơn bạn rất nhiều",
                    backExampleTranslation = "Cảm ơn bạn rất nhiều vì sự giúp đỡ quý giá.",
                    memoryTip = "Gốc từ: Ân sủng và lòng biết ơn (Grace)."
                ),
                FlashCardEntity(
                    id = 60003L,
                    deckId = "es_starter",
                    languageCode = "es",
                    frontWord = "Amigo",
                    phonetic = "/aˈmi.ɣo/",
                    partOfSpeech = "noun",
                    frontExample = "Un buen amigo siempre está a tu lado.",
                    backMeaning = "Người bạn / Bằng hữu thân thiết",
                    backExampleTranslation = "Một người bạn tốt luôn kề vai sát cánh bên bạn.",
                    memoryTip = "Amigo = Bạn bè (nam), Amiga = Bạn bè (nữ)."
                ),
                FlashCardEntity(
                    id = 60004L,
                    deckId = "es_starter",
                    languageCode = "es",
                    frontWord = "Hermoso",
                    phonetic = "/eɾˈmo.so/",
                    partOfSpeech = "adjective",
                    frontExample = "¡Qué día tan hermoso para aprender algo nuevo!",
                    backMeaning = "Đẹp đẽ, tuyệt đẹp, rực rỡ",
                    backExampleTranslation = "Thật là một ngày tuyệt đẹp để học điều mới!",
                    memoryTip = "Dùng để khen cảnh đẹp hoặc người đẹp."
                ),
                FlashCardEntity(
                    id = 60005L,
                    deckId = "es_starter",
                    languageCode = "es",
                    frontWord = "¡Vamos!",
                    phonetic = "/ˈba.mos/",
                    partOfSpeech = "phrase",
                    frontExample = "¡Vamos! ¡Tú puedes lograrlo con éxito!",
                    backMeaning = "Cùng tiến lên nào! / Cố lên!",
                    backExampleTranslation = "Tiến lên nào! Bạn hoàn toàn có thể làm được!",
                    memoryTip = "Khẩu hiệu khích lệ tinh thần nổi tiếng."
                )
            )

            AppLanguage.GERMAN -> listOf(
                FlashCardEntity(
                    id = 70001L,
                    deckId = "de_starter",
                    languageCode = "de",
                    frontWord = "Hallo",
                    phonetic = "/ˈhaloː/",
                    partOfSpeech = "phrase",
                    frontExample = "Hallo! Wie geht es dir?",
                    backMeaning = "Xin chào bạn!",
                    backExampleTranslation = "Xin chào! Bạn có khỏe không?",
                    memoryTip = "Lời chào thân mật, phổ biến nhất ở Đức."
                ),
                FlashCardEntity(
                    id = 70002L,
                    deckId = "de_starter",
                    languageCode = "de",
                    frontWord = "Danke",
                    phonetic = "/ˈdaŋkə/",
                    partOfSpeech = "phrase",
                    frontExample = "Vielen Dank für deine Unterstützung.",
                    backMeaning = "Cảm ơn bạn rất nhiều",
                    backExampleTranslation = "Cảm ơn bạn rất nhiều vì đã ủng hộ giúp đỡ.",
                    memoryTip = "Danke schön = Cảm ơn bạn rất nhiều."
                ),
                FlashCardEntity(
                    id = 70003L,
                    deckId = "de_starter",
                    languageCode = "de",
                    frontWord = "Wunderbar",
                    phonetic = "/ˈvʊndɐbaːɐ̯/",
                    partOfSpeech = "adjective",
                    frontExample = "Das Wetter heute ist einfach wunderbar.",
                    backMeaning = "Kỳ diệu, tuyệt vời, xuất sắc",
                    backExampleTranslation = "Thời tiết hôm nay thật sự tuyệt vời.",
                    memoryTip = "Wunder (Kỳ tích) + Bar = Đầy sự kỳ diệu."
                ),
                FlashCardEntity(
                    id = 70004L,
                    deckId = "de_starter",
                    languageCode = "de",
                    frontWord = "Freund",
                    phonetic = "/fʁɔɪ̯nt/",
                    partOfSpeech = "noun",
                    frontExample = "Er ist mein bester Freund seit vielen Jahren.",
                    backMeaning = "Người bạn thân / Bạn bè",
                    backExampleTranslation = "Anh ấy là người bạn thân nhất của tôi từ nhiều năm nay.",
                    memoryTip = "Freund = Bạn nam, Freundin = Bạn nữ."
                ),
                FlashCardEntity(
                    id = 70005L,
                    deckId = "de_starter",
                    languageCode = "de",
                    frontWord = "Guten Tag",
                    phonetic = "/ˌɡuːtn̩ ˈtaːk/",
                    partOfSpeech = "phrase",
                    frontExample = "Guten Tag, freut mich, Sie kennenzulernen.",
                    backMeaning = "Chúc một ngày tốt lành (Trang trọng)",
                    backExampleTranslation = "Xin chào, rất vui được làm quen với quý vị.",
                    memoryTip = "Gut (Tốt) + Tag (Ngày)."
                )
            )

            AppLanguage.ITALIAN -> listOf(
                FlashCardEntity(
                    id = 80001L,
                    deckId = "it_starter",
                    languageCode = "it",
                    frontWord = "Ciao",
                    phonetic = "/ˈtʃa.o/",
                    partOfSpeech = "phrase",
                    frontExample = "Ciao! Come stai oggi?",
                    backMeaning = "Xin chào / Tạm biệt (Thân thiện)",
                    backExampleTranslation = "Xin chào! Hôm nay bạn thế nào?",
                    memoryTip = "Từ chào nổi tiếng nhất thế giới của nước Ý."
                ),
                FlashCardEntity(
                    id = 80002L,
                    deckId = "it_starter",
                    languageCode = "it",
                    frontWord = "Grazie",
                    phonetic = "/ˈɡrat.tsje/",
                    partOfSpeech = "phrase",
                    frontExample = "Grazie mille di tutto cuore!",
                    backMeaning = "Cảm ơn bạn rất nhiều (Từ tận đáy lòng)",
                    backExampleTranslation = "Cảm ơn bạn ngàn lần từ tận đáy lòng!",
                    memoryTip = "Grazie mille = Ngàn lời cảm ơn bạn."
                ),
                FlashCardEntity(
                    id = 80003L,
                    deckId = "it_starter",
                    languageCode = "it",
                    frontWord = "Bellissimo",
                    phonetic = "/belˈlis.si.mo/",
                    partOfSpeech = "adjective",
                    frontExample = "L'Italia è un paese bellissimo e affascinante.",
                    backMeaning = "Cực kỳ xinh đẹp, tráng lệ, tuyệt vời",
                    backExampleTranslation = "Nước Ý là một đất nước vô cùng xinh đẹp và quyến rũ.",
                    memoryTip = "Cấp độ cao nhất của 'Bello' (Đẹp)."
                ),
                FlashCardEntity(
                    id = 80004L,
                    deckId = "it_starter",
                    languageCode = "it",
                    frontWord = "Amore",
                    phonetic = "/aˈmo.re/",
                    partOfSpeech = "noun",
                    frontExample = "L'amore per le lingue apre molte porte.",
                    backMeaning = "Tình yêu, niềm đam mê sâu sắc",
                    backExampleTranslation = "Tình yêu ngôn ngữ mở ra rất nhiều cánh cửa cơ hội.",
                    memoryTip = "Từ biểu tượng cho sự lãng mạn nước Ý."
                ),
                FlashCardEntity(
                    id = 80005L,
                    deckId = "it_starter",
                    languageCode = "it",
                    frontWord = "Buono",
                    phonetic = "/ˈbwɔ.no/",
                    partOfSpeech = "adjective",
                    frontExample = "Questo gelato al pistacchio è davvero buono!",
                    backMeaning = "Ngon miệng, tốt lành",
                    backExampleTranslation = "Món kem gelato hạt dẻ cười này ngon thật đấy!",
                    memoryTip = "Dùng cho món ăn ngon hoặc người tốt bụng."
                )
            )

            AppLanguage.PORTUGUESE -> listOf(
                FlashCardEntity(
                    id = 90001L,
                    deckId = "pt_starter",
                    languageCode = "pt",
                    frontWord = "Olá",
                    phonetic = "/oˈla/",
                    partOfSpeech = "phrase",
                    frontExample = "Olá! Como você está?",
                    backMeaning = "Xin chào bạn!",
                    backExampleTranslation = "Xin chào! Bạn thế nào rồi?",
                    memoryTip = "Lời chào ấm áp tại Bồ Đào Nha và Brazil."
                ),
                FlashCardEntity(
                    id = 90002L,
                    deckId = "pt_starter",
                    languageCode = "pt",
                    frontWord = "Obrigado",
                    phonetic = "/o.bɾiˈɡa.du/",
                    partOfSpeech = "phrase",
                    frontExample = "Muito obrigado pela sua grande ajuda.",
                    backMeaning = "Cảm ơn bạn rất nhiều",
                    backExampleTranslation = "Cảm ơn bạn rất nhiều vì sự giúp đỡ to lớn.",
                    memoryTip = "Obrigado (nam nói), Obrigada (nữ nói)."
                ),
                FlashCardEntity(
                    id = 90003L,
                    deckId = "pt_starter",
                    languageCode = "pt",
                    frontWord = "Amigo",
                    phonetic = "/ɐˈmi.ɡu/",
                    partOfSpeech = "noun",
                    frontExample = "É ótimo ter um amigo de verdade.",
                    backMeaning = "Người bạn / Bằng hữu chân thành",
                    backExampleTranslation = "Thật tuyệt vời khi có một người bạn đích thực.",
                    memoryTip = "Tình bạn thắm thiết."
                ),
                FlashCardEntity(
                    id = 90004L,
                    deckId = "pt_starter",
                    languageCode = "pt",
                    frontWord = "Maravilhoso",
                    phonetic = "/mɐ.ɾɐ.viˈʎo.zu/",
                    partOfSpeech = "adjective",
                    frontExample = "O pôr do sol na praia foi maravilhoso.",
                    backMeaning = "Kỳ diệu, tuyệt diệu, tráng lệ",
                    backExampleTranslation = "Hoàng hôn trên bãi biển thật là kỳ diệu.",
                    memoryTip = "Đồng nghĩa với Marvelous."
                ),
                FlashCardEntity(
                    id = 90005L,
                    deckId = "pt_starter",
                    languageCode = "pt",
                    frontWord = "Bom dia",
                    phonetic = "/bõ ˈdʒi.ɐ/",
                    partOfSpeech = "phrase",
                    frontExample = "Bom dia! Tenha um dia muito produtivo.",
                    backMeaning = "Chào buổi sáng tốt lành!",
                    backExampleTranslation = "Chào buổi sáng! Chúc bạn một ngày học tập thật năng suất.",
                    memoryTip = "Bom (Tốt) + Dia (Ngày)."
                )
            )

            AppLanguage.VIETNAMESE -> listOf(
                FlashCardEntity(
                    id = 100001L,
                    deckId = "vi_starter",
                    languageCode = "vi",
                    frontWord = "Xin chào",
                    phonetic = "Xin chào",
                    partOfSpeech = "phrase",
                    frontExample = "Xin chào tất cả các bạn đã đến đây!",
                    backMeaning = "Lời chào thân ái truyền thống của người Việt",
                    backExampleTranslation = "Lời chào lịch sự trong mọi hoàn cảnh.",
                    memoryTip = "Xin + Chào: Lời chào mở đầu chân thành."
                ),
                FlashCardEntity(
                    id = 100002L,
                    deckId = "vi_starter",
                    languageCode = "vi",
                    frontWord = "Cảm ơn",
                    phonetic = "Cảm ơn",
                    partOfSpeech = "phrase",
                    frontExample = "Cảm ơn bạn đã luôn đồng hành và ủng hộ tôi.",
                    backMeaning = "Bày tỏ lòng biết ơn chân thành",
                    backExampleTranslation = "Lời cảm ơn ấm áp từ trái tim.",
                    memoryTip = "Cảm (Cảm xúc) + Ơn (Ân tình nghĩa cử)."
                ),
                FlashCardEntity(
                    id = 100003L,
                    deckId = "vi_starter",
                    languageCode = "vi",
                    frontWord = "Tuyệt vời",
                    phonetic = "Tuyệt vời",
                    partOfSpeech = "adjective",
                    frontExample = "Bạn đã có một phần thể hiện xuất sắc và tuyệt vời!",
                    backMeaning = "Rất tốt, rất đáng khen ngợi, hoàn hảo",
                    backExampleTranslation = "Khen ngợi thành tích hoặc vẻ đẹp xuất sắc.",
                    memoryTip = "Tuyệt đỉnh + Vời vợi."
                ),
                FlashCardEntity(
                    id = 100004L,
                    deckId = "vi_starter",
                    languageCode = "vi",
                    frontWord = "Hạnh phúc",
                    phonetic = "Hạnh phúc",
                    partOfSpeech = "adjective",
                    frontExample = "Mỗi ngày tích lũy thêm kiến thức là một ngày hạnh phúc.",
                    backMeaning = "Cảm giác vui vẻ, mãn nguyện và an lành",
                    backExampleTranslation = "Trạng thái tinh thần hân hoan trọn vẹn.",
                    memoryTip = "Hạnh (Phúc hạnh) + Phúc (May mắn an lành)."
                ),
                FlashCardEntity(
                    id = 100005L,
                    deckId = "vi_starter",
                    languageCode = "vi",
                    frontWord = "Bạn bè",
                    phonetic = "Bạn bè",
                    partOfSpeech = "noun",
                    frontExample = "Chúng ta là những người bạn bè tốt luôn giúp đỡ nhau.",
                    backMeaning = "Những người đồng hành thân thiết, sẻ chia",
                    backExampleTranslation = "Mối quan hệ bạn bè gắn bó tri kỷ.",
                    memoryTip = "Bạn bè kề vai sát cánh cùng tiến bộ."
                )
            )
        }
    }
}
