package com.work.stickytrails.network


import com.work.stickytrails.models.LoginRequest
import com.work.stickytrails.models.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @POST("user/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse

    @POST("user/register")
    suspend fun registerUser(@Body registerRequest: Map<String, String>): LoginResponse

    @GET("user/{username}")
    suspend fun getUserByUsername(
        @Path("username") username: String,
        @Header("Authorization") token: String
    ): Map<String, Any>

    @PUT("user/{username}")
    suspend fun updateUserByUsername(
        @Path("username") username: String,
        @Header("Authorization") token: String,
        @Body updateData: Map<String, String>
    ): Map<String, Any>
}