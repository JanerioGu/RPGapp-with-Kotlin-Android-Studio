package com.example.rpgapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rpgapp.data.entities.Atributos
import com.example.rpgapp.data.entities.Personagem
import com.example.rpgapp.data.dao.AtributosDAO
import com.example.rpgapp.data.dao.PersonagemDAO

@Database(entities = [Atributos::class, Personagem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun atributosDAO(): AtributosDAO
    abstract fun personagemDAO(): PersonagemDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rpg_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}