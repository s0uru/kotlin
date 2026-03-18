package pl.wsei.pam.lab02

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab03.Lab03Activity

class Lab02Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab02)
    }

    // Ta metoda naprawia błąd "Could not find method onButtonClick"
    fun onButtonClick(v: View) {
        val intent = Intent(this, Lab03Activity::class.java)

        val size = when (v.id) {
            R.id.main_4_4_board -> intArrayOf(4, 4)
            R.id.main_4_3_board -> intArrayOf(4, 3)
            R.id.main_6_6_board -> intArrayOf(6, 6)
            // Jeśli masz osobny przycisk dla 3x2, dodaj go tutaj:
            // R.id.main_3_2_board -> intArrayOf(3, 2)
            else -> intArrayOf(3, 2) // Domyślnie ustawiamy 3x2 jeśli nic nie pasuje
        }

        intent.putExtra("size", size)
        startActivity(intent)
    }
}