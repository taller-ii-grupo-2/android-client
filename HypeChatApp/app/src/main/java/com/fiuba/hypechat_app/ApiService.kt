package com.fiuba.hypechat_app

import com.fiuba.hypechat_app.models.Channel
import com.fiuba.hypechat_app.models.Workgroup
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("users")
    fun createUser(@Body user: User): Call<DefaultResponse>

    @POST("login")
    fun signInUser(@Body token: Token): Call<DefaultResponse>

    @POST("organizations")
    fun createWorkgroup(@Body workgroup: Workgroup): Call<DefaultResponse>

    @GET("users/all")
    fun getListUsers(): Call<List<User>>

    @GET("user/organizations")
    fun getWorkgroupNameAndPhotoProfile(): Call<List<WorkgroupPhotoAndName>>

    @GET("user/organizations/{orgname}/channels")
    fun getWholeOrgaData(@Path("orgname") organame: String): Call<Workspace>

    @POST("/channels")
    fun createChannel(@Body channel: Channel): Call<DefaultResponse>

    @GET("/profile/{email}")
    fun getUserProfile(@Path("email") email: String): Call<UserProfile>

    @PUT("/users")
    fun updateUserProfile(@Body userprof: updateUserProfile): Call<DefaultResponse>

    @PUT("organizations/members")
    fun addMemberToWorkgroup(@Body member: newMember): Call<DefaultResponse>

    @GET("/messages/{orga_name}/{channel_name}")
    fun getMessagesFromChannel(@Path("orga_name") orga_name: String, @Path("channel_name") channel_name: String): Call<List<Chats>>

    @GET("/messages/{orga_name}/dms/{dm_dest_mail}")
    fun getMessagesFromDM(@Path("orga_name") orga_name: String, @Path("dm_dest_mail") dm_dest_mail: String): Call<List<Chats>>

    @HTTP(method = "DELETE", path = "/organizations", hasBody = true)
    fun deleteOrganization(@Body orga:deleteOrga): Call<DefaultResponse>

    @HTTP(method = "DELETE", path = "/organizations/members", hasBody = true)
    fun deleteUser(@Body member:deleteUser):Call<DefaultResponse>

    @HTTP(method = "DELETE", path = "/organizations/channels", hasBody = true)
    fun deleteChannel(@Body channel:deleteChannel):Call<DefaultResponse>

    @PUT ("/type/{orga_name}")
    fun updateRol(@Path("orga_name") orga_name: String, @Body type:Types): Call<DefaultResponse>

    @GET ("/type/{orga_name}")
    fun getUsersTypes(@Path("orga_name") orga_name: String): Call<List<Types>>

    /*  @DELETE ("/organizations/channels")
    fun deleteChannel(@Body channel:deleteChannel):Call<DefaultResponse>*/


}
class Types (val mail:String, val type:String)

class deleteUser (val nameOrga:String, val mail:String)

class deleteChannel (val nameOrga:String, val name_channel: String)

class deleteOrga (val nameOrga:String)

class Chats(val timestamp: String, val author_mail: String, val body: String)

class DefaultResponse(val message: String)

class Token(val token: String)

class User(
    val longitude: Double,
    val latitude: Double,
    val mail: String,
    val urlImageProfile: String,
    val surname: String,
    val name: String,
    val username: String
)

class UserProfile(
    val username: String,
    val name: String,
    val surname: String,
    val url: String,
    val organizations: List<WorkgroupAndChannelList>
)

class updateUserProfile(
    val username: String,
    val name: String,
    val surname: String,
    val urlImageProfile: String
)

class newMember(
    val org_name: String,
    val mail_of_user_to_add: String
)

class WorkgroupAndChannelList(val name: String, val channels: MutableList<String>)

class WorkgroupPhotoAndName(val urlImage: String, val name: String)

class Workspace(
    val description: String,
    val welcomMsg: String,
    val urlImage: String,
    val channels: MutableList<String>,
    val members: MutableList<String>
)

