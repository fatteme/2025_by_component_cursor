package com.example.movieswipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.example.movieswipe.domain.usecase.GetGenresUseCase
import com.example.movieswipe.ui.auth.AuthViewModel
import com.example.movieswipe.ui.group.GroupEntryScreen
import com.example.movieswipe.ui.group.GroupDetailScreen
import com.example.movieswipe.ui.group.GroupPreferencesScreen
import com.example.movieswipe.ui.voting.VotingResultScreen
import com.example.movieswipe.ui.voting.VotingSessionScreen
import com.example.movieswipe.ui.movie.MovieViewModel
import com.example.movieswipe.domain.model.Movie
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val user by authViewModel.user.collectAsState()
                    var currentScreen by remember { mutableStateOf("auth") }
                    var groupId by remember { mutableStateOf<String?>(null) }
                    var votingResult by remember { mutableStateOf<com.example.movieswipe.domain.model.VotingResult?>(null) }
                    val getGenresUseCase = GetGenresUseCase(hiltViewModel())
                    val movieViewModel: MovieViewModel = hiltViewModel()
                    suspend fun getMovie(tmdbId: Int): Movie? {
                        return movieViewModel.getMovieUseCase.invoke(tmdbId).getOrNull()
                    }
                    when (currentScreen) {
                        "auth" -> AuthScreen(viewModel = authViewModel)
                        "group_entry" -> GroupEntryScreen(onGroupReady = {
                            groupId = it
                            currentScreen = "group_detail"
                        })
                        "group_detail" -> GroupDetailScreen(
                            groupId = groupId!!,
                            onPreferences = {
                                currentScreen = "group_prefs"
                            },
                            onDelete = {
                                groupId = null
                                currentScreen = "group_entry"
                            }
                        )
                        "group_prefs" -> GroupPreferencesScreen(
                            groupId = groupId!!,
                            getGenresUseCase = getGenresUseCase,
                            onPreferencesSet = {
                                currentScreen = "voting_session"
                            }
                        )
                        "voting_session" -> VotingSessionScreen(
                            groupId = groupId!!,
                            userId = user?.id ?: "",
                            getMovie = { getMovie(it) },
                            onVotingEnd = {
                                votingResult = it
                                currentScreen = "voting_result"
                            }
                        )
                        "voting_result" -> VotingResultScreen(
                            result = votingResult!!,
                            getMovie = { getMovie(it) }
                        )
                    }
                    // Navigation logic
                    LaunchedEffect(user) {
                        if (user != null && currentScreen == "auth") currentScreen = "group_entry"
                    }
                }
            }
        }
    }
}
