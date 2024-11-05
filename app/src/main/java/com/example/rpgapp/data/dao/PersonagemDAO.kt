package com.example.rpgapp.data.dao

import androidx.room.*
import com.example.rpgapp.data.entities.Personagem

@Dao
interface PersonagemDAO {

    @Insert
    suspend fun insert(personagem: Personagem): Long

    @Update
    suspend fun update(personagem: Personagem): Int // Retorna o número de linhas afetadas

    @Delete
    suspend fun delete(personagem: Personagem): Int // Retorna o número de linhas afetadas

    @Query("SELECT * FROM personagem WHERE id = :id")
    suspend fun getPersonagemById(id: Int): Personagem?
}
