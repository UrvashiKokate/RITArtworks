package com.urvashikokate.ritartworks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item_description.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class ItemDescription : Fragment() {

    companion object {
        const val ARG_NAME = "name"
        const val ARG_PRICE = "price"
        const val ARG_DESCRIPTION = "description"
        const val ARG_CATEGORY = "category"
        const val ARG_IMAGE = "image"
        const val ARG_SELLER = "seller"

        fun newInstance(name: String,price:String,description:String,category:String, image:String, seller:String): ItemDescription {
            val fragment = ItemDescription()
            val bundle = Bundle().apply {
                putString(ARG_NAME, name)
                putString(ARG_PRICE,price)
                putString(ARG_DESCRIPTION,description)
                putString(ARG_CATEGORY,category)
                putString(ARG_IMAGE,image)
                putString(ARG_SELLER,seller)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

     override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
     ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_description, container, false)
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        contact.setOnClickListener {
            context?.let { it1 ->
                AlertDialog.Builder(it1,R.style.AlertDialogTheme)
                    .setTitle(" ")
                    .setMessage("Yet to be implemented")
                    .create().show()
            }
        }

        d_itemName.text = arguments?.getString(ARG_NAME)
        d_itemPrice.text = arguments?.getString(ARG_PRICE)
        d_itemDescription.text = arguments!!.getString(ARG_DESCRIPTION)
        d_itemCategory.text = arguments!!.getString(ARG_CATEGORY)
        sellerName.text = arguments!!.getString(ARG_SELLER)
        Picasso.get().load(arguments!!.getString(ARG_IMAGE)).into(d_itemImage,object :Callback{
            override fun onSuccess() {
                Log.d("Picasso","Success")
            }
            override fun onError(e: Exception?) {
                Log.d("Picasso","Error")
            }
        })
    }
}




