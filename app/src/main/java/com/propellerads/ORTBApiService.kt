package com.propellerads

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface ORTBApiService {
    @Headers("X-Tracer: add-Tracer-Value-Here")
    @POST
    fun sendOfferApiOrtb(
        @Url url: String,
        @Body requestBody: JsonObject
    ): Call<ResponseData>
}