package com.example.photochat.Fragment
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Adapter.PostAdapter
import com.example.photochat.Adapter.StoryAdapter
import com.example.photochat.AddPostActivity
import com.example.photochat.All_Message_Caht
import com.example.photochat.Model.Post
import com.example.photochat.Model.Story
import com.example.photochat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.posts_layout.*

class HomeFragment : Fragment() {
        private var postAdapter:PostAdapter? =null
    private var postList: MutableList<Post>?=null
    private var storyAdapter : StoryAdapter?=null
    private var storyList : MutableList<Story>?=null
    var recyclerview: RecyclerView? = null
    var recyclerview1: RecyclerView? = null
    private  var  followingList =ArrayList<String>()
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)



        //home recyler view
        recyclerview = view.findViewById(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout =true
        linearLayoutManager.stackFromEnd=true
        recyclerview!!.layoutManager = linearLayoutManager



        postList = ArrayList()
        postAdapter= PostAdapter(context!!,postList!!)!!
        recyclerview!!.adapter=postAdapter

        // story recycler View

        recyclerview1 = view.findViewById(R.id.recycler_view_story)
        val linearLayoutManager2 = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)


        recyclerview1!!.layoutManager = linearLayoutManager2
        storyList = ArrayList()
        storyAdapter = StoryAdapter(context!!,storyList!!)
        recyclerview1!!.adapter = storyAdapter
        RetrieveStory()
        checkFollowing()
        return view
    }
    private fun checkFollowing() {
        val followingRef =FirebaseDatabase.getInstance().reference.child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid).child("Following")
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    followingList.clear()
                    for (snapshot in p0.children) {
                        var id = snapshot.key.toString()
                        followingList.add(id)
                    }
                }
                retrieveAllPost()
            }
        })
    }
    private fun retrieveAllPost() {

        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()

                for(snapshot in p0.children){
                    val post = snapshot.getValue(Post::class.java)

                    for( userid in followingList){

                        if(userid ==(post!!.getpublisher())){
                            var post_id=snapshot.child("postId").value.toString()
                            var imagelink=snapshot.child("image").value.toString()
                            var description=snapshot.child("description").value.toString()
                            var publisher=snapshot.child("publisher").value.toString()
                            postList!!.add(Post(post_id,imagelink,description,publisher))

                        }

                        postAdapter!!.notifyDataSetChanged()
                    }


                }

            }

        })
    }
    private fun RetrieveStory() {
        val storyRef= FirebaseDatabase.getInstance().reference.child("Story")
        storyRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
           storyList?.clear()
                val timeCurrent = System.currentTimeMillis().toString()

                storyList!!.add(Story("",0,0,"",FirebaseAuth.getInstance().currentUser!!.uid))
                //Toast.makeText(activity,storyList.toString(),Toast.LENGTH_SHORT).show()
                storyAdapter!!.notifyDataSetChanged()

              for (id in followingList){

                    var story: Story?=null
                    var countStory =0
                  var story_id =""
                  var imagelink =""
                  var timeend =""
                  var timestart =""
                  var userid =""
                    for (snapshot in p0.child(id).children){
                    if(snapshot.exists())
               {
                    story_id=snapshot.child("storyid").value.toString()
                    imagelink=snapshot.child("imageurl").value.toString()
                    timeend=snapshot.child("timeend").value.toString()
                    timestart=snapshot.child("timestart").value.toString()
                    userid=snapshot.child("userid").value.toString()
                  // Toast.makeText(activity,snapshot.toString(),Toast.LENGTH_SHORT).show()
                  // story = snapshot.getValue<Story>(Story::class.java)
                    if(timeCurrent> timestart && timeCurrent<timeend)
                    {
                     countStory++
                    }
               }
               }
                    if(countStory>0)
                    {
                        storyList!!.add(Story(imagelink,timestart.toLong(),timeend.toLong(),story_id,userid)!!)
                        storyAdapter!!.notifyDataSetChanged()
                    }
            }
            }


    })




}}

