package pl.wsei.pam.lab03

import android.os.Bundle
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.wsei.pam.GameStates
import pl.wsei.pam.MemoryBoardView
import pl.wsei.pam.lab01.R
import java.util.Timer
import kotlin.concurrent.schedule

class Lab03Activity : AppCompatActivity() {
    lateinit var mBoard: GridLayout
    lateinit var mBoardModel: MemoryBoardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab03)

        mBoard = findViewById(R.id.game_board_grid)
        val size = intent.getIntArrayExtra("size") ?: intArrayOf(3, 3)

        mBoard.columnCount = size[1]
        mBoard.rowCount = size[0]

        mBoardModel = MemoryBoardView(mBoard, size[1], size[0])

        savedInstanceState?.getIntArray("state")?.let {
            mBoardModel.setState(it)
        }

        mBoardModel.setOnGameChangeListener { e ->
            when (e.state) {
                GameStates.Matching, GameStates.Match -> {
                    e.tiles.forEach { it.revealed = true }
                }
                GameStates.NoMatch -> {
                    e.tiles.forEach { it.revealed = true }
                    Timer().schedule(2000) {
                        runOnUiThread {
                            e.tiles.forEach { it.revealed = false }
                        }
                    }
                }
                GameStates.Finished -> {
                    e.tiles.forEach { it.revealed = true }
                    Toast.makeText(this, "Game finished!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("state", mBoardModel.getState())
    }
}