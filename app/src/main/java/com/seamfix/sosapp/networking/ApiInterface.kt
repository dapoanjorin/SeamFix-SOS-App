package com.seamfix.sosapp.networking

import com.seamfix.sosapp.models.SOSRequest
import com.seamfix.sosapp.models.SOSResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("v1/create")
    suspend fun sendSOS(@Body request: SOSRequest): Response<SOSResponse>
}