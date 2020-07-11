package com.example.photochat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Model.Comment
import com.example.photochat.Model.User
import com.example.photochat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdapter(private val mContext: Context,private val mCommnets: MutableList<Comment>):RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R .layout.comments_items_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mCommnets!!.size
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) {
       val comment = mCommnets[position]
        val ref = FirebaseDatabase.getInstance().reference.child("Comments")
        holder.commentDesc.text = comment.getComment()
        getUserInfoforComment(holder.commentPublisher,holder.commentImage,comment.getpublisherId())

    }

    private fun getUserInfoforComment(commentPublisher: TextView, commentImage: CircleImageView, getpublisherId: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(getpublisherId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
              if(p0.exists())
                {
               val user = p0.getValue<User>(User::class.java)

               Picasso.get().load(p0.child("image").value.toString()).placeholder(R.drawable.profile).into(commentImage)
                    commentPublisher.text=p0.child("fullname").value.toString()

                }
            }

        })
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var commentImage: CircleImageView
        var commentPublisher: TextView
        var commentDesc: TextView


        init {
            commentImage=itemView.findViewById(R.id.commenter_image)
            commentPublisher=itemView.findViewById(R.id.comment_publisher_name)
            commentDesc = itemView.findViewById(R.id.comment_desc)


        }
    }
}