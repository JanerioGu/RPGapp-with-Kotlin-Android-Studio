package com.example.rpgapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "atributos")
data class Atributos(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "forca")
    var forca: Int = 8,

    @ColumnInfo(name = "destreza")
    var destreza: Int = 8,

    @ColumnInfo(name = "constituicao")
    var constituicao: Int = 8,

    @ColumnInfo(name = "inteligencia")
    var inteligencia: Int = 8,

    @ColumnInfo(name = "sabedoria")
    var sabedoria: Int = 8,

    @ColumnInfo(name = "carisma")
    var carisma: Int = 8
)
