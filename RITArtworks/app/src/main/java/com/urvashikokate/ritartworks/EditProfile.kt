package com.urvashikokate.ritartworks

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*

/**
 * A simple [Fragment] subclass.
 */
class EditProfile : Fragment() {

    lateinit var mDatabaseUsers: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    lateinit var user: UserModel
    lateinit var uid:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        name_editBtn.setOnClickListener{
            editName()
        }

        mobile_editBtn.setOnClickListener {
            editMobile()
        }

        save.setOnClickListener {
            saveChanges()
        }

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("UserProfile")

        val mUser = mAuth.currentUser;
        mDatabaseUsers.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                uid = mUser!!.uid
                for (p0 in datasnapshot.children) {
                    if (uid == p0.key) {
                        user = p0.getValue(UserModel::class.java)!!
                        name.text = user.name
                        email.text = user.email
                        mobile.text = user.phone
                        edit_name.setText(user.name)
                        edit_mobile.setText(user.phone)
                    }
                }
            }
        })

    }

    private fun saveChanges() {
        val editedName = edit_name.text.toString()
        val editedMobile = edit_mobile.text.toString()
        mDatabaseUsers.child(uid).child("name").setValue(editedName)
        mDatabaseUsers.child(uid).child("phone").setValue(editedMobile)
        updateUI(editedName,editedMobile)
    }

    private fun updateUI(editedName:String,editedMobile:String) {
        name.text = editedName
        mobile.text = editedMobile
        name.visibility = View.VISIBLE
        edit_name.visibility = View.INVISIBLE
        mobile.visibility = View.VISIBLE
        edit_mobile.visibility = View.INVISIBLE
        this.context?.let {
            AlertDialog.Builder(it,R.style.AlertDialogTheme)
                .setTitle(" ")
                .setIcon(R.drawable.done)
                .setMessage("Updated Succesfully")
                .setOnDismissListener {
                    startActivity(Intent(activity,Loader::class.java))
                }
                .create().show()
        }
    }

    private fun editMobile() {
        save.visibility = View.VISIBLE
        mobile.visibility = View.INVISIBLE
        edit_mobile.visibility = View.VISIBLE
    }

    private fun editName() {
        name.visibility = View.INVISIBLE
        edit_name.visibility = View.VISIBLE
        save.visibility = View.VISIBLE
    }
}
