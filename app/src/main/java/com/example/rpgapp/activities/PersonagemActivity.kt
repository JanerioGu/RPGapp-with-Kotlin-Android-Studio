package com.example.rpgapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.rpgapp.R
import com.example.rpgapp.data.database.AppDatabase
import com.example.rpgapp.data.entities.Atributos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonagemActivity : ComponentActivity() {

    private lateinit var textAtributos: TextView
    private lateinit var buttonAtacar: Button
    private lateinit var buttonExibirVida: Button
    private lateinit var buttonAtualizar: Button
    private lateinit var buttonDeletar: Button
    private val database by lazy { AppDatabase.getDatabase(this) }
    private var personagemId: Int = -1
    private var atributosId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personagem)

        // Inicializar componentes da UI
        textAtributos = findViewById(R.id.text_attributes_personagem)
        buttonAtacar = findViewById(R.id.button_atacar_personagem)
        buttonExibirVida = findViewById(R.id.button_exibir_vida_personagem)
        buttonAtualizar = findViewById(R.id.button_atualizar_personagem)
        buttonDeletar = findViewById(R.id.button_deletar_personagem)

        // Receber o ID do personagem e atributos da Intent
        personagemId = intent.getIntExtra("personagemId", -1)
        atributosId = intent.getIntExtra("atributosId", -1)

        // Carregar atributos e exibir na tela
        if (atributosId != -1) {
            carregarAtributos(atributosId)
        }

        // Configurar ações dos botões
        buttonAtacar.setOnClickListener {
            atacar()
        }

        buttonExibirVida.setOnClickListener {
            exibirVida()
        }

        buttonAtualizar.setOnClickListener {
            atualizarPersonagem()
        }

        buttonDeletar.setOnClickListener {
            deletarPersonagem()
        }
    }

    private fun carregarAtributos(id: Int) {
        lifecycleScope.launch {
            val atributos = withContext(Dispatchers.IO) {
                database.atributosDAO().getAtributosById(id)
            }
            atributos?.let { exibirAtributos(it) }
        }
    }

    private fun exibirAtributos(atributos: Atributos) {
        textAtributos.text = """
            Força: ${atributos.forca}
            Destreza: ${atributos.destreza}
            Constituição: ${atributos.constituicao}
            Inteligência: ${atributos.inteligencia}
            Sabedoria: ${atributos.sabedoria}
            Carisma: ${atributos.carisma}
        """.trimIndent()
    }

    private fun atacar() {
        // Lógica de ataque - este é um exemplo simples
        textAtributos.append("\nPersonagem atacou!")
    }

    private fun exibirVida() {
        lifecycleScope.launch {
            val atributos = withContext(Dispatchers.IO) {
                database.atributosDAO().getAtributosById(atributosId)
            }
            atributos?.let {
                val vida = it.constituicao * 10 // Exemplo: Constituição define vida
                textAtributos.append("\nVida: $vida")
            }
        }
    }

    private fun atualizarPersonagem() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val personagem = database.personagemDAO().getPersonagemById(personagemId)
                personagem?.let {
                    it.nome = "Personagem Atualizado" // Exemplo de atualização
                    database.personagemDAO().update(it)
                }
            }
            finish()
        }
    }

    private fun deletarPersonagem() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val personagem = database.personagemDAO().getPersonagemById(personagemId)
                personagem?.let {
                    database.personagemDAO().delete(it)
                }
            }
            finish() // Fecha a atividade após a exclusão
        }
    }


}
