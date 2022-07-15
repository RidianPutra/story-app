package com.ridianputra.storyapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.ridianputra.storyapp.databinding.ActivityWelcomeBinding
import com.ridianputra.storyapp.ui.signin.SignInActivity
import com.ridianputra.storyapp.ui.signup.SignUpActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.signinPage.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.signupPage.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun playAnimation() {
        val welcome = ObjectAnimator.ofFloat(binding.welcomeText, View.ALPHA, 1f).setDuration(1000)
        val btnSignIn = ObjectAnimator.ofFloat(binding.signinPage, View.ALPHA, 1f).setDuration(600)
        val btnSignUp = ObjectAnimator.ofFloat(binding.signupPage, View.ALPHA, 1f).setDuration(600)

        val together = AnimatorSet().apply {
            playTogether(btnSignIn, btnSignUp)
        }

        AnimatorSet().apply {
            playSequentially(welcome, together)
            start()
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















