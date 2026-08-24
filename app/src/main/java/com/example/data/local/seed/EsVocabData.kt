package com.example.data.local.seed

import com.example.data.model.FlashCardEntity

object EsVocabData {
    fun getCards(): List<FlashCardEntity> = listOf(
        c("Hola","/ˈo.la/","phrase","¡Hola! ¿Cómo estás?","Xin chào","Xin chào! Bạn khỏe không?","Lời chào TBN phổ biến nhất"),
        c("Adiós","/a.ˈðjos/","phrase","Adiós, hasta mañana.","Tạm biệt","Tạm biệt, hẹn gặp lại ngày mai.","A + Diós = Giao phó cho Chúa"),
        c("Gracias","/ˈɡɾa.θjas/","phrase","Muchas gracias por tu ayuda.","Cảm ơn","Cảm ơn rất nhiều vì sự giúp đỡ.","Gracia = Ân huệ"),
        c("Por favor","/poɾ fa.ˈβoɾ/","phrase","Por favor, siéntese.","Xin vui lòng","Xin hãy ngồi xuống.","Từ lịch sự khi yêu cầu"),
        c("Sí","/si/","adverb","Sí, estoy de acuerdo.","Vâng / Đúng vậy","Vâng, tôi đồng ý.","Khẳng định"),
        c("No","/no/","adverb","No, no quiero eso.","Không","Không, tôi không muốn điều đó.","Phủ định"),
        c("Agua","/ˈa.ɣwa/","noun","¿Puedo tener un vaso de agua?","Nước","Cho tôi xin một ly nước?","Nhu cầu cơ bản"),
        c("Comida","/ko.ˈmi.ða/","noun","La comida está deliciosa.","Thức ăn","Thức ăn rất ngon.","Comer = Ăn → Comida"),
        c("Casa","/ˈka.sa/","noun","Vivo en una casa grande.","Ngôi nhà","Tôi sống trong ngôi nhà lớn.","Nơi ở"),
        c("Familia","/fa.ˈmi.lja/","noun","Mi familia es muy unida.","Gia đình","Gia đình tôi rất gắn kết.","Familiar → Familia"),
        c("Amigo","/a.ˈmi.ɣo/","noun","Él es mi mejor amigo.","Bạn bè","Anh ấy là bạn thân nhất.","Amigo = Bạn"),
        c("Escuela","/es.ˈkwe.la/","noun","Voy a la escuela cada día.","Trường học","Tôi đi học mỗi ngày.","Nơi học tập"),
        c("Libro","/ˈli.βɾo/","noun","Estoy leyendo un buen libro.","Sách","Tôi đang đọc cuốn sách hay.","Librería = Thư viện"),
        c("Tiempo","/ˈtjem.po/","noun","¿Qué hora es?","Thời gian","Mấy giờ rồi?","Khái niệm cơ bản"),
        c("Día","/ˈdi.a/","noun","¡Buen día!","Ngày","Chúc một ngày tốt lành!","Đơn vị thời gian"),
        c("Noche","/ˈno.tʃe/","noun","Buenas noches.","Đêm","Chúc ngủ ngon.","Thời gian nghỉ ngơi"),
        c("Mañana","/ma.ˈɲa.na/","noun","Buenos días, mañana.","Buổi sáng / Ngày mai","Chào buổi sáng.","Đa nghĩa: sáng + ngày mai"),
        c("Amor","/a.ˈmoɾ/","noun","El amor es importante.","Tình yêu","Tình yêu rất quan trọng.","Cảm xúc mạnh mẽ"),
        c("Feliz","/fe.ˈliθ/","adjective","Estoy muy feliz hoy.","Vui vẻ / Hạnh phúc","Hôm nay tôi rất vui.","Felicidad = Hạnh phúc"),
        c("Triste","/ˈtɾis.te/","adjective","No estés triste.","Buồn bã","Đừng buồn.","Trạng thái cảm xúc"),
        c("Grande","/ˈɡɾan.de/","adjective","Esta es una ciudad grande.","Lớn / To","Đây là thành phố lớn.","Grand → Grande"),
        c("Pequeño","/pe.ˈke.ɲo/","adjective","Tiene un perro pequeño.","Nhỏ / Bé","Cô ấy có chú chó nhỏ.","Kích thước nhỏ"),
        c("Bueno","/ˈbwe.no/","adjective","Eso es una buena idea.","Tốt / Hay","Đó là ý tưởng hay.","Bon → Bueno"),
        c("Malo","/ˈma.lo/","adjective","El tiempo está malo hoy.","Xấu / Tệ","Thời tiết hôm nay xấu.","Trái nghĩa: Bueno"),
        c("Nuevo","/ˈnwe.βo/","adjective","Tengo un teléfono nuevo.","Mới","Tôi có điện thoại mới.","Nuevo = New"),
        c("Bonito","/bo.ˈni.to/","adjective","¡Qué bonita puesta de sol!","Đẹp / Xinh","Hoàng hôn đẹp quá!","Mô tả vẻ đẹp"),
        c("Trabajar","/tɾa.βa.ˈxaɾ/","verb","Trabajo desde casa.","Làm việc","Tôi làm việc tại nhà.","Trabajo = Công việc"),
        c("Estudiar","/es.tu.ˈðjaɾ/","verb","Estudio español cada día.","Học","Tôi học tiếng Tây Ban Nha mỗi ngày.","Estudio = Học"),
        c("Comer","/ko.ˈmeɾ/","verb","Vamos a comer juntos.","Ăn","Cùng ăn nhé.","Comida → Comer"),
        c("Dormir","/doɾ.ˈmiɾ/","verb","Necesito dormir temprano.","Ngủ","Tôi cần ngủ sớm.","Dormitorio = Phòng ngủ"),
        c("Hablar","/a.ˈβlaɾ/","verb","¿Hablas español?","Nói","Bạn nói tiếng Tây Ban Nha không?","Giao tiếp"),
        c("Entender","/en.ten.ˈdeɾ/","verb","No entiendo esa palabra.","Hiểu","Tôi không hiểu từ đó.","Understand → Entender"),
        c("Comprar","/kom.ˈpɾaɾ/","verb","Quiero comprar un regalo.","Mua","Tôi muốn mua một món quà.","Compras = Mua sắm"),
        c("Dinero","/di.ˈne.ɾo/","noun","Ahorra tu dinero.","Tiền","Hãy tiết kiệm tiền.","Phương tiện trao đổi"),
        c("Hoy","/oj/","adverb","Hoy es un gran día.","Hôm nay","Hôm nay là ngày tuyệt vời.","Thời gian hiện tại"),
        c("Siempre","/ˈsjem.pɾe/","adverb","Siempre hago mi mejor esfuerzo.","Luôn luôn","Tôi luôn cố gắng hết sức.","Tần suất cao nhất"),
        c("Nunca","/ˈnuŋ.ka/","adverb","Nunca te rindas.","Không bao giờ","Đừng bao giờ bỏ cuộc.","Tần suất thấp nhất"),
        c("Rápido","/ˈra.pi.ðo/","adjective","El tren es muy rápido.","Nhanh","Tàu rất nhanh.","Rapid → Rápido"),
        c("Lento","/ˈlen.to/","adjective","Habla más lento, por favor.","Chậm","Nói chậm hơn xin vui lòng.","Trái nghĩa: Rápido"),
        c("Caliente","/ka.ˈljen.te/","adjective","Hace mucho calor hoy.","Nóng","Hôm nay rất nóng.","Nhiệt độ cao")
    )
    private fun c(w:String,p:String,pos:String,ex:String,m:String,tr:String,tip:String) =
        FlashCardEntity(deckId="es_basics",languageCode="es",frontWord=w,phonetic=p,partOfSpeech=pos,frontExample=ex,backMeaning=m,backExampleTranslation=tr,memoryTip=tip)
}
