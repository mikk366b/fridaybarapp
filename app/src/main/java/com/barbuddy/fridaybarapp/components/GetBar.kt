package com.barbuddy.fridaybarapp.components


import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barbuddy.fridaybarapp.CustomText
import com.barbuddy.fridaybarapp.LinkButton
import com.barbuddy.fridaybarapp.MapScreenDetails
import com.barbuddy.fridaybarapp.R
import com.barbuddy.fridaybarapp.firestore.service.FireStore
import com.barbuddy.fridaybarapp.firestore.service.Bar
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun GetBars(service: FireStore) { //nav: NavController
    val bars = remember { mutableStateOf(emptyList<Bar>()) }
    var viewDetails by remember { mutableStateOf(false) }
    var lastClicked by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val favoriteBars = remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val list = service.getFarvoritesbars()
        if (list != null) {
            bars.value = list
        }
    }

    if (!viewDetails) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.krone),
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                contentDescription = null,
                alignment = Alignment.Center
            )
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
                    data = "List of favorite bars",
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

            //Text(text = response)
            //Log.v("JSON response før", response)
            //val parts = response.lines()
            LaunchedEffect(Unit){
                if (service.loggedIn){
                    favoriteBars.value = service.getFarvoritesbars()!!.map { it.name }
                }
            }
            if (bars != null) {
                for (i in 0 until bars.value.size) {
                    val bar = bars.value[i]
                    val barName = bar.name
                    val isFavorite = barName in favoriteBars.value
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        Modifier
                            .height(80.dp)
                            .width(390.dp)
                            .offset(x = 10.dp)
                            .clickable {
                                viewDetails = !viewDetails
                                lastClicked = bar.name
                            },
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
                                    data = bar.name,
                                    fontSize = 25,
                                    Color(0xFFE70000),
                                    Modifier.offset(x = 5.dp)
                                )
                                CustomText(
                                    data = bar.name.substringBefore(", Denmark"),
                                    fontSize = 20,
                                    Color(0xFFE70000),
                                    Modifier.offset(x = 5.dp)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)) {
                                Icon(
                                    if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Favorite",
                                    modifier = Modifier
                                        .clickable {
                                            if (service.loggedIn) {
                                                coroutineScope.launch {
                                                    if (isFavorite) {
                                                        service.deleteFarvoritesbars(barName)
                                                    } else {
                                                        service.createFarvoritesbars(barName)
                                                    }
                                                    if (service.loggedIn) {
                                                        favoriteBars.value =
                                                            service
                                                                .getFarvoritesbars()!!
                                                                .map { it.name }
                                                    }
                                                }
                                            } else {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "You need to be logged in to use this feature",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        }
                                        .offset(x = -(10).dp, y = 0.dp)
                                        .height(45.dp)
                                        .width(45.dp),
                                    tint = Color(0xFFF0B432)
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        //Individuel bar side
        var currentBar: Bar? = null
        if (bars != null) {
            for (i in 0 until bars.value.size) {
                val bar = bars.value[i]
                if (bar.name == lastClicked) {
                    currentBar = bar
                }
            }

            if (currentBar != null) {
                Row(Modifier.height(50.dp)) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        "contentDescription",
                        Modifier
                            .clickable { viewDetails = !viewDetails }
                            .height(35.dp)
                            .width(35.dp)
                            .offset(x = 10.dp, y = 8.dp),
                        tint = Color(0xFFFFF0D2))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = -(15).dp)) {
                        LinkButton(link = currentBar.name)
                    }
                }
                Card(
                    Modifier
                        .height(80.dp)
                        .width(390.dp)
                        .offset(x = 10.dp),
                    shape = RoundedCornerShape(24),
                    border = BorderStroke(2.dp, color = Color(0xFF000000)),
                    backgroundColor = Color(0xFFA57D2D)
                ) {
                    Card(
                        Modifier.padding(4.dp),
                        shape = RoundedCornerShape(22),
                        backgroundColor = Color(0xFF000000),
                        border = BorderStroke(0.dp, color = Color(0xFFA57D2D))
                    ) {

                        Column(Modifier.padding(5.dp)) {
                            CustomText(
                                data = currentBar.name,
                                fontSize = 25,
                                Color(0xFFE70000),
                                Modifier.offset(x = 5.dp)
                            )
                            CustomText(
                                data = currentBar.name.substringBefore(", Denmark"),
                                fontSize = 20,
                                Color(0xFFE70000),
                                Modifier.offset(x = 5.dp)
                            )

                        }
                    }
                }
                Card(
                    Modifier
                        .padding(5.dp)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(3),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    MapScreenDetails(DetailsName = currentBar.name)
                }

            } else {
                //Burde ikke nå hertil, men bare for at være sikker
                viewDetails = !viewDetails
            }

        }
    }
}
