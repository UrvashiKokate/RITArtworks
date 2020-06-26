package com.urvashikokate.ritartworks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_my_items.*
import kotlinx.android.synthetic.main.item_list.*

/**
 * A simple [Fragment] subclass.
 */
class MyItems : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager ? = null
    val mAuth = FirebaseAuth.getInstance()
    lateinit var mDatabaseUsers: DatabaseReference
    private var myadapter: ItemListAdapter?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.layoutManager = GridLayoutManager(activity,2)
        myListRecycler.layoutManager = layoutManager

        val databaseUsers = FirebaseDatabase.getInstance().getReference("Items")

        //initialise array to store items of the database
        val list = ArrayList<ItemsModel>()

        val itemListener = object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                var item = p0.getValue(ItemsModel::class.java)
//                var uid = mAuth.currentUser!!.uid
//                if (item!!.uid == uid){
//                    list.add(item)
//                }
//
//                myadapter!!.notifyDataSetChanged()
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                var item = dataSnapshot.getValue(ItemsModel::class.java)
                var uid = mAuth.currentUser!!.uid
                if (item!!.uid == uid){
                    list.add(item)
                }

                myadapter!!.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                var item = dataSnapshot.getValue(ItemsModel::class.java)
                list.remove(item)
                myadapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error","Error")
            }

        }


        databaseUsers.addChildEventListener(itemListener)

        myadapter = context?.let { ItemListAdapter(it,list) }
        myListRecycler.adapter = myadapter

        mysearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                myadapter?.filter?.filter(newText)
                return false
            }
        })
    }

}
