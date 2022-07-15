package com.ridianputra.storyapp.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.ridianputra.storyapp.R
import com.ridianputra.storyapp.data.Result
import com.ridianputra.storyapp.ui.main.MainActivity
import com.ridianputra.storyapp.data.preferences.UserPreferences
import com.ridianputra.storyapp.data.preferences.UserViewModel
import com.ridianputra.storyapp.data.preferences.ViewModelFactory
import com.ridianputra.storyapp.databinding.ActivitySignInBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var userViewModel: UserViewModel
    private val factory: com.ridianputra.storyapp.ui.ViewModelFactory =
        com.ridianputra.storyapp.ui.ViewModelFactory.getInstance()
    private val viewModel: SignInViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
        setupUserViewModel()
        playAnimation()
        signInAction()
    }

    private fun setupUserViewModel() {
        userViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[UserViewModel::class.java]

        userViewModel.getUserSession().observe(this) {
            if (!it.token.isNullOrBlank()) {
                val i = Intent(this, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
        }
    }

    private fun signInAction() {
        val emailIcon = ContextCompat.getDrawable(this, R.drawable.ic_email) as Drawable
        val lockIcon = ContextCompat.getDrawable(this, R.drawable.ic_lock) as Drawable
        val email = binding.emailLayout
        val pass = binding.passwordLayout
        binding.signIn.setOnClickListener {
            if (email.text.isNullOrBlank()) {
                email.setDrawable(emailIcon, "red")
                email.setBackground("red")
                email.error = getString(R.string.empty_email)
            }
            if (pass.text.isNullOrBlank()) {
                pass.setDrawable(lockIcon, "red")
                pass.setBackground("red")
                pass.error = getString(R.string.empty_pass)
            }
            if (email.error.isNullOrBlank() &&
                pass.error.isNullOrBlank()
            ) {
                val emailText = email.text.toString()
                val passText = pass.text.toString()
                viewModel.getUser(emailText, passText).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                val loginResult = result.data
                                userViewModel.saveUserSession(
                                    loginResult.name,
                                    loginResult.userId,
                                    loginResult.token
                                )
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.failed_title))
                                    setMessage(getString(R.string.wrong_input))
                                    create()
                                    show()
                                }
                            }
                        }
                    }
                }
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.failed_title))
                    setMessage(getString(R.string.invalid_input))
                    create()
                    show()
                }
            }
        }
    }

    private fun playAnimation() {
        val signin = ObjectAnimator.ofFloat(binding.signinText, View.ALPHA, 1f).setDuration(600)
        val email = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(600)
        val pass = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(600)
        val btn = ObjectAnimator.ofFloat(binding.signIn, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(signin, email, pass, btn)
            start()
        }
    }

    private fun EditText.setDrawable(icon: Drawable, color: String) {
        val wrapIcon = DrawableCompat.wrap(icon)
        when (color) {
            "navy" -> DrawableCompat.setTint(
                wrapIcon,
                ContextCompat.getColor(context, R.color.navy)
            )
            "red" -> DrawableCompat.setTint(
                wrapIcon,
                ContextCompat.getColor(context, R.color.red)
            )
            "green" -> DrawableCompat.setTint(
                wrapIcon,
                ContextCompat.getColor(context, R.color.green)
            )
        }
        DrawableCompat.setTintMode(wrapIcon, PorterDuff.Mode.SRC_IN)
        setCompoundDrawablesWithIntrinsicBounds(wrapIcon, null, null, null)
    }

    private fun EditText.setBackground(color: String) {
        when (color) {
            "navy" -> background =
                ContextCompat.getDrawable(context, R.drawable.bg_edit_text)
            "red" -> background =
                ContextCompat.getDrawable(context, R.drawable.bg_edit_text_error)
            "green" -> background =
                ContextCompat.getDrawable(context, R.drawable.bg_edit_text_accepted)
        }
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}