package com.meadow.app.utils.system

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * GoogleSignInHelper.kt
 *
 * Handles Google authentication for collaborators and Drive/Calendar access.
 */

object GoogleSignInHelper {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun init(activity: Activity, webClientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    fun handleSignInResult(task: Task<GoogleSignInAccount>, onResult: (Boolean) -> Unit) {
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    onResult(authTask.isSuccessful)
                }
        } catch (e: Exception) {
            Log.e("GoogleSignInHelper", "Sign-in failed: ${e.message}")
            onResult(false)
        }
    }

    fun signOut(onComplete: () -> Unit) {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener { onComplete() }
    }

    fun getCurrentUserEmail(): String? = firebaseAuth.currentUser?.email
    fun getCurrentUserName(): String? = firebaseAuth.currentUser?.displayName
}