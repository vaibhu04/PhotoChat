package com.example.photochat.Model



class Story {
    private var imageurl : String =""
    private var timestart : Long=0
    private var timeend : Long=0
    private var storyid : String =""
    private var userid : String =""

constructor()
    constructor(imageurl: String, timestart: Long, timeend: Long, storyid: String, userid: String) {
        this.imageurl = imageurl
        this.timestart = timestart
        this.timeend = timeend
        this.storyid = storyid
        this.userid = userid
    }
    fun getImageUrl(): String{
        return imageurl
    }
    fun getStoryId(): String{
        return storyid
    }
    fun getUserId(): String{
        return userid
    }
    fun gettimeStart (): Long{
        return timestart
    }

    fun gettimeEnd (): Long{
        return timeend
    }

    fun setImageUrl(imageurl: String){
        this.imageurl=imageurl
    }
    fun setUserid(userid: String){
        this.userid=userid
    }
    fun setStorryid(storyid: String){
        this.storyid=storyid
    }
    fun setStrattime(timestart: Long){
        this.timestart =timestart
    }
    fun setEndTime(timeend: Long){
        this.timeend=timeend
    }
}