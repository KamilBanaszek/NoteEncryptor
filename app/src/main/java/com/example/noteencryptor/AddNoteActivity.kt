package com.example.noteencryptor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Date

class AddNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val titleEditText: TextInputEditText = findViewById(R.id.titleEditText)
        val descriptionEditText: TextInputEditText = findViewById(R.id.descriptionEditText)
        val saveButton: MaterialButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val timestamp = Date().time

            val resultIntent = Intent().apply {
                putExtra("title", title)
                putExtra("description", description)
                putExtra("timestamp", timestamp)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}