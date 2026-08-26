package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.components.VipAvatarFrame
import com.example.ui.components.VipLevel
import com.example.ui.components.VipLevelSelectorCard
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKPrimaryDark
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary

@Composable
fun UserProfileDialog(
    userName: String,
    userVipLevel: Int = 1,
    streakDays: Int,
    masteredWordsCount: Int,
    totalWordsCount: Int,
    onDismiss: () -> Unit,
    onUpdateName: (String) -> Unit,
    onSelectVipLevel: ((Int) -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    var nameInput by remember { mutableStateOf(userName) }
    var isEditing by remember { mutableStateOf(false) }
    val vipLevelObj = VipLevel.fromLevel(userVipLevel)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chỉnh sửa Hồ sơ",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng", tint = NTKTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Avatar with VIP Outer Border Frame
                VipAvatarFrame(
                    vipLevel = vipLevelObj,
                    avatarSize = 58.dp
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.take(1).uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (isEditing) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Tên người học", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (nameInput.isNotBlank()) {
                                    onUpdateName(nameInput)
                                }
                                isEditing = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary)
                        ) {
                            Text("Lưu", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKTextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { isEditing = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Sửa tên",
                                tint = NTKPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Text(
                        text = if (vipLevelObj == VipLevel.NONE) "Học viên Peace FlashCard" else "Học viên ${vipLevelObj.title} ${vipLevelObj.crownEmoji}",
                        fontSize = 12.sp,
                        fontWeight = if (vipLevelObj != VipLevel.NONE) FontWeight.Bold else FontWeight.Normal,
                        color = if (vipLevelObj != VipLevel.NONE) vipLevelObj.badgeBgColor else NTKTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // VIP Level Border Frame Selector inside Edit Profile
                VipLevelSelectorCard(
                    currentVipLevel = userVipLevel,
                    onSelectVipLevel = { level ->
                        onSelectVipLevel?.invoke(level)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Streak card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$streakDays Ngày",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "Chuỗi học",
                            fontSize = 10.sp,
                            color = Color(0xFFB45309)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Words Mastered card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFECEBFF), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = NTKPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$masteredWordsCount / $totalWordsCount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKPrimaryDark
                        )
                        Text(
                            text = "Đã thuộc",
                            fontSize = 10.sp,
                            color = NTKPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_close_profile"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NTKPrimary)
                ) {
                    Text("Hoàn tất & Lưu thay đổi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                onLogout?.let { logout ->
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = logout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_logout"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Đăng xuất", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
