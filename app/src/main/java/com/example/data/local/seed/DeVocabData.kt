package com.example.data.local.seed

import com.example.data.model.FlashCardEntity

object DeVocabData {
    fun getCards(): List<FlashCardEntity> = listOf(
        c("Hallo","/ˈhalo/","phrase","Hallo, wie geht's?","Xin chào","Xin chào, bạn khỏe không?","Lời chào cơ bản"),
        c("Tschüss","/tʃʏs/","phrase","Tschüss, bis morgen!","Tạm biệt","Tạm biệt, hẹn mai gặp!","Chào tạm biệt thân mật"),
        c("Danke","/ˈdaŋkə/","phrase","Danke für deine Hilfe.","Cảm ơn","Cảm ơn vì sự giúp đỡ của bạn.","Cảm ơn"),
        c("Bitte","/ˈbɪtə/","phrase","Bitte setzen Sie sich.","Xin vui lòng / Không có gì","Xin mời ngồi.","Dùng khi nhờ vả hoặc đáp lại lời cảm ơn"),
        c("Ja","/jaː/","adverb","Ja, ich verstehe.","Vâng / Có","Vâng, tôi hiểu.","Đồng ý"),
        c("Nein","/naɪ̯n/","adverb","Nein, das ist falsch.","Không","Không, điều đó sai rồi.","Từ chối"),
        c("Wasser","/ˈvasɐ/","noun","Ich trinke gern Wasser.","Nước","Tôi thích uống nước.","Đồ uống cơ bản"),
        c("Essen","/ˈɛsn̩/","noun","Das Essen schmeckt sehr gut.","Thức ăn","Thức ăn rất ngon.","Đồ ăn"),
        c("Haus","/haʊ̯s/","noun","Wir wohnen in einem großen Haus.","Ngôi nhà","Chúng tôi sống trong một ngôi nhà lớn.","Nơi ở"),
        c("Familie","/faˈmiːli̯ə/","noun","Meine Familie ist mir wichtig.","Gia đình","Gia đình rất quan trọng với tôi.","Gia đình"),
        c("Freund","/fʁɔʏ̯nt/","noun","Er ist mein bester Freund.","Bạn bè (Nam)","Anh ấy là bạn thân nhất của tôi.","Bạn bè"),
        c("Schule","/ˈʃuːlə/","noun","Kinder gehen in die Schule.","Trường học","Trẻ em đi học.","Nơi học tập"),
        c("Buch","/buːx/","noun","Ich lese ein spannendes Buch.","Quyển sách","Tôi đang đọc một cuốn sách hấp dẫn.","Tài liệu đọc"),
        c("Zeit","/t͡saɪ̯t/","noun","Hast du Zeit für mich?","Thời gian","Bạn có thời gian cho tôi không?","Thời gian"),
        c("Tag","/taːk/","noun","Guten Tag!","Ngày","Chào ngày mới (Chào buổi trưa/chiều)!","Ngày"),
        c("Nacht","/naxt/","noun","Gute Nacht!","Đêm","Chúc ngủ ngon!","Ban đêm"),
        c("Morgen","/ˈmɔʁɡn̩/","noun","Guten Morgen!","Buổi sáng","Chào buổi sáng!","Buổi sáng"),
        c("Liebe","/ˈliːbə/","noun","Liebe ist wunderschön.","Tình yêu","Tình yêu thật đẹp.","Tình cảm"),
        c("Glücklich","/ˈɡlʏklɪç/","adjective","Ich bin heute sehr glücklich.","Hạnh phúc / Vui vẻ","Hôm nay tôi rất vui.","Cảm xúc tích cực"),
        c("Traurig","/ˈtʁaʊ̯ʁɪç/","adjective","Warum bist du so traurig?","Buồn bã","Sao bạn buồn vậy?","Cảm xúc tiêu cực"),
        c("Groß","/ɡʁoːs/","adjective","Das ist ein großes Auto.","Lớn / To","Đây là một chiếc ô tô lớn.","Kích thước"),
        c("Klein","/klaɪ̯n/","adjective","Die Katze ist sehr klein.","Nhỏ / Bé","Con mèo rất nhỏ.","Kích thước"),
        c("Gut","/ɡuːt/","adjective","Das ist eine gute Idee.","Tốt","Đó là một ý tưởng hay.","Tính chất"),
        c("Schlecht","/ʃlɛçt/","adjective","Das Wetter ist heute schlecht.","Xấu / Tệ","Thời tiết hôm nay xấu.","Tính chất"),
        c("Neu","/nɔʏ̯/","adjective","Ich habe ein neues Handy.","Mới","Tôi có điện thoại mới.","Tình trạng"),
        c("Schön","/ʃøːn/","adjective","Das Bild ist sehr schön.","Đẹp","Bức tranh rất đẹp.","Thẩm mỹ"),
        c("Arbeiten","/ˈaʁbaɪ̯tn̩/","verb","Ich arbeite jeden Tag.","Làm việc","Tôi làm việc mỗi ngày.","Hành động"),
        c("Lernen","/ˈlɛʁnən/","verb","Ich lerne Deutsch.","Học","Tôi đang học tiếng Đức.","Hành động"),
        c("Essen (verb)","/ˈɛsn̩/","verb","Wir essen Pizza.","Ăn","Chúng tôi ăn pizza.","Hành động"),
        c("Schlafen","/ˈʃlaːfn̩/","verb","Ich muss jetzt schlafen.","Ngủ","Tôi phải đi ngủ bây giờ.","Hành động"),
        c("Sprechen","/ˈʃpʁɛçn̩/","verb","Sprechen Sie Englisch?","Nói","Bạn có nói tiếng Anh không?","Hành động"),
        c("Verstehen","/fɛɐ̯ˈʃteːən/","verb","Ich verstehe das nicht.","Hiểu","Tôi không hiểu điều đó.","Hành động"),
        c("Kaufen","/ˈkaʊ̯fn̩/","verb","Ich möchte etwas kaufen.","Mua","Tôi muốn mua một thứ gì đó.","Hành động"),
        c("Geld","/ɡɛlt/","noun","Ich habe kein Geld.","Tiền","Tôi không có tiền.","Tài chính"),
        c("Heute","/ˈhɔʏ̯tə/","adverb","Heute ist das Wetter schön.","Hôm nay","Hôm nay thời tiết đẹp.","Thời gian"),
        c("Immer","/ˈɪmɐ/","adverb","Er kommt immer zu spät.","Luôn luôn","Anh ta luôn đến muộn.","Tần suất"),
        c("Nie","/niː/","adverb","Ich trinke nie Kaffee.","Không bao giờ","Tôi không bao giờ uống cà phê.","Tần suất"),
        c("Schnell","/ʃnɛl/","adjective","Das Auto fährt sehr schnell.","Nhanh","Chiếc xe chạy rất nhanh.","Tốc độ"),
        c("Langsam","/ˈlaŋzaːm/","adjective","Bitte sprechen Sie langsam.","Chậm","Xin vui lòng nói chậm.","Tốc độ"),
        c("Heiß","/haɪ̯s/","adjective","Der Tee ist zu heiß.","Nóng","Trà quá nóng.","Nhiệt độ")
    )
    private fun c(w:String,p:String,pos:String,ex:String,m:String,tr:String,tip:String) =
        FlashCardEntity(deckId="de_basics",languageCode="de",frontWord=w,phonetic=p,partOfSpeech=pos,frontExample=ex,backMeaning=m,backExampleTranslation=tr,memoryTip=tip)
}
