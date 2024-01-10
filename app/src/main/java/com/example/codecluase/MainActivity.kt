package com.example.codecluase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codecluase.ui.theme.CodeCluaseTheme
import com.facebook.FacebookSdk

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.setApplicationId("360108409980897")
        FacebookSdk.sdkInitialize(applicationContext)
        setContent {
            CodeCluaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App(){
    val navController= rememberNavController()
    NavHost(navController = navController, startDestination = "login"){
        composable(route="login"){
            SignInScreen(navController)
        }
        composable(route="profileScreen"){
            ProfileScreen(navController)
        }
        composable(route="registerScreen"){
            RegisterScreen(navController)
        }
    }
}
