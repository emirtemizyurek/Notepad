package com.android.spexco

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class RecyclerViewAdapter(
    val context: Context,
    private var itemsArrayList: ArrayList<OOPItem>,
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private lateinit var mListener: OnItemClickListener
    val cardViewList = ArrayList<CardView>()

    fun setOnClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notes, parent, false)
        return ViewHolder(v, mListener)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewItemTitle.text = itemsArrayList[position].title
        holder.textViewItemTNote.text = itemsArrayList[position].note
        cardViewList.add(holder.cardView)

        if (itemsArrayList[position].priority == "High") {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.red))
        } else if (itemsArrayList[position].priority == "Normal") {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.yellow))
        }
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        var textViewItemTitle: TextView = itemView.findViewById(R.id.textview_item_title)
        var textViewItemTNote: TextView = itemView.findViewById(R.id.textview_item_notes)
        var cardView: CardView = itemView.findViewById(R.id.cardview_notes)

        init {
            itemView.setOnClickListener {
                listener.onItemLongClick(
                    adapterPosition,
                    itemsArrayList[adapterPosition],
                    itemsArrayList
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return itemsArrayList.size
    }

    interface OnItemClickListener {
        fun onItemLongClick(position: Int, item: OOPItem, arrayList: ArrayList<OOPItem>)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeItems(items: ArrayList<OOPItem>) {
        this.itemsArrayList = items
        notifyDataSetChanged()
    }


}