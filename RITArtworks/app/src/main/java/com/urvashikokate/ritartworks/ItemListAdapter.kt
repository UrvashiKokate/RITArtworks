package com.urvashikokate.ritartworks

import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ItemListAdapter( val context: Context, private var arrayList: ArrayList<ItemsModel>) : RecyclerView.Adapter<ItemListAdapter.ItemHolder>(), Filterable {


    private val fragment = ItemDescription()

    var mFilteredList = ArrayList<ItemsModel>()
    init {
        mFilteredList = arrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        Log.d("done", "done")
        val viewHolder = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return ItemHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val name = mFilteredList[position].name
        val price = mFilteredList[position].price

        holder.name.text = name
        holder.price.text = price
        Picasso.get().load(mFilteredList[position].image).into(holder.image, object : Callback {
            override fun onSuccess() {
                Log.d("Picasso", "Success")
            }

            override fun onError(e: Exception?) {
                Log.d("Picasso", "Error")
            }
        })

        holder.itemView.setOnClickListener {
            val arguments = Bundle()
            arguments.putString("name", name)
            arguments.putString("price", price)
            arguments.putString("category", mFilteredList[position].category)
            arguments.putString("description", mFilteredList[position].description)
            arguments.putString("imageUrl", mFilteredList[position].image)
            arguments.putString("seller", mFilteredList[position].seller)
            arguments.putString("id", "1")

            fragment.arguments = arguments
            val intent =
                Intent(context, Loader::class.java) //context we got from constructor
            intent.putExtras(arguments)
            context.startActivity(intent) // or we can use ContextCompat
        }
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.itemName)
        var price = itemView.findViewById<TextView>(R.id.itemPrice)
        var image = itemView.findViewById<ImageView>(R.id.itemImage)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    mFilteredList = arrayList
                } else {
                        val filteredList = ArrayList<ItemsModel>()
                        for (DataItem in arrayList) {
                                if (charString.toLowerCase(Locale.ENGLISH) in DataItem.category.toLowerCase(
                                        Locale.ENGLISH) || charString.toLowerCase(Locale.ENGLISH) in DataItem.name.toLowerCase(
                                        Locale.ENGLISH)
                                ) { filteredList.add(DataItem)
                                }
                        }
                        mFilteredList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                mFilteredList = filterResults?.values as ArrayList<ItemsModel>
                notifyDataSetChanged()
            }
        }
    }
}
