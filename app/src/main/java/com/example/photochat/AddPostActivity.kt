package com.example.photochat

import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {
    private var myUri = ""
    private var imageUri:Uri?= null
    private var storagePostPiceRef : StorageReference?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPiceRef = FirebaseStorage.getInstance().reference.child("Post Picture")

       CropImage.activity().start(this@AddPostActivity)

        image_post.setOnClickListener(){
            startActivity(Intent(applicationContext,AddPostActivity::class.java))
        }

        save_new_post_btn.setOnClickListener(){
            UploadImage()
        }

        close_add_post_btn?.setOnClickListener(){

            finish()

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== AppCompatActivity.RESULT_OK && data!=null){
            val result = CropImage.getActivityResult(data)
            imageUri= result.uri
            image_post.setImageURI(imageUri)
        }
    }

    private fun UploadImage() {
       when{
           imageUri==null -> Toast.makeText(this, "Add Pic to Post..", Toast.LENGTH_LONG).show()
           TextUtils.isEmpty(description_post.text.toString()) -> Toast.makeText(this, "Please Write Sometehing About it..", Toast.LENGTH_LONG).show()

           else-> {
               var progressdialog:ProgressDialog = ProgressDialog(this)
               progressdialog.setTitle("Add Post")
               progressdialog.setMessage("Please Wait, we are adding Your Post..")
               progressdialog.show()
               progressdialog.setCanceledOnTouchOutside(false)

                val fileRef = storagePostPiceRef!!.child(System.currentTimeMillis().toString()+".jpg")
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
               }).addOnCompleteListener {task ->
                   if(task.isSuccessful){

                    val downloadUrl = task.result
                       myUri = downloadUrl.toString()



                       val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                       val postId = ref.push().key!!
                       val PostMap = HashMap<String,Any>()

                       PostMap["postId"] = postId
                       PostMap["desciption"] = description_post.text.toString()
                       PostMap["publisher"] =FirebaseAuth.getInstance().currentUser!!.uid
                       PostMap["image"] = myUri

                       ref.child(postId).updateChildren(PostMap)
                       Toast.makeText(this, "Your Post is Posted successfully.", Toast.LENGTH_LONG).show()
                       val intent = Intent(this@AddPostActivity, MainActivity::class.java)
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
