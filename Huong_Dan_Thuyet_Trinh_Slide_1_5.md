# Hướng dẫn Thuyết trình & Kiến thức - 5 Slide Đầu (Dự án FlashCard)

Tài liệu này cung cấp chi tiết về kiến thức công nghệ được đề cập trong 5 slide đầu của file thuyết trình, cùng với kịch bản thuyết trình hiệu quả giúp team ghi điểm với ban giám khảo.

## Phần 1: Giải thích các Công nghệ & Khái niệm (Slide 3 & 5)

Dự án FlashCard sử dụng một stack công nghệ hiện đại của hệ sinh thái Android, kết hợp với các component hệ thống cơ bản. Bạn cần nắm chắc các khái niệm sau để tự tin trả lời Q&A:

### 1. Công nghệ Lõi & Kiến trúc (Core Tech & Architecture)
* **Kotlin**: Ngôn ngữ lập trình chính thức và hiện đại nhất cho Android hiện nay do Google khuyến nghị. Giúp code ngắn gọn, an toàn (tránh lỗi `NullPointerException`) và hỗ trợ lập trình bất đồng bộ xuất sắc thông qua Coroutines.
* **Jetpack Compose**: Bộ toolkit xây dựng UI khai báo (Declarative UI) hiện đại nhất của Android. Thay vì dùng file XML phức tạp để vẽ giao diện, Compose cho phép viết UI hoàn toàn bằng code Kotlin, giúp UI phản ứng linh hoạt với dữ liệu, dễ tái sử dụng component và giảm thiểu lỗi.
* **MVVM + Repository**: Mẫu kiến trúc tiêu chuẩn (Model - View - ViewModel).
  * **UI (View)**: Chỉ làm nhiệm vụ hiển thị dữ liệu (xây dựng bằng Jetpack Compose).
  * **ViewModel**: Chứa logic của UI, giữ trạng thái (**StateFlow**) để giao diện tự động cập nhật khi dữ liệu thay đổi.
  * **Repository**: Đóng vai trò trung gian quản lý các nguồn dữ liệu, quyết định việc lấy data từ Database cục bộ hay từ Network.
  * **DAO -> Database**: Tương tác trực tiếp với cơ sở dữ liệu.
  > *Tác dụng của MVVM*: Giúp code dễ bảo trì, dễ test, tách biệt rõ ràng phần giao diện và phần xử lý logic.
* **StateFlow**: Một API của Kotlin Coroutines dùng để quản lý luồng dữ liệu trạng thái (state). Nó liên tục theo dõi sự thay đổi của dữ liệu và tự động cập nhật lên UI một cách mượt mà, an toàn.
* **Room SQLite**: Thư viện của Google bọc ngoài SQLite cốt lõi, giúp thao tác với database cục bộ an toàn, dễ dàng hơn thông qua các annotation thay vì phải viết câu lệnh SQL thuần rườm rà, dễ sai sót.

### 2. Thành phần Hệ thống Android (System Components)
* **Activity, Fragment, Intent**: Các thành phần cốt lõi của Android. **Activity** là một màn hình của ứng dụng, **Fragment** là một phần giao diện có thể tái sử dụng (thường gắn vào Activity), và **Intent** dùng để điều hướng, chuyển đổi giữa các màn hình hoặc gọi các dịch vụ hệ thống.
* *(Lưu ý: Slide có nhắc đến cả Jetpack Compose và Fragment/RecyclerView. Điều này cho thấy ứng dụng đang sử dụng mô hình kết hợp (Hybrid) – có thể một số màn hình phức tạp vẫn dùng RecyclerView/Adapter cũ, hoặc team đang trong quá trình chuyển đổi dần sang Compose).*
* **View & Layout (RecyclerView, Adapter)**: Thành phần giao diện truyền thống dùng để hiển thị danh sách lớn cuộn mượt mà. **Adapter** làm nhiệm vụ nạp dữ liệu (bind data) vào từng item của danh sách.
* **BroadcastReceiver, Notification**: Dùng để lắng nghe các sự kiện từ hệ thống (vd: báo thức, khởi động máy) và đẩy thông báo (Notification) nhắc nhở người dùng học từ vựng mỗi ngày (Tính năng Smart Notifications).
* **Thread, Handler, Sync Task**: Các cơ chế xử lý đa luồng (Multi-threading). Giúp ứng dụng chạy các tác vụ nặng (như thuật toán tính toán thời gian, truy vấn DB) dưới nền (background) mà không làm đơ giao diện chính (Main/UI Thread).

### 3. Thuật toán & Tính năng nổi bật
* **Spaced Repetition System (SRS)**: Hệ thống học lặp lại ngắt quãng. Thuật toán (như SuperMemo-2) sẽ tính toán và nhắc nhở người dùng ôn lại từ vựng vào đúng thời điểm họ sắp quên, giúp tối ưu hóa trí nhớ dài hạn.
* **Gamification**: Ứng dụng các cơ chế của trò chơi (Streak - chuỗi ngày học liên tục, Danh hiệu VIP, Hiệu ứng pháo hoa) để tạo động lực, sự hứng thú và giữ chân người dùng ở lại với app.

