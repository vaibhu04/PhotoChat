package com.example.photochat.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.AccountSettingsActivity
import com.example.photochat.Adapter.MyImageAdapter
import com.example.photochat.All_Message_Caht
import com.example.photochat.Model.Post
import com.example.photochat.Model.User

import com.example.photochat.R
import com.example.photochat.ShowUserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.pro_image_profile_frag
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private var postlist: MutableList<Post>? = null
    private var myImageAdapter: MyImageAdapter? = null

    private var postlistSavesImg: MutableList<Post>? = null
    private var myImageAdapterSavesImg: MyImageAdapter? = null
    private var mySavedImgUrl: MutableList<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)



        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }


        if (profileId == firebaseUser.uid) {
            view.edit_account_settings_btn.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowingButtonStatus()
        }
        Myphotos()
        //RecyclerView for Posted iMages
        var recyclervierUploadImages: RecyclerView
        recyclervierUploadImages = view.findViewById(R.id.recycler_view_upload_pic)
        recyclervierUploadImages.setHasFixedSize(true)
        var linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclervierUploadImages.layoutManager = linearLayoutManager
        postlist = ArrayList()
        myImageAdapter = MyImageAdapter(context!!, postlist!!)!!
        recyclervierUploadImages.adapter = myImageAdapter


        //RecyclerView for Saved Images
        var recyclerviewsavedImg: RecyclerView
        recyclerviewsavedImg = view.findViewById(R.id.recycler_view_save_pic)
        recyclerviewsavedImg.setHasFixedSize(true)
        val linearLayoutManager1: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerviewsavedImg.layoutManager = linearLayoutManager1
        postlistSavesImg = ArrayList()
        myImageAdapterSavesImg = MyImageAdapter(context!!, postlistSavesImg!!)
        recyclerviewsavedImg.adapter = myImageAdapterSavesImg

        recyclervierUploadImages.visibility = View.VISIBLE
        recyclerviewsavedImg.visibility = View.GONE
        //
        var total_follower: TextView = view.findViewById(R.id.total_followers)
        total_follower.setOnClickListener() {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id", profileId!!)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }
        //
        var tottl_following: TextView = view.findViewById(R.id.total_following)
        tottl_following.setOnClickListener() {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }
        //
        var SeeuploadPIC: ImageView
        SeeuploadPIC = view.findViewById(R.id.images_grid_view_btn)
        SeeuploadPIC!!.setOnClickListener() {

            recyclervierUploadImages.visibility = View.VISIBLE
            recyclerviewsavedImg.visibility = View.GONE
        }
        var SeeSavedImg: ImageView
        SeeSavedImg = view.findViewById(R.id.images_save_btn)
        SeeSavedImg!!.setOnClickListener() {
            recyclervierUploadImages.visibility = View.GONE
            recyclerviewsavedImg.visibility = View.VISIBLE
        }

        view.edit_account_settings_btn.setOnClickListener {
            val getButtonText = view.edit_account_settings_btn.text.toString()

            when {
                getButtonText == "Edit Profile" -> startActivity(
                    Intent(
                        context,
                        AccountSettingsActivity::class.java
                    )
                )

                getButtonText == "Follow" -> {

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                        getFollowings()
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                        getFollowers()
                    }
                    AddNotification()

                }

                getButtonText == "Following" -> {

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                        getFollowings()
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    getFollowers()
                    }

                }
            }

        }
        findtotalPost()
        getFollowers()
        getFollowings()
        userInfo()
        getSavedImgUrl()
        //     getSavedimage()
        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {

        val followingRef = firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(profileId).exists()) {

                        view?.edit_account_settings_btn?.text = "Following"
                    } else {
                        view?.edit_account_settings_btn?.text = "Follow"
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }


    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun getFollowings() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view?.total_following?.text = ((p0.childrenCount) - 1).toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(p0.child("image").value.toString())
                        .placeholder(R.drawable.profile).into(pro_image_profile_frag)
                    view?.profile_fragment_username?.text = user!!.getUsername()
                    view?.full_name_profile_frag?.text = user.getFullname()
                    view?.bio_profile_frag?.text = user.getBio()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun Myphotos() {
        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for (snapshot in p0.children) {

                    val post = snapshot.getValue<Post>(Post::class.java)
                    if (snapshot.child("publisher").value.toString() == profileId!!) {

                        (postlist as ArrayList<Post>).add(post!!)
                    }
                    myImageAdapter!!.notifyDataSetChanged()
                    Collections.reverse(postlist)
                }

            }

        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    private fun findtotalPost() {
        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var i: Int = 0
                for (snapshot in p0.children) {
                    val post = snapshot.getValue<Post>(Post::class.java)
                    if (snapshot.child("publisher").value.toString() == profileId) {
                        i++
                    }

                }
                total_posts.text = i!!.toString()

            }

        })
    }

    private fun getSavedImgUrl() {
        mySavedImgUrl = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                mySavedImgUrl?.clear()
                if (p0.exists())
                    for (snapshot in p0.children) {
                        mySavedImgUrl!!.add(snapshot.key!!)
                    }
                getSavedimage()
            }
        })
    }

    private fun getSavedimage() {
        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                postlistSavesImg?.clear()
                for (snapshot in p0.children) {

                    val post = snapshot.getValue<Post>(Post::class.java)
                    for (id in mySavedImgUrl!!) {
                        if (id == post!!.getpostId()) {

                            var post_id = snapshot.child("postId").value.toString()
                            var imagelink = snapshot.child("image").value.toString()
                            var description = snapshot.child("desciption").value.toString()
                            var publisher = snapshot.child("publisher").value.toString()
                            postlistSavesImg!!.add(
                                Post(
                                    post_id!!,
                                    imagelink!!,
                                    description!!,
                                    publisher!!
                                )
                            )
                            myImageAdapterSavesImg?.notifyDataSetChanged()
                        }
                    }
                }
            }

        })
    }

    private fun AddNotification() {
        val ref = FirebaseDatabase.getInstance().reference.child("Notifications").child(profileId)
        var Notify = HashMap<String, Any>()
        Notify["userId"] = firebaseUser!!.uid
        Notify["text"] = "Started Following You.."
        Notify["postId"] = ""
        Notify["ispost"] = false
        Notify["seen"]=false
        Notify["notificationId"]=ref.key.toString()
        ref.push().setValue(Notify)


    }

}