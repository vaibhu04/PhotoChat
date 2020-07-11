package com.example.photochat

import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddStory : AppCompatActivity() {

    private var myUri = ""
    private var imageUri: Uri?= null
    private var storageStoryPiceRef : StorageReference?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        storageStoryPiceRef = FirebaseStorage.getInstance().reference.child("Story Pictures")

        CropImage.activity().setAspectRatio(9,16).start(this@AddStory)





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== AppCompatActivity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUri= result.uri
            UploadImage()

        }
    }

    private fun UploadImage() {
        when{
            imageUri==null -> Toast.makeText(this, "Please Write Sometehing About the Post..", Toast.LENGTH_LONG).show()


            else-> {
                var progressdialog: ProgressDialog = ProgressDialog(this)
                progressdialog.setTitle("Add Story")
                progressdialog.setMessage("Please Wait, we are Updating Your Stories..")
                progressdialog.show()
                progressdialog.setCanceledOnTouchOutside(false)

                val fileRef = storageStoryPiceRef!!.child(System.currentTimeMillis().toString()+".jpg")
                var uploadTask : StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressdialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if(task.isSuccessful){

                        val downloadUrl = task.result
                        myUri = downloadUrl.toString()



                        val ref = FirebaseDatabase.getInstance().reference.child("Story").child(FirebaseAuth.getInstance().currentUser!!.uid)
                        val StoryId = ref.push().key!!
                        val timeEnd = System.currentTimeMillis()+86400000
                        val StoryMap = HashMap<String,Any>()

                        StoryMap["storyid"] = StoryId
                        StoryMap["timeend"] = timeEnd
                        StoryMap["timestart"] = ServerValue.TIMESTAMP
                        StoryMap["imageurl"] = myUri
                        StoryMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid

                        ref.child(StoryId).updateChildren(StoryMap)
                        Toast.makeText(this, "Your Story is Updated successfully.", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@AddStory, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressdialog.dismiss()






                    }
                    else
                    {
                        progressdialog.dismiss()
                    }

                }
            }
        }

    }

}
