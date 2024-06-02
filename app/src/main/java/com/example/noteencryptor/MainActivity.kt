package com.example.noteencryptor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private var notes: MutableList<Note> = mutableListOf()
    private var filteredNotes: MutableList<Note> = mutableListOf()
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notes = PreferenceHelper.getNotes(this).toMutableList()
        filteredNotes.addAll(notes)
        adapter = NoteAdapter(filteredNotes,
            onDeleteClick = { note -> deleteNote(note) },
            onEditClick = { note -> editNote(note) }
        )
        recyclerView.adapter = adapter

        searchEditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNotes(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val fab: Button = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    private fun filterNotes(query: String?) {
        val queryLower = query?.lowercase() ?: ""
        filteredNotes.clear()
        filteredNotes.addAll(notes.filter { it.title.lowercase().contains(queryLower) })
        adapter.notifyDataSetChanged()
    }

    private fun deleteNote(note: Note) {
        notes.remove(note)
        PreferenceHelper.saveNotes(this, notes)
        filterNotes(searchEditText.text.toString())
    }

    private fun editNote(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("noteId", note.id)
        intent.putExtra("title", note.title) // Przekazanie tytułu notatki
        intent.putExtra("description", note.description) // Przekazanie opisu notatki
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.let {
                val title = it.getStringExtra("title") ?: ""
                val description = it.getStringExtra("description") ?: ""
                val timestamp = it.getLongExtra("timestamp", System.currentTimeMillis())
                val note = Note(
                    id = notes.size.toLong() + 1,
                    title = title,
                    description = description,
                    timestamp = timestamp
                )
                notes.add(note)
                PreferenceHelper.saveNotes(this, notes)
                filterNotes(searchEditText.text.toString())
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            data?.let {
                val noteId = it.getLongExtra("noteId", -1)
                val title = it.getStringExtra("title") ?: ""
                val description = it.getStringExtra("description") ?: ""
                val timestamp = it.getLongExtra("timestamp", System.currentTimeMillis())
                val note = adapter.getNoteById(noteId)
                note?.let {
                    it.title = title
                    it.description = description
                    it.timestamp = timestamp
                    adapter.updateNote(it)
                    PreferenceHelper.saveNotes(this, notes)
                    filterNotes(searchEditText.text.toString())
                }
            }
        }
    }
}