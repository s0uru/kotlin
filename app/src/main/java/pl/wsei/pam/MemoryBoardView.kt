package pl.wsei.pam

import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import java.util.Stack

class MemoryBoardView(
    private val gridLayout: GridLayout,
    private val cols: Int,
    private val rows: Int
) {
    private val tiles: MutableMap<String, Tile> = mutableMapOf()

    // Upewnij się, że masz wystarczającą liczbę ikon (min. 18 dla planszy 6x6)
    private val icons: List<Int> = listOf(
        android.R.drawable.ic_dialog_email, android.R.drawable.ic_dialog_info,
        android.R.drawable.ic_dialog_map, android.R.drawable.ic_menu_camera,
        android.R.drawable.ic_menu_call, android.R.drawable.ic_menu_compass,
        android.R.drawable.ic_menu_directions, android.R.drawable.ic_menu_gallery,
        android.R.drawable.ic_menu_help, android.R.drawable.ic_menu_info_details,
        android.R.drawable.ic_menu_mapmode, android.R.drawable.ic_menu_myplaces,
        android.R.drawable.ic_menu_preferences, android.R.drawable.ic_menu_recent_history,
        android.R.drawable.ic_menu_search, android.R.drawable.ic_menu_send,
        android.R.drawable.ic_menu_share, android.R.drawable.ic_menu_view
    )

    private val deckResource: Int = pl.wsei.pam.lab01.R.drawable.baseline_rocket_launch_24
    private var onGameChangeStateListener: (MemoryGameEvent) -> Unit = {}
    private val matchedPair: Stack<Tile> = Stack()
    private var logic: MemoryGameLogic = MemoryGameLogic((cols * rows) / 2)

    init {
        val numPairs = (cols * rows) / 2
        val shuffledIcons: MutableList<Int> = mutableListOf<Int>().also {
            // Pobieramy tylko tyle ikon, ile potrzebujemy par
            val selectedIcons = icons.subList(0, numPairs)
            it.addAll(selectedIcons)
            it.addAll(selectedIcons)
            it.shuffle()
        }

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (shuffledIcons.isEmpty()) break // Zabezpieczenie przed brakiem ikon

                val currentIcon = shuffledIcons.removeAt(0)
                val btn = ImageButton(gridLayout.context).also {
                    it.tag = "${row}x${col}"
                    val layoutParams = GridLayout.LayoutParams()
                    layoutParams.width = 0
                    layoutParams.height = 0
                    layoutParams.setGravity(Gravity.CENTER)
                    layoutParams.columnSpec = GridLayout.spec(col, 1, 1f)
                    layoutParams.rowSpec = GridLayout.spec(row, 1, 1f)
                    it.layoutParams = layoutParams
                    gridLayout.addView(it)
                }
                addTile(btn, currentIcon)
            }
        }
    }

    private fun onClickTile(v: View) {
        val tag = v.tag?.toString() ?: return
        val tile = tiles[tag] ?: return

        if (tile.revealed || matchedPair.contains(tile)) return

        matchedPair.push(tile)
        val matchResult = logic.process { tile.tileResource }
        onGameChangeStateListener(MemoryGameEvent(matchedPair.toList(), matchResult))

        if (matchResult != GameStates.Matching) {
            matchedPair.clear()
        }
    }

    private fun addTile(button: ImageButton, resourceImage: Int) {
        button.setOnClickListener(::onClickTile)
        val tile = Tile(button, resourceImage, deckResource)
        tiles[button.tag.toString()] = tile
    }

    fun setOnGameChangeListener(listener: (event: MemoryGameEvent) -> Unit) {
        onGameChangeStateListener = listener
    }

    fun getState(): IntArray {
        val state = IntArray(cols * rows)
        var index = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val tile = tiles["${row}x${col}"]
                state[index] = if (tile != null && tile.revealed) tile.tileResource else -1
                index++
            }
        }
        return state
    }

    fun setState(state: IntArray) {
        val matchedIcons = state.filter { it != -1 }.distinct()
        val availableIcons = icons.filterNot { it in matchedIcons }.toMutableList()
        val neededPairsCount = (state.count { it == -1 }) / 2

        logic = MemoryGameLogic(state.size / 2)

        val iconsToShuffle = mutableListOf<Int>()
        for (i in 0 until neededPairsCount) {
            if (availableIcons.isNotEmpty()) {
                val icon = availableIcons.removeAt(0)
                iconsToShuffle.add(icon)
                iconsToShuffle.add(icon)
            }
        }
        iconsToShuffle.shuffle()

        var index = 0
        var shuffleIndex = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val tile = tiles["${row}x${col}"]
                if (tile != null) {
                    val savedVal = state[index]
                    if (savedVal != -1) {
                        tile.tileResource = savedVal
                        tile.revealed = true
                        tile.removeOnClickListener()
                    } else if (shuffleIndex < iconsToShuffle.size) {
                        tile.tileResource = iconsToShuffle[shuffleIndex++]
                        tile.revealed = false
                    }
                }
                index++
            }
        }
    }
}