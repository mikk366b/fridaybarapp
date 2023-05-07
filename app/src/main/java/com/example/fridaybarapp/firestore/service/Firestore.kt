package com.example.fridaybarapp.firestore.service


import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
                    val test = it.get("favorites") as? List<String>
                    val favlists = test?.map { d -> Bar(d) }
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

    suspend fun deleteFarvoritesbars(name: String) {
        val fridaybarToFav = hashMapOf(
            //"Name" to name
            "favorites" to FieldValue.arrayRemove(name)
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

    suspend fun getACrawl(crawlId:String): List<Crawl>? {
        Log.v("getFarvoritesbars","karl")
        return suspendCoroutine { continuation ->
            api.collection("users").document(usere.id)
                .get()
                .addOnSuccessListener {
                    val test = it.get("crawls") as? Map<*, *>
                    Log.v("getCrawl",test.toString())
                    val array1 = test?.get(crawlId) as? List<String>
                    Log.v("getCrawl",array1.toString())
                    val crawllists = array1?.map { d -> Crawl(d) }
                    continuation.resume(crawllists)
                }.addOnFailureListener {
                    Log.v(TAG, "We failed $it")
                    throw it
                }
        }
    }
    suspend fun getAllCrawl(): List<List<Crawl>>? {
        Log.v("getFarvoritesbars","karl")
        return suspendCoroutine { continuation ->
            api.collection("users").document(usere.id)
                .get()
                .addOnSuccessListener {
                    val test = it.get("crawls") as? Map<*, Array<String>>
                    Log.v("getAllCrawl",test.toString())
                    //val crawllists = test?.toList()?.map { d -> Crawl(d) }
                    val list = mutableListOf<List<Crawl>>()
                    for (entry in test?.entries!!) {
                        val array = entry.value
                        list.add(array.toList().map { d -> Crawl(d) })
                    }
                    Log.v("getAllCrawlpls",list.toList().toString())
                    continuation.resume(list.toList())
                }.addOnFailureListener {
                    Log.v(TAG, "We failed $it")
                    throw it
                }
        }
    }
    suspend fun createCrawl(crawlId:String, name: String) {
        val data = hashMapOf(
            "crawls" to hashMapOf(
                crawlId to FieldValue.arrayUnion(name)
            )
        )
        val fridaybarToFav = hashMapOf(
            //"Name" to name
            crawlId to FieldValue.arrayUnion(name)
        )
        suspendCoroutine { continuation ->
            api.collection("users").document(usere.id)
                .update("crawls.$crawlId",FieldValue.arrayUnion(name))
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

    suspend fun deleteCrawl(crawlId:String, name: String) {
        suspendCoroutine { continuation ->
            api.collection("users").document(usere.id)
                .update("crawls.$crawlId",FieldValue.arrayRemove(name))
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
    suspend fun signup(email: String, password: String): String {
         return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser ?: throw Exception("Something wrong")
                        val signedInUser = user.email?.let { User(user.providerId, it) }
                            ?: throw Exception("createUserWithEmail:$email failure")
                        val field = Fields(
                            //listOf("heste")
                            email
                        )

                        continuation.resume("Sign up successful")
                        task.result?.user?.uid?.let { api.collection("users").document(it).set(field)
                            usere.id = it
                            usere.email = email}
                    } else {
                        when (task.exception) {
                            is FirebaseAuthUserCollisionException -> {
                            // User already exists, handle accordingly
                            continuation.resume("User already exists")}
                            is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid email/password, handle accordingly
                            continuation.resume("Invalid email/password") }
                            else -> {
                            // Other exception, handle accordingly
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            throw throw Exception("createUserWithEmail: $email failure", task.exception) }
                    }
                }
            }
        }
    }
    suspend fun login(email: String, password: String): String {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "LoginUserWithEmail:success")
                        val user = auth.currentUser ?: throw Exception("Something wrong")
                        val signedInUser = user.email?.let { User(user.providerId, it) }
                            ?: throw Exception("LoginUserWithEmail:$email failure")
                        continuation.resume("Login successful") //signedInUser
                        task.result?.user?.uid?.let {
                            usere.id = it
                            usere.email = email
                        }
                    } else {
                        when (task.exception) {
                            is FirebaseAuthInvalidUserException -> {
                                // User does not exist, handle accordingly
                                continuation.resume("User does not exist")
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                // Invalid email/password, handle accordingly
                                continuation.resume("Invalid email/password")
                            }
                            else -> {
                                // Other exception, handle accordingly
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "LoginUserWithEmail:failure", task.exception)
                                throw throw Exception(
                                    "LoginUserWithEmail: $email failure",
                                    task.exception
                                )
                            }
                        }
                    }
                }
        }
    }

}