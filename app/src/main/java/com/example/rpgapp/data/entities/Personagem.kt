package com.example.rpgapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personagem")
data class Personagem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "nome")
    var nome: String,

    @ColumnInfo(name = "raca")
    var raca: String,

    @ColumnInfo(name = "atributosId")
    var atributosId: Int
)
