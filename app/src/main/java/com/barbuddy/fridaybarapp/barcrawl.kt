package com.example.fridaybarapp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import com.barbuddy.fridaybarapp.CustomText
import org.json.JSONObject
import com.barbuddy.fridaybarapp.makeNetworkRequestJSON

@Composable
fun BarCrawlScreen() {
    val bars = remember { mutableStateListOf<JSONObject>() }

    LaunchedEffect(Unit) {
        val JSONbars = makeNetworkRequestJSON()
        if (JSONbars != null) {
            for (i in 0 until JSONbars.length()) {
                val bar = JSONbars.getJSONObject(i)
                bars.add(bar)
            }
        }
    }

    val randomizedBars = remember { mutableStateListOf<JSONObject>() }
    Button(
        onClick = {
            bars.shuffle()
            randomizedBars.clear()
            randomizedBars.addAll(bars.subList(0, 3))
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD2DF05))
    ) {
        Text(text = "Create BarCrawl")
    }

    if (randomizedBars.isNotEmpty()) {
        for (bar in randomizedBars) {
            val name = bar.getString("name")
            val address = bar.getString("address")

            Spacer(modifier = Modifier.height(12.dp))
            Card(
                Modifier
                    .height(80.dp)
                    .width(390.dp)
                    .offset(x = 10.dp),
                shape = RoundedCornerShape(20),
                backgroundColor = Color(0xFF000000),
                border = BorderStroke(2.dp, color = Color(0xFFA36D00))
            ) {
                Column(Modifier.padding(15.dp)) {
                    CustomText(data = name, fontSize = 25, Color(0xFFE70000), )
                    CustomText(data = address, fontSize = 15,Color(0xFFE70000),)
                }
            }
        }
    }
}



