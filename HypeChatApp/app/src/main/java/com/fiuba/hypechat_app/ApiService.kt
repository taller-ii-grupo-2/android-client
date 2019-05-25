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

    @GET ("user/organizations/channels")
    fun getWholeOrgaData(@Body org_name:String):Call<Workspace>


}

class DefaultResponse(val message:String)

class Token (val token:String)

class User (
    val longitude: Double,
    val latitude: Double,
    val mail: String,
    val urlImageProfile:String,
    val surname:String,
    val name:String,
    val username:String
)

class WorkgroupPhotoAndName (val urlImage: String, val name:String)

class Workspace()