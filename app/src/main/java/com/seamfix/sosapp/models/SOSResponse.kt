package com.seamfix.sosapp.models

data class SOSResponse(
    val data: SOSData,
    val message: String,
    val status: String
)

data class SOSData(
    val id: Int,
    val image: String,
    val location: Location,
    val phoneNumbers: List<String>
)

data class Location(
    val latitude: String,
    val longitude: String
)