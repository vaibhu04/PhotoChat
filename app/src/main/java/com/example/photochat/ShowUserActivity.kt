package com.example.photochat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Adapter.PostAdapter
import com.example.photochat.Adapter.UserAdapter
import com.example.photochat.Model.Post
import com.example.photochat.Model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowUserActivity : AppCompatActivity() {
    var id: String = ""
    var title: String = ""
    var UserList: MutableList<User>? = null
    var userAdapter: UserAdapter? = null

    var postlist: MutableList<Post>? = null
    var postAdapter: PostAdapter? = null
    var idList: MutableList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_user)

        val intent = intent
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")
        Toast.makeText(applicationContext, id + title, Toast.LENGTH_SHORT).show()
        val toolbar = findViewById<Toolbar>(R.id.show_user_activity_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = title


        val back_btn: ImageView = findViewById(R.id.back_bbtn)
        back_btn.setOnClickListener() {
            finish()
        }
        toolbar.setOnClickListener() {
            finish()
        }

        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.recyclerview_show_user)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        UserList = ArrayList()
        userAdapter = UserAdapter(this, UserList!!, false)
        recyclerView.adapter = userAdapter!!
        idList = ArrayList()


        //
        when (title) {
            "likes" -> getLikes()
            "following" -> getfollowing()
            "followers" -> getfollowers()
            "views" -> getviews()


        }

    }


    private fun getLikes() {
        val ref = FirebaseDatabase.getInstance().reference.child("Likes").child(id)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                idList!!.clear()

                for (snapshot in p0.children) {

                    idList!!.add(snapshot!!.key.toString())
                }
                showUses()


            }

        })
    }

    private fun showUses() {
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                UserList!!.clear()
                for (snapshot in p0.children) {

                    val user = snapshot.getValue<User>(User::class.java)
                    for (id1 in idList!!)
                        if (id1 == snapshot.child("uid").value.toString()) {
                            UserList!!.add(
                                User(
                                    snapshot.child("username").value.toString(),
                                    snapshot.child("fullname").value.toString()
                                    ,
                                    snapshot.child("bio").value.toString(),
                                    snapshot.child("image").value.toString(),
                                    snapshot.child("uid").value.toString(),
                                    snapshot.child("propername").value.toString()
                                )
                            )
                        }
                }
                userAdapter!!.notifyDataSetChanged()
            }

        })
    }

    private fun getfollowers() {

        val ref =
            FirebaseDatabase.getInstance().reference.child("Follow").child(id).child("Followers")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                idList!!.clear()

                for (snapshot in p0.children) {
                    //Toast.makeText(applicationContext, p0.toString(), Toast.LENGTH_SHORT).show()
                    idList!!.add(snapshot!!.key.toString())
                }
                showUses()


            }

        })
    }

    private fun getfollowing() {
        val ref =
            FirebaseDatabase.getInstance().reference.child("Follow").child(id).child("Following")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                idList!!.clear()

                for (snapshot in p0.children) {
                    //Toast.makeText(applicationContext, p0.toString(), Toast.LENGTH_SHORT).show()
                    idList!!.add(snapshot!!.key.toString())
                }
                showUses()


            }

        })

    }

    private fun getviews() {

        val ref = FirebaseDatabase.getInstance().reference.child("Story").child(id)
            .child(intent.getStringExtra("storyId")).child("views")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                idList!!.clear()

                for (snapshot in p0.children) {
                    //Toast.makeText(applicationContext, p0.toString(), Toast.LENGTH_SHORT).show()
                    idList!!.add(snapshot!!.key.toString())
                }
                showUses()


            }

        })
    }
}
