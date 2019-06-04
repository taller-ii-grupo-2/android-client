package com.fiuba.hypechat_app.models

import android.os.Parcelable
import com.fiuba.hypechat_app.User
import kotlinx.android.parcel.Parcelize


@Parcelize
class Workgroup (val name:String, val description:String, val welcomMsg:String,val urlImage:String ):Parcelable{
    constructor() : this("", "", "", "")

private var memberList:MutableList<String> = ArrayList()

     fun setListMembers(list:MutableList<String>){
        memberList = list
    }

     fun getListMembers():MutableList<String>{
        return memberList
    }
}