package com.urvashikokate.ritartworks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPassword : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        mAuth = FirebaseAuth.getInstance()
        btn_submit.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    private fun sendPasswordResetEmail() {

            val email = et_email.text.toString()

            if (!email.isEmpty()) {
                mAuth
                    .sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val message = "Email sent."
                            AlertDialog.Builder(this,R.style.AlertDialogTheme).setMessage("Password reset email sent to $email")
                                .setNeutralButton("OK"){
                                        dailog,listener -> startActivity(Intent(this,Login::class.java))
                                }
                                .create().show()
                        } else {
                            Log.w("tag", task.exception!!.message)
                            Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            }
        }

    }




