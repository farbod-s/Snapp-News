package com.snappbox.newsdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snappbox.data.model.NewsDto
import com.snappbox.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: NewsRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val article: StateFlow<NewsDto?> = savedStateHandle.getStateFlow<String?>("articleId", null)
        .mapLatest { articleId ->
            repository.getArticleById(articleId.orEmpty())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}