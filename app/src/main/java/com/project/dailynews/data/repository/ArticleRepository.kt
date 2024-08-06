package com.project.dailynews.data.repository

import com.project.dailynews.data.local.ArticleDatabase
import com.project.dailynews.data.model.Article
import com.project.dailynews.data.remote.NewsApi
import com.project.dailynews.data.util.Constants.Companion.API_KEY
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val database: ArticleDatabase,
    private val newsApi: NewsApi,
) {

    suspend fun getAllArticles(searchQuery: String, pageNumber: Int) =
        newsApi.getNews(searchQuery, pageNumber, API_KEY)

    fun getFavoriteArticles() = database.articleDao().getArticles()

    suspend fun insert(article: Article) = database.articleDao().insert(article)

    suspend fun deleteArticle(article: Article) = database.articleDao().deleteArticle(article)
}