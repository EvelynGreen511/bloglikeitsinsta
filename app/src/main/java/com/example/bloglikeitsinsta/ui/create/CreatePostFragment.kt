package com.example.bloglikeitsinsta.ui.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.bloglikeitsinsta.databinding.FragmentCreatePostBinding
import com.example.bloglikeitsinsta.wordpress.api.FileUtils
import kotlinx.coroutines.launch
import java.io.File
import androidx.navigation.fragment.findNavController

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    private val viewModel: CreatePostViewModel by viewModels()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.imageSelected.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        binding.buttonSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.buttonPublish.setOnClickListener {
            val content = binding.editTextContent.text.toString()
            val title = binding.editTextTitle.text.toString()
            if (imageUri == null || content.isBlank() || title.isBlank()) {
                Toast.makeText(requireContext(), "Please select an image and enter title / text.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            handleImageAndCreatePost(imageUri!!, title, content)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            if (state.isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
            state.message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

            if (state.shouldNavigateBack) {
                findNavController().popBackStack()
            }
        }

        return binding.root
    }

    private fun handleImageAndCreatePost(uri: Uri, title: String, content: String) {
        val context = requireContext()
        val mimeType = context.contentResolver.getType(uri)
        Log.e("CreatePostFragment", "MimeType: " + mimeType)
        lifecycleScope.launch {
            val file: File? = when (mimeType) {
                "image/jpeg", "image/png" -> FileUtils.getFileFromUri(context, uri)
                else -> convertToPng(context, uri)
            }
            if (file == null) {
                Toast.makeText(context, "Failed to process image.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val postImgMimeType = when (mimeType) { "image/jpeg" -> "image/jpeg" else -> "image/png" }
            viewModel.createPost(file, title, content,  postImgMimeType)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Converts any image to PNG using modern ImageDecoder
    private fun convertToPng(context: Context, uri: Uri): File? {
        return try {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.png")
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // PNG quality is always 100
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}