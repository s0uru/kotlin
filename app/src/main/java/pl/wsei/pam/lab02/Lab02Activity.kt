package pl.wsei.pam.lab02

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.lab01.R

class Lab02Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab02)

        // Sprawdź czy id to na pewno 'favorites_grid', jeśli tak nazwałeś głównego Grida
        val mainView = findViewById<View>(R.id.favorites_grid)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Funkcja obsługująca kliknięcie przycisków
    fun onButtonClick(v: View) {
        val tag: String? = v.tag as String?
        val tokens: List<String>? = tag?.split(" ")

        // Sprawdzamy czy tag nie jest pusty, aby uniknąć błędów
        if (tokens != null && tokens.size >= 2) {
            val rows = tokens[0].toInt()
            val columns = tokens[1].toInt()
            Toast.makeText(this, "rows: ${rows}, columns: ${columns}", Toast.LENGTH_SHORT).show()
        }
    }
}