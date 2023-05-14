package com.barbuddy.fridaybarapp
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.barbuddy.fridaybarapp.CustomText
import com.barbuddy.fridaybarapp.components.getCrawls
import org.json.JSONObject
import com.barbuddy.fridaybarapp.makeNetworkRequestJSON
import com.barbuddy.fridaybarapp.firestore.service.FireStore
import com.barbuddy.fridaybarapp.firestore.service.Crawl
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment

@Composable
fun BarCrawlScreen(response: String, service: FireStore) {
    val name = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val bars = remember { mutableStateListOf<JSONObject>() }
    var listOfBarCrawls = remember {mutableListOf<String>()}
    val crawls = remember { mutableStateOf(emptyList<List<Crawl>>()) }
    var getClicked = remember{ mutableStateOf(false)}
    var createClicked = remember{ mutableStateOf(false)}

    LaunchedEffect(Unit) {
        val JSONbars = makeNetworkRequestJSON()
        if (JSONbars != null) {
            for (i in 0 until JSONbars.length()) {
                val bar = JSONbars.getJSONObject(i)
                bars.add(bar)
            }
        }
        val list = service.getAllCrawl()
        //Log.v("Tests barcrawls get",list.toString())
        if (list != null) {
            crawls.value = list
        }
    }

    val randomizedBars = remember { mutableStateListOf<JSONObject>() }
    Button(
        onClick = {
            bars.shuffle()
            randomizedBars.clear()
            randomizedBars.addAll(bars.subList(0, 3))
            for (bar in randomizedBars) {
                listOfBarCrawls.add(bar.getString("name"))
            }
            getClicked.value = false
            createClicked.value = true
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD2DF05))
    ) {
        Text(text = "Create BarCrawl")
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                scope.launch{ crawls.value = service.getAllCrawl() ?: crawls.value } // ? : true : false
                createClicked.value = false
                getClicked.value = true },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD2DF05))
        ) {
            Text(text = "Get my Bar Crawls")
        }

        Button(
            onClick = { if (!service.loggedIn) {
                val message = "Please login to continue"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            else{
                if( name.value != "") {
                    // Handle button click when user is logged in
                    scope.launch {
                        //service.createCrawl(name.value,listOfBarCrawls)
                        listOfBarCrawls.map { service.createCrawl(name.value,it) }
                    }
                } else {
                    Toast.makeText(context, "Enter a name for this crawl", Toast.LENGTH_SHORT).show()
                }
            }
                      },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD2DF05))
        ) {
            Text(text = "Save Bar Crawl")
        }
        //Text("Name of BarCrawl list:")

    }
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically){
        TextField(value = name.value, onValueChange = { newText -> name.value = newText }, placeholder={ Text("Enter bar crawl name") }
        ,modifier = Modifier.fillMaxWidth(),colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black),shape = RoundedCornerShape(16.dp))
    }
    // printing bars in cards
    if (randomizedBars.isNotEmpty() && createClicked.value) {
        CustomText(
            data = "Random Bar Crawl list",
            fontSize = 25,
            Color(0xFF000000),
            Modifier.offset(x = 10.dp, y = -(2).dp),
        )
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
    if(crawls != emptyList<List<Crawl>>() && getClicked.value){
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            CustomText(
                data = "My Bar Crawls",
                fontSize = 25,
                Color(0xFF000000),
                Modifier.offset(x = 10.dp, y = -(2).dp),
            )
            crawls.value.map {
                Card(
                    Modifier
                        .width(390.dp)
                        .offset(x = 10.dp),
                    shape = RoundedCornerShape(20),
                    backgroundColor = Color(0xFF000000),
                    border = BorderStroke(2.dp, color = Color(0xFFA36D00))
                ) {
                    Column(Modifier.padding(25.dp)) {
                        //Log.v("listst",it.toString())
                        CustomText(data = it.last().name, fontSize = 25, Color(0xFFE70000), )
                        val temp = it.dropLast(1)
                        temp.map{
                            Row() {
                                //CustomText(data = it.name, fontSize = 25, Color(0xFFE70000), )
                                Column(Modifier.padding(5.dp)) {
                                    CustomText(data = it.name, fontSize = 20, Color(0xFFE70000), )
                            }
                            }
                        }
                    }
                }
            }
        }
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



