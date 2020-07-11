package com.example.photochat.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import com.example.photochat.*
import com.example.photochat.Fragment.PostDetailsFragment
import com.example.photochat.Fragment.ProfileFragment
import com.example.photochat.Model.Post
import com.example.photochat.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Tag
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import androidx.recyclerview.widget.RecyclerView as RecyclerView1

class PostAdapter( private var mContext : Context, private var mPost : List<Post>): RecyclerView1.Adapter<PostAdapter.ViewHolder>() {
private var firebaseUser: FirebaseUser ? =null

    inner class ViewHolder(@NonNull itemView: View): RecyclerView1.ViewHolder(itemView){

        var profileImage :CircleImageView
        var postImage :ImageView
        var likeImage: ImageView
        var commentImage :ImageView
        var saveImage : ImageView
        var userName: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView
        var sent_btn : ImageView
        init {
            profileImage =itemView.findViewById(R.id.user_profile_image_search)
            postImage =itemView.findViewById(R.id.post_image_home)
            likeImage =itemView.findViewById(R.id.post_image_like_btn)
            commentImage =itemView.findViewById(R.id.post_image_comment_btn)
            saveImage =itemView.findViewById(R.id.post_save_comment_btn)
            userName =itemView.findViewById(R.id.user_name_search)
            likes =itemView.findViewById(R.id.likes)
            publisher =itemView.findViewById(R.id.publisher)
            description =itemView.findViewById(R.id.description)
            comments =itemView.findViewById(R.id.comments)
            sent_btn =itemView.findViewById(R.id.send_btn)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val  post = mPost[position]

        if(firebaseUser!!.uid==post.getpublisher())
        {
            holder.sent_btn.visibility=View.GONE
        }
        holder.likes.setOnClickListener(){
            val intent = Intent(mContext,ShowUserActivity::class.java)
            intent.putExtra("id",post.getpostId())
            intent.putExtra("title","likes")
            mContext.startActivity(intent)
        }

        holder.sent_btn.setOnClickListener{

            val intent = Intent(mContext,MessageChatActivity::class.java)
            intent.putExtra("userId",post.getpublisher())
            mContext.startActivity(intent)

        }


        checkStatusOfCheckedPic(post.getpostId(),holder.saveImage)
       // Picasso.get().load(post!!.getimaage()!!).into(holder.postImage)
        val ref =FirebaseDatabase.getInstance().reference.child("Posts").child(post.getpostId()).child("image")

        holder.saveImage.setOnClickListener(){
            if(holder.saveImage.tag=="Saved"){
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).child(post.getpostId()).removeValue()
                
            }
            else{
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).child(post.getpostId()).setValue(true)

            }
        }

        holder.postImage.setOnClickListener{
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postId",post.getpostId())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                PostDetailsFragment()
            ).commit()
        }
        holder.publisher.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("profileId",post.getpublisher())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                ProfileFragment()
            ).commit()
        }
        holder.profileImage.setOnClickListener{
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("profileId",post.getpublisher())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                ProfileFragment()
            ).commit()
        }

        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                    Picasso.get().load(p0.value.toString()).placeholder(R.drawable.profile).into(holder.postImage)
            }

        })
        isLiked(post.getpostId(),holder.likeImage)
        NumberOfLikes(post.getpostId(),holder.likes)
        getTotalComments(post.getpostId(),holder.comments)
        holder.commentImage?.setOnClickListener(){
            val intentcomment = Intent(mContext,CommentsActivity::class.java)
            intentcomment.putExtra("postId",post.getpostId())
            intentcomment.putExtra("publisherId",post.getpublisher())
            mContext.startActivity(intentcomment)

        }
        holder.comments.setOnClickListener(){
            val intentcomment = Intent(mContext,CommentsActivity::class.java)
            intentcomment.putExtra("postId",post.getpostId())
            intentcomment.putExtra("publisherId",post.getpublisher())
            mContext.startActivity(intentcomment)

        }
        if(post.getdescription().equals("")){
            holder.description.visibility =View.GONE

        }
        else{
            holder.description.visibility =View.VISIBLE
            holder.description.text=FirebaseDatabase.getInstance().reference.child("Posts").child(post.getpostId()).toString()
            val firebase = FirebaseAuth.getInstance().currentUser
            val likeNo = FirebaseDatabase.getInstance().reference.child("Posts").child(post.getpostId())
            likeNo.addValueEventListener( object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {

                    if(p0.exists())
                    {  val post = p0.getValue(Post::class.java)
                       holder.description.text=p0.child("desciption").value.toString()


                    }
                }

            })
        }
        holder.likeImage.setOnClickListener {
            if(holder.likeImage.tag =="Like"){
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getpostId())
                    .child(firebaseUser!!.uid).setValue(true)
                NumberOfLikes(post.getpostId(),holder.likes)
            AddNotification(post.getpublisher(),post.getpostId())
            }
            else{
                FirebaseDatabase.getInstance().reference.child("Likes").child(post.getpostId()).child(firebaseUser!!.uid).removeValue()
                NumberOfLikes(post.getpostId(),holder.likes)
            }
        }
        publisherInfo(holder.profileImage,holder.userName,holder.publisher,post.getpublisher())

    }


    private fun NumberOfLikes(getpostId: String, likes: TextView) {
        val firebase = FirebaseAuth.getInstance().currentUser
        val likeNo = FirebaseDatabase.getInstance().reference.child("Likes").child(getpostId)
        likeNo.addValueEventListener( object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                likes.text = p0.childrenCount.toString() + " likes"
            }

        })
    }
    private fun getTotalComments(  postId:String, comments:TextView){
        val ref = FirebaseDatabase.getInstance().reference.child("Comments").child(postId!!)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
            comments.text = "view all "+p0.childrenCount.toString() + " comments.."
            }

        })
    }
    private fun isLiked(getpostId: String, likeImage: ImageView) {
        val firebseRef = FirebaseAuth.getInstance().currentUser
        val LikeRef = FirebaseDatabase.getInstance().reference.child("Likes").child(getpostId)
        LikeRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(firebseRef!!.uid).exists()){
                likeImage.setImageResource(R.drawable.heart_clicked)
                    likeImage.tag ="Liked"
                }
                else{
                    likeImage.setImageResource(R.drawable.heart_not_clicked)
                    likeImage.tag ="Like"
                }
            }

        })
    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, getpublisher: String)
    {
        val useref = FirebaseDatabase.getInstance().reference.child("Users").child(getpublisher)
        useref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
               val user = p0.getValue<User>(User::class.java)
                Picasso.get().load(p0.child("image").value.toString()).placeholder(R.drawable.profile).into(profileImage)
                userName.text = (p0.child("username").value.toString())
                publisher.text = (p0.child("fullname").value.toString())
            }

        })

    }

    private fun checkStatusOfCheckedPic(postId: String, imageView: ImageView){
        val ref = FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.child(postId).exists()){
                    imageView.tag="Saved"
                    imageView.setImageResource(R.drawable.save_large_icon)
                }
                else{
                    imageView.tag="Save"
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                }
            }

        })
    }

    private fun AddNotification(userId: String, postId: String){
        val ref = FirebaseDatabase.getInstance().reference.child("Notifications").child(userId)
        var Notify =HashMap<String,Any>()
        Notify["userId"]= firebaseUser!!.uid
        Notify["text"]= "Liked Your Post.."
        Notify["postId"]= postId
        Notify["ispost"]= true
        Notify["seen"]=false
        ref.push().setValue(Notify)

    }
}

