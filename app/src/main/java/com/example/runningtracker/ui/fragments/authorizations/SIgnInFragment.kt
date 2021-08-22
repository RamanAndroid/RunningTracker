package com.example.runningtracker.ui.fragments.authorizations

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.runningtracker.R
import com.example.runningtracker.RunningTrackerApplication
import com.example.runningtracker.databinding.FragmentSignInBinding
import com.example.runningtracker.presenter.authorizations.PresenterAuthorizationsContract
import com.example.runningtracker.presenter.authorizations.PresenterSignIn
import com.example.runningtracker.presenter.authorizations.PresenterSignUp
import com.example.runningtracker.retrofit.pojo.authorizations.SignInRequest
import com.example.runningtracker.ui.activities.AuthorizationActivity
import com.example.runningtracker.ui.activities.MainActivity
import com.example.runningtracker.ui.fragments.BaseFragment

class SIgnInFragment :
    BaseFragment<PresenterAuthorizationsContract.IPresenterSignIn, PresenterAuthorizationsContract.IViewAuthorizations>(),
    PresenterAuthorizationsContract.IViewAuthorizations {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.hyperlinkChangeFragment.setOnClickListener {
            val activity = (context as AuthorizationActivity)
            activity.replaceFragment(AuthorizationActivity.TAG_SIGN_UP_FRAGMENT)
        }
        binding.signInBtn.setOnClickListener {
            if (
                isValidEmail(
                    binding.emailInput.text?.toString()
                )
                && isValidPassword(
                    binding.passwordInput.text?.toString()
                )
            )
                getPresenter().signIn(
                    SignInRequest(
                        email = binding.emailInput.text.toString(),
                        password = binding.passwordInput.text.toString()
                    )
                )
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isValidEmail(email: String?): Boolean {
        return when {
            email.isNullOrEmpty() -> {
                binding.emailInputLayout.error = getString(R.string.error_empty_email_field)
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailInputLayout.error = getString(R.string.error_email_not_valid)
                false
            }
            else -> {
                binding.emailInputLayout.error = null
                true
            }
        }
    }

    private fun isValidPassword(password: String?): Boolean {
        return when {
            password.isNullOrEmpty() -> {
                binding.passwordInputLayout.error = getString(R.string.error_password_field_empty)
                false
            }
            else -> {
                binding.passwordInput.error = null
                true
            }
        }
    }


    override fun createPresenter(): PresenterAuthorizationsContract.IPresenterSignIn {
        return PresenterSignIn(
            RunningTrackerApplication.apiService,
            RunningTrackerApplication.sharedPreferences
        )
    }

    override fun errorResponse(t: Throwable) {
        Log.d(PresenterSignUp.ERROR, t.toString())
        when (t.message) {
            PresenterSignIn.INVALID_CREDENTIALS -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_incorrectly_entered_data_to_register),
                    Toast.LENGTH_SHORT
                ).show()
            }
            PresenterSignUp.ERROR -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_unexpected),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSuccess() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        (activity as AuthorizationActivity).finish()
    }

    override fun showViewLoading() {
        binding.progressBar.isVisible = true
    }

    override fun hideViewLoading() {
        binding.progressBar.isVisible = false
    }
}