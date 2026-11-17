package com.group29.eventapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.group29.eventapp.common.SessionManager
import com.group29.eventapp.common.UserRole
import com.group29.eventapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavigation()
        observeSession()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // Attendee Tabs
                R.id.nav_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_explore -> {
                    navController.navigate(R.id.searchFilterFragment)
                    true
                }
                R.id.nav_my_events -> {
                    navController.navigate(R.id.myEventsFragment)
                    true
                }
                R.id.nav_networking -> {
                    navController.navigate(R.id.smartMatchFragment)
                    true
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }

                // Organizer Tabs
                R.id.nav_org_overview -> {
                    navController.navigate(R.id.organizerDashboardFragment)
                    true
                }
                R.id.nav_org_events -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_org_scan -> {
                    navController.navigate(R.id.organizerScannerFragment)
                    true
                }
                R.id.nav_org_quests -> {
                    navController.navigate(R.id.organizerQuestEditorFragment)
                    true
                }
                R.id.nav_org_profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registerFragment -> {
                    binding.bottomNav.visibility = View.GONE
                }
                R.id.homeFragment,
                R.id.searchFilterFragment,
                R.id.myEventsFragment,
                R.id.smartMatchFragment,
                R.id.profileFragment,
                R.id.organizerDashboardFragment,
                R.id.organizerScannerFragment,
                R.id.organizerQuestEditorFragment -> {
                    binding.bottomNav.visibility = View.VISIBLE
                }
                else -> {
                    // Ẩn thanh bottom nav trên các màn chi tiết sâu
                    binding.bottomNav.visibility = View.GONE
                }
            }
        }
    }

    private fun observeSession() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                SessionManager.currentRole.collect { role ->
                    if (role == UserRole.ORGANIZER) {
                        binding.bottomNav.menu.clear()
                        binding.bottomNav.inflateMenu(R.menu.menu_bottom_nav_organizer)
                    } else {
                        binding.bottomNav.menu.clear()
                        binding.bottomNav.inflateMenu(R.menu.menu_bottom_nav_attendee)
                    }
                }
            }
        }
    }
}
