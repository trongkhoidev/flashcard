import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_weather_beginner", "languageCode": "en", "title": "Thời tiết", "subtitle": "Thời tiết - Beginner", "iconEmoji": "⛅", "level": "Beginner", "colorHex": "#3B82F6", "cardCount": 30},
    {"id": "en_weather_intermediate", "languageCode": "en", "title": "Thời tiết", "subtitle": "Thời tiết - Intermediate", "iconEmoji": "⛅", "level": "Intermediate", "colorHex": "#2563EB", "cardCount": 30},
    {"id": "en_weather_advanced", "languageCode": "en", "title": "Thời tiết", "subtitle": "Thời tiết - Advanced", "iconEmoji": "⛅", "level": "Advanced", "colorHex": "#1D4ED8", "cardCount": 30},
    {"id": "en_weather_expert", "languageCode": "en", "title": "Thời tiết", "subtitle": "Thời tiết - Expert", "iconEmoji": "⛅", "level": "Expert", "colorHex": "#1E40AF", "cardCount": 30}
]

vocab_data = [
    # Beginner (0)
    ("Sun", "/sʌn/", "noun", "The sun is shining.", "Mặt trời", "Mặt trời đang chiếu sáng.", "Ngôi sao ban ngày", 0),
    ("Rain", "/reɪn/", "noun", "I like the rain.", "Mưa", "Tôi thích mưa.", "Nước từ trên trời rơi xuống", 0),
    ("Cloud", "/klaʊd/", "noun", "Look at that white cloud.", "Đám mây", "Nhìn đám mây trắng kia kìa.", "Hơi nước tụ lại", 0),
    ("Wind", "/wɪnd/", "noun", "The wind is strong.", "Gió", "Gió rất mạnh.", "Không khí di chuyển", 0),
    ("Snow", "/snəʊ/", "noun", "The snow is beautiful.", "Tuyết", "Tuyết thật đẹp.", "Hạt đá trắng rơi", 0),
    ("Sky", "/skaɪ/", "noun", "The sky is blue.", "Bầu trời", "Bầu trời màu xanh.", "Không gian trên cao", 0),
    ("Hot", "/hɒt/", "adjective", "It is very hot today.", "Nóng", "Hôm nay trời rất nóng.", "Nhiệt độ cao", 0),
    ("Cold", "/kəʊld/", "adjective", "It is cold outside.", "Lạnh", "Ngoài trời lạnh.", "Nhiệt độ thấp", 0),
    ("Warm", "/wɔːm/", "adjective", "The weather is warm.", "Ấm áp", "Thời tiết ấm áp.", "Dễ chịu", 0),
    ("Cool", "/kuːl/", "adjective", "It is a cool evening.", "Mát mẻ", "Đó là một buổi tối mát mẻ.", "Hơi lạnh dễ chịu", 0),
    ("Sunny", "/ˈsʌn.i/", "adjective", "It is a sunny day.", "Có nắng", "Đó là một ngày có nắng.", "Nhiều ánh mặt trời", 0),
    ("Rainy", "/ˈreɪ.ni/", "adjective", "We stay home on rainy days.", "Có mưa", "Chúng tôi ở nhà vào những ngày mưa.", "Trời đổ mưa", 0),
    ("Cloudy", "/ˈklaʊ.di/", "adjective", "The sky is cloudy.", "Nhiều mây", "Bầu trời nhiều mây.", "Bị mây che", 0),
    ("Windy", "/ˈwɪn.di/", "adjective", "It is too windy.", "Có gió", "Trời quá nhiều gió.", "Gió thổi mạnh", 0),
    ("Snowy", "/ˈsnəʊ.i/", "adjective", "A snowy winter.", "Có tuyết", "Một mùa đông có tuyết.", "Phủ đầy tuyết", 0),
    ("Dry", "/draɪ/", "adjective", "The ground is dry.", "Khô ráo", "Mặt đất thì khô.", "Không ẩm ướt", 0),
    ("Wet", "/wet/", "adjective", "My shoes are wet.", "Ẩm ướt", "Giày của tôi bị ướt.", "Có nhiều nước", 0),
    ("Storm", "/stɔːm/", "noun", "A big storm is coming.", "Cơn bão", "Một cơn bão lớn đang đến.", "Thời tiết xấu, gió to", 0),
    ("Ice", "/aɪs/", "noun", "The lake is covered with ice.", "Băng đá", "Hồ nước bị bao phủ bởi băng.", "Nước đông cứng", 0),
    ("Star", "/stɑːr/", "noun", "I can see a star.", "Ngôi sao", "Tôi có thể nhìn thấy một ngôi sao.", "Điểm sáng ban đêm", 0),
    ("Moon", "/muːn/", "noun", "The moon is bright.", "Mặt trăng", "Mặt trăng sáng tỏ.", "Vệ tinh của Trái đất", 0),
    ("Day", "/deɪ/", "noun", "Have a good day.", "Ban ngày", "Chúc một ngày tốt lành.", "Có ánh sáng mặt trời", 0),
    ("Night", "/naɪt/", "noun", "Sleep well at night.", "Ban đêm", "Ngủ ngon vào ban đêm.", "Trời tối", 0),
    ("Clear", "/klɪər/", "adjective", "A clear sky.", "Quang đãng", "Bầu trời quang đãng.", "Không có mây", 0),
    ("Dark", "/dɑːk/", "adjective", "It is dark outside.", "Tối tăm", "Ngoài trời tối đen.", "Thiếu ánh sáng", 0),
    ("Light", "/laɪt/", "noun", "The light of the sun.", "Ánh sáng", "Ánh sáng của mặt trời.", "Thứ giúp nhìn thấy", 0),
    ("Weather", "/ˈweð.ər/", "noun", "Good weather.", "Thời tiết", "Thời tiết đẹp.", "Trạng thái khí quyển", 0),
    ("Spring", "/sprɪŋ/", "noun", "Spring is beautiful.", "Mùa xuân", "Mùa xuân rất đẹp.", "Mùa cây đâm chồi", 0),
    ("Summer", "/ˈsʌm.ər/", "noun", "I love summer.", "Mùa hè", "Tôi yêu mùa hè.", "Mùa nóng nhất", 0),
    ("Winter", "/ˈwɪn.tər/", "noun", "Winter is cold.", "Mùa đông", "Mùa đông thì lạnh.", "Mùa lạnh nhất", 0),

    # Intermediate (1)
    ("Autumn", "/ˈɔː.təm/", "noun", "Autumn leaves fall.", "Mùa thu", "Lá mùa thu rụng.", "Mùa lá rụng", 1),
    ("Fall", "/fɔːl/", "noun", "I visit in the fall.", "Mùa thu", "Tôi ghé thăm vào mùa thu.", "Từ Mỹ cho mùa thu", 1),
    ("Temperature", "/ˈtem.prə.tʃər/", "noun", "High temperature.", "Nhiệt độ", "Nhiệt độ cao.", "Độ nóng lạnh", 1),
    ("Degree", "/dɪˈɡriː/", "noun", "It is 30 degrees.", "Độ", "Bây giờ là 30 độ.", "Đơn vị đo nhiệt", 1),
    ("Forecast", "/ˈfɔː.kɑːst/", "noun", "Check the weather forecast.", "Dự báo thời tiết", "Kiểm tra dự báo thời tiết.", "Dự đoán trước", 1),
    ("Climate", "/ˈklaɪ.mət/", "noun", "A tropical climate.", "Khí hậu", "Khí hậu nhiệt đới.", "Thời tiết lâu dài", 1),
    ("Humidity", "/hjuːˈmɪd.ə.ti/", "noun", "High humidity today.", "Độ ẩm", "Độ ẩm cao hôm nay.", "Lượng hơi nước", 1),
    ("Fog", "/fɒɡ/", "noun", "Thick fog.", "Sương mù", "Sương mù dày đặc.", "Hơi nước mờ mịt", 1),
    ("Foggy", "/ˈfɒɡ.i/", "adjective", "A foggy morning.", "Có sương mù", "Một buổi sáng có sương mù.", "Đầy sương", 1),
    ("Breeze", "/briːz/", "noun", "A gentle breeze.", "Gió nhẹ", "Một cơn gió nhẹ.", "Gió hiu hiu", 1),
    ("Thunder", "/ˈθʌn.dər/", "noun", "A loud clap of thunder.", "Sấm", "Một tiếng sấm lớn.", "Âm thanh khi mưa", 1),
    ("Lightning", "/ˈlaɪt.nɪŋ/", "noun", "A flash of lightning.", "Chớp", "Một tia chớp.", "Ánh sáng trên trời", 1),
    ("Shower", "/ˈʃaʊ.ər/", "noun", "A brief rain shower.", "Mưa rào", "Một cơn mưa rào ngắn.", "Mưa xối xả", 1),
    ("Freeze", "/friːz/", "verb", "Water freezes at 0 degrees.", "Đóng băng", "Nước đóng băng ở 0 độ.", "Làm đông cứng", 1),
    ("Freezing", "/ˈfriː.zɪŋ/", "adjective", "It is freezing cold.", "Lạnh cóng", "Trời lạnh cóng.", "Rất lạnh", 1),
    ("Melt", "/melt/", "verb", "The snow will melt.", "Tan chảy", "Tuyết sẽ tan chảy.", "Chuyển thành nước", 1),
    ("Pour", "/pɔːr/", "verb", "It is pouring outside.", "Mưa to (đổ)", "Bên ngoài đang mưa trút nước.", "Mưa như trút", 1),
    ("Umbrella", "/ʌmˈbrel.ə/", "noun", "Take an umbrella.", "Cái ô, dù", "Mang theo một chiếc ô.", "Vật che mưa", 1),
    ("Raincoat", "/ˈreɪn.kəʊt/", "noun", "Wear a raincoat.", "Áo mưa", "Mặc áo mưa vào.", "Áo chống nước", 1),
    ("Rainbow", "/ˈreɪn.bəʊ/", "noun", "A colorful rainbow.", "Cầu vồng", "Một chiếc cầu vồng nhiều màu sắc.", "Vòng cung 7 màu", 1),
    ("Heat", "/hiːt/", "noun", "I can't stand the heat.", "Hơi nóng", "Tôi không thể chịu được hơi nóng.", "Sức nóng", 1),
    ("Mild", "/maɪld/", "adjective", "A mild winter.", "Ôn hòa", "Một mùa đông ôn hòa.", "Nhẹ nhàng", 1),
    ("Chilly", "/ˈtʃɪl.i/", "adjective", "A chilly breeze.", "Lạnh lẽo", "Một cơn gió se lạnh.", "Hơi lạnh", 1),
    ("Overcast", "/ˌəʊ.vəˈkɑːst/", "adjective", "An overcast sky.", "U ám, phủ mây", "Bầu trời u ám.", "Nhiều mây xám", 1),
    ("Puddle", "/ˈpʌd.əl/", "noun", "Jump in the puddle.", "Vũng nước", "Nhảy vào vũng nước.", "Nước đọng lại", 1),
    ("Flood", "/flʌd/", "noun", "A severe flood.", "Lũ lụt", "Một trận lũ lụt nghiêm trọng.", "Nước tràn ngập", 1),
    ("Drought", "/draʊt/", "noun", "A long drought.", "Hạn hán", "Một đợt hạn hán kéo dài.", "Thiếu nước", 1),
    ("Hurricane", "/ˈhʌr.ɪ.kən/", "noun", "A destructive hurricane.", "Bão cuồng phong", "Một cơn bão cuồng phong có tính tàn phá.", "Bão lớn", 1),
    ("Tornado", "/tɔːˈneɪ.dəʊ/", "noun", "A tornado warning.", "Lốc xoáy", "Cảnh báo lốc xoáy.", "Vòi rồng", 1),
    ("Season", "/ˈsiː.zən/", "noun", "My favorite season.", "Mùa", "Mùa yêu thích của tôi.", "Thời kỳ trong năm", 1),

    # Advanced (2)
    ("Atmosphere", "/ˈæt.mə.sfɪər/", "noun", "The Earth's atmosphere.", "Khí quyển", "Khí quyển của Trái Đất.", "Lớp không khí", 2),
    ("Barometer", "/bəˈrɒm.ɪ.tər/", "noun", "Check the barometer.", "Khí áp kế", "Kiểm tra khí áp kế.", "Dụng cụ đo áp suất", 2),
    ("Blizzard", "/ˈblɪz.əd/", "noun", "Trapped in a blizzard.", "Bão tuyết", "Bị mắc kẹt trong một trận bão tuyết.", "Bão tuyết mạnh", 2),
    ("Typhoon", "/taɪˈfuːn/", "noun", "A typhoon hit the coast.", "Bão nhiệt đới", "Một cơn bão nhiệt đới ập vào bờ biển.", "Bão ở châu Á", 2),
    ("Cyclone", "/ˈsaɪ.kləʊn/", "noun", "A tropical cyclone.", "Lốc, gió xoáy", "Một cơn lốc xoáy nhiệt đới.", "Gió xoay tròn", 2),
    ("Monsoon", "/mɒnˈsuːn/", "noun", "The monsoon season.", "Gió mùa, mùa mưa", "Mùa gió mùa.", "Gió theo mùa", 2),
    ("Avalanche", "/ˈæv.əl.ɑːntʃ/", "noun", "Buried by an avalanche.", "Tuyết lở", "Bị vùi lấp bởi một trận tuyết lở.", "Tuyết sạt lở", 2),
    ("Drizzle", "/ˈdrɪz.əl/", "noun", "A light drizzle.", "Mưa phùn", "Một cơn mưa phùn nhẹ.", "Mưa lất phất", 2),
    ("Sleet", "/sliːt/", "noun", "Rain mixed with sleet.", "Mưa tuyết", "Mưa rào lẫn với mưa tuyết.", "Mưa đá nhỏ", 2),
    ("Hail", "/heɪl/", "noun", "Hail damaged the car.", "Mưa đá", "Mưa đá làm hỏng chiếc xe.", "Cục đá từ trời rơi", 2),
    ("Frost", "/frɒst/", "noun", "Frost on the window.", "Sương giá", "Sương giá trên cửa sổ.", "Lớp băng mỏng", 2),
    ("Dew", "/dʒuː/", "noun", "Morning dew on the grass.", "Sương, giọt sương", "Giọt sương ban mai trên bãi cỏ.", "Hạt nước đọng", 2),
    ("Smog", "/smɒɡ/", "noun", "City smog.", "Khói bụi", "Khói bụi thành phố.", "Khói ô nhiễm", 2),
    ("Mist", "/mɪst/", "noun", "Mist over the mountains.", "Sương mù nhẹ", "Sương mù nhẹ bao phủ những ngọn núi.", "Sương lờ mờ", 2),
    ("Meteorologist", "/ˌmiː.ti.əˈrɒl.ə.dʒɪst/", "noun", "A meteorologist predicted rain.", "Nhà khí tượng học", "Nhà khí tượng học đã dự báo trời mưa.", "Người dự báo thời tiết", 2),
    ("Precipitation", "/prɪˌsɪp.ɪˈteɪ.ʃən/", "noun", "Annual precipitation.", "Lượng mưa", "Lượng mưa hàng năm.", "Mưa, tuyết rơi", 2),
    ("Downpour", "/ˈdaʊn.pɔːr/", "noun", "Caught in a sudden downpour.", "Trận mưa trút nước", "Bị mắc kẹt trong một trận mưa trút nước bất ngờ.", "Mưa rất to", 2),
    ("Gale", "/ɡeɪl/", "noun", "Gale-force winds.", "Gió giật mạnh", "Những cơn gió giật mạnh.", "Gió cấp cao", 2),
    ("Gust", "/ɡʌst/", "noun", "A gust of wind.", "Cơn gió lốc", "Một cơn gió lốc.", "Gió thổi mạnh bất ngờ", 2),
    ("Drought-stricken", "/ˈdraʊtˌstrɪk.ən/", "adjective", "A drought-stricken area.", "Bị hạn hán", "Một khu vực bị hạn hán hoành hành.", "Gặp hạn hán", 2),
    ("Sweltering", "/ˈswel.tər.ɪŋ/", "adjective", "Sweltering heat.", "Oi bức", "Cái nóng oi bức.", "Nóng đổ mồ hôi", 2),
    ("Muggy", "/ˈmʌɡ.i/", "adjective", "A muggy afternoon.", "Nồm, oi bức", "Một buổi chiều nồm oi bức.", "Ẩm ướt và nóng", 2),
    ("Scorching", "/ˈskɔː.tʃɪŋ/", "adjective", "A scorching summer day.", "Nóng thiêu đốt", "Một ngày hè nóng thiêu đốt.", "Nóng cháy da", 2),
    ("Blistering", "/ˈblɪs.tər.ɪŋ/", "adjective", "Blistering cold.", "Khắc nghiệt (cực nóng/lạnh)", "Cái lạnh cắt da.", "Cực độ", 2),
    ("Frostbite", "/ˈfrɒst.baɪt/", "noun", "He suffered from frostbite.", "Bị bỏng lạnh", "Anh ấy bị chứng bỏng lạnh.", "Tổn thương do lạnh", 2),
    ("Sunstroke", "/ˈsʌn.strəʊk/", "noun", "Risk of sunstroke.", "Say nắng", "Nguy cơ say nắng.", "Say vì mặt trời", 2),
    ("Heatwave", "/ˈhiːt.weɪv/", "noun", "A summer heatwave.", "Đợt nắng nóng", "Một đợt nắng nóng mùa hè.", "Cơn sóng nhiệt", 2),
    ("Altitude", "/ˈæl.tɪ.tʃuːd/", "noun", "High altitude.", "Độ cao", "Độ cao lớn.", "Độ cao so với mặt biển", 2),
    ("Latitude", "/ˈlæt.ɪ.tʃuːd/", "noun", "Low latitude.", "Vĩ độ", "Vĩ độ thấp.", "Vị trí Bắc Nam", 2),
    ("Longitude", "/ˈlɒŋ.ɡɪ.tʃuːd/", "noun", "Lines of longitude.", "Kinh độ", "Các đường kinh độ.", "Vị trí Đông Tây", 2),

    # Expert (3)
    ("Climatology", "/ˌklaɪ.məˈtɒl.ə.dʒi/", "noun", "Study climatology.", "Khí hậu học", "Nghiên cứu khí hậu học.", "Ngành học về khí hậu", 3),
    ("Troposphere", "/ˈtrɒp.ə.sfɪər/", "noun", "Weather occurs in the troposphere.", "Tầng đối lưu", "Thời tiết diễn ra ở tầng đối lưu.", "Tầng khí quyển thấp", 3),
    ("Stratosphere", "/ˈstræt.ə.sfɪər/", "noun", "Ozone in the stratosphere.", "Tầng bình lưu", "Khí ô-zôn trong tầng bình lưu.", "Tầng khí quyển cao", 3),
    ("Ozone layer", "/ˈəʊ.zəʊn ˌleɪ.ər/", "noun", "Hole in the ozone layer.", "Tầng ô-zôn", "Lỗ thủng ở tầng ô-zôn.", "Lớp bảo vệ Trái Đất", 3),
    ("Greenhouse effect", "/ˈɡriːn.haʊs ɪˌfekt/", "noun", "The greenhouse effect.", "Hiệu ứng nhà kính", "Hiệu ứng nhà kính.", "Làm nóng Trái Đất", 3),
    ("Global warming", "/ˌɡləʊ.bəl ˈwɔː.mɪŋ/", "noun", "Stop global warming.", "Sự nóng lên toàn cầu", "Ngăn chặn sự nóng lên toàn cầu.", "Trái Đất nóng lên", 3),
    ("El Niño", "/el ˈniːn.jəʊ/", "noun", "El Niño pattern.", "Hiện tượng El Niño", "Hiện tượng El Niño.", "Nóng lên bất thường", 3),
    ("La Niña", "/lɑː ˈniːn.jə/", "noun", "La Niña event.", "Hiện tượng La Niña", "Hiện tượng La Niña.", "Lạnh đi bất thường", 3),
    ("Equator", "/ɪˈkweɪ.tər/", "noun", "Near the equator.", "Đường xích đạo", "Gần đường xích đạo.", "Đường chia Trái Đất", 3),
    ("Hemisphere", "/ˈhem.ɪ.sfɪər/", "noun", "Northern hemisphere.", "Bán cầu", "Bán cầu bắc.", "Một nửa Trái Đất", 3),
    ("Solstice", "/ˈsɒl.stɪs/", "noun", "Summer solstice.", "Điểm chí", "Hạ chí.", "Thời điểm mặt trời mọc/lặn xa nhất", 3),
    ("Equinox", "/ˈek.wɪ.nɒks/", "noun", "Vernal equinox.", "Điểm phân", "Xuân phân.", "Ngày đêm bằng nhau", 3),
    ("Microclimate", "/ˈmaɪ.krəʊˌklaɪ.mət/", "noun", "A city microclimate.", "Vi khí hậu", "Vi khí hậu của thành phố.", "Khí hậu vùng nhỏ", 3),
    ("Anemometer", "/ˌæn.ɪˈmɒm.ɪ.tər/", "noun", "Measure wind with an anemometer.", "Máy đo phong tốc", "Đo gió bằng máy đo phong tốc.", "Máy đo gió", 3),
    ("Hygrometer", "/haɪˈɡrɒm.ɪ.tər/", "noun", "Check the hygrometer.", "Ẩm kế", "Kiểm tra ẩm kế.", "Máy đo độ ẩm", 3),
    ("Isobar", "/ˈaɪ.səʊ.bɑːr/", "noun", "Lines of isobar.", "Đường đẳng áp", "Các đường đẳng áp.", "Đường nối áp suất", 3),
    ("Convection", "/kənˈvek.ʃən/", "noun", "Heat convection.", "Sự đối lưu", "Sự đối lưu nhiệt.", "Truyền nhiệt trong chất lỏng/khí", 3),
    ("Evaporation", "/ɪˌvæp.əˈreɪ.ʃən/", "noun", "Water evaporation.", "Sự bay hơi", "Sự bay hơi của nước.", "Nước hóa hơi", 3),
    ("Condensation", "/ˌkɒn.denˈseɪ.ʃən/", "noun", "Cloud condensation.", "Sự ngưng tụ", "Sự ngưng tụ của mây.", "Hơi nước hóa lỏng", 3),
    ("Transpiration", "/ˌtræn.spɪˈreɪ.ʃən/", "noun", "Plant transpiration.", "Sự thoát hơi nước", "Sự thoát hơi nước của cây.", "Thoát hơi ở lá", 3),
    ("Sublimation", "/ˌsʌb.lɪˈmeɪ.ʃən/", "noun", "Ice sublimation.", "Sự thăng hoa", "Sự thăng hoa của băng.", "Rắn hóa khí", 3),
    ("Deposition", "/ˌdep.əˈzɪʃ.ən/", "noun", "Vapor deposition.", "Sự lắng đọng", "Sự lắng đọng hơi nước.", "Khí hóa rắn", 3),
    ("Inversion", "/ɪnˈvɜː.ʃən/", "noun", "Temperature inversion.", "Nghịch nhiệt", "Nghịch nhiệt.", "Nhiệt độ ngược đời", 3),
    ("Anticyclone", "/ˌæn.tiˈsaɪ.kləʊn/", "noun", "An anticyclone system.", "Khối áp cao", "Một hệ thống khối áp cao.", "Vùng áp suất cao", 3),
    ("Jet stream", "/ˈdʒet ˌstriːm/", "noun", "The polar jet stream.", "Luồng gió phản lực", "Luồng gió phản lực vùng cực.", "Dòng khí mạnh trên cao", 3),
    ("Trade winds", "/ˈtreɪd ˌwɪndz/", "noun", "Sailing on trade winds.", "Gió tín phong", "Căng buồm trên gió tín phong.", "Gió mậu dịch", 3),
    ("Permafrost", "/ˈpɜː.mə.frɒst/", "noun", "Melting permafrost.", "Tầng đất đóng băng vĩnh cửu", "Tầng đất đóng băng vĩnh cửu đang tan.", "Đất luôn đóng băng", 3),
    ("Tsunami", "/tsuːˈnɑː.mi/", "noun", "Tsunami warning.", "Sóng thần", "Cảnh báo sóng thần.", "Sóng lớn từ biển", 3),
    ("Desertification", "/dɪˌzɜː.tɪ.fɪˈkeɪ.ʃən/", "noun", "Stop desertification.", "Hiện tượng sa mạc hóa", "Ngăn chặn hiện tượng sa mạc hóa.", "Đất biến thành sa mạc", 3),
    ("Albedo", "/ælˈbiː.dəʊ/", "noun", "Earth's albedo.", "Suất phản chiếu", "Suất phản chiếu của Trái Đất.", "Tỉ lệ phản xạ ánh sáng", 3)
]

level_map = ["en_weather_beginner", "en_weather_intermediate", "en_weather_advanced", "en_weather_expert"]

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

print("Successfully appended 120 words for Weather (English) to DB!")
