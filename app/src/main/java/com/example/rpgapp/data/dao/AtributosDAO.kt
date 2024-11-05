package com.example.rpgapp.data.dao

import androidx.room.*
import com.example.rpgapp.data.entities.Atributos

@Dao
interface AtributosDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(atributos: Atributos): Long

    @Query("SELECT * FROM atributos WHERE id = :id")
    suspend fun getAtributosById(id: Int): Atributos? // Função adicionada para buscar Atributos por ID
}
