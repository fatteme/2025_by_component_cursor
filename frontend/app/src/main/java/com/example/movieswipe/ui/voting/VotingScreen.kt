package com.example.movieswipe.ui.voting

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.movieswipe.domain.model.VotingSession
import com.example.movieswipe.domain.model.VotingResult
import com.example.movieswipe.domain.model.Movie

@Composable
fun VotingSessionScreen(
    groupId: String,
    userId: String,
    votingViewModel: VotingViewModel = hiltViewModel(),
    getMovie: suspend (Int) -> Movie?,
    onVotingEnd: (VotingResult?) -> Unit
) {
    val session by votingViewModel.session.collectAsState()
    val loading by votingViewModel.loading.collectAsState()
    val error by votingViewModel.error.collectAsState()
    val result by votingViewModel.result.collectAsState()
    var currentIndex by remember { mutableStateOf(0) }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var votedMovies by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Load session and movies
    LaunchedEffect(groupId) {
        votingViewModel.getVotingSession(groupId)
    }
    LaunchedEffect(session) {
        session?.let {
            val loaded = it.movies.mapNotNull { tmdbId -> getMovie(tmdbId) }
            movies = loaded
        }
    }
    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (result != null) {
        onVotingEnd(result)
        return
    }
    if (session == null || movies.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No movies to vote on.") }
        return
    }
    val movie = movies.getOrNull(currentIndex)
    if (movie == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("You have voted on all movies.")
            Button(onClick = { votingViewModel.endVotingSession(session!!.id) }, modifier = Modifier.padding(top = 16.dp)) {
                Text("End Voting Session")
            }
        }
        return
    }
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Swipe right for YES, left for NO", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(movie.tmdbId) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        if (dragAmount > 100) {
                            votingViewModel.submitVote(session!!.id, movie.tmdbId, true)
                            votedMovies = votedMovies + movie.tmdbId
                            currentIndex++
                        } else if (dragAmount < -100) {
                            votingViewModel.submitVote(session!!.id, movie.tmdbId, false)
                            votedMovies = votedMovies + movie.tmdbId
                            currentIndex++
                        }
                    }
                }
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(movie.title, style = MaterialTheme.typography.titleLarge)
                Text(movie.overview, style = MaterialTheme.typography.bodyMedium)
                Text("Genres: " + movie.genres.joinToString { it.name })
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                votingViewModel.submitVote(session!!.id, movie.tmdbId, false)
                votedMovies = votedMovies + movie.tmdbId
                currentIndex++
            }) { Text("No") }
            Button(onClick = {
                votingViewModel.submitVote(session!!.id, movie.tmdbId, true)
                votedMovies = votedMovies + movie.tmdbId
                currentIndex++
            }) { Text("Yes") }
        }
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun VotingResultScreen(result: VotingResult, getMovie: suspend (Int) -> Movie?) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    LaunchedEffect(result.movieId) {
        movie = getMovie(result.movieId)
    }
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Voting Result", style = MaterialTheme.typography.titleLarge)
        if (movie != null) {
            Text(movie!!.title, style = MaterialTheme.typography.titleMedium)
            Text(movie!!.overview, style = MaterialTheme.typography.bodyMedium)
            Text("Genres: " + movie!!.genres.joinToString { it.name })
        }
        Text("Yes votes: ${result.yesVotes}")
        Text("No votes: ${result.noVotes}")
        Text("Score: ${result.score}")
    }
}
