package com.sesac.developer_study_platform.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sesac.developer_study_platform.R
import com.sesac.developer_study_platform.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var splashScreen: SplashScreen
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
//        startSplash()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
        navController = navHostFragment.navController
        binding.bnv.setupWithNavController(navController)

        hideBottomNavigationView(navController)

        setNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setNewIntent(intent)
    }

    private fun startSplash() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            ObjectAnimator.ofPropertyValuesHolder(splashScreenView.iconView).run {
                interpolator = AnticipateInterpolator()
                duration = 1000L
                doOnEnd {
                    splashScreenView.remove()
                }
                start()
            }
        }
    }

    private fun hideBottomNavigationView(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bnv.visibility = when (destination.id) {
                R.id.dest_login -> View.GONE
                R.id.dest_study_form -> View.GONE
                R.id.dest_search_result -> View.GONE
                R.id.dest_detail -> View.GONE
                R.id.dest_profile -> View.GONE
                R.id.dest_message -> View.GONE
                R.id.dest_webview -> View.GONE
                R.id.dest_exit_dialog -> View.GONE
                R.id.dest_ban_dialog -> View.GONE
                R.id.dest_join_study_dialog -> View.GONE
                R.id.dest_notification_permission_dialog -> View.GONE
                else -> View.VISIBLE
            }
        }
    }

    private fun setNewIntent(intent: Intent?) {
        if (intent != null) {
            val sid = intent.getStringExtra("sid")
            if (!sid.isNullOrEmpty()) {
                val action = MainActivityDirections.actionGlobalToMessage(sid)
                navController.navigate(action)
            }
        }
    }
}