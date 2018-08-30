package com.sournary.contentproviderwithroom.ui.main

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sournary.contentproviderwithroom.R
import com.sournary.contentproviderwithroom.data.model.Cheese
import java.util.concurrent.Executors

/**
 * Created: 04/09/2018
 * By: Sang
 * Description:
 */
class CheeseAdapter(
    callback: DiffUtil.ItemCallback<Cheese> = object : DiffUtil.ItemCallback<Cheese>() {

        override fun areItemsTheSame(oldItem: Cheese, newItem: Cheese): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cheese, newItem: Cheese): Boolean =
            oldItem.name == newItem.name
    }
) : ListAdapter<Cheese, CheeseAdapter.ViewHolder>(
    AsyncDifferConfig.Builder<Cheese>(callback)
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cheese, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    fun getCheeseList(cursor: Cursor?): MutableList<Cheese> {
        val cheeses = mutableListOf<Cheese>()
        if (cursor == null || cursor.count == 0) {
            return cheeses
        }
        cursor.moveToFirst()
        val nameColumn = cursor.getColumnIndex(Cheese.COLUMN_NAME)
        while (cursor.moveToNext()) {
            val name = cursor.getString(nameColumn)
            cheeses.add(Cheese(name = name))
        }
        return cheeses
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cheeseText = itemView.findViewById<AppCompatTextView>(R.id.text_cheese)

        fun bindView(cheese: Cheese) {
            cheeseText.text = cheese.name
        }
    }
}
