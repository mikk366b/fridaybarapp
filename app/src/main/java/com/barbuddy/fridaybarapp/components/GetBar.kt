package com.barbuddy.fridaybarapp.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbuddy.fridaybarapp.CustomText
import com.barbuddy.fridaybarapp.firestore.service.FireStore
import com.barbuddy.fridaybarapp.firestore.service.Bar

@Composable
fun GetBars(service: FireStore) { //nav: NavController
    val bars = remember { mutableStateOf(emptyList<Bar>()) }
    LaunchedEffect(Unit) {
        val list = service.getFarvoritesbars()
        if (list != null) {
            bars.value = list
        }
    }
    Card(
        Modifier
        .width(390.dp)
        .offset(x = 10.dp),
        shape = RoundedCornerShape(20),
        backgroundColor = Color(0xFF000000),
        border = BorderStroke(2.dp, color = Color(0xFFA36D00))
    ) {
        Column(Modifier.padding(25.dp)) {
            CustomText(data = "Names: ", fontSize = 25, Color(0xFFE70000), )
            bars.value.map {
                Row(Modifier.padding(5.dp)) {
                    CustomText(data = it.name, fontSize = 20, Color(0xFFE70000), )
                }
            }
        }
    }
}
