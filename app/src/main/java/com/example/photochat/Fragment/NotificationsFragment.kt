package com.example.photochat.Fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photochat.Adapter.NotificationAdapter
import com.example.photochat.Model.Notification
import com.example.photochat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.posts_layout.*


class NotificationsFragment : Fragment() {
private var notificationList : MutableList<Notification>? = null
    private var notificationAdapter: NotificationAdapter?=null
    private var firebaseUser: FirebaseUser?=null
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_notifications, container, false)

        //
        var recyclerView: RecyclerView
        recyclerView= view.findViewById(R.id.recycler_view_notifications)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager =LinearLayoutManager(context)

firebaseUser = FirebaseAuth.getInstance().currentUser
        notificationList=ArrayList()
        notificationAdapter = NotificationAdapter(context!!,notificationList!!)
        recyclerView.adapter = notificationAdapter

        readAllNotifications()
return view
    }

    private fun readAllNotifications() {
        val ref= FirebaseDatabase.getInstance().reference.child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    notificationList?.clear()
                    for( snapshot in p0!!.children) {
//                        Toast.makeText(context,p0.toString(),Toast.LENGTH_SHORT).show()

//                        val notify = snapshot.getValue<Notification>(Notification::class.java)


                        val publisher = snapshot.child("userId").value.toString()
                        val ispost1 = snapshot.child("ispost").value.toString()
                        val postId = snapshot.child("postId").value.toString()
                        val text = snapshot.child("text").value.toString()
                        val Seen = snapshot.child("seen").value.toString()


                       if(publisher != firebaseUser!!.uid)
                       notificationList?.add(Notification(publisher,text,postId,ispost1.toBoolean(),true)!!)


                    }

                    notificationList!!.reverse()
                    notificationAdapter!!.notifyDataSetChanged()


                    }
            }

        })
    }


}