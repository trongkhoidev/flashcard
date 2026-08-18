import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_fashion_beginner", "languageCode": "en", "title": "Thời trang", "subtitle": "Thời trang - Beginner", "iconEmoji": "👗", "level": "Beginner", "colorHex": "#EC4899", "cardCount": 30},
    {"id": "en_fashion_intermediate", "languageCode": "en", "title": "Thời trang", "subtitle": "Thời trang - Intermediate", "iconEmoji": "👗", "level": "Intermediate", "colorHex": "#D946EF", "cardCount": 30},
    {"id": "en_fashion_advanced", "languageCode": "en", "title": "Thời trang", "subtitle": "Thời trang - Advanced", "iconEmoji": "👗", "level": "Advanced", "colorHex": "#8B5CF6", "cardCount": 30},
    {"id": "en_fashion_expert", "languageCode": "en", "title": "Thời trang", "subtitle": "Thời trang - Expert", "iconEmoji": "👗", "level": "Expert", "colorHex": "#6366F1", "cardCount": 30}
]

vocab_data = [
    # Beginner
    ("Shirt", "/ʃɜːt/", "noun", "I wear a blue shirt.", "Áo sơ mi", "Tôi mặc một chiếc áo sơ mi màu xanh.", "Sơ mi", 0),
    ("Pants", "/pænts/", "noun", "He bought new pants.", "Quần dài", "Anh ấy đã mua chiếc quần dài mới.", "Quần", 0),
    ("Dress", "/dres/", "noun", "She wears a beautiful dress.", "Váy liền", "Cô ấy mặc một chiếc váy liền tuyệt đẹp.", "Váy", 0),
    ("Skirt", "/skɜːt/", "noun", "The skirt is too short.", "Chân váy", "Chiếc chân váy này quá ngắn.", "Váy", 0),
    ("Shoes", "/ʃuːz/", "noun", "These shoes are comfortable.", "Đôi giày", "Đôi giày này rất thoải mái.", "Giày", 0),
    ("Hat", "/hæt/", "noun", "Don't forget your hat.", "Cái mũ", "Đừng quên chiếc mũ của bạn.", "Mũ đội", 0),
    ("Socks", "/sɒks/", "noun", "I need warm socks.", "Tất, vớ", "Tôi cần những chiếc tất ấm.", "Tất chân", 0),
    ("Jacket", "/ˈdʒæk.ɪt/", "noun", "Put on your jacket.", "Áo khoác (ngắn)", "Hãy mặc áo khoác của bạn vào.", "Áo khoác", 0),
    ("Coat", "/kəʊt/", "noun", "It's cold, wear a coat.", "Áo khoác (dài)", "Trời lạnh, hãy mặc áo khoác dài.", "Áo khoác", 0),
    ("T-shirt", "/ˈtiː.ʃɜːt/", "noun", "A cotton T-shirt is cool.", "Áo thun", "Một chiếc áo thun cotton thì mát mẻ.", "Áo chữ T", 0),
    ("Jeans", "/dʒiːnz/", "noun", "Blue jeans are popular.", "Quần bò", "Quần bò xanh rất phổ biến.", "Quần Jean", 0),
    ("Shorts", "/ʃɔːts/", "noun", "I wear shorts in summer.", "Quần đùi", "Tôi mặc quần đùi vào mùa hè.", "Short (ngắn)", 0),
    ("Sweater", "/ˈswet.ər/", "noun", "This sweater is made of wool.", "Áo len", "Chiếc áo len này làm bằng lông cừu.", "Sweat (đổ mồ hôi) -> áo ấm", 0),
    ("Tie", "/taɪ/", "noun", "He wears a red tie.", "Cà vạt", "Anh ấy đeo cà vạt đỏ.", "Buộc", 0),
    ("Belt", "/belt/", "noun", "The belt is made of leather.", "Thắt lưng", "Thắt lưng được làm bằng da.", "Dây nịt", 0),
    ("Scarf", "/skɑːf/", "noun", "Wrap a scarf around your neck.", "Khăn quàng cổ", "Quấn khăn quàng quanh cổ bạn.", "Khăn", 0),
    ("Gloves", "/ɡlʌvz/", "noun", "Wear gloves in the snow.", "Găng tay", "Đeo găng tay trong tuyết.", "Găng tay", 0),
    ("Boots", "/buːts/", "noun", "Hiking boots are strong.", "Giày bốt", "Giày leo núi rất chắc chắn.", "Bốt", 0),
    ("Sneakers", "/ˈsniː.kəz/", "noun", "I run in my sneakers.", "Giày thể thao", "Tôi chạy bằng giày thể thao.", "Sneak -> giày đi nhẹ", 0),
    ("Suit", "/suːt/", "noun", "He wore a black suit.", "Bộ vest", "Anh ấy mặc một bộ vest đen.", "Trang phục trang trọng", 0),
    ("Ring", "/rɪŋ/", "noun", "She wears a gold ring.", "Chiếc nhẫn", "Cô ấy đeo một chiếc nhẫn vàng.", "Nhẫn", 0),
    ("Watch", "/wɒtʃ/", "noun", "My watch is broken.", "Đồng hồ đeo tay", "Đồng hồ của tôi bị hỏng.", "Đồng hồ", 0),
    ("Glasses", "/ˈɡlɑː.sɪz/", "noun", "I need reading glasses.", "Kính mắt", "Tôi cần kính viễn thị.", "Kính", 0),
    ("Pocket", "/ˈpɒk.ɪt/", "noun", "Put it in your pocket.", "Túi quần áo", "Đặt nó vào túi của bạn.", "Túi", 0),
    ("Button", "/ˈbʌt.ən/", "noun", "One button is missing.", "Cái cúc áo", "Một chiếc cúc áo bị thiếu.", "Nút", 0),
    ("Zipper", "/ˈzɪp.ər/", "noun", "The zipper is stuck.", "Khóa kéo", "Khóa kéo bị kẹt.", "Zip (kéo)", 0),
    ("Size", "/saɪz/", "noun", "What size do you wear?", "Kích cỡ", "Bạn mặc cỡ nào?", "Size", 0),
    ("Wear", "/weər/", "verb", "I wear uniforms to work.", "Mặc, đeo", "Tôi mặc đồng phục đi làm.", "Mặc", 0),
    ("Try on", "/traɪ ɒn/", "verb", "Can I try this on?", "Mặc thử", "Tôi có thể mặc thử cái này không?", "Try (thử)", 0),
    ("Fit", "/fɪt/", "verb", "This shirt fits me well.", "Vừa vặn", "Chiếc áo này vừa vặn với tôi.", "Vừa", 0),

    # Intermediate
    ("Wardrobe", "/ˈwɔː.drəʊb/", "noun", "My wardrobe is full of clothes.", "Tủ quần áo", "Tủ quần áo của tôi đầy đồ.", "Tủ đồ", 1),
    ("Accessory", "/əkˈses.ər.i/", "noun", "Sunglasses are a nice accessory.", "Phụ kiện", "Kính râm là một phụ kiện đẹp.", "Trang sức phụ", 1),
    ("Outfit", "/ˈaʊt.fɪt/", "noun", "She bought a new outfit.", "Trang phục (cả bộ)", "Cô ấy mua một bộ trang phục mới.", "Bộ đồ", 1),
    ("Fabric", "/ˈfæb.rɪk/", "noun", "Silk is a soft fabric.", "Vải, chất liệu", "Lụa là loại vải mềm mại.", "Chất liệu", 1),
    ("Cotton", "/ˈkɒt.ən/", "noun", "Cotton shirts are breathable.", "Vải bông, cotton", "Áo sơ mi cotton rất thoáng khí.", "Sợi bông", 1),
    ("Wool", "/wʊl/", "noun", "Wool keeps you warm in winter.", "Len, lông cừu", "Len giữ ấm cho bạn vào mùa đông.", "Lông", 1),
    ("Leather", "/ˈleð.ər/", "noun", "A leather jacket looks cool.", "Da", "Một chiếc áo khoác da trông rất ngầu.", "Da động vật", 1),
    ("Silk", "/sɪlk/", "noun", "She wore a silk scarf.", "Lụa", "Cô ấy quàng chiếc khăn lụa.", "Tơ lụa", 1),
    ("Denim", "/ˈden.ɪm/", "noun", "Denim jeans are durable.", "Vải bò", "Quần bò rất bền.", "Vải jean", 1),
    ("Pattern", "/ˈpæt.ən/", "noun", "I like the floral pattern.", "Họa tiết", "Tôi thích họa tiết hoa.", "Mẫu vẽ", 1),
    ("Stripe", "/straɪp/", "noun", "A shirt with blue stripes.", "Sọc, kẻ sọc", "Một chiếc áo sơ mi có sọc xanh.", "Kẻ sọc", 1),
    ("Checkered", "/ˈtʃek.əd/", "adjective", "He wore a checkered shirt.", "Kẻ ca-rô", "Anh ấy mặc áo sơ mi kẻ ca-rô.", "Ca-rô", 1),
    ("Polka dot", "/ˈpɒl.kə ˌdɒt/", "noun", "She has a polka dot dress.", "Chấm bi", "Cô ấy có một chiếc váy chấm bi.", "Chấm tròn", 1),
    ("Sleeve", "/sliːv/", "noun", "Roll up your sleeves.", "Ống tay áo", "Hãy xắn tay áo lên.", "Tay áo", 1),
    ("Collar", "/ˈkɒl.ər/", "noun", "The collar is dirty.", "Cổ áo", "Cổ áo bị bẩn.", "Cổ áo", 1),
    ("Hem", "/hem/", "noun", "The hem of the skirt is torn.", "Gấu áo/váy", "Gấu váy bị rách.", "Viền gấu", 1),
    ("Thread", "/θred/", "noun", "I need a needle and thread.", "Sợi chỉ", "Tôi cần kim và chỉ.", "Chỉ", 1),
    ("Needle", "/ˈniː.dəl/", "noun", "The tailor uses a needle.", "Cái kim", "Thợ may sử dụng kim.", "Kim khâu", 1),
    ("Tailor", "/ˈteɪ.lər/", "noun", "The tailor made a suit for me.", "Thợ may", "Thợ may đã làm một bộ vest cho tôi.", "Thợ may đồ nam", 1),
    ("Boutique", "/buːˈtiːk/", "noun", "She shops at a small boutique.", "Cửa hàng quần áo nhỏ", "Cô ấy mua sắm tại một cửa hàng thời trang nhỏ.", "Shop nhỏ", 1),
    ("Trend", "/trend/", "noun", "Baggy jeans are the new trend.", "Xu hướng", "Quần bò thùng thình là xu hướng mới.", "Trào lưu", 1),
    ("Fashionable", "/ˈfæʃ.ən.ə.bəl/", "adjective", "She is always fashionable.", "Hợp thời trang", "Cô ấy luôn ăn mặc hợp thời trang.", "Đúng mốt", 1),
    ("Casual", "/ˈkæʒ.u.əl/", "adjective", "I prefer casual clothes.", "Thường phục, thoải mái", "Tôi thích trang phục thường ngày.", "Thoải mái", 1),
    ("Formal", "/ˈfɔː.məl/", "adjective", "Wear formal attire to the wedding.", "Trang trọng", "Mặc trang phục trang trọng đến đám cưới.", "Lịch sự", 1),
    ("Match", "/mætʃ/", "verb", "Your tie matches your shirt.", "Hợp, phối (đồ)", "Cà vạt hợp với áo sơ mi của bạn.", "Hợp màu", 1),
    ("Loose", "/luːs/", "adjective", "These pants are too loose.", "Rộng, lùng thùng", "Chiếc quần này quá rộng.", "Lỏng lẻo", 1),
    ("Tight", "/taɪt/", "adjective", "The shoes are too tight.", "Chật", "Đôi giày quá chật.", "Bó sát", 1),
    ("Dye", "/daɪ/", "verb", "I want to dye my shirt black.", "Nhuộm", "Tôi muốn nhuộm chiếc áo sơ mi thành màu đen.", "Nhuộm màu", 1),
    ("Wrinkled", "/ˈrɪŋ.kəld/", "adjective", "Your shirt is wrinkled.", "Nhăn nheo", "Áo của bạn bị nhăn rồi.", "Nhăn", 1),
    ("Iron", "/aɪən/", "verb", "I need to iron my clothes.", "Là, ủi", "Tôi cần ủi quần áo.", "Bàn là", 1),

    # Advanced
    ("Haute couture", "/ˌəʊt kuːˈtjʊər/", "noun", "Paris is famous for haute couture.", "Thời trang cao cấp (đặt may riêng)", "Paris nổi tiếng với thời trang cao cấp.", "Thời trang cao cấp", 2),
    ("Avant-garde", "/ˌæv.ɒ̃ˈɡɑːd/", "adjective", "His designs are avant-garde.", "Phá cách, tiên phong", "Thiết kế của anh ấy mang tính tiên phong.", "Đi trước thời đại", 2),
    ("Apparel", "/əˈpær.əl/", "noun", "The store sells men's apparel.", "Quần áo, y phục (trang trọng)", "Cửa hàng bán y phục nam giới.", "Trang phục", 2),
    ("Garment", "/ˈɡɑː.mənt/", "noun", "Silk is a delicate garment.", "Món đồ mặc (quần/áo)", "Lụa là một loại trang phục mỏng manh.", "Đồ mặc", 2),
    ("Textile", "/ˈtek.staɪl/", "noun", "The textile industry is growing.", "Hàng dệt may", "Ngành công nghiệp dệt may đang phát triển.", "Dệt", 2),
    ("Silhouette", "/ˌsɪl.uˈet/", "noun", "The dress has a beautiful silhouette.", "Hình bóng, phom dáng", "Chiếc váy có phom dáng rất đẹp.", "Đường nét", 2),
    ("Seam", "/siːm/", "noun", "The seam of my pants ripped.", "Đường may", "Đường may quần của tôi bị rách.", "Đường nối", 2),
    ("Embroidery", "/ɪmˈbrɔɪ.dər.i/", "noun", "The dress features beautiful embroidery.", "Nghệ thuật thêu", "Chiếc váy có họa tiết thêu tuyệt đẹp.", "Thêu thùa", 2),
    ("Velvet", "/ˈvel.vɪt/", "noun", "She wore a velvet gown.", "Vải nhung", "Cô ấy mặc một chiếc váy nhung.", "Nhung", 2),
    ("Linen", "/ˈlɪn.ɪn/", "noun", "Linen shirts are perfect for summer.", "Vải lanh", "Áo sơ mi vải lanh rất hợp cho mùa hè.", "Lanh", 2),
    ("Cashmere", "/ˈkæʃ.mɪər/", "noun", "Cashmere sweaters are very expensive.", "Len cashmere", "Áo len cashmere rất đắt.", "Len mịn", 2),
    ("Suede", "/sweɪd/", "noun", "Don't get your suede shoes wet.", "Da lộn", "Đừng làm ướt đôi giày da lộn của bạn.", "Da lộn", 2),
    ("Chiffon", "/ˈʃɪf.ɒn/", "noun", "The scarf is made of chiffon.", "Vải voan", "Chiếc khăn được làm từ vải voan.", "Voan", 2),
    ("Corduroy", "/ˈkɔː.də.rɔɪ/", "noun", "He wore corduroy trousers.", "Vải nhung kẻ", "Anh ấy mặc quần nhung kẻ.", "Nhung gân", 2),
    ("Vintage", "/ˈvɪn.tɪdʒ/", "adjective", "She loves shopping for vintage clothes.", "Cổ điển, đồ cũ", "Cô ấy thích mua quần áo mang hơi hướng cổ điển.", "Cổ, cũ", 2),
    ("Bespoke", "/bɪˈspəʊk/", "adjective", "He ordered a bespoke suit.", "Đo ni đóng giày, may đo riêng", "Anh ấy đặt một bộ đồ may đo riêng.", "May đo", 2),
    ("Runway", "/ˈrʌn.weɪ/", "noun", "Models walk down the runway.", "Sàn diễn thời trang", "Người mẫu đi trên sàn diễn.", "Sàn catwalk", 2),
    ("Catwalk", "/ˈkæt.wɔːk/", "noun", "The catwalk was illuminated.", "Sàn diễn (hẹp)", "Sàn diễn thời trang rực sáng.", "Sàn diễn", 2),
    ("Vogue", "/vəʊɡ/", "noun", "Short skirts are back in vogue.", "Mốt, thịnh hành", "Chân váy ngắn đang thịnh hành trở lại.", "Mốt", 2),
    ("Chic", "/ʃiːk/", "adjective", "She looks very chic in that hat.", "Sang trọng, kiểu cách", "Cô ấy trông rất kiểu cách với chiếc mũ đó.", "Sành điệu", 2),
    ("Tacky", "/ˈtæk.i/", "adjective", "That plastic jewelry looks tacky.", "Lòe loẹt, rẻ tiền", "Món trang sức nhựa đó trông rẻ tiền.", "Xấu, quê", 2),
    ("Flattering", "/ˈflæt.ər.ɪŋ/", "adjective", "That color is very flattering on you.", "Tôn dáng, tôn da", "Màu đó rất tôn da của bạn.", "Tôn nét đẹp", 2),
    ("Frumpy", "/ˈfrʌm.pi/", "adjective", "She wore a frumpy dress.", "Lôi thôi, lỗi thời", "Cô ấy mặc một chiếc váy lôi thôi.", "Kém hấp dẫn", 2),
    ("Tear", "/teər/", "noun", "There is a tear in your sleeve.", "Vết rách", "Có một vết rách trên tay áo của bạn.", "Rách", 2),
    ("Mend", "/mend/", "verb", "I need to mend this hole.", "Vá, sửa", "Tôi cần vá cái lỗ này.", "Sửa chữa", 2),
    ("Alter", "/ˈɒl.tər/", "verb", "The tailor will alter the dress.", "Chỉnh sửa (quần áo)", "Thợ may sẽ chỉnh sửa chiếc váy.", "Sửa đồ", 2),
    ("Shrink", "/ʃrɪŋk/", "verb", "Hot water will shrink the sweater.", "Co lại", "Nước nóng sẽ làm áo len bị co lại.", "Bị rút lại", 2),
    ("Fade", "/feɪd/", "verb", "The color will fade in the sun.", "Phai màu", "Màu sắc sẽ phai nhạt dưới ánh mặt trời.", "Phai mờ", 2),
    ("Wardrobe malfunction", "/ˈwɔː.drəʊb mælˈfʌŋk.ʃən/", "noun", "She had a wardrobe malfunction on stage.", "Sự cố trang phục", "Cô ấy gặp sự cố trang phục trên sân khấu.", "Rách đồ bất ngờ", 2),
    ("Dress code", "/ˈdres ˌkəʊd/", "noun", "The party has a strict dress code.", "Quy tắc ăn mặc", "Bữa tiệc có quy định trang phục nghiêm ngặt.", "Luật ăn mặc", 2),

    # Expert
    ("Sartorial", "/sɑːˈtɔː.ri.əl/", "adjective", "He is known for his sartorial elegance.", "Thuộc về may mặc/thời trang nam", "Anh ấy nổi tiếng với sự thanh lịch trong cách ăn mặc.", "Cách ăn mặc", 3),
    ("Androgynous", "/ænˈdrɒdʒ.ɪ.nəs/", "adjective", "The new fashion line is highly androgynous.", "Lưỡng tính (trang phục nam nữ đều mặc được)", "Bộ sưu tập mới mang đậm phong cách lưỡng tính.", "Phi giới tính", 3),
    ("Fast fashion", "/ˌfɑːst ˈfæʃ.ən/", "noun", "Fast fashion is harmful to the environment.", "Thời trang ăn liền", "Thời trang ăn liền gây hại cho môi trường.", "Thời trang nhanh", 3),
    ("Upcycle", "/ˈʌpˌsaɪ.kəl/", "verb", "She upcycles old clothes into new designs.", "Tái chế nâng cấp", "Cô ấy tái chế quần áo cũ thành các thiết kế mới.", "Nâng cấp đồ cũ", 3),
    ("Minimalism", "/ˈmɪn.ɪ.mə.lɪ.zəm/", "noun", "Minimalism focuses on simple, essential clothing.", "Chủ nghĩa tối giản", "Chủ nghĩa tối giản tập trung vào trang phục đơn giản, thiết yếu.", "Tối giản", 3),
    ("Utilitarian", "/juːˌtɪl.ɪˈteə.ri.ən/", "adjective", "Utilitarian fashion is practical and functional.", "Thời trang thực dụng", "Thời trang thực dụng mang tính thiết thực và đa năng.", "Thực tế", 3),
    ("Drape", "/dreɪp/", "verb", "The fabric drapes beautifully over the shoulders.", "Rủ xuống (vải)", "Lớp vải rủ xuống thật đẹp qua bờ vai.", "Xếp nếp", 3),
    ("Pleat", "/pliːt/", "noun", "The skirt has sharp pleats.", "Nếp gấp", "Chiếc váy có những nếp gấp sắc nét.", "Xếp ly", 3),
    ("Ruche", "/ruːʃ/", "noun", "The dress features a ruche on the side.", "Đường nhún bèo", "Chiếc váy có đường nhún bèo ở một bên.", "Nhún bèo", 3),
    ("Gusset", "/ˈɡʌs.ɪt/", "noun", "A gusset was added to make the bag wider.", "Miếng vải lót (tăng độ rộng/chắc)", "Một miếng vải lót được thêm vào để làm cho chiếc túi rộng hơn.", "Phần nêm", 3),
    ("Placket", "/ˈplæk.ɪt/", "noun", "The shirt has a concealed button placket.", "Nẹp áo (che cúc)", "Áo sơ mi có một dải nẹp che cúc.", "Nẹp cài cúc", 3),
    ("Dart", "/dɑːt/", "noun", "The tailor added a dart to fit the waist.", "Đường chít eo", "Thợ may đã thêm một đường chít eo để vừa vặn hơn.", "Chít ly", 3),
    ("Inseam", "/ˈɪn.siːm/", "noun", "Measure the inseam of the trousers.", "Đường may đũng quần", "Đo đường may đũng của quần.", "Đường nối trong", 3),
    ("Appliqué", "/æpˈliː.keɪ/", "noun", "The jacket is decorated with floral appliqué.", "Nghệ thuật đáp vải", "Chiếc áo khoác được trang trí bằng nghệ thuật đáp vải họa tiết hoa.", "Đáp vải trang trí", 3),
    ("Motif", "/məʊˈtiːf/", "noun", "The collection uses a butterfly motif.", "Họa tiết chủ đạo", "Bộ sưu tập sử dụng họa tiết chủ đạo là con bướm.", "Họa tiết lặp lại", 3),
    ("Monochrome", "/ˈmɒn.ə.krəʊm/", "adjective", "She wore a chic monochrome outfit.", "Đơn sắc", "Cô ấy mặc một bộ trang phục đơn sắc sang trọng.", "Một màu", 3),
    ("Eclectic", "/ɪˈklek.tɪk/", "adjective", "His style is highly eclectic.", "Pha trộn nhiều phong cách", "Phong cách của anh ấy vô cùng pha trộn.", "Chiết trung", 3),
    ("Preppy", "/ˈprep.i/", "adjective", "He likes preppy clothes like polo shirts.", "Phong cách học sinh tư thục (gọn gàng)", "Anh ấy thích phong cách gọn gàng như áo polo.", "Chuẩn mực", 3),
    ("Bohemian", "/bəʊˈhiː.mi.ən/", "adjective", "She loves bohemian maxi dresses.", "Phong cách du mục", "Cô ấy yêu thích váy maxi phong cách du mục.", "Tự do, du mục", 3),
    ("Edgy", "/ˈedʒ.i/", "adjective", "Her leather jacket gives her an edgy look.", "Phá cách, nổi loạn", "Chiếc áo khoác da mang lại cho cô ấy vẻ ngoài phá cách.", "Cá tính mạnh", 3),
    ("Ethereal", "/ɪˈθɪə.ri.əl/", "adjective", "The sheer fabric gives an ethereal appearance.", "Thanh tao, thoát tục", "Loại vải mỏng manh mang lại vẻ ngoài thanh tao.", "Nhẹ nhàng", 3),
    ("Oversized", "/ˈəʊ.və.saɪzd/", "adjective", "Oversized blazers are trendy right now.", "Phom rộng", "Áo blazer phom rộng đang là xu hướng hiện nay.", "Rộng rãi", 3),
    ("Asymmetrical", "/ˌeɪ.sɪˈmet.rɪ.kəl/", "adjective", "The dress has an asymmetrical hemline.", "Bất đối xứng", "Chiếc váy có đường viền lai bất đối xứng.", "Không cân xứng", 3),
    ("Tailoring", "/ˈteɪ.lər.ɪŋ/", "noun", "The suit features impeccable tailoring.", "Nghệ thuật cắt may", "Bộ đồ có kỹ thuật cắt may hoàn hảo.", "Kỹ thuật may", 3),
    ("Lookbook", "/ˈlʊk.bʊk/", "noun", "The brand released its fall lookbook.", "Bộ ảnh thời trang mẫu", "Thương hiệu đã ra mắt bộ ảnh mẫu mùa thu.", "Sách mẫu", 3),
    ("Capsule wardrobe", "/ˈkæp.sjuːl ˈwɔː.drəʊb/", "noun", "She built a capsule wardrobe for summer.", "Tủ đồ tối giản (ít món dễ phối)", "Cô ấy đã tạo ra một tủ đồ tối giản cho mùa hè.", "Tủ đồ cơ bản", 3),
    ("Merchandising", "/ˈmɜː.tʃən.daɪ.zɪŋ/", "noun", "Visual merchandising attracts customers.", "Sự trưng bày/kinh doanh thời trang", "Việc trưng bày trực quan thu hút khách hàng.", "Trưng bày hàng", 3),
    ("Counterfeit", "/ˈkaʊn.tə.fɪt/", "noun", "Selling counterfeit bags is illegal.", "Hàng nhái, hàng giả", "Bán túi xách nhái là bất hợp pháp.", "Hàng giả", 3),
    ("Thrifting", "/θrɪft ɪŋ/", "noun", "Thrifting is an eco-friendly way to shop.", "Mua sắm đồ cũ", "Mua đồ cũ là một cách mua sắm thân thiện với môi trường.", "Mua hàng thùng", 3),
    ("Sustainable", "/səˈsteɪ.nə.bəl/", "adjective", "Sustainable fashion uses organic materials.", "Bền vững", "Thời trang bền vững sử dụng các vật liệu hữu cơ.", "Bảo vệ môi trường", 3)
]

level_map = ["en_fashion_beginner", "en_fashion_intermediate", "en_fashion_advanced", "en_fashion_expert"]

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

# Read existing data
if os.path.exists(DB_FILE):
    with open(DB_FILE, "r", encoding="utf-8") as f:
        data = json.load(f)
else:
    data = {"decks": [], "flashCards": []}

data["decks"].extend(decks)
data["flashCards"].extend(new_flashcards)

with open(DB_FILE, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print("Successfully appended 120 words for Fashion (English) to DB!")
