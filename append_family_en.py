import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_family_beginner", "languageCode": "en", "title": "Gia đình", "subtitle": "Gia đình - Beginner", "iconEmoji": "👨‍👩‍👧‍👦", "level": "Beginner", "colorHex": "#10B981", "cardCount": 30},
    {"id": "en_family_intermediate", "languageCode": "en", "title": "Gia đình", "subtitle": "Gia đình - Intermediate", "iconEmoji": "👨‍👩‍👧‍👦", "level": "Intermediate", "colorHex": "#059669", "cardCount": 30},
    {"id": "en_family_advanced", "languageCode": "en", "title": "Gia đình", "subtitle": "Gia đình - Advanced", "iconEmoji": "👨‍👩‍👧‍👦", "level": "Advanced", "colorHex": "#047857", "cardCount": 30},
    {"id": "en_family_expert", "languageCode": "en", "title": "Gia đình", "subtitle": "Gia đình - Expert", "iconEmoji": "👨‍👩‍👧‍👦", "level": "Expert", "colorHex": "#064E3B", "cardCount": 30}
]

vocab_data = [
    # Beginner
    ("Father", "/ˈfɑː.ðər/", "noun", "My father is tall.", "Bố, cha", "Bố tôi rất cao.", "Dad", 0),
    ("Mother", "/ˈmʌð.ər/", "noun", "My mother loves me.", "Mẹ", "Mẹ tôi rất yêu tôi.", "Mom", 0),
    ("Brother", "/ˈbrʌð.ər/", "noun", "He is my older brother.", "Anh/em trai", "Anh ấy là anh trai tôi.", "Anh em", 0),
    ("Sister", "/ˈsɪs.tər/", "noun", "She has one sister.", "Chị/em gái", "Cô ấy có một người em gái.", "Chị em", 0),
    ("Son", "/sʌn/", "noun", "Their son is five years old.", "Con trai", "Con trai họ năm tuổi.", "Đứa con trai", 0),
    ("Daughter", "/ˈdɔː.tər/", "noun", "She is my only daughter.", "Con gái", "Cô ấy là con gái duy nhất của tôi.", "Đứa con gái", 0),
    ("Grandfather", "/ˈɡræn.fɑː.ðər/", "noun", "My grandfather is very wise.", "Ông (nội/ngoại)", "Ông tôi rất thông thái.", "Ông", 0),
    ("Grandmother", "/ˈɡræn.mʌð.ər/", "noun", "My grandmother bakes cookies.", "Bà (nội/ngoại)", "Bà tôi nướng bánh quy.", "Bà", 0),
    ("Uncle", "/ˈʌŋ.kəl/", "noun", "My uncle lives in London.", "Chú, bác, cậu", "Chú tôi sống ở London.", "Người chú", 0),
    ("Aunt", "/ɑːnt/", "noun", "Aunt Mary is coming today.", "Cô, dì, thím, mợ", "Dì Mary sẽ đến hôm nay.", "Người dì", 0),
    ("Cousin", "/ˈkʌz.ən/", "noun", "I play with my cousin.", "Anh chị em họ", "Tôi chơi với anh họ của mình.", "Anh họ", 0),
    ("Baby", "/ˈbeɪ.bi/", "noun", "The baby is sleeping.", "Em bé", "Em bé đang ngủ.", "Trẻ sơ sinh", 0),
    ("Parents", "/ˈpeə.rənts/", "noun", "My parents are working.", "Bố mẹ", "Bố mẹ tôi đang làm việc.", "Phụ huynh", 0),
    ("Family", "/ˈfæm.əl.i/", "noun", "We have a big family.", "Gia đình", "Chúng tôi có một gia đình lớn.", "Gia đình", 0),
    ("Home", "/həʊm/", "noun", "Let's go home.", "Tổ ấm, nhà", "Hãy về nhà thôi.", "Nhà cửa", 0),
    ("House", "/haʊs/", "noun", "This is my house.", "Ngôi nhà", "Đây là nhà của tôi.", "Căn nhà", 0),
    ("Man", "/mæn/", "noun", "He is a good man.", "Đàn ông", "Ông ấy là một người đàn ông tốt.", "Phái nam", 0),
    ("Woman", "/ˈwʊm.ən/", "noun", "The woman is reading.", "Phụ nữ", "Người phụ nữ đang đọc sách.", "Phái nữ", 0),
    ("Boy", "/bɔɪ/", "noun", "The boy is playing outside.", "Cậu bé", "Cậu bé đang chơi bên ngoài.", "Bé trai", 0),
    ("Girl", "/ɡɜːl/", "noun", "The girl is singing.", "Cô bé", "Cô bé đang hát.", "Bé gái", 0),
    ("Child", "/tʃaɪld/", "noun", "The child is happy.", "Đứa trẻ", "Đứa trẻ rất vui vẻ.", "Trẻ con", 0),
    ("Wife", "/waɪf/", "noun", "He loves his wife.", "Vợ", "Anh ấy yêu vợ mình.", "Người vợ", 0),
    ("Husband", "/ˈhʌz.bənd/", "noun", "Her husband is a doctor.", "Chồng", "Chồng cô ấy là một bác sĩ.", "Người chồng", 0),
    ("Love", "/lʌv/", "verb", "I love my family.", "Yêu, tình yêu", "Tôi yêu gia đình mình.", "Tình cảm", 0),
    ("Care", "/keər/", "verb", "Mothers care for their children.", "Chăm sóc", "Những người mẹ chăm sóc con cái của họ.", "Quan tâm", 0),
    ("Hug", "/hʌɡ/", "verb", "Give me a hug.", "Ôm", "Hãy ôm tôi một cái.", "Cái ôm", 0),
    ("Kiss", "/kɪs/", "verb", "She gave him a kiss.", "Hôn", "Cô ấy đã hôn anh ấy.", "Nụ hôn", 0),
    ("Help", "/help/", "verb", "I help my parents.", "Giúp đỡ", "Tôi giúp đỡ bố mẹ mình.", "Trợ giúp", 0),
    ("Share", "/ʃeər/", "verb", "Share your toys.", "Chia sẻ", "Hãy chia sẻ đồ chơi của bạn.", "Chia ngọt sẻ bùi", 0),
    ("Play", "/pleɪ/", "verb", "I play with my brother.", "Chơi", "Tôi chơi cùng em trai.", "Vui chơi", 0),

    # Intermediate
    ("Relative", "/ˈrel.ə.tɪv/", "noun", "All my relatives came to the party.", "Người họ hàng", "Tất cả họ hàng của tôi đã đến bữa tiệc.", "Bà con", 1),
    ("Nephew", "/ˈnef.juː/", "noun", "My nephew is very smart.", "Cháu trai (của cô/chú/bác)", "Cháu trai tôi rất thông minh.", "Cháu trai", 1),
    ("Niece", "/niːs/", "noun", "I bought a gift for my niece.", "Cháu gái (của cô/chú/bác)", "Tôi đã mua một món quà cho cháu gái mình.", "Cháu gái", 1),
    ("Sibling", "/ˈsɪb.lɪŋ/", "noun", "Do you have any siblings?", "Anh chị em ruột", "Bạn có anh chị em ruột nào không?", "Anh em ruột", 1),
    ("Grandson", "/ˈɡræn.sʌn/", "noun", "He is very proud of his grandson.", "Cháu trai (của ông bà)", "Ông ấy rất tự hào về đứa cháu nội của mình.", "Cháu trai", 1),
    ("Granddaughter", "/ˈɡrænˌdɔː.tər/", "noun", "Her granddaughter is adorable.", "Cháu gái (của ông bà)", "Cháu ngoại của bà ấy rất đáng yêu.", "Cháu gái", 1),
    ("Stepfather", "/ˈstepˌfɑː.ðər/", "noun", "Her stepfather treats her well.", "Bố dượng", "Bố dượng đối xử rất tốt với cô ấy.", "Cha kế", 1),
    ("Stepmother", "/ˈstepˌmʌð.ər/", "noun", "Cinderella's stepmother was cruel.", "Mẹ kế", "Mẹ kế của Lọ Lem rất độc ác.", "Mẹ kế", 1),
    ("Stepbrother", "/ˈstepˌbrʌð.ər/", "noun", "He plays video games with his stepbrother.", "Anh/em trai kế", "Anh ấy chơi game với em trai kế.", "Anh em kế", 1),
    ("Stepsister", "/ˈstepˌsɪs.tər/", "noun", "My stepsister is older than me.", "Chị/em gái kế", "Chị gái kế của tôi lớn tuổi hơn tôi.", "Chị em kế", 1),
    ("Father-in-law", "/ˈfɑː.ðər.ɪnˌlɔː/", "noun", "My father-in-law is a retired teacher.", "Bố chồng/Bố vợ", "Bố vợ tôi là giáo viên đã nghỉ hưu.", "Bố vợ/chồng", 1),
    ("Mother-in-law", "/ˈmʌð.ər.ɪnˌlɔː/", "noun", "She gets along well with her mother-in-law.", "Mẹ chồng/Mẹ vợ", "Cô ấy rất hòa thuận với mẹ chồng.", "Mẹ vợ/chồng", 1),
    ("Brother-in-law", "/ˈbrʌð.ər.ɪnˌlɔː/", "noun", "My brother-in-law helped me move.", "Anh/em rể, anh/em vợ", "Anh rể tôi đã giúp tôi chuyển nhà.", "Anh rể", 1),
    ("Sister-in-law", "/ˈsɪs.tər.ɪnˌlɔː/", "noun", "My sister-in-law is a doctor.", "Chị/em dâu, chị/em vợ", "Chị dâu tôi là một bác sĩ.", "Chị dâu", 1),
    ("Marriage", "/ˈmær.ɪdʒ/", "noun", "They have a happy marriage.", "Cuộc hôn nhân", "Họ có một cuộc hôn nhân hạnh phúc.", "Kết hôn", 1),
    ("Wedding", "/ˈwed.ɪŋ/", "noun", "The wedding was beautiful.", "Đám cưới", "Lễ cưới diễn ra rất đẹp.", "Lễ cưới", 1),
    ("Divorced", "/dɪˈvɔːst/", "adjective", "They got divorced last year.", "Đã ly hôn", "Họ đã ly hôn vào năm ngoái.", "Ly dị", 1),
    ("Single", "/ˈsɪŋ.ɡəl/", "adjective", "He is a single father.", "Độc thân", "Anh ấy là một ông bố đơn thân.", "Đơn thân", 1),
    ("Engaged", "/ɪnˈɡeɪdʒd/", "adjective", "They are engaged to be married.", "Đã đính hôn", "Họ đã đính hôn và chuẩn bị cưới.", "Đính ước", 1),
    ("Pregnant", "/ˈpreɡ.nənt/", "adjective", "She is six months pregnant.", "Mang thai", "Cô ấy đang mang thai sáu tháng.", "Có thai", 1),
    ("Toddler", "/ˈtɒd.lər/", "noun", "The toddler is learning to walk.", "Trẻ mới biết đi", "Đứa trẻ lẫm chẫm đang học đi.", "Trẻ lẫm chẫm", 1),
    ("Teenager", "/ˈtiːnˌeɪ.dʒər/", "noun", "It's hard to raise a teenager.", "Thanh thiếu niên", "Nuôi dạy một thiếu niên rất khó khăn.", "Tuổi teen", 1),
    ("Adult", "/ˈæd.ʌlt/", "noun", "He is now an independent adult.", "Người trưởng thành", "Anh ấy giờ đã là một người lớn độc lập.", "Người lớn", 1),
    ("Ancestor", "/ˈæn.ses.tər/", "noun", "My ancestors came from Italy.", "Tổ tiên", "Tổ tiên của tôi đến từ Ý.", "Người đi trước", 1),
    ("Descendant", "/dɪˈsen.dənt/", "noun", "He is a descendant of a king.", "Hậu duệ", "Anh ấy là hậu duệ của một vị vua.", "Người nối dõi", 1),
    ("Orphan", "/ˈɔː.fən/", "noun", "The war left him an orphan.", "Trẻ mồ côi", "Chiến tranh khiến cậu bé trở thành trẻ mồ côi.", "Mồ côi", 1),
    ("Adopt", "/əˈdɒpt/", "verb", "They decided to adopt a child.", "Nhận nuôi", "Họ quyết định nhận nuôi một đứa trẻ.", "Nhận làm con", 1),
    ("Foster", "/ˈfɒs.tər/", "verb", "She is a foster mother.", "Nuôi dưỡng (tạm thời)", "Bà ấy là một người mẹ nuôi dưỡng.", "Nuôi hộ", 1),
    ("Guardian", "/ˈɡɑː.di.ən/", "noun", "His uncle is his legal guardian.", "Người giám hộ", "Chú của anh ấy là người giám hộ hợp pháp.", "Bảo hộ", 1),
    ("Generation", "/ˌdʒen.əˈreɪ.ʃən/", "noun", "Three generations live in this house.", "Thế hệ", "Ba thế hệ cùng sống trong ngôi nhà này.", "Thế hệ", 1),

    # Advanced
    ("Genealogy", "/ˌdʒiː.niˈæl.ə.dʒi/", "noun", "I am researching my family's genealogy.", "Gia phả học", "Tôi đang nghiên cứu gia phả của gia đình.", "Gia phả", 2),
    ("Heritage", "/ˈher.ɪ.tɪdʒ/", "noun", "We must protect our cultural heritage.", "Di sản, dòng máu", "Chúng ta phải bảo vệ di sản văn hóa của mình.", "Di sản", 2),
    ("Lineage", "/ˈlɪn.i.ɪdʒ/", "noun", "He comes from a noble lineage.", "Dòng dõi", "Anh ta xuất thân từ một dòng dõi quý tộc.", "Dòng máu", 2),
    ("Matriarch", "/ˈmeɪ.tri.ɑːk/", "noun", "My grandmother is the matriarch of our family.", "Nữ trưởng tộc", "Bà nội tôi là nữ trưởng tộc của gia đình.", "Nữ quyền", 2),
    ("Patriarch", "/ˈpeɪ.tri.ɑːk/", "noun", "The patriarch made all the major decisions.", "Nam trưởng tộc", "Gia trưởng đã đưa ra mọi quyết định lớn.", "Gia trưởng", 2),
    ("Offspring", "/ˈɒf.sprɪŋ/", "noun", "A mother bird protects her offspring.", "Con cái, con cháu", "Chim mẹ bảo vệ những đứa con của nó.", "Đàn con", 2),
    ("Kinship", "/ˈkɪn.ʃɪp/", "noun", "There is a strong bond of kinship between them.", "Quan hệ họ hàng", "Có một mối quan hệ họ hàng bền chặt giữa họ.", "Tình ruột thịt", 2),
    ("Spousal", "/ˈspaʊ.zəl/", "adjective", "He requested spousal support after the divorce.", "Thuộc về vợ chồng", "Anh ta yêu cầu tiền cấp dưỡng vợ chồng sau ly hôn.", "Vợ/chồng", 2),
    ("Nuptial", "/ˈnʌp.ʃəl/", "adjective", "They signed a pre-nuptial agreement.", "Thuộc về lễ cưới", "Họ đã ký một thỏa thuận tiền hôn nhân.", "Lễ cưới", 2),
    ("Domestic", "/dəˈmes.tɪk/", "adjective", "Domestic violence is a serious crime.", "Trong nhà, gia đình", "Bạo lực gia đình là một tội ác nghiêm trọng.", "Gia đình", 2),
    ("Upbringing", "/ˈʌpˌbrɪŋ.ɪŋ/", "noun", "Her strict upbringing shaped her character.", "Sự nuôi dưỡng, giáo dục", "Sự nuôi dưỡng nghiêm khắc đã hình thành nên tính cách của cô ấy.", "Sự dạy dỗ", 2),
    ("Nurture", "/ˈnɜː.tʃər/", "verb", "Parents must nurture their children's talents.", "Nuôi dưỡng, bồi đắp", "Cha mẹ phải bồi đắp tài năng của con cái.", "Chăm bẵm", 2),
    ("Maternal", "/məˈtɜː.nəl/", "adjective", "She has strong maternal instincts.", "Thuộc về người mẹ", "Cô ấy có bản năng làm mẹ rất mạnh mẽ.", "Bên ngoại/Mẹ", 2),
    ("Paternal", "/pəˈtɜː.nəl/", "adjective", "His paternal grandfather was a soldier.", "Thuộc về người cha", "Ông nội của anh ấy từng là một người lính.", "Bên nội/Cha", 2),
    ("Fraternal", "/frəˈtɜː.nəl/", "adjective", "They are fraternal twins.", "Thuộc về anh em", "Họ là anh em sinh đôi khác trứng.", "Tình anh em", 2),
    ("Dynasty", "/ˈdɪn.ə.sti/", "noun", "The Ming dynasty ruled China for centuries.", "Triều đại, dòng họ lớn", "Triều đại nhà Minh đã cai trị Trung Quốc trong nhiều thế kỷ.", "Triều đại", 2),
    ("Hereditary", "/hɪˈred.ɪ.tər.i/", "adjective", "Baldness can be a hereditary trait.", "Có tính di truyền", "Chứng hói đầu có thể là một đặc điểm di truyền.", "Di truyền", 2),
    ("Heir", "/eər/", "noun", "He is the sole heir to the fortune.", "Người thừa kế", "Anh ấy là người thừa kế duy nhất của khối tài sản.", "Thừa kế", 2),
    ("Successor", "/səkˈses.ər/", "noun", "The CEO is training his successor.", "Người kế vị", "Giám đốc đang đào tạo người kế vị của mình.", "Kế nhiệm", 2),
    ("Progeny", "/ˈprɒdʒ.ə.ni/", "noun", "He left his wealth to his progeny.", "Dòng dõi, con cháu", "Ông ta để lại tài sản cho con cháu mình.", "Đời sau", 2),
    ("Consanguinity", "/ˌkɒn.sæŋˈɡwɪn.ə.ti/", "noun", "Marriage is forbidden in cases of close consanguinity.", "Quan hệ cùng huyết thống", "Hôn nhân bị cấm trong các trường hợp cận huyết thống.", "Cùng máu mủ", 2),
    ("Cohabitation", "/kəʊˌhæb.ɪˈteɪ.ʃən/", "noun", "Cohabitation before marriage is common today.", "Sự sống thử, chung sống", "Sống thử trước hôn nhân rất phổ biến ngày nay.", "Sống chung", 2),
    ("Estranged", "/ɪˈstreɪndʒd/", "adjective", "He tried to contact his estranged wife.", "Bị ly thân, xa lánh", "Anh ta cố gắng liên lạc với người vợ đã ly thân.", "Lạnh nhạt", 2),
    ("Alimony", "/ˈæl.ɪ.mə.ni/", "noun", "He pays alimony to his ex-wife.", "Tiền cấp dưỡng (sau ly hôn)", "Anh ta trả tiền cấp dưỡng cho vợ cũ.", "Cấp dưỡng", 2),
    ("Bereavement", "/bɪˈriːv.mənt/", "noun", "She suffered a great bereavement.", "Sự tang tóc, mất người thân", "Cô ấy đã phải chịu đựng một nỗi mất mát lớn.", "Tang tóc", 2),
    ("Widow", "/ˈwɪd.əʊ/", "noun", "She became a widow at a young age.", "Quả phụ, góa phụ", "Cô ấy trở thành góa phụ khi còn trẻ.", "Vợ góa chồng", 2),
    ("Widower", "/ˈwɪd.əʊ.ər/", "noun", "The widower raised his children alone.", "Người góa vợ", "Người đàn ông góa vợ đã tự mình nuôi nấng các con.", "Chồng góa vợ", 2),
    ("Prenuptial", "/ˌpriːˈnʌp.ʃəl/", "adjective", "They signed a prenuptial agreement.", "Tiền hôn nhân", "Họ đã ký một hợp đồng tiền hôn nhân.", "Trước khi cưới", 2),
    ("Patrimony", "/ˈpæt.rɪ.mə.ni/", "noun", "He squandered his family patrimony.", "Gia tài thừa kế từ cha", "Anh ta đã phung phí gia tài thừa kế từ người cha.", "Tài sản cha để lại", 2),
    ("Matrimony", "/ˈmæt.rɪ.mə.ni/", "noun", "They were joined in holy matrimony.", "Hôn nhân", "Họ đã gắn kết với nhau bằng cuộc hôn nhân thiêng liêng.", "Kết hôn", 2),

    # Expert
    ("Primogeniture", "/ˌpraɪ.məʊˈdʒen.ɪ.tʃər/", "noun", "The crown passes by primogeniture.", "Quyền trưởng nam kế vị", "Vương miện được truyền lại theo quyền trưởng nam kế vị.", "Con cả thừa kế", 3),
    ("Endogamy", "/enˈdɒɡ.ə.mi/", "noun", "Endogamy is the practice of marrying within a specific group.", "Tục nội hôn", "Nội hôn là tục lệ kết hôn trong một nhóm cụ thể.", "Cưới cùng tộc", 3),
    ("Exogamy", "/ekˈsɒɡ.ə.mi/", "noun", "Exogamy prevents inbreeding.", "Tục ngoại hôn", "Ngoại hôn ngăn ngừa giao phối cận huyết.", "Cưới khác tộc", 3),
    ("Polygamy", "/pəˈlɪɡ.ə.mi/", "noun", "Polygamy is illegal in many countries.", "Chế độ đa thê/đa phu", "Chế độ đa thê/đa phu là bất hợp pháp ở nhiều quốc gia.", "Nhiều vợ/chồng", 3),
    ("Monogamy", "/məˈnɒɡ.ə.mi/", "noun", "Most societies practice monogamy.", "Chế độ một vợ một chồng", "Hầu hết các xã hội thực hành chế độ một vợ một chồng.", "Một vợ một chồng", 3),
    ("Polygyny", "/pəˈlɪdʒ.ɪ.ni/", "noun", "Polygyny allows a man to have multiple wives.", "Chế độ đa thê", "Chế độ đa thê cho phép một người đàn ông có nhiều vợ.", "Nhiều vợ", 3),
    ("Polyandry", "/ˈpɒl.i.æn.dri/", "noun", "Polyandry is rare across human cultures.", "Chế độ đa phu", "Chế độ đa phu rất hiếm trong các nền văn hóa nhân loại.", "Nhiều chồng", 3),
    ("Patrilineal", "/ˌpæt.rɪˈlɪn.i.əl/", "adjective", "They live in a patrilineal society.", "Theo dòng nội", "Họ sống trong một xã hội theo dòng cha.", "Theo họ cha", 3),
    ("Matrilineal", "/ˌmæt.rɪˈlɪn.i.əl/", "adjective", "Property is passed down in a matrilineal line.", "Theo dòng ngoại", "Tài sản được truyền lại theo dòng ngoại.", "Theo họ mẹ", 3),
    ("Neolocal", "/ˌniː.əʊˈləʊ.kəl/", "adjective", "A neolocal residence is established by newlyweds.", "Sống ra riêng (sau cưới)", "Nơi ở mới được thiết lập bởi cặp vợ chồng mới cưới.", "Ở riêng", 3),
    ("Avunculocal", "/əˌvʌŋ.kjʊˈləʊ.kəl/", "adjective", "In an avunculocal society, boys live with their mother's brother.", "Chế độ cư trú bên cậu", "Trong xã hội độ cư trú bên cậu, các cậu bé sống với anh trai của mẹ mình.", "Ở nhà họ hàng", 3),
    ("Consanguineous", "/ˌkɒn.sæŋˈɡwɪn.i.əs/", "adjective", "Consanguineous marriages increase genetic risks.", "Đồng huyết", "Hôn nhân cận huyết làm tăng rủi ro di truyền.", "Cùng huyết thống", 3),
    ("Affinal", "/əˈfaɪ.nəl/", "adjective", "Affinal relatives are related by marriage.", "Có quan hệ thông gia", "Họ hàng thông gia là do hôn nhân mang lại.", "Quan hệ kết hôn", 3),
    ("Fictive kin", "/ˈfɪk.tɪv kɪn/", "noun", "Close friends are often considered fictive kin.", "Bà con nuôi, họ hàng giả định", "Những người bạn thân thường được coi như họ hàng ruột thịt.", "Người như ruột thịt", 3),
    ("Genogram", "/ˈdʒen.ə.ɡræm/", "noun", "A genogram maps out family relationships visually.", "Biểu đồ gia phả (tâm lý học)", "Một genogram lập sơ đồ các mối quan hệ gia đình một cách trực quan.", "Bản đồ gia đình", 3),
    ("Nuclear family", "/ˌnjuː.klɪə ˈfæm.əl.i/", "noun", "A nuclear family consists of parents and children.", "Gia đình hạt nhân", "Một gia đình hạt nhân bao gồm cha mẹ và con cái.", "Gia đình cơ bản", 3),
    ("Extended family", "/ɪkˌsten.dɪd ˈfæm.əl.i/", "noun", "My extended family includes aunts and uncles.", "Đại gia đình", "Đại gia đình của tôi bao gồm các cô và chú.", "Gia đình nhiều thế hệ", 3),
    ("Blended family", "/ˌblen.dɪd ˈfæm.əl.i/", "noun", "They formed a blended family after remarrying.", "Gia đình chắp vá (con chung con riêng)", "Họ đã tạo nên một gia đình chắp vá sau khi tái hôn.", "Gia đình tái giá", 3),
    ("Emancipation", "/ɪˌmæn.sɪˈpeɪ.ʃən/", "noun", "The teenager sought legal emancipation.", "Sự giải phóng, tự lập pháp lý", "Cậu thiếu niên tìm kiếm sự tự lập hợp pháp khỏi cha mẹ.", "Sự giải phóng", 3),
    ("Filial piety", "/ˈfɪl.i.əl ˈpaɪ.ə.ti/", "noun", "Filial piety is highly valued in Asian cultures.", "Đạo hiếu", "Đạo hiếu rất được coi trọng trong văn hóa Á Đông.", "Lòng hiếu thảo", 3),
    ("Procreation", "/ˌprəʊ.kriˈeɪ.ʃən/", "noun", "Procreation is essential for human survival.", "Sự sinh sản", "Sự sinh sản là rất cần thiết cho sự sống còn của nhân loại.", "Sinh con", 3),
    ("Sororate", "/sɒˈrɔː.rɪt/", "noun", "Sororate marriage involves a man marrying his wife's sister.", "Tục cưới chị/em gái của vợ (khi vợ mất)", "Tục hôn nhân sororate là người đàn ông cưới em gái của vợ.", "Cưới em vợ", 3),
    ("Levirate", "/ˈliː.vɪ.rɪt/", "noun", "Levirate marriage requires a man to marry his brother's widow.", "Tục cưới vợ của anh/em trai (khi họ mất)", "Tục hôn nhân levirate yêu cầu người đàn ông cưới góa phụ của anh trai.", "Cưới chị dâu", 3),
    ("Matrifocal", "/ˌmæt.rɪˈfəʊ.kəl/", "adjective", "In a matrifocal family, the mother is the head.", "Gia đình mẫu hệ", "Trong gia đình mẫu hệ, người mẹ là trụ cột.", "Mẹ làm chủ", 3),
    ("Patrifocal", "/ˌpæt.rɪˈfəʊ.kəl/", "adjective", "A patrifocal structure centers around the father.", "Gia đình phụ hệ", "Cấu trúc phụ hệ xoay quanh người cha.", "Cha làm chủ", 3),
    ("Egalitarian family", "/ɪˌɡæl.ɪˈteə.ri.ən ˈfæm.əl.i/", "noun", "Power is shared equally in an egalitarian family.", "Gia đình bình quyền", "Quyền lực được chia sẻ bình đẳng trong một gia đình bình quyền.", "Bình đẳng", 3),
    ("Empty nest", "/ˌemp.ti ˈnest/", "noun", "They suffer from empty nest syndrome.", "Hội chứng tổ ấm trống trải (con cái ra riêng)", "Họ mắc hội chứng tổ ấm trống trải.", "Nhà vắng con", 3),
    ("Boomerang child", "/ˈbuː.mə.ræŋ tʃaɪld/", "noun", "He became a boomerang child after losing his job.", "Đứa con quay về nhà ở cùng bố mẹ (khi đã lớn)", "Anh ấy trở thành 'đứa con boomerang' sau khi mất việc.", "Con dọn về ở cùng", 3),
    ("Sandwich generation", "/ˈsæn.wɪdʒ ˌdʒen.əˈreɪ.ʃən/", "noun", "The sandwich generation cares for both parents and kids.", "Thế hệ bị kẹp giữa (phải chăm cả cha mẹ già lẫn con nhỏ)", "Thế hệ bị kẹp giữa chăm lo cho cả bố mẹ già và con cái.", "Kẹp giữa", 3),
    ("Kith", "/kɪθ/", "noun", "He invited all his kith and kin.", "Người quen thân", "Ông ta đã mời tất cả bạn bè và người thân.", "Kith and kin", 3)
]

level_map = ["en_family_beginner", "en_family_intermediate", "en_family_advanced", "en_family_expert"]

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

print("Successfully appended 120 words for Family (English) to DB!")
