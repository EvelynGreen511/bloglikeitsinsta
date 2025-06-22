package com.example.bloglikeitsinsta.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloglikeitsinsta.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()

        return binding.root
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter { post ->
            // Navigate to post detail fragment
            val action = HomeFragmentDirections.actionNavHomeToPostDetailFragment(post)
            findNavController().navigate(action)
        }

        binding.recyclerViewPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postsAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                binding.swipeRefreshLayout.isRefreshing = state.isLoading

                if (state.posts.isNotEmpty()) {
                    binding.recyclerViewPosts.visibility = View.VISIBLE
                    binding.textEmptyState.visibility = View.GONE
                    postsAdapter.submitList(state.posts)
                } else if (!state.isLoading) {
                    binding.recyclerViewPosts.visibility = View.GONE
                    binding.textEmptyState.visibility = View.VISIBLE
                    binding.textEmptyState.text = state.errorMessage ?: "No posts available"
                }

                state.errorMessage?.let { message ->
                    if (!state.isLoading) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}