package com.example.codecluase

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ProfileScreen(navController: NavHostController) {
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = user?.photoUrl?:"https://icons.veryicon.com/png/o/miscellaneous/two-color-icon-library/user-286.png"),
            contentDescription = "User profile image",
            modifier = Modifier
                .height(120.dp)
                .width((120.dp))
                .clip(CircleShape)
                .border(
                    1.dp, Color.Cyan,
                    CircleShape
                )
        )
        Text(text = "Welcome, ${user?.displayName}")

        TextButton(onClick = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login")
        }) {
            Text("Sign Out")
        }
    }
}
