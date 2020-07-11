package com.example.photochat.Model

class Comment {
         private var comment:String =""
    private var publisherId: String=""

    constructor()
    constructor(comment: String, publisherId: String) {
        this.comment = comment
        this.publisherId = publisherId
    }
    fun getComment():String{
        return comment
    }
    fun getpublisherId ():String{
        return publisherId
    }
    fun setComment(comment: String){
        this.comment=comment
    }
    fun setpublisherId(publisherId: String){
        this.publisherId = publisherId
    }
}