package com.barbuddy.fridaybarapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.barbuddy.fridaybarapp.firestore.service.FireStore
import kotlinx.coroutines.launch

@Composable
fun CreateFavBar(service: FireStore, nav: NavController) {
    val name = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column() {
        Row() {
            Text("Name:")
            TextField(value = name.value, onValueChange = { newText -> name.value = newText })
        }
        Button(onClick = {
            scope.launch {
                service.createFarvoritesbars(name.value)
                //nav.navigate("Horses")
            }
        }) {
            Text("Create")
        }
    }
}