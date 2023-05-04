package com.example.fridaybarapp.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.fridaybarapp.firestore.service.FireStore
import com.example.fridaybarapp.firestore.service.Bar

@Composable
fun Bars(service: FireStore, nav: NavController) {
    val bars = remember { mutableStateOf(emptyList<Bar>()) }
    LaunchedEffect(Unit) {
        val list = service.getFarvoritesbars()
        bars.value = list
    }
    Card() {
        Column() {
            bars.value.map {
                Column() {
                    Row() {
                        Text("Id: ")
                        Text(it.id)
                    }
                    Row() {
                        Text("Name: ")
                        Text(it.name)
                    }
                }
            }
            Button(onClick = { nav.navigate("CreateBar") }) {
                Text("Create Horse")
            }
        }
    }
}
