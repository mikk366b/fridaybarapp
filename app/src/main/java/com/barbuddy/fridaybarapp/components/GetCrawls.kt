package com.barbuddy.fridaybarapp.components


import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.barbuddy.fridaybarapp.firestore.service.FireStore
import com.barbuddy.fridaybarapp.firestore.service.Crawl
import org.json.JSONObject


@Composable
fun getCrawls(service: FireStore) { //nav: NavController
    val crawls = remember { mutableStateOf(emptyList<List<Crawl>>()) }
    val crawlId = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        //val list = service.getACrawl(crawlId.value)
        val list = service.getAllCrawl()
        Log.v("Tests",list.toString())
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
                it.map{
                    Column(modifier = Modifier.background(color = Color.Cyan)) {
                        Row() {
                            Text("Bar: ")
                            Text(it.name)
                        }
                    }
                }
            }
            Button(onClick = { //nav.navigate("GetCrawl")
            }) {
                Text("Get Crawls")
            }
        }
    }
}
