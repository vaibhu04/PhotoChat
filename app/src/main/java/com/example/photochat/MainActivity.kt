package com.example.photochat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.photochat.Fragment.HomeFragment
import com.example.photochat.Fragment.NotificationsFragment
import com.example.photochat.Fragment.ProfileFragment
import com.example.photochat.Fragment.SearchFragment
import com.example.photochat.Model.Notification
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_addpost -> {
                item.isChecked=false
                startActivity(Intent(this@MainActivity,AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }

            R.id.nav_notifications -> {
                moveToFragment(NotificationsFragment())
                val navigationView: BottomNavigationView = findViewById(R.id.nav_view);
                val menu: Menu = navigationView.getMenu();
                val menuItem:MenuItem = menu.findItem(R.id.nav_notifications)
                menuItem.setIcon(R.drawable.heart)

                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        moveToFragment(HomeFragment())
        readAllNotifications()
    }


    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }

    private fun readAllNotifications() {
        val ref= FirebaseDatabase.getInstance().reference.child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){

                    for( snapshot in p0!!.children) {

                        val Seen = snapshot.child("seen").value.toString()
                        if(Seen.toBoolean()==false){
                            val navigationView: BottomNavigationView = findViewById(R.id.nav_view);
                            val menu: Menu = navigationView.getMenu();
                             val menuItem:MenuItem = menu.findItem(R.id.nav_notifications)
                            menuItem.setIcon(R.drawable.heart1)

                        }

                    }


                }
            }

        })
    }

}
