package com.seamfix.sosapp.models

data class SOSRequest(
    var phoneNumbers: List<String> = listOf(),
    var image: String = "",
    var location: Location? = null
)