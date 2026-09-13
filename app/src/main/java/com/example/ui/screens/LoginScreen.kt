package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LoginButtonBlack
import com.example.ui.theme.LoginGreen
import com.example.ui.theme.LoginGreenLight
import com.example.ui.theme.LoginYellow
import com.example.ui.theme.LoginYellowLight

@Composable
fun LoginScreen(
    authError: String?,
    onLoginClick: (String) -> Unit
) {
    var keyInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Login screen theme: Yellow and Green gradients with stylish black button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F140F),
                        Color(0xFF131D12),
                        Color(0xFF1B2616),
                        Color(0xFF0D0F0B)
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Mascot Header with glowing Yellow and Green borders
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .scale(pulseScale)
            ) {
                // Outer Glow ring
                Box(
                    modifier = Modifier
                        .size(126.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.sweepGradient(
                                listOf(
                                    LoginYellow,
                                    LoginGreen,
                                    LoginYellowLight,
                                    LoginGreenLight,
                                    LoginYellow
                                )
                            )
                        )
                )
                // Mascot image
                Image(
                    painter = painterResource(id = R.drawable.bot_mascot),
                    contentDescription = "Mauro Bot Mascot",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(116.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFF0A0C09), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Title in Yellow and Green
            Text(
                text = "🤖 MAURO BOT BAC BO LIVE",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LoginYellow,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )

            Text(
                text = "INTELIGÊNCIA ARTIFICIAL • SINAIS EM TEMPO REAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = LoginGreenLight,
                textAlign = TextAlign.Center,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Key Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF121712)
                ),
                border = BorderStroke(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(
                        listOf(LoginGreen, LoginYellow)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Chave",
                            tint = LoginYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CHAVE DE ACESSO VIP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LoginYellow
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("key_input"),
                        placeholder = {
                            Text(
                                "Insira sua Chave ou Senha",
                                color = Color(0xFF7A8478),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = LoginGreen
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPasswordVisible) "Ocultar" else "Mostrar",
                                    tint = LoginYellow
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (keyInput.isNotBlank()) onLoginClick(keyInput)
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LoginYellow,
                            unfocusedBorderColor = Color(0xFF2A3A27),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = LoginYellow,
                            focusedContainerColor = Color(0xFF090D09),
                            unfocusedContainerColor = Color(0xFF090D09)
                        )
                    )

                    if (!authError.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = authError,
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stylish Black Button with Yellow/Green border and glowing effects
                    Button(
                        onClick = {
                            if (keyInput.isNotBlank()) {
                                onLoginClick(keyInput)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("login_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LoginButtonBlack
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(
                                listOf(LoginYellow, LoginGreen)
                            )
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🚀 ACESSAR O ROBÔ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = LoginYellow,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Multi-device and Support Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F150E)
                ),
                border = BorderStroke(1.dp, Color(0xFF223120))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = LoginGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Acesso Multi-Dispositivo Autorizado",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Uma chave permite mais de 20 usuários conectados simultaneamente em diferentes celulares.",
                            fontSize = 11.sp,
                            color = Color(0xFF98A696),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
