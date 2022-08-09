package com.seamfix.sosapp.models

sealed class SOSResult {
    data class Success(val data: SOSResponse) : SOSResult()
    object Failure : SOSResult()
    object EmptyState : SOSResult()
}