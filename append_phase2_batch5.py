import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    # Daily Life
    {"id": "ja_daily_beginner", "languageCode": "ja", "title": "日常生活", "subtitle": "Cuộc sống hàng ngày - Beginner", "iconEmoji": "🏠", "level": "Beginner", "colorHex": "#8B5CF6", "cardCount": 30},
    {"id": "ja_daily_intermediate", "languageCode": "ja", "title": "日常生活", "subtitle": "Cuộc sống hàng ngày - Intermediate", "iconEmoji": "🏠", "level": "Intermediate", "colorHex": "#7C3AED", "cardCount": 30},
    {"id": "ja_daily_advanced", "languageCode": "ja", "title": "日常生活", "subtitle": "Cuộc sống hàng ngày - Advanced", "iconEmoji": "🏠", "level": "Advanced", "colorHex": "#6D28D9", "cardCount": 30},
    {"id": "ja_daily_expert", "languageCode": "ja", "title": "日常生活", "subtitle": "Cuộc sống hàng ngày - Expert", "iconEmoji": "🏠", "level": "Expert", "colorHex": "#5B21B6", "cardCount": 30}
]

vocab_data = [
    # ---- DAILY LIFE ----
    # Beginner (0)
    ("朝", "Asa", "noun", "朝起きる。", "Buổi sáng", "Thức dậy vào buổi sáng.", "Lúc mới bình minh", "ja_daily_beginner"),
    ("昼", "Hiru", "noun", "昼に休む。", "Buổi trưa", "Nghỉ ngơi vào buổi trưa.", "Giữa ngày", "ja_daily_beginner"),
    ("夜", "Yoru", "noun", "夜寝る。", "Buổi tối, đêm", "Ngủ vào ban đêm.", "Mặt trời lặn", "ja_daily_beginner"),
    ("毎日", "Mainichi", "noun", "毎日勉強する。", "Mỗi ngày", "Học bài mỗi ngày.", "Ngày nào cũng vậy", "ja_daily_beginner"),
    ("起きる", "Okiru", "verb", "7時に起きる。", "Thức dậy", "Thức dậy lúc 7 giờ.", "Mở mắt rời giường", "ja_daily_beginner"),
    ("寝る", "Neru", "verb", "早く寝る。", "Ngủ", "Ngủ sớm.", "Nhắm mắt", "ja_daily_beginner"),
    ("食べる", "Taberu", "verb", "ご飯を食べる。", "Ăn", "Ăn cơm.", "Đưa vào miệng", "ja_daily_beginner"),
    ("飲む", "Nomu", "verb", "水を飲む。", "Uống", "Uống nước.", "Nuốt chất lỏng", "ja_daily_beginner"),
    ("行く", "Iku", "verb", "会社に行く。", "Đi", "Đi đến công ty.", "Di chuyển tới", "ja_daily_beginner"),
    ("来る", "Kuru", "verb", "友達が来る。", "Đến", "Bạn bè tới.", "Tới chỗ mình", "ja_daily_beginner"),
    ("帰る", "Kaeru", "verb", "家に帰る。", "Trở về", "Về nhà.", "Quay lại chỗ cũ", "ja_daily_beginner"),
    ("洗う", "Arau", "verb", "顔を洗う。", "Rửa", "Rửa mặt.", "Làm sạch bằng nước", "ja_daily_beginner"),
    ("掃除", "Souji", "noun/verb", "部屋を掃除する。", "Dọn dẹp", "Dọn dẹp căn phòng.", "Làm sạch nhà", "ja_daily_beginner"),
    ("洗濯", "Sentaku", "noun/verb", "服を洗濯する。", "Giặt giũ", "Giặt quần áo.", "Làm sạch đồ", "ja_daily_beginner"),
    ("料理", "Ryouri", "noun/verb", "料理を作る。", "Nấu ăn", "Làm đồ ăn.", "Chế biến", "ja_daily_beginner"),
    ("働く", "Hataraku", "verb", "会社で働く。", "Làm việc", "Làm việc tại công ty.", "Lao động", "ja_daily_beginner"),
    ("勉強", "Benkyou", "noun/verb", "日本語を勉強する。", "Học tập", "Học tiếng Nhật.", "Tiếp thu kiến thức", "ja_daily_beginner"),
    ("遊ぶ", "Asobu", "verb", "公園で遊ぶ。", "Chơi", "Chơi ở công viên.", "Giải trí", "ja_daily_beginner"),
    ("買う", "Kau", "verb", "パンを買う。", "Mua", "Mua bánh mì.", "Trả tiền lấy đồ", "ja_daily_beginner"),
    ("話す", "Hanasu", "verb", "友達と話す。", "Nói chuyện", "Nói chuyện với bạn.", "Giao tiếp", "ja_daily_beginner"),
    ("聞く", "Kiku", "verb", "音楽を聞く。", "Nghe", "Nghe nhạc.", "Dùng tai", "ja_daily_beginner"),
    ("見る", "Miru", "verb", "テレビを見る。", "Xem, nhìn", "Xem Tivi.", "Dùng mắt", "ja_daily_beginner"),
    ("読む", "Yomu", "verb", "本を読む。", "Đọc", "Đọc sách.", "Nhìn chữ", "ja_daily_beginner"),
    ("書く", "Kaku", "verb", "手紙を書く。", "Viết", "Viết thư.", "Tạo ra chữ", "ja_daily_beginner"),
    ("乗る", "Noru", "verb", "電車に乗る。", "Lên (xe, tàu)", "Lên tàu điện.", "Lên xe cộ", "ja_daily_beginner"),
    ("降りる", "Oriru", "verb", "バスを降りる。", "Xuống (xe, tàu)", "Xuống xe buýt.", "Rời xe cộ", "ja_daily_beginner"),
    ("歩く", "Aruku", "verb", "駅まで歩く。", "Đi bộ", "Đi bộ đến ga.", "Đi bằng chân", "ja_daily_beginner"),
    ("走る", "Hashiru", "verb", "公園を走る。", "Chạy", "Chạy bộ ở công viên.", "Đi nhanh", "ja_daily_beginner"),
    ("休み", "Yasumi", "noun", "今日は休みだ。", "Ngày nghỉ", "Hôm nay là ngày nghỉ.", "Không phải làm", "ja_daily_beginner"),
    ("時間", "Jikan", "noun", "時間がない。", "Thời gian", "Không có thời gian.", "Giờ giấc", "ja_daily_beginner"),

    # Intermediate (1)
    ("日常", "Nichijou", "noun", "日常生活。", "Ngày thường", "Cuộc sống ngày thường.", "Đều đặn", "ja_daily_intermediate"),
    ("習慣", "Shuukan", "noun", "早起きの習慣。", "Thói quen", "Thói quen dậy sớm.", "Tập quán", "ja_daily_intermediate"),
    ("通勤", "Tsuukin", "noun/verb", "電車で通勤する。", "Đi làm", "Đi làm bằng tàu điện.", "Quá trình tới chỗ làm", "ja_daily_intermediate"),
    ("通学", "Tsuugaku", "noun/verb", "自転車で通学する。", "Đi học", "Đi học bằng xe đạp.", "Quá trình tới trường", "ja_daily_intermediate"),
    ("支度", "Shitaku", "noun/verb", "出かける支度をする。", "Sửa soạn", "Chuẩn bị đi ra ngoài.", "Chuẩn bị đồ đạc", "ja_daily_intermediate"),
    ("化粧", "Keshou", "noun/verb", "化粧をする。", "Trang điểm", "Trang điểm.", "Làm đẹp", "ja_daily_intermediate"),
    ("髭剃り", "Higesori", "noun", "髭剃りをする。", "Cạo râu", "Cạo râu.", "Cắt bỏ râu", "ja_daily_intermediate"),
    ("着替え", "Kigae", "noun", "着替えをする。", "Thay đồ", "Thay quần áo.", "Mặc đồ khác", "ja_daily_intermediate"),
    ("出勤", "Shukkin", "noun/verb", "9時に出勤する。", "Tới nơi làm", "Tới công ty lúc 9 giờ.", "Bắt đầu làm", "ja_daily_intermediate"),
    ("退勤", "Taikin", "noun/verb", "6時に退勤する。", "Tan làm", "Ra về lúc 6 giờ.", "Kết thúc làm", "ja_daily_intermediate"),
    ("残業", "Zangyou", "noun/verb", "今日は残業だ。", "Làm thêm giờ", "Hôm nay tôi làm thêm giờ.", "Làm quá giờ", "ja_daily_intermediate"),
    ("休憩", "Kyuukei", "noun/verb", "10分休憩する。", "Nghỉ giải lao", "Nghỉ ngơi 10 phút.", "Nghỉ giữa giờ", "ja_daily_intermediate"),
    ("会議", "Kaigi", "noun", "会議に出る。", "Cuộc họp", "Tham gia cuộc họp.", "Thảo luận công việc", "ja_daily_intermediate"),
    ("約束", "Yakusoku", "noun/verb", "友達と約束する。", "Cuộc hẹn, lời hứa", "Hẹn với bạn.", "Lên lịch trước", "ja_daily_intermediate"),
    ("訪問", "Houmon", "noun/verb", "顧客を訪問する。", "Đến thăm (khách)", "Đến thăm khách hàng.", "Thăm hỏi", "ja_daily_intermediate"),
    ("外食", "Gaishoku", "noun/verb", "家族で外食する。", "Đi ăn ngoài", "Cả nhà đi ăn tiệm.", "Ăn ở hàng quán", "ja_daily_intermediate"),
    ("自炊", "Jisui", "noun/verb", "毎日自炊する。", "Tự nấu ăn", "Tự nấu ăn mỗi ngày.", "Tự nấu đồ", "ja_daily_intermediate"),
    ("ゴミ出し", "Gomidashi", "noun", "ゴミ出しをする。", "Vứt rác", "Mang rác đi đổ.", "Đổ rác", "ja_daily_intermediate"),
    ("片付け", "Katadzuke", "noun", "部屋の片付け。", "Dọn dẹp đồ đạc", "Dọn dẹp căn phòng.", "Sắp xếp lại", "ja_daily_intermediate"),
    ("アイロン", "Airon", "noun", "アイロンをかける。", "Bàn là", "Là (ủi) quần áo.", "Làm phẳng đồ", "ja_daily_intermediate"),
    ("散歩", "Sanpo", "noun/verb", "犬の散歩に行く。", "Đi dạo", "Dắt chó đi dạo.", "Tản bộ", "ja_daily_intermediate"),
    ("趣味", "Shumi", "noun", "趣味は読書だ。", "Sở thích", "Sở thích là đọc sách.", "Niềm vui cá nhân", "ja_daily_intermediate"),
    ("習い事", "Naraigoto", "noun", "習い事に行く。", "Học năng khiếu", "Đi học ngoại khóa.", "Học thêm sở thích", "ja_daily_intermediate"),
    ("買い物", "Kaimono", "noun/verb", "スーパーで買い物する。", "Mua sắm", "Mua sắm ở siêu thị.", "Đi mua đồ", "ja_daily_intermediate"),
    ("貯金", "Chokin", "noun/verb", "お金を貯金する。", "Tiết kiệm tiền", "Gửi tiền tiết kiệm.", "Cất tiền", "ja_daily_intermediate"),
    ("節約", "Setsuyaku", "noun/verb", "電気代を節約する。", "Tiết kiệm (giảm chi)", "Tiết kiệm tiền điện.", "Bớt xài", "ja_daily_intermediate"),
    ("予定", "Yotei", "noun", "週末の予定。", "Dự định", "Kế hoạch cuối tuần.", "Kế hoạch", "ja_daily_intermediate"),
    ("週末", "Shuumatsu", "noun", "週末は休む。", "Cuối tuần", "Nghỉ ngơi vào cuối tuần.", "Thứ 7 chủ nhật", "ja_daily_intermediate"),
    ("平日", "Heijitsu", "noun", "平日は忙しい。", "Ngày thường", "Ngày thường rất bận.", "Từ thứ 2 tới 6", "ja_daily_intermediate"),
    ("祝日", "Shukujitsu", "noun", "明日は祝日だ。", "Ngày lễ", "Ngày mai là ngày nghỉ lễ.", "Lễ quốc gia", "ja_daily_intermediate"),

    # Advanced (2)
    ("ライフスタイル", "Raifusutairu", "noun", "ライフスタイルを変える。", "Phong cách sống", "Thay đổi phong cách sống.", "Cách sống", "ja_daily_advanced"),
    ("ワークライフバランス", "Wākuraifubaransu", "noun", "ワークライフバランスを重視する。", "Cân bằng công việc và cuộc sống", "Coi trọng sự cân bằng cuộc sống và việc làm.", "Cân đối thời gian", "ja_daily_advanced"),
    ("余暇", "Yoka", "noun", "余暇を楽しむ。", "Thời gian rảnh rỗi", "Tận hưởng thời gian rảnh rỗi.", "Lúc rảnh", "ja_daily_advanced"),
    ("気晴らし", "Kibarashi", "noun", "気晴らしに散歩する。", "Giải khuây", "Đi dạo để giải khuây.", "Xả stress", "ja_daily_advanced"),
    ("息抜き", "Ikinuki", "noun", "仕事の息抜き。", "Xả hơi", "Phút xả hơi trong công việc.", "Thư giãn", "ja_daily_advanced"),
    ("癒し", "Iyashi", "noun", "音楽は心の癒しだ。", "Sự thư giãn, chữa lành", "Âm nhạc chữa lành tâm hồn.", "An ủi", "ja_daily_advanced"),
    ("ストレス発散", "Sutoresu hassan", "noun", "ストレス発散にカラオケに行く。", "Xả stress", "Đi hát karaoke để xả stress.", "Giải tỏa áp lực", "ja_daily_advanced"),
    ("日課", "Nikka", "noun", "毎朝のジョギングが日課だ。", "Công việc hằng ngày", "Việc chạy bộ mỗi sáng là thói quen hằng ngày.", "Routine", "ja_daily_advanced"),
    ("惰性", "Dasei", "noun", "惰性でテレビを見る。", "Theo quán tính", "Xem Tivi theo quán tính (không chủ đích).", "Thói quen vô thức", "ja_daily_advanced"),
    ("マンネリ", "Manneri", "noun", "マンネリ化する。", "Sự nhàm chán", "Lặp đi lặp lại đâm ra nhàm chán.", "Rập khuôn", "ja_daily_advanced"),
    ("充実", "Juujitsu", "noun/verb", "充実した一日。", "Trọn vẹn, ý nghĩa", "Một ngày thật ý nghĩa trọn vẹn.", "Đầy đủ", "ja_daily_advanced"),
    ("徹夜", "Tetsuya", "noun/verb", "徹夜で勉強する。", "Thức trắng đêm", "Thức trắng đêm để học bài.", "Cả đêm không ngủ", "ja_daily_advanced"),
    ("寝坊", "Nebou", "noun/verb", "寝坊して遅刻する。", "Ngủ nướng", "Ngủ quên nên bị trễ.", "Dậy trễ", "ja_daily_advanced"),
    ("居眠り", "Inemuri", "noun/verb", "電車で居眠りする。", "Ngủ gật", "Ngủ gật trên tàu điện.", "Ngủ thiếp đi", "ja_daily_advanced"),
    ("雑用", "Zatsuyou", "noun", "雑用に追われる。", "Việc vặt", "Lu bù với những việc vặt.", "Việc không tên", "ja_daily_advanced"),
    ("家計", "Kakei", "noun", "家計をやりくりする。", "Chi tiêu gia đình", "Xoay sở chi tiêu trong nhà.", "Tài chính nhà", "ja_daily_advanced"),
    ("出費", "Shuppi", "noun/verb", "今月は出費が多い。", "Chi phí", "Tháng này tốn nhiều chi phí.", "Tiền chi ra", "ja_daily_advanced"),
    ("収入", "Shuunyuu", "noun", "収入が減る。", "Thu nhập", "Thu nhập bị giảm.", "Tiền kiếm được", "ja_daily_advanced"),
    ("支出", "Shishutsu", "noun", "支出を抑える。", "Chi tiêu", "Cố gắng cắt giảm chi tiêu.", "Tiền xài", "ja_daily_advanced"),
    ("お小遣い", "Okozukai", "noun", "お小遣いをもらう。", "Tiền tiêu vặt", "Nhận tiền tiêu vặt.", "Tiền riêng nhỏ", "ja_daily_advanced"),
    ("年金", "Nenkin", "noun", "年金で暮らす。", "Lương hưu", "Sống bằng lương hưu.", "Tiền hưu trí", "ja_daily_advanced"),
    ("税金", "Zeikin", "noun", "税金を納める。", "Thuế", "Đóng thuế.", "Tiền nộp nhà nước", "ja_daily_advanced"),
    ("公共料金", "Koukyou ryoukin", "noun", "公共料金を払う。", "Phí tiện ích", "Thanh toán phí điện, nước, gas.", "Phí sinh hoạt chung", "ja_daily_advanced"),
    ("家賃", "Yachin", "noun", "家賃を振り込む。", "Tiền thuê nhà", "Chuyển khoản tiền thuê nhà.", "Phí trọ", "ja_daily_advanced"),
    ("ローン", "Rōn", "noun", "住宅ローンを組む。", "Tiền vay trả góp", "Vay trả góp mua nhà.", "Trả dần", "ja_daily_advanced"),
    ("契約", "Keiyaku", "noun/verb", "スマホの契約をする。", "Hợp đồng", "Làm hợp đồng điện thoại.", "Giao kèo", "ja_daily_advanced"),
    ("解約", "Kaiyaku", "noun/verb", "契約を解約する。", "Hủy hợp đồng", "Hủy bỏ hợp đồng.", "Chấm dứt", "ja_daily_advanced"),
    ("手続き", "Tetsuduki", "noun", "引っ越しの手続き。", "Thủ tục", "Thủ tục chuyển nhà.", "Quy trình", "ja_daily_advanced"),
    ("役所", "Yakusho", "noun", "役所で書類をもらう。", "Ủy ban, cơ quan hành chính", "Nhận giấy tờ ở ủy ban.", "Cơ quan nhà nước", "ja_daily_advanced"),
    ("近所付き合い", "Kinjo zukaiai", "noun", "近所付き合いが大切だ。", "Quan hệ hàng xóm", "Quan hệ láng giềng rất quan trọng.", "Láng giềng", "ja_daily_advanced"),

    # Expert (3)
    ("ＱＯＬ", "Kyū-ō-eru", "noun", "ＱＯＬを向上させる。", "Chất lượng cuộc sống", "Nâng cao chất lượng sống.", "Quality of Life", "ja_daily_expert"),
    ("ミニマリスト", "Minimarisuto", "noun", "ミニマリストの生活。", "Người sống tối giản", "Cuộc sống của người tối giản.", "Tối giản", "ja_daily_expert"),
    ("断捨離", "Danshari", "noun/verb", "断捨離をして部屋をすっきりさせる。", "Tối giản đồ đạc", "Từ bỏ đồ đạc dư thừa để dọn phòng.", "Từ chối, vứt, rời xa đồ đạc", "ja_daily_expert"),
    ("スローライフ", "Surōraifu", "noun", "田舎でスローライフを送る。", "Sống chậm", "Sống chậm ở vùng quê.", "Slow life", "ja_daily_expert"),
    ("丁寧な暮らし", "Teinei na kurashi", "noun", "丁寧な暮らしに憧れる。", "Sống tinh tế, trau chuốt", "Ngưỡng mộ lối sống trau chuốt tỉ mỉ.", "Sống kỹ lưỡng", "ja_daily_expert"),
    ("ルーティン", "Rūtin", "noun", "朝のルーティンをこなす。", "Thói quen cố định", "Thực hiện chu trình buổi sáng.", "Routine", "ja_daily_expert"),
    ("タイムマネジメント", "Taimumanejimento", "noun", "タイムマネジメントがうまい。", "Quản lý thời gian", "Giỏi quản lý thời gian.", "Time management", "ja_daily_expert"),
    ("タスク", "Tasuku", "noun", "今日のタスクを整理する。", "Nhiệm vụ", "Sắp xếp lại nhiệm vụ hôm nay.", "Task", "ja_daily_expert"),
    ("マルチタスク", "Maruchitasuku", "noun", "マルチタスクでこなす。", "Đa nhiệm", "Giải quyết nhiều việc một lúc.", "Multitasking", "ja_daily_expert"),
    ("生産性", "Seisansei", "noun", "生産性を上げる。", "Năng suất", "Nâng cao năng suất làm việc.", "Hiệu quả", "ja_daily_expert"),
    ("効率化", "Kouritsuka", "noun/verb", "業務を効率化する。", "Tối ưu hóa hiệu suất", "Tối ưu hóa công việc kinh doanh.", "Làm cho năng suất", "ja_daily_expert"),
    ("可処分所得", "Kashobun shotoku", "noun", "可処分所得が減少する。", "Thu nhập tự do chi tiêu", "Thu nhập có thể tự do chi tiêu bị giảm.", "Tiền dư sau thuế", "ja_daily_expert"),
    ("可処分時間", "Kashobun jikan", "noun", "可処分時間をどう使うか。", "Thời gian rảnh rỗi tự do", "Sử dụng thời gian rảnh như thế nào.", "Thời gian cá nhân", "ja_daily_expert"),
    ("エンゲル係数", "Engeru keisuu", "noun", "エンゲル係数が高い。", "Hệ số Engel", "Tỷ lệ chi cho ăn uống (Engel) cao.", "Tỉ lệ tiền ăn", "ja_daily_expert"),
    ("確定申告", "Kakutei shinkoku", "noun", "確定申告の時期だ。", "Kê khai thuế", "Đã đến kỳ kê khai thuế cuối năm.", "Báo cáo thuế", "ja_daily_expert"),
    ("ふるさと納税", "Furusato nouzei", "noun", "ふるさと納税を利用する。", "Thuế đóng góp quê hương", "Sử dụng chế độ quyên góp quê hương.", "Furusato Nozei", "ja_daily_expert"),
    ("マイナンバー", "Mainanbā", "noun", "マイナンバーカードを作る。", "Thẻ căn cước", "Làm thẻ căn cước (My Number).", "Mã số cá nhân (Nhật)", "ja_daily_expert"),
    ("キャッシュレス化", "Kyasshuresuka", "noun", "キャッシュレス化が進む。", "Không dùng tiền mặt", "Xu hướng thanh toán điện tử gia tăng.", "Thanh toán thẻ", "ja_daily_expert"),
    ("デジタルデバイド", "Dejitaru debaido", "noun", "デジタルデバイドの問題。", "Khoảng cách công nghệ", "Vấn đề chênh lệch công nghệ số.", "Digital divide", "ja_daily_expert"),
    ("情報リテラシー", "Jouhou riterashī", "noun", "情報リテラシーを身につける。", "Năng lực tiếp cận thông tin", "Trang bị kỹ năng phân tích thông tin.", "Information literacy", "ja_daily_expert"),
    ("ネットサーフィン", "Netto sāfin", "noun/verb", "夜遅くまでネットサーフィンをする。", "Lướt mạng", "Lướt web đến khuya.", "Surfing the net", "ja_daily_expert"),
    ("サブスク疲れ", "Sabusuku zukare", "noun", "サブスク疲れを感じる。", "Mệt mỏi vì các dịch vụ đăng ký", "Cảm thấy mệt mỏi với phí thuê bao.", "Mệt vì đóng phí hàng tháng", "ja_daily_expert"),
    ("スマホ依存症", "Sumaho izonshou", "noun", "スマホ依存症の若者。", "Hội chứng nghiện smartphone", "Người trẻ nghiện smartphone.", "Nghiện điện thoại", "ja_daily_expert"),
    ("孤独", "Kodoku", "noun/adjective", "都会の孤独。", "Sự cô đơn", "Nỗi cô đơn chốn đô thị.", "Đơn độc", "ja_daily_expert"),
    ("孤立", "Koritsu", "noun/verb", "社会から孤立する。", "Cô lập", "Bị cô lập khỏi xã hội.", "Cách ly, một mình", "ja_daily_expert"),
    ("引きこもり", "Hikikomori", "noun", "引きこもりの支援。", "Người sống khép kín", "Hỗ trợ cho người Hikikomori.", "Hikikomori", "ja_daily_expert"),
    ("承認欲求", "Shounin yokkyuu", "noun", "承認欲求を満たす。", "Mong muốn được công nhận", "Thỏa mãn nhu cầu được chú ý.", "Khát khao công nhận", "ja_daily_expert"),
    ("自己肯定感", "Jiko kouteikan", "noun", "自己肯定感が低い。", "Sự tự khẳng định bản thân", "Mức độ tự trân trọng bản thân thấp.", "Lòng tự tôn", "ja_daily_expert"),
    ("マインドフルネス", "Maindofurunesu", "noun", "マインドフルネス瞑想。", "Chánh niệm", "Thiền chánh niệm.", "Mindfulness", "ja_daily_expert"),
    ("ウェルビーイング", "Werubīingu", "noun", "ウェルビーイングの追求。", "Sự thịnh vượng, hạnh phúc", "Theo đuổi sự hạnh phúc toàn diện.", "Well-being", "ja_daily_expert")
]

new_flashcards = []
for word, phonetic, pos, ex_ja, meaning, ex_vi, tip, deck_id in vocab_data:
    new_flashcards.append({
        "deckId": deck_id,
        "languageCode": "ja",
        "frontWord": word,
        "phonetic": phonetic,
        "partOfSpeech": pos,
        "frontExample": ex_ja,
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

print("Successfully appended 120 words (Daily Life - Japanese) to DB!")
