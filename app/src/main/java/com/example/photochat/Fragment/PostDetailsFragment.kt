package com.example.photochat.Fragment

import android.content.Context
import android.os.Bundle
import android.preference.Preference
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Adapter.PostAdapter
import com.example.photochat.Model.Post

import com.example.photochat.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class PostDetailsFragment : Fragment() {

    private var postList : MutableList<Post>?=null
    private var postId: String?=""
    private var postadapter:PostAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view= inflater.inflate(R.layout.fragment_post_details, container, false)
        retreivePost()
        var recyclerView : RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview_see_posts)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager : LinearLayoutManager
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        postList = ArrayList()
        postadapter = PostAdapter(context!!,postList!!)
        recyclerView.adapter=postadapter
        val Preference = context!!.getSharedPreferences("PREFS",Context.MODE_PRIVATE)
        if(Preference!=null){
            postId = Preference.getString("postId","none")
        }
        return view
    }
private fun retreivePost(){
    val ref= FirebaseDatabase.getInstance().reference.child("Posts").child(postId!!)
    ref.addValueEventListener(object :ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            postList?.clear()
            if(p0.exists()){
                for(snapshot in p0.children){
                    if(snapshot.child("postId").value.toString()==postId)
                    {
                    val posts = snapshot.getValue<Post>(Post::class.java)
                    postList?.add(posts!!)
                    postadapter!!.notifyDataSetChanged()}
                }

            }
        }

    })
}
}
