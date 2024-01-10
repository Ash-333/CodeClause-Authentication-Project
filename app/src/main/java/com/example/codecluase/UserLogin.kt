package com.example.codecluase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("profileScreen")
        }
    }

    val provider = OAuthProvider.newBuilder("github.com")


    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(context, auth, account.idToken!!, navController)
        } catch (e: ApiException) {
            Log.d("ERROR", e.toString())
        }
    }

    val facebookCallback = rememberUpdatedState(newValue = {
        val callbackManager = CallbackManager.Factory.create()
        val auth = FirebaseAuth.getInstance()

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    firebaseAuthWithFacebook(result.accessToken, auth, navController, context)
                }

                override fun onCancel() {
                    // Handle cancellation
                    Log.d("ERROR", "Facebook login canceled")
                }

                override fun onError(error: FacebookException) {
                    // Handle error
                    Log.d("ERROR", "Facebook login error: ${error.message}")
                }
            })

        callbackManager
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.codeclause),
            contentDescription = "logo",
            modifier = Modifier
                .height(150.dp)
                .width(150.dp)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(text = "Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            visualTransformation = PasswordVisualTransformation(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), // Ensure Row spans the full width
            horizontalArrangement = Arrangement.End // Align content to the right
        ) {
            Text(text = "Forgot password?", color = Color.Blue)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Button(modifier = Modifier.fillMaxWidth(), onClick = {
            emailPasswordLogin(email.value, password.value, auth, context, navController)
        }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Login with")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { googleLauncher.launch(getSignInIntent(context)) },
            ) {
                Text(text = "Google")
            }
            Button(
                onClick = {
                    val callbackManager = facebookCallback.value
                    val loginManager = LoginManager.getInstance()
                    loginManager.logInWithReadPermissions(context as Activity, listOf("email"))
                },

                ) {
                Text(text = "Facebook")
            }

            Button(
                onClick = {
                    signInWithGithub(context as Activity, auth, navController, context)
                },
            ) {
                Text(text = "Github")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Text(text = "Don't have an account?")
            Text(text = "Signup", fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                navController.navigate("registerScreen")
            })
        }

    }
}

private fun emailPasswordLogin(
    email: String,
    password: String,
    auth: FirebaseAuth,
    context: Context,
    navController: NavHostController
) {
    if(email.isEmpty() or password.isEmpty()){
        Toast.makeText(context,"Please enter required credentials",Toast.LENGTH_SHORT).show()
    }
    else{
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            navController.navigate("profileScreen")
        }.addOnFailureListener {
            Log.d("Error",it.message.toString())
            Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
        }
    }
}

private fun getSignInIntent(context: Context): Intent {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso).signInIntent
}

private fun firebaseAuthWithGoogle(
    context: Context,
    auth: FirebaseAuth,
    idToken: String,
    navController: NavHostController
) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                Log.d("ERROR", "Successful registered")
                navController.navigate("profileScreen")
                Toast.makeText(context, "Google login successful", Toast.LENGTH_LONG).show()
            } else {
                // Handle sign-in failure
                Log.d("ERROR", "not registered")
                Toast.makeText(context, "Google login not successful", Toast.LENGTH_LONG).show()
            }
        }
}

private fun firebaseAuthWithFacebook(
    accessToken: AccessToken,
    auth: FirebaseAuth,
    navController: NavHostController,
    context: Context
) {
    val credential = FacebookAuthProvider.getCredential(accessToken.token)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("ERROR", "Successful Facebook login")
                navController.navigate("profileScreen")
                Toast.makeText(context, "Successful Facebook login", Toast.LENGTH_LONG).show()
            } else {
                Log.d("ERROR", "Facebook login not successful")
                Toast.makeText(context, "Facebook login not successful", Toast.LENGTH_LONG).show()
            }
        }
}

fun signInWithGithub(
    activity: Activity,
    auth: FirebaseAuth,
    navController: NavHostController,
    context: Context
) {
    val provider = OAuthProvider.newBuilder("github.com")
    auth.startActivityForSignInWithProvider(activity, provider.build())
        .addOnSuccessListener {
            navController.navigate("profileScreen")
            Log.d("GITHUB", "SUCCESS")
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Github login not successful", Toast.LENGTH_LONG).show()
            Log.d("GitERROR", e.message.toString())
        }
}
