package com.shivank.mynotesapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class AddUpdateNoteFragment : Fragment() {
    private lateinit var noteTitleEdt: EditText
    private lateinit var noteEdt: EditText
    private lateinit var saveBtn: Button
    private lateinit var database : DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_add_update_note, container, false)

        noteTitleEdt = view.findViewById(R.id.etNoteName)
        noteEdt = view.findViewById(R.id.etNoteDesc)
        saveBtn = view.findViewById(R.id.btnSaveNote)
        auth = Firebase.auth
        val noteid=arguments?.getString("noteid")
        val notestitle=arguments?.getString("title")
        val notesdesc=arguments?.getString("desc")

        if(noteid!=null){

            noteTitleEdt.setText(notestitle)
            noteEdt.setText(notesdesc)
            saveBtn.text=getString(R.string.update_note)
            saveBtn.setOnClickListener {
                val title = noteTitleEdt.text.toString()
                val desc = noteEdt.text.toString()

                if (title.isNotEmpty() && desc.isNotEmpty()) {
                val notes = mapOf(
                    "noteId" to noteid,
                    "title" to title,
                    "description" to desc
                )
                    database = FirebaseDatabase.getInstance().getReference(auth.currentUser!!.uid)

                    database.child(noteid).updateChildren(notes).addOnSuccessListener {

                    Toast.makeText(context, "Successfully Updated", Toast.LENGTH_SHORT).show()
                    val fragmentManager = activity?.supportFragmentManager
                    val navHostFragment =
                        fragmentManager?.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.nav_to_notesHomeFragment2)

                }.addOnFailureListener {

                    Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show()

                }
            }else{
                when {
                    title.isEmpty() -> {
                        Toast.makeText(context, "Please enter note title!", Toast.LENGTH_LONG).show()
                    }
                    desc.isEmpty() -> {
                        Toast.makeText(context, "Please enter note description!", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }}
        else{

        saveBtn.setOnClickListener {
            saveData()
        }}

        return view
    }

    private fun saveData() {
        val title = noteTitleEdt.text.toString()
        val desc = noteEdt.text.toString()
        if (title.isNotEmpty() && desc.isNotEmpty()) {
        database = FirebaseDatabase.getInstance().getReference(auth.currentUser!!.uid)
        val noteId=database.push().key
        val notes = Notes(noteId,title,desc)
        database.child(noteId!!).setValue(notes).addOnSuccessListener {

            Toast.makeText(context,"Note Saved",Toast.LENGTH_SHORT).show()
            val fragmentManager = activity?.supportFragmentManager
            val navHostFragment = fragmentManager?.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.nav_to_notesHomeFragment2)


        }.addOnFailureListener{

            Toast.makeText(context,"Unable to save. Please try again!",Toast.LENGTH_SHORT).show()


        }}
        else{
            when {
                title.isEmpty() -> {
                    Toast.makeText(context, "Please enter note title!", Toast.LENGTH_LONG).show()
                }
                desc.isEmpty() -> {
                    Toast.makeText(context, "Please enter note description!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}