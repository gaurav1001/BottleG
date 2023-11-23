package com.example.bottleg

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.bottleg.databinding.FragmentSubscriptionBinding
import com.google.android.play.integrity.internal.c
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date


class Subscription : Fragment() {

    companion object{
        const val TAG = "subscription"
    }
    val db = Firebase.firestore
    private lateinit var user: FirebaseUser
    private lateinit var binding:FragmentSubscriptionBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var plan:String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
       return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAuth = Firebase.auth
        user = mAuth.currentUser!!

        binding.plan1.setOnClickListener {
            Toast.makeText(requireContext(),"Available on future update",Toast.LENGTH_SHORT).show()
        }
        binding.plan2.setOnClickListener {
            Toast.makeText(requireContext(),"Available on future update",Toast.LENGTH_SHORT).show()
        }
        binding.plan3.setOnClickListener {
            plan = "3"
        }
        binding.subs.setOnClickListener {
            schedulerProductForAWeek()
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder
                .setMessage("You get the Subscription")
                .setTitle("Thank You")

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addToSubscription(p:List<String>){
        db.collection("subs").document(user.uid)
            .set(
               hashMapOf(
                   "uid" to user.uid,
                   "plan" to plan,
                   "period" to p
               )
            )
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun schedulerProductForAWeek(){
      val period = mutableListOf<String>()
      var today = LocalDate.now()
      val endOfWeek = today.plusDays(6)
      while (today.isBefore(endOfWeek)){
          Log.d(TAG,today.toString())
          period.add(today.toString())
          today = today.plus(2,ChronoUnit.DAYS)
      }
        addToSubscription(period)
    }
}





