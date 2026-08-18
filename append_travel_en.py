import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_travel_beginner", "languageCode": "en", "title": "Du lịch", "subtitle": "Du lịch - Beginner", "iconEmoji": "✈️", "level": "Beginner", "colorHex": "#0EA5E9", "cardCount": 30},
    {"id": "en_travel_intermediate", "languageCode": "en", "title": "Du lịch", "subtitle": "Du lịch - Intermediate", "iconEmoji": "✈️", "level": "Intermediate", "colorHex": "#0284C7", "cardCount": 30},
    {"id": "en_travel_advanced", "languageCode": "en", "title": "Du lịch", "subtitle": "Du lịch - Advanced", "iconEmoji": "✈️", "level": "Advanced", "colorHex": "#0369A1", "cardCount": 30},
    {"id": "en_travel_expert", "languageCode": "en", "title": "Du lịch", "subtitle": "Du lịch - Expert", "iconEmoji": "✈️", "level": "Expert", "colorHex": "#075985", "cardCount": 30}
]

vocab_data = [
    # Beginner (0)
    ("Travel", "/ˈtræv.əl/", "verb", "I love to travel.", "Du lịch", "Tôi thích đi du lịch.", "Đi xa", 0),
    ("Trip", "/trɪp/", "noun", "A short trip.", "Chuyến đi", "Một chuyến đi ngắn.", "Đi chơi", 0),
    ("Tour", "/tʊər/", "noun", "A city tour.", "Chuyến tham quan", "Một chuyến tham quan thành phố.", "Đi xem cảnh", 0),
    ("Ticket", "/ˈtɪk.ɪt/", "noun", "Buy a ticket.", "Vé", "Mua một tấm vé.", "Giấy chứng nhận trả tiền", 0),
    ("Bag", "/bæɡ/", "noun", "Carry your bag.", "Túi xách", "Mang theo túi xách của bạn.", "Túi đựng đồ", 0),
    ("Map", "/mæp/", "noun", "Look at the map.", "Bản đồ", "Nhìn vào bản đồ.", "Chỉ đường", 0),
    ("Guide", "/ɡaɪd/", "noun", "A tour guide.", "Người hướng dẫn", "Một hướng dẫn viên du lịch.", "Người chỉ đường", 0),
    ("Hotel", "/həʊˈtel/", "noun", "Stay in a hotel.", "Khách sạn", "Ở trong một khách sạn.", "Nơi nghỉ ngơi", 0),
    ("Room", "/ruːm/", "noun", "A nice room.", "Phòng", "Một căn phòng đẹp.", "Chỗ ngủ", 0),
    ("Bed", "/bed/", "noun", "Sleep in a bed.", "Giường", "Ngủ trên một chiếc giường.", "Đồ để nằm", 0),
    ("Plane", "/pleɪn/", "noun", "Fly by plane.", "Máy bay", "Đi bằng máy bay.", "Bay trên trời", 0),
    ("Train", "/treɪn/", "noun", "Travel by train.", "Tàu hỏa", "Đi du lịch bằng tàu hỏa.", "Chạy trên đường ray", 0),
    ("Bus", "/bʌs/", "noun", "Catch the bus.", "Xe buýt", "Bắt xe buýt.", "Xe chở nhiều người", 0),
    ("Car", "/kɑːr/", "noun", "Rent a car.", "Xe hơi", "Thuê một chiếc xe hơi.", "Ô tô", 0),
    ("Boat", "/bəʊt/", "noun", "Sail a boat.", "Thuyền", "Đi một chiếc thuyền.", "Tàu nhỏ", 0),
    ("Ship", "/ʃɪp/", "noun", "A big ship.", "Tàu thủy", "Một chiếc tàu thủy lớn.", "Tàu to trên biển", 0),
    ("Bike", "/baɪk/", "noun", "Ride a bike.", "Xe đạp", "Đạp xe.", "Xe hai bánh", 0),
    ("Taxi", "/ˈtæk.si/", "noun", "Call a taxi.", "Xe tắc-xi", "Gọi một chiếc tắc-xi.", "Xe chở khách thuê", 0),
    ("Fly", "/flaɪ/", "verb", "Fly to Paris.", "Bay", "Bay đến Paris.", "Di chuyển trên không", 0),
    ("Drive", "/draɪv/", "verb", "Drive a car.", "Lái xe", "Lái một chiếc xe hơi.", "Điều khiển xe", 0),
    ("Walk", "/wɔːk/", "verb", "Walk to the park.", "Đi bộ", "Đi bộ đến công viên.", "Đi bằng chân", 0),
    ("Go", "/ɡəʊ/", "verb", "Go on holiday.", "Đi", "Đi nghỉ mát.", "Di chuyển", 0),
    ("Visit", "/ˈvɪz.ɪt/", "verb", "Visit a museum.", "Đến thăm", "Đến thăm một viện bảo tàng.", "Ghé chơi", 0),
    ("Stay", "/steɪ/", "verb", "Stay here.", "Ở lại", "Ở lại đây.", "Không rời đi", 0),
    ("See", "/siː/", "verb", "See the view.", "Ngắm nhìn", "Ngắm nhìn khung cảnh.", "Nhìn", 0),
    ("Photo", "/ˈfəʊ.təʊ/", "noun", "Take a photo.", "Bức ảnh", "Chụp một bức ảnh.", "Hình", 0),
    ("Sea", "/siː/", "noun", "Swim in the sea.", "Biển", "Bơi trên biển.", "Nước mặn", 0),
    ("Sun", "/sʌn/", "noun", "Lie in the sun.", "Mặt trời", "Nằm phơi nắng.", "Ánh nắng", 0),
    ("Beach", "/biːtʃ/", "noun", "Go to the beach.", "Bãi biển", "Đi ra bãi biển.", "Bãi cát", 0),
    ("Mountain", "/ˈmaʊn.tɪn/", "noun", "Climb a mountain.", "Ngọn núi", "Leo một ngọn núi.", "Nơi cao", 0),

    # Intermediate (1)
    ("Journey", "/ˈdʒɜː.ni/", "noun", "A long journey.", "Hành trình", "Một hành trình dài.", "Chuyến đi xa", 1),
    ("Voyage", "/ˈvɔɪ.ɪdʒ/", "noun", "A sea voyage.", "Chuyến đi biển", "Một chuyến hành trình trên biển.", "Đi biển dài ngày", 1),
    ("Destination", "/ˌdes.tɪˈneɪ.ʃən/", "noun", "Reach your destination.", "Điểm đến", "Đến được điểm đến của bạn.", "Nơi cần đến", 1),
    ("Tourist", "/ˈtʊə.rɪst/", "noun", "A tourist attraction.", "Khách du lịch", "Một điểm thu hút khách du lịch.", "Người đi chơi xa", 1),
    ("Passenger", "/ˈpæs.ən.dʒər/", "noun", "A train passenger.", "Hành khách", "Một hành khách trên tàu.", "Người đi xe", 1),
    ("Baggage", "/ˈbæɡ.ɪdʒ/", "noun", "Baggage claim.", "Hành lý", "Khu vực nhận hành lý.", "Đồ đạc mang theo", 1),
    ("Luggage", "/ˈlʌɡ.ɪdʒ/", "noun", "Heavy luggage.", "Hành lý", "Hành lý nặng.", "Đồ đạc", 1),
    ("Suitcase", "/ˈsuːt.keɪs/", "noun", "Pack your suitcase.", "Va-li", "Sắp xếp va-li của bạn.", "Hộp đựng áo quần", 1),
    ("Backpack", "/ˈbæk.pæk/", "noun", "Carry a backpack.", "Ba-lô", "Mang một chiếc ba-lô.", "Túi đeo lưng", 1),
    ("Passport", "/ˈpɑːs.pɔːt/", "noun", "Show your passport.", "Hộ chiếu", "Xuất trình hộ chiếu của bạn.", "Giấy phép đi nước ngoài", 1),
    ("Visa", "/ˈviː.zə/", "noun", "Apply for a visa.", "Thị thực", "Xin cấp thị thực.", "Giấy cho phép nhập cảnh", 1),
    ("Flight", "/flaɪt/", "noun", "A direct flight.", "Chuyến bay", "Một chuyến bay thẳng.", "Hành trình máy bay", 1),
    ("Airport", "/ˈeə.pɔːt/", "noun", "Arrive at the airport.", "Sân bay", "Đến sân bay.", "Nơi máy bay đỗ", 1),
    ("Station", "/ˈsteɪ.ʃən/", "noun", "A railway station.", "Nhà ga", "Một nhà ga xe lửa.", "Bến tàu", 1),
    ("Platform", "/ˈplæt.fɔːm/", "noun", "Platform 9.", "Sân ga", "Sân ga số 9.", "Nơi chờ tàu", 1),
    ("Booking", "/ˈbʊk.ɪŋ/", "noun", "Make a booking.", "Sự đặt chỗ", "Thực hiện việc đặt chỗ.", "Giữ chỗ trước", 1),
    ("Reservation", "/ˌrez.əˈveɪ.ʃən/", "noun", "Confirm your reservation.", "Sự đặt trước", "Xác nhận đặt chỗ của bạn.", "Giữ phòng", 1),
    ("Reception", "/rɪˈsep.ʃən/", "noun", "Ask at the reception.", "Quầy tiếp tân", "Hỏi tại quầy tiếp tân.", "Nơi đón khách", 1),
    ("Check-in", "/ˈtʃek.ɪn/", "verb", "Check in at the hotel.", "Nhận phòng", "Làm thủ tục nhận phòng khách sạn.", "Đăng ký vào", 1),
    ("Check-out", "/ˈtʃek.aʊt/", "verb", "Check out before noon.", "Trả phòng", "Trả phòng trước buổi trưa.", "Thanh toán rời đi", 1),
    ("Depart", "/dɪˈpɑːt/", "verb", "The train will depart soon.", "Khởi hành", "Tàu sẽ khởi hành sớm.", "Bắt đầu đi", 1),
    ("Arrive", "/əˈraɪv/", "verb", "Arrive on time.", "Đến nơi", "Đến nơi đúng giờ.", "Tới đích", 1),
    ("Board", "/bɔːd/", "verb", "Board the plane.", "Lên máy bay", "Lên máy bay.", "Lên phương tiện", 1),
    ("Delay", "/dɪˈleɪ/", "verb", "The flight is delayed.", "Hoãn lại", "Chuyến bay bị hoãn.", "Lùi giờ", 1),
    ("Cancel", "/ˈkæn.səl/", "verb", "Cancel the trip.", "Hủy bỏ", "Hủy bỏ chuyến đi.", "Không thực hiện", 1),
    ("Sightseeing", "/ˈsaɪtˌsiː.ɪŋ/", "noun", "Go sightseeing.", "Ngắm cảnh", "Đi tham quan ngắm cảnh.", "Xem phong cảnh", 1),
    ("Souvenir", "/ˌsuː.vənˈɪər/", "noun", "Buy a souvenir.", "Quà lưu niệm", "Mua một món quà lưu niệm.", "Đồ kỷ niệm", 1),
    ("Currency", "/ˈkʌr.ən.si/", "noun", "Foreign currency.", "Tiền tệ", "Tiền tệ nước ngoài.", "Đồng tiền", 1),
    ("Customs", "/ˈkʌs.təmz/", "noun", "Go through customs.", "Hải quan", "Đi qua hải quan.", "Khu vực kiểm tra đồ", 1),
    ("Border", "/ˈbɔː.dər/", "noun", "Cross the border.", "Biên giới", "Vượt qua biên giới.", "Ranh giới quốc gia", 1),

    # Advanced (2)
    ("Itinerary", "/aɪˈtɪn.ər.ər.i/", "noun", "Plan an itinerary.", "Lịch trình", "Lên kế hoạch một lịch trình.", "Kế hoạch chuyến đi", 2),
    ("Expedition", "/ˌek.spəˈdɪʃ.ən/", "noun", "An expedition to the pole.", "Cuộc thám hiểm", "Một cuộc thám hiểm đến vùng cực.", "Chuyến đi xa khám phá", 2),
    ("Excursion", "/ɪkˈskɜː.ʃən/", "noun", "A shore excursion.", "Chuyến du ngoạn", "Một chuyến du ngoạn trên bờ.", "Đi chơi ngắn", 2),
    ("Pilgrimage", "/ˈpɪl.ɡrɪ.mɪdʒ/", "noun", "A holy pilgrimage.", "Cuộc hành hương", "Một cuộc hành hương thần thánh.", "Đi viếng thăm", 2),
    ("Transit", "/ˈtræn.zɪt/", "noun", "Passengers in transit.", "Sự quá cảnh", "Hành khách đang quá cảnh.", "Dừng chân giữa đường", 2),
    ("Stopover", "/ˈstɒpˌəʊ.vər/", "noun", "A stopover in Dubai.", "Điểm dừng chân", "Một điểm dừng chân ở Dubai.", "Chờ chuyển bay", 2),
    ("Layover", "/ˈleɪˌəʊ.vər/", "noun", "A long layover.", "Thời gian chờ chuyển chuyến", "Một thời gian chờ đợi dài.", "Đợi máy bay", 2),
    ("Terminal", "/ˈtɜː.mɪ.nəl/", "noun", "Terminal 2.", "Nhà đón khách", "Nhà đón khách số 2.", "Khu vực ở sân bay", 2),
    ("Concierge", "/ˌkɒn.siˈeəʒ/", "noun", "Ask the hotel concierge.", "Nhân viên hỗ trợ", "Hỏi nhân viên hỗ trợ khách sạn.", "Người giúp việc khách sạn", 2),
    ("Accommodation", "/əˌkɒm.əˈdeɪ.ʃən/", "noun", "Book accommodation.", "Chỗ ở", "Đặt chỗ ở.", "Nơi qua đêm", 2),
    ("Amenities", "/əˈmiː.nə.tiz/", "noun", "Hotel amenities.", "Tiện nghi", "Các tiện nghi của khách sạn.", "Đồ dùng tiện lợi", 2),
    ("Boutique hotel", "/buːˈtiːk həʊˈtel/", "noun", "Stay at a boutique hotel.", "Khách sạn nhỏ sang trọng", "Ở tại một khách sạn nhỏ sang trọng.", "Khách sạn đẹp", 2),
    ("Ecotourism", "/ˈiː.kəʊˌtʊə.rɪ.zəm/", "noun", "Promote ecotourism.", "Du lịch sinh thái", "Thúc đẩy du lịch sinh thái.", "Du lịch bảo vệ môi trường", 2),
    ("Backpacking", "/ˈbækˌpæk.ɪŋ/", "noun", "Go backpacking in Asia.", "Du lịch bụi", "Đi du lịch bụi ở châu Á.", "Du lịch tự túc", 2),
    ("Hitchhike", "/ˈhɪtʃ.haɪk/", "verb", "Hitchhike across the country.", "Đi nhờ xe", "Đi nhờ xe xuyên đất nước.", "Vẫy xe đi nhờ", 2),
    ("Navigate", "/ˈnæv.ɪ.ɡeɪt/", "verb", "Navigate the map.", "Định hướng", "Định hướng trên bản đồ.", "Dò đường", 2),
    ("Disembark", "/ˌdɪs.ɪmˈbɑːk/", "verb", "Disembark from the ship.", "Xuống tàu, xe", "Rời khỏi tàu thủy.", "Rời phương tiện", 2),
    ("Commute", "/kəˈmjuːt/", "verb", "Commute to work.", "Đi lại (làm việc)", "Đi lại đều đặn tới chỗ làm.", "Đi làm hằng ngày", 2),
    ("Jet lag", "/ˈdʒet ˌlæɡ/", "noun", "I have jet lag.", "Mệt mỏi do lệch múi giờ", "Tôi bị mệt mỏi do lệch múi giờ.", "Lệch giờ", 2),
    ("Seasickness", "/ˈsiːˌsɪk.nəs/", "noun", "Pills for seasickness.", "Say sóng", "Thuốc chống say sóng.", "Chóng mặt trên thuyền", 2),
    ("Altitude", "/ˈæl.tɪ.tʃuːd/", "noun", "High altitude.", "Độ cao", "Độ cao lớn.", "Mức cao", 2),
    ("Panorama", "/ˌpæn.əˈrɑː.mə/", "noun", "A beautiful panorama.", "Toàn cảnh", "Một toàn cảnh tuyệt đẹp.", "Góc nhìn rộng", 2),
    ("Picturesque", "/ˌpɪk.tʃərˈesk/", "adjective", "A picturesque village.", "Đẹp như tranh", "Một ngôi làng đẹp như tranh vẽ.", "Rất đẹp", 2),
    ("Breathtaking", "/ˈbreθˌteɪ.kɪŋ/", "adjective", "Breathtaking views.", "Đẹp ngoạn mục", "Những khung cảnh ngoạn mục.", "Cảnh tượng hùng vĩ", 2),
    ("Pristine", "/ˈprɪs.tiːn/", "adjective", "A pristine beach.", "Hoang sơ", "Một bãi biển hoang sơ.", "Chưa bị con người phá", 2),
    ("Cosmopolitan", "/ˌkɒz.məˈpɒl.ɪ.tən/", "adjective", "A cosmopolitan city.", "Đa văn hóa", "Một thành phố đa văn hóa.", "Hội tụ nhiều người", 2),
    ("Bustling", "/ˈbʌs.lɪŋ/", "adjective", "A bustling market.", "Nhộn nhịp", "Một khu chợ nhộn nhịp.", "Hối hả", 2),
    ("Off-the-beaten-path", "/ˌɒf.ðəˌbiː.tənˈpɑːθ/", "adjective", "Travel off the beaten path.", "Ít người biết", "Đi du lịch đến nơi vắng vẻ.", "Xa lạ", 2),
    ("Heritage", "/ˈher.ɪ.tɪdʒ/", "noun", "A world heritage site.", "Di sản", "Một khu di sản thế giới.", "Kế thừa văn hóa", 2),
    ("Landmark", "/ˈlænd.mɑːk/", "noun", "A famous landmark.", "Địa danh", "Một địa danh nổi tiếng.", "Nơi dễ nhận biết", 2),

    # Expert (3)
    ("Wayfarer", "/ˈweɪˌfeə.rər/", "noun", "A weary wayfarer.", "Khách bộ hành", "Một khách bộ hành mệt mỏi.", "Người đi bộ xa", 3),
    ("Globetrotter", "/ˈɡləʊbˌtrɒt.ər/", "noun", "A wealthy globetrotter.", "Người đi khắp thế giới", "Một người đi du lịch toàn thế giới giàu có.", "Du lịch muôn nơi", 3),
    ("Nomadic", "/nəʊˈmæd.ɪk/", "adjective", "A nomadic lifestyle.", "Du mục", "Một lối sống du mục.", "Di chuyển liên tục", 3),
    ("Peripatetic", "/ˌper.ɪ.pəˈtet.ɪk/", "adjective", "A peripatetic teacher.", "Lưu động", "Một giáo viên lưu động.", "Hay đi đây đó", 3),
    ("Itinerant", "/aɪˈtɪn.ər.ənt/", "adjective", "An itinerant musician.", "Lưu động", "Một nhạc sĩ lưu động.", "Làm việc di động", 3),
    ("Odyssey", "/ˈɒd.ɪ.si/", "noun", "A spiritual odyssey.", "Chuyến phiêu lưu dài", "Một chuyến phiêu lưu tâm linh.", "Hành trình dài", 3),
    ("Trekking", "/ˈtrek.ɪŋ/", "noun", "Mountain trekking.", "Đi bộ leo núi", "Đi bộ leo núi.", "Đi bộ việt dã", 3),
    ("Mountaineering", "/ˌmaʊn.tɪˈnɪə.rɪŋ/", "noun", "He loves mountaineering.", "Môn leo núi", "Anh ấy yêu thích môn leo núi.", "Chinh phục đỉnh cao", 3),
    ("Speleology", "/ˌspiː.liˈɒl.ə.dʒi/", "noun", "The study of speleology.", "Thám hiểm hang động", "Nghiên cứu thám hiểm hang động.", "Nghiên cứu hang đá", 3),
    ("Agritourism", "/ˈæɡ.rɪˌtʊə.rɪ.zəm/", "noun", "Agritourism is growing.", "Du lịch nông nghiệp", "Du lịch nông nghiệp đang phát triển.", "Trải nghiệm làm nông", 3),
    ("Voluntourism", "/ˌvɒl.ənˈtʊə.rɪ.zəm/", "noun", "Go on voluntourism.", "Du lịch tình nguyện", "Đi du lịch kết hợp làm tình nguyện.", "Từ thiện", 3),
    ("Glamping", "/ˈɡlæm.pɪŋ/", "noun", "Luxury glamping.", "Cắm trại sang chảnh", "Cắm trại sang trọng.", "Lều tiện nghi", 3),
    ("Staycation", "/steɪˈkeɪ.ʃən/", "noun", "A relaxing staycation.", "Kỳ nghỉ tại nhà", "Một kỳ nghỉ thư giãn tại nhà.", "Không đi xa", 3),
    ("Time-share", "/ˈtaɪm.ʃeər/", "noun", "Buy a time-share.", "Sở hữu kỳ nghỉ", "Mua quyền sở hữu kỳ nghỉ.", "Chia sẻ nơi ở", 3),
    ("Purser", "/ˈpɜː.sər/", "noun", "The ship's purser.", "Tiếp viên trưởng", "Tiếp viên trưởng của con tàu.", "Quản lý phục vụ", 3),
    ("Maitre d'", "/ˌmeɪ.trə ˈdiː/", "noun", "Ask the maitre d'.", "Quản lý nhà hàng", "Hãy hỏi quản lý nhà hàng.", "Người xếp chỗ", 3),
    ("Bellhop", "/ˈbel.hɒp/", "noun", "Tip the bellhop.", "Nhân viên xách hành lý", "Cho tiền boa nhân viên xách hành lý.", "Mang đồ", 3),
    ("Emigration", "/ˌem.ɪˈɡreɪ.ʃən/", "noun", "Mass emigration.", "Sự di cư", "Cuộc di cư hàng loạt.", "Rời đi", 3),
    ("Immigration", "/ˌɪm.ɪˈɡreɪ.ʃən/", "noun", "Immigration control.", "Sự nhập cư", "Kiểm soát nhập cư.", "Đến ở nước khác", 3),
    ("Deportation", "/ˌdiː.pɔːˈteɪ.ʃən/", "noun", "Face deportation.", "Sự trục xuất", "Đối mặt với sự trục xuất.", "Bị đuổi khỏi quốc gia", 3),
    ("Repatriation", "/riːˌpæt.riˈeɪ.ʃən/", "noun", "Repatriation of refugees.", "Sự hồi hương", "Sự hồi hương của những người tị nạn.", "Trả về nước", 3),
    ("Extradition", "/ˌek.strəˈdɪʃ.ən/", "noun", "Extradition treaty.", "Sự dẫn độ", "Hiệp ước dẫn độ.", "Giao tội phạm", 3),
    ("Contraband", "/ˈkɒn.trə.bænd/", "noun", "Smuggle contraband.", "Hàng lậu", "Buôn lậu hàng cấm.", "Đồ cấm", 3),
    ("Duty-free", "/ˌdʒuː.tiˈfriː/", "adjective", "Duty-free shop.", "Miễn thuế", "Cửa hàng miễn thuế.", "Không tính thuế", 3),
    ("Quarantine", "/ˈkwɒr.ən.tiːn/", "noun", "Animal quarantine.", "Sự cách ly kiểm dịch", "Kiểm dịch động vật.", "Cách ly y tế", 3),
    ("Assimilation", "/əˌsɪm.ɪˈleɪ.ʃən/", "noun", "Cultural assimilation.", "Sự đồng hóa", "Sự đồng hóa văn hóa.", "Hòa nhập hoàn toàn", 3),
    ("Xenophobia", "/ˌzen.əˈfəʊ.bi.ə/", "noun", "Combat xenophobia.", "Hội chứng bài ngoại", "Chống lại chứng bài ngoại.", "Ghét người nước ngoài", 3),
    ("Gastrotourism", "/ˌɡæs.trəʊˈtʊə.rɪ.zəm/", "noun", "Enjoy gastrotourism.", "Du lịch ẩm thực", "Tận hưởng du lịch ẩm thực.", "Tour ăn uống", 3),
    ("Astrotourism", "/ˌæs.trəʊˈtʊə.rɪ.zəm/", "noun", "Astrotourism in Chile.", "Du lịch ngắm sao", "Du lịch ngắm sao ở Chile.", "Xem thiên văn", 3),
    ("Wanderlust", "/ˈwɒn.də.lʌst/", "noun", "A feeling of wanderlust.", "Khát khao du lịch", "Cảm giác khát khao đi du lịch.", "Thèm đi chơi xa", 3)
]

level_map = ["en_travel_beginner", "en_travel_intermediate", "en_travel_advanced", "en_travel_expert"]

new_flashcards = []
for word, phonetic, pos, ex_en, meaning, ex_vi, tip, lvl in vocab_data:
    new_flashcards.append({
        "deckId": level_map[lvl],
        "languageCode": "en",
        "frontWord": word,
        "phonetic": phonetic,
        "partOfSpeech": pos,
        "frontExample": ex_en,
        "backMeaning": meaning,
        "backExampleTranslation": ex_vi,
        "memoryTip": tip
    })

if os.path.exists(DB_FILE):
    with open(DB_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)
else:
    data = {"decks": [], "flashCards": []}

data["decks"].extend(decks)
data["flashCards"].extend(new_flashcards)

with open(DB_FILE, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print("Successfully appended 120 words for Travel (English) to DB!")
