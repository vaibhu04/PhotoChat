package com.example.photochat


import android.app.Application
import com.example.photochat.R
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Adapter.UserAdapter
import com.example.photochat.Model.ChatList
import com.example.photochat.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class All_Message_Caht : AppCompatActivity() {
    var chatslist: MutableList<String>? = null
    var userlist: MutableList<User>? = null
    var userAdapter: UserAdapter? = null
    var recyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all__message__caht)

        recyclerView = findViewById(R.id.recyclerview_chats)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.reverseLayout = true

        recyclerView!!.layoutManager = linearLayoutManager
        userlist = ArrayList()
        userAdapter = UserAdapter(this@All_Message_Caht, userlist!!)
        recyclerView!!.adapter = userAdapter
        findchatlist()

    }

    private fun findchatlist() {
        var ref = FirebaseDatabase.getInstance().reference.child("ChatList")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (id in p0.children) {
                        chatslist?.add(id!!.child("id").value.toString())

                    }

                    val ref = FirebaseDatabase.getInstance().reference.child("Users")
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            userlist!!.clear()
                            for (snapshot in p0.children) {

                                val user = snapshot.getValue<User>(User::class.java)
                                for (id1 in chatslist!!)
                                    if (id1 == snapshot.child("uid").value.toString()) {
                                        userlist!!.add(
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
            }
        })
    }
}