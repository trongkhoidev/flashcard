import json
import os
import time

# pip install google-generativeai
try:
    import google.generativeai as genai
except ImportError:
    print("Please install the google-generativeai package: pip install google-generativeai")
    exit(1)

# Configure API Key
API_KEY = os.environ.get("GEMINI_API_KEY", "YOUR_API_KEY_HERE")
genai.configure(api_key=API_KEY)

# Define topics and languages
TOPICS = [
    {"id": "education", "name": "Giáo dục", "icon": "🎓"},
    {"id": "fashion", "name": "Thời trang", "icon": "👗"},
    {"id": "family", "name": "Gia đình", "icon": "👨‍👩‍👧‍👦"},
    {"id": "shopping", "name": "Mua sắm", "icon": "🛍️"},
    {"id": "health", "name": "Sức khoẻ", "icon": "⚕️"},
    {"id": "weather", "name": "Thời tiết", "icon": "⛅"},
    {"id": "food", "name": "Đồ ăn-uống", "icon": "🍔"},
    {"id": "tech", "name": "Công nghệ", "icon": "💻"},
    {"id": "travel", "name": "Du lịch", "icon": "✈️"},
    {"id": "daily", "name": "Cuộc sống hàng ngày", "icon": "☕"}
]

LEVELS = ["Beginner", "Intermediate", "Advanced", "Expert"]

LANGUAGES = [
    {"code": "en", "name": "Tiếng Anh"},
    {"code": "ja", "name": "Tiếng Nhật"},
    {"code": "ko", "name": "Tiếng Hàn"},
    {"code": "zh", "name": "Tiếng Trung"},
    {"code": "es", "name": "Tiếng Tây Ban Nha"},
    {"code": "de", "name": "Tiếng Đức"},
    {"code": "ru", "name": "Tiếng Nga"}
]

# Configure model
model = genai.GenerativeModel('gemini-1.5-flash', generation_config={"response_mime_type": "application/json"})

def generate_vocab(topic, level, language):
    prompt = f"""
    Bạn là một chuyên gia ngôn ngữ học. Hãy tạo danh sách 30 từ vựng {language['name']} thuộc chủ đề '{topic['name']}' ở cấp độ '{level}'.
    Yêu cầu trả về định dạng JSON MẢNG chứa các object có cấu trúc sau:
    [
      {{
        "frontWord": "Từ vựng ({language['name']})",
        "phonetic": "Phiên âm IPA hoặc Romaji/Pinyin",
        "partOfSpeech": "Từ loại (noun, verb, adj, adv)",
        "frontExample": "1 câu ví dụ bằng {language['name']}",
        "backMeaning": "Nghĩa tiếng Việt",
        "backExampleTranslation": "Bản dịch câu ví dụ sang tiếng Việt",
        "memoryTip": "Mẹo nhớ từ vựng này (ngắn gọn)"
      }}
    ]
    Chỉ trả về JSON hợp lệ, không kèm theo text nào khác.
    """
    
    try:
        response = model.generate_content(prompt)
        return json.loads(response.text)
    except Exception as e:
        print(f"Error generating for {topic['name']} - {language['name']} - {level}: {e}")
        return []

def main():
    print("Starting vocabulary generation process...")
    decks = []
    flashcards = []
    
    # We will just generate 1 topic, 1 language, 1 level as an example to prevent API limit issues
    # Remove the break statements to generate EVERYTHING (Warning: takes a long time and API quota)
    for lang in LANGUAGES:
        for topic in TOPICS:
            for level in LEVELS:
                deck_id = f"{lang['code']}_{topic['id']}_{level.lower()}"
                
                # Add Deck
                decks.append({
                    "id": deck_id,
                    "languageCode": lang["code"],
                    "title": f"{topic['name']}",
                    "subtitle": f"Chủ đề {topic['name']} - {level}",
                    "iconEmoji": topic["icon"],
                    "level": level,
                    "colorHex": "#3B82F6", # You can randomize colors
                    "cardCount": 30
                })
                
                print(f"Generating data for {deck_id}...")
                words = generate_vocab(topic, level, lang)
                
                for word in words:
                    word["deckId"] = deck_id
                    word["languageCode"] = lang["code"]
                    flashcards.append(word)
                
                time.sleep(2) # Prevent rate limiting
                break # Remove this break to generate all levels
            break # Remove this break to generate all topics
        break # Remove this break to generate all languages

    # Save to file
    output_data = {
        "decks": decks,
        "flashCards": flashcards
    }
    
    output_path = "app/src/main/assets/vocab_data.json"
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(output_data, f, ensure_ascii=False, indent=2)
        
    print(f"Successfully generated JSON data to {output_path}")

if __name__ == "__main__":
    main()
