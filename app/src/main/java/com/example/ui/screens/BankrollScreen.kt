package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BotRedPrimary
import com.example.ui.theme.CasinoBlack
import com.example.ui.theme.CasinoCard
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LossRed
import com.example.ui.theme.TieGold

@Composable
fun BankrollScreen(
    onBackClick: () -> Unit
) {
    var bankrollInput by remember { mutableStateOf("10000") }
    var selectedRiskPct by remember { mutableDoubleStateOf(1.0) } // 1% or 2%

    val bankroll = bankrollInput.toDoubleOrNull() ?: 10000.0
    val baseStake = bankroll * (selectedRiskPct / 100.0)
    val tieStake = baseStake * 0.15 // 15% on Empate
    val gale1Stake = baseStake * 2.0
    val gale1TieStake = tieStake * 2.0

    val stopWin10 = bankroll * 1.10
    val stopWin15 = bankroll * 1.15
    val stopLoss = bankroll * 0.90

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CasinoBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
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
                    text = "💰 CALCULADORA DE GESTÃO DE BANCA",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Método Mauro Bot • Disciplina & Gale 1 Seguro",
                    fontSize = 11.sp,
                    color = Color(0xFFA0A5B5)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BANKROLL INPUT CARD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoCard),
            border = BorderStroke(1.dp, CasinoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SUA BANCA TOTAL ATUAL (Kz / R$)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TieGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bankrollInput,
                    onValueChange = { bankrollInput = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenSuccess,
                        unfocusedBorderColor = CasinoCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Perfil de Risco por Entrada:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedRiskPct == 1.0,
                        onClick = { selectedRiskPct = 1.0 },
                        label = { Text("Conservador (1%)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenSuccess,
                            selectedLabelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = selectedRiskPct == 2.0,
                        onClick = { selectedRiskPct = 2.0 },
                        label = { Text("Moderado (2%)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TieGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // STAKE RECOMMENDATIONS CARD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoCard),
            border = BorderStroke(1.dp, CasinoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🎯 RECOMENDAÇÃO DE ENTRADAS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                StakeRow(
                    phase = "1ª Entrada (Sem Gale)",
                    mainStake = "${String.format("%.0f", baseStake)} Kz",
                    tieStake = "${String.format("%.0f", tieStake)} Kz",
                    color = GreenSuccess
                )

                Spacer(modifier = Modifier.height(10.dp))

                StakeRow(
                    phase = "Gale 1 (Se necessário)",
                    mainStake = "${String.format("%.0f", gale1Stake)} Kz",
                    tieStake = "${String.format("%.0f", gale1TieStake)} Kz",
                    color = Color(0xFFFF9100)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TARGET METAS (STOP WIN & STOP LOSS)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoCard),
            border = BorderStroke(1.dp, CasinoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🛡️ LIMITES DIÁRIOS DE SEGURANÇA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GoalBox(
                        title = "Stop Win (+10%)",
                        value = "${String.format("%.0f", stopWin10)} Kz",
                        color = GreenSuccess,
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    GoalBox(
                        title = "Stop Win (+15%)",
                        value = "${String.format("%.0f", stopWin15)} Kz",
                        color = GreenSuccess,
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    GoalBox(
                        title = "Stop Loss (-10%)",
                        value = "${String.format("%.0f", stopLoss)} Kz",
                        color = LossRed,
                        icon = Icons.Default.TrendingDown,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // RULES OF GALE 1
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF191217),
            border = BorderStroke(1.dp, BotRedPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = BotRedPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Regra Absoluta do Gale 1", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "O Mauro Bot opera com no máximo GALE 1. Se a rodada não bater no Gale 1, aceite o loss e preserve a banca para a próxima oportunidade. O mercado é cíclico e a disciplina é o que gera lucro consistente!",
                    fontSize = 11.sp,
                    color = Color(0xFFA0A5B5),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun StakeRow(
    phase: String,
    mainStake: String,
    tieStake: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF11141B),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = phase, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "Proteção Empate: $tieStake", fontSize = 10.sp, color = TieGold)
            }
            Text(text = mainStake, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun GoalBox(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF11141B),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFA0A5B5))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}
