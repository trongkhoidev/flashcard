import json
import os

DB_FILE = "app/src/main/assets/vocab_data.json"

decks = [
    {"id": "en_shopping_beginner", "languageCode": "en", "title": "Mua sắm", "subtitle": "Mua sắm - Beginner", "iconEmoji": "🛍️", "level": "Beginner", "colorHex": "#F59E0B", "cardCount": 30},
    {"id": "en_shopping_intermediate", "languageCode": "en", "title": "Mua sắm", "subtitle": "Mua sắm - Intermediate", "iconEmoji": "🛍️", "level": "Intermediate", "colorHex": "#D97706", "cardCount": 30},
    {"id": "en_shopping_advanced", "languageCode": "en", "title": "Mua sắm", "subtitle": "Mua sắm - Advanced", "iconEmoji": "🛍️", "level": "Advanced", "colorHex": "#B45309", "cardCount": 30},
    {"id": "en_shopping_expert", "languageCode": "en", "title": "Mua sắm", "subtitle": "Mua sắm - Expert", "iconEmoji": "🛍️", "level": "Expert", "colorHex": "#78350F", "cardCount": 30}
]

vocab_data = [
    # Beginner (0)
    ("Shop", "/ʃɒp/", "verb", "I shop at the mall.", "Mua sắm", "Tôi mua sắm ở trung tâm thương mại.", "Mua đồ", 0),
    ("Store", "/stɔːr/", "noun", "The store is open.", "Cửa hàng", "Cửa hàng đang mở.", "Nơi bán hàng", 0),
    ("Buy", "/baɪ/", "verb", "I buy a new book.", "Mua", "Tôi mua một quyển sách mới.", "Trả tiền để lấy đồ", 0),
    ("Sell", "/sel/", "verb", "They sell fruits here.", "Bán", "Họ bán trái cây ở đây.", "Đưa đồ đổi lấy tiền", 0),
    ("Pay", "/peɪ/", "verb", "I pay with cash.", "Trả tiền", "Tôi trả bằng tiền mặt.", "Thanh toán", 0),
    ("Money", "/ˈmʌn.i/", "noun", "I have some money.", "Tiền", "Tôi có một ít tiền.", "Đồng tiền", 0),
    ("Cash", "/kæʃ/", "noun", "Do you have cash?", "Tiền mặt", "Bạn có tiền mặt không?", "Tiền giấy/xu", 0),
    ("Card", "/kɑːd/", "noun", "I pay by credit card.", "Thẻ", "Tôi trả bằng thẻ tín dụng.", "Thẻ ngân hàng", 0),
    ("Price", "/praɪs/", "noun", "The price is low.", "Giá cả", "Giá cả thì thấp.", "Mức giá", 0),
    ("Cheap", "/tʃiːp/", "adjective", "This shirt is cheap.", "Rẻ", "Cái áo này rẻ.", "Ít tiền", 0),
    ("Expensive", "/ɪkˈspen.sɪv/", "adjective", "The phone is expensive.", "Đắt", "Cái điện thoại thì đắt.", "Nhiều tiền", 0),
    ("Bag", "/bæɡ/", "noun", "Put it in the bag.", "Túi", "Đặt nó vào túi.", "Giỏ xách", 0),
    ("Customer", "/ˈkʌs.tə.mər/", "noun", "The customer is always right.", "Khách hàng", "Khách hàng luôn đúng.", "Người mua", 0),
    ("Sale", "/seɪl/", "noun", "The items are on sale.", "Giảm giá, bán hàng", "Các món đồ đang được giảm giá.", "Giảm giá", 0),
    ("Mall", "/mɔːl/", "noun", "Let's go to the mall.", "Trung tâm thương mại", "Hãy đến trung tâm thương mại.", "Khu mua sắm lớn", 0),
    ("Market", "/ˈmɑː.kɪt/", "noun", "We buy food at the market.", "Chợ", "Chúng tôi mua đồ ăn ở chợ.", "Khu chợ", 0),
    ("Supermarket", "/ˈsuː.pəˌmɑː.kɪt/", "noun", "The supermarket is big.", "Siêu thị", "Siêu thị rất lớn.", "Chợ siêu lớn", 0),
    ("Cart", "/kɑːt/", "noun", "Push the shopping cart.", "Xe đẩy", "Đẩy chiếc xe mua sắm.", "Xe chở hàng", 0),
    ("Basket", "/ˈbɑː.skɪt/", "noun", "Take a basket.", "Giỏ hàng", "Lấy một cái giỏ.", "Cái giỏ", 0),
    ("Size", "/saɪz/", "noun", "What is your size?", "Kích cỡ", "Kích cỡ của bạn là gì?", "Độ lớn", 0),
    ("Try", "/traɪ/", "verb", "Try it on.", "Thử", "Hãy mặc thử xem.", "Thử đồ", 0),
    ("Fit", "/fɪt/", "verb", "Does it fit you?", "Vừa vặn", "Nó có vừa với bạn không?", "Vừa vặn", 0),
    ("Return", "/rɪˈtɜːn/", "verb", "I need to return this.", "Trả lại", "Tôi cần trả lại cái này.", "Đưa lại", 0),
    ("Open", "/ˈəʊ.pən/", "adjective", "The shop is open.", "Mở cửa", "Cửa hàng đang mở.", "Hoạt động", 0),
    ("Close", "/kləʊz/", "verb", "They close at 9 PM.", "Đóng cửa", "Họ đóng cửa lúc 9 tối.", "Nghỉ bán", 0),
    ("Clothes", "/kləʊðz/", "noun", "I want new clothes.", "Quần áo", "Tôi muốn quần áo mới.", "Trang phục", 0),
    ("Shoes", "/ʃuːz/", "noun", "These shoes are nice.", "Giày", "Những chiếc giày này rất đẹp.", "Đôi giày", 0),
    ("Food", "/fuːd/", "noun", "Buy some food.", "Đồ ăn", "Mua chút đồ ăn đi.", "Thức ăn", 0),
    ("Drink", "/drɪŋk/", "noun", "Do you want a drink?", "Đồ uống", "Bạn có muốn đồ uống không?", "Thức uống", 0),
    ("Bill", "/bɪl/", "noun", "Ask for the bill.", "Hóa đơn", "Yêu cầu thanh toán hóa đơn.", "Phiếu tính tiền", 0),

    # Intermediate (1)
    ("Discount", "/ˈdɪs.kaʊnt/", "noun", "We got a 10% discount.", "Sự giảm giá", "Chúng tôi được giảm giá 10%.", "Giảm tiền", 1),
    ("Receipt", "/rɪˈsiːt/", "noun", "Keep your receipt.", "Biên lai", "Hãy giữ lại biên lai của bạn.", "Hóa đơn nhỏ", 1),
    ("Refund", "/ˈriː.fʌnd/", "noun", "I asked for a refund.", "Hoàn tiền", "Tôi đã yêu cầu hoàn tiền.", "Trả lại tiền", 1),
    ("Checkout", "/ˈtʃek.aʊt/", "noun", "Go to the checkout.", "Quầy thanh toán", "Hãy đến quầy thanh toán.", "Nơi tính tiền", 1),
    ("Cashier", "/kæʃˈɪər/", "noun", "The cashier was friendly.", "Thu ngân", "Thu ngân rất thân thiện.", "Người thu tiền", 1),
    ("Queue", "/kjuː/", "noun", "There is a long queue.", "Xếp hàng", "Có một hàng đợi dài.", "Hàng người", 1),
    ("Bargain", "/ˈbɑː.ɡɪn/", "noun", "That shirt is a bargain.", "Món hời, mặc cả", "Chiếc áo đó là một món hời.", "Đồ rẻ", 1),
    ("Counter", "/ˈkaʊn.tər/", "noun", "Leave it on the counter.", "Quầy hàng", "Hãy để nó trên quầy.", "Bàn quầy", 1),
    ("Boutique", "/buːˈtiːk/", "noun", "She shops at a boutique.", "Cửa hàng thời trang nhỏ", "Cô ấy mua sắm ở một cửa hàng thời trang.", "Cửa tiệm", 1),
    ("Grocery", "/ˈɡrəʊ.sər.i/", "noun", "I need to buy grocery.", "Cửa hàng tạp hóa", "Tôi cần mua hàng tạp hóa.", "Tạp hóa", 1),
    ("Coupon", "/ˈkuː.pɒn/", "noun", "Use this discount coupon.", "Phiếu giảm giá", "Sử dụng phiếu giảm giá này.", "Phiếu ưu đãi", 1),
    ("Voucher", "/ˈvaʊ.tʃər/", "noun", "A gift voucher for you.", "Phiếu quà tặng", "Một phiếu quà tặng cho bạn.", "Phiếu mua hàng", 1),
    ("Promotion", "/prəˈməʊ.ʃən/", "noun", "A special promotion today.", "Chương trình khuyến mãi", "Khuyến mãi đặc biệt hôm nay.", "Khuyến mãi", 1),
    ("Warranty", "/ˈwɒr.ən.ti/", "noun", "A one-year warranty.", "Giấy bảo hành", "Bảo hành một năm.", "Bảo hành", 1),
    ("Guarantee", "/ˌɡær.ənˈtiː/", "noun", "Money-back guarantee.", "Bảo đảm", "Bảo đảm hoàn tiền.", "Cam kết", 1),
    ("Exchange", "/ɪksˈtʃeɪndʒ/", "verb", "I want to exchange this.", "Trao đổi", "Tôi muốn đổi cái này.", "Đổi hàng", 1),
    ("Delivery", "/dɪˈlɪv.ər.i/", "noun", "Free delivery service.", "Sự giao hàng", "Dịch vụ giao hàng miễn phí.", "Giao đồ", 1),
    ("Tax", "/tæks/", "noun", "The price includes tax.", "Thuế", "Giá này đã bao gồm thuế.", "Tiền thuế", 1),
    ("Budget", "/ˈbʌdʒ.ɪt/", "noun", "I am on a tight budget.", "Ngân sách", "Tôi đang có ngân sách eo hẹp.", "Khoản tiền", 1),
    ("Afford", "/əˈfɔːd/", "verb", "I can't afford it.", "Đủ khả năng", "Tôi không đủ khả năng mua nó.", "Đủ tiền", 1),
    ("Brand", "/brænd/", "noun", "My favorite brand is Nike.", "Thương hiệu", "Thương hiệu yêu thích của tôi là Nike.", "Nhãn hiệu", 1),
    ("Tag", "/tæɡ/", "noun", "Look at the price tag.", "Mác, thẻ giá", "Nhìn vào thẻ giá kìa.", "Thẻ giá", 1),
    ("Stock", "/stɒk/", "noun", "We are out of stock.", "Hàng tồn kho", "Chúng tôi đã hết hàng.", "Kho hàng", 1),
    ("Aisle", "/aɪl/", "noun", "It is in aisle three.", "Lối đi", "Nó ở lối đi số ba.", "Lối đi", 1),
    ("Shelf", "/ʃelf/", "noun", "Top shelf of the rack.", "Kệ hàng", "Kệ trên cùng của giá.", "Ngăn kệ", 1),
    ("Credit", "/ˈkred.ɪt/", "noun", "Pay with credit.", "Tín dụng", "Trả bằng tín dụng.", "Tiền vay", 1),
    ("Debit", "/ˈdeb.ɪt/", "noun", "Use a debit card.", "Ghi nợ", "Sử dụng thẻ ghi nợ.", "Trừ thẳng", 1),
    ("Installment", "/ɪnˈstɔːl.mənt/", "noun", "Pay in installments.", "Trả góp", "Thanh toán bằng cách trả góp.", "Trả dần", 1),
    ("Wallet", "/ˈwɒl.ɪt/", "noun", "He lost his wallet.", "Cái ví", "Anh ấy làm mất ví.", "Ví nam", 1),
    ("Purse", "/pɜːs/", "noun", "She bought a new purse.", "Ví nữ", "Cô ấy mua một cái ví mới.", "Ví nữ", 1),

    # Advanced (2)
    ("Retail", "/ˈriː.teɪl/", "noun", "Retail price is higher.", "Bán lẻ", "Giá bán lẻ thì cao hơn.", "Bán từng cái", 2),
    ("Wholesale", "/ˈhəʊl.seɪl/", "noun", "Buy at wholesale price.", "Bán buôn", "Mua ở mức giá bán buôn.", "Bán sỉ", 2),
    ("Merchandise", "/ˈmɜː.tʃən.daɪz/", "noun", "Display the merchandise.", "Hàng hóa", "Trưng bày hàng hóa.", "Đồ bán", 2),
    ("Inventory", "/ˈɪn.vən.tər.i/", "noun", "Check the inventory.", "Hàng tồn kho", "Kiểm tra hàng tồn kho.", "Hàng hóa lưu trữ", 2),
    ("Counterfeit", "/ˈkaʊn.tə.fɪt/", "adjective", "Beware of counterfeit goods.", "Hàng giả", "Hãy cẩn thận với hàng giả.", "Làm giả", 2),
    ("Consumer", "/kənˈsjuː.mər/", "noun", "Consumer behavior changes.", "Người tiêu dùng", "Hành vi người tiêu dùng thay đổi.", "Người mua đồ", 2),
    ("Purchase", "/ˈpɜː.tʃəs/", "verb", "Confirm your purchase.", "Mua", "Xác nhận giao dịch mua của bạn.", "Sự mua", 2),
    ("Transaction", "/trænˈzæk.ʃən/", "noun", "Transaction completed.", "Giao dịch", "Giao dịch đã hoàn tất.", "Mua bán", 2),
    ("Apparel", "/əˈpær.əl/", "noun", "Men's apparel store.", "Quần áo", "Cửa hàng quần áo nam.", "Y phục", 2),
    ("Cosmetics", "/kɒzˈmet.ɪks/", "noun", "Buy cosmetics online.", "Mỹ phẩm", "Mua mỹ phẩm trực tuyến.", "Đồ trang điểm", 2),
    ("Clearance", "/ˈklɪə.rəns/", "noun", "Clearance sale starts.", "Thanh lý", "Đợt bán thanh lý bắt đầu.", "Xả hàng", 2),
    ("Overcharge", "/ˌəʊ.vəˈtʃɑːdʒ/", "verb", "They overcharged me.", "Tính giá quá cao", "Họ đã tính giá cho tôi quá cao.", "Chặt chém", 2),
    ("Rip-off", "/ˈrɪp.ɒf/", "noun", "That price is a rip-off.", "Sự chém giá", "Mức giá đó đúng là chém giá.", "Bán giá cắt cổ", 2),
    ("Shoplift", "/ˈʃɒp.lɪft/", "verb", "He tried to shoplift.", "Ăn cắp", "Anh ta đã cố ăn cắp trong cửa hàng.", "Trộm đồ tiệm", 2),
    ("Garment", "/ˈɡɑː.mənt/", "noun", "A beautiful silk garment.", "Quần áo (nói chung)", "Một bộ quần áo lụa đẹp.", "Trang phục", 2),
    ("Outlet", "/ˈaʊt.let/", "noun", "Factory outlet stores.", "Cửa hàng tiêu thụ", "Các cửa hàng tiêu thụ của nhà máy.", "Điểm bán hàng", 2),
    ("Vendor", "/ˈven.dər/", "noun", "A street vendor.", "Người bán hàng rong", "Một người bán hàng trên phố.", "Người bán lẻ", 2),
    ("Authentic", "/ɔːˈθen.tɪk/", "adjective", "Authentic Italian leather.", "Chính hãng", "Da thật từ Ý.", "Thật, chính gốc", 2),
    ("Second-hand", "/ˌsek.əndˈhænd/", "adjective", "A second-hand car.", "Hàng cũ", "Một chiếc xe hơi cũ.", "Đã qua sử dụng", 2),
    ("Defective", "/dɪˈfek.tɪv/", "adjective", "This item is defective.", "Có lỗi", "Món đồ này bị lỗi.", "Bị khuyết điểm", 2),
    ("Barter", "/ˈbɑː.tər/", "verb", "Barter goods for food.", "Trao đổi", "Trao đổi hàng hóa lấy thức ăn.", "Đổi chác", 2),
    ("Haggle", "/ˈhæɡ.əl/", "verb", "Haggle over the price.", "Mặc cả", "Mặc cả về giá tiền.", "Trả giá", 2),
    ("Splurge", "/splɜːdʒ/", "verb", "Splurge on a dress.", "Vung tiền", "Vung tiền vào một chiếc váy.", "Xài hoang", 2),
    ("Thrifty", "/ˈθrɪf.ti/", "adjective", "She is a thrifty shopper.", "Tiết kiệm", "Cô ấy là một người mua sắm tiết kiệm.", "Tính toán kỹ", 2),
    ("Frugal", "/ˈfruː.ɡəl/", "adjective", "Live a frugal life.", "Tằn tiện", "Sống một cuộc sống tằn tiện.", "Rất tiết kiệm", 2),
    ("Exorbitant", "/ɪɡˈzɔː.bɪ.tənt/", "adjective", "Exorbitant prices.", "Giá cắt cổ", "Mức giá quá đáng.", "Quá đắt", 2),
    ("Invoice", "/ˈɪn.vɔɪs/", "noun", "Please pay the invoice.", "Hóa đơn", "Vui lòng thanh toán hóa đơn.", "Hóa đơn thương mại", 2),
    ("Catalogue", "/ˈkæt.əl.ɒɡ/", "noun", "Browse the catalogue.", "Danh mục", "Duyệt qua danh mục.", "Sách mẫu", 2),
    ("Window-shopping", "/ˈwɪn.dəʊ ˌʃɒp.ɪŋ/", "noun", "Just window-shopping.", "Đi xem hàng", "Chỉ đi xem chứ không mua.", "Xem cửa hàng", 2),
    ("Lavish", "/ˈlæv.ɪʃ/", "adjective", "Lavish gifts.", "Xa hoa", "Những món quà xa hoa.", "Hoang phí", 2),

    # Expert (3)
    ("Consumerism", "/kənˈsjuː.mə.rɪ.zəm/", "noun", "Modern consumerism.", "Chủ nghĩa tiêu dùng", "Chủ nghĩa tiêu dùng hiện đại.", "Xã hội chuộng mua", 3),
    ("Commodification", "/kəˌmɒd.ɪ.fɪˈkeɪ.ʃən/", "noun", "Commodification of art.", "Thương mại hóa", "Thương mại hóa nghệ thuật.", "Biến thành hàng hóa", 3),
    ("Patronize", "/ˈpæt.rə.naɪz/", "verb", "Patronize local stores.", "Là khách quen", "Thường xuyên ghé cửa hàng địa phương.", "Ủng hộ tiệm", 3),
    ("Emporium", "/ɪmˈpɔː.ri.əm/", "noun", "A grand emporium.", "Trung tâm thương mại lớn", "Một trung tâm thương mại lộng lẫy.", "Siêu thị lớn", 3),
    ("Procure", "/prəˈkjʊər/", "verb", "Procure materials.", "Thu được", "Kiếm được vật liệu.", "Kiếm được", 3),
    ("Purveyor", "/pəˈveɪ.ər/", "noun", "Purveyor of fine foods.", "Nhà cung cấp", "Người cung cấp thực phẩm ngon.", "Người cung ứng", 3),
    ("Merchandising", "/ˈmɜː.tʃən.daɪ.zɪŋ/", "noun", "Merchandising strategy.", "Nghề buôn bán", "Chiến lược tiếp thị bán hàng.", "Bày bán", 3),
    ("Supply-chain", "/səˈplaɪ ˌtʃeɪn/", "noun", "Global supply chain.", "Chuỗi cung ứng", "Chuỗi cung ứng toàn cầu.", "Quy trình cung cấp", 3),
    ("Markup", "/ˈmɑːk.ʌp/", "noun", "A 50% markup.", "Tiền lãi cộng thêm", "Cộng thêm 50% lợi nhuận.", "Lãi thêm", 3),
    ("Markdown", "/ˈmɑːk.daʊn/", "noun", "Massive markdown.", "Sự giảm giá bán", "Sự giảm giá bán khổng lồ.", "Hạ giá bán", 3),
    ("Rebate", "/ˈriː.beɪt/", "noun", "Claim your tax rebate.", "Sự hoàn tiền", "Yêu cầu hoàn tiền thuế.", "Trả lại tiền", 3),
    ("Remittance", "/rɪˈmɪt.əns/", "noun", "Send a remittance.", "Số tiền được gửi", "Gửi một số tiền.", "Gửi tiền", 3),
    ("Stipend", "/ˈstaɪ.pend/", "noun", "A monthly stipend.", "Lương phụ cấp", "Một khoản phụ cấp hàng tháng.", "Trợ cấp", 3),
    ("Expend", "/ɪkˈspend/", "verb", "Expend effort and money.", "Tiêu xài", "Tiêu tốn công sức và tiền bạc.", "Bỏ ra", 3),
    ("Expenditure", "/ɪkˈspen.dɪ.tʃər/", "noun", "Public expenditure.", "Sự chi tiêu", "Chi tiêu công.", "Khoản chi", 3),
    ("Extravagant", "/ɪkˈstræv.ə.ɡənt/", "adjective", "An extravagant lifestyle.", "Phung phí", "Một lối sống phung phí.", "Tiêu tốn", 3),
    ("Remunerate", "/rɪˈmjuː.nər.eɪt/", "verb", "Remunerate for services.", "Trả công", "Trả công cho các dịch vụ.", "Thù lao", 3),
    ("Disbursement", "/dɪsˈbɜːs.mənt/", "noun", "Loan disbursement.", "Sự giải ngân", "Việc giải ngân khoản vay.", "Xuất tiền", 3),
    ("Reimburse", "/ˌriː.ɪmˈbɜːs/", "verb", "Reimburse travel costs.", "Hoàn trả lại", "Hoàn trả chi phí đi lại.", "Trả tiền lại", 3),
    ("Solvency", "/ˈsɒl.vən.si/", "noun", "Financial solvency.", "Khả năng thanh toán", "Khả năng thanh toán tài chính.", "Đủ tiền trả", 3),
    ("Liquidity", "/lɪˈkwɪd.ɪ.ti/", "noun", "Ensure high liquidity.", "Tính thanh khoản", "Đảm bảo tính thanh khoản cao.", "Dễ đổi ra tiền mặt", 3),
    ("Outsource", "/ˈaʊt.sɔːs/", "verb", "Outsource production.", "Thuê ngoài", "Thuê ngoài khâu sản xuất.", "Thuê làm", 3),
    ("Procurement", "/prəˈkjʊə.mənt/", "noun", "Public procurement.", "Sự thu mua", "Sự thu mua công cộng.", "Mua sắm thiết bị", 3),
    ("Conspicuous-consumption", "/kənˌspɪk.ju.əs kənˈsʌmp.ʃən/", "noun", "Conspicuous consumption.", "Tiêu dùng phô trương", "Việc tiêu dùng mang tính phô trương.", "Xài để khoe", 3),
    ("Retailing", "/ˈriː.teɪ.lɪŋ/", "noun", "Retailing business.", "Ngành bán lẻ", "Kinh doanh ngành bán lẻ.", "Việc bán lẻ", 3),
    ("Monopolize", "/məˈnɒp.əl.aɪz/", "verb", "Monopolize the market.", "Độc quyền", "Thao túng độc quyền thị trường.", "Chiếm một mình", 3),
    ("Franchise", "/ˈfræn.tʃaɪz/", "noun", "Buy a franchise.", "Nhượng quyền", "Mua một thương hiệu nhượng quyền.", "Mua quyền kinh doanh", 3),
    ("Oligopoly", "/ˌɒl.ɪˈɡɒp.əl.i/", "noun", "An oligopoly market.", "Độc quyền nhóm", "Một thị trường độc quyền nhóm.", "Ít người bán", 3),
    ("Subsidize", "/ˈsʌb.sɪ.daɪz/", "verb", "Subsidize housing.", "Trợ cấp", "Trợ cấp nhà ở.", "Hỗ trợ tiền", 3),
    ("Premium", "/ˈpriː.mi.əm/", "adjective", "Premium quality.", "Cao cấp", "Chất lượng cao cấp.", "Chất lượng cao", 3),
]

level_map = ["en_shopping_beginner", "en_shopping_intermediate", "en_shopping_advanced", "en_shopping_expert"]

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

print("Successfully appended 120 words for Shopping (English) to DB!")
