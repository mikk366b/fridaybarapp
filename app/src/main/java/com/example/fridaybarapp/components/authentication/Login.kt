package com.example.fridaybarapp.components.authentication


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import com.example.fridaybarapp.firestore.service.FireStore
import kotlinx.coroutines.launch

@Composable
fun Login(service: FireStore, nav: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column() {
        Row() {
            Text("Email:")
            TextField(value = email.value, onValueChange = { newText -> email.value = newText })
        }
        Row() {
            Text("Password:")
            TextField(
                value = password.value,
                onValueChange = { newText -> password.value = newText },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }
        Button(onClick = {
            scope.launch {
                val user = service.login(email.value, password.value)
                nav.navigate("GetCrawl")
            }
        }) {
            Text("Login")
        }
    }
}