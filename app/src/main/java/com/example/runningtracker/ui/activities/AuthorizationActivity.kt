package com.example.runningtracker.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding
import com.example.runningtracker.databinding.AuthorizationActivityBinding
import com.example.runningtracker.ui.fragments.authorizations.SIgnInFragment
import com.example.runningtracker.ui.fragments.authorizations.SignUpFragment

class AuthorizationActivity : AppCompatActivity() {
    private lateinit var binding: AuthorizationActivityBinding

    companion object {
        const val TAG_SIGN_IN_FRAGMENT = "SIGN_IN_FRAGMENT"
        const val TAG_SIGN_UP_FRAGMENT = "SIGN_UP_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthorizationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(TAG_SIGN_UP_FRAGMENT)
    }

    fun replaceFragment(tag: String) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        var nowDisplayedFragment = fragmentManager.findFragmentByTag(tag)
        if (nowDisplayedFragment != null) {
            transaction.replace(R.id.fragment_container, nowDisplayedFragment, tag)
        } else {
            nowDisplayedFragment = createFragment(tag)
            transaction.replace(R.id.fragment_container, nowDisplayedFragment, tag)
                .addToBackStack(null)
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }

    private fun createFragment(tag: String): Fragment {
        return when (tag) {
            TAG_SIGN_IN_FRAGMENT -> {
                SIgnInFragment()
            }
            TAG_SIGN_UP_FRAGMENT -> {
                SignUpFragment()
            }
            else -> error("Unexpected tag $tag")
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

}