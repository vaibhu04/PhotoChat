package com.example.photochat.Model

class Chat{
    private var senderId: String ?=""
    private var message : String?=""
    private var receiverId : String?=""
    private var isseen : Boolean?=false
    private var messageId : String?=""
    private var url : String?=""
constructor()
    constructor(
        senderId: String?,
        message: String?,
        receiverId: String?,
        isseen: Boolean?,
        messageId: String?,
        url: String?
    ) {
        this.senderId = senderId
        this.message = message
        this.receiverId = receiverId
        this.isseen = isseen
        this.messageId = messageId
        this.url = url
    }
    fun getsenderid():String{
        return senderId!!
    }
    fun getmessage():String{
        return message!!
    }

    fun getReceiverId():String{
        return receiverId!!
    }
    fun getisSEen ():Boolean{
        return isseen!!
    }
    fun getmessageId():String{
        return messageId!!
    }
    fun getImage():String{
        return url!!
    }
}