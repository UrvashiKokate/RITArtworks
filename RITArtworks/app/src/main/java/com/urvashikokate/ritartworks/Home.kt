package com.urvashikokate.ritartworks

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 */
class Home : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager ? = null
    private var adapter: ItemListAdapter?= null
    val list = ArrayList<ItemsModel>()
    var filter_list = ArrayList<ItemsModel>()
    var flag: String = "a"
    var filter_flag by Delegates.notNull<Boolean>()
    var flagg = "1"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.layoutManager = GridLayoutManager(activity, 2)
        itemListRecycler.layoutManager = layoutManager

        val databaseUsers = FirebaseDatabase.getInstance().getReference("Items")

        //sortByPrice
        sortBtn.setOnClickListener {
            sortByPrice()
        }


        val itemListener = object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                var item = p0.getValue(ItemsModel::class.java)
//                list.add(item!!)
//                adapter!!.notifyDataSetChanged()
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val item = dataSnapshot.getValue(ItemsModel::class.java)
                list.add(item!!)
                adapter!!.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val item = dataSnapshot.getValue(ItemsModel::class.java)
                list.remove(item)
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error", "Error")
            }

        }

        databaseUsers.addChildEventListener(itemListener)

        adapter = context?.let { ItemListAdapter(it, list) }
        itemListRecycler.adapter = adapter


        //fabButton
        fab.setOnClickListener {
            val mainIntent = Intent(activity, Loader::class.java)
            val arguments = Bundle()
            arguments.putString("id", "add")
            mainIntent.putExtras(arguments)
            startActivity(mainIntent)

        }

        //searchView
        searchView.setOnQueryTextListener(object : OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }
        })


        var checkedCategoryArray = booleanArrayOf(false, false, false, false,false)
        val previousCheckedStates = booleanArrayOf(false, false, false, false,false)



        filterBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity,R.style.AlertDialogTheme)
            // String array for alert dialog multi choice items
            val categories = arrayOf("Cards", "Jewellery", "Painting", "Sketches","Other")

            // Boolean array for initial selected items

            if (flagg == "1") {
                checkedCategoryArray = booleanArrayOf(false, false, false, false,false)
            } else {
                for (i in checkedCategoryArray.indices) {
                    checkedCategoryArray[i] = previousCheckedStates[i]
                }
            }

            // Convert the color array to list
            val categoryList = listOf(*categories)
            builder.setTitle("Select category")

            builder.setMultiChoiceItems(
                categories,
                checkedCategoryArray
            ) { dialog, which, isChecked ->
                checkedCategoryArray[which] = isChecked
                for (i in checkedCategoryArray.indices) {
                    previousCheckedStates[i] = checkedCategoryArray[i]
                }
                }

            builder.setPositiveButton("OK") { dialog, which ->
                for (i in checkedCategoryArray.indices) {
                    val checked = checkedCategoryArray[i]
                    if (checked) {
                        filter_flag = true
                        for (item in list){
                            if (item.category==categoryList[i])
                                filter_list.add(item)
                        }
                    }
                }
                if (filter_list.isEmpty()){
                    if (filter_flag){
                        filter_list = list.clone() as ArrayList<ItemsModel> }
                    else
                    {
                        Toast.makeText(activity,"No items found",Toast.LENGTH_LONG).show()
                        filter_flag = true
                    }
                }
                adapter = context?.let { ItemListAdapter(it, filter_list) }
                itemListRecycler.adapter = adapter
                flagg = "Ok"
            }
            filter_list.clear()

            val dialog = builder.create()
            dialog.show()
            }

    }//onViewCreated

    private fun sortByPrice() {
        val listItems = arrayOf("Price Low to High", "Price High to Low")
        val mBuilder = AlertDialog.Builder(activity,R.style.AlertDialogTheme)
        mBuilder.setTitle("Sort By")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            if (listItems[i] == "Price Low to High"){
                if (flagg == "Ok"){
                filter_list.sortWith(compareByDescending { it.price })
                    filter_list.reverse()
                adapter = context?.let { ItemListAdapter(it, filter_list) }
                itemListRecycler.adapter = adapter
                }else {
                    list.sortWith(compareByDescending { it.price })
                    list.reverse()
                    adapter = context?.let { ItemListAdapter(it, list) }
                    itemListRecycler.adapter = adapter
                }
            }else{
                if (flagg == "Ok"){
                filter_list.sortWith(compareByDescending { it.price })
                adapter = context?.let { ItemListAdapter(it, filter_list) }
                itemListRecycler.adapter = adapter
                }else {
                    list.sortWith(compareByDescending { it.price })
                    adapter = context?.let { ItemListAdapter(it, list) }
                    itemListRecycler.adapter = adapter
                }
            }
            dialogInterface.dismiss()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

}//Home

