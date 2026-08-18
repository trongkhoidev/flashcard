package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.DeckEntity
import com.example.data.model.FlashCardEntity
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary

@Composable
fun CreateCardDialog(
    deckId: String,
    languageCode: String,
    onDismiss: () -> Unit,
    onSave: (FlashCardEntity) -> Unit
) {
    var frontWord by remember { mutableStateOf("") }
    var phonetic by remember { mutableStateOf("") }
    var partOfSpeech by remember { mutableStateOf("noun") }
    var frontExample by remember { mutableStateOf("") }
    var backMeaning by remember { mutableStateOf("") }
    var backExampleTranslation by remember { mutableStateOf("") }
    var memoryTip by remember { mutableStateOf("") }

    val partsOfSpeech = listOf("noun", "verb", "adjective", "adverb", "phrase")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thêm Flashcard Mới",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = NTKTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Front Word
                OutlinedTextField(
                    value = frontWord,
                    onValueChange = { frontWord = it },
                    label = { Text("Từ vựng / Cụm từ (*)") },
                    placeholder = { Text("Ví dụ: Eloquent, 안녕하세요...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_front_word"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phonetic
                OutlinedTextField(
                    value = phonetic,
                    onValueChange = { phonetic = it },
                    label = { Text("Phiên âm / Romaji / Pinyin") },
                    placeholder = { Text("Ví dụ: /ˈel.ə.kwənt/") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Part of Speech selector
                Text(
                    text = "Loại từ:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NTKTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    partsOfSpeech.forEach { pos ->
                        val isSelected = partOfSpeech == pos
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NTKPrimary else Color(0xFFF1F5F9),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { partOfSpeech = pos }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = pos,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else NTKTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Meaning
                OutlinedTextField(
                    value = backMeaning,
                    onValueChange = { backMeaning = it },
                    label = { Text("Giải nghĩa tiếng Việt (*)") },
                    placeholder = { Text("Ví dụ: Hùng biện, lưu loát...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_back_meaning"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = false,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Example sentence
                OutlinedTextField(
                    value = frontExample,
                    onValueChange = { frontExample = it },
                    label = { Text("Câu ví dụ") },
                    placeholder = { Text("Ví dụ: She gave an eloquent speech.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = false,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Memory Tip
                OutlinedTextField(
                    value = memoryTip,
                    onValueChange = { memoryTip = it },
                    label = { Text("Mẹo ghi nhớ / Gợi ý") },
                    placeholder = { Text("Ví dụ: Liên tưởng đến...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Save Button
                Button(
                    onClick = {
                        if (frontWord.isNotBlank() && backMeaning.isNotBlank()) {
                            onSave(
                                FlashCardEntity(
                                    deckId = deckId,
                                    languageCode = languageCode,
                                    frontWord = frontWord.trim(),
                                    phonetic = phonetic.trim(),
                                    partOfSpeech = partOfSpeech,
                                    frontExample = frontExample.trim(),
                                    backMeaning = backMeaning.trim(),
                                    backExampleTranslation = backExampleTranslation.trim(),
                                    memoryTip = memoryTip.trim()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_save_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary),
                    enabled = frontWord.isNotBlank() && backMeaning.isNotBlank()
                ) {
                    Text("Lưu Flashcard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CreateDeckDialog(
    currentLanguageCode: String,
    onDismiss: () -> Unit,
    onSave: (DeckEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("📚") }
    var selectedLevel by remember { mutableStateOf("Cơ bản") }

    val emojis = listOf("📚", "💡", "✈️", "☕", "💼", "🍲", "🎯", "🌟", "🌸", "🔥")
    val levels = listOf("Cơ bản", "Trung cấp", "Nâng cao")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tạo Bộ Thẻ Mới",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = NTKTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên bộ từ vựng (*)") },
                    placeholder = { Text("Ví dụ: 100 Từ vựng phỏng vấn") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_deck_title"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Mô tả ngắn") },
                    placeholder = { Text("Ví dụ: Chuẩn bị xin việc công ty nước ngoài") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Biểu tượng (Icon):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NTKTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojis.take(5).forEach { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(if (isSelected) Color(0xFFECEBFF) else Color(0xFFF8FAFC), CircleShape)
                                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NTKPrimary else Color(0xFFE2E8F0), CircleShape)
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Cấp độ:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NTKTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    levels.forEach { level ->
                        val isSelected = selectedLevel == level
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) NTKPrimary else Color(0xFFF1F5F9),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedLevel = level }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = level,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else NTKTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newId = "custom_${System.currentTimeMillis()}"
                            onSave(
                                DeckEntity(
                                    id = newId,
                                    languageCode = currentLanguageCode,
                                    title = title.trim(),
                                    subtitle = subtitle.trim(),
                                    iconEmoji = selectedEmoji,
                                    level = selectedLevel,
                                    colorHex = "#4D47E9",
                                    isCustom = true
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_save_deck"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary),
                    enabled = title.isNotBlank()
                ) {
                    Text("Tạo Bộ Thẻ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
