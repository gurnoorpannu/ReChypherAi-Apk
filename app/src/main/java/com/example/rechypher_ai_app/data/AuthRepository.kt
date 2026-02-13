package com.example.rechypher_ai_app.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    // Configure Google Sign-In
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(com.example.rechypher_ai_app.R.string.default_web_client_id))
        .requestEmail()
        .build()
    
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    
    // Get current user
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    // Sign in with Google credential
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Sign in failed: User is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sign out
    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }
}
