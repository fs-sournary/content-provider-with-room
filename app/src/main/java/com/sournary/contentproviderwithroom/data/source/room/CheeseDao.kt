package com.sournary.contentproviderwithroom.data.source.room

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sournary.contentproviderwithroom.data.model.Cheese

/**
 * Created: 30/08/2018
 * By: Sang
 * Description:
 */
@Dao
interface CheeseDao {

    /**
     * Counting number of cheeses in the table
     *
     * @return: The number of cheeses
     */
    @Query("SELECT COUNT(*) FROM ${Cheese.TABLE_NAME}")
    fun count(): Int

    /**
     * Insert a cheese into the table
     *
     * @return: The row id of the newly inserted cheese
     */
    @Insert
    fun insert(cheese: Cheese): Long

    /**
     * Insert multiple cheeses into the table
     *
     * @return: The row ids of the newly inserted cheeses.
     */
    @Insert
    fun insertAll(cheeses: List<Cheese>): List<Long>

    /**
     * Select all cheeses
     *
     * @return: A Cursor of all the cheeses in the table.
     */
    @Query("SELECT * FROM ${Cheese.TABLE_NAME}")
    fun selectedAll(): Cursor

    /**
     * Select a Cheese by the Id
     *
     * @return: A Cursor of the selected Id.
     */
    @Query("SELECT * FROM ${Cheese.TABLE_NAME} WHERE ${Cheese.COLUMN_ID} = :id")
    fun selectById(id: Long): Cursor

    /**
     * Delete a Cheese by the Id
     *
     * @return: A number of Cheese deleted.
     */
    @Query("DELETE FROM ${Cheese.TABLE_NAME} WHERE ${Cheese.COLUMN_ID} = :id")
    fun deleteById(id: Long): Int

    /**
     * Update the Cheese
     */
    @Update
    fun update(cheese: Cheese): Int
}
