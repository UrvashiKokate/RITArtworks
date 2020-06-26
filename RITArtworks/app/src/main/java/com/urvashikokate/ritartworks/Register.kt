package com.urvashikokate.ritartworks

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    lateinit var mDatabase:DatabaseReference
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mDatabase = FirebaseDatabase.getInstance().getReference("UserProfile")
        val register_Btn = findViewById<View>(R.id.registerButton)   
        register_Btn.setOnClickListener(View.OnClickListener { v -> register() })
    }

    private fun register() {
        val emailTxt = findViewById<View>(R.id.emailEditText2) as EditText
        val passwordTxt = findViewById<View>(R.id.passwordEditText2) as EditText
        val nameTxt = findViewById<View>(R.id.nameEditText) as EditText
        val phoneTxt = findViewById<View>(R.id.phoneEditText) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        var name = nameTxt.text.toString()
        var phone = phoneTxt.text.toString()

        if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty() && !phone.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,
                OnCompleteListener { task->
                    if (task.isSuccessful) {
                        verifyEmail(email,name,phone)
                    }else {

                        AlertDialog.Builder(this,R.style.AlertDialogTheme)
                            .setTitle(" ")
                            .setIcon(R.drawable.error)
                            .setMessage("Error in creating account.Please check your account details")
                            .create().show()
                    }
                    })
        } else {

            AlertDialog.Builder(this,R.style.AlertDialogTheme).setMessage("Please enter credentials.")
                .setNeutralButton("OK") { dialog, which ->
                    {}
                }.create().show()
        }
        }

    private fun verifyEmail(email:String,name:String,phone:String) {
        val mUser = mAuth.currentUser;
        mUser!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = mUser.uid
                    mDatabase.child(uid).child("name").setValue(name)
                    mDatabase.child(uid).child("email").setValue(email)
                    mDatabase.child(uid).child("phone").setValue(phone)
                    AlertDialog.Builder(this,R.style.AlertDialogTheme).setTitle("Verify")
                        .setIcon(R.drawable.done)
                        .setMessage("Verification email sent to " + mUser.getEmail())
                        .setNeutralButton("OK"){
                                dailog,listener -> startActivity(Intent(this,Login::class.java))
                        }
                        .create().show()
                    FirebaseAuth.getInstance().signOut()

                } else {
                    Log.e("tag", "sendEmailVerification", task.exception)
                    AlertDialog.Builder(this,R.style.AlertDialogTheme).setTitle("Verify")
                        .setIcon(R.drawable.error)
                        .setMessage("Failed to send verification Email")
                        .create().show()
                }
            }

    }


}

