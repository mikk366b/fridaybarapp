package com.example.fridaybarapp.components


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
import com.example.fridaybarapp.firestore.service.FireStore
import kotlinx.coroutines.launch

@Composable
fun CreateCrawls(service: FireStore, nav: NavController) {
    val name = remember { mutableStateOf("") }
    val crawlId = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column() {
        Row() {
            Text("Name:")
            TextField(value = name.value, onValueChange = { newText -> name.value = newText })
        }
        Row() {
            Text("crawlId:")
            TextField(value = crawlId.value, onValueChange = { newText -> crawlId.value = newText })
        }
        Button(onClick = {
            scope.launch {
                service.createCrawl(crawlId.value,name.value)
                //nav.navigate("Horses")
            }
        }) {
            Text("Create")
        }
    }
}