package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccessKeyEntity
import com.example.data.model.EbookEntity
import com.example.data.model.ScoreboardEntity
import com.example.data.model.StrategyEntity
import com.example.ui.theme.BankerRed
import com.example.ui.theme.BotRedGlow
import com.example.ui.theme.BotRedPrimary
import com.example.ui.theme.CasinoBlack
import com.example.ui.theme.CasinoCard
import com.example.ui.theme.CasinoCardBorder
import com.example.ui.theme.CasinoDarkSurface
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.LossRed
import com.example.ui.theme.PlayerBlue
import com.example.ui.theme.TieGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanelScreen(
    strategies: List<StrategyEntity>,
    accessKeys: List<AccessKeyEntity>,
    scoreboard: ScoreboardEntity,
    ebooks: List<EbookEntity>,
    onBackClick: () -> Unit,
    onAddStrategy: (String, String, String, Int) -> Unit,
    onToggleStrategy: (StrategyEntity) -> Unit,
    onDeleteStrategy: (StrategyEntity) -> Unit,
    onGenerateKey: (String?, Int) -> Unit,
    onDeleteKey: (AccessKeyEntity) -> Unit,
    onUpdateScoreboard: (Int, Int, Int, Int, Int) -> Unit,
    onResetScoreboard: () -> Unit,
    onAddEbook: (String, String, String, String, String) -> Unit,
    onDeleteEbook: (EbookEntity) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "🔑 Chaves",
        "📊 Gráfico & Estratégias",
        "🏆 Placar",
        "📚 E-books"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CasinoBlack)
    ) {
        // ADMIN HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    text = "👑 PAINEL ADMINISTRATIVO",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = TieGold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Gerenciamento completo do Mauro Bot",
                    fontSize = 11.sp,
                    color = Color(0xFFA0A5B5)
                )
            }
        }

        // TAB BAR
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = CasinoDarkSurface,
            contentColor = BotRedPrimary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = BotRedPrimary,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color.White else Color(0xFFA0A5B5)
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> KeysAdminTab(
                    accessKeys = accessKeys,
                    onGenerateKey = onGenerateKey,
                    onDeleteKey = onDeleteKey
                )
                1 -> StrategiesAdminTab(
                    strategies = strategies,
                    onAddStrategy = onAddStrategy,
                    onToggleStrategy = onToggleStrategy,
                    onDeleteStrategy = onDeleteStrategy
                )
                2 -> ScoreboardAdminTab(
                    scoreboard = scoreboard,
                    onUpdateScoreboard = onUpdateScoreboard,
                    onResetScoreboard = onResetScoreboard
                )
                3 -> EbooksAdminTab(
                    ebooks = ebooks,
                    onAddEbook = onAddEbook,
                    onDeleteEbook = onDeleteEbook
                )
            }
        }
    }
}

