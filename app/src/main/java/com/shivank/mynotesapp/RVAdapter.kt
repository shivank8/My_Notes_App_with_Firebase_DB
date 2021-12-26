package com.shivank.mynotesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVAdapter(private val notesList : ArrayList<Notes>, private val noteClickDeleteInterface: NoteClickDeleteInterface,
                private val editClickInterface: EditClickInterface) : RecyclerView.Adapter<RVAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_single_item,
            parent,false)
        return MyViewHolder(itemView)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = notesList[position]

        holder.title.text = currentitem.title
        holder.desc.text = currentitem.description
        holder.notesId.text = currentitem.noteId

        holder.deleteIV.setOnClickListener {
            noteClickDeleteInterface.onDeleteClick(currentitem)
            notesList.clear()
            notifyDataSetChanged()
        }
        holder.editIV.setOnClickListener {
            editClickInterface.onEditClick(currentitem)
        }

    }

    override fun getItemCount(): Int {

        return notesList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val title : TextView = itemView.findViewById(R.id.txtNoteTitle)
        val desc : TextView = itemView.findViewById(R.id.txtNoteDesc)
        val notesId : TextView = itemView.findViewById(R.id.txtNotesId)
        val deleteIV: ImageView = itemView.findViewById(R.id.imgDelete)
        val editIV: ImageView = itemView.findViewById(R.id.imgEdit)

    }


}

interface NoteClickDeleteInterface {
    fun onDeleteClick(note: Notes)
}

interface EditClickInterface {
    fun onEditClick(note: Notes)
}