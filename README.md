# NTK FlashCard — Ứng dụng học từ vựng đa ngôn ngữ

> Học nhanh – Nhớ lâu · Mọi ngôn ngữ trong tầm tay

**NTK FlashCard** là ứng dụng Android học từ vựng theo phương pháp Flashcard, kết hợp **Spaced Repetition (SRS / SuperMemo-2)**, **Trắc nghiệm (Quiz)**, **Ghép từ (Word Match)**, **thông báo nhắc học thông minh** và **widget từ vựng trên màn hình chính**. Ứng dụng hỗ trợ **10 ngôn ngữ** và hoạt động **hoàn toàn ngoại tuyến** cho việc học & phát âm.

---

## 1. Tính năng chính

| Nhóm | Tính năng |
| --- | --- |
| **Học từ vựng** | Thẻ Flashcard lật 3D, khung VIP động, phát âm, chế độ tự động lướt (Auto-play), xáo trộn thẻ |
| **Ghi nhớ (SRS)** | Thuật toán **SuperMemo-2**: lặp lại ngắt quãng, đánh giá "Đã thuộc / Chưa thuộc", interval & ease factor |
| **Luyện tập** | Trắc nghiệm 4 đáp án có **điểm số + hệ số chuỗi (streak multiplier) + pháo hoa**; Ghép từ với nghĩa |
| **Tài khoản** | Đăng ký / Đăng nhập (xác thực cục bộ), Onboarding 7 bước cá nhân hoá lần đầu |
| **Nhắc nhở thông minh** | AlarmManager hẹn giờ học, notification nhận biết ngữ cảnh (đã học / chưa học / streak / "Để sau") |
| **Widget** | Widget từ vựng trên màn hình chính: từ mới, streak, tự làm mới mỗi 8 giây |
| **Xã hội & thưởng** | Bảng xếp hạng (Leaderboard), hệ thống VIP 8 cấp, điểm thưởng |
| **Đa ngôn ngữ** | 10 ngôn ngữ: Anh, Hàn, Nhật, Việt, Trung, Pháp, Tây Ban Nha, Đức, Ý, Bồ Đào Nha |
| **Lịch sử** | Ghi nhật ký phiên học (`study_sessions`) và kết quả quiz/match (`quiz_records`) |

## 2. Công nghệ & thư viện

- **Ngôn ngữ:** Kotlin `2.2.10`
- **UI:** Jetpack Compose (BOM `2024.09.00`), Material 3, Material Icons, Coil `2.7.0`
- **Kiến trúc:** MVVM + Repository, điều hướng bằng `ScreenState` sealed class (không dùng Navigation Component)
- **Lưu trữ:** Room `2.7.0` (SQLite, 8 bảng), dữ liệu mẫu nhúng trong mã nguồn
- **Bất đồng bộ:** Kotlin Coroutines & Flow `1.10.2`
- **Phát âm:** Android `TextToSpeech`
- **Thông báo & hẹn giờ:** `NotificationCompat`, `AlarmManager`, `AppWidget` (RemoteViews)
- **Hỗ trợ:** Firebase BOM (App Check, Firebase AI — dự phòng), Moshi + Retrofit (dự phòng cho tính năng mạng), Robolectric + Roborazzi (test)

## 3. Kiến trúc tổng quan

Mô hình **MVVM + Repository**, luồng dữ liệu một chiều:

```
┌────────────────────────  UI (Compose)  ────────────────────────┐
│ Screens · Components · Theme · Dialogs · Leaderboard · VIP      │
│ Chỉ hiển thị & phát sự kiện qua callback, không chứa logic     │
└──────────────────────────────┬──────────────────────────────────┘
                               │ gọi hàm / quan sát StateFlow
┌──────────────────────────────▼──────────────────────────────────┐
│                  MainViewModel + Notification/Widget            │
│ Điều hướng (ScreenState) · Điều phối · TTS · SmartNotification │
└──────────────────────────────┬──────────────────────────────────┘
                               │ suspend / Flow
┌──────────────────────────────▼──────────────────────────────────┐
│             FlashCardRepository (8 DAO, Single Source of Truth) │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│         Room Database — 8 bảng (AppDatabase, 8 DAO)             │
└─────────────────────────────────────────────────────────────────┘
```

## 4. Cấu trúc mã nguồn

```
app/src/main/java/com/example/
├── MainActivity.kt                     # Điểm vào + điều hướng + banner thông báo in-app
├── audio/TTSManager.kt                 # Phát âm đa ngôn ngữ
├── data/
│   ├── model/                          # 8 entity + AppLanguage (10 ngôn ngữ) + StudySchedule
│   ├── local/                          # AppDatabase + 8 DAO + DefaultVocabData
│   └── repository/FlashCardRepository.kt
├── notification/                       # SmartNotificationEngine, NotificationHelper,
│                                       # StudyAlarmScheduler/Receiver, NotificationActionReceiver
├── widget/VocabularyStreakWidgetProvider.kt
└── ui/
    ├── viewmodel/MainViewModel.kt      # ScreenState + StateFlow + nghiệp vụ
    ├── theme/  components/             # (VipAvatarFrame, Flashcard3DView, ...)
    ├── welcome/  home/  detail/        # Welcome, Login, Register, Onboarding, Home, DeckDetail
    ├── study/  quiz/  match/           # Màn hình học & mini-game
    ├── leaderboard/  dialogs/
```

## 5. Xây dựng & chạy

```bash
# Yêu cầu: Android Studio (JDK 11+), compileSdk 36
./gradlew assembleDebug        # Build APK debug
./gradlew installDebug         # Cài lên thiết bị/emulator
./gradlew testDebugUnitTest    # Chạy unit test (Robolectric/Roborazzi)
```

> Bản release ký bằng biến môi trường `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`. File `database/databaseflashcard.sql` là lược đồ SQL tham khảo (để trình bày thiết kế CSDL).

## 6. Phân công công việc (Team 3 người)

| Thành viên | Phạm vi | Tài liệu chi tiết |
| --- | --- | --- |
| **Khôi — UI/UX** | Toàn bộ giao diện & hiệu ứng Compose (thư mục `ui/`) | [`DOC_KHOI_UI.md`](DOC_KHOI_UI.md) |
| **Nam — Data Layer** | CSDL (8 bảng), 8 DAO, Repository, dữ liệu mẫu (thư mục `data/`) | [`DOC_NAM_DATA.md`](DOC_NAM_DATA.md) |
| **Tuấn — Business Logic** | ViewModel, điều hướng, âm thanh TTS, notification, widget (`viewmodel/`, `audio/`, `notification/`, `widget/`, `MainActivity.kt`) | [`DOC_TUAN_LOGIC.md`](DOC_TUAN_LOGIC.md) |

## 7. Tài liệu liên quan

| Tài liệu | Nội dung |
| --- | --- |
| [`DOC_PROJECT_OVERVIEW.md`](DOC_PROJECT_OVERVIEW.md) | **Tổng quát toàn dự án**: luồng hoạt động, toàn bộ chức năng, kiến trúc & sơ đồ chi tiết — đọc đầu tiên |
| [`DOC_KHOI_UI.md`](DOC_KHOI_UI.md) | Chi tiết phần giao diện cho Khôi |
| [`DOC_NAM_DATA.md`](DOC_NAM_DATA.md) | Chi tiết phần dữ liệu cho Nam |
| [`DOC_TUAN_LOGIC.md`](DOC_TUAN_LOGIC.md) | Chi tiết phần logic, notification & widget cho Tuấn |