---

## Phần 2: Hướng dẫn Thuyết trình Chi tiết (Dành cho 5 Slide Đầu)

> **Mẹo chung**: Đừng đọc text trên slide! Hãy dùng slide làm dàn ý, mắt hướng về ban giám khảo/khán giả và nói bằng ngôn ngữ tự nhiên. 

### Slide 1: Giới thiệu Dự án (Title Slide)
* **Thời lượng**: 30 - 45 giây.
* **Mục tiêu**: Thu hút sự chú ý, tạo không khí tự tin, chuyên nghiệp từ giây đầu tiên.
* **Cách trình bày & Body Language**: 
  * Đứng thẳng, mỉm cười tự tin. Quét ánh mắt một vòng quanh phòng/nhìn thẳng ban giám khảo.
  * Giọng nói rõ ràng, dõng dạc.
* **Kịch bản gợi ý**:
  > *"Xin chào ban giám khảo và các anh chị. Chúng em là team PEACE. Hôm nay, team chúng em rất vinh dự được trình bày về sản phẩm tâm huyết của nhóm trong kỳ thực tập tại Samsung: **FlashCard - Ứng dụng Android học từ vựng đa ngôn ngữ**. Sản phẩm này được chúng em phát triển hoàn toàn bằng ngôn ngữ Kotlin với mong muốn mang lại một giải pháp học ngoại ngữ thông minh và hiện đại nhất."*

### Slide 2: Giới thiệu Team
* **Thời lượng**: 45 giây.
* **Mục tiêu**: Thể hiện tinh thần làm việc nhóm và sự phân chia vai trò rõ ràng.
* **Cách trình bày & Body Language**: 
  * Hướng tay (bàn tay mở, lịch sự) về phía thành viên khi nhắc đến tên họ.
* **Kịch bản gợi ý**:
  > *"Để hoàn thành dự án này, team PEACE chúng em đã phân công và phối hợp chặt chẽ với nhau: 
  > Đầu tiên là em, [Tên bạn], với vai trò [Vai trò của bạn - vd: Trọng Khôi - Project Lead & Front-end], chịu trách nhiệm quản lý tiến độ dự án, định hướng kỹ thuật và xây dựng UI. 
  > Tiếp theo là bạn Đình Tuấn - Android Developer, phụ trách thiết kế hệ thống Back-end và phát triển các tính năng cốt lõi. 
  > Và cuối cùng là bạn Đăng Nam - đảm nhận việc xử lý Data học thuật và làm Tester, đóng vai trò gác cổng để đảm bảo tính nhất quán và ổn định của ứng dụng. Mỗi người một thế mạnh, chúng em đã ráp nối lại để tạo nên FlashCard."*

### Slide 3: Tổng quan Dự án (Project Overview)
* **Thời lượng**: 1 phút - 1.5 phút.
* **Mục tiêu**: Cho giám khảo thấy ngay "bức tranh toàn cảnh", quy mô và sự chuyên nghiệp trong kiến trúc ứng dụng.
* **Cách trình bày & Body Language**:
  * Nhấn giọng vào các "từ khóa ăn tiền" như: *10 ngôn ngữ, Spaced Repetition, MVVM, Jetpack Compose*.
  * Dùng tay đếm nhẩm (1, 2, 3) khi liệt kê các điểm chính để tạo nhịp điệu.
* **Kịch bản gợi ý**:
  > *"Nhìn một cách tổng quan, FlashCard không chỉ là một app xem từ vựng thông thường. 
  > Về **mục đích**, nền tảng của chúng em hỗ trợ học đến 10 ngôn ngữ khác nhau.
  > Để làm được điều đó, **công nghệ lõi** chúng em áp dụng là những công nghệ mới nhất từ hệ sinh thái Google: Kotlin, Jetpack Compose cho UI, và quản lý luồng dữ liệu với StateFlow kết hợp Room SQLite.
  > Điểm làm nên giá trị của ứng dụng nằm ở các **tính năng thông minh**: Thuật toán lặp lại ngắt quãng (SRS) giúp tối ưu trí nhớ, hệ thống Gamification như chuỗi Streak, cấp độ VIP để tạo động lực, cùng với Widget và Smart Notifications.
  > Tất cả được xây dựng vững chắc trên **kiến trúc MVVM tiêu chuẩn**, phân tách rõ ràng từ UI xuống Database, giúp app hoạt động cực kỳ mượt mà và dễ dàng bảo trì."*

### Slide 4: Mục đích Dự án (Problem - Solution - Goal)
* **Thời lượng**: 1 phút.
* **Mục tiêu**: Thuyết phục người nghe về lý do dự án ra đời. Dẫn dắt logic từ Nỗi đau (Problem) -> Giải pháp (Solution) -> Mục tiêu (Goal).
* **Cách trình bày & Body Language**: 
  * Khi nói về Nỗi đau: Giọng trầm xuống một chút, thể hiện sự đồng cảm với người dùng.
  * Khi nói về Giải pháp & Mục tiêu: Giọng sáng hơn, hào hứng và tự tin.
