package com.orlane.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

const val TEMPS_MAX = 15 // secondes par question

data class Question(val texte: String, val choix: List<String>, val bonne: Int)

val questions = listOf(
    Question("Quelle est la capitale officielle du Bénin ?", listOf("Cotonou", "Porto-Novo", "Parakou", "Abomey"), 1),
    Question("En quelle année le Bénin (Dahomey) est-il devenu indépendant ?", listOf("1958", "1960", "1963", "1975"), 1),
    Question("Quelle est la monnaie utilisée au Bénin ?", listOf("Naira", "Cedi", "Franc CFA", "Euro"), 2),
    Question("Quelle est la plus grande planète du système solaire ?", listOf("Saturne", "Terre", "Mars", "Jupiter"), 3),
    Question("Combien font 7 × 8 ?", listOf("54", "56", "58", "64"), 1),
    Question("Quel est le plus grand océan du monde ?", listOf("Atlantique", "Indien", "Pacifique", "Arctique"), 2),
    Question("Qui a peint La Joconde ?", listOf("Picasso", "Léonard de Vinci", "Van Gogh", "Monet"), 1),
    Question("Que signifie H2O ?", listOf("Oxygène", "Hydrogène", "Eau", "Sel"), 2)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Quiz()
                }
            }
        }
    }
}

@Composable
fun Quiz() {
    var index by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var choisi by remember { mutableStateOf<Int?>(null) } // -1 = temps écoulé
    var fini by remember { mutableStateOf(false) }
    var temps by remember { mutableStateOf(TEMPS_MAX) }
    var partie by remember { mutableStateOf(0) }

    LaunchedEffect(index, partie) {
        temps = TEMPS_MAX
        while (temps > 0 && choisi == null) {
            delay(1000)
            if (choisi == null) temps--
        }
        if (choisi == null && !fini) choisi = -1
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (fini) {
            Text("🏆 Quiz terminé !", fontSize = 28.sp)
            Spacer(Modifier.height(16.dp))
            Text("Score : $score / ${questions.size}", fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                when {
                    score == questions.size -> "Parfait, t'es un génie 🔥"
                    score >= questions.size / 2 -> "Pas mal du tout 👏"
                    else -> "Faut réviser un peu 😅"
                },
                fontSize = 18.sp
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = {
                index = 0; score = 0; choisi = null; fini = false; partie++
            }) { Text("Rejouer") }
        } else {
            val q = questions[index]
            Text("Question ${index + 1} / ${questions.size}", fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))

            val couleurChrono = if (temps <= 5) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
            Text("⏱️ $temps s", fontSize = 20.sp, color = couleurChrono)
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { temps / TEMPS_MAX.toFloat() },
                color = couleurChrono,
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            Spacer(Modifier.height(20.dp))

            Text(q.texte, fontSize = 22.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))

            q.choix.forEachIndexed { i, reponse ->
                val couleur = when {
                    choisi == null -> MaterialTheme.colorScheme.primary
                    i == q.bonne -> Color(0xFF2E7D32)
                    i == choisi -> Color(0xFFC62828)
                    else -> Color.Gray
                }
                Button(
                    onClick = {
                        if (choisi == null) {
                            choisi = i
                            if (i == q.bonne) score++
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = couleur),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) { Text(reponse, fontSize = 18.sp) }
            }

            Spacer(Modifier.height(16.dp))
            if (choisi == -1) {
                Text("⏰ Temps écoulé !", fontSize = 18.sp, color = Color(0xFFC62828))
                Spacer(Modifier.height(8.dp))
            }
            if (choisi != null) {
                Button(onClick = {
                    if (index + 1 < questions.size) {
                        index++; choisi = null
                    } else {
                        fini = true
                    }
                }) { Text(if (index + 1 < questions.size) "Suivant ➡️" else "Voir mon score") }
            }
        }
    }
}
