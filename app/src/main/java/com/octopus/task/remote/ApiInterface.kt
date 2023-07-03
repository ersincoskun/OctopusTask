package com.octopus.task.remote

import com.octopus.task.model.ResponseModel
import com.octopus.task.model.SpecifyBodyModel
import com.octopus.task.model.SpecifyResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {
    @POST("screen/{uuid}")
    suspend fun specify(
        @Path("uuid") uuid: String,
        @Body body: SpecifyBodyModel
    ): Response<SpecifyResponseModel?>?

    @GET("screen/{uuid}")
    suspend fun getPlaylist(
        @Path("uuid") uuid: String
    ): Response<ResponseModel?>?
}