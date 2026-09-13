package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.ActiveSignal
import com.example.data.model.BetHouse
import com.example.data.model.GalePhase
import com.example.data.model.SignalTarget
import com.example.ui.theme.BankerRed
import com.example.ui.theme.BotRedGlow
import com.example.ui.theme.BotRedPrimary
import com.example.ui.theme.CasinoBlack
import com.example.ui.theme.CasinoCard
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.PlayerBlue
import com.example.ui.theme.TieGold

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewBetScreen(
    betHouse: BetHouse,
    activeSignal: ActiveSignal?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var webProgress by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CasinoBlack)
    ) {
        // TOP CONTROLS & MINI LIVE SIGNAL OVERLAY
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CasinoCard,
            border = BorderStroke(1.dp, CasinoCardBorder)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF161821), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = betHouse.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Mesa Bac Bo Ao Vivo",
                                fontSize = 10.sp,
                                color = Color(0xFFA0A5B5)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { webViewInstance?.reload() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Recarregar",
                                tint = Color(0xFFA0A5B5),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(betHouse.url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Abrir fora",
                                tint = Color(0xFFA0A5B5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // FLOATING LIVE BOT BANNER (ROBÔ TRANSMITE SINAIS ENQUANTO VOCÊ JOGA)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeSignal != null) Color(0xFF2B0A11) else Color(0xFF11131A),
                    border = BorderStroke(
                        1.dp,
                        if (activeSignal != null) BotRedPrimary else Color(0xFF282C3D)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (activeSignal != null) BotRedGlow else GreenSuccess)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (activeSignal != null) "🚨 SINAL ATIVO:" else "🤖 MAURO BOT:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeSignal != null) BotRedGlow else Color(0xFFA0A5B5)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            if (activeSignal != null) {
                                val targetName = if (activeSignal.target == SignalTarget.BANKER) "BANKER" else "PLAYER"
                                val targetColor = if (activeSignal.target == SignalTarget.BANKER) BankerRed else PlayerBlue
                                Text(
                                    text = "$targetName + EMPATE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = targetColor
                                )
                            } else {
                                Text(
                                    text = "Analisando rodadas...",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }

                        if (activeSignal != null) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (activeSignal.phase == GalePhase.GALE_1) Color(0xFFFF9800) else GreenSuccess
                            ) {
                                Text(
                                    text = if (activeSignal.phase == GalePhase.GALE_1) "GALE 1" else "SEM GALE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (webProgress in 1..99) {
            LinearProgressIndicator(
                progress = { webProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp),
                color = BotRedPrimary,
                trackColor = Color.Transparent
            )
        }

        // ANDROID WEBVIEW
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                    }
                    webViewClient = object : WebViewClient() {}
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            webProgress = newProgress
                        }
                    }
                    loadUrl(betHouse.url)
                    webViewInstance = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
