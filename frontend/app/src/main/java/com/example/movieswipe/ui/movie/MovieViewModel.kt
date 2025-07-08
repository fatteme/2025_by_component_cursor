package com.example.movieswipe.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieswipe.domain.model.Genre
import com.example.movieswipe.domain.model.Movie
import com.example.movieswipe.domain.model.MovieRecommendation
import com.example.movieswipe.domain.model.UserPreference
import com.example.movieswipe.domain.usecase.GetGenresUseCase
import com.example.movieswipe.domain.usecase.GetMovieUseCase
import com.example.movieswipe.domain.usecase.RecommendMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase,
    private val recommendMovieUseCase: RecommendMovieUseCase,
    private val getMovieUseCase: GetMovieUseCase
) : ViewModel() {
    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _selectedGenres = MutableStateFlow<Set<Int>>(emptySet())
    val selectedGenres: StateFlow<Set<Int>> = _selectedGenres

    private val _recommendation = MutableStateFlow<MovieRecommendation?>(null)
    val recommendation: StateFlow<MovieRecommendation?> = _recommendation

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadGenres() {
        viewModelScope.launch {
            _loading.value = true
            val result = getGenresUseCase()
            _loading.value = false
            result.onSuccess { _genres.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun toggleGenre(tmdbId: Int) {
        _selectedGenres.value = _selectedGenres.value.toMutableSet().apply {
            if (contains(tmdbId)) remove(tmdbId) else add(tmdbId)
        }
    }

    fun recommendMovie(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            val prefs = listOf(UserPreference(userId, _selectedGenres.value.toList()))
            val result = recommendMovieUseCase(prefs)
            _loading.value = false
            result.onSuccess { _recommendation.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun clearRecommendation() {
        _recommendation.value = null
    }
}

