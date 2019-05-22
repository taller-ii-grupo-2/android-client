package com.fiuba.hypechat_app

import com.fiuba.hypechat_app.models.Workgroup
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    @POST("users")
    fun createUser(@Body user:User): Call<DefaultResponse>

    @POST("login")
    fun signInUser(@Body token:Token): Call<DefaultResponse>

    @POST("organizations")
    fun createWorkgroup(@Body workgroup: Workgroup):Call<DefaultResponse>

    @GET ("users/all")
    fun getListUsers(): Call<List<User>>

    @GET ("user/organizations")
    fun getWorkgroupNameAndPhotoProfile():Call<List<WorkgroupPhotoAndName>>


}

class DefaultResponse(val message:String)

class Token (val token:String)

class User (
    val username:String,
    val name:String,
    val sername:String,
    val urlImageProfile:String,
    val mail: String,
    val latitude: Double,
    val longitude: Double
)

class WorkgroupPhotoAndName (val urlImage: String, val name:String)
