package com.example.runningtracker.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityRunningBinding
import com.example.runningtracker.ui.fragments.reminder.ReminderFragment
import com.example.runningtracker.ui.fragments.running.RunningFragment

class RunningActivity : AppCompatActivity() {

    companion object {
        private const val FRAGMENT_STACK_RUNNING_ACTIVITY = "FRAGMENT_STACK_RUNNING_ACTIVITY"
        private const val TAG_FRAGMENT_RUNNING = "FRAGMENT_RUNNING"
    }

    private lateinit var binding: ActivityRunningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRunningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializingToolbar()
        replaceFragment(TAG_FRAGMENT_RUNNING)
    }

    private fun initializingToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun replaceFragment(tag: String) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        var nowDisplayedFragment = fragmentManager.findFragmentByTag(tag)
        if (nowDisplayedFragment != null) {
            transaction.replace(R.id.nav_host_main_fragment, nowDisplayedFragment, tag)
                .addToBackStack(FRAGMENT_STACK_RUNNING_ACTIVITY)
        } else {
            nowDisplayedFragment = createFragment(tag)
            transaction.replace(R.id.nav_host_main_fragment, nowDisplayedFragment, tag)
                .addToBackStack(FRAGMENT_STACK_RUNNING_ACTIVITY)
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }

    private fun createFragment(tag: String): Fragment {
        return when (tag) {
            TAG_FRAGMENT_RUNNING -> {
                binding.toolbar.title = getString(R.string.running_tracker)
                RunningFragment()
            }
            else -> error("Unexpected tag $tag")
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == MainActivity.DEFAULT_BACK_STACK_SIZE ||
            supportFragmentManager.backStackEntryCount > MainActivity.DEFAULT_BACK_STACK_SIZE
        ) {
            finish()
        }
        super.onBackPressed()
    }
}