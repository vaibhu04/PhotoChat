package com.example.photochat

import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Adapter.ChatAdapter
import com.example.photochat.Model.Chat
import com.example.photochat.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {
    var messagerId: String? = null
    var firebaseUser: FirebaseUser? = null
        var storagePostPiceRef : StorageReference ?=null
    var mChatAdapter: ChatAdapter?=null
    var mChatList: MutableList<Chat>?=null
    var callby : String?=null
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        storagePostPiceRef = FirebaseStorage.getInstance().reference.child("Chat Picture")
        val intent = intent
        messagerId = intent.getStringExtra("userId")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        //check for video call
        //checkcalls()
        //recyclerview

        recyclerView= findViewById(R.id.message_chat_recycllerview)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager =LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        recyclerView.layoutManager=linearLayoutManager


        video_call_btn?.setOnClickListener{
            val intent = Intent(this@MessageChatActivity, CallingActivity::class.java)
            intent.putExtra("receiverId",messagerId)
            startActivity(intent)

        }

// add circularimage and fullname
        val reference  =  FirebaseDatabase.getInstance().reference.child("Users").child(messagerId!!)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
              if(p0.exists()){
                  val user = p0.getValue<User>(User::class.java)
                  Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(messge_chat_image)
                  message_chat_fullname.text = user.getFullname()
                  retrievemessages()
              }
            }

        })

        ///send image button..
        add_img_message_chat.setOnClickListener{
            CropImage.activity().start(this@MessageChatActivity)

        }

        send_mess_btn?.setOnClickListener {
            val message = mess_txt.text.toString()
            mess_txt.text.clear()
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(applicationContext, "Wrrite SomeThing..", Toast.LENGTH_SHORT).show()
            } else {
                    sendMessagetoUser(firebaseUser!!.uid,messagerId,message)
            }
        }
    }

    private fun retrievemessages() {
        mChatList= ArrayList()
        val ref= FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                mChatList!!.clear()
                for(snapshot in p0.children){
                    val chat = snapshot.getValue<Chat>(Chat::class.java)
                    if((chat!!.getsenderid()==firebaseUser!!.uid && chat!!.getReceiverId()==messagerId )||
                            (chat!!.getsenderid()==messagerId && chat!!.getReceiverId()==firebaseUser!!.uid )){

                        mChatList!!.add(chat)


                    }
                    mChatAdapter= ChatAdapter(this@MessageChatActivity,mChatList!!)
                    recyclerView.adapter = mChatAdapter
                    mChatAdapter!!.notifyDataSetChanged()
                }
            }

        })

    }

    private fun sendMessagetoUser(uid: String, messagerId: String?, message: String) {

        val ref = FirebaseDatabase.getInstance().reference
        val messagekey = ref.push().key

        val messageHashMap = HashMap<String,Any>()
        messageHashMap["senderId"]= uid
        messageHashMap["message"]= message
        messageHashMap["receiverId"]= messagerId!!
        messageHashMap["isseen"]= false
        messageHashMap["messageId"]= messagekey!!
        messageHashMap["url"]= ""

        //Toast.makeText(this, "Your image is sent successfully.", Toast.LENGTH_LONG).show()
        ref.child("Chats").child(messagekey!!).setValue(messageHashMap).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val chatListRef = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid).child(messagerId)

                chatListRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(!p0.exists()){
                            chatListRef.child("id").setValue(messagerId)
                        }

                        val chatListReceiverRef = FirebaseDatabase.getInstance().reference.child("ChatList").child(messagerId).child(firebaseUser!!.uid)
                        chatListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                    }

                })




                val reference  =  FirebaseDatabase.getInstance().reference.child("Users").child(uid)
                //implement push notifications here
            }

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== AppCompatActivity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
           val imageUri= result.uri

            var progressdialog:ProgressDialog = ProgressDialog(this)
            progressdialog.setTitle("Sending image")
            progressdialog.setMessage("Please Wait..")
            progressdialog.show()
            progressdialog.setCanceledOnTouchOutside(false)

            val fileRef = storagePostPiceRef!!.child(System.currentTimeMillis().toString()+".jpg")
            var uploadTask : StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                        progressdialog.dismiss()
                    }
                }
                progressdialog.dismiss()
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    val ref= FirebaseDatabase.getInstance().reference
                    val downloadUrl = task.result
                    val url  = downloadUrl.toString()
                    val messagekey = ref.push().key

                    val messageHashMap = HashMap<String,Any>()
                    messageHashMap["senderId"]= firebaseUser!!.uid
                    messageHashMap["message"]= "sent you an image"
                    messageHashMap["receiverId"]= messagerId!!
                    messageHashMap["isseen"]= false
                    messageHashMap["messageId"]= messagekey!!
                    messageHashMap["url"]= url
                    ref.child("Chats").child(messagekey!!).setValue(messageHashMap)


                }}



                }
    }
    private fun checkcalls(){
        val ref = FirebaseDatabase.getInstance().reference.child("Calls").child(firebaseUser!!.uid).child("Ringing")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("ringinguid")){
                    callby=p0.child("ringinguid").value.toString()
                    val intent = Intent(this@MessageChatActivity,CallingActivity::class.java)
                    intent.putExtra("receiverId",callby)
                    startActivity(intent)
                }
            }

        })
    }

    override fun onStart() {
        checkcalls()
        super.onStart()
    }
}
