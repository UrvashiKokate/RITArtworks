package com.urvashikokate.ritartworks

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_add_items.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddItems : Fragment() {

    val mAuth = FirebaseAuth.getInstance()
    lateinit var mDatabaseItems: DatabaseReference
    lateinit var mDatabaseUsers: DatabaseReference
    lateinit var spinnervalue: String
    lateinit var mStorageRef: StorageReference
    lateinit var URI : Uri
    var flag = "1"
    private var url:String? = null



    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        addPriceEditText.setText("$")
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("UserProfile")
        mDatabaseItems = FirebaseDatabase.getInstance().getReference("Items")
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        uploadBtn.setOnClickListener { uploadImage() }
        addBtn.setOnClickListener {
            if (addProductNameEditText.text.toString().isEmpty() || addPriceEditText.text.toString().isEmpty() || flag=="1" || addDescriptionEditText.text.toString().isEmpty()) {
                context?.let { it1 ->
                    AlertDialog.Builder(it1, R.style.AlertDialogTheme)
                        .setTitle(" ")
                        .setIcon(R.drawable.error)
                        .setMessage("Please enter all details of the product.")
                        .setNeutralButton("OK") { dialog, which ->
                            {}
                        }.create().show()
                }
            }else { addtoStorage()
                }
            }


        //category spinner
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.item_category,
                android.R.layout.simple_spinner_item
            )
                .also { arrayAdapter ->
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                    spinner.adapter = arrayAdapter
                }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinner.prompt = "Select Category"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnervalue =
                    spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
            }
        }//spinner listener
    }//onViewCreated

    //upload image
    private fun uploadImage() {
        flag = "2"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(this.requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted
                pickImageFromGallery();
            }
        } else {
            //system OS is < Marshmallow
            pickImageFromGallery();
        }
    }


    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        var imageView = findViewById<View>(R.id.imageView) as ImageView
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageView.setImageURI(data?.data)
            imageView.visibility = View.VISIBLE
            if (data != null) {
                URI = data.data!!
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun addtoStorage() {
        //add to storage
        val random: String = randomString()
        val uploadTask = mStorageRef.child(random).putFile(URI)

        uploadTask.addOnSuccessListener {
        }.addOnFailureListener{

            context?.let { it1 ->
                AlertDialog.Builder(it1,R.style.AlertDialogTheme)
                    .setTitle(" ")
                    .setIcon(R.drawable.error)
                    .setMessage("Upload Failed")
                    .create().show()
            }
        }
        uploadTask.addOnProgressListener { taskSnapshot ->
                progressBar.visibility = View.VISIBLE
            }
//            .addOnPausedListener {
//            }
            .addOnSuccessListener { taskSnapshot ->
            }
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                mStorageRef.child(random).downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl: Uri? = task.result
                    if (downloadUrl != null) {
                        addItem(downloadUrl)
                    }
                }
                progressBar.visibility = View.INVISIBLE
            }
            .addOnFailureListener { e ->
            }
    }


    private fun addItem(image_url:Uri) {
        val product_name = addProductNameEditText.text.toString()
        val product_price = addPriceEditText.text.toString()
        val product_description = addDescriptionEditText.text.toString()

        val uid = mAuth.currentUser!!.uid
        mDatabaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
              for (p0 in datasnapshot.children){
                  if (uid == p0.key){
                    val user = p0.getValue(UserModel::class.java)
                      if (user != null) {

                          //add to database
                          val key= mDatabaseItems.push().key
                          if (key != null) {
                              mDatabaseItems.child(key).child("name").setValue(product_name)
                              mDatabaseItems.child(key).child("price").setValue(product_price)
                              mDatabaseItems.child(key).child("description").setValue(product_description)
                              mDatabaseItems.child(key).child("image").setValue(image_url.toString())
                              mDatabaseItems.child(key).child("category").setValue(spinnervalue)
                              mDatabaseItems.child(key).child("seller").setValue(user.name)
                              mDatabaseItems.child(key).child("uid").setValue(uid)
                          }
                      }
                  }
              }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })


        this.context?.let {
            AlertDialog.Builder(it,R.style.AlertDialogTheme)
                .setTitle(" ")
                .setIcon(R.drawable.done)
                .setMessage("Item added Succesfully")
                .setOnDismissListener {
                    startActivity(Intent(activity,Loader::class.java))
                }
                .create().show()
        }
    }



    //genrate random string
    fun randomString(): String {
        val list = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()
        var randomS = ""
        for (i in 1..10) {
            randomS += list[getRandomNumber(0, list.size - 1)]
        }
        return randomS
    }
    fun getRandomNumber(min: Int, max: Int): Int {
        return Random().nextInt((max - min) + 1) + min
    }


}//activity
