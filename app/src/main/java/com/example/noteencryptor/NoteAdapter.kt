package com.example.noteencryptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button


class NoteAdapter(
    private val notes: MutableList<Note>,
    private val onDeleteClick: (Note) -> Unit,
    private val onEditClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.noteTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.noteDescription)
        val timestampTextView: TextView = itemView.findViewById(R.id.noteTimestamp)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.descriptionTextView.text = note.description
        holder.timestampTextView.text = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(note.timestamp))

        holder.deleteButton.setOnClickListener {
            onDeleteClick(note)
        }

        holder.itemView.setOnClickListener {
            onEditClick(note)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun removeItem(note: Note) {
        val position = notes.indexOf(note)
        if (position != -1) {
            notes.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateNote(note: Note) {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note
            notifyItemChanged(index)
        }
    }

    fun getNoteById(id: Long): Note? {
        return notes.find { it.id == id }
    }
}