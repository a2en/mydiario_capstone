package io.github.a2en.mydiario.ui.home.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import io.github.a2en.mydiario.R
import io.github.a2en.mydiario.databinding.FragmentDetailBinding
import java.io.IOException
import java.util.*
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationCallback

import android.os.Looper





class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this)[DetailViewModel::class.java]
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val args: DetailFragmentArgs by navArgs()
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                checkDeviceLocationSettingsAndFetchPlaceName()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is needed for this app to work",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val resolutionForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK)
                checkDeviceLocationSettingsAndFetchPlaceName(false)
            else {
                Toast.makeText(
                    requireContext(),
                    "Location is needed for this app to work",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        args.diaryEntry?.let {
            viewModel.updateId = it.id
            viewModel.title.value = it.title ?: ""
            viewModel.body.value = it.body ?: ""
            viewModel.date.value = viewModel.formatDate(it.date ?: "")

        }

        binding.back.setOnClickListener {
            view.findNavController().popBackStack()
        }

        binding.dateEditText.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .build()

            datePicker.show(childFragmentManager, "tag")
            datePicker.addOnPositiveButtonClickListener {
                binding.dateEditText.setText(datePicker.headerText)
            }
        }

        viewModel.showToast.observe(this, {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })

        viewModel.navigate.observe(this, {
            view.findNavController().popBackStack()
        })

        viewModel.loading.observe(this, {
            if (it) {
                binding.saveButton.visibility = View.INVISIBLE
                binding.progressCircular.visibility = View.VISIBLE
            } else {
                binding.saveButton.visibility = View.VISIBLE
                binding.progressCircular.visibility = View.INVISIBLE
            }
        })

        binding.saveButton.setOnClickListener {
            var dialog = AlertDialog.Builder(requireContext())
                .setMessage("Do you want to save you current location with this entry?")
                .setPositiveButton("YES") { dialog, which ->
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                .setNegativeButton("NO") { dialog, which ->
                    viewModel.saveEntry()
                }
                .show()
            dialog.makeButtonTextWhite()
        }

    }

    @SuppressLint("MissingPermission")
    private fun checkDeviceLocationSettingsAndFetchPlaceName(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("TAG", "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                checkDeviceLocationSettingsAndFetchPlaceName()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            viewModel.place = getAddress(location.latitude, location.latitude)
                            viewModel.saveEntry()
                        }else{
                            requestNewLocationData()
                        }
                    }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
               saveEntry(location)
            }
        }
    }

    private fun saveEntry(location: Location) {
        viewModel.place = getAddress(location.latitude, location.latitude)
        viewModel.saveEntry()
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)
            val obj: Address = addresses[0]
            return if (obj.adminArea != null)
                obj.adminArea
            else
                "Unknown location"
        } catch (e: IOException) {
            e.printStackTrace()
            return "Unknown location"
        }
    }


}

fun AlertDialog.makeButtonTextWhite() {
    this.getButton(AlertDialog.BUTTON_POSITIVE)
        .setTextColor(ContextCompat.getColor(context, R.color.white))
    this.getButton(AlertDialog.BUTTON_NEGATIVE)
        .setTextColor(ContextCompat.getColor(context, R.color.white))
}
