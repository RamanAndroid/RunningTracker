package com.example.runningtracker.ui.fragments.tracks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.runningtracker.RunningTrackerApplication
import com.example.runningtracker.database.entity.TrackEntity
import com.example.runningtracker.databinding.FragmentTracksBinding
import com.example.runningtracker.presenter.tracks.PresenterTrackList
import com.example.runningtracker.presenter.tracks.PresenterTrackListContract
import com.example.runningtracker.ui.activities.RunningActivity
import com.example.runningtracker.ui.fragments.BaseFragment


class TrackListFragment :
    BaseFragment<PresenterTrackListContract.IPresenterTrackList, PresenterTrackListContract.IViewTrackList>(),
    PresenterTrackListContract.IViewTrackList {

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTracksBinding.inflate(inflater, container, false)

        binding.btnEnterRunning.setOnClickListener {
            val intent = Intent(activity, RunningActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun createPresenter(): PresenterTrackListContract.IPresenterTrackList {
        return PresenterTrackList(RunningTrackerApplication.databaseHelper)
    }

    override fun setData(reminderList: List<TrackEntity>) {
        TODO("Not yet implemented")
    }

    override fun errorResponse(t: Throwable) {
        TODO("Not yet implemented")
    }

    override fun showViewLoading() {
        TODO("Not yet implemented")
    }

    override fun hideViewLoading() {
        TODO("Not yet implemented")
    }

}