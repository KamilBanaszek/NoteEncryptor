package com.example.noteencryptor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import java.util.Date

class AddNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val titleEditText: TextInputEditText = findViewById(R.id.titleEditText)
        val descriptionEditText: TextInputEditText = findViewById(R.id.descriptionEditText)
        val passwordEditText: TextInputEditText = findViewById(R.id.passwordEditText) // Nowe pole na hasło
        val saveButton: Button = findViewById(R.id.saveButton)

        val noteId = intent.getLongExtra("noteId", -1)
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        titleEditText.setText(title)
        descriptionEditText.setText(description)

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val password = passwordEditText.text.toString()
            val timestamp = Date().time
            val passwordHash = if (password.isNotEmpty()) hashPassword(password) else null

            val resultIntent = Intent().apply {
                putExtra("noteId", noteId)
                putExtra("title", title)
                putExtra("description", description)
                putExtra("timestamp", timestamp)
                putExtra("passwordHash", passwordHash)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun hashPassword(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}