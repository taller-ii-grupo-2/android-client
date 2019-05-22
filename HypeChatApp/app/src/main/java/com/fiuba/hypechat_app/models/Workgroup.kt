package com.fiuba.hypechat_app.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class Workgroup (val name:String, val description:String, val welcomeMsg:String,val urlImage:String ):Parcelable{
    constructor() : this("", "", "", "")
}