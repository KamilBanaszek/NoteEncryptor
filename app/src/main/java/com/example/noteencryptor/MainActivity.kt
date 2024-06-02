package com.example.noteencryptor

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

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
            onEditClick = { note -> editNoteWithPasswordCheck(note) }
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

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showNoteTypeDialog()
        }
    }

    private fun showNoteTypeDialog() {
        val options = arrayOf("Notatka tekstowa", "Notatka głosowa")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Wybierz typ notatki")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(this, AddNoteActivity::class.java)
                        startActivityForResult(intent, 1)
                    }
                    1 -> {
                        val intent = Intent(this, VoiceNoteActivity::class.java)
                        startActivityForResult(intent, 3)
                    }
                }
            }
        builder.create().show()
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

    private fun editNoteWithPasswordCheck(note: Note) {
        if (note.passwordHash != null) {
            val passwordEditText = TextInputEditText(this)
            passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

            AlertDialog.Builder(this)
                .setTitle("Wprowadź hasło")
                .setView(passwordEditText)
                .setPositiveButton("OK") { dialog, _ ->
                    val enteredPassword = passwordEditText.text.toString()
                    if (hashPassword(enteredPassword) == note.passwordHash) {
                        editNote(note, enteredPassword)
                    } else {
                        Toast.makeText(this, "Błędne hasło", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Anuluj") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            editNote(note, null)
        }
    }

    private fun hashPassword(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun editNote(note: Note, password: String?) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("noteId", note.id)
        intent.putExtra("title", note.title)
        intent.putExtra("description", note.description)
        intent.putExtra("passwordHash", note.passwordHash)
        intent.putExtra("password", password)
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            data?.let {
                val title = it.getStringExtra("title") ?: ""
                val description = it.getStringExtra("description") ?: ""
                val timestamp = it.getLongExtra("timestamp", System.currentTimeMillis())
                val passwordHash = it.getStringExtra("passwordHash")
                val note = Note(
                    id = System.currentTimeMillis(),
                    title = title,
                    description = description,
                    timestamp = timestamp,
                    passwordHash = passwordHash
                )

                when (requestCode) {
                    1 -> {
                        addNoteToTop(note)
                    }
                    2 -> {
                        val noteId = it.getLongExtra("noteId", -1)
                        val existingNote = notes.find { it.id == noteId }
                        existingNote?.let { updatedNote ->
                            updatedNote.title = title
                            updatedNote.description = description
                            updatedNote.timestamp = timestamp
                            updatedNote.passwordHash = passwordHash
                            PreferenceHelper.saveNotes(this, notes)
                            filterNotes(searchEditText.text.toString())
                        }
                    }
                    3 -> {
                        addNoteToTop(note)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun addNoteToTop(note: Note) {
        notes.add(0, note)
        filteredNotes.add(0, note)
        adapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
        PreferenceHelper.saveNotes(this, notes)
        filterNotes(searchEditText.text.toString())
    }
}