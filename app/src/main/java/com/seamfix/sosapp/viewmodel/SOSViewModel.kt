package com.seamfix.sosapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seamfix.sosapp.models.Location
import com.seamfix.sosapp.models.SOSRequest
import com.seamfix.sosapp.models.SOSResult
import com.seamfix.sosapp.networking.RequestHandler
import com.seamfix.sosapp.util.AppCoroutineDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SOSViewModel  @Inject constructor(
    private val requestHandler: RequestHandler,
    private val appCoroutineDispatcher: AppCoroutineDispatchers
): ViewModel() {

    private val sosResultStateFlow = MutableStateFlow<SOSResult>(SOSResult.EmptyState)
    val sosResult = sosResultStateFlow.asStateFlow()

    val sosRequest: SOSRequest = SOSRequest(phoneNumbers = listOf("911", "112"))

    private fun updateData(sosResult: SOSResult) {
        sosResultStateFlow.value = sosResult
    }

    fun sendSOS() {
        viewModelScope.launch(appCoroutineDispatcher.io) {

            val request = requestHandler.sendSOS(sosRequest)
            if(request.isSuccessful) {
                when(val response = request.body()) {
                    null -> updateData(SOSResult.Failure)
                    else ->  updateData(SOSResult.Success(response))
                }
            } else {
                updateData(SOSResult.Failure)
            }
        }
    }

    fun updateImage(encodedImage: String) {
        sosRequest.image = encodedImage
    }

    fun updateLocation(longitude: Double?, latitude: Double?) {
        sosRequest.location = Location(longitude.toString(), latitude.toString())
        sendSOS()
    }
}