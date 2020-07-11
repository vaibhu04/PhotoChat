package com.example.photochat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.photochat.Model.Story
import com.example.photochat.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_story.*

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {

    var storyId :String =""
    var userId : String=""
    var CurrentUser : String =""
    var imageList : List<String>? = null
    var storyIdList : List<String>?= null
    var counter = 0
    var storiesProgressView: StoriesProgressView ?=null
    var pressTime = 0L
    var Limit = 500L

    private val OnTouchListener = View.OnTouchListener{view, motionEvent->
    when(motionEvent.action){

        MotionEvent.ACTION_DOWN->{
        pressTime= System.currentTimeMillis()
            storiesProgressView!!.pause()
            return@OnTouchListener false
        }
            MotionEvent.ACTION_UP->{
            val now = System.currentTimeMillis()
                storiesProgressView!!.resume()
                return@OnTouchListener Limit< now-pressTime
            }
    }


        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)
CurrentUser = FirebaseAuth.getInstance().currentUser!!.uid

        userId = intent.getStringExtra("userId")
        storyId= intent.getStringExtra("storyId")
        storiesProgressView = findViewById(R.id.stories_prgress)

        layout_delete.visibility =View.GONE
        layout_seen.visibility =View.GONE
        if(userId == CurrentUser){
    layout_delete.visibility =View.VISIBLE
    layout_seen.visibility =View.VISIBLE
}

        getStories()
        userInfo(userId)

        val reverse : View = findViewById(R.id.reverse)
        reverse.setOnClickListener{
            storiesProgressView!!.reverse()
        }
        reverse.setOnTouchListener(OnTouchListener)
        val skip : View = findViewById(R.id.skip)
        skip.setOnClickListener{
            storiesProgressView!!.skip()
        }
        skip.setOnTouchListener(OnTouchListener)



        //
        seen_number.setOnClickListener(){
            val intent = Intent(this@StoryActivity, ShowUserActivity::class.java)
            intent.putExtra("id",userId)
            intent.putExtra("storyId",storyIdList!![counter])
            intent.putExtra("title","views")
            startActivity(intent)
        }

        delete_stories.setOnClickListener {
            val ref= FirebaseDatabase.getInstance().reference.child("Story").child(userId).child(storyIdList!![counter])

            ref.removeValue().addOnCompleteListener{task->
                if (task.isSuccessful)
                    Toast.makeText(applicationContext,"Deleted...",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getStories(){
        val ref=  FirebaseDatabase.getInstance().reference.child("Story").child(userId)

        imageList = ArrayList()
        storyIdList =ArrayList()

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                (imageList as ArrayList<String>).clear()
                (storyIdList as ArrayList<String>).clear()
                if(p0.exists()){
                    for(snapshot in p0.children)
                    {
                     //   val story = snapshot.getValue<Story>(Story::class.java)
                        val Currenttime = System.currentTimeMillis()

                        if(Currenttime.toString() > snapshot.child("timestart").value.toString() && Currenttime.toString()< snapshot.child("timeend").value.toString())
                        {
                            (imageList as ArrayList<String>).add(snapshot.child("imageurl").value.toString())
                            (storyIdList as ArrayList<String>).add(snapshot.child("storyid").value.toString())

}

                }
                    storiesProgressView!!.setStoriesCount((imageList as ArrayList<String>).size)
                    storiesProgressView!!.setStoryDuration(6000L)
                    storiesProgressView!!.setStoriesListener(this@StoryActivity)
                    storiesProgressView!!.startStories(counter)
                    Picasso.get().load(imageList!!.get(counter)).placeholder(R.drawable.profile).into(image_story)
                    addviewToStory(storyIdList!!.get(counter))
                    seenNumber(storyIdList!!.get(counter))
                }
            }

        })
    }
    private fun userInfo(userId: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(p0.child("image").value.toString())
                        .placeholder(R.drawable.profile).into(story_profile_img)
                    story_profile_username.text = user!!.getUsername()

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun addviewToStory(storyId: String){
        FirebaseDatabase.getInstance().reference.child("Story").child(userId).child(storyId).child("views").child(CurrentUser).setValue(true)
    }

    private fun seenNumber(storyId : String){
        val ref= FirebaseDatabase.getInstance().reference.child("Story").child(userId).child(storyId).child("views")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                seen_number.text = " "+ p0.childrenCount
            }

        })
    }

    override fun onComplete() {
       finish()

    }

    override fun onPrev() {
        if(counter-1<0) return
        Picasso.get().load(imageList!![--counter]).placeholder(R.drawable.profile).into(image_story)

        seenNumber(storyIdList!!.get(counter))

    }

    override fun onNext() {
        Picasso.get().load(imageList!![++counter]).placeholder(R.drawable.profile).into(image_story)
        addviewToStory(storyIdList!!.get(counter))
        seenNumber(storyIdList!!.get(counter))

    }

    override fun onDestroy() {

        super.onDestroy()
        storiesProgressView!!.destroy()
    }

    override fun onResume() {
        super.onResume()
        storiesProgressView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        storiesProgressView!!.pause()
    }
}
