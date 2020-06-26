package com.urvashikokate.ritartworks

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


import kotlinx.android.synthetic.main.activity_loader.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class Loader : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout

    lateinit var mDatabaseUsers: DatabaseReference
    val mAuth = FirebaseAuth.getInstance()
    lateinit var user: UserModel
    lateinit var uid:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = "Home"

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("UserProfile")

        val mUser = mAuth.currentUser;
        mDatabaseUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                uid = mUser!!.uid
                for (p0 in datasnapshot.children) {
                    if (uid == p0.key) {
                        user = p0.getValue(UserModel::class.java)!!
                        var name = user.name
                        drawer_title.text = "Hello $name"
                    }
                }
            }
        })

        //initialise action bar drawer toggle instance
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this, drawer_layout,toolbar,R.string.drawer_open,R.string.drawer_close)
        {
            override fun onDrawerClosed(drawerView:View) {
                super.onDrawerClosed(drawerView)
            }
            override fun onDrawerOpened(drawerView: View){
                super.onDrawerOpened(drawerView)

            }
        }//toggle

        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view.setNavigationItemSelectedListener { menuItem ->
            drawer_layout.closeDrawers()
            menuItem.isChecked = true

            when (menuItem.itemId){
                R.id.drawer_home -> {
                    actionBar?.title = "Home"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, Home(), null).commit()
                }
                R.id.drawer_account -> {
                    actionBar?.title = "Account"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, EditProfile(), null).commit()
                }
                R.id.drawer_items -> {
                    actionBar?.title = "My Items"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, MyItems(), null).commit()
                }
                R.id.drawer_logout ->{
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, Login::class.java))
                }
            }
            true
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.frame, Home(), null)
            .commit()

        val intent = intent
        val arguments = intent.extras
        val id = arguments?.getString("id")

        if (id == "1" ) {
            val name = arguments.getString("name")
            val price = arguments.getString("price")
            val description = arguments.getString("description")
            val category = arguments.getString("category")
            val imageUrl = arguments.getString("imageUrl")
            val seller = arguments.getString("seller")
            val fragment = ItemDescription.newInstance(
                name!!,
                price!!,
                description!!,
                category!!,
                imageUrl!!,
                seller!!
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, fragment, null).commit()
        }else if (id == "add"){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, AddItems(), null).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
     }
}