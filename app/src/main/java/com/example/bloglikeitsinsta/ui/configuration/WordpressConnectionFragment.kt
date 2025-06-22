package com.example.bloglikeitsinsta.ui.configuration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.bloglikeitsinsta.databinding.FragmentWordpressConnectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WordpressConnectionFragment : Fragment() {
    private var _binding: FragmentWordpressConnectionBinding? = null
    private val binding get() = _binding!!

    // Use Hilt's viewModels() delegate
    private val viewModel: WordpressConnectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordpressConnectionBinding.inflate(inflater, container, false)

        setupUI()
        observeViewModel()
        viewModel.loadCurrentSettings()

        return binding.root
    }

    private fun setupUI() {
        binding.btnSave.setOnClickListener {
            val url = binding.etWordpressUrl.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (url.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                viewModel.saveSettings(url, username, password)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnTestConnection.setOnClickListener {
            viewModel.testConnection()
        }

        binding.btnClearToken.setOnClickListener {
            viewModel.clearToken()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                binding.btnSave.isEnabled = !state.isLoading
                binding.btnTestConnection.isEnabled = !state.isLoading
                binding.btnClearToken.isEnabled = !state.isLoading

                binding.tvTokenStatus.text = if (state.hasValidToken) {
                    "✅ Authenticated"
                } else {
                    "❌ Not Authenticated"
                }

                state.message?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currentSettings.collect { settings ->
                binding.etWordpressUrl.setText(settings.url)
                binding.etUsername.setText(settings.username)
                binding.etPassword.setText(settings.password)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}