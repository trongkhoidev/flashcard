-- ============================================================================
-- 1. BẢNG: decks (Danh mục các bộ thẻ bài học)
-- ============================================================================
CREATE TABLE IF NOT EXISTS decks (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    languageCode TEXT NOT NULL,
    level TEXT NOT NULL,
    coverUrl TEXT,
    cardCount INTEGER NOT NULL DEFAULT 0,
    isCustom INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL
);

-- Dữ liệu mẫu cho bảng decks:
INSERT INTO decks (id, title, description, languageCode, level, coverUrl, cardCount, isCustom, createdAt)
VALUES 
('en_basics_01', 'English Basics 101', 'Từ vựng tiếng Anh giao tiếp hàng ngày', 'en', 'Cơ bản', 'https://example.com/en_basics.png', 50, 0, 1771747200000),
('ja_n5_vocab', 'Tiếng Nhật N5 Khởi Động', 'Bộ từ vựng cơ bản luyện thi JLPT N5', 'ja', 'N5', 'https://example.com/ja_n5.png', 60, 0, 1771747200000);


-- ============================================================================
-- 2. BẢNG: flashcards (Thẻ từ vựng & Thuật toán Ghi nhớ ngắt quãng SRS SM-2)
-- ============================================================================
CREATE TABLE IF NOT EXISTS flashcards (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    deckId TEXT NOT NULL,
    languageCode TEXT NOT NULL,
    frontWord TEXT NOT NULL,
    backMeaning TEXT NOT NULL,
    phonetic TEXT NOT NULL,
    frontExample TEXT,
    backExampleTranslation TEXT,
    imageUrl TEXT,
    audioUrl TEXT,
    difficulty INTEGER NOT NULL DEFAULT 0,
    isStarred INTEGER NOT NULL DEFAULT 0,
    isMastered INTEGER NOT NULL DEFAULT 0,
    reviewCount INTEGER NOT NULL DEFAULT 0,
    lastReviewedTimestamp INTEGER NOT NULL DEFAULT 0,
    srsInterval INTEGER NOT NULL DEFAULT 1,
    srsEaseFactor REAL NOT NULL DEFAULT 2.5,
    srsRepetitions INTEGER NOT NULL DEFAULT 0,
    nextReviewTimestamp INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (deckId) REFERENCES decks(id) ON DELETE CASCADE
);

-- Dữ liệu mẫu cho bảng flashcards:
INSERT INTO flashcards (deckId, languageCode, frontWord, backMeaning, phonetic, frontExample, backExampleTranslation, imageUrl, audioUrl, difficulty, isStarred, isMastered, reviewCount, lastReviewedTimestamp, srsInterval, srsEaseFactor, srsRepetitions, nextReviewTimestamp)
VALUES 
('en_basics_01', 'en', 'Resilience', 'Khả năng phục hồi, kiên cường', '/rɪˈzɪl.jəns/', 'Courage and resilience in the face of adversity.', 'Lòng dũng cảm và sự kiên cường trước nghịch cảnh.', NULL, NULL, 1, 1, 1, 4, 1771833600000, 6, 2.6, 3, 1772352000000),
('ja_n5_vocab', 'ja', 'こんにちは (Konnichiwa)', 'Xin chào (buổi trưa/chiều)', '/kon-ni-chi-wa/', '皆さん、こんにちは！', 'Xin chào tất cả mọi người!', NULL, NULL, 2, 0, 0, 1, 1771833600000, 1, 2.5, 1, 1771920000000);


-- ============================================================================
-- 3. BẢNG: user_languages (Theo dõi tiến trình học Đa ngôn ngữ)
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_languages (
    languageCode TEXT PRIMARY KEY NOT NULL,
    displayName TEXT NOT NULL,
    flagEmoji TEXT NOT NULL,
    isCurrentActive INTEGER NOT NULL DEFAULT 0,
    dailyGoalCards INTEGER NOT NULL DEFAULT 20,
    masteredCardsCount INTEGER NOT NULL DEFAULT 0,
    totalWordsEnrolled INTEGER NOT NULL DEFAULT 50,
    streakDays INTEGER NOT NULL DEFAULT 0,
    level TEXT NOT NULL DEFAULT 'Mới bắt đầu',
    enrolledTimestamp INTEGER NOT NULL,
    lastStudiedTimestamp INTEGER NOT NULL
);

-- Dữ liệu mẫu cho bảng user_languages:
INSERT INTO user_languages (languageCode, displayName, flagEmoji, isCurrentActive, dailyGoalCards, masteredCardsCount, totalWordsEnrolled, streakDays, level, enrolledTimestamp, lastStudiedTimestamp)
VALUES 
('en', 'Tiếng Anh', '🇬🇧', 1, 20, 32, 50, 7, 'Cơ bản', 1771000000000, 1771833600000),
('ja', 'Tiếng Nhật', '🇯🇵', 0, 15, 10, 60, 3, 'N5 Sơ cấp', 1771200000000, 1771747200000);


