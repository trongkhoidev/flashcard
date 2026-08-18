import json

decks = [
    {"id": "en_edu_beginner", "languageCode": "en", "title": "Giáo dục", "subtitle": "Chủ đề Giáo dục - Beginner", "iconEmoji": "🎓", "level": "Beginner", "colorHex": "#3B82F6", "cardCount": 30},
    {"id": "en_edu_intermediate", "languageCode": "en", "title": "Giáo dục", "subtitle": "Chủ đề Giáo dục - Intermediate", "iconEmoji": "🎓", "level": "Intermediate", "colorHex": "#10B981", "cardCount": 30},
    {"id": "en_edu_advanced", "languageCode": "en", "title": "Giáo dục", "subtitle": "Chủ đề Giáo dục - Advanced", "iconEmoji": "🎓", "level": "Advanced", "colorHex": "#F59E0B", "cardCount": 30},
    {"id": "en_edu_expert", "languageCode": "en", "title": "Giáo dục", "subtitle": "Chủ đề Giáo dục - Expert", "iconEmoji": "🎓", "level": "Expert", "colorHex": "#EF4444", "cardCount": 30}
]

vocab_data = [
    # Beginner
    ("Student", "/ˈstjuː.dənt/", "noun", "He is a good student.", "Học sinh, sinh viên", "Anh ấy là một học sinh giỏi.", "Stud-ent (người đang học)", 0),
    ("Teacher", "/ˈtiː.tʃər/", "noun", "The teacher is very kind.", "Giáo viên", "Giáo viên rất tốt bụng.", "Teach + er (người dạy)", 0),
    ("School", "/skuːl/", "noun", "I go to school every day.", "Trường học", "Tôi đến trường mỗi ngày.", "Nơi để học", 0),
    ("Classroom", "/ˈklɑːs.ruːm/", "noun", "The classroom is large.", "Phòng học", "Phòng học rất lớn.", "Class + room", 0),
    ("Homework", "/ˈhəʊm.wɜːk/", "noun", "I do my homework at night.", "Bài tập về nhà", "Tôi làm bài tập về nhà vào buổi tối.", "Home + work", 0),
    ("Pen", "/pen/", "noun", "I need a pen to write.", "Cái bút bi", "Tôi cần một cái bút để viết.", "Bút viết", 0),
    ("Pencil", "/ˈpen.səl/", "noun", "Use a pencil to draw.", "Bút chì", "Dùng bút chì để vẽ.", "Bút chì", 0),
    ("Book", "/bʊk/", "noun", "She is reading a book.", "Quyển sách", "Cô ấy đang đọc sách.", "Sách", 0),
    ("Notebook", "/ˈnəʊt.bʊk/", "noun", "Write it in your notebook.", "Vở ghi chép", "Hãy viết nó vào vở của bạn.", "Note + book", 0),
    ("Desk", "/desk/", "noun", "Sit at your desk.", "Bàn học", "Hãy ngồi vào bàn học.", "Bàn làm việc/học", 0),
    ("Chair", "/tʃeər/", "noun", "The chair is comfortable.", "Cái ghế", "Cái ghế này rất thoải mái.", "Ghế ngồi", 0),
    ("Board", "/bɔːd/", "noun", "Look at the board.", "Cái bảng", "Hãy nhìn lên bảng.", "Bảng viết", 0),
    ("Eraser", "/ɪˈreɪ.zər/", "noun", "I need an eraser.", "Cục tẩy", "Tôi cần một cục tẩy.", "Erase + er (đồ để xóa)", 0),
    ("Ruler", "/ˈruː.lər/", "noun", "Use a ruler for straight lines.", "Cái thước kẻ", "Dùng thước để kẻ đường thẳng.", "Rule + er", 0),
    ("Bag", "/bæɡ/", "noun", "My bag is heavy.", "Cặp sách, túi", "Cặp sách của tôi rất nặng.", "Túi đựng", 0),
    ("Lesson", "/ˈles.ən/", "noun", "The lesson is interesting.", "Bài học", "Bài học rất thú vị.", "Bài học", 0),
    ("Test", "/test/", "noun", "We have a math test today.", "Bài kiểm tra (nhỏ)", "Chúng ta có bài kiểm tra toán hôm nay.", "Kiểm tra", 0),
    ("Exam", "/ɪɡˈzæm/", "noun", "The final exam is hard.", "Kỳ thi, bài thi", "Bài thi cuối kỳ rất khó.", "Viết tắt của Examination", 0),
    ("Grade", "/ɡreɪd/", "noun", "She got a good grade.", "Điểm số, lớp", "Cô ấy đạt điểm cao.", "Mức độ/điểm", 0),
    ("Mark", "/mɑːk/", "noun", "His marks are excellent.", "Điểm số", "Điểm số của anh ấy rất xuất sắc.", "Dấu mốc/điểm", 0),
    ("Class", "/klɑːs/", "noun", "The class starts at 8 AM.", "Lớp học, tiết học", "Tiết học bắt đầu lúc 8 giờ sáng.", "Lớp/nhóm", 0),
    ("Subject", "/ˈsʌb.dʒekt/", "noun", "Math is my favorite subject.", "Môn học", "Toán là môn học yêu thích của tôi.", "Chủ đề/môn", 0),
    ("Math", "/mæθ/", "noun", "Math is numbers.", "Môn Toán", "Toán học là về các con số.", "Mathematics", 0),
    ("Science", "/ˈsaɪ.əns/", "noun", "We learn about nature in science.", "Môn Khoa học", "Chúng ta học về tự nhiên trong môn khoa học.", "Khoa học", 0),
    ("History", "/ˈhɪs.tər.i/", "noun", "History is about the past.", "Môn Lịch sử", "Lịch sử là về quá khứ.", "Chuyện xưa", 0),
    ("Art", "/ɑːt/", "noun", "I like to draw in art class.", "Môn Mỹ thuật", "Tôi thích vẽ trong giờ mỹ thuật.", "Nghệ thuật", 0),
    ("Music", "/ˈmjuː.zɪk/", "noun", "We sing in music class.", "Môn Âm nhạc", "Chúng tôi hát trong giờ âm nhạc.", "Âm nhạc", 0),
    ("Read", "/riːd/", "verb", "I read books every day.", "Đọc", "Tôi đọc sách mỗi ngày.", "Đọc chữ", 0),
    ("Write", "/raɪt/", "verb", "Write your name here.", "Viết", "Hãy viết tên bạn vào đây.", "Viết chữ", 0),
    ("Learn", "/lɜːn/", "verb", "I want to learn English.", "Học", "Tôi muốn học tiếng Anh.", "Tiếp thu kiến thức", 0),

    # Intermediate
    ("University", "/ˌjuː.nɪˈvɜː.sə.ti/", "noun", "She studies at the university.", "Trường đại học", "Cô ấy học ở trường đại học.", "Đại học", 1),
    ("College", "/ˈkɒl.ɪdʒ/", "noun", "He is in college.", "Trường cao đẳng/đại học", "Anh ấy đang học cao đẳng.", "Cao đẳng", 1),
    ("Professor", "/prəˈfes.ər/", "noun", "The professor gives a lecture.", "Giáo sư", "Giáo sư đang giảng bài.", "Giảng viên đại học", 1),
    ("Lecture", "/ˈlek.tʃər/", "noun", "The lecture was very long.", "Bài giảng", "Bài giảng rất dài.", "Giảng bài", 1),
    ("Assignment", "/əˈsaɪn.mənt/", "noun", "Finish your assignment by Friday.", "Bài tập được giao", "Hoàn thành bài tập trước thứ Sáu.", "Assign + ment", 1),
    ("Project", "/ˈprɒdʒ.ekt/", "noun", "We are working on a group project.", "Dự án", "Chúng tôi đang làm dự án nhóm.", "Công trình", 1),
    ("Presentation", "/ˌprez.ənˈteɪ.ʃən/", "noun", "He gave a great presentation.", "Bài thuyết trình", "Anh ấy đã có một bài thuyết trình tuyệt vời.", "Present + ation", 1),
    ("Semester", "/sɪˈmes.tər/", "noun", "This is the first semester.", "Học kỳ", "Đây là học kỳ đầu tiên.", "Kỳ học", 1),
    ("Term", "/tɜːm/", "noun", "The spring term ends in June.", "Học kỳ, thuật ngữ", "Học kỳ mùa xuân kết thúc vào tháng 6.", "Kỳ hạn", 1),
    ("Diploma", "/dɪˈpləʊ.mə/", "noun", "She received her high school diploma.", "Bằng cấp, chứng chỉ", "Cô ấy nhận được bằng tốt nghiệp trung học.", "Bằng cấp", 1),
    ("Degree", "/dɪˈɡriː/", "noun", "He has a degree in biology.", "Bằng đại học/cử nhân", "Anh ấy có bằng cử nhân sinh học.", "Trình độ/bằng", 1),
    ("Graduate", "/ˈɡrædʒ.u.ət/", "verb", "I will graduate next year.", "Tốt nghiệp", "Tôi sẽ tốt nghiệp vào năm sau.", "Ra trường", 1),
    ("Undergraduate", "/ˌʌn.dəˈɡrædʒ.u.ət/", "noun", "He is an undergraduate student.", "Sinh viên chưa tốt nghiệp", "Anh ấy là sinh viên đại học.", "Under + graduate", 1),
    ("Scholarship", "/ˈskɒl.ə.ʃɪp/", "noun", "She won a full scholarship.", "Học bổng", "Cô ấy giành được học bổng toàn phần.", "Scholar + ship", 1),
    ("Campus", "/ˈkæm.pəs/", "noun", "The campus is very beautiful.", "Khuôn viên trường", "Khuôn viên trường rất đẹp.", "Khu đất trường", 1),
    ("Library", "/ˈlaɪ.brər.i/", "noun", "I study in the library.", "Thư viện", "Tôi học ở thư viện.", "Nơi chứa sách", 1),
    ("Laboratory", "/ləˈbɒr.ə.tər.i/", "noun", "The chemistry laboratory is well-equipped.", "Phòng thí nghiệm", "Phòng thí nghiệm hóa học được trang bị tốt.", "Lab", 1),
    ("Essay", "/ˈes.eɪ/", "noun", "Write a 500-word essay.", "Bài luận", "Hãy viết một bài luận 500 chữ.", "Bài văn", 1),
    ("Thesis", "/ˈθiː.sɪs/", "noun", "He is writing his master's thesis.", "Luận văn", "Anh ấy đang viết luận văn thạc sĩ.", "Bài khóa luận", 1),
    ("Course", "/kɔːs/", "noun", "This course is very difficult.", "Khóa học", "Khóa học này rất khó.", "Lộ trình học", 1),
    ("Syllabus", "/ˈsɪl.ə.bəs/", "noun", "Read the syllabus carefully.", "Đề cương khóa học", "Hãy đọc kỹ đề cương khóa học.", "Chương trình học", 1),
    ("Tuition", "/tjuːˈɪʃ.ən/", "noun", "The tuition fee is high.", "Học phí", "Học phí rất cao.", "Tiền học", 1),
    ("Enroll", "/ɪnˈrəʊl/", "verb", "I want to enroll in this class.", "Đăng ký nhập học", "Tôi muốn đăng ký vào lớp này.", "Ghi danh", 1),
    ("Attendance", "/əˈten.dəns/", "noun", "Attendance is mandatory.", "Sự điểm danh, có mặt", "Việc điểm danh là bắt buộc.", "Attend + ance", 1),
    ("Review", "/rɪˈvjuː/", "verb", "Review the lesson before the test.", "Ôn tập, xem lại", "Hãy ôn lại bài trước bài kiểm tra.", "Re + view", 1),
    ("Discuss", "/dɪˈskʌs/", "verb", "We will discuss this topic tomorrow.", "Thảo luận", "Chúng ta sẽ thảo luận chủ đề này vào ngày mai.", "Trao đổi", 1),
    ("Evaluate", "/ɪˈvæl.ju.eɪt/", "verb", "The teacher will evaluate your performance.", "Đánh giá", "Giáo viên sẽ đánh giá hiệu suất của bạn.", "Value", 1),
    ("Academic", "/ˌæk.əˈdem.ɪk/", "adjective", "She has a good academic record.", "Thuộc về học thuật", "Cô ấy có thành tích học tập tốt.", "Học thuật", 1),
    ("Major", "/ˈmeɪ.dʒər/", "noun", "My major is computer science.", "Chuyên ngành", "Chuyên ngành của tôi là khoa học máy tính.", "Môn chính", 1),
    ("Minor", "/ˈmaɪ.nər/", "noun", "Her minor is psychology.", "Ngành phụ", "Ngành phụ của cô ấy là tâm lý học.", "Môn phụ", 1),

    # Advanced
    ("Dissertation", "/ˌdɪs.əˈteɪ.ʃən/", "noun", "Writing a doctoral dissertation takes years.", "Luận án tiến sĩ", "Viết luận án tiến sĩ mất nhiều năm.", "Luận văn bậc cao", 2),
    ("Curriculum", "/kəˈrɪk.jə.ləm/", "noun", "The school curriculum includes arts.", "Chương trình giảng dạy", "Chương trình giảng dạy của trường bao gồm cả nghệ thuật.", "Hệ thống môn học", 2),
    ("Pedagogy", "/ˈped.ə.ɡɒdʒ.i/", "noun", "He studies modern pedagogy.", "Sư phạm học, phương pháp giảng dạy", "Anh ấy nghiên cứu phương pháp sư phạm hiện đại.", "Nghệ thuật dạy học", 2),
    ("Accreditation", "/əˌkred.ɪˈteɪ.ʃən/", "noun", "The university lost its accreditation.", "Sự công nhận (chất lượng đào tạo)", "Trường đại học đã bị mất sự công nhận chất lượng.", "Cấp chứng nhận", 2),
    ("Plagiarism", "/ˈpleɪ.dʒər.ɪ.zəm/", "noun", "Plagiarism is strictly forbidden.", "Sự đạo văn", "Việc đạo văn bị nghiêm cấm hoàn toàn.", "Ăn cắp ý tưởng", 2),
    ("Fellowship", "/ˈfel.əʊ.ʃɪp/", "noun", "He was awarded a research fellowship.", "Học bổng nghiên cứu sinh", "Anh ấy được trao học bổng nghiên cứu.", "Nghiên cứu sinh", 2),
    ("Alumnus", "/əˈlʌm.nəs/", "noun", "He is an alumnus of Harvard.", "Cựu sinh viên (nam)", "Anh ấy là cựu sinh viên của Harvard.", "Sinh viên cũ", 2),
    ("Transcript", "/ˈtræn.skrɪpt/", "noun", "Please submit your official transcript.", "Bảng điểm", "Vui lòng nộp bảng điểm chính thức của bạn.", "Bản sao điểm", 2),
    ("Tenure", "/ˈten.jər/", "noun", "The professor was granted tenure.", "Sự biên chế (giáo sư)", "Vị giáo sư đã được cấp biên chế trọn đời.", "Quyền tại vị", 2),
    ("Sabbatical", "/səˈbæt.ɪ.kəl/", "noun", "She is on a sabbatical leave to write a book.", "Kỳ nghỉ phép (của giảng viên)", "Cô ấy đang trong kỳ nghỉ phép để viết sách.", "Năm nghỉ ngơi", 2),
    ("Seminar", "/ˈsem.ɪ.nɑːr/", "noun", "I attended a seminar on education.", "Hội thảo chuyên đề", "Tôi đã tham dự một hội thảo về giáo dục.", "Lớp nghiên cứu", 2),
    ("Colloquium", "/kəˈləʊ.kwi.əm/", "noun", "The physics colloquium is held on Fridays.", "Buổi hội đàm học thuật", "Buổi hội đàm vật lý được tổ chức vào các ngày thứ Sáu.", "Thảo luận nhóm", 2),
    ("Prerequisite", "/ˌpriːˈrek.wɪ.zɪt/", "noun", "Algebra is a prerequisite for Calculus.", "Điều kiện tiên quyết", "Đại số là môn tiên quyết cho Giải tích.", "Pre + requisite", 2),
    ("Extracurricular", "/ˌek.strə.kəˈrɪk.jə.lər/", "adjective", "She participates in many extracurricular activities.", "Ngoại khóa", "Cô ấy tham gia nhiều hoạt động ngoại khóa.", "Ngoài chương trình", 2),
    ("Valedictorian", "/ˌvæl.ə.dɪkˈtɔː.ri.ən/", "noun", "The valedictorian gave a moving speech.", "Thủ khoa", "Thủ khoa đã có một bài phát biểu cảm động.", "Đại diện đọc diễn văn", 2),
    ("Prodigy", "/ˈprɒd.ɪ.dʒi/", "noun", "He was a mathematical prodigy.", "Thần đồng", "Anh ấy từng là một thần đồng toán học.", "Tài năng trẻ", 2),
    ("Literacy", "/ˈlɪt.ər.ə.si/", "noun", "The country has a high literacy rate.", "Sự biết chữ", "Quốc gia này có tỷ lệ biết chữ cao.", "Khả năng đọc viết", 2),
    ("Numeracy", "/ˈnjuː.mər.ə.si/", "noun", "Basic numeracy skills are essential.", "Kỹ năng toán học cơ bản", "Kỹ năng tính toán cơ bản là rất cần thiết.", "Khả năng tính toán", 2),
    ("Cognition", "/kɒɡˈnɪʃ.ən/", "noun", "Learning a language improves cognition.", "Nhận thức", "Học ngôn ngữ giúp cải thiện nhận thức.", "Quá trình tư duy", 2),
    ("Methodology", "/ˌmeθ.əˈdɒl.ə.dʒi/", "noun", "We need to clearly define our research methodology.", "Phương pháp luận", "Chúng ta cần xác định rõ phương pháp luận nghiên cứu.", "Hệ thống phương pháp", 2),
    ("Hypothesis", "/haɪˈpɒθ.ə.sɪs/", "noun", "The experiment proved his hypothesis.", "Giả thuyết", "Cuộc thí nghiệm đã chứng minh giả thuyết của anh ấy.", "Giả định", 2),
    ("Empirical", "/ɪmˈpɪr.ɪ.kəl/", "adjective", "We need empirical evidence.", "Dựa trên kinh nghiệm, thực nghiệm", "Chúng ta cần bằng chứng thực nghiệm.", "Từ thực tế", 2),
    ("Qualitative", "/ˈkwɒl.ɪ.tə.tɪv/", "adjective", "This is a qualitative study.", "Định tính", "Đây là một nghiên cứu định tính.", "Chất lượng", 2),
    ("Quantitative", "/ˈkwɒn.tɪ.tə.tɪv/", "adjective", "Quantitative analysis involves numbers.", "Định lượng", "Phân tích định lượng liên quan đến các con số.", "Số lượng", 2),
    ("Synthesis", "/ˈsɪn.θə.sɪs/", "noun", "His book is a synthesis of previous ideas.", "Sự tổng hợp", "Cuốn sách của anh ấy là sự tổng hợp các ý tưởng trước đó.", "Hợp nhất", 2),
    ("Analysis", "/əˈnæl.ə.sɪs/", "noun", "Detailed analysis is required.", "Sự phân tích", "Cần phải phân tích chi tiết.", "Phân tích", 2),
    ("Abstract", "/ˈæb.strækt/", "noun", "Read the abstract before the full paper.", "Bản tóm tắt (nghiên cứu)", "Hãy đọc bản tóm tắt trước khi đọc toàn văn.", "Trừu tượng/tóm tắt", 2),
    ("Peer-review", "/pɪər rɪˈvjuː/", "noun", "The journal uses a strict peer-review process.", "Đánh giá đồng cấp", "Tạp chí áp dụng quy trình đánh giá đồng cấp nghiêm ngặt.", "Đồng nghiệp đánh giá", 2),
    ("Citation", "/saɪˈteɪ.ʃən/", "noun", "You must include proper citations.", "Sự trích dẫn", "Bạn phải bao gồm các trích dẫn đúng chuẩn.", "Trích dẫn tài liệu", 2),
    ("Bibliography", "/ˌbɪb.liˈɒɡ.rə.fi/", "noun", "The bibliography is at the end of the book.", "Tài liệu tham khảo", "Danh mục tài liệu tham khảo nằm ở cuối sách.", "Danh sách sách", 2),

    # Expert
    ("Epistemology", "/ɪˌpɪs.təˈmɒl.ə.dʒi/", "noun", "Epistemology explores the nature of knowledge.", "Nhận thức luận", "Nhận thức luận khám phá bản chất của tri thức.", "Lý thuyết về kiến thức", 3),
    ("Heuristics", "/hjʊəˈrɪs.tɪks/", "noun", "Heuristics allow for quick problem-solving.", "Phương pháp tự khám phá (Heuristic)", "Phương pháp Heuristic cho phép giải quyết vấn đề nhanh chóng.", "Học hỏi qua thực hành", 3),
    ("Andragogy", "/ˈæn.drəˌɡɒdʒ.i/", "noun", "Andragogy focuses on adult learning principles.", "Phương pháp giáo dục người lớn", "Andragogy tập trung vào các nguyên tắc học tập của người trưởng thành.", "Giáo dục người lớn", 3),
    ("Constructivism", "/kənˈstrʌk.tɪ.vɪ.zəm/", "noun", "Constructivism suggests learners build their own understanding.", "Thuyết kiến tạo", "Thuyết kiến tạo cho rằng người học tự xây dựng sự hiểu biết của mình.", "Xây dựng kiến thức", 3),
    ("Didactic", "/daɪˈdæk.tɪk/", "adjective", "His teaching style is highly didactic.", "Có tính giáo huấn, giáo khoa", "Phong cách giảng dạy của ông ấy mang tính giáo huấn cao.", "Dạy bảo", 3),
    ("Rote", "/rəʊt/", "noun", "Rote learning is not always effective.", "Học vẹt", "Học vẹt không phải lúc nào cũng hiệu quả.", "Học thuộc lòng máy móc", 3),
    ("Mnemonic", "/nɪˈmɒn.ɪk/", "noun", "Rhymes are a common mnemonic device.", "Mẹo ghi nhớ", "Vần điệu là một công cụ ghi nhớ phổ biến.", "Ký ức", 3),
    ("Autodidact", "/ˌɔː.təʊˈdaɪ.dækt/", "noun", "Da Vinci was a famous autodidact.", "Người tự học", "Da Vinci là một người tự học nổi tiếng.", "Tự đào tạo", 3),
    ("Polymath", "/ˈpɒl.i.mæθ/", "noun", "A polymath excels in multiple academic disciplines.", "Học giả uyên bác (đa ngành)", "Một học giả uyên bác xuất sắc trong nhiều lĩnh vực học thuật.", "Biết nhiều môn", 3),
    ("Academia", "/ˌæk.əˈdiː.mi.ə/", "noun", "He spent his life in academia.", "Giới học thuật", "Ông ấy dành cả đời cho giới học thuật.", "Môi trường học thuật", 3),
    ("Elitism", "/iˈliː.tɪ.zəm/", "noun", "The university was accused of elitism.", "Chủ nghĩa tinh hoa", "Trường đại học bị cáo buộc mang tính chủ nghĩa tinh hoa.", "Chỉ dành cho giới thượng lưu", 3),
    ("Egalitarianism", "/ɪˌɡæl.ɪˈteə.ri.ə.nɪ.zəm/", "noun", "Egalitarianism promotes equal educational opportunities.", "Chủ nghĩa bình quân", "Chủ nghĩa bình quân thúc đẩy cơ hội giáo dục bình đẳng.", "Bình đẳng", 3),
    ("Meritocracy", "/ˌmer.ɪˈtɒk.rə.si/", "noun", "A meritocracy rewards hard work and talent.", "Chế độ nhân tài", "Chế độ nhân tài khen thưởng sự chăm chỉ và tài năng.", "Dựa vào thực lực", 3),
    ("Paradigm", "/ˈpær.ə.daɪm/", "noun", "There is a paradigm shift in modern education.", "Mô hình, hệ quy chiếu", "Đang có sự chuyển dịch mô hình trong giáo dục hiện đại.", "Mẫu chuẩn", 3),
    ("Pedantic", "/pəˈdæn.tɪk/", "adjective", "The professor is overly pedantic about grammar.", "Nệ cổ, quá câu nệ tiểu tiết", "Giáo sư quá câu nệ tiểu tiết về ngữ pháp.", "Khoe khoang học thức", 3),
    ("Esoteric", "/ˌes.əˈter.ɪk/", "adjective", "He studies esoteric literature.", "Huyền bí, chỉ người trong giới mới hiểu", "Anh ấy nghiên cứu văn học chuyên sâu/huyền bí.", "Thâm sâu", 3),
    ("Ubiquitous", "/juːˈbɪk.wɪ.təs/", "adjective", "Smartphones are ubiquitous in modern schools.", "Có mặt ở khắp nơi", "Điện thoại thông minh có mặt ở khắp nơi trong trường học hiện đại.", "Phổ biến", 3),
    ("Dialectic", "/ˌdaɪ.əˈlek.tɪk/", "noun", "They engaged in a dialectic debate.", "Biện chứng, lý luận", "Họ đã tham gia vào một cuộc tranh luận biện chứng.", "Tranh luận logic", 3),
    ("Axiom", "/ˈæk.si.əm/", "noun", "It is an axiom of modern science.", "Tiên đề", "Đó là một tiên đề của khoa học hiện đại.", "Chân lý hiển nhiên", 3),
    ("Corollary", "/kəˈrɒl.ər.i/", "noun", "The corollary of this theory is fascinating.", "Hệ luận", "Hệ luận của lý thuyết này rất thú vị.", "Kết quả tất yếu", 3),
    ("Polemic", "/pəˈlem.ɪk/", "noun", "He published a polemic against the new policy.", "Bài luận chiến", "Ông đã xuất bản một bài luận chiến chống lại chính sách mới.", "Tranh luận gay gắt", 3),
    ("Rhetoric", "/ˈret.ər.ɪk/", "noun", "His speech was full of empty rhetoric.", "Thuật hùng biện, lời lẽ hoa mỹ", "Bài phát biểu của anh ta đầy những lời lẽ hoa mỹ sáo rỗng.", "Nghệ thuật thuyết phục", 3),
    ("Syllogism", "/ˈsɪl.ə.dʒɪ.zəm/", "noun", "He used a classic logical syllogism.", "Tam đoạn luận", "Anh ấy đã sử dụng một tam đoạn luận logic cổ điển.", "Suy luận logic", 3),
    ("Tautology", "/tɔːˈtɒl.ə.dʒi/", "noun", "Saying 'free gift' is a tautology.", "Sự trùng nghĩa, thừa chữ", "Nói 'quà tặng miễn phí' là một sự thừa chữ.", "Lặp lại ý", 3),
    ("Fallacy", "/ˈfæl.ə.si/", "noun", "That argument is based on a fallacy.", "Ngụy biện, sai lầm", "Lập luận đó dựa trên một sự ngụy biện.", "Lập luận sai", 3),
    ("Postulate", "/ˈpɒs.tʃə.leɪt/", "verb", "Let's postulate that the universe is infinite.", "Đưa ra định đề, mặc định", "Hãy đặt định đề rằng vũ trụ là vô hạn.", "Tiền đề", 3),
    ("Pragmatism", "/ˈpræɡ.mə.tɪ.zəm/", "noun", "Pragmatism focuses on practical outcomes.", "Chủ nghĩa thực dụng", "Chủ nghĩa thực dụng tập trung vào kết quả thực tế.", "Thực tế", 3),
    ("Ontology", "/ɒnˈtɒl.ə.dʒi/", "noun", "Ontology is the branch of metaphysics dealing with the nature of being.", "Bản thể luận", "Bản thể luận là nhánh của siêu hình học đối phó với bản chất của tồn tại.", "Nghiên cứu về sự tồn tại", 3),
    ("Dichotomy", "/daɪˈkɒt.ə.mi/", "noun", "There is a false dichotomy between arts and sciences.", "Sự phân đôi, rẽ đôi", "Có một sự phân đôi sai lầm giữa nghệ thuật và khoa học.", "Hai mặt đối lập", 3),
    ("Hermeneutics", "/ˌhɜː.mɪˈnjuː.tɪks/", "noun", "He specializes in biblical hermeneutics.", "Khoa học chú giải, diễn dịch", "Ông chuyên về khoa học diễn giải Kinh thánh.", "Nghệ thuật giải thích", 3)
]

level_map = ["en_edu_beginner", "en_edu_intermediate", "en_edu_advanced", "en_edu_expert"]

flashcards = []
for word, phonetic, pos, ex_en, meaning, ex_vi, tip, lvl in vocab_data:
    flashcards.append({
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

data = {
    "decks": decks,
    "flashCards": flashcards
}

with open("app/src/main/assets/vocab_data.json", "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print("Successfully replaced vocab_data.json with 120 words for Education (English)!")
