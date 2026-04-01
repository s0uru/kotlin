package pl.wsei.pam.lab03

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import pl.wsei.pam.GameStates
import pl.wsei.pam.MemoryBoardView
import pl.wsei.pam.lab01.R

class Lab03Activity : AppCompatActivity() {
    lateinit var mBoard: GridLayout
    lateinit var mBoardModel: MemoryBoardView

    private lateinit var completionPlayer: MediaPlayer
    private lateinit var negativePlayer: MediaPlayer
    private var isSound: Boolean = true

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
                GameStates.Matching -> {
                    e.tiles.forEach { it.revealed = true }
                }
                GameStates.Match -> {
                    e.tiles.forEach { it.revealed = true }
                    if (isSound) completionPlayer.start()


                    setBoardClickable(false)

                    var animationsFinished = 0
                    e.tiles.forEach { tile ->

                        animatePairedButton(tile.button, Runnable {
                            animationsFinished++

                            if (animationsFinished == e.tiles.size) {
                                setBoardClickable(true)
                            }
                        })
                    }
                }
                GameStates.NoMatch -> {
                    e.tiles.forEach { it.revealed = true }
                    if (isSound) negativePlayer.start()

                    if (e.tiles.size >= 2) {

                        setBoardClickable(false)

                        animateMismatchedButton(e.tiles[0].button, e.tiles[1].button, Runnable {
                            runOnUiThread {
                                e.tiles.forEach { it.revealed = false }

                                setBoardClickable(true)
                            }
                        })
                    }
                }
                GameStates.Finished -> {
                    e.tiles.forEach { it.revealed = true }
                    if (isSound) completionPlayer.start()

                    setBoardClickable(false)

                    var animationsFinished = 0
                    e.tiles.forEach { tile ->
                        animatePairedButton(tile.button, Runnable {
                            animationsFinished++

                            if (animationsFinished == e.tiles.size) {
                                Toast.makeText(this@Lab03Activity, "Game finished!", Toast.LENGTH_SHORT).show()
                                setBoardClickable(true)
                            }
                        })
                    }
                }
            }
        }
    }

    private fun setBoardClickable(isClickable: Boolean) {
        for (i in 0 until mBoard.childCount) {
            mBoard.getChildAt(i).isClickable = isClickable
        }
    }

    override fun onResume() {
        super.onResume()
        completionPlayer = MediaPlayer.create(applicationContext, R.raw.completion)
        negativePlayer = MediaPlayer.create(applicationContext, R.raw.negative_guitar)
    }

    override fun onPause() {
        super.onPause()
        completionPlayer.release()
        negativePlayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.board_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.board_activity_sound -> {

                if (isSound) {
                    Toast.makeText(this, "Dźwięk wyłączony", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_off_24)
                    isSound = false
                } else {
                    Toast.makeText(this, "Dźwięk włączony", Toast.LENGTH_SHORT).show()
                    item.setIcon(R.drawable.baseline_volume_up_24)
                    isSound = true
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("state", mBoardModel.getState())
    }

    private fun animatePairedButton(button: View, action: Runnable) {
        val set = AnimatorSet()
        button.pivotX = (button.width / 2).toFloat()
        button.pivotY = (button.height / 2).toFloat()

        val rotation = ObjectAnimator.ofFloat(button, "rotation", 1080f)
        val scallingX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 4f)
        val scallingY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 4f)
        val fade = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)

        set.startDelay = 500
        set.duration = 2000
        set.interpolator = DecelerateInterpolator()
        set.playTogether(rotation, scallingX, scallingY, fade)
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                button.scaleX = 1f
                button.scaleY = 1f
                button.alpha = 0.0f
                action.run()
            }
        })
        set.start()
    }

    private fun animateMismatchedButton(button1: View, button2: View, action: Runnable) {
        val set = AnimatorSet()
        val shake1 = ObjectAnimator.ofFloat(button1, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        val shake2 = ObjectAnimator.ofFloat(button2, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)

        set.duration = 500
        set.playTogether(shake1, shake2)
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                action.run()
            }
        })
        set.start()
    }
}