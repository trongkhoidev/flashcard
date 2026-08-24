package com.example.data.local.seed

import com.example.data.model.FlashCardEntity

object ItVocabData {
    fun getCards(): List<FlashCardEntity> = listOf(
        c("Ciao","/tʃao/","phrase","Ciao, come stai?","Xin chào / Tạm biệt","Xin chào, bạn khỏe không?","Dùng để chào và tạm biệt"),
        c("Arrivederci","/ar.ri.veˈder.tʃi/","phrase","Arrivederci, a presto!","Tạm biệt","Tạm biệt, hẹn gặp lại sớm!","Lời chào trang trọng hơn"),
        c("Grazie","/ˈɡrat.tsje/","phrase","Grazie mille per l'aiuto.","Cảm ơn","Cảm ơn rất nhiều vì sự giúp đỡ.","Cảm ơn"),
        c("Per favore","/per faˈvo.re/","phrase","Un caffè, per favore.","Xin vui lòng","Cho tôi một cà phê, xin vui lòng.","Lịch sự"),
        c("Sì","/si/","adverb","Sì, sono d'accordo.","Vâng / Có","Vâng, tôi đồng ý.","Đồng ý"),
        c("No","/nɔ/","adverb","No, non lo voglio.","Không","Không, tôi không muốn nó.","Từ chối"),
        c("Acqua","/ˈak.kwa/","noun","Vorrei un bicchiere d'acqua.","Nước","Tôi muốn một cốc nước.","Đồ uống"),
        c("Cibo","/ˈtʃi.bo/","noun","Il cibo italiano è buonissimo.","Thức ăn","Đồ ăn Ý rất ngon.","Thức ăn"),
        c("Casa","/ˈka.za/","noun","La mia casa è piccola.","Ngôi nhà","Nhà tôi thì nhỏ.","Nơi ở"),
        c("Famiglia","/faˈmiʎ.ʎa/","noun","La famiglia è tutto.","Gia đình","Gia đình là tất cả.","Gia đình"),
        c("Amico","/aˈmi.ko/","noun","Lui è un mio caro amico.","Bạn bè (Nam)","Anh ấy là một người bạn thân của tôi.","Bạn bè"),
        c("Scuola","/ˈskwɔ.la/","noun","Vado a scuola a piedi.","Trường học","Tôi đi bộ đến trường.","Trường học"),
        c("Libro","/ˈli.bro/","noun","Questo libro è interessante.","Quyển sách","Quyển sách này thú vị.","Sách"),
        c("Tempo","/ˈtɛm.po/","noun","Che tempo fa oggi?","Thời tiết / Thời gian","Hôm nay thời tiết thế nào? / Có thời gian không?","Thời gian / Thời tiết"),
        c("Giorno","/ˈdʒor.no/","noun","Buongiorno a tutti!","Ngày","Chào buổi sáng mọi người!","Ngày"),
        c("Notte","/ˈnɔt.te/","noun","Buonanotte, sogni d'oro.","Đêm","Chúc ngủ ngon, mơ đẹp nhé.","Ban đêm"),
        c("Mattina","/matˈti.na/","noun","La mattina bevo sempre il caffè.","Buổi sáng","Buổi sáng tôi luôn uống cà phê.","Buổi sáng"),
        c("Amore","/aˈmo.re/","noun","L'amore vince su tutto.","Tình yêu","Tình yêu chiến thắng tất cả.","Tình cảm"),
        c("Felice","/feˈli.tʃe/","adjective","Sono molto felice di vederti.","Hạnh phúc / Vui vẻ","Tôi rất vui được gặp bạn.","Cảm xúc"),
        c("Triste","/ˈtris.te/","adjective","Perché sei così triste?","Buồn bã","Sao bạn buồn thế?","Cảm xúc"),
        c("Grande","/ˈɡran.de/","adjective","Roma è una città grande.","Lớn / To","Rome là một thành phố lớn.","Kích thước"),
        c("Piccolo","/ˈpik.ko.lo/","adjective","Ho un cane piccolo.","Nhỏ / Bé","Tôi có một chú chó nhỏ.","Kích thước"),
        c("Buono","/ˈbwɔ.no/","adjective","Questo gelato è molto buono.","Tốt / Ngon","Món kem này rất ngon.","Đánh giá"),
        c("Cattivo","/katˈti.vo/","adjective","Il tempo è cattivo oggi.","Xấu / Tệ","Thời tiết hôm nay rất tệ.","Đánh giá"),
        c("Nuovo","/ˈnwɔ.vo/","adjective","Ho comprato un vestito nuovo.","Mới","Tôi đã mua một chiếc váy mới.","Trạng thái"),
        c("Bello","/ˈbɛl.lo/","adjective","Che bel panorama!","Đẹp","Phong cảnh đẹp quá!","Thẩm mỹ"),
        c("Lavorare","/la.voˈra.re/","verb","Lavoro in un ufficio.","Làm việc","Tôi làm việc trong văn phòng.","Hành động"),
        c("Studiare","/stuˈdja.re/","verb","Studio l'italiano all'università.","Học","Tôi học tiếng Ý ở trường đại học.","Hành động"),
        c("Mangiare","/manˈdʒa.re/","verb","Mangiamo una pizza?","Ăn","Chúng ta ăn pizza nhé?","Hành động"),
        c("Dormire","/dorˈmi.re/","verb","I bambini devono dormire.","Ngủ","Những đứa trẻ cần phải đi ngủ.","Hành động"),
        c("Parlare","/parˈla.re/","verb","Parli inglese?","Nói","Bạn có nói tiếng Anh không?","Hành động"),
        c("Capire","/kaˈpi.re/","verb","Non capisco, puoi ripetere?","Hiểu","Tôi không hiểu, bạn lặp lại được không?","Hành động"),
        c("Comprare","/komˈpra.re/","verb","Voglio comprare una macchina.","Mua","Tôi muốn mua một chiếc ô tô.","Hành động"),
        c("Soldi","/ˈsɔl.di/","noun","Non ho soldi con me.","Tiền","Tôi không mang theo tiền.","Tài chính"),
        c("Oggi","/ˈɔd.dʒi/","adverb","Oggi è il mio compleanno.","Hôm nay","Hôm nay là sinh nhật của tôi.","Thời gian"),
        c("Sempre","/ˈsɛm.pre/","adverb","Sei sempre in ritardo!","Luôn luôn","Bạn luôn đi trễ!","Tần suất"),
        c("Mai","/ma.i/","adverb","Non dico mai bugie.","Không bao giờ","Tôi không bao giờ nói dối.","Tần suất"),
        c("Veloce","/veˈlo.tʃe/","adjective","Il treno è molto veloce.","Nhanh","Chuyến tàu rất nhanh.","Tốc độ"),
        c("Lento","/ˈlɛn.to/","adjective","Cammina troppo lento.","Chậm","Anh ấy đi bộ quá chậm.","Tốc độ"),
        c("Caldo","/ˈkal.do/","adjective","L'estate in Italia è molto calda.","Nóng","Mùa hè ở Ý rất nóng.","Nhiệt độ")
    )
    private fun c(w:String,p:String,pos:String,ex:String,m:String,tr:String,tip:String) =
        FlashCardEntity(deckId="it_basics",languageCode="it",frontWord=w,phonetic=p,partOfSpeech=pos,frontExample=ex,backMeaning=m,backExampleTranslation=tr,memoryTip=tip)
}
