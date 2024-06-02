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
    private lateinit var notes: MutableList<Note>
    private lateinit var filteredNotes: MutableList<Note>
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notes = PreferenceHelper.getNotes(this).toMutableList()
        filteredNotes = notes.toMutableList()
        adapter = NoteAdapter(filteredNotes) { note ->
            deleteNote(note)
        }
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
        }
    }
}