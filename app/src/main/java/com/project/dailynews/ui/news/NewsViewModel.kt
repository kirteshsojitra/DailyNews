package com.project.dailynews.ui.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import com.project.dailynews.data.model.Article
import com.project.dailynews.data.model.News
import com.project.dailynews.data.repository.ArticleRepository
import com.project.dailynews.data.util.Constants.Companion.DEFAULT_TOPIC
import com.project.dailynews.data.util.Constants.Companion.STARTING_PAGE_INDEX
import com.project.dailynews.data.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val articleRepository: ArticleRepository) : ViewModel() {

    val articles: MutableLiveData<Resource<News>> = MutableLiveData()
    private var newsResponse: News? = null
    var page = STARTING_PAGE_INDEX

    init {
        getAllArticles(DEFAULT_TOPIC)
    }

    fun getAllArticles(q: String) = viewModelScope.launch {
        articles.postValue(Resource.Loading())
        val response = articleRepository.getAllArticles(q, page)

        if (response.isSuccessful) {
            response.body()?.let { news ->
                val filteredArticles = news.articles.filter { it.source?.id != null }
                val filteredNews = news.copy(articles = filteredArticles.toMutableList())
                articles.postValue(handleArticlesResponse(Response.success(filteredNews)))
            }
        } else {
            articles.postValue(Resource.Error(response.message()))
        }
    }

    fun getAllNewArticles(q: String) = viewModelScope.launch {
        articles.postValue(Resource.Loading())
        val response = articleRepository.getAllArticles(q, page)
        articles.postValue(handleNewArticlesResponse(response))
    }

    private fun handleArticlesResponse(response: Response<News>): Resource<News> {
        if (response.isSuccessful) {
            response.body()?.let { news ->
                page++
                if (newsResponse == null) {
                    newsResponse = news
                } else {
                    val oldArticles = newsResponse?.articles
                    val newArticles = news.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(news)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleNewArticlesResponse(response: Response<News>): Resource<News> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }


    fun getFavoriteArticles() = articleRepository.getFavoriteArticles()

    fun addArticleToFavorites(article: Article) = viewModelScope.launch {
        articleRepository.insert(article)
    }

    fun removeArticleFromFavorites(article: Article) = viewModelScope.launch {
        articleRepository.deleteArticle(article)
    }
}