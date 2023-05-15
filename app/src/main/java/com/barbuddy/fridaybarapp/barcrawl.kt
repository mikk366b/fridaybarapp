package com.barbuddy.fridaybarapp
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight

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
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF0B432))
    ) {
        Text(text = "Create bar crawl", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                scope.launch{ crawls.value = service.getAllCrawl() ?: crawls.value } // ? : true : false
                createClicked.value = false
                getClicked.value = true },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF0B432))
        ) {
            Text(text = "Get my bar crawls", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
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
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF0B432))
        ) {
            Text(text = "Save bar crawl", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        //Text("Name of BarCrawl list:")

    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically){
        TextField(value = name.value, onValueChange = { newText -> name.value = newText },
            placeholder={ Text("Enter bar crawl name", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0x33FFFFFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black),
            shape = RoundedCornerShape(16.dp),
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
    // printing bars in cards
    if (randomizedBars.isNotEmpty() && createClicked.value) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))
        ) {
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFF0B432))
        ) {
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))
        ) {
        }
        Row(
            Modifier
                .height(35.dp)
                .fillMaxWidth()
                .background(Color(0xFFB90000))
        ) {
            CustomText(
                data = "Random bar crawl",
                fontSize = 25,
                Color(0xFF000000),
                Modifier.offset(x = 10.dp, y = -(2).dp),
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))
        ) {
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFF0B432))
        ) {
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))
        ) {
        }
        for (bar in randomizedBars) {
            val name = bar.getString("name")
            val address = bar.getString("address").substringBefore(", Denmark")
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                Modifier
                    .height(80.dp)
                    .width(390.dp)
                    .offset(x = 10.dp),
                shape = RoundedCornerShape(30),
                border = BorderStroke(2.dp, color = Color(0xFF000000)),
                backgroundColor = Color(0xFFA57D2D)
            ) {
                Card(
                    Modifier.padding(4.dp),
                    shape = RoundedCornerShape(28),
                    backgroundColor = Color(0xFF000000),
                    border = BorderStroke(0.dp, color = Color(0xFFA57D2D))
                ) {
                    Column(Modifier.padding(3.dp)) {
                        CustomText(
                            data = name,
                            fontSize = 25,
                            Color(0xFFE70000),
                            Modifier.offset(x = 5.dp)
                        )
                        CustomText(
                            data = address,
                            fontSize = 20,
                            Color(0xFFE70000),
                            Modifier.offset(x = 5.dp)
                        )
                    }
                }
            }
        }
    }
    if(crawls != emptyList<List<Crawl>>() && getClicked.value){
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF000000))
            ) {
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color(0xFFF0B432))
            ) {
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF000000))
            ) {
            }
            Row(
                Modifier
                    .height(35.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFB90000))
            ) {
                CustomText(
                    data = "My bar crawls",
                    fontSize = 25,
                    Color(0xFF000000),
                    Modifier.offset(x = 10.dp, y = -(2).dp),
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF000000))
            ) {
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color(0xFFF0B432))
            ) {
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF000000))
            ) {
            }
            crawls.value.map {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    Modifier
                        .width(390.dp)
                        .offset(x = 10.dp),
                    shape = RoundedCornerShape(38.dp),
                    border = BorderStroke(3.dp, color = Color(0xFF000000)),
                    backgroundColor = Color(0xFFA57D2D)
                ) {
                    Card(
                        Modifier.padding(4.dp),
                        shape = RoundedCornerShape(35.dp),
                        backgroundColor = Color(0xFF000000),
                        border = BorderStroke(0.dp, color = Color(0xFFA57D2D))
                    ) {
                        Column(
                            Modifier
                                .padding(10.dp)
                                .offset(x = 10.dp)) {
                            //Log.v("listst",it.toString())
                            CustomText(data = it.last().name, fontSize = 30, Color(0xFFE70000))
                            Spacer(modifier = Modifier.height(5.dp))
                            val temp = it.dropLast(1)
                            temp.map{
                                Row() {
                                    //CustomText(data = it.name, fontSize = 25, Color(0xFFE70000), )
                                    Column(Modifier.padding(vertical = 1.dp)) {
                                        CustomText(data = it.name, fontSize = 20, Color(0xFFE70000), )
                                    }
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



