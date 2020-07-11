package com.example.photochat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Adapter.CommentsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*
import org.w3c.dom.Comment

class CommentsActivity : AppCompatActivity() {
        private var commentsAdapter:CommentsAdapter?=null
    private var commentList: MutableList<com.example.photochat.Model.Comment>?=null
        private var postId =""
        private var publisherId = ""
    private var firebaseUser: FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

       


        var recyclerView: RecyclerView
        recyclerView = findViewById(R.id.comments_recyclerview)
        commentList = ArrayList()
        commentsAdapter = CommentsAdapter(this, commentList!!)
        back_comments_btn.setOnClickListener(){

            finish()
        }


        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout=false
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter=commentsAdapter

        val intent = intent
        postId = intent.getStringExtra("postId")
        publisherId= intent.getStringExtra("publisherId")
        firebaseUser = FirebaseAuth.getInstance().currentUser


        retrieveimageinfo()
        getPostImageforcomment()
        readAllComments()

        publish_btn.setOnClickListener(){
            if(TextUtils.isEmpty(comments_message.text))

            { Toast.makeText(applicationContext,"First Write a comment..",Toast.LENGTH_SHORT).show()}
            else
            {  AddComment()
           }

        }

    }

    private fun AddComment() {
        val ref = FirebaseDatabase.getInstance().reference.child("Comments").child(postId!!)
        val commentMap = HashMap<String,Any>()
        commentMap["comment"] = comments_message.text.toString()
        commentMap["publisherId"] = firebaseUser!!.uid
        ref.push().setValue(commentMap)
        AddNotification()
        comments_message.text.clear()

    }

    private fun retrieveimageinfo() {
   val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<com.example.photochat.Model.User>(com.example.photochat.Model.User::class.java)

                    Picasso.get().load(p0.child("image").value.toString()).placeholder(R.drawable.profile).into(comments_view_profile_frag)
                }
            }

        })
    }

    private fun getPostImageforcomment() {
        val ref = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user = p0.getValue<com.example.photochat.Model.User>(com.example.photochat.Model.User::class.java)

                    Picasso.get().load(p0.child("image").value.toString()).placeholder(R.drawable.profile).into(image_comment)
                    fullname_comment.text=p0.child("desciption").value.toString()

                }
            }

        })
    }

    private fun readAllComments(){
        val ref= FirebaseDatabase.getInstance().reference.child("Comments").child(postId!!)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                commentList?.clear()
                for(snapshot in p0.children){
                val comment = snapshot.getValue<com.example.photochat.Model.Comment>(com.example.photochat.Model.Comment::class.java)
                val publiherId = snapshot.child("publisherId").value.toString()
                val commente =snapshot.child("comment").value.toString()
                  commentList!!.add(com.example.photochat.Model.Comment(commente,publiherId))
                    commentsAdapter!!.notifyDataSetChanged()}

            }


        })
    }

    private fun AddNotification(){
        val ref =FirebaseDatabase.getInstance().reference.child("Notifications").child(publisherId!!)
        var Notify = HashMap<String,Any>()
        Notify["userId"]= firebaseUser!!.uid
        Notify["text"]= "Commented: " + comments_message.text.toString()
        Notify["postId"]= postId
        Notify["ispost"]= true
        Notify["seen"] =false
        Notify["notificationId"]=ref.push().key.toString()
        ref.push().setValue(Notify)

    }


}
