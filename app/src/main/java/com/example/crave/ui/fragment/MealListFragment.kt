package com.example.crave.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crave.DetailActivity
import com.example.crave.databinding.FragmentMealListBinding
import com.example.crave.ui.adapter.MealAdapter
import com.example.crave.ui.viewmodel.MainViewModel

/**
 * Main screen: meals fetched from the API.
 *
 * Responsibilities (VIEW only):
 *  1. Inflate the layout with ViewBinding.
 *  2. Set up the RecyclerView.
 *  3. Observe the ViewModel's LiveData and reflect it on screen.
 *  4. Notify the ViewModel of user events.
 */
class MealListFragment : Fragment() {

    // ViewBinding in Fragments: set to null in onDestroyView to avoid memory leaks.
    private var _binding: FragmentMealListBinding? = null
    private val binding get() = _binding!!

    // activityViewModels(): ViewModel tied to the Activity (shared with the cookbook).
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: MealAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupCategorySpinner()
        setupSearch()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MealAdapter { meal ->
            // Open the detail screen (Compose), passing the id of the tapped meal.
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_MEAL_ID, meal.id)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupCategorySpinner() {
        // When the user picks an option, we notify the ViewModel.
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val category = binding.spinnerCategory.getItemAtPosition(position) as String
                // If the user changed category, the search text is cleared.
                if (category != viewModel.currentCategory) binding.etSearch.text?.clear()
                viewModel.selectCategory(category)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSearch() {
        // Keyboard search key: search the typed text (name or ingredient).
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchMeals(binding.etSearch.text.toString())
                true
            } else {
                false
            }
        }

        // X button: clears the text and goes back to the selected category's list.
        binding.tilSearch.setEndIconOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.searchMeals("")
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            // ArrayAdapter: simple adapter that turns each String into a Spinner row.
            val spinnerAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item, categories
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = spinnerAdapter

            // Preselect the current category (Beef on launch, or the chosen one after rotation).
            val index = categories.indexOf(viewModel.currentCategory)
            if (index >= 0) binding.spinnerCategory.setSelection(index)
        }

        viewModel.meals.observe(viewLifecycleOwner) { meals ->
            adapter.submitList(meals)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            binding.tvMessage.isVisible = message != null
            binding.tvMessage.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}