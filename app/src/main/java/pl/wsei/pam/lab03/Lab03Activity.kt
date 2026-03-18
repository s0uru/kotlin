package pl.wsei.pam.lab03

import android.os.Bundle
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pl.wsei.pam.GameStates
import pl.wsei.pam.MemoryBoardView
import pl.wsei.pam.lab01.R
// DWA BARDZO WAŻNE IMPORTY DLA TIMERA:
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {

    lateinit var mBoard: GridLayout
    lateinit var mBoardModel: MemoryBoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab03)

        mBoard = findViewById(R.id.game_board_grid)

        ViewCompat.setOnApplyWindowInsetsListener(mBoard) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)
        val rows = size[0]
        val columns = size[1]

        mBoard.columnCount = columns
        mBoard.rowCount = rows

        // Tworzymy model planszy
        mBoardModel = MemoryBoardView(mBoard, columns, rows)

        // NOWY KOD (KROK 3) - ODCZYT STANU PO OBROCIE EKRANU:
        if (savedInstanceState != null) {
            val savedState = savedInstanceState.getIntArray("state")
            if (savedState != null) {
                // Jeśli jest zapis, przywracamy ułożenie kart!
                mBoardModel.setState(savedState)
            }
        }

        // OBSŁUGA ZDARZEŃ GRY:
        mBoardModel.setOnGameChangeListener { e ->
            when (e.state) {
                GameStates.Matching -> {
                    // Odkryto pierwszą kartę z pary -> odsłoń ją
                    e.tiles.forEach { it.revealed = true }
                }
                GameStates.Match -> {
                    // Odkryto drugą kartę i PASUJE -> odsłoń ją (obie zostają odkryte na zawsze)
                    e.tiles.forEach { it.revealed = true }
                }
                GameStates.NoMatch -> {
                    // Karty NIE PASUJĄ

                    // 1. Odsłaniamy obie karty od razu, żeby gracz zobaczył co kliknął
                    e.tiles.forEach { it.revealed = true }

                    // 2. Czekamy 2 sekundy (2000 ms) w tle (nie blokując ekranu)
                    Timer().schedule(2000) {

                        // 3. Po 2 sekundach wracamy do głównego wątku ekranu (UI)
                        runOnUiThread {
                            // 4. Zakrywamy obie karty z powrotem
                            e.tiles.forEach { it.revealed = false }
                        }
                    }
                }
                GameStates.Finished -> {
                    // Gra skończona (ostatnia para odkryta)
                    // Dla pewności upewniamy się, że ostatnia para również zostanie odsłonięta
                    e.tiles.forEach { it.revealed = true }

                    // Wyświetlamy dymek zwycięstwa
                    Toast.makeText(this, "Game finished!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // NOWY KOD (KROK 3) - ZAPIS STANU PRZED OBROTEM EKRANU:
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Pobieramy "zdjęcie" planszy i zapisujemy pod kluczem "state"
        val currentState = mBoardModel.getState()
        outState.putIntArray("state", currentState)
    }
}