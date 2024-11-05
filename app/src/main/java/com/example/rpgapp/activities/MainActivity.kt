package com.example.rpgapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.rpgapp.R
import com.example.rpgapp.data.database.AppDatabase
import com.example.rpgapp.data.entities.Personagem
import com.example.rpgapp.data.entities.Atributos
import com.example.rpgapp.logic.AtributosHelper
import com.example.rpgapp.logic.PersonagemManager
import com.example.rpgapp.models.Raca
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var personagemManager: PersonagemManager
    private lateinit var buttonCriarPersonagem: Button
    private lateinit var buttonVisualizarPersonagem: Button
    private lateinit var pontosRestantesTextView: TextView
    private lateinit var textBonusRace: TextView
    private var racaEscolhida: Raca = Raca.Humanos
    private val database by lazy { AppDatabase.getDatabase(this) }
    private var personagemId: Int = -1
    private var atributosId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        solicitarPermissaoNotificacao()

        val atributosHelper = AtributosHelper()
        personagemManager = PersonagemManager(atributosHelper)

        val forcaInput: EditText = findViewById(R.id.edit_forca)
        val destrezaInput: EditText = findViewById(R.id.edit_destreza)
        val constituicaoInput: EditText = findViewById(R.id.edit_constituicao)
        val inteligenciaInput: EditText = findViewById(R.id.edit_inteligencia)
        val sabedoriaInput: EditText = findViewById(R.id.edit_sabedoria)
        val carismaInput: EditText = findViewById(R.id.edit_carisma)

        pontosRestantesTextView = findViewById(R.id.text_pontos_restantes)
        textBonusRace = findViewById(R.id.text_bonus_race)
        buttonCriarPersonagem = findViewById(R.id.button_create_character)
        buttonVisualizarPersonagem = findViewById(R.id.button_visualizar_personagem)

        atualizarPontosRestantes()
        adicionarTextWatcher(
            forcaInput,
            destrezaInput,
            constituicaoInput,
            inteligenciaInput,
            sabedoriaInput,
            carismaInput
        )

        val spinnerRaca: Spinner = findViewById(R.id.spinner_race)
        configurarSpinnerRacas(spinnerRaca)

        buttonCriarPersonagem.setOnClickListener {
            enviarNotificacao()
            val atributosDistribuidos = personagemManager.distribuirAtributos(
                forcaInput.text.toString().toIntOrNull() ?: 0,
                destrezaInput.text.toString().toIntOrNull() ?: 0,
                constituicaoInput.text.toString().toIntOrNull() ?: 0,
                inteligenciaInput.text.toString().toIntOrNull() ?: 0,
                sabedoriaInput.text.toString().toIntOrNull() ?: 0,
                carismaInput.text.toString().toIntOrNull() ?: 0
            )
            personagemManager.aplicarBonusRacial(racaEscolhida, atributosDistribuidos)
            personagemManager.calcularEAtribuirModificadores(atributosDistribuidos)

            lifecycleScope.launch {
                atributosId = salvarAtributos(atributosDistribuidos)
                val personagem = Personagem(
                    nome = "Novo Personagem",
                    raca = racaEscolhida.toString(),
                    atributosId = atributosId
                )
                personagemId = salvarPersonagem(personagem)
            }
        }

        buttonVisualizarPersonagem.setOnClickListener {
            lifecycleScope.launch {
                val personagemExiste = withContext(Dispatchers.IO) {
                    database.personagemDAO().getPersonagemById(personagemId) != null
                }

                if (personagemExiste) {
                    abrirPersonagemActivity(personagemId, atributosId)
                } else {
                    Toast.makeText(this@MainActivity, "Personagem não encontrado. Ele pode ter sido excluído.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun abrirPersonagemActivity(personagemId: Int, atributosId: Int) {
        val intent = Intent(this, PersonagemActivity::class.java).apply {
            putExtra("personagemId", personagemId)
            putExtra("atributosId", atributosId)
        }
        startActivity(intent)
    }

    private fun solicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    private fun enviarNotificacao() {
        val name = "Canal de Notificações"
        val descriptionText = "Descrição da notificação"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("canal_id", name, importance).apply {
            description = descriptionText
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(this, "canal_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Dungeons and Dragons")
            .setContentText("Crie seu personagem!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        notificationManager.notify(1, builder.build())
    }

    private fun configurarSpinnerRacas(spinner: Spinner) {
        val racas = listOf(
            Raca.Humanos, Raca.Anoes, Raca.AltoElfo, Raca.Drow, Raca.Draconato,
            Raca.ElfoDaFloresta, Raca.GnomoDasRochas, Raca.HalflingPésLeves,
            Raca.MeioOrc, Raca.Tiefling, Raca.AnãoDaColina, Raca.AnãoDaMontanha,
            Raca.Gnomo, Raca.GnomoDaFloresta, Raca.HalflingRobusto, Raca.Halflings, Raca.MeioElfo
        )
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, racas.map { it.toString() })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                racaEscolhida = racas[position]
                mostrarBonusRaca(racaEscolhida)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun mostrarBonusRaca(raca: Raca) {
        val bonusTexto = when (raca) {
            Raca.Humanos -> "+1 em todos os atributos (Força, Destreza, Constituição, Inteligência, Sabedoria, Carisma)"
            Raca.Anoes -> "+2 em Constituição"
            Raca.AnãoDaColina -> "+2 em Constituição, +1 em Sabedoria"
            Raca.AnãoDaMontanha -> "+2 em Constituição, +2 em Força"
            Raca.AltoElfo -> "+2 em Destreza, +1 em Inteligência"
            Raca.Drow -> "+2 em Destreza, +1 em Carisma"
            Raca.Draconato -> "+2 em Força, +1 em Carisma"
            Raca.ElfoDaFloresta -> "+2 em Destreza, +1 em Sabedoria"
            Raca.GnomoDasRochas -> "+2 em Inteligência, +1 em Constituição"
            Raca.Gnomo -> "+2 em Inteligência"
            Raca.GnomoDaFloresta -> "+2 em Inteligência, +1 em Destreza"
            Raca.HalflingPésLeves -> "+2 em Destreza, +1 em Carisma"
            Raca.HalflingRobusto -> "+2 em Destreza, +1 em Constituição"
            Raca.Halflings -> "+2 em Destreza"
            Raca.MeioOrc -> "+2 em Força, +1 em Constituição"
            Raca.MeioElfo -> "+2 em Carisma, +1 em dois outros atributos à escolha"
            Raca.Tiefling -> "+1 em Inteligência, +2 em Carisma"
        }
        textBonusRace.text = "Bônus: $bonusTexto"
    }

    private suspend fun salvarAtributos(atributos: com.example.rpgapp.models.Atributos): Int {
        val atributosEntity = Atributos(
            forca = atributos.forca,
            destreza = atributos.destreza,
            constituicao = atributos.constituicao,
            inteligencia = atributos.inteligencia,
            sabedoria = atributos.sabedoria,
            carisma = atributos.carisma
        )
        return database.atributosDAO().insert(atributosEntity).toInt()
    }

    private suspend fun salvarPersonagem(personagem: Personagem): Int {
        return withContext(Dispatchers.IO) {
            database.personagemDAO().insert(personagem).toInt()
        }
    }


    private fun adicionarTextWatcher(vararg editTexts: EditText) {
        for (editText in editTexts) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val value = s.toString().toIntOrNull() ?: 0
                    if (value > 7) {
                        editText.setText("7")
                        editText.setSelection(editText.text.length)
                        Toast.makeText(this@MainActivity, "O valor máximo é 7", Toast.LENGTH_SHORT).show()
                    }
                    atualizarPontosRestantes()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun atualizarPontosRestantes() {
        val forca = findViewById<EditText>(R.id.edit_forca).text.toString().toIntOrNull() ?: 0
        val destreza = findViewById<EditText>(R.id.edit_destreza).text.toString().toIntOrNull() ?: 0
        val constituicao = findViewById<EditText>(R.id.edit_constituicao).text.toString().toIntOrNull() ?: 0
        val inteligencia = findViewById<EditText>(R.id.edit_inteligencia).text.toString().toIntOrNull() ?: 0
        val sabedoria = findViewById<EditText>(R.id.edit_sabedoria).text.toString().toIntOrNull() ?: 0
        val carisma = findViewById<EditText>(R.id.edit_carisma).text.toString().toIntOrNull() ?: 0

        val atributos = mapOf(
            "forca" to forca,
            "destreza" to destreza,
            "constituicao" to constituicao,
            "inteligencia" to inteligencia,
            "sabedoria" to sabedoria,
            "carisma" to carisma
        )

        val totalCusto = personagemManager.atributosHelper.calcularCustoTotal(atributos)

        val pontosRestantes = 27 - totalCusto
        pontosRestantesTextView.text = "Pontos restantes: $pontosRestantes"
        buttonCriarPersonagem.isEnabled = pontosRestantes >= 0
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1
    }
}
