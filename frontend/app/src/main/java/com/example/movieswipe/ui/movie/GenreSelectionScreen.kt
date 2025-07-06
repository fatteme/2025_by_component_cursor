package com.example.movieswipe.ui.movie

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.movieswipe.domain.model.MovieRecommendation

@Composable
fun GenreSelectionScreen(
    userId: String,
    viewModel: MovieViewModel = hiltViewModel()
) {
    val genres by viewModel.genres.collectAsState()
    val selectedGenres by viewModel.selectedGenres.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val recommendation by viewModel.recommendation.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadGenres() }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (recommendation != null) {
        RecommendationScreen(recommendation!!, onBack = { viewModel.clearRecommendation() })
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select your favorite genres:", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f)) {
            items(genres) { genre ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleGenre(genre.tmdbId) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedGenres.contains(genre.tmdbId),
                        onCheckedChange = { viewModel.toggleGenre(genre.tmdbId) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(genre.name)
                }
            }
        }
        if (error != null) {
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = { viewModel.recommendMovie(userId) },
            enabled = selectedGenres.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Get Recommendation")
        }
    }
}

@Composable
fun RecommendationScreen(recommendation: MovieRecommendation, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Recommended Movie:", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(recommendation.movie.title, style = MaterialTheme.typography.titleMedium)
        Text(recommendation.movie.overview, style = MaterialTheme.typography.bodyMedium)
        Text("Genres: " + recommendation.movie.genres.joinToString { it.name })
        Text("Score: ${recommendation.score}")
        Button(onClick = onBack, modifier = Modifier.padding(top = 24.dp)) {
            Text("Back to Genres")
        }
    }
}

