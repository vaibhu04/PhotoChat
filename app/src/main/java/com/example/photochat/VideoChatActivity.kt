package com.example.photochat

import android.content.Intent
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.opentok.android.*
import kotlinx.android.synthetic.main.activity_video_chat.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.jar.Manifest

class VideoChatActivity : AppCompatActivity() ,Session.SessionListener,PublisherKit.PublisherListener {
    private var API_KEY = "46695382"
    val userid = FirebaseAuth.getInstance().currentUser!!.uid
    private var Session_Id = "1_MX40NjY5NTM4Mn5-MTU4NzYzMjQ3NTc1OX5HZi9vSVpXWWE2bFQxbnBQSS9FUnY1TUh-fg"
    private var Token = "T1==cGFydG5lcl9pZD00NjY5NTM4MiZzaWc9MzY1M2YyOTIzZDkwZTVhZWE2MzQxYWUzODliZThkYzU5N2Y0ZjA0ZjpzZXNzaW9uX2lkPTFfTVg0ME5qWTVOVE00TW41LU1UVTROell6TWpRM05UYzFPWDVIWmk5dlNWcFhXV0UyYkZReGJuQlFTUzlGVW5ZMVRVaC1mZyZjcmVhdGVfdGltZT0xNTg3NjMyNTUzJm5vbmNlPTAuODY4ODA3MzY4MTU4MjYyJnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTE1ODc2NTQxNTEmaW5pdGlhbF9sYXlvdXRfY2xhc3NfbGlzdD0="
    private var LOG_TAG = VideoChatActivity::class.java.simpleName
    private var mPublisherViewController: FrameLayout?=null
    private var mSubscriberViewController: FrameLayout?=null
    private var mSession: Session?=null
    private var mPublisher:Publisher?=null
    private var mSubscriber: Subscriber?=null
    companion object{
        const val RC_VIDEO_PERM=124
    }

    //private val TAG
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_chat)

//close button
        close_video_chat_btn?.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().reference.child("Calls")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.child(userid).hasChild("Ringing")){
                        ref.child(userid).child("Ringing").removeValue()
                        if(mPublisher!=null){
                            mPublisher!!.destroy()
                        }
                        if(mSubscriber!=null){
                            mSubscriber!!.destroy()
                        }
                        startActivity(Intent(this@VideoChatActivity,MessageChatActivity::class.java))
                        finish()
                    }
                    if(p0.child(userid).hasChild("Calling")){
                        ref.child(userid).child("Calling").removeValue()

                        if(mPublisher!=null){
                            mPublisher!!.destroy()
                        }
                        if(mSubscriber!=null){
                            mSubscriber!!.destroy()
                        }
                        startActivity(Intent(this@VideoChatActivity,MessageChatActivity::class.java))
                        finish()
                    }
                    else{
                        if(mPublisher!=null){
                            mPublisher!!.destroy()
                        }
                        if(mSubscriber!=null){
                            mSubscriber!!.destroy()
                        }
                        startActivity(Intent(this@VideoChatActivity,MessageChatActivity::class.java))
                        finish()

                    }

                }

            })

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
@AfterPermissionGranted(RC_VIDEO_PERM)
 private  fun requestPermission(){


  var perms= arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.INTERNET)


     if(EasyPermissions.hasPermissions(this,android.Manifest.permission.CAMERA))
         if(EasyPermissions.hasPermissions(this,android.Manifest.permission.INTERNET))
             if(EasyPermissions.hasPermissions(this,android.Manifest.permission.RECORD_AUDIO))

     {
         mPublisherViewController = findViewById(R.id.publisher_container)
         mSubscriberViewController = findViewById(R.id.subsriber_container)

         mSession= Session.Builder(this,API_KEY,Session_Id).build()
         mSession!!.setSessionListener(this)
         mSession!!.connect(Token)
     }
    else{
                 EasyPermissions.requestPermissions(this,"please provide the necessary permissions",
                     RC_VIDEO_PERM,android.Manifest.permission.RECORD_AUDIO)
                 EasyPermissions.requestPermissions(this,"please provide the necessary permissions",
                     RC_VIDEO_PERM,android.Manifest.permission.INTERNET)
                 EasyPermissions.requestPermissions(this,"please provide the necessary permissions",
                     RC_VIDEO_PERM,android.Manifest.permission.CAMERA)
             }

 }

    override fun onStreamDropped(p0: Session?, p1: Stream?) {
        Log.i(LOG_TAG,"Stream Dropped")
if(mSubscriber==null){
    mSubscriber=null
    mSubscriberViewController!!.removeAllViews()
}

    }

    override fun onStreamReceived(p0: Session?, p1: Stream?) {
        Log.i(LOG_TAG,"Stream Received")
    }

    override fun onConnected(p0: Session?) {
        Log.i(LOG_TAG,"Session Connected")
         mPublisher = Publisher.Builder(this).build()
        mPublisher!!.setPublisherListener(this)
        mPublisherViewController!!.addView(mPublisher!!.view)
        if(mPublisher!!.view is GLSurfaceView){
            (mPublisher!!.view as (GLSurfaceView)).setZOrderOnTop(true)
        }
        mSession!!.publish(mPublisher)

    }

    override fun onDisconnected(p0: Session?) {
        Log.i(LOG_TAG,"Stream Disconnected")
    }

    override fun onError(p0: Session?, p1: OpentokError?) {
        TODO("Not yet implemented")
    }

    override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {
        Log.i(LOG_TAG,"Stream Received")
        if(mSubscriber ==null){
            mSubscriber = Subscriber.Builder(this,p1).build()
            mSubscriberViewController!!.addView(mSubscriber!!.view)

        }
    }

    override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: PublisherKit?, p1: OpentokError?) {
        TODO("Not yet implemented")
    }
}
