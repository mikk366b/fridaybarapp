package com.example.fridaybarapp.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.fridaybarapp.firestore.service.FireStore
import com.example.fridaybarapp.firestore.service.Bar
import com.example.fridaybarapp.firestore.service.Crawl

@Composable
fun Crawls(service: FireStore, nav: NavController) {
    val crawls = remember { mutableStateOf(emptyList<Crawl>()) }
    val crawlId = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val list = service.getACrawl(crawlId.value)
        if (list != null) {
            crawls.value = list
        }
    }
    Card() {
        Column() {
            Row() {
                Text("Name:")
                TextField(value = crawlId.value, onValueChange = { newText -> crawlId.value = newText })
            }
            crawls.value.map {
                Column() {
                    Row() {
                        Text("Bar: ")
                        Text(it.name)
                    }
                }
            }
            Button(onClick = { nav.navigate("GetCrawl") }) {
                Text("Get Crawls")
            }
        }
    }
}
