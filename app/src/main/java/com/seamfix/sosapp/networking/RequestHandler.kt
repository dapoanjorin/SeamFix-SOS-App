package com.seamfix.sosapp.networking

import com.seamfix.sosapp.models.SOSRequest
import javax.inject.Inject

class RequestHandler @Inject constructor(private val client: ApiInterface) {

    suspend fun sendSOS(request: SOSRequest) = client.sendSOS(request)
}