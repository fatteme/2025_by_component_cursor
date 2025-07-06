package com.example.movieswipe.ui.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movieswipe.domain.model.Genre
import com.example.movieswipe.domain.usecase.GetGenresUseCase
import androidx.hilt.navigation.compose.hiltViewModel

// Screens for group management (create, join, manage)
object GroupScreen {
    // ...to be implemented
}

@Composable
fun GroupEntryScreen(
    onGroupReady: (String) -> Unit,
    viewModel: GroupViewModel = hiltViewModel()
) {
    var groupName by remember { mutableStateOf("") }
    var invitationCode by remember { mutableStateOf("") }
    val group by viewModel.group.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    if (group != null) {
        onGroupReady(group!!.id)
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create or Join a Group", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.createGroup(groupName) },
            enabled = groupName.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text("Create Group") }
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = invitationCode,
            onValueChange = { invitationCode = it },
            label = { Text("Invitation Code") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.joinGroup(invitationCode) },
            enabled = invitationCode.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text("Join Group") }
        if (loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun GroupDetailScreen(
    groupId: String,
    viewModel: GroupViewModel = hiltViewModel(),
    onPreferences: (String) -> Unit,
    onDelete: () -> Unit
) {
    val group by viewModel.group.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(groupId) { viewModel.getGroup(groupId) }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    if (group == null) return

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Group: ${group!!.name}", style = MaterialTheme.typography.titleLarge)
        Text("Invitation Code: ${group!!.invitationCode ?: "-"}")
        Text("Members: ${group!!.members.size}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onPreferences(groupId) }, modifier = Modifier.fillMaxWidth()) {
            Text("Set Preferences")
        }
        Button(onClick = { viewModel.deleteGroup(groupId); onDelete() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Delete Group")
        }
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun GroupPreferencesScreen(
    groupId: String,
    getGenresUseCase: GetGenresUseCase,
    onPreferencesSet: () -> Unit,
    viewModel: GroupViewModel = hiltViewModel()
) {
    var genres by remember { mutableStateOf<List<Genre>>(emptyList()) }
    var selectedGenres by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        val result = getGenresUseCase()
        result.onSuccess { genres = it }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select your favorite genres for this group:", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        genres.forEach { genre ->
            Row(
                Modifier.fillMaxWidth().clickable {
                    selectedGenres = selectedGenres.toMutableSet().apply {
                        if (contains(genre.tmdbId)) remove(genre.tmdbId) else add(genre.tmdbId)
                    }
                }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedGenres.contains(genre.tmdbId),
                    onCheckedChange = {
                        selectedGenres = selectedGenres.toMutableSet().apply {
                            if (contains(genre.tmdbId)) remove(genre.tmdbId) else add(genre.tmdbId)
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text(genre.name)
            }
        }
        if (error != null) {
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = {
                viewModel.setPreferences(groupId, selectedGenres.toList())
                onPreferencesSet()
            },
            enabled = selectedGenres.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Save Preferences")
        }
        if (loading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