* **Kịch bản gợi ý**:
  > *"Vậy tại sao chúng em lại chọn phát triển đề tài này? 
  > Bắt nguồn từ **vấn đề thực tế (Problem)**: Việc học từ vựng hiện nay của sinh viên thường khô khan, thiếu hệ thống, dẫn đến tình trạng học trước quên sau. Đặc biệt với những ai học đa ngôn ngữ, việc quản lý từ vựng trở nên vô cùng khó khăn.
  > Từ đó, **giải pháp (Solution)** của team là tạo ra ứng dụng FlashCard tận dụng sức mạnh của Kotlin và giao diện hiện đại của Jetpack Compose.
  > **Mục tiêu cuối cùng (Goal)** của chúng em là mang đến một công cụ học tập không chỉ hiệu quả về mặt học thuật, mà còn phải thật dễ sử dụng, khơi gợi cảm hứng học tập mỗi ngày cho người dùng."*

### Slide 5: Công nghệ sử dụng (Technology Stack)
* **Thời lượng**: 1.5 phút.
* **Mục tiêu**: Khẳng định hàm lượng kỹ thuật của dự án. Cho thấy team hiểu rất rõ công cụ mình đang dùng từ tầng giao diện đến hệ thống ngầm.
* **Cách trình bày & Body Language**:
  * Đừng chỉ đọc tên công nghệ, hãy kèm theo *lý do tại sao sử dụng nó*.
  * Hướng tay về các khối tương ứng trên slide.
* **Kịch bản gợi ý**:
  > *"Để hiện thực hóa giải pháp trên, đây là bức tranh hệ thống công nghệ chúng em đã áp dụng:
  > Ở tầng **UI & View**, chúng em sử dụng linh hoạt giữa *Jetpack Compose* hiện đại và các view truyền thống như *RecyclerView, Adapter* để đảm bảo hiệu năng tối đa khi cuộn danh sách hàng ngàn từ vựng.
  > Ở tầng **Hệ thống (System)**, ứng dụng bám sát vòng đời của *Activity* và *Fragment*, kết hợp *Intent* để điều hướng và truyền dữ liệu an toàn.
  > Về **Dữ liệu (Data)**, toàn bộ tài nguyên học thuật được lưu trữ trơn tru bằng *SQLite* (thông qua Room), đảm bảo trải nghiệm học tập offline tức thì mà không cần mạng.
  > Cuối cùng, để xử lý các thuật toán nặng hay gửi thông báo nhắc học, chúng em vận dụng sức mạnh đa luồng qua các cơ chế *Thread, Handler, Sync Task* và *BroadcastReceiver* của Android. Sự kết hợp đồng bộ này đảm bảo ứng dụng luôn mượt mà dù xử lý lượng dữ liệu đa ngôn ngữ rất lớn."*

---

## 💡 Lời khuyên chuẩn bị Q&A (Dự phòng ban giám khảo hỏi)
1. **Tại sao dự án dùng Jetpack Compose rồi mà vẫn nhắc đến RecyclerView/Fragment ở Slide 5?**
   * *Trả lời:* "Dạ, dự án của chúng em áp dụng cách tiếp cận Hybrid (Kết hợp). Những màn hình mới, linh hoạt được xây dựng hoàn toàn bằng Jetpack Compose để tăng tốc độ phát triển. Tuy nhiên, ở một số màn hình danh sách quá phức tạp cần custom animation sâu, hoặc tái sử dụng lại các resource có sẵn, team vẫn tận dụng sức mạnh của RecyclerView và Fragment để tối ưu hiệu năng."
2. **Kiến trúc MVVM mang lại lợi ích thực tế gì cho dự án của em?**
   * *Trả lời:* "Dạ MVVM giúp chúng em tách biệt hoàn toàn phần vẽ giao diện (UI) và phần xử lý logic (ViewModel). Khi dữ liệu từ database thay đổi, StateFlow trong ViewModel sẽ tự động báo cho UI cập nhật. Điều này giúp app không bao giờ bị đơ, code của bạn làm UI và bạn làm Logic không bị đụng độ nhau, và sau này viết Unit Test rất dễ dàng."
3. **Thuật toán Spaced Repetition (SRS) trong app hoạt động ra sao?**
   * *Trả lời:* "Dạ nó dựa vào phản hồi của người dùng khi lật thẻ flashcard. Nếu người dùng nhớ tốt, từ đó sẽ lâu lặp lại hơn (ví dụ 3 ngày, 7 ngày sau mới hiện). Nếu người dùng quên, từ đó sẽ lặp lại ngay trong phiên học đó hoặc vào ngày mai. Việc tính toán khoảng thời gian này được xử lý ngầm (background) để không ảnh hưởng đến giao diện học."
