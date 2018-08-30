package com.sournary.contentproviderwithroom.data.source.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sournary.contentproviderwithroom.data.model.Cheese

/**
 * Created: 30/08/2018
 * By: Sang
 * Description:
 */
@Database(entities = [Cheese::class], version = 1)
abstract class CheeseDatabase : RoomDatabase() {

    abstract fun cheeseDao(): CheeseDao

    fun initData() {
        if (cheeseDao().count() == 0) {
            val cheese = Cheese()
            beginTransaction()
            Cheese.CHEESES.forEach {
                cheese.name = it
                cheeseDao().insert(cheese)
            }
            setTransactionSuccessful()
            endTransaction()
        }
    }

    companion object {

        private const val DATABASE_NAME = "cheeses.db"

        private var INSTANCE: CheeseDatabase? = null

        @Synchronized
        fun getInstance(context: Context): CheeseDatabase =
            INSTANCE ?: Room.databaseBuilder(context, CheeseDatabase::class.java, DATABASE_NAME)
                .build().apply {
                    INSTANCE = this
                    initData()
                }
    }
}