-- ============================================================================
-- 4. BẢNG: user_profiles (Hồ sơ người dùng, Streak & Điểm thưởng)
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_profiles (
    id INTEGER PRIMARY KEY NOT NULL,
    userName TEXT NOT NULL DEFAULT 'Bạn Học',
    avatarEmoji TEXT NOT NULL DEFAULT '🦉',
    avatarBgColorHex TEXT NOT NULL DEFAULT '#EEF2FF',
    vipLevel INTEGER NOT NULL DEFAULT 1,
    streakDays INTEGER NOT NULL DEFAULT 0,
    maxStreakDays INTEGER NOT NULL DEFAULT 0,
    totalPoints INTEGER NOT NULL DEFAULT 0,
    totalCardsLearned INTEGER NOT NULL DEFAULT 0,
    lastActiveTimestamp INTEGER NOT NULL
);

-- Dữ liệu mẫu cho bảng user_profiles:
INSERT INTO user_profiles (id, userName, avatarEmoji, avatarBgColorHex, vipLevel, streakDays, maxStreakDays, totalPoints, totalCardsLearned, lastActiveTimestamp)
VALUES 
(1, 'Bạn Học', '🦉', '#EEF2FF', 2, 7, 14, 1850, 42, 1771833600000);


-- ============================================================================
-- 5. BẢNG: user_accounts (Tài khoản bảo mật & Xác thực)
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_accounts (
    userId TEXT PRIMARY KEY NOT NULL,
    email TEXT NOT NULL,
    displayName TEXT NOT NULL,
    passwordHash TEXT NOT NULL,
    salt TEXT NOT NULL,
    authProvider TEXT NOT NULL DEFAULT 'LOCAL',
    createdAt INTEGER NOT NULL,
    lastLoginAt INTEGER NOT NULL
);

-- Dữ liệu mẫu cho bảng user_accounts:
INSERT INTO user_accounts (userId, email, displayName, passwordHash, salt, authProvider, createdAt, lastLoginAt)
VALUES 
('usr_9981aef', 'learner@example.com', 'Nguyen Van A', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 's@lt_key_123', 'LOCAL', 1771000000000, 1771833600000),
('usr_gg_7721', 'google_user@gmail.com', 'Le Thi B', '', '', 'GOOGLE', 1771100000000, 1771820000000);


-- ============================================================================
-- 6. BẢNG: study_sessions (Nhật ký các buổi học từ vựng)
-- ============================================================================
CREATE TABLE IF NOT EXISTS study_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    deckId TEXT NOT NULL,
    cardsStudied INTEGER NOT NULL,
    cardsMastered INTEGER NOT NULL,
    durationSeconds INTEGER NOT NULL,
    timestamp INTEGER NOT NULL,
    FOREIGN KEY (deckId) REFERENCES decks(id) ON DELETE CASCADE
);

-- Dữ liệu mẫu cho bảng study_sessions:
INSERT INTO study_sessions (deckId, cardsStudied, cardsMastered, durationSeconds, timestamp)
VALUES 
('en_basics_01', 20, 18, 420, 1771830000000),
('ja_n5_vocab', 15, 12, 350, 1771740000000);


-- ============================================================================
-- 7. BẢNG: quiz_records (Kết quả thi Trắc nghiệm & Minigame Ghép thẻ)
-- ============================================================================
CREATE TABLE IF NOT EXISTS quiz_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    deckId TEXT NOT NULL,
    totalQuestions INTEGER NOT NULL,
    correctAnswers INTEGER NOT NULL,
    scorePercentage INTEGER NOT NULL,
    pointsEarned INTEGER NOT NULL,
    mode TEXT NOT NULL,
    durationSeconds INTEGER NOT NULL,
    timestamp INTEGER NOT NULL,
    FOREIGN KEY (deckId) REFERENCES decks(id) ON DELETE CASCADE
);

-- Dữ liệu mẫu cho bảng quiz_records:
INSERT INTO quiz_records (deckId, totalQuestions, correctAnswers, scorePercentage, pointsEarned, mode, durationSeconds, timestamp)
VALUES 
('en_basics_01', 10, 9, 90, 90, 'QUIZ_MULTIPLE_CHOICE', 65, 1771831000000),
('ja_n5_vocab', 12, 12, 100, 120, 'MATCH_GAME', 48, 1771745000000);


-- ============================================================================
-- 8. BẢNG: study_schedules (Lịch hẹn giờ thông báo nhắc nhở)
-- ============================================================================
CREATE TABLE IF NOT EXISTS study_schedules (
    id INTEGER PRIMARY KEY NOT NULL,
    hourOfDay INTEGER NOT NULL,
    minuteOfHour INTEGER NOT NULL,
    isEnabled INTEGER NOT NULL DEFAULT 1,
    lastTriggeredDate TEXT NOT NULL DEFAULT '',
    streakCount INTEGER NOT NULL DEFAULT 0
);

-- Dữ liệu mẫu cho bảng study_schedules:
INSERT INTO study_schedules (id, hourOfDay, minuteOfHour, isEnabled, lastTriggeredDate, streakCount)
VALUES 
(1, 20, 30, 1, '2026-08-23', 7);