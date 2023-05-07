package com.example.fridaybarapp.components.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import com.example.fridaybarapp.firestore.service.FireStore
import kotlinx.coroutines.launch
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.fridaybarapp.MainActivity
import com.example.fridaybarapp.firestore.service.User
import com.google.maps.android.Context.getApplicationContext
import java.security.AccessController.getContext

@Composable
fun SignupLogin(service: FireStore) { //, nav: NavController
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var isPasswordVisible = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        Icon(
                            imageVector = if (isPasswordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (isPasswordVisible.value) "Hide password" else "Show password"
                        )
                    }
                }
            )
        }
        Button(onClick = {
            scope.launch {
                val result = service.login(email.value, password.value)
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                //nav.navigate("GetCrawl")
            }
        }) {
            Text("Login")
        }
        Button(onClick = {
            scope.launch {
                val result = service.signup(email.value, password.value)
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                //nav.navigate("GetCrawl")
            }
        }) {
            Text("Signup")
        }
    }
}