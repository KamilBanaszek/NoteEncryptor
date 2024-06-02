package com.example.noteencryptor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class VoiceNoteActivity : AppCompatActivity() {

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val REQUEST_SPEECH_INPUT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_note)

        val recordButton: Button = findViewById(R.id.recordButton)
        recordButton.setOnClickListener {
            if (checkPermission()) {
                startVoiceInput()
            } else {
                requestPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your note")
        try {
            startActivityForResult(intent, REQUEST_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Your device does not support speech input", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            data?.let {
                val result = it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val description = result?.get(0) ?: ""
                val intent = Intent().apply {
                    putExtra("title", "Voice Note")
                    putExtra("description", description)
                    putExtra("timestamp", System.currentTimeMillis())
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startVoiceInput()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}