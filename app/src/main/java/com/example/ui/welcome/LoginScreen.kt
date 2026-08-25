package com.example.ui.welcome

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.R
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKPrimaryLight
import com.example.ui.theme.NTKTextMuted
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onBackToWelcome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSubmit: (suspend (String, String) -> Boolean)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Back Button
                IconButton(
                    onClick = onBackToWelcome,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), CircleShape)
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = NTKTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            // HEADER SECTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chào mừng trở lại!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NTKPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Đăng nhập",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NTKTextPrimary,
                        lineHeight = 34.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tiếp tục hành trình học tập cùng NTK FlashCard",
                        fontSize = 13.sp,
                        color = NTKTextSecondary,
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Rounded Card Frame for Penguin Mascot
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_mascot_penguin_1787286222548),
                        contentDescription = "Penguin Mascot",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // LOGIN FORM CARD
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = NTKPrimary.copy(alpha = 0.15f)
                    ),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // FIELD 1: Tên đăng nhập
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Tên đăng nhập",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKTextPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = { Text("Nhập tên đăng nhập", fontSize = 14.sp, color = NTKTextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = if (username.isNotBlank()) NTKPrimary else NTKTextSecondary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NTKPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_input")
                        )
                    }

                    // FIELD 2: Mật khẩu
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Mật khẩu",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKTextPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Nhập mật khẩu", fontSize = 14.sp, color = NTKTextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = if (password.isNotBlank()) NTKPrimary else NTKTextSecondary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = "Hiển thị mật khẩu",
                                        tint = NTKTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NTKPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input")
                        )
                    }

                    // REMEMBER ME & FORGOT PASSWORD ROW
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { rememberMe = !rememberMe }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (rememberMe) NTKPrimary else Color.Transparent)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (rememberMe) NTKPrimary else Color(0xFFCBD5E1),
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (rememberMe) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ghi nhớ đăng nhập",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = NTKTextPrimary
                            )
                        }

                        Text(
                            text = "Quên mật khẩu?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKPrimary,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, "Tính năng lấy lại mật khẩu qua Email đang được cập nhật", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("forgot_password_link")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // PRIMARY LOGIN BUTTON
                    Button(
                        enabled = !isLoading,
                        onClick = {
                            val userStr = username.ifBlank { "tuanzeebee" }
                            val passStr = password.ifBlank { "123456" }
                            if (onLoginSubmit != null) {
                                isLoading = true
                                coroutineScope.launch {
                                    val success = onLoginSubmit(userStr, passStr)
                                    isLoading = false
                                    if (success) {
                                        Toast.makeText(context, "✨ Đăng nhập thành công! Chào mừng $userStr", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess(userStr)
                                    } else {
                                        Toast.makeText(context, "❌ Tên đăng nhập hoặc mật khẩu không chính xác!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Đăng nhập thành công! Chào mừng $userStr", Toast.LENGTH_SHORT).show()
                                onLoginSuccess(userStr)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = NTKPrimary.copy(alpha = 0.4f)
                            )
                            .testTag("submit_login_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NTKPrimary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Đăng nhập",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // SUBTLE INFO CARD ("Học mọi lúc, mọi nơi")
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NTKPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Security,
                                    contentDescription = null,
                                    tint = NTKPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Học mọi lúc, mọi nơi",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NTKTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Đăng nhập để đồng bộ tiến độ học tập và trải nghiệm nhiều tính năng thú vị.",
                                    fontSize = 11.sp,
                                    color = NTKTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Outlined.Public,
                                contentDescription = null,
                                tint = NTKPrimary.copy(alpha = 0.7f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // FOOTER: "Chưa có tài khoản? Đăng ký ngay"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Chưa có tài khoản? ",
                    fontSize = 14.sp,
                    color = NTKTextSecondary
                )
                Text(
                    text = "Đăng ký ngay",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NTKPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToRegister() }
                        .testTag("register_link")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        }
    }
}
