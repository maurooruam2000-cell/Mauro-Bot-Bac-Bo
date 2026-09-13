package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AccessKeyEntity
import com.example.data.model.EbookEntity
import com.example.data.model.ScoreboardEntity
import com.example.data.model.StrategyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StrategyEntity::class,
        AccessKeyEntity::class,
        ScoreboardEntity::class,
        EbookEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun strategyDao(): StrategyDao
    abstract fun accessKeyDao(): AccessKeyDao
    abstract fun scoreboardDao(): ScoreboardDao
    abstract fun ebookDao(): EbookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mauro_bot_bacbo.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            populateInitialData(getDatabase(context))
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Seed default strategies
            if (database.strategyDao().count() == 0) {
                database.strategyDao().insert(
                    StrategyEntity(
                        name = "Surfe de Tendência Banker",
                        sequence = "B, B",
                        recommendation = "BANKER",
                        confidence = 96,
                        isActive = true
                    )
                )
                database.strategyDao().insert(
                    StrategyEntity(
                        name = "Surfe de Tendência Jogador",
                        sequence = "P, P",
                        recommendation = "PLAYER",
                        confidence = 94,
                        isActive = true
                    )
                )
                database.strategyDao().insert(
                    StrategyEntity(
                        name = "Quebra de Sequência Tripla",
                        sequence = "P, P, P",
                        recommendation = "BANKER",
                        confidence = 97,
                        isActive = true
                    )
                )
                database.strategyDao().insert(
                    StrategyEntity(
                        name = "Alternância de Padrão",
                        sequence = "B, P, B",
                        recommendation = "PLAYER",
                        confidence = 93,
                        isActive = true
                    )
                )
            }

            // Seed default keys
            if (database.accessKeyDao().count() == 0) {
                database.accessKeyDao().insert(
                    AccessKeyEntity(
                        key = "MAURO-VIP-2026",
                        durationDays = -1,
                        note = "Chave Master VIP Vitalícia (Liberada para múltiplos aparelhos)"
                    )
                )
                database.accessKeyDao().insert(
                    AccessKeyEntity(
                        key = "BACBO-PRO-AO",
                        durationDays = 30,
                        note = "Chave Mensal Premium 30 Dias"
                    )
                )
            }

            // Seed scoreboard
            if (database.scoreboardDao().getScoreboard() == null) {
                database.scoreboardDao().insertOrUpdate(
                    ScoreboardEntity(
                        id = 1,
                        greens = 148,
                        losses = 3,
                        empates = 19,
                        currentStreak = 12,
                        maxConsecutiveGreens = 28
                    )
                )
            }

            // Seed default ebooks
            if (database.ebookDao().count() == 0) {
                database.ebookDao().insert(
                    EbookEntity(
                        title = "Manual Supremo do Bac Bo Live",
                        category = "Estratégia Básica",
                        readTime = "5 min",
                        summary = "Entenda a mecânica dos dados, a vantagem matemática da banca e os ciclos de pagamento.",
                        content = """
                            CAPÍTULO 1: O QUE É O BAC BO LIVE?
                            O Bac Bo é uma das atrações mais dinâmicas dos cassinos ao vivo da Evolution. Diferente do Baccarat tradicional com cartas, no Bac Bo cada lado (Jogador e Banca) possui dois copos vibradores onde 2 dados são agitados.
                            
                            CAPÍTULO 2: VALORES E PONTUAÇÃO
                            - Cada dado varia de 1 a 6.
                            - A pontuação final de cada lado é a SOMA dos 2 dados (varia de 2 a 12).
                            - Vence o lado com a maior soma!
                            
                            CAPÍTULO 3: A VANTAGEM DO BANQUEIRO
                            Estatisticamente, a aposta no Banqueiro (Banker) possui uma ligeira vantagem matemática. Porém, o verdadeiro segredo dos profissionais está na leitura de tendências consecutivas (surfe) e na proteção no empate.
                            
                            CAPÍTULO 4: DICAS DE OURO DO MAURO BOT
                            1. Nunca entre sem confirmação do sinal.
                            2. Sempre cubra o Empate com 10% da aposta principal.
                            3. Use no máximo GALE 1! Se não bater no Gale 1, aceite o loss e espere o próximo ciclo.
                        """.trimIndent()
                    )
                )
                database.ebookDao().insert(
                    EbookEntity(
                        title = "Gestão de Banca Milionária & Gale 1",
                        category = "Gestão de Risco",
                        readTime = "8 min",
                        summary = "Como preservar seu capital, calcular o Gale 1 e aplicar Stop Win e Stop Loss rigorosos.",
                        content = """
                            REGRA Nº 1: A DIVISÃO DA SUA BANCA
                            Divida sua banca total em pelo menos 50 a 100 unidades de aposta.
                            Exemplo com Banca de 10.000 Kz:
                            - Aposta Base (1 unidade): 100 Kz a 200 Kz (1% a 2%)
                            - Proteção no Empate: 20 Kz a 40 Kz (10% a 20% da base)
                            
                            COMO APLICAR O GALE 1 CORRETAMENTE:
                            - Se a 1ª entrada (Sem Gale) não vencer:
                              Você dobra a aposta principal para a próxima rodada (2x) e mantém a proporção do empate.
                            - NUNCA use Gale 2 ou Gale 3! O método Mauro Bot é calibrado para bater na primeira ou no Gale 1 com mais de 96% de assertividade.
                            
                            METAS DIÁRIAS (STOP WIN & STOP LOSS):
                            - Stop Win: Ao atingir 10% a 15% de lucro diário sobre a banca, PARE IMEDIATAMENTE!
                            - Stop Loss: Se perder 3 sinais seguidos ou atingir -10%, encerre o dia. O mercado continuará amanhã!
                        """.trimIndent()
                    )
                )
                database.ebookDao().insert(
                    EbookEntity(
                        title = "O Segredo da Proteção no Empate (Tie 88x)",
                        category = "Multiplicadores",
                        readTime = "4 min",
                        summary = "Aprenda por que o Empate é contado como Green e como alavancar lucros exponenciais.",
                        content = """
                            POR QUE O EMPATE É TÃO PODEROSO NO BAC BO?
                            No Bac Bo, o empate paga no mínimo 4:1 e pode pagar até impressionantes 88:1 dependendo da pontuação dos dados:
                            
                            TABELA DE MULTIPLICADORES DO EMPATE:
                            - Empate em 2 ou 12: Paga 88x!
                            - Empate em 3 ou 11: Paga 25x!
                            - Empate em 4 ou 10: Paga 10x!
                            - Empate em 5 ou 9: Paga 6x!
                            - Empate em 6, 7 ou 8: Paga 4x!
                            
                            COMO O MAURO BOT TRABALHA COM O EMPATE:
                            Toda entrada recomendada é:
                            "ENTRADA CONFIRMADA: BANKER + EMPATE" ou "PLAYER + EMPATE".
                            Ao cobrir o empate, mesmo que o lado principal não vença, o empate recupera o valor apostado e gera lucro astronômico, sendo comemorado como GREEN NO EMPATE!
                        """.trimIndent()
                    )
                )
            }
        }
    }
}
