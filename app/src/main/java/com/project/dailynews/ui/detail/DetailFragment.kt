package com.project.dailynews.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.dailynews.R
import com.project.dailynews.data.model.Article
import com.project.dailynews.databinding.FragmentDetailBinding
import com.project.dailynews.ui.news.NewsViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()

    private val args: DetailFragmentArgs by navArgs()

    lateinit var article: Article

    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        article = args.article
        isFavorite = args.favorite
        bind()
    }

    private fun bind() {
        binding.apply {
            newsTitle.text = article.title
            newsText.text = article.content
            newsSource.text = article.source?.name

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate: String? = article.publishedAt?.let {
                inputFormat.parse(it)?.let { date -> outputFormat.format(date) }
            }

            newsDate.text = formattedDate ?: "N/A"
            Picasso.get().load(article.urlToImage).into(newsImage)

            goToSource.setOnClickListener {
                val action =
                    DetailFragmentDirections.actionNavigationDetailToArticleWebViewFragment(article.url)
                findNavController().navigate(action)
            }
        }
    }

    private fun setIcon(item: MenuItem) {
        item.icon =
            if (isFavorite)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.baseline_favorite)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.baseline_favorite_border)
    }

    private fun shareArticle(article: Article) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, article.url)
            putExtra(Intent.EXTRA_SUBJECT, article.title)
        }
        startActivity(Intent.createChooser(intent, null))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_action_bar, menu)
        setIcon(menu.getItem(1))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                shareArticle(article)
                true
            }
            R.id.favorite -> {
                if (isFavorite) {
                    viewModel.removeArticleFromFavorites(article)
                } else {
                    viewModel.addArticleToFavorites(article)
                }
                isFavorite = !isFavorite
                setIcon(item)
                true
            }
            else -> {
                findNavController().navigateUp()
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}