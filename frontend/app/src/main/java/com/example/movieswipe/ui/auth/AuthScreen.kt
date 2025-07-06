package com.example.movieswipe.ui.auth

import android.app.Activity
import android.content.Context
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
import com.example.movieswipe.ui.auth.AuthViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException

// UI for Google Sign-In and authentication
@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val user by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    val oneTapClient = remember {
        Identity.getSignInClient(context)
    }

    val signInRequest = remember {
        BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("YOUR_GOOGLE_CLIENT_ID") // TODO: Replace with real client ID
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
        val idToken = credential.googleIdToken
        if (idToken != null) {
            viewModel.authenticateWithGoogle(idToken)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user == null) {
            Button(onClick = {
                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(activity!!) { result ->
                        launcher.launch(
                            ActivityResultContracts.StartIntentSenderForResult().createIntent(context, result.pendingIntent.intentSender, null, 0, 0, 0, null)
                        )
                    }
                    .addOnFailureListener {
                        // Handle error
                    }
            }) {
                Text("Sign in with Google")
            }
            if (error != null) {
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Text("Welcome, ${user?.name}")
            Text("Email: ${user?.email}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.logout() }) {
                Text("Logout")
            }
        }
    }
}
