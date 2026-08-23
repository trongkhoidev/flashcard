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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.NTKPrimary
import com.example.ui.theme.NTKPrimaryDark
import com.example.ui.theme.NTKPrimaryLight
import com.example.ui.theme.NTKTextPrimary
import com.example.ui.theme.NTKTextSecondary
import com.example.ui.theme.NTKTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onBackToWelcome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Dynamic validations
    val isUsernameValid = username.length in 4..20 && !username.contains(" ")
    val isPasswordValid = password.length >= 8
    val isPasswordsMatch = password == confirmPassword && confirmPassword.isNotEmpty()

    // Determine password strength
    val (strengthText, strengthColor, activeSegments) = remember(password) {
        when {
            password.isEmpty() -> Triple("", Color.Transparent, 0)
            password.length < 6 -> Triple("Rất yếu", Color(0xFFEF4444), 1)
            password.length < 8 -> Triple("Yếu", Color(0xFFF87171), 2)
            password.length < 12 -> Triple("Trung bình", Color(0xFFFBBF24), 3)
            else -> {
                // Check if password has a mix of uppercase, numbers, and symbols
                val hasUpper = password.any { it.isUpperCase() }
                val hasDigit = password.any { it.isDigit() }
                val hasSpecial = password.any { !it.isLetterOrDigit() }
                if (hasUpper && hasDigit && hasSpecial) {
                    Triple("Rất mạnh", Color(0xFF10B981), 4)
                } else {
                    Triple("Mạnh", Color(0xFF34D399), 4)
                }
            }
        }
    }

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
                        text = "Đăng ký tài khoản",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NTKTextPrimary,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tạo tài khoản để bắt đầu hành trình học tập cùng NTK FlashCard",
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
                                colors = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))
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

            // REGISTRATION FORM CARD
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
                                    tint = if (isUsernameValid) NTKPrimary else NTKTextSecondary
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
                                .testTag("username_input")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tên đăng nhập phải từ 4–20 ký tự, không chứa khoảng trắng.",
                            fontSize = 11.sp,
                            color = if (username.isEmpty() || isUsernameValid) NTKTextSecondary else Color(0xFFEF4444),
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
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
                                    tint = if (isPasswordValid) NTKPrimary else NTKTextSecondary
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
                                .testTag("password_input")
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Segmented Password Strength Indicator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mật khẩu phải từ 8 ký tự trở lên",
                                fontSize = 11.sp,
                                color = if (password.isEmpty() || isPasswordValid) NTKTextSecondary else Color(0xFFEF4444)
                            )
                            if (password.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Độ mạnh: ",
                                        fontSize = 11.sp,
                                        color = NTKTextSecondary
                                    )
                                    Text(
                                        text = strengthText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = strengthColor
                                    )
                                }
                            }
                        }

                        // Colored strength bars
                        if (password.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (i in 1..4) {
                                    val barColor = if (i <= activeSegments) strengthColor else Color(0xFFE2E8F0)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(barColor)
                                    )
                                }
                            }
                        }
                    }

                    // FIELD 3: Xác nhận mật khẩu
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Xác nhận mật khẩu",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NTKTextPrimary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = { Text("Nhập lại mật khẩu", fontSize = 14.sp, color = NTKTextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = if (isPasswordsMatch) NTKPrimary else NTKTextSecondary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isConfirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = "Hiển thị mật khẩu",
                                        tint = NTKTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                                .testTag("confirm_password_input")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nhập lại mật khẩu giống với mật khẩu đã nhập.",
                            fontSize = 11.sp,
                            color = if (confirmPassword.isEmpty() || isPasswordsMatch) NTKTextSecondary else Color(0xFFEF4444),
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // PRIMARY REGISTER BUTTON
                    Button(
                        onClick = {
                            if (!isUsernameValid) {
                                Toast.makeText(context, "Tên đăng nhập không hợp lệ!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isPasswordValid) {
                                Toast.makeText(context, "Mật khẩu phải từ 8 ký tự trở lên!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isPasswordsMatch) {
                                Toast.makeText(context, "Mật khẩu nhập lại không trùng khớp!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Toast success
                            Toast.makeText(context, "Đăng ký tài khoản thành công!", Toast.LENGTH_LONG).show()
                            onRegisterSuccess(username)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = NTKPrimary.copy(alpha = 0.4f)
                            )
                            .testTag("register_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NTKPrimary
                        )
                    ) {
                        Text(
                            text = "Đăng ký",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // FOOTER: "Đã có tài khoản? Đăng nhập"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Đã có tài khoản? ",
                    fontSize = 14.sp,
                    color = NTKTextSecondary
                )
                Text(
                    text = "Đăng nhập",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NTKPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .testTag("login_link")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        }
    }
}
