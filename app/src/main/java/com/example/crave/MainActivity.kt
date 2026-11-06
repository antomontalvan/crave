package com.example.crave

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.crave.databinding.ActivityMainBinding
import com.example.crave.ui.fragment.CookbookFragment
import com.example.crave.ui.fragment.MealListFragment

/**
 * Container Activity. It only handles NAVIGATION:
 * it hosts the two Fragments in the FragmentContainerView and switches them with the BottomNavigationView.
 * It has no business logic: that lives in the ViewModel.
 *
 * The Fragments are added only once and then shown/hidden (show/hide),
 * so the search and scroll position are kept when switching tabs.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG_LIST = "tag_meal_list"
        private const val TAG_COOKBOOK = "tag_cookbook"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupBottomNavigation()

        // First launch: show the list. (On rotation, the FragmentManager restores the Fragments by itself.)
        if (savedInstanceState == null) {
            showFragment(TAG_LIST)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> showFragment(TAG_LIST)
                R.id.nav_cookbook -> showFragment(TAG_COOKBOOK)
            }
            true
        }
        // Tapping the current tab again does nothing.
        binding.bottomNavigation.setOnItemReselectedListener { }
    }

    /** Hides all Fragments and shows the requested one (creating it the first time). */
    private fun showFragment(tag: String) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        fragmentManager.fragments.forEach { transaction.hide(it) }

        val existing = fragmentManager.findFragmentByTag(tag)
        if (existing != null) {
            transaction.show(existing)
        } else {
            transaction.add(R.id.fragmentContainer, createFragment(tag), tag)
        }
        transaction.commit()
    }

    private fun createFragment(tag: String): Fragment = when (tag) {
        TAG_COOKBOOK -> CookbookFragment()
        else -> MealListFragment()
    }

    /**
     * Since Android 15 the app is drawn edge to edge: padding is added at the top and the
     * sides so content doesn't end up under the status bar. The BottomNavigationView already
     * handles the space of the bottom navigation bar by itself.
     */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }
    }
}