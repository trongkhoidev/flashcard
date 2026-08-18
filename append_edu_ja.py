import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "ja_education_beginner", "languageCode": "ja", "title": "教育", "subtitle": "Giáo dục - Beginner", "iconEmoji": "🎓", "level": "Beginner", "colorHex": "#3B82F6", "cardCount": 30},
    {"id": "ja_education_intermediate", "languageCode": "ja", "title": "教育", "subtitle": "Giáo dục - Intermediate", "iconEmoji": "🎓", "level": "Intermediate", "colorHex": "#2563EB", "cardCount": 30},
    {"id": "ja_education_advanced", "languageCode": "ja", "title": "教育", "subtitle": "Giáo dục - Advanced", "iconEmoji": "🎓", "level": "Advanced", "colorHex": "#1D4ED8", "cardCount": 30},
    {"id": "ja_education_expert", "languageCode": "ja", "title": "教育", "subtitle": "Giáo dục - Expert", "iconEmoji": "🎓", "level": "Expert", "colorHex": "#1E40AF", "cardCount": 30}
]

vocab_data = [
    # Beginner (0)
    ("学校", "Gakkou", "noun", "学校に行く。", "Trường học", "Tôi đi đến trường.", "Học đường", 0),
    ("先生", "Sensei", "noun", "先生に聞く。", "Giáo viên", "Tôi hỏi giáo viên.", "Người dạy", 0),
    ("学生", "Gakusei", "noun", "私は学生です。", "Học sinh, sinh viên", "Tôi là học sinh.", "Người học", 0),
    ("本", "Hon", "noun", "本を読む。", "Sách", "Tôi đọc sách.", "Quyển sách", 0),
    ("鉛筆", "Enpitsu", "noun", "鉛筆で書く。", "Bút chì", "Viết bằng bút chì.", "Dùng để viết", 0),
    ("ペン", "Pen", "noun", "ペンを貸して。", "Bút bi", "Cho tôi mượn bút bi.", "Bút mực", 0),
    ("教室", "Kyoushitsu", "noun", "教室に入る。", "Phòng học", "Bước vào phòng học.", "Lớp học", 0),
    ("勉強", "Benkyou", "noun/verb", "日本語を勉強する。", "Học tập", "Tôi học tiếng Nhật.", "Việc học", 0),
    ("テスト", "Tesuto", "noun", "明日テストがある。", "Bài kiểm tra", "Ngày mai có bài kiểm tra.", "Kiểm tra", 0),
    ("クラス", "Kurasu", "noun", "クラスが始まる。", "Lớp học", "Lớp học bắt đầu.", "Lớp (khóa học)", 0),
    ("机", "Tsukue", "noun", "机の上に本がある。", "Bàn học", "Có quyển sách trên bàn.", "Bàn", 0),
    ("椅子", "Isu", "noun", "椅子に座る。", "Ghế", "Ngồi lên ghế.", "Chỗ ngồi", 0),
    ("友達", "Tomodachi", "noun", "友達と遊ぶ。", "Bạn bè", "Chơi với bạn bè.", "Người bạn", 0),
    ("宿題", "Shukudai", "noun", "宿題をする。", "Bài tập về nhà", "Tôi làm bài tập về nhà.", "Bài tập", 0),
    ("辞書", "Jisho", "noun", "辞書を引く。", "Từ điển", "Tra từ điển.", "Sách tra từ", 0),
    ("教科書", "Kyoukasho", "noun", "教科書を開く。", "Sách giáo khoa", "Mở sách giáo khoa ra.", "Sách học", 0),
    ("問題", "Mondai", "noun", "問題を解く。", "Vấn đề, câu hỏi", "Giải quyết câu hỏi.", "Đề bài", 0),
    ("質問", "Shitsumon", "noun/verb", "質問があります。", "Câu hỏi", "Tôi có câu hỏi.", "Đặt câu hỏi", 0),
    ("答える", "Kotaeru", "verb", "質問に答える。", "Trả lời", "Trả lời câu hỏi.", "Hồi đáp", 0),
    ("書く", "Kaku", "verb", "名前を書く。", "Viết", "Viết tên của bạn.", "Viết chữ", 0),
    ("読む", "Yomu", "verb", "文章を読む。", "Đọc", "Đọc đoạn văn.", "Đọc chữ", 0),
    ("聞く", "Kiku", "verb", "先生の話を聞く。", "Nghe, hỏi", "Nghe giáo viên nói.", "Lắng nghe", 0),
    ("話す", "Hanasu", "verb", "英語で話す。", "Nói chuyện", "Nói bằng tiếng Anh.", "Nói", 0),
    ("わかる", "Wakaru", "verb", "意味がわかる。", "Hiểu", "Tôi hiểu ý nghĩa.", "Hiểu biết", 0),
    ("覚える", "Oboeru", "verb", "単語を覚える。", "Nhớ, học thuộc", "Học thuộc từ vựng.", "Ghi nhớ", 0),
    ("忘れる", "Wasureru", "verb", "宿題を忘れる。", "Quên", "Quên bài tập về nhà.", "Không nhớ", 0),
    ("教える", "Oshieru", "verb", "数学を教える。", "Dạy, chỉ bảo", "Dạy toán.", "Dạy học", 0),
    ("習う", "Narau", "verb", "ピアノを習う。", "Học (có người dạy)", "Học piano.", "Đi học thêm", 0),
    ("大学", "Daigaku", "noun", "大学に入る。", "Trường đại học", "Vào trường đại học.", "Bậc đại học", 0),
    ("ノート", "Nōto", "noun", "ノートに書く。", "Vở", "Viết vào vở.", "Cuốn sổ", 0),

    # Intermediate (1)
    ("授業", "Jugyou", "noun", "授業に出る。", "Buổi học, tiết học", "Tham gia tiết học.", "Giờ học", 1),
    ("試験", "Shiken", "noun", "試験に合格する。", "Kì thi", "Thi đỗ kì thi.", "Thi cử", 1),
    ("成績", "Seiseki", "noun", "成績が良い。", "Thành tích, điểm số", "Điểm số tốt.", "Kết quả học", 1),
    ("卒業", "Sotsugyou", "noun/verb", "大学を卒業する。", "Tốt nghiệp", "Tốt nghiệp đại học.", "Ra trường", 1),
    ("入学", "Nyuugaku", "noun/verb", "高校に入学する。", "Nhập học", "Nhập học trường cấp 3.", "Vào trường", 1),
    ("科目", "Kamoku", "noun", "好きな科目は何ですか。", "Môn học", "Môn học yêu thích là gì?", "Môn học", 1),
    ("数学", "Suugaku", "noun", "数学が難しい。", "Toán học", "Môn toán rất khó.", "Môn toán", 1),
    ("歴史", "Rekishi", "noun", "歴史を学ぶ。", "Lịch sử", "Học lịch sử.", "Môn sử", 1),
    ("科学", "Kagaku", "noun", "科学の実験。", "Khoa học", "Thí nghiệm khoa học.", "Môn khoa học", 1),
    ("文学", "Bungaku", "noun", "日本文学。", "Văn học", "Văn học Nhật Bản.", "Môn văn", 1),
    ("予習", "Yoshuu", "noun/verb", "予習をする。", "Soạn bài, chuẩn bị", "Tôi chuẩn bị bài trước.", "Học trước", 1),
    ("復習", "Fukushuu", "noun/verb", "復習が大切だ。", "Ôn tập", "Việc ôn tập rất quan trọng.", "Học lại", 1),
    ("塾", "Juku", "noun", "塾に通う。", "Lớp học thêm", "Đi học ở lớp học thêm.", "Nơi học thêm", 1),
    ("知識", "Chishiki", "noun", "知識を得る。", "Kiến thức", "Đạt được kiến thức.", "Sự hiểu biết", 1),
    ("黒板", "Kokuban", "noun", "黒板を消す。", "Bảng đen", "Xóa bảng đen.", "Bảng phấn", 1),
    ("定規", "Jougi", "noun", "定規で線を引く。", "Thước kẻ", "Kẻ đường thẳng bằng thước.", "Thước", 1),
    ("消しゴム", "Keshigomu", "noun", "消しゴムを貸す。", "Cục tẩy", "Cho mượn cục tẩy.", "Cục gôm", 1),
    ("欠席", "Kesseki", "noun/verb", "授業を欠席する。", "Vắng mặt", "Vắng mặt trong buổi học.", "Nghỉ học", 1),
    ("出席", "Shusseki", "noun/verb", "出席を取る。", "Có mặt, điểm danh", "Điểm danh.", "Đi học", 1),
    ("奨学金", "Shougakukin", "noun", "奨学金をもらう。", "Học bổng", "Nhận được học bổng.", "Tiền hỗ trợ học", 1),
    ("専攻", "Senkou", "noun/verb", "専攻は経済です。", "Chuyên ngành", "Chuyên ngành của tôi là kinh tế.", "Ngành học chính", 1),
    ("単位", "Tan'i", "noun", "単位を落とす。", "Tín chỉ", "Bị rớt tín chỉ.", "Đơn vị học trình", 1),
    ("ゼミ", "Zemi", "noun", "ゼミの発表。", "Hội thảo, chuyên đề", "Bài thuyết trình của lớp chuyên đề.", "Seminar", 1),
    ("教授", "Kyouju", "noun", "大学の教授。", "Giáo sư", "Giáo sư đại học.", "Người dạy đại học", 1),
    ("論文", "Ronbun", "noun", "論文を書く。", "Luận văn", "Viết luận văn.", "Bài viết học thuật", 1),
    ("提出", "Teishutsu", "noun/verb", "レポートを提出する。", "Nộp (bài)", "Nộp bài báo cáo.", "Giao nộp", 1),
    ("期限", "Kigen", "noun", "提出期限を守る。", "Thời hạn", "Tuân thủ thời hạn nộp bài.", "Hạn chót", 1),
    ("留学", "Ryuugaku", "noun/verb", "日本に留学する。", "Du học", "Du học ở Nhật Bản.", "Học ở nước ngoài", 1),
    ("辞める", "Yameru", "verb", "学校を辞める。", "Bỏ (học), nghỉ", "Nghỉ học.", "Từ bỏ", 1),
    ("サークル", "Sākuru", "noun", "テニスサークルに入る。", "Câu lạc bộ (đại học)", "Tham gia câu lạc bộ tennis.", "Hội nhóm", 1),

    # Advanced (2)
    ("基礎", "Kiso", "noun", "基礎を固める。", "Nền tảng, cơ bản", "Củng cố kiến thức nền tảng.", "Căn bản", 2),
    ("応用", "Ouyou", "noun/verb", "応用問題。", "Ứng dụng", "Bài tập ứng dụng.", "Áp dụng", 2),
    ("評価", "Hyouka", "noun/verb", "先生の評価。", "Đánh giá", "Sự đánh giá của giáo viên.", "Chấm điểm", 2),
    ("筆記", "Hikki", "noun", "筆記試験。", "Thi viết", "Kỳ thi viết.", "Viết tay", 2),
    ("面接", "Mensetsu", "noun/verb", "面接を受ける。", "Phỏng vấn", "Tham gia phỏng vấn.", "Hỏi đáp trực tiếp", 2),
    ("講義", "Kougi", "noun", "講義を聴く。", "Bài giảng", "Lắng nghe bài giảng.", "Giảng dạy đại học", 2),
    ("偏差値", "Hensachi", "noun", "偏差値が高い。", "Điểm chuẩn học lực", "Điểm học lực cao.", "Thang đo học lực", 2),
    ("浪人", "Rounin", "noun", "浪人して勉強する。", "Ôn thi lại đại học", "Ôn thi lại một năm để học.", "Học lại 1 năm", 2),
    ("学部", "Gakubu", "noun", "文学部。", "Khoa (trường đại học)", "Khoa Văn.", "Ngành lớn", 2),
    ("大学院", "Daigakuin", "noun", "大学院に進学する。", "Cao học", "Học lên cao học.", "Sau đại học", 2),
    ("修士", "Shuushi", "noun", "修士号を取得する。", "Thạc sĩ", "Lấy bằng thạc sĩ.", "Bậc thạc sĩ", 2),
    ("博士", "Hakase/Hakushi", "noun", "博士課程。", "Tiến sĩ", "Chương trình tiến sĩ.", "Bậc tiến sĩ", 2),
    ("学術", "Gakujutsu", "noun", "学術研究。", "Học thuật", "Nghiên cứu học thuật.", "Thuộc về khoa học", 2),
    ("討論", "Touron", "noun/verb", "討論会を開く。", "Thảo luận", "Tổ chức buổi tranh luận.", "Bàn luận", 2),
    ("結論", "Ketsuron", "noun", "結論を出す。", "Kết luận", "Đưa ra kết luận.", "Lời chốt", 2),
    ("考察", "Kousatsu", "noun/verb", "結果を考察する。", "Khảo sát, xem xét kỹ", "Đánh giá kết quả.", "Suy xét", 2),
    ("引用", "Inyou", "noun/verb", "文献を引用する。", "Trích dẫn", "Trích dẫn tài liệu.", "Lấy từ nguồn khác", 2),
    ("文献", "Bunken", "noun", "参考文献。", "Tài liệu tham khảo", "Tài liệu tham khảo.", "Sách vở tài liệu", 2),
    ("概要", "Gaiyou", "noun", "概要をまとめる。", "Tóm tắt, khái quát", "Tóm tắt phần khái quát.", "Ý chính", 2),
    ("専修", "Senshuu", "noun", "専修学校。", "Chuyên tu", "Trường dạy nghề.", "Học chuyên sâu", 2),
    ("履修", "Rishuu", "noun/verb", "科目を履修する。", "Đăng ký học", "Đăng ký môn học.", "Hoàn thành môn", 2),
    ("シラバス", "Shirabasu", "noun", "シラバスを確認する。", "Đề cương môn học", "Kiểm tra đề cương môn học.", "Lịch trình học", 2),
    ("暗記", "Anki", "noun/verb", "単語を暗記する。", "Học thuộc lòng", "Học thuộc lòng từ vựng.", "Nhớ kỹ", 2),
    ("読解", "Dokkai", "noun", "読解力が高い。", "Đọc hiểu", "Khả năng đọc hiểu cao.", "Kỹ năng đọc", 2),
    ("聴解", "Choukai", "noun", "聴解のテスト。", "Nghe hiểu", "Bài kiểm tra nghe hiểu.", "Kỹ năng nghe", 2),
    ("語彙", "Goi", "noun", "語彙を増やす。", "Từ vựng", "Tăng cường vốn từ vựng.", "Tập hợp từ", 2),
    ("文法", "Bunpou", "noun", "文法を間違える。", "Ngữ pháp", "Sai ngữ pháp.", "Quy tắc ngôn ngữ", 2),
    ("記述", "Kijutsu", "noun/verb", "記述式の問題。", "Mô tả, tự luận", "Câu hỏi dạng tự luận.", "Viết ra", 2),
    ("解答", "Kaitou", "noun/verb", "解答用紙。", "Lời giải, đáp án", "Giấy ghi đáp án.", "Trả lời bài thi", 2),
    ("正解", "Seikai", "noun/verb", "正解を当てる。", "Đáp án đúng", "Đoán đáp án đúng.", "Trả lời chính xác", 2),

    # Expert (3)
    ("認知", "Ninchi", "noun", "認知能力。", "Nhận thức", "Khả năng nhận thức.", "Quá trình tư duy", 3),
    ("教育学", "Kyouikugaku", "noun", "教育学を専攻する。", "Giáo dục học", "Chuyên ngành giáo dục học.", "Ngành sư phạm", 3),
    ("啓蒙", "Keimou", "noun/verb", "啓蒙思想。", "Khai sáng", "Tư tưởng khai sáng.", "Mở mang trí tuệ", 3),
    ("奨励", "Shourei", "noun/verb", "読書を奨励する。", "Khuyến khích", "Khuyến khích việc đọc sách.", "Cổ vũ", 3),
    ("探求", "Tankyuu", "noun/verb", "真理を探求する。", "Tìm tòi, khám phá", "Tìm tòi chân lý.", "Nghiên cứu sâu", 3),
    ("洞察", "Dousatsu", "noun/verb", "深い洞察力。", "Sự sáng suốt", "Khả năng nhìn nhận sâu sắc.", "Nhìn thấu", 3),
    ("推論", "Suiron", "noun/verb", "論理的推論。", "Suy luận", "Sự suy luận logic.", "Suy đoán", 3),
    ("演繹", "Eneki", "noun/verb", "演繹的アプローチ。", "Diễn dịch", "Phương pháp diễn dịch.", "Suy từ cái chung", 3),
    ("帰納", "Kinou", "noun/verb", "帰納法を用いる。", "Quy nạp", "Sử dụng phương pháp quy nạp.", "Suy từ cái riêng", 3),
    ("実証", "Jisshou", "noun/verb", "実証研究。", "Chứng minh thực tế", "Nghiên cứu thực chứng.", "Dựa trên thực tế", 3),
    ("概念", "Gainen", "noun", "新しい概念。", "Khái niệm", "Một khái niệm mới.", "Ý niệm", 3),
    ("体系", "Taikei", "noun", "知識の体系。", "Hệ thống", "Hệ thống kiến thức.", "Cấu trúc chặt chẽ", 3),
    ("理念", "Rinen", "noun", "教育理念。", "Triết lý, lý tưởng", "Triết lý giáo dục.", "Tư tưởng cốt lõi", 3),
    ("倫理", "Rinri", "noun", "研究倫理。", "Đạo đức", "Đạo đức nghiên cứu.", "Luân thường", 3),
    ("査読", "Sadoku", "noun/verb", "論文の査読。", "Bình duyệt", "Việc bình duyệt bài báo.", "Đánh giá chuyên môn", 3),
    ("学位", "Gakui", "noun", "学位を授与される。", "Học vị", "Được trao học vị.", "Bằng cấp", 3),
    ("教養", "Kyouyou", "noun", "教養を身につける。", "Sự giáo dục, tu dưỡng", "Tích lũy kiến thức tu dưỡng.", "Hiểu biết chung", 3),
    ("師事", "Shiji", "noun/verb", "教授に師事する。", "Tôn làm thầy", "Bái giáo sư làm thầy.", "Học hỏi chuyên gia", 3),
    ("研鑽", "Kensan", "noun/verb", "研鑽を積む。", "Mài giũa, rèn luyện", "Tích cực rèn luyện học hỏi.", "Nỗ lực học tập", 3),
    ("首席", "Shuseki", "noun", "首席で卒業する。", "Thủ khoa", "Tốt nghiệp thủ khoa.", "Đứng đầu", 3),
    ("登壇", "Toudan", "noun/verb", "学会で登壇する。", "Lên bục phát biểu", "Lên bục phát biểu tại hội nghị.", "Đứng trên bục", 3),
    ("質疑応答", "Shitsugi outou", "noun", "質疑応答の時間。", "Hỏi đáp", "Thời gian dành cho phần hỏi đáp.", "Q&A", 3),
    ("知的好奇心", "Chiteki koukishin", "noun", "知的好奇心が旺盛だ。", "Sự tò mò trí thức", "Đầy ắp sự tò mò trí thức.", "Ham học hỏi", 3),
    ("独学", "Dokugaku", "noun/verb", "英語を独学する。", "Tự học", "Tự học tiếng Anh.", "Tự mày mò", 3),
    ("生涯学習", "Shougai gakushuu", "noun", "生涯学習の時代。", "Học tập suốt đời", "Thời đại của việc học tập suốt đời.", "Học không ngừng", 3),
    ("権威", "Ken'i", "noun", "その分野の権威。", "Quyền uy, chuyên gia", "Một chuyên gia đầu ngành.", "Người uy tín", 3),
    ("剽窃", "Hyousetsu", "noun/verb", "剽窃は許されない。", "Đạo văn", "Đạo văn là không được phép.", "Ăn cắp ý tưởng", 3),
    ("修辞学", "Shuujigaku", "noun", "修辞学を学ぶ。", "Thuật hùng biện", "Học thuật hùng biện.", "Nghệ thuật nói", 3),
    ("弁証法", "Benshouhou", "noun", "ヘーゲルの弁証法。", "Phép biện chứng", "Phép biện chứng của Hegel.", "Biện luận", 3),
    ("学際的", "Gakusaiteki", "adjective", "学際的な研究。", "Liên ngành", "Nghiên cứu mang tính liên ngành.", "Kết hợp nhiều ngành", 3)
]

level_map = ["ja_education_beginner", "ja_education_intermediate", "ja_education_advanced", "ja_education_expert"]

new_flashcards = []
for word, phonetic, pos, ex_en, meaning, ex_vi, tip, lvl in vocab_data:
    new_flashcards.append({
        "deckId": level_map[lvl],
        "languageCode": "ja",
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

print("Successfully appended 120 words for Education (Japanese) to DB!")
