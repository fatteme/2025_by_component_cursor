package com.movieswipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.movieswipe.feature_auth.presentation.AuthScreen
import com.movieswipe.feature_group.presentation.GroupScreen
import com.movieswipe.feature_voting.presentation.VotingScreen
import dagger.hilt.android.AndroidEntryPoint
import com.movieswipe.feature_group.presentation.GroupViewModel
import com.movieswipe.feature_voting.presentation.VotingViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.movieswipe.feature_group.presentation.GroupUiState
import com.movieswipe.feature_voting.presentation.VotingUiState
import com.movieswipe.feature_auth.presentation.AuthViewModel
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Checkbox
import androidx.compose.ui.tooling.preview.Preview
import com.movieswipe.feature_movie.presentation.MovieResultViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Groups : Screen("groups")
    object Preferences : Screen("preferences/{groupId}") {
        fun createRoute(groupId: String) = "preferences/$groupId"
    }
    object Voting : Screen("voting/{sessionId}") {
        fun createRoute(sessionId: String) = "voting/$sessionId"
    }
    object MovieResult : Screen("movie_result/{movieId}") {
        fun createRoute(movieId: Int) = "movie_result/$movieId"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Auth.route) {
                composable(Screen.Auth.route) {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = { navController.navigate(Screen.Groups.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        } }
                    )
                }
                composable(Screen.Groups.route) {
                    val groupViewModel: GroupViewModel = hiltViewModel()
                    GroupScreen(
                        viewModel = groupViewModel,
                        onGroupJoined = { groupId -> navController.navigate(Screen.Preferences.createRoute(groupId)) }
                    )
                }
                composable(Screen.Preferences.route) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                    val votingViewModel: VotingViewModel = hiltViewModel()
                    PreferencesScreen(
                        groupId = groupId,
                        viewModel = votingViewModel,
                        onPreferencesSet = { navController.navigate(Screen.Groups.route) }
                    )
                }
                composable(Screen.Voting.route) { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                    val votingViewModel: VotingViewModel = hiltViewModel()
                    VotingScreen(
                        sessionId = sessionId,
                        viewModel = votingViewModel,
                        onVotingEnd = { navController.navigate(Screen.Groups.route) }
                    )
                }
                composable(Screen.MovieResult.route) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0
                    MovieResultScreen(movieId = movieId)
                }
            }
        }
    }
}

@Composable
fun GroupScreen(viewModel: GroupViewModel, onGroupJoined: (String) -> Unit) {
    val state by viewModel.uiState.collectAsState()
    var groupName by remember { mutableStateOf("") }
    var invitationCode by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { viewModel.loadGroups() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Groups", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        when (state) {
            is GroupUiState.Loading -> CircularProgressIndicator()
            is GroupUiState.Error -> Text((state as GroupUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is GroupUiState.Success -> {
                val groups = (state as GroupUiState.Success).groups
                LazyColumn(Modifier.weight(1f)) {
                    items(groups) { group ->
                        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(group.name, Modifier.weight(1f))
                            Button(onClick = { onGroupJoined(group.id) }) { Text("Join") }
                        }
                    }
                }
            }
            else -> {}
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("New Group Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { viewModel.createGroup(groupName) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Text("Create Group")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = invitationCode,
            onValueChange = { invitationCode = it },
            label = { Text("Invitation Code") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { viewModel.joinGroup(invitationCode) }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Text("Join Group")
        }
    }
}

@Composable
fun PreferencesScreen(groupId: String, viewModel: VotingViewModel, onPreferencesSet: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    var selectedGenres by remember { mutableStateOf(setOf<Int>()) }
    var genres by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    val context = LocalContext.current
    LaunchedEffect(groupId) {
        // Fetch genres from backend (simulate for now)
        genres = listOf(28 to "Action", 12 to "Adventure", 35 to "Comedy", 18 to "Drama")
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select Your Preferred Genres", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        genres.forEach { (id, name) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = selectedGenres.contains(id),
                    onCheckedChange = { checked ->
                        selectedGenres = if (checked) selectedGenres + id else selectedGenres - id
                    }
                )
                Text(name)
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.setPreferences(groupId, selectedGenres.toList()); onPreferencesSet() }, enabled = selectedGenres.isNotEmpty()) {
            Text("Save Preferences")
        }
        if (state is VotingUiState.Error) {
            Text((state as VotingUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun VotingScreen(sessionId: String, viewModel: VotingViewModel, onVotingEnd: (Int?) -> Unit) {
    val state by viewModel.uiState.collectAsState()
    var currentMovieIndex by remember { mutableStateOf(0) }
    val movies = listOf(550, 680, 13, 155) // Simulated movie IDs
    var bestMatchMovieId by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    // Poll voting results every 3 seconds to check if voting has ended and bestMatch is available
    LaunchedEffect(sessionId) {
        while (true) {
            delay(3000)
            val results = viewModel.getVotingResultsSync(sessionId)
            if (results != null && results.isActive == false && results.bestMatch != null) {
                bestMatchMovieId = results.bestMatch.movieId
                break
            }
        }
        if (bestMatchMovieId != null) {
            onVotingEnd(bestMatchMovieId)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Voting Session", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        if (movies.isNotEmpty() && currentMovieIndex < movies.size) {
            Text("Movie ID: ${movies[currentMovieIndex]}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    viewModel.submitVote(sessionId, movies[currentMovieIndex], "no")
                    currentMovieIndex++
                }) { Text("No") }
                Button(onClick = {
                    viewModel.submitVote(sessionId, movies[currentMovieIndex], "yes")
                    currentMovieIndex++
                }) { Text("Yes") }
            }
        } else {
            Button(onClick = { viewModel.endVotingSession(sessionId) }, enabled = bestMatchMovieId == null) {
                Text("End Voting Session")
            }
        }
        if (state is VotingUiState.Error) {
            Text((state as VotingUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun MovieResultScreen(movieId: Int) {
    val viewModel: MovieResultViewModel = hiltViewModel()
    val state = viewModel.uiState
    LaunchedEffect(movieId) { viewModel.loadMovie(movieId) }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            is com.movieswipe.feature_movie.presentation.MovieResultUiState.Loading -> CircularProgressIndicator()
            is com.movieswipe.feature_movie.presentation.MovieResultUiState.Error -> Text((state as com.movieswipe.feature_movie.presentation.MovieResultUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is com.movieswipe.feature_movie.presentation.MovieResultUiState.Success -> {
                val movie = (state as com.movieswipe.feature_movie.presentation.MovieResultUiState.Success).movie
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    movie.posterUrl?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = movie.title,
                            modifier = Modifier.height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(movie.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text("Genres: " + movie.genres.joinToString { it.name }, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Rating: ${movie.rating}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(movie.overview, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
