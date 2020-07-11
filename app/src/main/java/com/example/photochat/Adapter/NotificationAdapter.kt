package com.example.photochat.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.photochat.Fragment.PostDetailsFragment
import com.example.photochat.Fragment.ProfileFragment
import com.example.photochat.Model.Notification
import com.example.photochat.Model.Post
import com.example.photochat.Model.User
import com.example.photochat.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class NotificationAdapter(private var mContext: Context,private var mNotification :MutableList< Notification>): Adapter<NotificationAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){

        var profileImage: CircleImageView
        var publisher : TextView
        var description : TextView
        var postImg:ImageView
        init {
            profileImage = itemView.findViewById(R.id.notification_profile_image)
            publisher = itemView.findViewById(R.id.notification_publisher_name)
            description = itemView.findViewById(R.id.notification_desc)
            postImg=itemView.findViewById(R.id.postImg_notification)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_items_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotification!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notification = mNotification[position]
        userInfofornotification(notification.getuserId(),holder.profileImage,holder.publisher)
        if(notification.getisPost()==true)
        {
            holder.postImg.visibility = View.VISIBLE
            getPostImagefornotification(notification.getPostId(),holder.postImg)
        }

        else holder.postImg.visibility = View.GONE


        //

        holder.description.text = notification.getText()

        holder.itemView.setOnClickListener(){
            if(notification.getisPost()){
                val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("postId",notification.getPostId())
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    PostDetailsFragment()
                ).commit()
            }else{
                val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                editor.putString("profileId",notification.getuserId())
                editor.apply()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                    ProfileFragment()
                ).commit()
            }
        }

    }
    private fun userInfofornotification(profileId: String, imageView: CircleImageView,profilename:TextView)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(p0.child("image").value.toString()).placeholder(R.drawable.profile).into(imageView)

                    profilename?.text = user!!.getFullname()

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun getPostImagefornotification(postId: String, imageView: ImageView) {
        val ref = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user = p0.getValue<Post>(Post::class.java)

                    Picasso.get().load(p0.child("image").value.toString()).placeholder(R.drawable.profile).into(imageView) }
            }

        })
    }
}