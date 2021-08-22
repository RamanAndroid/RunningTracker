package com.example.runningtracker.ui.fragments.authorizations

import android.content.Intent
import android.content.ServiceConnection
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
import com.example.runningtracker.databinding.FragmentSignUpBinding
import com.example.runningtracker.presenter.authorizations.PresenterAuthorizationsContract
import com.example.runningtracker.presenter.authorizations.PresenterSignUp
import com.example.runningtracker.retrofit.pojo.authorizations.SignUpRequest
import com.example.runningtracker.ui.activities.AuthorizationActivity
import com.example.runningtracker.ui.activities.MainActivity
import com.example.runningtracker.ui.fragments.BaseFragment
import java.util.regex.Pattern


class SignUpFragment :
    BaseFragment<PresenterAuthorizationsContract.IPresenterSignUp, PresenterAuthorizationsContract.IViewAuthorizations>(),
    PresenterAuthorizationsContract.IViewAuthorizations {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.hyperlinkChangeFragment.setOnClickListener {
            val activity = (context as AuthorizationActivity)
            activity.replaceFragment(AuthorizationActivity.TAG_SIGN_IN_FRAGMENT)
        }
        binding.signUpBtn.setOnClickListener {
            if (
                isValidEmail(
                    binding.emailInput.text?.toString()
                )
                && isValidName(
                    binding.nameInput.text?.toString(),
                    binding.secondNameInput.text?.toString()
                ) && isValidPassword(
                    binding.passwordInput.text?.toString(),
                    binding.repeatPasswordInput.text?.toString()
                )
            )
                getPresenter().signUp(
                    SignUpRequest(
                        email = binding.emailInput.text.toString(),
                        name = binding.nameInput.text.toString(),
                        secondName = binding.secondNameInput.text.toString(),
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

    private fun isValidName(name: String?, secondName: String?): Boolean {
        return when {
            name.isNullOrEmpty() -> {
                binding.nameInputLayout.error = getString(R.string.error_name_field_empty)
                binding.secondNameInputLayout.error = null
                false
            }
            secondName.isNullOrEmpty() -> {
                binding.nameInputLayout.error = null
                binding.secondNameInputLayout.error =
                    getString(R.string.error_second_name_field_empty)
                false
            }
            else -> {
                binding.secondNameInputLayout.error = null
                binding.nameInputLayout.error = null
                true
            }
        }
    }

    private fun isValidPassword(password: String?, repeatPassword: String?): Boolean {
        val passwordPattern: Pattern =
            Pattern.compile("^(?=.*[A-Za-z].*)(?=.*[0-9].*)[A-Za-z0-9]{6,}$")
        return when {
            password.isNullOrEmpty() -> {
                binding.passwordInputLayout.error = getString(R.string.error_password_field_empty)
                binding.repeatPasswordInput.error = null
                false
            }
            !passwordPattern.matcher(password).matches() -> {
                binding.repeatPasswordInputLayout.error = null
                binding.passwordInputLayout.error =
                    getString(R.string.error_password_is_not_complex_enough)
                false
            }
            password != repeatPassword -> {
                binding.passwordInputLayout.error = null
                binding.repeatPasswordInputLayout.error =
                    getString(R.string.error_repeat_password_not_equal)
                false
            }
            else -> {
                binding.passwordInputLayout.error = null
                binding.repeatPasswordInputLayout.error = null
                true
            }
        }
    }

    override fun errorResponse(t: Throwable) {
        Log.d(PresenterSignUp.ERROR, t.toString())
        when (t.message) {
            PresenterSignUp.ERROR_FROM_THE_SERVER -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_occurred_on_the_server),
                    Toast.LENGTH_SHORT
                ).show()
            }
            PresenterSignUp.REQUIRED_FIELDS_ARE_EMPTY -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_required_filed_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
            PresenterSignUp.INVALID_EMAIL -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_email_not_valid),
                    Toast.LENGTH_SHORT
                ).show()
            }
            PresenterSignUp.EMAIL_ALREADY_EXISTS -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_email_already_exist),
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

    override fun createPresenter(): PresenterAuthorizationsContract.IPresenterSignUp {
        return PresenterSignUp(
            provider = RunningTrackerApplication.apiService,
            sharedPreferences = RunningTrackerApplication.sharedPreferences
        )
    }
}
