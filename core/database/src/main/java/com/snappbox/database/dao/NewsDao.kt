package com.snappbox.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.snappbox.database.model.LastNewsEntity
import com.snappbox.database.model.NewsEntity
import com.snappbox.database.model.toEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    // region News methods
    @Upsert
    suspend fun insertAll(articles: List<NewsEntity>)

    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun pagingSource(): PagingSource<Int, NewsEntity>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getArticleById(id: String): NewsEntity?

    @Query("SELECT MAX(publishedAt) FROM news")
    suspend fun getLatestPublishedAt(): String?

    @Query("DELETE FROM news")
    suspend fun clearAll()
    // endregion

    // region LastNews methods
    @Query("SELECT * FROM last_news ORDER BY publishedAt DESC")
    suspend fun getLastNews(): List<LastNewsEntity>

    @Query("SELECT COUNT(*) FROM last_news")
    fun getLastNewsCount(): Flow<Int>

    @Query("SELECT MAX(publishedAt) FROM last_news")
    suspend fun getLastNewsLatestPublishedAt(): String?

    @Upsert
    suspend fun insertLastNews(news: List<LastNewsEntity>)

    @Query("DELETE FROM last_news")
    suspend fun clearLastNews()

    @Transaction
    suspend fun migrateLastNews() {
        insertAll(getLastNews().map { it.toEntity() })
        clearLastNews()
    }
    // endregion
}