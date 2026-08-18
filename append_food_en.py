import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_food_beginner", "languageCode": "en", "title": "Đồ ăn-uống", "subtitle": "Đồ ăn-uống - Beginner", "iconEmoji": "🍔", "level": "Beginner", "colorHex": "#F97316", "cardCount": 30},
    {"id": "en_food_intermediate", "languageCode": "en", "title": "Đồ ăn-uống", "subtitle": "Đồ ăn-uống - Intermediate", "iconEmoji": "🍔", "level": "Intermediate", "colorHex": "#EA580C", "cardCount": 30},
    {"id": "en_food_advanced", "languageCode": "en", "title": "Đồ ăn-uống", "subtitle": "Đồ ăn-uống - Advanced", "iconEmoji": "🍔", "level": "Advanced", "colorHex": "#C2410C", "cardCount": 30},
    {"id": "en_food_expert", "languageCode": "en", "title": "Đồ ăn-uống", "subtitle": "Đồ ăn-uống - Expert", "iconEmoji": "🍔", "level": "Expert", "colorHex": "#9A3412", "cardCount": 30}
]

vocab_data = [
    # Beginner (0)
    ("Food", "/fuːd/", "noun", "I love healthy food.", "Đồ ăn", "Tôi yêu đồ ăn tốt cho sức khỏe.", "Thức ăn", 0),
    ("Drink", "/drɪŋk/", "verb", "I drink water every day.", "Uống", "Tôi uống nước mỗi ngày.", "Uống nước", 0),
    ("Water", "/ˈwɔː.tər/", "noun", "A glass of water.", "Nước", "Một cốc nước.", "Chất lỏng thiết yếu", 0),
    ("Milk", "/mɪlk/", "noun", "Drink a glass of milk.", "Sữa", "Uống một cốc sữa.", "Sữa trắng", 0),
    ("Tea", "/tiː/", "noun", "Would you like some tea?", "Trà", "Bạn có muốn một chút trà không?", "Nước trà", 0),
    ("Coffee", "/ˈkɒf.i/", "noun", "A cup of hot coffee.", "Cà phê", "Một tách cà phê nóng.", "Cà phê đen", 0),
    ("Juice", "/dʒuːs/", "noun", "Fresh orange juice.", "Nước ép", "Nước ép cam tươi.", "Nước hoa quả", 0),
    ("Bread", "/bred/", "noun", "Bake some bread.", "Bánh mì", "Nướng một ít bánh mì.", "Làm từ bột mì", 0),
    ("Rice", "/raɪs/", "noun", "A bowl of rice.", "Cơm, gạo", "Một bát cơm.", "Thức ăn chính ở châu Á", 0),
    ("Meat", "/miːt/", "noun", "I don't eat meat.", "Thịt", "Tôi không ăn thịt.", "Thịt động vật", 0),
    ("Fish", "/fɪʃ/", "noun", "We had fish for dinner.", "Cá", "Chúng tôi đã ăn cá vào bữa tối.", "Sinh vật bơi", 0),
    ("Chicken", "/ˈtʃɪk.ɪn/", "noun", "Fried chicken is tasty.", "Thịt gà", "Gà rán rất ngon.", "Gia cầm", 0),
    ("Egg", "/eɡ/", "noun", "Boil an egg.", "Trứng", "Luộc một quả trứng.", "Sinh ra từ gà", 0),
    ("Cheese", "/tʃiːz/", "noun", "Cheese and wine.", "Phô mai", "Phô mai và rượu vang.", "Làm từ sữa", 0),
    ("Fruit", "/fruːt/", "noun", "Eat more fresh fruit.", "Trái cây", "Hãy ăn nhiều trái cây tươi.", "Hoa quả", 0),
    ("Apple", "/ˈæp.əl/", "noun", "A red apple.", "Quả táo", "Một quả táo đỏ.", "Táo", 0),
    ("Banana", "/bəˈnɑː.nə/", "noun", "Peel a banana.", "Quả chuối", "Bóc vỏ một quả chuối.", "Chuối vàng", 0),
    ("Orange", "/ˈɒr.ɪndʒ/", "noun", "Sweet orange.", "Quả cam", "Quả cam ngọt.", "Cam", 0),
    ("Vegetable", "/ˈvedʒ.tə.bəl/", "noun", "Green vegetables.", "Rau củ", "Các loại rau xanh.", "Rau", 0),
    ("Potato", "/pəˈteɪ.təʊ/", "noun", "Mashed potato.", "Khoai tây", "Khoai tây nghiền.", "Khoai tây", 0),
    ("Tomato", "/təˈmɑː.təʊ/", "noun", "Tomato soup.", "Cà chua", "Súp cà chua.", "Cà chua đỏ", 0),
    ("Onion", "/ˈʌn.jən/", "noun", "Chop the onion.", "Hành tây", "Thái nhỏ hành tây.", "Củ hành", 0),
    ("Cake", "/keɪk/", "noun", "A birthday cake.", "Bánh ngọt", "Một chiếc bánh sinh nhật.", "Bánh nướng ngọt", 0),
    ("Sugar", "/ˈʃʊɡ.ər/", "noun", "Add some sugar.", "Đường", "Thêm một chút đường.", "Chất tạo ngọt", 0),
    ("Salt", "/sɒlt/", "noun", "Pass the salt, please.", "Muối", "Làm ơn đưa muối cho tôi.", "Gia vị mặn", 0),
    ("Soup", "/suːp/", "noun", "Hot chicken soup.", "Món súp", "Súp gà nóng.", "Canh súp", 0),
    ("Breakfast", "/ˈbrek.fəst/", "noun", "Eat breakfast early.", "Bữa sáng", "Ăn sáng sớm.", "Bữa đầu ngày", 0),
    ("Lunch", "/lʌntʃ/", "noun", "Have lunch at noon.", "Bữa trưa", "Ăn trưa vào giữa ngày.", "Bữa giữa ngày", 0),
    ("Dinner", "/ˈdɪn.ər/", "noun", "A romantic dinner.", "Bữa tối", "Một bữa tối lãng mạn.", "Bữa cuối ngày", 0),
    ("Eat", "/iːt/", "verb", "Let's eat together.", "Ăn", "Hãy cùng nhau ăn nhé.", "Cho thức ăn vào miệng", 0),

    # Intermediate (1)
    ("Meal", "/miːl/", "noun", "A heavy meal.", "Bữa ăn", "Một bữa ăn thịnh soạn.", "Bữa ăn", 1),
    ("Snack", "/snæk/", "noun", "A midnight snack.", "Bữa ăn nhẹ", "Một bữa ăn nhẹ lúc nửa đêm.", "Đồ ăn vặt", 1),
    ("Dessert", "/dɪˈzɜːt/", "noun", "What is for dessert?", "Món tráng miệng", "Món tráng miệng là gì?", "Đồ ngọt sau ăn", 1),
    ("Recipe", "/ˈres.ɪ.pi/", "noun", "A secret recipe.", "Công thức nấu ăn", "Một công thức bí mật.", "Hướng dẫn nấu", 1),
    ("Ingredient", "/ɪnˈɡriː.di.ənt/", "noun", "Fresh ingredients.", "Nguyên liệu", "Những nguyên liệu tươi ngon.", "Thành phần món ăn", 1),
    ("Pepper", "/ˈpep.ər/", "noun", "Salt and pepper.", "Hạt tiêu", "Muối và hạt tiêu.", "Tiêu", 1),
    ("Garlic", "/ˈɡɑː.lɪk/", "noun", "Garlic bread.", "Tỏi", "Bánh mì bơ tỏi.", "Củ tỏi", 1),
    ("Butter", "/ˈbʌt.ər/", "noun", "Melt the butter.", "Bơ", "Làm chảy bơ.", "Bơ từ sữa", 1),
    ("Oil", "/ɔɪl/", "noun", "Olive oil.", "Dầu ăn", "Dầu ô-liu.", "Dầu nấu ăn", 1),
    ("Sauce", "/sɔːs/", "noun", "Tomato sauce.", "Nước sốt", "Nước sốt cà chua.", "Nước xốt", 1),
    ("Salad", "/ˈsæl.əd/", "noun", "A green salad.", "Món rau trộn", "Một món xà lách xanh.", "Rau trộn", 1),
    ("Beef", "/biːf/", "noun", "Roast beef.", "Thịt bò", "Thịt bò nướng.", "Thịt bò", 1),
    ("Pork", "/pɔːk/", "noun", "Pork chops.", "Thịt heo", "Sườn heo.", "Thịt heo", 1),
    ("Seafood", "/ˈsiː.fuːd/", "noun", "A seafood restaurant.", "Hải sản", "Một nhà hàng hải sản.", "Đồ ăn từ biển", 1),
    ("Shrimp", "/ʃrɪmp/", "noun", "Grilled shrimp.", "Tôm", "Tôm nướng.", "Con tôm", 1),
    ("Crab", "/kræb/", "noun", "Crab meat.", "Cua", "Thịt cua.", "Con cua", 1),
    ("Noodle", "/ˈnuː.dəl/", "noun", "Instant noodles.", "Mì", "Mì ăn liền.", "Sợi mì", 1),
    ("Pasta", "/ˈpæs.tə/", "noun", "Italian pasta.", "Mì Ý", "Mì Ý.", "Mì ống Ý", 1),
    ("Pizza", "/ˈpiːt.sə/", "noun", "A slice of pizza.", "Bánh pizza", "Một lát bánh pizza.", "Bánh tròn Ý", 1),
    ("Sandwich", "/ˈsæn.wɪdʒ/", "noun", "A ham sandwich.", "Bánh mì kẹp", "Một chiếc bánh mì kẹp giăm bông.", "Bánh mì kẹp", 1),
    ("Biscuit", "/ˈbɪs.kɪt/", "noun", "A chocolate biscuit.", "Bánh quy", "Một chiếc bánh quy sô-cô-la.", "Bánh quy", 1),
    ("Chocolate", "/ˈtʃɒk.lət/", "noun", "Dark chocolate.", "Sô-cô-la", "Sô-cô-la đen.", "Kẹo cacao", 1),
    ("Ice cream", "/ˌaɪs ˈkriːm/", "noun", "Vanilla ice cream.", "Kem", "Kem va-ni.", "Kem lạnh", 1),
    ("Yogurt", "/ˈjɒɡ.ət/", "noun", "Strawberry yogurt.", "Sữa chua", "Sữa chua dâu tây.", "Sữa chua lên men", 1),
    ("Honey", "/ˈhʌn.i/", "noun", "Milk and honey.", "Mật ong", "Sữa và mật ong.", "Mật ong", 1),
    ("Peanut", "/ˈpiː.nʌt/", "noun", "Peanut butter.", "Đậu phộng", "Bơ đậu phộng.", "Lạc", 1),
    ("Mushroom", "/ˈmʌʃ.ruːm/", "noun", "Wild mushroom.", "Nấm", "Nấm rừng.", "Cây nấm", 1),
    ("Carrot", "/ˈkær.ət/", "noun", "Carrot juice.", "Cà rốt", "Nước ép cà rốt.", "Củ cà rốt", 1),
    ("Lemon", "/ˈlem.ən/", "noun", "A slice of lemon.", "Quả chanh vàng", "Một lát chanh.", "Chanh vàng", 1),
    ("Grape", "/ɡreɪp/", "noun", "A bunch of grapes.", "Quả nho", "Một chùm nho.", "Nho", 1),

    # Advanced (2)
    ("Nutrition", "/njuːˈtrɪʃ.ən/", "noun", "Good nutrition is vital.", "Dinh dưỡng", "Dinh dưỡng tốt là điều sống còn.", "Chất bổ dưỡng", 2),
    ("Diet", "/ˈdaɪ.ət/", "noun", "A balanced diet.", "Chế độ ăn", "Một chế độ ăn uống cân bằng.", "Khẩu phần ăn", 2),
    ("Vegetarian", "/ˌvedʒ.ɪˈteə.ri.ən/", "noun", "She is a vegetarian.", "Người ăn chay", "Cô ấy là một người ăn chay.", "Không ăn thịt", 2),
    ("Vegan", "/ˈviː.ɡən/", "noun", "A strict vegan diet.", "Người ăn thuần chay", "Chế độ ăn thuần chay nghiêm ngặt.", "Không ăn sản phẩm từ động vật", 2),
    ("Cuisine", "/kwɪˈziːn/", "noun", "French cuisine.", "Ẩm thực", "Ẩm thực Pháp.", "Phong cách nấu ăn", 2),
    ("Appetite", "/ˈæp.ə.taɪt/", "noun", "Lose one's appetite.", "Sự thèm ăn", "Mất cảm giác ngon miệng.", "Cảm giác muốn ăn", 2),
    ("Beverage", "/ˈbev.ər.ɪdʒ/", "noun", "Alcoholic beverages.", "Đồ uống", "Đồ uống có cồn.", "Thức uống (trang trọng)", 2),
    ("Refreshment", "/rɪˈfreʃ.mənt/", "noun", "Light refreshments.", "Đồ ăn uống giải khát", "Những món ăn nhẹ và đồ uống.", "Giải khát", 2),
    ("Delicacy", "/ˈdel.ɪ.kə.si/", "noun", "A local delicacy.", "Đặc sản", "Một món đặc sản địa phương.", "Món ngon đắt tiền", 2),
    ("Appetizer", "/ˈæp.ə.taɪ.zər/", "noun", "Order an appetizer.", "Món khai vị", "Gọi một món khai vị.", "Món ăn mở màn", 2),
    ("Main course", "/ˌmeɪn ˈkɔːs/", "noun", "Fish for the main course.", "Món chính", "Cá cho món chính.", "Món ăn trung tâm", 2),
    ("Portion", "/ˈpɔː.ʃən/", "noun", "A large portion.", "Khẩu phần", "Một khẩu phần ăn lớn.", "Phần ăn", 2),
    ("Calorie", "/ˈkæl.ər.i/", "noun", "Low in calories.", "Ca-lo", "Ít ca-lo.", "Đơn vị năng lượng", 2),
    ("Protein", "/ˈprəʊ.tiːn/", "noun", "High protein diet.", "Chất đạm", "Chế độ ăn giàu đạm.", "Chất đạm", 2),
    ("Carbohydrate", "/ˌkɑː.bəʊˈhaɪ.dreɪt/", "noun", "Complex carbohydrates.", "Chất bột đường", "Tinh bột phức tạp.", "Chất đường bột (Carb)", 2),
    ("Vitamin", "/ˈvɪt.ə.mɪn/", "noun", "Vitamin C.", "Vi-ta-min", "Vi-ta-min C.", "Sinh tố", 2),
    ("Mineral", "/ˈmɪn.ər.əl/", "noun", "Vitamins and minerals.", "Khoáng chất", "Vi-ta-min và khoáng chất.", "Khoáng chất", 2),
    ("Fiber", "/ˈfaɪ.bər/", "noun", "Dietary fiber.", "Chất xơ", "Chất xơ trong chế độ ăn.", "Chất xơ", 2),
    ("Cholesterol", "/kəˈles.tər.ɒl/", "noun", "High cholesterol levels.", "Chô-lét-tê-rôn", "Mức chô-lét-tê-rôn cao.", "Chất béo trong máu", 2),
    ("Allergy", "/ˈæl.ə.dʒi/", "noun", "Food allergy.", "Dị ứng", "Dị ứng thực phẩm.", "Phản ứng của cơ thể", 2),
    ("Spices", "/spaɪs/", "noun", "Indian spices.", "Gia vị", "Gia vị Ấn Độ.", "Hương liệu nêm nếm", 2),
    ("Herb", "/hɜːb/", "noun", "Fresh herbs.", "Thảo mộc, rau thơm", "Rau thơm tươi.", "Rau gia vị", 2),
    ("Vinegar", "/ˈvɪn.ɪ.ɡər/", "noun", "Oil and vinegar.", "Giấm", "Dầu và giấm.", "Chất chua từ rượu lên men", 2),
    ("Mustard", "/ˈmʌs.təd/", "noun", "Hot mustard.", "Mù tạt", "Mù tạt cay.", "Gia vị cay nồng vàng", 2),
    ("Pastry", "/ˈpeɪ.stri/", "noun", "A flaky pastry.", "Bánh ngọt (bột nhào)", "Một loại bánh ngọt nhiều lớp bột.", "Bánh nướng", 2),
    ("Dough", "/dəʊ/", "noun", "Knead the dough.", "Bột nhào", "Nhào bột.", "Cục bột đã trộn nước", 2),
    ("Yeast", "/jiːst/", "noun", "Add the yeast.", "Men nở", "Thêm men nở vào.", "Chất làm nở bánh", 2),
    ("Broth", "/brɒθ/", "noun", "Chicken broth.", "Nước dùng", "Nước dùng gà.", "Nước lèo hầm xương", 2),
    ("Poultry", "/ˈpəʊl.tri/", "noun", "Eat more poultry.", "Gia cầm", "Ăn nhiều gia cầm hơn.", "Thịt các loài chim", 2),
    ("Mutton", "/ˈmʌt.ən/", "noun", "Roast mutton.", "Thịt cừu", "Thịt cừu nướng.", "Thịt cừu lớn", 2),

    # Expert (3)
    ("Gastronomy", "/ɡæsˈtrɒn.ə.mi/", "noun", "Molecular gastronomy.", "Nghệ thuật ẩm thực", "Nghệ thuật ẩm thực phân tử.", "Khoa học ẩm thực", 3),
    ("Culinary", "/ˈkʌl.ɪ.nər.i/", "adjective", "Culinary skills.", "Thuộc về nấu nướng", "Kỹ năng bếp núc.", "Liên quan đến bếp", 3),
    ("Gourmet", "/ˈɡɔː.meɪ/", "noun", "A gourmet chef.", "Người sành ăn, hảo hạng", "Một đầu bếp hảo hạng.", "Đồ ăn cao cấp", 3),
    ("Connoisseur", "/ˌkɒn.əˈsɜːr/", "noun", "A wine connoisseur.", "Chuyên gia am hiểu", "Một chuyên gia về rượu vang.", "Người am hiểu sâu", 3),
    ("Sommelier", "/sɒˈmel.i.eɪ/", "noun", "Ask the sommelier.", "Chuyên gia thử nếm rượu vang", "Hãy hỏi chuyên gia thử rượu.", "Nhân viên phục vụ rượu", 3),
    ("Condiment", "/ˈkɒn.dɪ.mənt/", "noun", "Sauces and condiments.", "Đồ gia vị", "Nước sốt và gia vị.", "Gia vị ăn kèm", 3),
    ("Preservative", "/prɪˈzɜː.və.tɪv/", "noun", "No artificial preservatives.", "Chất bảo quản", "Không có chất bảo quản nhân tạo.", "Chất giữ thực phẩm lâu", 3),
    ("Additive", "/ˈæd.ɪ.tɪv/", "noun", "Food additives.", "Chất phụ gia", "Phụ gia thực phẩm.", "Chất thêm vào thực phẩm", 3),
    ("Pasteurization", "/ˌpæs.tʃər.aɪˈzeɪ.ʃən/", "noun", "Pasteurization of milk.", "Sự thanh trùng", "Sự thanh trùng sữa.", "Diệt khuẩn bằng nhiệt độ", 3),
    ("Fermentation", "/ˌfɜː.menˈteɪ.ʃən/", "noun", "Natural fermentation.", "Sự lên men", "Quá trình lên men tự nhiên.", "Vi sinh vật phân hủy", 3),
    ("Decaffeinated", "/diːˈkæf.ɪ.neɪ.tɪd/", "adjective", "Decaffeinated coffee.", "Đã khử ca-phê-in", "Cà phê đã khử ca-phê-in.", "Không có cafein", 3),
    ("Sauté", "/ˈsəʊ.teɪ/", "verb", "Sauté the onions.", "Xào, áp chảo", "Xào hành tây.", "Đảo nhanh trong dầu mỡ", 3),
    ("Simmer", "/ˈsɪm.ər/", "verb", "Simmer gently.", "Ninh, hầm", "Ninh nhỏ lửa.", "Nấu liu riu", 3),
    ("Blanch", "/blɑːntʃ/", "verb", "Blanch the vegetables.", "Chần", "Chần rau qua nước sôi.", "Nhúng nước sôi", 3),
    ("Marinate", "/ˈmær.ɪ.neɪt/", "verb", "Marinate the meat overnight.", "Ướp", "Ướp thịt qua đêm.", "Ngâm trong gia vị", 3),
    ("Garnish", "/ˈɡɑː.nɪʃ/", "verb", "Garnish with parsley.", "Trang trí (món ăn)", "Trang trí bằng rau mùi tây.", "Tô điểm món ăn", 3),
    ("Flambé", "/flɒmˈbeɪ/", "verb", "A flambé dessert.", "Đốt rượu", "Một món tráng miệng đốt rượu.", "Chế rượu và châm lửa", 3),
    ("Poach", "/pəʊtʃ/", "verb", "Poach an egg.", "Chần (trứng), rim", "Chần một quả trứng.", "Nấu chậm trong nước", 3),
    ("Carve", "/kɑːv/", "verb", "Carve the turkey.", "Lạng, thái mỏng", "Thái mỏng con gà tây.", "Cắt thịt thành lát", 3),
    ("Mince", "/mɪns/", "verb", "Mince the garlic.", "Băm nhỏ", "Băm nhỏ tỏi.", "Cắt thật nhỏ", 3),
    ("Grate", "/ɡreɪt/", "verb", "Grate some cheese.", "Bào, xát", "Bào một ít phô mai.", "Chà xát thành sợi/bột", 3),
    ("Knead", "/niːd/", "verb", "Knead the dough for 10 minutes.", "Nhào", "Nhào bột trong 10 phút.", "Dùng tay nhào trộn", 3),
    ("Whisk", "/wɪsk/", "verb", "Whisk the eggs.", "Đánh (trứng)", "Đánh trứng.", "Đánh tan bằng phới", 3),
    ("Sieve", "/sɪv/", "verb", "Sieve the flour.", "Rây, lọc", "Rây bột mì.", "Lọc bột/chất lỏng", 3),
    ("Zest", "/zest/", "noun", "Lemon zest.", "Lớp vỏ ngoài", "Vỏ chanh bào.", "Vỏ ngoài tạo mùi", 3),
    ("Bouillon", "/ˈbuː.jɒn/", "noun", "Beef bouillon.", "Nước xuýt", "Nước hầm xương bò.", "Nước hầm trong", 3),
    ("Caviar", "/ˈkæv.i.ɑːr/", "noun", "Black caviar.", "Trứng cá muối", "Trứng cá đen.", "Trứng cá tẩm muối", 3),
    ("Truffle", "/ˈtrʌf.əl/", "noun", "White truffle.", "Nấm cục", "Nấm cục trắng.", "Loại nấm mọc ngầm", 3),
    ("Saffron", "/ˈsæf.rən/", "noun", "A pinch of saffron.", "Nhụy hoa nghệ tây", "Một nhúm nhụy hoa nghệ tây.", "Gia vị màu vàng đắt đỏ", 3),
    ("Venison", "/ˈven.ɪ.sən/", "noun", "Roast venison.", "Thịt nai", "Thịt nai nướng.", "Thịt nai/hươu", 3)
]

level_map = ["en_food_beginner", "en_food_intermediate", "en_food_advanced", "en_food_expert"]

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

print("Successfully appended 120 words for Food (English) to DB!")
