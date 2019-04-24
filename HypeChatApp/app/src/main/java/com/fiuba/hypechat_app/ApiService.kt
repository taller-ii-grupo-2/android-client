package com.fiuba.hypechat_app

import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    @POST("register")
    fun createUser(@Body user:User): Call<DefaultResponse>

    @POST("login")
    fun signInUser(@Body token:Token): Call<DefaultResponse>

    @GET ("users/all")
    fun getListUsers(): Call<List<User>>
}

class DefaultResponse(val message:String)

class Token (val token:String)

class User (val mail:String, val name: String)