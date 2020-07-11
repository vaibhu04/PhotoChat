package com.example.photochat.Model

class Post {
    private var postId :String =""
    private var image :String =""
    private var desciption :String =""
    private var publisher :String =""

    constructor()
    constructor(postId: String, image: String, description: String, publisher: String) {
        this.postId = postId
        this.image = image
        this.desciption = description
        this.publisher = publisher
    }
fun getpostId ():String{
    return postId
}
    fun getimaage ():String{
        return image
    }
    fun getdescription():String{
        return desciption
    }
    fun getpublisher ():String{
        return publisher
    }
    fun setpostId( postId: String){
        this.postId=postId
    }
    fun setimage(image: String){
        this.image=image
    }
    fun setdescription(description: String){
        this.desciption=description
    }
    fun setpublisher (publisher: String){
        this.publisher=publisher
    }
}