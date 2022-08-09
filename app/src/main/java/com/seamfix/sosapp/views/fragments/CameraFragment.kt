package com.seamfix.sosapp.views.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.seamfix.sosapp.databinding.FragmentCameraBinding
import com.seamfix.sosapp.models.SOSResult
import com.seamfix.sosapp.showToast
import com.seamfix.sosapp.viewmodel.SOSViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123

@AndroidEntryPoint
class CameraFragment(var sosViewModel: SOSViewModel? = null) : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var locationPermissionGranted = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        binding.camera.setLifecycleOwner(viewLifecycleOwner)

        binding.camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                showToast("Processing...")
                val encodedImage = Base64.encodeToString(result.data, Base64.DEFAULT)
                sosViewModel?.updateImage(encodedImage)
                getDeviceLocation()
            }
        })

        binding.btnCapturePicture.setOnClickListener {
            binding.camera.takePicture()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sosViewModel?.sosResult?.collectLatest { observeData(it) }
            }
        }
    }

    private fun observeData(sosResult: SOSResult) {
        when (sosResult) {
            is SOSResult.Success -> {
                showToast("Your location and data has been received!")
                showToast("Help is on the way")
            }
            is SOSResult.Failure -> {
                showToast("Error sending data")
            }
        }
    }

    private fun init() {
        getLocationPermission()

        sosViewModel = ViewModelProvider(this)[SOSViewModel::class.java]

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {


                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            sosViewModel?.updateLocation(
                                lastKnownLocation?.longitude,
                                lastKnownLocation?.latitude
                            )
                        } else {
                            Log.e("Location", "Error getting location")
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}