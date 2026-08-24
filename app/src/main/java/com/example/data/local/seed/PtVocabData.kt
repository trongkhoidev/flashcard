package com.example.data.local.seed

import com.example.data.model.FlashCardEntity

object PtVocabData {
    fun getCards(): List<FlashCardEntity> = listOf(
        c("Olá","/oˈla/","phrase","Olá, tudo bem?","Xin chào","Xin chào, mọi thứ ổn chứ?","Lời chào"),
        c("Adeus","/aˈdews/","phrase","Adeus, até amanhã.","Tạm biệt","Tạm biệt, hẹn ngày mai.","Tạm biệt"),
        c("Obrigado","/o.bɾiˈɡa.du/","phrase","Muito obrigado pela ajuda.","Cảm ơn (Nam nói)","Cảm ơn rất nhiều vì sự giúp đỡ.","Nữ nói: Obrigada"),
        c("Por favor","/puɾ fɐˈvoɾ/","phrase","Um café, por favor.","Xin vui lòng","Một ly cà phê, xin vui lòng.","Lịch sự"),
        c("Sim","/sĩ/","adverb","Sim, eu concordo.","Vâng / Có","Vâng, tôi đồng ý.","Đồng ý"),
        c("Não","/nɐ̃w/","adverb","Não, eu não quero.","Không","Không, tôi không muốn.","Từ chối"),
        c("Água","/ˈa.ɡwɐ/","noun","Preciso beber água.","Nước","Tôi cần uống nước.","Đồ uống"),
        c("Comida","/kuˈmi.dɐ/","noun","A comida está deliciosa.","Thức ăn","Thức ăn rất ngon.","Thức ăn"),
        c("Casa","/ˈka.zɐ/","noun","Minha casa é perto daqui.","Ngôi nhà","Nhà tôi ở gần đây.","Nơi ở"),
        c("Família","/fɐˈmi.li.ɐ/","noun","Eu amo minha família.","Gia đình","Tôi yêu gia đình mình.","Gia đình"),
        c("Amigo","/aˈmi.ɡu/","noun","Ele é um bom amigo.","Bạn bè (Nam)","Anh ấy là một người bạn tốt.","Bạn bè"),
        c("Escola","/isˈkɔ.lɐ/","noun","As crianças estão na escola.","Trường học","Những đứa trẻ đang ở trường.","Trường học"),
        c("Livro","/ˈli.vɾu/","noun","Este livro é muito bom.","Quyển sách","Quyển sách này rất hay.","Sách"),
        c("Tempo","/ˈtẽ.pu/","noun","O tempo está ensolarado hoje.","Thời gian / Thời tiết","Hôm nay trời nắng.","Thời gian/Thời tiết"),
        c("Dia","/ˈdʒi.ɐ/","noun","Bom dia!","Ngày","Chào buổi sáng!","Ngày"),
        c("Noite","/ˈnoj.tʃi/","noun","Boa noite.","Đêm","Chúc ngủ ngon.","Ban đêm"),
        c("Manhã","/mɐˈɲɐ̃/","noun","Eu acordo cedo de manhã.","Buổi sáng","Tôi thức dậy sớm vào buổi sáng.","Buổi sáng"),
        c("Amor","/aˈmoɾ/","noun","O amor é lindo.","Tình yêu","Tình yêu thật đẹp.","Tình cảm"),
        c("Feliz","/fiˈlis/","adjective","Estou muito feliz hoje.","Hạnh phúc / Vui vẻ","Hôm nay tôi rất hạnh phúc.","Cảm xúc"),
        c("Triste","/ˈtɾis.tʃi/","adjective","Ela parece triste.","Buồn bã","Cô ấy trông có vẻ buồn.","Cảm xúc"),
        c("Grande","/ˈɡɾɐ̃.dʒi/","adjective","A cidade é grande.","Lớn / To","Thành phố này lớn.","Kích thước"),
        c("Pequeno","/piˈke.nu/","adjective","O cachorro é pequeno.","Nhỏ / Bé","Con chó thì nhỏ.","Kích thước"),
        c("Bom","/bõ/","adjective","Este bolo é muito bom.","Tốt / Ngon","Cái bánh này rất ngon.","Đánh giá"),
        c("Ruim","/ʁuˈĩ/","adjective","O filme foi ruim.","Xấu / Tệ","Bộ phim thật tệ.","Đánh giá"),
        c("Novo","/ˈno.vu/","adjective","Tenho um carro novo.","Mới","Tôi có một chiếc ô tô mới.","Trạng thái"),
        c("Bonito","/boˈni.tu/","adjective","O vestido é bonito.","Đẹp","Chiếc váy thì đẹp.","Thẩm mỹ"),
        c("Trabalhar","/tɾa.baˈʎaɾ/","verb","Eu tenho que trabalhar amanhã.","Làm việc","Tôi phải làm việc vào ngày mai.","Hành động"),
        c("Estudar","/is.tuˈdaɾ/","verb","Estudo português todos os dias.","Học","Tôi học tiếng Bồ Đào Nha mỗi ngày.","Hành động"),
        c("Comer","/koˈmeɾ/","verb","Vamos comer agora.","Ăn","Chúng ta ăn bây giờ nhé.","Hành động"),
        c("Dormir","/doɾˈmiɾ/","verb","Preciso dormir oito horas.","Ngủ","Tôi cần ngủ tám tiếng.","Hành động"),
        c("Falar","/faˈlaɾ/","verb","Você fala inglês?","Nói","Bạn có nói tiếng Anh không?","Hành động"),
        c("Entender","/ẽ.tẽˈdeɾ/","verb","Eu não entendo.","Hiểu","Tôi không hiểu.","Hành động"),
        c("Comprar","/kõˈpɾaɾ/","verb","Eu quero comprar roupas.","Mua","Tôi muốn mua quần áo.","Hành động"),
        c("Dinheiro","/dʒiˈɲej.ɾu/","noun","Eu não tenho dinheiro.","Tiền","Tôi không có tiền.","Tài chính"),
        c("Hoje","/ˈo.ʒi/","adverb","Hoje faz calor.","Hôm nay","Hôm nay trời nóng.","Thời gian"),
        c("Sempre","/ˈsẽ.pɾi/","adverb","Eu sempre estudo à noite.","Luôn luôn","Tôi luôn học vào buổi tối.","Tần suất"),
        c("Nunca","/ˈnũ.kɐ/","adverb","Nunca fui à França.","Não bao giờ","Tôi chưa bao giờ đến Pháp.","Tần suất"),
        c("Rápido","/ˈʁa.pi.du/","adjective","O gato corre rápido.","Nhanh","Con mèo chạy nhanh.","Tốc độ"),
        c("Lento","/ˈlẽ.tu/","adjective","O trânsito está lento.","Chậm","Giao thông chậm chạp.","Tốc độ"),
        c("Quente","/ˈkẽ.tʃi/","adjective","A água está muito quente.","Nóng","Nước rất nóng.","Nhiệt độ")
    )
    private fun c(w:String,p:String,pos:String,ex:String,m:String,tr:String,tip:String) =
        FlashCardEntity(deckId="pt_basics",languageCode="pt",frontWord=w,phonetic=p,partOfSpeech=pos,frontExample=ex,backMeaning=m,backExampleTranslation=tr,memoryTip=tip)
}
