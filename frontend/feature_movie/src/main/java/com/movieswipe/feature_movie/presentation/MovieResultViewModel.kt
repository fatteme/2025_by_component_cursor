package com.movieswipe.feature_movie.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieswipe.feature_movie.data.model.MovieDTO
import com.movieswipe.feature_movie.domain.usecase.GetMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

sealed class MovieResultUiState {
    object Loading : MovieResultUiState()
    data class Success(val movie: MovieDTO) : MovieResultUiState()
    data class Error(val message: String) : MovieResultUiState()
}

@HiltViewModel
class MovieResultViewModel @Inject constructor(
    private val getMovieUseCase: GetMovieUseCase
) : ViewModel() {
    var uiState by mutableStateOf<MovieResultUiState>(MovieResultUiState.Loading)
        private set

    fun loadMovie(movieId: Int) {
        uiState = MovieResultUiState.Loading
        viewModelScope.launch {
            val response: Response<com.movieswipe.feature_movie.data.model.MovieResponseDTO> = getMovieUseCase(movieId)
            if (response.isSuccessful && response.body() != null) {
                uiState = MovieResultUiState.Success(response.body()!!.movie)
            } else {
                uiState = MovieResultUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }
}

