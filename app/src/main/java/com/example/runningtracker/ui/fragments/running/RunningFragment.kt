package com.example.runningtracker.ui.fragments.running

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.runningtracker.R
import com.example.runningtracker.RunningTrackerApplication
import com.example.runningtracker.database.entity.Point
import com.example.runningtracker.databinding.FragmentRunningBinding
import com.example.runningtracker.presenter.running.PresenterRunning
import com.example.runningtracker.presenter.running.PresenterRunningContract
import com.example.runningtracker.ui.fragments.BaseFragment

class RunningFragment :
    BaseFragment<PresenterRunningContract.IPresenterRunning, PresenterRunningContract.IViewRunning>(),
    PresenterRunningContract.IViewRunning {

    companion object {
        const val LOCATION_SERVICE_IS_START = "IS_START"
        const val DISTANCE_FROM_SERVICE = " DISTANCE_FROM_SERVICE"
        const val LIST_POINT = "LIST_POINT"
        const val LOCATION_UPDATE = "LOCATION_UPDATE"
        private const val REQUEST_CODE_PERMISSION_LOCATION = 404
        private const val TAG_ERROR_FROM_SERVER = "TAG_ERROR_FROM_SERVER"
    }

    private var _binding: FragmentRunningBinding? = null
    private val binding get() = _binding!!
    private var trackId: Int = 0
    private var trackIdFromServer: Int = 0
    private var distance = 0
    private var beginTime = 0L
    private val coordinateList = mutableListOf<Point>()
    private var alertDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionGps()
        val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate_image)
        binding.btnRequestPermission.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        binding.btnStartRunning.setOnClickListener {
            startForegroundLocationService(true)
            binding.btnStartRunning.isVisible = false
            binding.runningLayout.isVisible = true
            binding.runningLayout.startAnimation(rotate)
        }
        binding.btnFinish.setOnClickListener {
            binding.runningDistance.isVisible = true
            binding.runningLayout.startAnimation(rotate)
        }
        getPresenter().getLastIdTrack()
    }

    private fun checkPermissionGps() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.btnStartRunning.isVisible = true
            binding.btnRequestPermission.isVisible = false
            isGpsEnabled()
        } else {
            binding.btnRequestPermission.isVisible = true
            binding.btnStartRunning.isVisible = false
            this.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_PERMISSION_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.btnStartRunning.isVisible = true
                    binding.btnRequestPermission.isVisible = false
                } else {
                    binding.btnStartRunning.isVisible = false
                    binding.btnRequestPermission.isVisible = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.gps_permission_denied),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alertDialog = null
        _binding = null
    }

    override fun createPresenter(): PresenterRunningContract.IPresenterRunning {
        return PresenterRunning(
            RunningTrackerApplication.databaseHelper,
            RunningTrackerApplication.apiService
        )
    }

    override fun errorResponse(t: Throwable) {
        Log.d(TAG_ERROR_FROM_SERVER, t.message.toString())
    }

    override fun setIdForTrack(id: Int) {
        this.trackIdFromServer = id
    }

    override fun getIdFromServer(id: Int) {
        this.trackIdFromServer = id
    }

    override fun showViewLoading() {
        binding.progressBar.isVisible = true
    }

    override fun hideViewLoading() {
        binding.progressBar.isVisible = false
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            true
        } else {
            createAlertDialogGpsDisabled()
            false
        }
    }

    private fun createAlertDialogGpsDisabled() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.turn_the_gps))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.yes)) { dialogs, _ ->
            if (isGpsEnabled()) {
                dialogs.dismiss()
            }
        }
        alertDialog = builder.create()
        alertDialog?.let {
            it.show()
            it.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.GREEN)
            it.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    private fun startForegroundLocationService(isStart: Boolean) {
        val intent = Intent(context, CheckLocationService::class.java)
            .putExtra(LOCATION_SERVICE_IS_START, isStart)
        requireActivity().startService(intent)
    }
}