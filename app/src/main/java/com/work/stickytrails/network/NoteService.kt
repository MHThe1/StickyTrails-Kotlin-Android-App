package com.work.stickytrails.network

import com.work.stickytrails.models.NoteModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface NoteService {
    @GET("notes")
    suspend fun getNotes(
        @Header("Authorization") token: String
    ): Response<ApiResponse>
}

data class ApiResponse(
    val success: Boolean,
    val data: List<NoteModel>
)
