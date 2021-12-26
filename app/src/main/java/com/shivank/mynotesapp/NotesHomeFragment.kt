package com.shivank.mynotesapp

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class NotesHomeFragment : Fragment(),EditClickInterface, NoteClickDeleteInterface {

    private lateinit var dbref : DatabaseReference
    private lateinit var notesArrayList : ArrayList<Notes>
    private lateinit var notesRV: RecyclerView
    private lateinit var addNewNote: FloatingActionButton
    private lateinit var noteRVAdapter: RVAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_notes_home, container, false)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val fragmentManager = activity?.supportFragmentManager
        val navHostFragment = fragmentManager?.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        if(currentUser==null){
            navController.navigate(R.id.action_notesHomeFragment_to_googleLoginFragment)
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        notesRV = view.findViewById(R.id.notesRV)
        addNewNote=view.findViewById(R.id.idAddNewNote)

        notesRV.layoutManager = LinearLayoutManager(context)
        notesArrayList = arrayListOf<Notes>()
        noteRVAdapter = RVAdapter(notesArrayList , this, this)

        try {
            getUserData()
        }catch (e: Exception){}

        addNewNote.setOnClickListener {
            navController.navigate(R.id.nav_to_addUpdateNoteFragment)
        }


        return view
    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference(auth.currentUser!!.uid)

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){


                        val notes = userSnapshot.getValue(Notes::class.java)
                        notesArrayList.add(notes!!)

                    }
                    notesRV.adapter = noteRVAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Unable to load data!", Toast.LENGTH_SHORT).show()

            }
        })

    }

    override fun onDeleteClick(note: Notes) {
        dbref = FirebaseDatabase.getInstance().getReference(auth.currentUser!!.uid)
        dbref.child(note.noteId!!).removeValue().addOnSuccessListener {
            Toast.makeText(context, "${note.title} Deleted.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, "Unable to delete!.", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onEditClick(note: Notes) {
        val fragmentManager = activity?.supportFragmentManager
        val navHostFragment = fragmentManager?.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val bundle = Bundle()
        bundle.putString("noteid",note.noteId)
        bundle.putString("title",note.title)
        bundle.putString("desc",note.description)
        navController.navigate(R.id.nav_to_addUpdateNoteFragment,bundle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_signout,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.log_out){
            signOut()

        }
        return super.onOptionsItemSelected(item)
    }
    private fun signOut() {
        Firebase.auth.signOut()
        val fragmentManager = activity?.supportFragmentManager
        val navHostFragment = fragmentManager?.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.action_notesHomeFragment_to_googleLoginFragment)
        Toast.makeText(context, "Log out success!", Toast.LENGTH_SHORT).show()


    }
}