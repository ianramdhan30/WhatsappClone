package com.example.whatsappclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import idn.whatsappproject.whatsappclone.R

class StatusUpdateFragment : Fragment() {


    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var imageUrl = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}