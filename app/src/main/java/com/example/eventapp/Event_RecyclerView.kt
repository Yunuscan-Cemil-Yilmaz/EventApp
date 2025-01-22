package com.example.eventapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventMain_RecyclerViewAdapter(private val context: Context,
                                    private val items: MutableList<Event>,
                                    private val clickListener: (Int) -> Unit) : RecyclerView.Adapter<EventMain_RecyclerViewAdapter.MyViewHolder>() {

    // ViewHolder class to hold references to item views
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var eventName : TextView = itemView.findViewById(R.id.textName)
        var eventDate: TextView = itemView.findViewById(R.id.textDate)
        var eventTime: TextView = itemView.findViewById(R.id.textTime)
        var eventCategory: TextView = itemView.findViewById(R.id.textCategory)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.event_satir, parent, false)

        return EventMain_RecyclerViewAdapter.MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.eventName.setText(
            if (items.get(position).name.length > 20) items.get(position).name.take(20) + "..." else items.get(position).name
        )
        holder.eventDate.setText(items.get(position).dates.start.localDate)
        holder.eventTime.setText(items.get(position).dates.start.localTime)
        holder.eventCategory.setText(items.get(position).classifications[0].segment.name)
        //holder.eventLink.setText(items.get(position).url)


        holder.itemView.setOnClickListener {
            clickListener(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size

    fun updateData(newItems: List<Event>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}