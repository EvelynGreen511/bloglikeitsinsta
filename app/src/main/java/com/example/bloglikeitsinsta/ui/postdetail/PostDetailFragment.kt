package com.example.bloglikeitsinsta.ui.postdetail

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.bloglikeitsinsta.databinding.FragmentPostDetailBinding
import com.example.bloglikeitsinsta.wordpress.model.WordPressPost
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PostDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        val post = args.post

        // Check if content contains images
        val contentHasImages = post.content.rendered.contains("<img", ignoreCase = true)

        if (contentHasImages) {
            // Content has images - use WebView to display everything
            showContentInWebView(post)
            binding.imageFeatured.visibility = View.GONE
        } else {
            // Content has no images - show featured image separately + text content
            showFeaturedImageAndTextContent(post)
            binding.webViewContent.visibility = View.GONE
        }

        // Common UI elements
        binding.apply {
            textTitle.text = Html.fromHtml(post.title.rendered, Html.FROM_HTML_MODE_COMPACT)
            textDate.text = formatDate(post.date)
        }
    }


    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    private fun showContentInWebView(post: WordPressPost) {
        val htmlContent = """
        <html>
          <head>
            <style>
              img { 
                display: block; 
                max-width: 100%; 
                height: auto; 
              }
              body {
                margin: 0;
                padding: 8px;
                box-sizing: border-box;
                font-family: sans-serif;
              }
            </style>
          </head>
          <body>
            ${post.content.rendered}
          </body>
        </html>
    """.trimIndent()

        binding.webViewContent.apply {
            visibility = View.VISIBLE
            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    }

    private fun showFeaturedImageAndTextContent(post: WordPressPost) {
        // Show featured image
        val featuredImageUrl = post.embedded?.featuredMedia?.firstOrNull()?.sourceUrl
        if (featuredImageUrl != null) {
            binding.imageFeatured.load(featuredImageUrl)
            binding.imageFeatured.visibility = View.VISIBLE
        } else {
            binding.imageFeatured.visibility = View.GONE
        }

        // Show text content only (no images)
        binding.textContent.apply {
            visibility = View.VISIBLE
            text = Html.fromHtml(post.content.rendered, Html.FROM_HTML_MODE_COMPACT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}