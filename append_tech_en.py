import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_tech_beginner", "languageCode": "en", "title": "Công nghệ", "subtitle": "Công nghệ - Beginner", "iconEmoji": "💻", "level": "Beginner", "colorHex": "#8B5CF6", "cardCount": 30},
    {"id": "en_tech_intermediate", "languageCode": "en", "title": "Công nghệ", "subtitle": "Công nghệ - Intermediate", "iconEmoji": "💻", "level": "Intermediate", "colorHex": "#7C3AED", "cardCount": 30},
    {"id": "en_tech_advanced", "languageCode": "en", "title": "Công nghệ", "subtitle": "Công nghệ - Advanced", "iconEmoji": "💻", "level": "Advanced", "colorHex": "#6D28D9", "cardCount": 30},
    {"id": "en_tech_expert", "languageCode": "en", "title": "Công nghệ", "subtitle": "Công nghệ - Expert", "iconEmoji": "💻", "level": "Expert", "colorHex": "#5B21B6", "cardCount": 30}
]

vocab_data = [
    # Beginner (0)
    ("Computer", "/kəmˈpjuː.tər/", "noun", "I use a computer for work.", "Máy tính", "Tôi sử dụng máy tính để làm việc.", "Máy điện toán", 0),
    ("Phone", "/fəʊn/", "noun", "My phone is ringing.", "Điện thoại", "Điện thoại của tôi đang đổ chuông.", "Điện thoại di động", 0),
    ("Screen", "/skriːn/", "noun", "A flat screen.", "Màn hình", "Một màn hình phẳng.", "Phần hiển thị", 0),
    ("Mouse", "/maʊs/", "noun", "Use the mouse to click.", "Chuột máy tính", "Dùng chuột để nhấp.", "Thiết bị di chuyển trỏ", 0),
    ("Keyboard", "/ˈkiː.bɔːd/", "noun", "Type on the keyboard.", "Bàn phím", "Gõ trên bàn phím.", "Bảng phím gõ", 0),
    ("Internet", "/ˈɪn.tə.net/", "noun", "Surf the internet.", "Mạng internet", "Lướt mạng internet.", "Mạng toàn cầu", 0),
    ("Website", "/ˈweb.saɪt/", "noun", "Visit our website.", "Trang web", "Truy cập trang web của chúng tôi.", "Địa chỉ trên mạng", 0),
    ("App", "/æp/", "noun", "Download the app.", "Ứng dụng", "Tải ứng dụng.", "Phần mềm di động", 0),
    ("Game", "/ɡeɪm/", "noun", "Play a video game.", "Trò chơi", "Chơi một trò chơi điện tử.", "Trò giải trí", 0),
    ("Video", "/ˈvɪd.i.əʊ/", "noun", "Watch a funny video.", "Video", "Xem một video hài hước.", "Đoạn phim", 0),
    ("Photo", "/ˈfəʊ.təʊ/", "noun", "Take a photo.", "Bức ảnh", "Chụp một bức ảnh.", "Hình chụp", 0),
    ("Camera", "/ˈkæm.rə/", "noun", "A digital camera.", "Máy ảnh", "Một máy ảnh kỹ thuật số.", "Máy chụp hình", 0),
    ("Message", "/ˈmes.ɪdʒ/", "noun", "Send a text message.", "Tin nhắn", "Gửi một tin nhắn văn bản.", "Đoạn chữ gửi đi", 0),
    ("Email", "/ˈiː.meɪl/", "noun", "Check your email.", "Thư điện tử", "Kiểm tra thư điện tử của bạn.", "Thư trên mạng", 0),
    ("Password", "/ˈpɑːs.wɜːd/", "noun", "Enter your password.", "Mật khẩu", "Nhập mật khẩu của bạn.", "Mã bảo mật", 0),
    ("File", "/faɪl/", "noun", "Save the file.", "Tệp tin", "Lưu tệp tin.", "Tài liệu máy tính", 0),
    ("Folder", "/ˈfəʊl.dər/", "noun", "Create a new folder.", "Thư mục", "Tạo một thư mục mới.", "Chỗ chứa tệp tin", 0),
    ("Click", "/klɪk/", "verb", "Click the button.", "Nhấn chuột", "Nhấp vào nút.", "Bấm chuột", 0),
    ("Type", "/taɪp/", "verb", "Type your name.", "Gõ phím", "Gõ tên của bạn.", "Nhập chữ", 0),
    ("Send", "/send/", "verb", "Send an email.", "Gửi", "Gửi một email.", "Chuyển đi", 0),
    ("Save", "/seɪv/", "verb", "Save your work.", "Lưu", "Lưu công việc của bạn.", "Giữ lại dữ liệu", 0),
    ("Delete", "/dɪˈliːt/", "verb", "Delete the picture.", "Xóa", "Xóa bức ảnh.", "Bỏ đi", 0),
    ("Search", "/sɜːtʃ/", "verb", "Search on Google.", "Tìm kiếm", "Tìm kiếm trên Google.", "Tìm thông tin", 0),
    ("Online", "/ˌɒnˈlaɪn/", "adjective", "Buy things online.", "Trực tuyến", "Mua đồ trực tuyến.", "Kết nối mạng", 0),
    ("Offline", "/ˌɒfˈlaɪn/", "adjective", "Play offline games.", "Ngoại tuyến", "Chơi game ngoại tuyến.", "Không kết nối mạng", 0),
    ("Battery", "/ˈbæt.ər.i/", "noun", "Low battery.", "Pin", "Pin yếu.", "Nguồn điện", 0),
    ("Charger", "/ˈtʃɑː.dʒər/", "noun", "Where is my charger?", "Cục sạc", "Cục sạc của tôi ở đâu?", "Thiết bị nạp điện", 0),
    ("Cable", "/ˈkeɪ.bəl/", "noun", "A USB cable.", "Dây cáp", "Một dây cáp USB.", "Dây nối", 0),
    ("Printer", "/ˈprɪn.tər/", "noun", "Print a document on the printer.", "Máy in", "In tài liệu trên máy in.", "Máy xuất giấy", 0),
    ("Laptop", "/ˈlæp.tɒp/", "noun", "I carry my laptop everywhere.", "Máy tính xách tay", "Tôi mang máy tính xách tay đi mọi nơi.", "Máy tính gập", 0),

    # Intermediate (1)
    ("Software", "/ˈsɒft.weər/", "noun", "Install new software.", "Phần mềm", "Cài đặt phần mềm mới.", "Chương trình máy tính", 1),
    ("Hardware", "/ˈhɑːd.weər/", "noun", "Computer hardware.", "Phần cứng", "Phần cứng máy tính.", "Thiết bị vật lý", 1),
    ("Network", "/ˈnet.wɜːk/", "noun", "A wireless network.", "Mạng lưới", "Một mạng lưới không dây.", "Kết nối các máy", 1),
    ("Wireless", "/ˈwaɪə.ləs/", "adjective", "Wireless connection.", "Không dây", "Kết nối không dây.", "Dùng sóng truyền", 1),
    ("Download", "/ˌdaʊnˈləʊd/", "verb", "Download a song.", "Tải xuống", "Tải xuống một bài hát.", "Lấy từ mạng về", 1),
    ("Upload", "/ˌʌpˈləʊd/", "verb", "Upload a video.", "Tải lên", "Tải lên một video.", "Đưa lên mạng", 1),
    ("Install", "/ɪnˈstɔːl/", "verb", "Install the app.", "Cài đặt", "Cài đặt ứng dụng.", "Thiết lập phần mềm", 1),
    ("Update", "/ʌpˈdeɪt/", "verb", "Update your system.", "Cập nhật", "Cập nhật hệ thống của bạn.", "Làm mới, phiên bản mới", 1),
    ("Upgrade", "/ʌpˈɡreɪd/", "verb", "Upgrade your account.", "Nâng cấp", "Nâng cấp tài khoản của bạn.", "Lên đời cao hơn", 1),
    ("Server", "/ˈsɜː.vər/", "noun", "The server is down.", "Máy chủ", "Máy chủ đang gặp sự cố.", "Máy tính trung tâm", 1),
    ("Database", "/ˈdeɪ.tə.beɪs/", "noun", "Customer database.", "Cơ sở dữ liệu", "Cơ sở dữ liệu khách hàng.", "Kho chứa dữ liệu", 1),
    ("Browser", "/ˈbraʊ.zər/", "noun", "Open a web browser.", "Trình duyệt", "Mở một trình duyệt web.", "Phần mềm lướt web", 1),
    ("Link", "/lɪŋk/", "noun", "Click the link.", "Liên kết", "Nhấp vào liên kết.", "Đường dẫn", 1),
    ("Virus", "/ˈvaɪə.rəs/", "noun", "A computer virus.", "Vi-rút máy tính", "Một vi-rút máy tính.", "Mã độc", 1),
    ("Antivirus", "/ˌæn.tiˈvaɪə.rəs/", "noun", "Antivirus software.", "Phần mềm diệt vi-rút", "Phần mềm diệt vi-rút.", "Chống mã độc", 1),
    ("Hacker", "/ˈhæk.ər/", "noun", "A dangerous hacker.", "Tin tặc", "Một tin tặc nguy hiểm.", "Kẻ xâm nhập mạng", 1),
    ("Security", "/sɪˈkjʊə.rə.ti/", "noun", "Cyber security.", "Bảo mật", "Bảo mật mạng (an ninh mạng).", "Sự an toàn thông tin", 1),
    ("Privacy", "/ˈprɪv.ə.si/", "noun", "Privacy policy.", "Quyền riêng tư", "Chính sách quyền riêng tư.", "Bảo mật cá nhân", 1),
    ("Account", "/əˈkaʊnt/", "noun", "Create an account.", "Tài khoản", "Tạo một tài khoản.", "Định danh người dùng", 1),
    ("Profile", "/ˈprəʊ.faɪl/", "noun", "Update your profile.", "Hồ sơ cá nhân", "Cập nhật hồ sơ cá nhân của bạn.", "Thông tin cá nhân", 1),
    ("Login", "/ˈlɒɡ.ɪn/", "verb", "Login to your account.", "Đăng nhập", "Đăng nhập vào tài khoản của bạn.", "Truy cập hệ thống", 1),
    ("Logout", "/ˈlɒɡ.aʊt/", "verb", "Remember to logout.", "Đăng xuất", "Hãy nhớ đăng xuất.", "Thoát hệ thống", 1),
    ("Tablet", "/ˈtæb.lət/", "noun", "Read on a tablet.", "Máy tính bảng", "Đọc sách trên máy tính bảng.", "Màn hình cảm ứng to", 1),
    ("Device", "/dɪˈvaɪs/", "noun", "A smart device.", "Thiết bị", "Một thiết bị thông minh.", "Công cụ điện tử", 1),
    ("Gadget", "/ˈɡædʒ.ɪt/", "noun", "A useful gadget.", "Tiện ích, thiết bị nhỏ", "Một tiện ích hữu dụng.", "Đồ chơi công nghệ", 1),
    ("Reboot", "/ˌriːˈbuːt/", "verb", "Reboot the computer.", "Khởi động lại", "Khởi động lại máy tính.", "Bật lại máy", 1),
    ("Crash", "/kræʃ/", "verb", "My computer crashed.", "Sập, ngừng hoạt động", "Máy tính của tôi bị sập.", "Đứng máy", 1),
    ("Backup", "/ˈbæk.ʌp/", "noun", "Data backup.", "Sao lưu", "Sao lưu dữ liệu.", "Lưu trữ dự phòng", 1),
    ("Storage", "/ˈstɔː.rɪdʒ/", "noun", "Cloud storage.", "Lưu trữ", "Lưu trữ đám mây.", "Bộ nhớ chứa đồ", 1),
    ("Cloud", "/klaʊd/", "noun", "Save it in the cloud.", "Điện toán đám mây", "Lưu nó trên đám mây.", "Dữ liệu trên mạng", 1),

    # Advanced (2)
    ("Algorithm", "/ˈæl.ɡə.rɪ.ðəm/", "noun", "A complex algorithm.", "Thuật toán", "Một thuật toán phức tạp.", "Cách giải quyết", 2),
    ("Programming", "/ˈprəʊ.ɡræm.ɪŋ/", "noun", "Learn programming.", "Lập trình", "Học lập trình.", "Viết mã code", 2),
    ("Developer", "/dɪˈvel.ə.pər/", "noun", "A software developer.", "Nhà phát triển", "Một nhà phát triển phần mềm.", "Lập trình viên", 2),
    ("Interface", "/ˈɪn.tə.feɪs/", "noun", "User interface.", "Giao diện", "Giao diện người dùng.", "Tương tác người máy", 2),
    ("Application", "/ˌæp.lɪˈkeɪ.ʃən/", "noun", "A web application.", "Ứng dụng", "Một ứng dụng web.", "Phần mềm ứng dụng", 2),
    ("Operating system", "/ˈɒp.ər.eɪ.tɪŋ ˌsɪs.təm/", "noun", "A fast operating system.", "Hệ điều hành", "Một hệ điều hành nhanh.", "Phần mềm nền tảng", 2),
    ("Bandwidth", "/ˈbænd.wɪdθ/", "noun", "High bandwidth.", "Băng thông", "Băng thông cao.", "Dung lượng truyền", 2),
    ("Broadband", "/ˈbrɔːd.bænd/", "noun", "Broadband internet.", "Mạng băng thông rộng", "Internet băng thông rộng.", "Kết nối tốc độ cao", 2),
    ("Ethernet", "/ˈiː.θə.net/", "noun", "An Ethernet cable.", "Mạng cục bộ (có dây)", "Một dây cáp mạng nội bộ.", "Mạng LAN có dây", 2),
    ("Firewall", "/ˈfaɪə.wɔːl/", "noun", "A strong firewall.", "Tường lửa", "Một tường lửa mạnh.", "Chắn bảo vệ", 2),
    ("Encryption", "/ɪnˈkrɪp.ʃən/", "noun", "Data encryption.", "Mã hóa", "Sự mã hóa dữ liệu.", "Biến dữ liệu thành mã", 2),
    ("Decryption", "/dɪˈkrɪp.ʃən/", "noun", "Data decryption.", "Giải mã", "Sự giải mã dữ liệu.", "Đưa mã về ban đầu", 2),
    ("Authentication", "/ɔːˌθen.tɪˈkeɪ.ʃən/", "noun", "Two-factor authentication.", "Xác thực", "Xác thực hai yếu tố.", "Kiểm tra danh tính", 2),
    ("Authorization", "/ˌɔː.θər.aɪˈzeɪ.ʃən/", "noun", "Require authorization.", "Cấp quyền", "Yêu cầu cấp quyền.", "Cho phép truy cập", 2),
    ("Vulnerability", "/ˌvʌl.nər.əˈbɪl.ə.ti/", "noun", "A security vulnerability.", "Lỗ hổng bảo mật", "Một lỗ hổng bảo mật.", "Điểm yếu hệ thống", 2),
    ("Malware", "/ˈmæl.weər/", "noun", "Remove malware.", "Phần mềm độc hại", "Gỡ bỏ phần mềm độc hại.", "Mã độc", 2),
    ("Spyware", "/ˈspaɪ.weər/", "noun", "Detect spyware.", "Phần mềm gián điệp", "Phát hiện phần mềm gián điệp.", "Theo dõi lén", 2),
    ("Phishing", "/ˈfɪʃ.ɪŋ/", "noun", "A phishing scam.", "Lừa đảo qua mạng", "Một vụ lừa đảo qua mạng.", "Câu mồi lấy thông tin", 2),
    ("Spam", "/spæm/", "noun", "A spam folder.", "Thư rác", "Một thư mục thư rác.", "Thư không mong muốn", 2),
    ("Troubleshooting", "/ˈtrʌb.əlˌʃuː.tɪŋ/", "noun", "Troubleshooting guide.", "Xử lý sự cố", "Hướng dẫn xử lý sự cố.", "Gỡ rối lỗi", 2),
    ("Debug", "/ˌdiːˈbʌɡ/", "verb", "Debug the code.", "Gỡ lỗi", "Gỡ lỗi mã nguồn.", "Tìm và sửa lỗi code", 2),
    ("Compile", "/kəmˈpaɪl/", "verb", "Compile the program.", "Biên dịch", "Biên dịch chương trình.", "Dịch code sang mã máy", 2),
    ("Execute", "/ˈek.sɪ.kjuːt/", "verb", "Execute a command.", "Thực thi", "Thực thi một câu lệnh.", "Chạy lệnh", 2),
    ("Deploy", "/dɪˈplɔɪ/", "verb", "Deploy a server.", "Triển khai", "Triển khai một máy chủ.", "Đưa vào hoạt động", 2),
    ("Integration", "/ˌɪn.tɪˈɡreɪ.ʃən/", "noun", "System integration.", "Sự tích hợp", "Sự tích hợp hệ thống.", "Gắn kết các phần", 2),
    ("Automation", "/ˌɔː.təˈmeɪ.ʃən/", "noun", "Process automation.", "Tự động hóa", "Tự động hóa quy trình.", "Làm tự động", 2),
    ("Virtualization", "/ˌvɜː.tʃu.ə.laɪˈzeɪ.ʃən/", "noun", "Server virtualization.", "Ảo hóa", "Ảo hóa máy chủ.", "Tạo môi trường ảo", 2),
    ("Protocol", "/ˈprəʊ.tə.kɒl/", "noun", "Internet protocol.", "Giao thức", "Giao thức internet.", "Quy tắc truyền thông", 2),
    ("Repository", "/rɪˈpɒz.ɪ.tər.i/", "noun", "A code repository.", "Kho lưu trữ", "Một kho lưu trữ mã nguồn.", "Chỗ chứa code", 2),
    ("Framework", "/ˈfreɪm.wɜːk/", "noun", "A testing framework.", "Khuôn khổ phần mềm", "Một bộ khung kiểm thử.", "Khung làm việc", 2),

    # Expert (3)
    ("Artificial Intelligence", "/ˌɑː.tɪˈfɪʃ.əl ɪnˈtel.ɪ.dʒəns/", "noun", "AI is growing fast.", "Trí tuệ nhân tạo (AI)", "Trí tuệ nhân tạo đang phát triển nhanh chóng.", "Trí tuệ do máy", 3),
    ("Machine learning", "/məˈʃiːn ˌlɜː.nɪŋ/", "noun", "A machine learning model.", "Học máy", "Một mô hình học máy.", "Máy tự học", 3),
    ("Deep learning", "/diːp ˈlɜː.nɪŋ/", "noun", "Deep learning networks.", "Học sâu", "Các mạng học sâu.", "Học máy phức tạp", 3),
    ("Neural network", "/ˌnjʊə.rəl ˈnet.wɜːk/", "noun", "Train a neural network.", "Mạng nơ-ron", "Huấn luyện một mạng nơ-ron.", "Mô phỏng não người", 3),
    ("Blockchain", "/ˈblɒk.tʃeɪn/", "noun", "Blockchain technology.", "Chuỗi khối", "Công nghệ chuỗi khối.", "Sổ cái phi tập trung", 3),
    ("Cryptocurrency", "/ˈkrɪp.təʊˌkʌr.ən.si/", "noun", "Trade cryptocurrency.", "Tiền điện tử", "Giao dịch tiền điện tử.", "Tiền mã hóa", 3),
    ("Cryptography", "/krɪpˈtɒɡ.rə.fi/", "noun", "Modern cryptography.", "Mật mã học", "Mật mã học hiện đại.", "Khoa học mã hóa", 3),
    ("Quantum computing", "/ˈkwɒn.təm kəmˌpjuː.tɪŋ/", "noun", "Quantum computing is the future.", "Điện toán lượng tử", "Điện toán lượng tử là tương lai.", "Máy tính siêu việt", 3),
    ("Nanotechnology", "/ˌnæn.əʊ.tekˈnɒl.ə.dʒi/", "noun", "Uses of nanotechnology.", "Công nghệ nano", "Các ứng dụng của công nghệ nano.", "Công nghệ siêu nhỏ", 3),
    ("Biotechnology", "/ˌbaɪ.əʊ.tekˈnɒl.ə.dʒi/", "noun", "Biotechnology research.", "Công nghệ sinh học", "Nghiên cứu công nghệ sinh học.", "Sinh học ứng dụng", 3),
    ("Augmented reality", "/ɔːɡˌmen.tɪd riˈæl.ə.ti/", "noun", "AR glasses.", "Thực tế tăng cường (AR)", "Kính thực tế tăng cường.", "Chèn ảo vào thực", 3),
    ("Virtual reality", "/ˌvɜː.tʃu.əl riˈæl.ə.ti/", "noun", "VR headset.", "Thực tế ảo (VR)", "Kính thực tế ảo.", "Môi trường hoàn toàn ảo", 3),
    ("Internet of Things", "/ˈɪn.tə.net əv θɪŋz/", "noun", "IoT devices.", "Internet vạn vật (IoT)", "Các thiết bị IoT.", "Vạn vật kết nối mạng", 3),
    ("Big data", "/bɪɡ ˈdeɪ.tə/", "noun", "Big data analytics.", "Dữ liệu lớn", "Phân tích dữ liệu lớn.", "Khối dữ liệu khổng lồ", 3),
    ("Cloud native", "/klaʊd ˈneɪ.tɪv/", "adjective", "A cloud native application.", "Ứng dụng đám mây gốc", "Một ứng dụng xây dựng riêng cho đám mây.", "Gốc đám mây", 3),
    ("Microservices", "/ˈmaɪ.krəʊˌsɜː.vɪs.ɪz/", "noun", "Microservices architecture.", "Vi dịch vụ", "Kiến trúc vi dịch vụ.", "Dịch vụ cực nhỏ", 3),
    ("Containerization", "/kənˌteɪ.nər.aɪˈzeɪ.ʃən/", "noun", "Containerization simplifies deployment.", "Đóng gói container", "Đóng gói ứng dụng giúp đơn giản hóa việc triển khai.", "Đóng gói môi trường", 3),
    ("Orchestration", "/ˌɔː.kɪˈstreɪ.ʃən/", "noun", "Container orchestration.", "Sự điều phối", "Sự điều phối các container.", "Quản lý và điều phối", 3),
    ("Continuous integration", "/kənˈtɪn.ju.əs ˌɪn.tɪˈɡreɪ.ʃən/", "noun", "Use continuous integration.", "Tích hợp liên tục", "Sử dụng tích hợp liên tục.", "Ghép code tự động", 3),
    ("Continuous delivery", "/kənˈtɪn.ju.əs dɪˈlɪv.ər.i/", "noun", "Continuous delivery pipeline.", "Chuyển giao liên tục", "Đường ống chuyển giao liên tục.", "Đẩy code tự động", 3),
    ("Polymorphism", "/ˌpɒl.iˈmɔː.fɪ.zəm/", "noun", "Polymorphism in OOP.", "Tính đa hình", "Tính đa hình trong lập trình hướng đối tượng.", "Nhiều hình thái", 3),
    ("Encapsulation", "/ɪnˌkæp.sjəˈleɪ.ʃən/", "noun", "Encapsulation protects data.", "Tính đóng gói", "Tính đóng gói bảo vệ dữ liệu.", "Gói gọn dữ liệu", 3),
    ("Inheritance", "/ɪnˈher.ɪ.təns/", "noun", "Class inheritance.", "Tính kế thừa", "Sự kế thừa lớp.", "Thừa hưởng thuộc tính", 3),
    ("Asynchronous", "/eɪˈsɪŋ.krə.nəs/", "adjective", "Asynchronous programming.", "Bất đồng bộ", "Lập trình bất đồng bộ.", "Không chờ nhau", 3),
    ("Concurrency", "/kənˈkʌr.ən.si/", "noun", "Handle concurrency.", "Tính đồng thời", "Xử lý tính đồng thời.", "Nhiều luồng cùng lúc", 3),
    ("Latency", "/ˈleɪ.tən.si/", "noun", "Low latency.", "Độ trễ", "Độ trễ thấp.", "Thời gian trễ", 3),
    ("Throughput", "/ˈθruː.pʊt/", "noun", "High network throughput.", "Thông lượng", "Thông lượng mạng cao.", "Khối lượng truyền", 3),
    ("Scalability", "/ˌskeɪ.ləˈbɪl.ə.ti/", "noun", "System scalability.", "Khả năng mở rộng", "Khả năng mở rộng của hệ thống.", "Dễ dàng phình to", 3),
    ("Refactoring", "/ˌriːˈfæk.tər.ɪŋ/", "noun", "Code refactoring.", "Tái cấu trúc mã", "Tái cấu trúc mã nguồn.", "Làm gọn code", 3),
    ("Heuristics", "/hjʊəˈrɪs.tɪks/", "noun", "Heuristics algorithm.", "Phương pháp phỏng đoán", "Thuật toán phỏng đoán.", "Dùng kinh nghiệm", 3)
]

level_map = ["en_tech_beginner", "en_tech_intermediate", "en_tech_advanced", "en_tech_expert"]

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

print("Successfully appended 120 words for Technology (English) to DB!")
