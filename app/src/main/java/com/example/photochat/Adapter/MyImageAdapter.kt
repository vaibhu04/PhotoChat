package com.example.photochat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Fragment.PostDetailsFragment
import com.example.photochat.Model.Post
import com.example.photochat.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MyImageAdapter(private val mContext: Context,private val mPost: MutableList<Post>):RecyclerView.Adapter<MyImageAdapter.ViewHolder?>() {


    inner class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        var image_post: ImageView
        init {
            image_post = itemView.findViewById(R.id.post_images)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(mContext).inflate(R.layout.images_items_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     val post = mPost[position]

        holder.image_post.setOnClickListener(){
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postId",post.getpostId())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,PostDetailsFragment()).commit()
        }
        val ref =FirebaseDatabase.getInstance().reference.child("Posts").child(post.getpostId()).child("image")
        //Toast.makeText(mContext,post.getimaage(),Toast.LENGTH_SHORT).show()
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                Picasso.get().load(p0.value.toString()).placeholder(R.drawable.profile).into(holder.image_post)
            }

        })
  //      Picasso.get().load(post.getimaage()).placeholder(R.drawable.profile).into(holder.image_post)

    }
}