package com.sesac.developer_study_platform.ui.main

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
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

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        startSplash()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigationView()
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

    private fun setBottomNavigationView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnv.setupWithNavController(navController)
        hideBottomNavigationView(navController)
    }

    private fun hideBottomNavigationView(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bnv.visibility = when (destination.id) {
                R.id.dest_login -> View.GONE
                R.id.dest_study_form -> View.GONE
                R.id.dest_search_result -> View.GONE
                R.id.dest_detail -> View.GONE
                else -> View.VISIBLE
            }
        }
    }
}