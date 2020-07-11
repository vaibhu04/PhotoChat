package com.example.photochat.Model

class Notification {
    private var userId: String=""
    private var text : String=""
    private var postId : String=""

    private var ispost =false
    private var seen = false
    constructor()
    constructor(userId: String, text: String, postId: String, ispost: Boolean, seen:Boolean) {
        this.userId = userId
        this.text = text
        this.postId = postId

        this.ispost = ispost
        this.seen =seen
    }
    fun getuserId():String{
        return userId
    }
    fun getSeen(): Boolean{
        return seen
    }
    fun getPostId():String{
        return postId
    }

    fun getText():String{
        return text
    }
    fun getisPost():Boolean{
        return ispost
    }
    fun setUserid(userId: String){
        this.userId =userId
    }
    fun setPostId(postId: String){
        this.postId=postId
    }
    fun setText(text: String){
        this.text=text
    }
    fun setIsPost(ispost: Boolean){
        this.ispost= ispost
    }
    fun setSeen(seen: Boolean){
        this.seen=seen
    }
}