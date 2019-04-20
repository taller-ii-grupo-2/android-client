package com.fiuba.hypechat_app

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("users")
    fun createUser(
        @Field("username") username:String,
        @Field("email") email:String,
        @Field("uid") uid:String
        ): Call<DefaultResponse>
}

class DefaultResponse {

}
