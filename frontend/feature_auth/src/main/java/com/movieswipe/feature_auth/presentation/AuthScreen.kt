package com.movieswipe.feature_auth.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.signInWithGoogle(idToken)
            } else {
                viewModel.resetState()
            }
        } catch (e: Exception) {
            viewModel.resetState()
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text((authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                SignInButton { launchGoogleSignIn(context, launcher) }
            }
            else -> SignInButton { launchGoogleSignIn(context, launcher) }
        }
    }
}

@Composable
fun SignInButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Sign in with Google")
    }
}

private fun launchGoogleSignIn(context: Context, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com")
        .requestEmail()
        .build()
    val client: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(client.signInIntent)
}

