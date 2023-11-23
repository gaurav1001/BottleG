package com.example.bottleg

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bottleg.databinding.FragmentHomeScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HomeScreen : Fragment(){
    private val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var binding:FragmentHomeScreenBinding
    private lateinit var mAuth:FirebaseAuth
    private val periodList = mutableListOf<String>()

    companion object {
        const val TAG = "HomeScreen"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeScreenBinding.inflate(inflater,container,false)

        return binding.root
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         mAuth = Firebase.auth
         user = mAuth.currentUser!!

        db.collection("product")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val url = document.getString("img")
                    Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.loading_icon)
                        .into(binding.productImg)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"data not found")
            }

        binding.subscribe.setOnClickListener {
          findNavController().navigate(R.id.subscription)
        }

        //record user
        db.collection("user").document(user.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    val userData = hashMapOf(
                        "name" to user.displayName.toString(),
                        "email" to user.email.toString(),
                        "photoUrl" to user.photoUrl.toString(),
                        "uId" to user.uid,
                        "verified" to user.isEmailVerified.toString()
                    )
                    addUserIntoDatabase(userData)
                } else {
                    Log.d(TAG, "User already exists!")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Error: ${it.message}")
            }

       db.collection("subs").document(user.uid).get()
            .addOnSuccessListener {result->
               if(result.exists()){
                  val period = result.get("period") as? List<String>
                  if(period != null){
                      val lastDate = period[period.size - 1]
                      binding.validity.text = resources.getString(R.string.validity,lastDate)
                      checkExpiry(lastDate)
                      for(documentSnap in period){
                        periodList.add(documentSnap)
                      }
                      val customAdapter = CustomAdapter(periodList)
                      binding.recyclerview.adapter = customAdapter
                  }

               }
            }
            .addOnFailureListener {e->
                e.printStackTrace()
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
  /*  private fun subsLogic(date:String) {
        val today = LocalDate.now()
        var valid = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
        val endOfWeek = valid.plusDays(6)
        while (valid.isBefore(endOfWeek)){

            valid =  valid.plus(2, ChronoUnit.DAYS)
        }
        binding.validity.text = String.format(resources.getString(R.string.validity),valid.toString())
        if(today.isBefore(valid)){
            binding.status.text = "Active"
        }else{
            binding.status.text = "Expired"
        }
    }


   */

    private fun addUserIntoDatabase(userData:HashMap<String,String>){
        db.collection("user").document(user.uid.toString())
            .set(userData)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

   @RequiresApi(Build.VERSION_CODES.O)
   private fun checkExpiry(date:String){
       val today = LocalDate.now()
       val expiry = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
       if(today.isBefore(expiry)){
           binding.status.text = "Active"
       }else{
           binding.status.text = "Expiry"
       }
   }
}
