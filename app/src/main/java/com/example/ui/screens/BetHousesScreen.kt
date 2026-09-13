package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BetHouse
import com.example.ui.theme.BotRedGlow
import com.example.ui.theme.BotRedPrimary
import com.example.ui.theme.CasinoBlack
import com.example.ui.theme.CasinoCard
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.TieGold

val PREDEFINED_BET_HOUSES = listOf(
    BetHouse(
        name = "Elephant Bet",
        url = "https://m.elephantbet.co.ao/pt/",
        drawableRes = R.drawable.logo_elephant,
        tagline = "Líder em Apostas e Bac Bo Ao Vivo em Angola",
        bonusInfo = "Bônus de Boas-Vindas + Rodadas Grátis"
    ),
    BetHouse(
        name = "Kwanza bet",
        url = "https://m.kwanzabet.ao/pt/",
        drawableRes = R.drawable.logo_kwanza,
        tagline = "Depósitos rápidos via Multicaixa Express",
        bonusInfo = "Mesas Bac Bo Evolution em tempo real"
    ),
    BetHouse(
        name = "Bantu Bet",
        url = "https://m.bantubet.co.ao/pt/",
        drawableRes = R.drawable.logo_bantu,
        tagline = "A casa dos grandes apostadores em Angola",
        bonusInfo = "Melhores odds e cashback no cassino ao vivo"
    )
)

@Composable
fun BetHousesScreen(
    onBackClick: () -> Unit,
    onSelectBetHouse: (BetHouse) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CasinoBlack)
            .padding(16.dp)
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(CasinoCard, RoundedCornerShape(10.dp))
                    .border(1.dp, CasinoCardBorder, RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "🎰 ESCOLHA SUA CASA DE APOSTAS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Jogue com o Mauro Bot em tempo real",
                    fontSize = 11.sp,
                    color = Color(0xFFA0A5B5)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF17121C),
            border = BorderStroke(1.dp, Color(0xFF332442))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BotRedPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = BotRedGlow,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Integração Direta com Bac Bo Live",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Clique na imagem ou no botão para abrir a casa de apostas integrada.",
                        fontSize = 10.sp,
                        color = Color(0xFFA0A5B5)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LIST OF BET HOUSES
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(PREDEFINED_BET_HOUSES) { house ->
                BetHouseCard(
                    betHouse = house,
                    onCardClick = { onSelectBetHouse(house) },
                    onOpenBrowserClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(house.url))
                        context.startActivity(browserIntent)
                    }
                )
            }
        }
    }
}

@Composable
fun BetHouseCard(
    betHouse: BetHouse,
    onCardClick: () -> Unit,
    onOpenBrowserClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("bet_house_${betHouse.name.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CasinoCard),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(CasinoCardBorder, BotRedPrimary.copy(alpha = 0.6f))
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // House Logo Image
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, CasinoCardBorder, RoundedCornerShape(14.dp))
                        .clickable { onCardClick() }
                ) {
                    Image(
                        painter = painterResource(id = betHouse.drawableRes),
                        contentDescription = betHouse.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = betHouse.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verificada",
                            tint = GreenSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = betHouse.tagline,
                        fontSize = 11.sp,
                        color = Color(0xFFA0A5B5),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF142416),
                        border = BorderStroke(1.dp, GreenSuccess.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🎁 ${betHouse.bonusInfo}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCardClick,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BotRedPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "JOGAR COM O BOT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenBrowserClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CasinoCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = Color(0xFFA0A5B5),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "NAVEGADOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA0A5B5)
                        )
                    }
                }
            }
        }
    }
}