// ======================== TAB 1: KEYS ========================
@Composable
fun KeysAdminTab(
    accessKeys: List<AccessKeyEntity>,
    onGenerateKey: (String?, Int) -> Unit,
    onDeleteKey: (AccessKeyEntity) -> Unit
) {
    val context = LocalContext.current
    var customKeyName by remember { mutableStateOf("") }
    var selectedDurationDays by remember { mutableIntStateOf(-1) } // -1 = vitalício

    val durationOptions = listOf(
        Pair("Vitalício", -1),
        Pair("1 Dia", 1),
        Pair("7 Dias", 7),
        Pair("15 Dias", 15),
        Pair("30 Dias", 30),
        Pair("1 Ano", 365)
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoCard),
                border = BorderStroke(1.dp, CasinoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "➕ GERAR NOVA CHAVE DE ACESSO",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TieGold
                    )
                    Text(
                        text = "A chave permite login em múltiplos celulares simultâneos (>20 usuários).",
                        fontSize = 11.sp,
                        color = Color(0xFFA0A5B5),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = customKeyName,
                        onValueChange = { customKeyName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nome da chave (opcional, ex: VIP-MARCO)", fontSize = 13.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BotRedPrimary,
                            unfocusedBorderColor = CasinoCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Validade do Acesso:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        durationOptions.take(3).forEach { (label, days) ->
                            FilterChip(
                                selected = selectedDurationDays == days,
                                onClick = { selectedDurationDays = days },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BotRedPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        durationOptions.drop(3).forEach { (label, days) ->
                            FilterChip(
                                selected = selectedDurationDays == days,
                                onClick = { selectedDurationDays = days },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BotRedPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            onGenerateKey(customKeyName.ifBlank { null }, selectedDurationDays)
                            customKeyName = ""
                            Toast.makeText(context, "Chave gerada com sucesso!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BotRedPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CRIAR CHAVE DE ACESSO", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "CHAVES ATIVAS (${accessKeys.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA0A5B5)
            )
        }

        items(accessKeys) { key ->
            val isExpired = key.isExpired()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val expiresText = if (key.durationDays < 0) "Vitalício (Sem prazo)" else "Expira em: ${dateFormat.format(Date(key.expiresAt))}"

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoCard),
                border = BorderStroke(1.dp, if (isExpired) LossRed.copy(alpha = 0.5f) else CasinoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isExpired) LossRed.copy(alpha = 0.15f) else GreenSuccess.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = if (isExpired) LossRed else GreenSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = key.key,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = expiresText,
                            fontSize = 11.sp,
                            color = if (isExpired) LossRed else Color(0xFFA0A5B5)
                        )
                        Text(
                            text = key.note,
                            fontSize = 10.sp,
                            color = GreenSuccess
                        )
                    }

                    // Copy Key
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Chave Bot", key.key))
                            Toast.makeText(context, "Chave copiada: ${key.key}", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar",
                            tint = TieGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete / Revoke Key
                    IconButton(
                        onClick = { onDeleteKey(key) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = LossRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ======================== TAB 2: STRATEGIES & CHART ========================
@Composable
fun StrategiesAdminTab(
    strategies: List<StrategyEntity>,
    onAddStrategy: (String, String, String, Int) -> Unit,
    onToggleStrategy: (StrategyEntity) -> Unit,
    onDeleteStrategy: (StrategyEntity) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var sequence by remember { mutableStateOf("B, B") }
    var recommendation by remember { mutableStateOf("BANKER") }
    var confidence by remember { mutableIntStateOf(95) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoCard),
                border = BorderStroke(1.dp, CasinoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📈 ADICIONAR ESTRATÉGIA AO GRÁFICO",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TieGold
                    )
                    Text(
                        text = "Os sinais automáticos do robô são gerados a partir destas estratégias. Se nenhuma estiver ativa, o robô fica em '🔎 Analisando o Jogo'.",
                        fontSize = 11.sp,
                        color = Color(0xFFA0A5B5),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nome da Estratégia (ex: Surfe Duplo)", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BotRedPrimary,
                            unfocusedBorderColor = CasinoCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = sequence,
                        onValueChange = { sequence = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Sequência de Gatilho (ex: B, B ou P, P, P)", fontSize = 13.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BotRedPrimary,
                            unfocusedBorderColor = CasinoCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Entrada Recomendada:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { recommendation = "BANKER" }
                        ) {
                            RadioButton(
                                selected = recommendation == "BANKER",
                                onClick = { recommendation = "BANKER" },
                                colors = RadioButtonDefaults.colors(selectedColor = BankerRed)
                            )
                            Text("BANKER (Banca)", color = BankerRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { recommendation = "PLAYER" }
                        ) {
                            RadioButton(
                                selected = recommendation == "PLAYER",
                                onClick = { recommendation = "PLAYER" },
                                colors = RadioButtonDefaults.colors(selectedColor = PlayerBlue)
                            )
                            Text("PLAYER (Jogador)", color = PlayerBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assertividade Estimada: $confidence%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && sequence.isNotBlank()) {
                                onAddStrategy(name, sequence, recommendation, confidence)
                                name = ""
                                Toast.makeText(context, "Estratégia adicionada!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BotRedPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SALVAR ESTRATÉGIA NO GRÁFICO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "ESTRATÉGIAS ATIVAS NO GRÁFICO (${strategies.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA0A5B5)
            )
        }

        items(strategies) { strat ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoCard),
                border = BorderStroke(1.dp, if (strat.isActive) BotRedPrimary.copy(alpha = 0.5f) else CasinoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = strat.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (strat.recommendation == "BANKER") BankerRed.copy(alpha = 0.2f) else PlayerBlue.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = strat.recommendation,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (strat.recommendation == "BANKER") BankerRed else PlayerBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Gatilho: [ ${strat.sequence} ]  •  Confiança: ${strat.confidence}%",
                            fontSize = 11.sp,
                            color = Color(0xFFA0A5B5)
                        )
                    }

                    Switch(
                        checked = strat.isActive,
                        onCheckedChange = { onToggleStrategy(strat) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BotRedPrimary,
                            checkedTrackColor = BotRedPrimary.copy(alpha = 0.3f)
                        )
                    )

                    IconButton(onClick = { onDeleteStrategy(strat) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = LossRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ======================== TAB 3: SCOREBOARD (EDITAR & ZERAR) ========================
@Composable
fun ScoreboardAdminTab(
    scoreboard: ScoreboardEntity,
    onUpdateScoreboard: (Int, Int, Int, Int, Int) -> Unit,
    onResetScoreboard: () -> Unit
) {
    val context = LocalContext.current
    var greens by remember(scoreboard) { mutableStateOf(scoreboard.greens.toString()) }
    var losses by remember(scoreboard) { mutableStateOf(scoreboard.losses.toString()) }
    var empates by remember(scoreboard) { mutableStateOf(scoreboard.empates.toString()) }
    var streak by remember(scoreboard) { mutableStateOf(scoreboard.currentStreak.toString()) }
    var maxStreak by remember(scoreboard) { mutableStateOf(scoreboard.maxConsecutiveGreens.toString()) }

    var showConfirmResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CasinoCard),
            border = BorderStroke(1.dp, CasinoCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏆 EDITAR PLACAR MANUALMENTE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TieGold
                )
                Text(
                    text = "Altere os valores de Green, Loss, Empates e Sequências a qualquer momento.",
                    fontSize = 11.sp,
                    color = Color(0xFFA0A5B5),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ScoreEditField(label = "Vitórias (GREEN)", value = greens, onValueChange = { greens = it }, color = GreenSuccess)
                ScoreEditField(label = "Derrotas (LOSS)", value = losses, onValueChange = { losses = it }, color = LossRed)
                ScoreEditField(label = "Empates (EMPATE)", value = empates, onValueChange = { empates = it }, color = TieGold)
                ScoreEditField(label = "Sequência Atual de Greens", value = streak, onValueChange = { streak = it }, color = Color(0xFFFF9100))
                ScoreEditField(label = "Recorde Máximo de Greens Seguidos", value = maxStreak, onValueChange = { maxStreak = it }, color = GreenSuccess)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val g = greens.toIntOrNull() ?: scoreboard.greens
                        val l = losses.toIntOrNull() ?: scoreboard.losses
                        val e = empates.toIntOrNull() ?: scoreboard.empates
                        val s = streak.toIntOrNull() ?: scoreboard.currentStreak
                        val m = maxStreak.toIntOrNull() ?: scoreboard.maxConsecutiveGreens
                        onUpdateScoreboard(g, l, e, s, m)
                        Toast.makeText(context, "Placar atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SALVAR ALTERAÇÕES DO PLACAR", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ZERAR PLACAR BUTTON
                OutlinedButton(
                    onClick = { showConfirmResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reset_scoreboard_button"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.5.dp, LossRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LossRed)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = LossRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ZERAR PLACAR COMPLETO (0 - 0)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LossRed)
                }
            }
        }
    }

    if (showConfirmResetDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmResetDialog = false },
            containerColor = CasinoCard,
            title = { Text("Zerar Placar?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Esta ação resetará todas as vitórias, derrotas, empates e sequências para zero (0). Deseja continuar?",
                    color = Color(0xFFA0A5B5),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetScoreboard()
                        greens = "0"
                        losses = "0"
                        empates = "0"
                        streak = "0"
                        maxStreak = "0"
                        showConfirmResetDialog = false
                        Toast.makeText(context, "Placar zerado com sucesso!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LossRed)
                ) {
                    Text("SIM, ZERAR AGORA", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmResetDialog = false }) {
                    Text("CANCELAR", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun ScoreEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    color: Color
) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = color)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = CasinoCardBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

// ======================== TAB 4: E-BOOKS ========================
@Composable
fun EbooksAdminTab(
    ebooks: List<EbookEntity>,
    onAddEbook: (String, String, String, String, String) -> Unit,
    onDeleteEbook: (EbookEntity) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Gestão de Banca") }
    var readTime by remember { mutableStateOf("5 min") }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoCard),
                border = BorderStroke(1.dp, CasinoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📚 PUBLICAR NOVO E-BOOK / GUIA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TieGold
                    )
                    Text(
                        text = "Cadastre materiais sobre gestão de banca, regras e estratégias para os alunos.",
                        fontSize = 11.sp,
                        color = Color(0xFFA0A5B5),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Título do E-book", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BotRedPrimary,
                            unfocusedBorderColor = CasinoCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Categoria", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BotRedPrimary,
                                unfocusedBorderColor = CasinoCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = readTime,
                            onValueChange = { readTime = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Tempo (ex: 5 min)", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BotRedPrimary,
                                unfocusedBorderColor = CasinoCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Resumo breve do material", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BotRedPrimary,
                            unfocusedBorderColor = CasinoCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        placeholder = { Text("Conteúdo completo do e-book...", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BotRedPrimary,
                            unfocusedBorderColor = CasinoCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onAddEbook(title, category, readTime, summary, content)
                                title = ""
                                summary = ""
                                content = ""
                                Toast.makeText(context, "E-book publicado com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BotRedPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PUBLICAR E-BOOK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "E-BOOKS CADASTRADOS (${ebooks.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA0A5B5)
            )
        }

        items(ebooks) { ebook ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CasinoCard),
                border = BorderStroke(1.dp, CasinoCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(TieGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = TieGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ebook.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${ebook.category}  •  ${ebook.readTime}",
                            fontSize = 10.sp,
                            color = Color(0xFFA0A5B5)
                        )
                    }

                    IconButton(onClick = { onDeleteEbook(ebook) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = LossRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
