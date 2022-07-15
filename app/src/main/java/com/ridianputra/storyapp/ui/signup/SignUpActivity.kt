package com.ridianputra.storyapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.ridianputra.storyapp.R
import com.ridianputra.storyapp.data.Result
import com.ridianputra.storyapp.databinding.ActivitySignUpBinding
import com.ridianputra.storyapp.ui.ViewModelFactory
import com.ridianputra.storyapp.ui.signin.SignInActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance()
    private val viewModel: SignUpViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
        playAnimation()
        signUpAction()
    }

    private fun signUpAction() {
        val personIcon = ContextCompat.getDrawable(this, R.drawable.ic_person) as Drawable
        val emailIcon = ContextCompat.getDrawable(this, R.drawable.ic_email) as Drawable
        val lockIcon = ContextCompat.getDrawable(this, R.drawable.ic_lock) as Drawable
        val name = binding.nameLayout
        val email = binding.emailLayout
        val pass = binding.passwordLayout
        binding.signUp.setOnClickListener {
            if (name.text.isNullOrBlank()) {
                name.setDrawable(personIcon, "red")
                name.setBackground("red")
                name.error = getString(R.string.empty_name)
            }
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
            if (name.error.isNullOrBlank() &&
                email.error.isNullOrBlank() &&
                pass.error.isNullOrBlank()
            ) {
                val nameText = name.text.toString().trim()
                val emailText = email.text.toString()
                val passText = pass.text.toString()
                viewModel.createUser(nameText, emailText, passText).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                val i = Intent(this, SignInActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(i)
                                finish()
                            }
                            is Result.Error -> {
                                binding.apply {
                                    emailLayout.setDrawable(emailIcon, "red")
                                    emailLayout.setBackground("red")
                                    emailLayout.error = getString(R.string.email_taken)
                                    progressBar.visibility = View.GONE
                                }
                                AlertDialog.Builder(this).apply {
                                    setTitle(getString(R.string.failed_title))
                                    setMessage(getString(R.string.email_taken))
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
        val signup = ObjectAnimator.ofFloat(binding.signupText, View.ALPHA, 1f).setDuration(600)
        val name = ObjectAnimator.ofFloat(binding.nameLayout, View.ALPHA, 1f).setDuration(600)
        val email = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(600)
        val pass = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(600)
        val btn = ObjectAnimator.ofFloat(binding.signUp, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(signup, name, email, pass, btn)
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