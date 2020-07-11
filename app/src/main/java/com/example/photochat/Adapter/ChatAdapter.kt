package com.example.photochat.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.CallingActivity
import com.example.photochat.Model.Chat
import com.example.photochat.Model.ChatList
import com.example.photochat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.message_item_left.view.*

class ChatAdapter(mContext: Context, mChatList: List<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
    private val mContext: Context
    private val mChatList: List<Chat>


    init {
        this.mContext = mContext
        this.mChatList = mChatList

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var show_text_mess: TextView? = null
        var left_image_vie: ImageView? = null
        var right_image_vie: ImageView? = null
        var text_seen: TextView? = null
        var video_call_btn: ImageView?=null

        init {
            show_text_mess = itemView.findViewById(R.id.show_text_message)
            left_image_vie = itemView.findViewById(R.id.left_image_view)
            right_image_vie = itemView.findViewById(R.id.right_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            video_call_btn = itemView.findViewById(R.id.video_call_btn)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            //Toast.makeText(mContext,position.toString(),Toast.LENGTH_SHORT).show()
        if (position == 0) {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            return ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            return ViewHolder(view)

        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val chat: Chat = mChatList[position]


        //video call btn
        //video call

        //fetch image string
        var imageurl =""
        val ref= FirebaseDatabase.getInstance().reference.child("Chats").child(chat.getmessageId())
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val chats = p0.getValue(Chat::class.java)
                   // Toast.makeText(mContext,p0.child("url").value.toString(),Toast.LENGTH_SHORT).show()
                    imageurl = p0.child("url").value.toString()

                    //image message
                    if (imageurl == "") {

                        holder!!.show_text_mess!!.text = chat.getmessage()

                    }
                    //text message
                    else {

                        //Toast.makeText(mContext,chat.getImage(),Toast.LENGTH_SHORT).show()
                        if (chat.getsenderid().equals(firebaseUser)) {
                            holder.show_text_mess!!.visibility = View.GONE
                            holder.right_image_vie!!.visibility = View.VISIBLE
                            Picasso.get().load(imageurl).placeholder(R.drawable.profile)
                                .into(holder.right_image_vie)
                        } else {
                            holder.show_text_mess!!.visibility = View.GONE
                            holder.left_image_vie!!.visibility = View.VISIBLE
                            Picasso.get().load(imageurl).placeholder(R.drawable.profile)
                                .into(holder.left_image_vie) }
                    }
                }
            }

        })





        //seen unseen feature
        if (position == mChatList.size - 1) {
            if (chat.getisSEen()) {
                holder.text_seen!!.text = "Seen"
                if (chat.getmessage() == "sent you an image" && chat.getImage() != "") {
                    val lp: RelativeLayout.LayoutParams? =
                        holder!!.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }
            } else {
                holder.text_seen!!.text = "Sent"
                if (chat.getmessage() == "sent you an image" && chat.getImage() != "") {
                    val lp: RelativeLayout.LayoutParams? =
                        holder!!.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = lp
                }


            }
        } else {
            holder.text_seen!!.visibility = View.GONE
        }
    }


    override fun getItemViewType(position: Int): Int {


        if (firebaseUser == mChatList[position].getsenderid())
            return 0
        else return 1
    }
}