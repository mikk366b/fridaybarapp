package com.barbuddy.fridaybarapp.components.authentication

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.barbuddy.fridaybarapp.firestore.service.FireStore
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignupLogin(service: FireStore) { //, nav: NavController
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var isPasswordVisible = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column() {

            TextField(colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0x33FFFFFF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black),
                shape = RoundedCornerShape(16.dp),
                value = email.value, onValueChange = { newText -> email.value = newText },
                placeholder = { Text("Email", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            )


            
            TextField(colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0x33FFFFFF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black),
                shape = RoundedCornerShape(16.dp),
                value = password.value,
                onValueChange = { newText -> password.value = newText },
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        Icon(
                            imageVector = if (isPasswordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (isPasswordVisible.value) "Hide password" else "Show password"
                        )
                    }
                },
                placeholder = { Text("Password", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
                textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold))
        }
        Row(modifier = Modifier.fillMaxWidth()){
        Button(onClick = {
            if(email.value.isNotEmpty() &&  password.value.isNotEmpty()){
            scope.launch {
                val result = service.login(email.value, password.value)
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                //nav.navigate("GetCrawl")
            }}
            else{Toast.makeText(context, "Error in email or password", Toast.LENGTH_SHORT).show()}
        }, shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .defaultMinSize(minWidth = 80.dp, minHeight = 30.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF0B432))) {
            Text("Log in", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        }
        Button(onClick = {
            if(email.value.isNotEmpty() &&  password.value.isNotEmpty()){
            scope.launch {
                val result = service.signup(email.value, password.value)
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
                //nav.navigate("GetCrawl")
            }}
            else{Toast.makeText(context, "Error in email or password", Toast.LENGTH_SHORT).show()}
        },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .defaultMinSize(minWidth = 80.dp, minHeight = 30.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF0B432))) {
            Text("Sign up", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        }
        }
    }
}