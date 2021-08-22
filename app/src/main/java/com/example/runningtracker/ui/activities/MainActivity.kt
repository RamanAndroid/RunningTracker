package com.example.runningtracker.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.runningtracker.ApplicationConstants
import com.example.runningtracker.R
import com.example.runningtracker.RunningTrackerApplication.Companion.sharedPreferences
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.ui.fragments.reminder.ReminderBroadcastReceiver
import com.example.runningtracker.ui.fragments.reminder.ReminderFragment
import com.example.runningtracker.ui.fragments.tracks.TrackListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        private const val TAG_FRAGMENT_TRACKS = "FRAGMENT_TRACKS"
        private const val TAG_FRAGMENT_REMINDER = "FRAGMENT_REMINDER"
        private const val FRAGMENT_STACK_MAIN_ACTIVITY = "FRAGMENT_STACK_MAIN_ACTIVITY"
        private const val EMPTY = ""
        const val DEFAULT_BACK_STACK_SIZE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initializingDrawerLayout()
        checkReminderEnter()
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        if (supportFragmentManager.backStackEntryCount == DEFAULT_BACK_STACK_SIZE ||
            supportFragmentManager.backStackEntryCount > DEFAULT_BACK_STACK_SIZE
        ) {
            finish()
        }
        super.onBackPressed()
    }

    private fun replaceFragment(tag: String) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        var nowDisplayedFragment = fragmentManager.findFragmentByTag(tag)
        if (nowDisplayedFragment != null) {
            transaction.replace(R.id.nav_host_main_fragment, nowDisplayedFragment, tag)
                .addToBackStack(FRAGMENT_STACK_MAIN_ACTIVITY)
        } else {
            nowDisplayedFragment = createFragment(tag)
            transaction.replace(R.id.nav_host_main_fragment, nowDisplayedFragment, tag)
                .addToBackStack(FRAGMENT_STACK_MAIN_ACTIVITY)
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }

    private fun createFragment(tag: String): Fragment {
        return when (tag) {
            TAG_FRAGMENT_TRACKS -> {
                TrackListFragment()
            }
            TAG_FRAGMENT_REMINDER -> {
                ReminderFragment()
            }
            else -> error("Unexpected tag $tag")
        }
    }

    private fun initializingDrawerLayout() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open_drawer_layout,
            R.string.close_drawer_layout
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_menu -> {
                    supportActionBar?.title = getString(R.string.main_menu)
                    replaceFragment(TAG_FRAGMENT_TRACKS)

                }
                R.id.reminder_menu -> {
                    supportActionBar?.title = getString(R.string.reminder)
                    replaceFragment(TAG_FRAGMENT_REMINDER)
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        supportActionBar?.title = getString(R.string.main_menu)
        replaceFragment(TAG_FRAGMENT_TRACKS)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    private fun logout() {
        sharedPreferences.edit()
            .putString(
                ApplicationConstants.CONSTANTS_USER_TOKEN,
                EMPTY
            ).apply()
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkReminderEnter() {
        val isReminderEnter =
            intent.getBooleanExtra(ReminderBroadcastReceiver.REMINDER_BROADCAST_RECEIVER, false)
        if (isReminderEnter) {
            val intent = Intent(this, RunningActivity::class.java)
            startActivity(intent)
        }
    }
}