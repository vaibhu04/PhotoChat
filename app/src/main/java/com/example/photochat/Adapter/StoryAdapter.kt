package com.example.photochat.Adapter

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.AddStory
import com.example.photochat.Model.Story
import com.example.photochat.Model.User
import com.example.photochat.R
import com.example.photochat.StoryActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter(private val mContext: Context,private val mStory: MutableList<Story>): RecyclerView.Adapter<StoryAdapter.ViewHolder>() {



    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){

       var storyImg : CircleImageView ?=null
        var storyAddbtn :CircleImageView?=null
        var Addstory_text: TextView?=null

        var story_username : TextView?=null
        var storyImageSeen : CircleImageView?=null
        init {
            // AddStory and StoryTo SEE
            storyImg = itemView.findViewById(R.id.story_imgae)
            storyAddbtn = itemView.findViewById(R.id.story_plus_btn)
            Addstory_text =itemView.findViewById(R.id.add_story_text)
            storyImageSeen = itemView.findViewById(R.id.story_imgae_seen)
            story_username = itemView.findViewById(R.id.story_username)
        }


    }

    override fun getItemViewType(position: Int): Int {
        if(position ==0)
            return 0
        else return 1

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if(viewType == 0){
            val view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item,parent,false)
            ViewHolder(view)
            }
            else{
                val view = LayoutInflater.from(mContext).inflate(R.layout.story_item,parent,false)
                ViewHolder(view)
            }
        }

    override fun getItemCount(): Int {
     //   Toast.makeText(mContext,mStory.size.toString(),Toast.LENGTH_SHORT).show()
        return mStory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = mStory[position]
        userInfo(holder,story.getUserId(),position)
        if(holder.adapterPosition!==0){
            userInfo(holder,story.getUserId(),position)
          seenStory(holder,story.getUserId())
        }
        if(holder.adapterPosition===0){
            userInfo(holder,story.getUserId(),position)
            myStory(holder.Addstory_text!!,holder.storyAddbtn!!,false,holder)
        }



        holder.itemView.setOnClickListener(){
            if(holder.adapterPosition===0){
                myStory(holder.Addstory_text!!,holder.storyAddbtn!!,true,holder)
            }
            else{
            val intent = Intent(mContext, StoryActivity::class.java)
            intent.putExtra("userId",story.getUserId())
                intent.putExtra("storyId",story.getStoryId())
            mContext.startActivity(intent)}
        }
    }

    private fun userInfo(viewHolder: ViewHolder,userId: String,position: Int) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage())
                        .placeholder(R.drawable.profile).into(viewHolder.storyImg)
                    if(position!=0)
                    {viewHolder.story_username!!.text = user!!.getUsername()
                        Picasso.get().load(user!!.getImage())
                            .placeholder(R.drawable.profile).into(viewHolder.storyImageSeen)

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun seenStory(viewHolder: ViewHolder,userId:String){

        val ref= FirebaseDatabase.getInstance().reference.child("Story").child(userId)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                var i=0
                for(snapshot in p0.children)
                {                if(!snapshot.child("views").child(FirebaseAuth.getInstance().currentUser!!.uid).exists())
                  //  if( System.currentTimeMillis().toString()<snapshot.child("endtime").value!!.toString())
                {
                    i++
                }


                }
                if(i>0){
                    viewHolder.storyImageSeen!!.visibility=View.GONE
                    viewHolder.storyImg!!.visibility=View.VISIBLE
                }
                else{
                    viewHolder.storyImageSeen!!.visibility=View.VISIBLE
                    viewHolder.storyImg!!.visibility=View.GONE

                }
            }

        })

    }

    private fun myStory(textView: TextView,imageView: CircleImageView,click: Boolean,viewHolder: ViewHolder)
    {
            val ref= FirebaseDatabase.getInstance().reference.child("Story")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                var counter = 0
                var story_id =""
                val timecurrent = System.currentTimeMillis().toString()
                for(snapshot in p0.children){
                     story_id=snapshot.child("storyid").value.toString()
                    var imagelink=snapshot.child("imageurl").value.toString()
                    var timeend=snapshot.child("timeend").value.toString()
                    var timestart=snapshot.child("timestart").value.toString()
                    var userid=snapshot.child("userid").value.toString()

                    if(timecurrent>timestart && timecurrent<timeend){
                        counter++
                    }

                }

                if(click){
                    if(counter>0){
                        val alertDialog= AlertDialog.Builder(mContext).create()
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"View Story")
                        {
                            dialoginterface, which: Int ->
                            val intent= Intent(mContext,StoryActivity::class.java)
                            intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                            intent.putExtra("storyId",story_id)
                            mContext.startActivity(intent)
                            dialoginterface.dismiss()
                        }
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Add Story")
                        {
                                dialog: DialogInterface?, which: Int ->
                            val intent= Intent(mContext,AddStory::class.java)
                            intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(intent)
                            dialog!!.dismiss()
                        }
                        alertDialog.show()



                    }
                    else{
                        if(counter>0){
                            textView.text= "My Story"
                            imageView.visibility= View.GONE
                            val intent= Intent(mContext,AddStory::class.java)
                            intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(intent)

                        }
                        else{
                            textView.text= "Add Story"
                            imageView.visibility= View.VISIBLE
                            val intent= Intent(mContext,AddStory::class.java)
                            intent.putExtra("userId",FirebaseAuth.getInstance().currentUser!!.uid)
                            mContext.startActivity(intent)
                        }

                    }
                }
            }

        })

    }

    }


