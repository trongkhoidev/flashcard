import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_daily_beginner", "languageCode": "en", "title": "Cuộc sống", "subtitle": "Cuộc sống - Beginner", "iconEmoji": "☕", "level": "Beginner", "colorHex": "#14B8A6", "cardCount": 30},
    {"id": "en_daily_intermediate", "languageCode": "en", "title": "Cuộc sống", "subtitle": "Cuộc sống - Intermediate", "iconEmoji": "☕", "level": "Intermediate", "colorHex": "#0D9488", "cardCount": 30},
    {"id": "en_daily_advanced", "languageCode": "en", "title": "Cuộc sống", "subtitle": "Cuộc sống - Advanced", "iconEmoji": "☕", "level": "Advanced", "colorHex": "#0F766E", "cardCount": 30},
    {"id": "en_daily_expert", "languageCode": "en", "title": "Cuộc sống", "subtitle": "Cuộc sống - Expert", "iconEmoji": "☕", "level": "Expert", "colorHex": "#115E59", "cardCount": 30}
]

vocab_data = [
    # Beginner (0)
    ("Wake up", "/weɪk ʌp/", "verb", "I wake up early.", "Thức dậy", "Tôi thức dậy sớm.", "Mở mắt dậy", 0),
    ("Sleep", "/sliːp/", "verb", "I sleep well.", "Ngủ", "Tôi ngủ ngon.", "Nhắm mắt nghỉ", 0),
    ("Bed", "/bed/", "noun", "Go to bed.", "Giường", "Đi ngủ.", "Đồ để nằm", 0),
    ("Morning", "/ˈmɔː.nɪŋ/", "noun", "Good morning.", "Buổi sáng", "Chào buổi sáng.", "Lúc bình minh", 0),
    ("Afternoon", "/ˌɑːf.təˈnuːn/", "noun", "Good afternoon.", "Buổi chiều", "Chào buổi chiều.", "Lúc xế chiều", 0),
    ("Evening", "/ˈiːv.nɪŋ/", "noun", "Good evening.", "Buổi tối", "Chào buổi tối.", "Lúc mặt trời lặn", 0),
    ("Night", "/naɪt/", "noun", "Good night.", "Ban đêm", "Chúc ngủ ngon.", "Trời tối hẳn", 0),
    ("Breakfast", "/ˈbrek.fəst/", "noun", "Eat breakfast.", "Bữa sáng", "Ăn sáng.", "Bữa đầu ngày", 0),
    ("Lunch", "/lʌntʃ/", "noun", "Have lunch.", "Bữa trưa", "Ăn trưa.", "Bữa giữa ngày", 0),
    ("Dinner", "/ˈdɪn.ər/", "noun", "Cook dinner.", "Bữa tối", "Nấu bữa tối.", "Bữa cuối ngày", 0),
    ("Eat", "/iːt/", "verb", "Eat food.", "Ăn", "Ăn thức ăn.", "Nhai nuốt", 0),
    ("Drink", "/drɪŋk/", "verb", "Drink water.", "Uống", "Uống nước.", "Nuốt chất lỏng", 0),
    ("Water", "/ˈwɔː.tər/", "noun", "Drink some water.", "Nước", "Uống một chút nước.", "Chất lỏng", 0),
    ("Home", "/həʊm/", "noun", "Go home.", "Nhà", "Về nhà.", "Tổ ấm", 0),
    ("House", "/haʊs/", "noun", "A big house.", "Ngôi nhà", "Một ngôi nhà lớn.", "Tòa nhà để ở", 0),
    ("Work", "/wɜːk/", "verb", "I work hard.", "Làm việc", "Tôi làm việc chăm chỉ.", "Lao động", 0),
    ("School", "/skuːl/", "noun", "Go to school.", "Trường học", "Đi đến trường.", "Nơi học tập", 0),
    ("Go", "/ɡəʊ/", "verb", "Go out.", "Đi", "Đi ra ngoài.", "Di chuyển", 0),
    ("Come", "/kʌm/", "verb", "Come here.", "Đến", "Đến đây.", "Tới gần", 0),
    ("Time", "/taɪm/", "noun", "What time is it?", "Thời gian", "Mấy giờ rồi?", "Giờ giấc", 0),
    ("Day", "/deɪ/", "noun", "A nice day.", "Ngày", "Một ngày đẹp trời.", "24 giờ", 0),
    ("Week", "/wiːk/", "noun", "Next week.", "Tuần", "Tuần tới.", "7 ngày", 0),
    ("Month", "/mʌnθ/", "noun", "Next month.", "Tháng", "Tháng tới.", "Khoảng 30 ngày", 0),
    ("Year", "/jɪər/", "noun", "Happy new year.", "Năm", "Chúc mừng năm mới.", "12 tháng", 0),
    ("Friend", "/frend/", "noun", "My best friend.", "Bạn bè", "Người bạn tốt nhất của tôi.", "Người kết bạn", 0),
    ("Family", "/ˈfæm.əl.i/", "noun", "My family.", "Gia đình", "Gia đình tôi.", "Người thân", 0),
    ("Play", "/pleɪ/", "verb", "Play games.", "Chơi", "Chơi game.", "Vui đùa", 0),
    ("Read", "/riːd/", "verb", "Read a book.", "Đọc", "Đọc một cuốn sách.", "Xem chữ", 0),
    ("Watch", "/wɒtʃ/", "verb", "Watch TV.", "Xem", "Xem tivi.", "Nhìn theo dõi", 0),
    ("TV", "/ˌtiːˈviː/", "noun", "Turn on the TV.", "Tivi", "Bật tivi lên.", "Máy vô tuyến", 0),

    # Intermediate (1)
    ("Routine", "/ruːˈtiːn/", "noun", "My daily routine.", "Thói quen hàng ngày", "Thói quen hàng ngày của tôi.", "Việc lặp lại", 1),
    ("Habit", "/ˈhæb.ɪt/", "noun", "A good habit.", "Thói quen", "Một thói quen tốt.", "Tật quen thuộc", 1),
    ("Shower", "/ˈʃaʊ.ər/", "noun", "Take a shower.", "Tắm vòi sen", "Đi tắm vòi sen.", "Tắm rửa", 1),
    ("Brush", "/brʌʃ/", "verb", "Brush your teeth.", "Chải", "Đánh răng.", "Chà rửa", 1),
    ("Dress", "/dres/", "verb", "Get dressed.", "Mặc quần áo", "Mặc quần áo vào.", "Khoác đồ lên", 1),
    ("Commute", "/kəˈmjuːt/", "verb", "Commute to work.", "Đi làm hằng ngày", "Đi lại tới chỗ làm.", "Đi làm xa", 1),
    ("Traffic", "/ˈtræf.ɪk/", "noun", "Heavy traffic.", "Giao thông", "Giao thông đông đúc.", "Xe cộ qua lại", 1),
    ("Grocery", "/ˈɡrəʊ.sər.i/", "noun", "Buy grocery.", "Tạp hóa", "Mua hàng tạp hóa.", "Đồ dùng gia đình", 1),
    ("Laundry", "/ˈlɔːn.dri/", "noun", "Do the laundry.", "Giặt giũ", "Giặt quần áo.", "Làm sạch đồ", 1),
    ("Chores", "/tʃɔːrz/", "noun", "Do household chores.", "Việc nhà", "Làm việc nhà.", "Việc vặt", 1),
    ("Exercise", "/ˈek.sə.saɪz/", "verb", "Exercise daily.", "Tập thể dục", "Tập thể dục mỗi ngày.", "Vận động cơ thể", 1),
    ("Gym", "/dʒɪm/", "noun", "Go to the gym.", "Phòng tập thể hình", "Đi đến phòng tập.", "Nơi tập tạ", 1),
    ("Relax", "/rɪˈlæks/", "verb", "Time to relax.", "Thư giãn", "Đến lúc thư giãn.", "Nghỉ xả hơi", 1),
    ("Hobby", "/ˈhɒb.i/", "noun", "My hobby is reading.", "Sở thích", "Sở thích của tôi là đọc sách.", "Thú vui", 1),
    ("Weekend", "/ˌwiːkˈend/", "noun", "A busy weekend.", "Cuối tuần", "Một ngày cuối tuần bận rộn.", "Thứ 7, CN", 1),
    ("Schedule", "/ˈʃedʒ.uːl/", "noun", "A tight schedule.", "Lịch trình", "Một lịch trình kín mít.", "Thời gian biểu", 1),
    ("Appointment", "/əˈpɔɪnt.mənt/", "noun", "A doctor's appointment.", "Cuộc hẹn", "Một cuộc hẹn với bác sĩ.", "Hẹn gặp", 1),
    ("Meeting", "/ˈmiː.tɪŋ/", "noun", "A business meeting.", "Cuộc họp", "Một cuộc họp kinh doanh.", "Họp mặt bàn bạc", 1),
    ("Colleague", "/ˈkɒl.iːɡ/", "noun", "A helpful colleague.", "Đồng nghiệp", "Một người đồng nghiệp nhiệt tình.", "Người làm chung", 1),
    ("Boss", "/bɒs/", "noun", "My boss is strict.", "Sếp", "Sếp của tôi rất nghiêm khắc.", "Quản lý cấp trên", 1),
    ("Salary", "/ˈsæl.ər.i/", "noun", "A good salary.", "Lương", "Mức lương tốt.", "Tiền công tháng", 1),
    ("Budget", "/ˈbʌdʒ.ɪt/", "noun", "Monthly budget.", "Ngân sách", "Ngân sách hàng tháng.", "Khoản tiền chi", 1),
    ("Bill", "/bɪl/", "noun", "Pay the electric bill.", "Hóa đơn", "Trả hóa đơn tiền điện.", "Giấy báo tiền", 1),
    ("Pay", "/peɪ/", "verb", "Pay for food.", "Trả tiền", "Trả tiền đồ ăn.", "Thanh toán", 1),
    ("Save", "/seɪv/", "verb", "Save money.", "Tiết kiệm", "Tiết kiệm tiền.", "Giữ lại", 1),
    ("Spend", "/spend/", "verb", "Spend time with family.", "Tiêu xài, dành", "Dành thời gian cho gia đình.", "Sử dụng (tiền/giờ)", 1),
    ("Clean", "/kliːn/", "verb", "Clean the room.", "Dọn dẹp", "Dọn dẹp căn phòng.", "Làm sạch", 1),
    ("Cook", "/kʊk/", "verb", "Cook a meal.", "Nấu ăn", "Nấu một bữa ăn.", "Làm chín đồ ăn", 1),
    ("Rest", "/rest/", "verb", "Take a rest.", "Nghỉ ngơi", "Nghỉ ngơi một chút.", "Nằm nghỉ", 1),
    ("Nap", "/næp/", "noun", "Take a short nap.", "Giấc ngủ trưa", "Ngủ một giấc ngắn.", "Ngủ chợp mắt", 1),

    # Advanced (2)
    ("Productivity", "/ˌprɒd.ʌkˈtɪv.ə.ti/", "noun", "High productivity.", "Năng suất", "Năng suất cao.", "Khả năng làm ra", 2),
    ("Efficiency", "/ɪˈfɪʃ.ən.si/", "noun", "Improve efficiency.", "Hiệu quả", "Cải thiện hiệu quả.", "Năng lực làm tốt", 2),
    ("Procrastinate", "/prəˈkræs.tɪ.neɪt/", "verb", "Stop procrastinating.", "Trì hoãn", "Hãy ngừng trì hoãn.", "Để chậm lại", 2),
    ("Prioritize", "/praɪˈɒr.ɪ.taɪz/", "verb", "Prioritize tasks.", "Ưu tiên", "Ưu tiên các công việc.", "Xếp lên trên", 2),
    ("Multitask", "/ˌmʌl.tiˈtɑːsk/", "verb", "Ability to multitask.", "Làm nhiều việc cùng lúc", "Khả năng làm nhiều việc cùng lúc.", "Đa nhiệm", 2),
    ("Deadline", "/ˈded.laɪn/", "noun", "Meet the deadline.", "Hạn chót", "Kịp hạn chót.", "Hạn cuối", 2),
    ("Milestone", "/ˈmaɪl.stəʊn/", "noun", "An important milestone.", "Cột mốc", "Một cột mốc quan trọng.", "Điểm đáng nhớ", 2),
    ("Balance", "/ˈbæl.əns/", "noun", "Work-life balance.", "Cân bằng", "Cân bằng công việc và cuộc sống.", "Trạng thái ổn định", 2),
    ("Lifestyle", "/ˈlaɪf.staɪl/", "noun", "A healthy lifestyle.", "Lối sống", "Một lối sống lành mạnh.", "Cách sống", 2),
    ("Wellbeing", "/ˌwelˈbiː.ɪŋ/", "noun", "Physical wellbeing.", "Sự khỏe mạnh", "Sự khỏe mạnh về thể chất.", "Trạng thái an vui", 2),
    ("Mindfulness", "/ˈmaɪnd.fəl.nəs/", "noun", "Practice mindfulness.", "Sự chánh niệm", "Thực hành chánh niệm.", "Tập trung hiện tại", 2),
    ("Meditation", "/ˌmed.ɪˈteɪ.ʃən/", "noun", "Morning meditation.", "Sự thiền định", "Thiền buổi sáng.", "Tĩnh tâm", 2),
    ("Leisure", "/ˈleʒ.ər/", "noun", "Leisure activities.", "Thời gian rảnh rỗi", "Các hoạt động lúc rảnh rỗi.", "Giờ giải trí", 2),
    ("Recreation", "/ˌrek.riˈeɪ.ʃən/", "noun", "Outdoor recreation.", "Sự giải trí", "Hoạt động giải trí ngoài trời.", "Tiêu khiển", 2),
    ("Extracurricular", "/ˌek.strə.kəˈrɪk.jə.lər/", "adjective", "Extracurricular activities.", "Ngoại khóa", "Hoạt động ngoại khóa.", "Ngoài giờ học", 2),
    ("Socialize", "/ˈsəʊ.ʃəl.aɪz/", "verb", "Socialize with friends.", "Giao lưu xã hội", "Giao lưu với bạn bè.", "Giao tiếp xã hội", 2),
    ("Networking", "/ˈnet.wɜː.kɪŋ/", "noun", "Business networking.", "Xây dựng mạng lưới quan hệ", "Xây dựng mạng lưới kinh doanh.", "Tạo quan hệ", 2),
    ("Obligation", "/ˌɒb.lɪˈɡeɪ.ʃən/", "noun", "A moral obligation.", "Nghĩa vụ", "Một nghĩa vụ đạo đức.", "Bổn phận", 2),
    ("Responsibility", "/rɪˌspɒn.sɪˈbɪl.ə.ti/", "noun", "Take responsibility.", "Trách nhiệm", "Chịu trách nhiệm.", "Việc phải gánh vác", 2),
    ("Commitment", "/kəˈmɪt.mənt/", "noun", "A strong commitment.", "Sự cam kết", "Một sự cam kết mạnh mẽ.", "Lời hứa chắc chắn", 2),
    ("Errand", "/ˈer.ənd/", "noun", "Run errands.", "Việc vặt", "Đi làm việc vặt.", "Việc lặt vặt", 2),
    ("Utility", "/juːˈtɪl.ə.ti/", "noun", "Utility bills.", "Tiện ích", "Hóa đơn điện nước.", "Dịch vụ công cộng", 2),
    ("Mortgage", "/ˈmɔː.ɡɪdʒ/", "noun", "Pay the mortgage.", "Khoản vay thế chấp", "Trả tiền thế chấp nhà.", "Vay tiền mua nhà", 2),
    ("Insurance", "/ɪnˈʃʊə.rəns/", "noun", "Health insurance.", "Bảo hiểm", "Bảo hiểm y tế.", "Phí bảo vệ rủi ro", 2),
    ("Pension", "/ˈpen.ʃən/", "noun", "Retirement pension.", "Lương hưu", "Lương hưu trí.", "Tiền cho người già", 2),
    ("Inflation", "/ɪnˈfleɪ.ʃən/", "noun", "High inflation.", "Lạm phát", "Lạm phát cao.", "Giá cả tăng cao", 2),
    ("Commuter", "/kəˈmjuː.tər/", "noun", "A daily commuter.", "Người đi làm xa", "Một người đi làm xa hằng ngày.", "Người di chuyển xa", 2),
    ("Infrastructure", "/ˈɪn.frəˌstrʌk.tʃər/", "noun", "City infrastructure.", "Cơ sở hạ tầng", "Cơ sở hạ tầng của thành phố.", "Đường xá, cầu cống", 2),
    ("Amenity", "/əˈmiː.nə.ti/", "noun", "Local amenities.", "Sự tiện nghi", "Những tiện nghi tại địa phương.", "Tiện nghi công cộng", 2),
    ("Sustainability", "/səˌsteɪ.nəˈbɪl.ə.ti/", "noun", "Environmental sustainability.", "Sự bền vững", "Sự bền vững môi trường.", "Duy trì dài lâu", 2),

    # Expert (3)
    ("Circadian rhythm", "/sɜːˌkeɪ.di.ən ˈrɪð.əm/", "noun", "Disrupt circadian rhythm.", "Nhịp sinh học", "Phá vỡ nhịp sinh học.", "Đồng hồ sinh học", 3),
    ("Sedentary", "/ˈsed.ən.tər.i/", "adjective", "A sedentary job.", "Ít vận động", "Một công việc ít vận động.", "Ngồi nhiều", 3),
    ("Ergonomics", "/ˌɜː.ɡəˈnɒm.ɪks/", "noun", "Office ergonomics.", "Công thái học", "Công thái học tại văn phòng.", "Thiết kế phù hợp cơ thể", 3),
    ("Minimalism", "/ˈmɪn.ɪ.mə.lɪ.zəm/", "noun", "Embrace minimalism.", "Chủ nghĩa tối giản", "Đi theo chủ nghĩa tối giản.", "Sống đơn giản", 3),
    ("Consumerism", "/kənˈsjuː.mə.rɪ.zəm/", "noun", "Modern consumerism.", "Chủ nghĩa tiêu dùng", "Chủ nghĩa tiêu dùng hiện đại.", "Xã hội chuộng mua sắm", 3),
    ("Frugality", "/fruːˈɡæl.ə.ti/", "noun", "Live with frugality.", "Sự tiết kiệm", "Sống tiết kiệm thanh đạm.", "Cần kiệm", 3),
    ("Affluence", "/ˈæf.lu.əns/", "noun", "A life of affluence.", "Sự sung túc", "Một cuộc sống sung túc.", "Sự giàu có", 3),
    ("Gentrification", "/ˌdʒen.trɪ.fɪˈkeɪ.ʃən/", "noun", "Urban gentrification.", "Chỉnh trang đô thị", "Sự chỉnh trang đô thị (làm giá đất tăng).", "Làm mới khu dân cư", 3),
    ("Urbanization", "/ˌɜː.bən.aɪˈzeɪ.ʃən/", "noun", "Rapid urbanization.", "Sự đô thị hóa", "Sự đô thị hóa nhanh chóng.", "Lên thành phố", 3),
    ("Suburban", "/səˈbɜː.bən/", "adjective", "Suburban life.", "Ngoại ô", "Cuộc sống ở ngoại ô.", "Ven đô", 3),
    ("Cosmopolitan", "/ˌkɒz.məˈpɒl.ɪ.tən/", "adjective", "A cosmopolitan society.", "Đa văn hóa", "Một xã hội đa văn hóa.", "Mang tính quốc tế", 3),
    ("Metropolitan", "/ˌmet.rəˈpɒl.ɪ.tən/", "adjective", "Metropolitan area.", "Đô thị", "Khu vực đô thị lớn.", "Thủ phủ", 3),
    ("Demographics", "/ˌdem.əˈɡræf.ɪks/", "noun", "Changing demographics.", "Nhân khẩu học", "Nhân khẩu học đang thay đổi.", "Dữ liệu dân số", 3),
    ("Societal", "/səˈsaɪ.ə.təl/", "adjective", "Societal norms.", "Thuộc về xã hội", "Các chuẩn mực xã hội.", "Tính xã hội", 3),
    ("Egalitarian", "/ɪˌɡæl.ɪˈteə.ri.ən/", "adjective", "An egalitarian society.", "Bình đẳng", "Một xã hội bình đẳng.", "Ngang bằng", 3),
    ("Meritocracy", "/ˌmer.ɪˈtɒk.rə.si/", "noun", "A true meritocracy.", "Chế độ nhân tài", "Một chế độ nhân tài thực sự.", "Đề cao năng lực", 3),
    ("Nepotism", "/ˈnep.ə.tɪ.zəm/", "noun", "Accused of nepotism.", "Gia đình trị", "Bị cáo buộc thói con ông cháu cha.", "Kéo bè phái người nhà", 3),
    ("Bureaucracy", "/bjʊəˈrɒk.rə.si/", "noun", "Government bureaucracy.", "Chế độ quan liêu", "Chế độ quan liêu của chính phủ.", "Máy móc hành chính", 3),
    ("Red tape", "/ˌred ˈteɪp/", "noun", "Cut through red tape.", "Tệ quan liêu", "Cắt giảm thủ tục rườm rà.", "Thủ tục giấy tờ", 3),
    ("Status quo", "/ˌsteɪ.təs ˈkwəʊ/", "noun", "Maintain the status quo.", "Hiện trạng", "Duy trì hiện trạng.", "Trạng thái hiện tại", 3),
    ("Paradigm shift", "/ˈpær.ə.daɪm ˌʃɪft/", "noun", "A paradigm shift.", "Sự chuyển đổi nhận thức", "Một sự thay đổi mô hình nhận thức.", "Thay đổi tư duy", 3),
    ("Zeitgeist", "/ˈtsaɪt.ɡaɪst/", "noun", "The zeitgeist of the 90s.", "Tinh thần thời đại", "Tinh thần thời đại của thập niên 90.", "Đặc trưng thời kì", 3),
    ("Ephemeral", "/ɪˈfem.ər.əl/", "adjective", "Ephemeral joy.", "Phù du", "Niềm vui chóng vánh.", "Chóng tàn", 3),
    ("Ubiquitous", "/juːˈbɪk.wɪ.təs/", "adjective", "Ubiquitous smartphones.", "Khắp nơi", "Điện thoại thông minh có mặt khắp nơi.", "Nhan nhản", 3),
    ("Mundane", "/mʌnˈdeɪn/", "adjective", "Mundane tasks.", "Nhàm chán", "Các công việc bình thường tẻ nhạt.", "Lặp đi lặp lại", 3),
    ("Monotonous", "/məˈnɒt.ən.əs/", "adjective", "A monotonous voice.", "Đơn điệu", "Một giọng nói đơn điệu.", "Không thay đổi", 3),
    ("Hedonism", "/ˈhiː.dən.ɪ.zəm/", "noun", "A life of hedonism.", "Chủ nghĩa khoái lạc", "Một cuộc sống chạy theo khoái lạc.", "Ham hưởng thụ", 3),
    ("Altruism", "/ˈæl.tru.ɪ.zəm/", "noun", "Acts of altruism.", "Lòng vị tha", "Những hành động vị tha.", "Làm vì người khác", 3),
    ("Philanthropy", "/fɪˈlæn.θrə.pi/", "noun", "Corporate philanthropy.", "Hoạt động từ thiện", "Hoạt động từ thiện của doanh nghiệp.", "Bố thí, giúp đỡ", 3),
    ("Existential", "/ˌeɡ.zɪˈsten.ʃəl/", "adjective", "Existential crisis.", "Thuộc về sự tồn tại", "Khủng hoảng hiện sinh (tồn tại).", "Thuộc về lẽ sống", 3)
]

level_map = ["en_daily_beginner", "en_daily_intermediate", "en_daily_advanced", "en_daily_expert"]

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

print("Successfully appended 120 words for Daily Life (English) to DB!")
