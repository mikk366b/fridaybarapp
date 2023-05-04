package com.example.fridaybarapp.firestore.service


import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FireStore(private val api: FirebaseFirestore, private val auth: FirebaseAuth) {
    companion object {
        const val TAG = "FIRE_STORE_SERVICE"
    }

    //Not tested
    suspend fun getFarvoritesbars(): List<Bar>? {
        Log.v("getFarvoritesbars","karl")
        return suspendCoroutine { continuation ->
            api.collection("users").document(usere.id)
                .get()
                .addOnSuccessListener {
                    val test = it.get("favorites") as List<String>
                    val favlists = test.map { d -> Bar(d) }
                    continuation.resume(favlists)
                }.addOnFailureListener {
                    Log.v(TAG, "We failed $it")
                    throw it
                }
        }
    }

    suspend fun createFarvoritesbars(name: String) {
        val fridaybarToFav = hashMapOf(
            //"Name" to name
            "favorites" to FieldValue.arrayUnion(name)
        )
        suspendCoroutine { continuation ->
            api.collection("users").document(usere.id)
                .update(fridaybarToFav as Map<String, Any>)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference}")
                    continuation.resume(documentReference)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    throw e
                }
        }
    }
    internal var usere = User("null","null")
    suspend fun signup(email: String, password: String) {
        suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser ?: throw Exception("Something wrong")
                        val signedInUser = user.email?.let { User(user.providerId, it) }
                            ?: throw Exception("createUserWithEmail:$email failure")
                        val field = Fields(
                            listOf("heste")
                        )

                        continuation.resume(signedInUser)
                        task.result?.user?.uid?.let { api.collection("users").document(it).set(field)
                            usere.id = it
                            usere.email = email}
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        throw throw Exception("createUserWithEmail: $email failure", task.exception)
                    }
                }
        }
    }
    suspend fun login(email: String, password: String) {
        suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "LoginUserWithEmail:success")
                        val user = auth.currentUser ?: throw Exception("Something wrong")
                        val signedInUser = user.email?.let { User(user.providerId, it) }
                            ?: throw Exception("LoginUserWithEmail:$email failure")
                        continuation.resume(signedInUser)
                        task.result?.user?.uid?.let {
                            usere.id = it
                            usere.email = email}
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "LoginUserWithEmail:failure", task.exception)
                        throw throw Exception("LoginUserWithEmail: $email failure", task.exception)
                    }
                }
        }
    }
}