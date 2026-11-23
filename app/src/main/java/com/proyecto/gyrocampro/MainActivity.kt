package com.proyecto.gyrocampro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.proyecto.gyrocampro.databinding.ActivityMainBinding
import com.proyecto.gyrocampro.ui.auth.LoginActivity
import com.proyecto.gyrocampro.utils.AuthSession

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        val darkModeEnabled = getSharedPreferences("gyrocam_preferences", MODE_PRIVATE)
            .getBoolean("dark_mode_enabled", false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        if (!AuthSession.isAuthenticated) {
            redirectToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        binding.mainToolbar.inflateMenu(R.menu.main_toolbar_menu)
        binding.mainToolbar.menu.findItem(R.id.action_logout)?.icon?.setTint(
            ContextCompat.getColor(this, R.color.color_primary)
        )
        binding.mainToolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                performLogout()
                true
            } else {
                false
            }
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.mainToolbar.title = destination.label ?: getString(R.string.app_name)
        }
    }

    fun performLogout() {
        AuthSession.clear()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
