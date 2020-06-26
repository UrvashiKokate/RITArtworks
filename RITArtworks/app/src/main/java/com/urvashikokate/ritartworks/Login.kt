package com.urvashikokate.ritartworks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val signin_Btn = findViewById<View>(R.id.signInButton) as Button
        val register = findViewById<View>(R.id.registerTextview) as Button

        register.setOnClickListener {  startActivity(Intent(this,Register::class.java)) }

        signin_Btn.setOnClickListener(View.OnClickListener { v -> login() })
        forgotPasswordBtn.setOnClickListener { startActivity(Intent(this,ResetPassword::class.java)) }
        }


    private fun login() {
        val emailTxt = findViewById<View>(R.id.emailEditText) as EditText
        val passwordTxt = findViewById<View>(R.id.passwordEditText) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                progressBar.visibility = View.VISIBLE
            }
                .addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful){
                    if (mAuth.currentUser!!.isEmailVerified) {
                        startActivity(Intent(this, Loader::class.java))
                        progressBar.visibility = View.INVISIBLE
                    }
                    else{
                        progressBar.visibility = View.INVISIBLE
                        AlertDialog.Builder(this,R.style.AlertDialogTheme).setMessage("Please verify your email")
                            .setNeutralButton("OK") { dialog, which ->  {}}
                            .create().show()
                    }
                } else {
                    AlertDialog.Builder(this,R.style.AlertDialogTheme)
                        .setTitle(" ")
                        .setIcon(R.drawable.error)
                        .setMessage("Email or Password is incorrect")
                        .create().show()
                }
            })
        } else {

            AlertDialog.Builder(this,R.style.AlertDialogTheme)
                .setTitle(" ")
                .setIcon(R.drawable.error)
                .setMessage("Please enter credentials.")
                .setNeutralButton("OK") { dialog, which ->
                    dialog.dismiss()
                }.create().show()
        }
    }
}

