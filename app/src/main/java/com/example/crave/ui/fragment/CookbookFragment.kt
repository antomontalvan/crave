package com.example.crave.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crave.DetailActivity
import com.example.crave.databinding.FragmentCookbookBinding
import com.example.crave.ui.adapter.MealAdapter
import com.example.crave.ui.viewmodel.MainViewModel

/**
 * Cookbook: recipes saved in SQLite. Works without internet.
 *
 * Reuses the same MealAdapter as the main screen.
 * Tapping a recipe opens the detail screen, which reads it from SQLite.
 */
class CookbookFragment : Fragment() {

    private var _binding: FragmentCookbookBinding? = null
    private val binding get() = _binding!!

    // Same ViewModel as MealListFragment (it belongs to the Activity).
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: MealAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCookbookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    /**
     * Recipes are saved or removed on ANOTHER screen (DetailActivity).
     * That's why we reload the cookbook every time it becomes visible again.
     */
    override fun onResume() {
        super.onResume()
        viewModel.loadSavedMeals()
    }

    /** With show/hide, onResume isn't called when switching tabs: we reload when shown. */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) viewModel.loadSavedMeals()
    }

    private fun setupRecyclerView() {
        adapter = MealAdapter { meal ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_MEAL_ID, meal.id)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.savedMeals.observe(viewLifecycleOwner) { meals ->
            adapter.submitList(meals)
            binding.tvEmpty.isVisible = meals.isEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}