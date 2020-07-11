package com.example.photochat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CallingActivity : AppCompatActivity() {
    private var ref = FirebaseDatabase.getInstance().reference
    private var fullname: TextView?=null
    private var checker : String?=""        //to check if cancel button is clickeed or not
    private var cancel_btn : ImageView?=null
    private var accept_btn : ImageView?=null
    private var profile_img : ImageView?=null
    private var receiverId : String? = null
    private var receiverimg : String? = null
    private var receivername : String? = null
    private var senderId : String?=null
    private var senderimg : String?=null
    private var sendername : String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)

        fullname = findViewById(R.id.fullname_video_call)
        cancel_btn = findViewById(R.id.cancel_call_image)
        accept_btn = findViewById(R.id.make_call_image)
        profile_img = findViewById(R.id.profile_img_video_call)
        senderId = FirebaseAuth.getInstance().currentUser!!.uid
        val intent = intent
        receiverId = intent.getStringExtra("receiverId")

        getandsetReceiverProfileInfo()


        cancel_btn!!.setOnClickListener {
            checker = "clicked"

            cancelthecallinguser()//remove calling and ringing id
        }
    }

    private fun cancelthecallinguser() {
        /// from sender side code
        val callref = FirebaseDatabase.getInstance().reference
        callref.child("Calls").child(senderId!!).child("Calling").addValueEventListener(object : ValueEventListener{
            var callingID =""
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists() && p0.hasChild("receiverid")){
                    callingID = p0.child("receiverid").value.toString()

                    callref.child("Calls").child(callingID).child("Ringing").removeValue().addOnCompleteListener {task->
                        if (task.isSuccessful){
                            callref.child("Calls").child(senderId!!).child("Calling").removeValue().addOnCompleteListener{
                                startActivity(Intent(this@CallingActivity,MessageChatActivity::class.java))
                                finish()
                            }
                        }
                    }


                }
                else
                {
                    startActivity(Intent(this@CallingActivity,MessageChatActivity::class.java))
                    finish()
                }
            }


        })

        // from receiver side code
        callref.child("Calls").child(senderId!!).child("Ringing").addValueEventListener(object : ValueEventListener{
            var ringingID =""
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists() && p0.hasChild("ringinguid")){
                    ringingID = p0.child("ringinguid").value.toString()

                    callref.child("Calls").child(ringingID).child("Calling").removeValue().addOnCompleteListener {task->
                        if (task.isSuccessful){
                            callref.child("Calls").child(senderId!!).child("Ringing").removeValue().addOnCompleteListener{
                                startActivity(Intent(this@CallingActivity,MessageChatActivity::class.java))
                                finish()
                            }
                        }
                    }


                }
                else
                {
                    startActivity(Intent(this@CallingActivity,MessageChatActivity::class.java))
                    finish()
                }
            }


        })
    }

    private fun getandsetReceiverProfileInfo() {
        val ref = FirebaseDatabase.getInstance().reference.child("Users")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(receiverId!!).exists()){
                 //   Toast.makeText(applicationContext,p0.child(receiverId!!).child("image").value.toString(),Toast.LENGTH_LONG).show()
                    receivername = p0.child(receiverId!!).child("fullname").value.toString()
                    receiverimg = p0.child(receiverId!!).child("image").value.toString()
                    fullname!!.text =receivername
                    Picasso.get().load(p0.child(receiverId!!).child("image").value.toString()).placeholder(R.drawable.profile).into(profile_img)

                }
                if(p0.child(senderId!!).exists()){
                    sendername = p0.child(senderId!!).child("fullname").value.toString()
                    senderimg = p0.child(senderId!!).child("image").value.toString()


                }
            }


        })

    }

    override fun onStart() {

        super.onStart()
        ref=ref.child("Calls")
        ref.child(receiverId!!).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
             if(!p0.hasChild("Calling") && !p0.hasChild("Ringing" ) && checker!="clicked"){
                 val callinginfo = HashMap<String,Any>()
                 callinginfo["uid"] = senderId!!
                 callinginfo["receiverid"] = receiverId!!

                 ref.child(senderId!!).child("Calling").updateChildren(callinginfo).addOnCompleteListener{task ->
                     if (task.isSuccessful){
                         val ringinginfo = HashMap<String,Any>()
                         ringinginfo["ringinguid"] = senderId!!
                         ringinginfo["uid"] = receiverId!!

                        ref.child(receiverId!!).child("Ringing").updateChildren(ringinginfo)
                     }
                 }

             }
            }

        })

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.child(senderId!!).hasChild("Ringing") && !p0.child(senderId!!).hasChild("Calling")){

                    accept_btn!!.visibility= View.VISIBLE
                }
            }

        })
    }
}
