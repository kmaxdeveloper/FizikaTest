package uz.kmax.fizikatest.data.game

/**
 * Physics 2048 o'yinining asosiy logika mexanizmi.
 */
class Physics2048Engine {

    companion object {
        const val GRID_SIZE = 4
        const val EMPTY = 0
    }

    private val grid = Array(GRID_SIZE) { IntArray(GRID_SIZE) }

    var score: Int = 0
        private set

    var hasWon: Boolean = false
        private set

    data class TileMove(val fromRow: Int, val fromCol: Int, val toRow: Int, val toCol: Int)
    data class MergedTile(val row: Int, val col: Int, val value: Int)
    data class NewTile(val row: Int, val col: Int, val value: Int)

    private val _lastMoves = mutableListOf<TileMove>()
    private val _lastMerges = mutableListOf<MergedTile>()
    private var _lastNewTile: NewTile? = null

    val lastMoves: List<TileMove> get() = _lastMoves.toList()
    val lastMerges: List<MergedTile> get() = _lastMerges.toList()
    val lastNewTile: NewTile? get() = _lastNewTile

    private val _mergedCells = mutableListOf<Pair<Int, Int>>()

    init {
        addFixedTile(2)
        addFixedTile(2)
    }

    fun getMergedCells(): List<Pair<Int, Int>> = _mergedCells.toList()

    fun getGrid(): Array<IntArray> = Array(GRID_SIZE) { r -> grid[r].copyOf() }

    fun hasEmptyCell(): Boolean = grid.any { row -> row.any { it == EMPTY } }

    fun canMove(): Boolean {
        if (hasEmptyCell()) return true
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                val v = grid[r][c]
                if (c + 1 < GRID_SIZE && grid[r][c + 1] == v) return true
                if (r + 1 < GRID_SIZE && grid[r + 1][c] == v) return true
            }
        }
        return false
    }

    fun swipeLeft(): Boolean = move(1, 0)
    fun swipeRight(): Boolean = move(-1, GRID_SIZE - 1)
    fun swipeUp(): Boolean = moveVertical(1)
    fun swipeDown(): Boolean = moveVertical(-1, GRID_SIZE - 1)

    private fun move(colStep: Int, startCol: Int): Boolean {
        _lastMoves.clear(); _lastMerges.clear(); _lastNewTile = null; _mergedCells.clear()
        var changed = false
        for (r in 0 until GRID_SIZE) {
            val cols = if (colStep > 0) 0 until GRID_SIZE else GRID_SIZE - 1 downTo 0
            val oldLinePositions = cols.filter { grid[r][it] != EMPTY }
            val line = oldLinePositions.map { grid[r][it] }
            
            val result = mutableListOf<Int>()
            val sourcePositions = oldLinePositions.toMutableList()
            var i = 0
            while (i < line.size) {
                if (i + 1 < line.size && line[i] == line[i + 1]) {
                    val mergedValue = line[i] * 2
                    result.add(mergedValue)
                    score += mergedValue
                    if (mergedValue >= 2048) hasWon = true
                    
                    val targetCol = if (colStep > 0) result.size - 1 else GRID_SIZE - result.size
                    _mergedCells.add(Pair(r, targetCol))
                    
                    _lastMoves.add(TileMove(r, sourcePositions[i], r, targetCol))
                    _lastMoves.add(TileMove(r, sourcePositions[i+1], r, targetCol))
                    
                    i += 2
                } else {
                    result.add(line[i])
                    val targetCol = if (colStep > 0) result.size - 1 else GRID_SIZE - result.size
                    _lastMoves.add(TileMove(r, sourcePositions[i], r, targetCol))
                    i++
                }
            }
            
            val newLine = result.toMutableList()
            while (newLine.size < GRID_SIZE) newLine.add(EMPTY)
            
            for (j in 0 until GRID_SIZE) {
                val targetCol = if (colStep > 0) j else GRID_SIZE - 1 - j
                val newValue = newLine[j]
                if (grid[r][targetCol] != newValue) {
                    changed = true
                    grid[r][targetCol] = newValue
                }
            }
        }
        if (changed) { addRandomTile() }
        return changed
    }

    private fun moveVertical(rowStep: Int, startRow: Int = 0): Boolean {
        _lastMoves.clear(); _lastMerges.clear(); _lastNewTile = null; _mergedCells.clear()
        var changed = false
        for (c in 0 until GRID_SIZE) {
            val rows = if (rowStep > 0) 0 until GRID_SIZE else GRID_SIZE - 1 downTo 0
            val oldLinePositions = rows.filter { grid[it][c] != EMPTY }
            val line = oldLinePositions.map { grid[it][c] }
            
            val result = mutableListOf<Int>()
            val sourcePositions = oldLinePositions.toMutableList()
            var i = 0
            while (i < line.size) {
                if (i + 1 < line.size && line[i] == line[i + 1]) {
                    val mergedValue = line[i] * 2
                    result.add(mergedValue)
                    score += mergedValue
                    if (mergedValue >= 2048) hasWon = true
                    
                    val targetRow = if (rowStep > 0) result.size - 1 else GRID_SIZE - result.size
                    _mergedCells.add(Pair(targetRow, c))
                    
                    _lastMoves.add(TileMove(sourcePositions[i], c, targetRow, c))
                    _lastMoves.add(TileMove(sourcePositions[i+1], c, targetRow, c))
                    
                    i += 2
                } else {
                    result.add(line[i])
                    val targetRow = if (rowStep > 0) result.size - 1 else GRID_SIZE - result.size
                    _lastMoves.add(TileMove(sourcePositions[i], c, targetRow, c))
                    i++
                }
            }
            
            val newLine = result.toMutableList()
            while (newLine.size < GRID_SIZE) newLine.add(EMPTY)

            for (j in 0 until GRID_SIZE) {
                val targetRow = if (rowStep > 0) j else GRID_SIZE - 1 - j
                val newValue = newLine[j]
                if (grid[targetRow][c] != newValue) {
                    changed = true
                    grid[targetRow][c] = newValue
                }
            }
        }
        if (changed) { addRandomTile() }
        return changed
    }

    private fun addRandomTile() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        var maxZ = 0
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] == EMPTY) emptyCells.add(Pair(r, c))
                if (grid[r][c] > maxZ) maxZ = grid[r][c]
            }
        }
        
        if (emptyCells.isEmpty()) return
        val (r, c) = emptyCells.random()
        
        val rand = Math.random()
        val value = when {
            maxZ >= 1024 -> if (rand < 0.9) 32 else 64
            maxZ >= 512 -> if (rand < 0.9) 16 else 32
            maxZ >= 256 -> if (rand < 0.9) 8 else 16
            maxZ >= 128 -> if (rand < 0.9) 4 else 8
            else -> if (rand < 0.9) 2 else 4
        }
        
        grid[r][c] = value
        _lastNewTile = NewTile(r, c, value)
    }

    private fun getMinGeneratedVal(): Int {
        var maxZ = 0
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] > maxZ) maxZ = grid[r][c]
            }
        }
        return when {
            maxZ >= 512 -> 16
            maxZ >= 256 -> 8
            maxZ >= 128 -> 4
            maxZ >= 64 -> 4
            else -> 2
        }
    }

    fun revive() {
        val threshold = getMinGeneratedVal()
        for (r in 0 until GRID_SIZE)
            for (c in 0 until GRID_SIZE)
                if (grid[r][c] != EMPTY && grid[r][c] <= threshold) grid[r][c] = EMPTY
        addRandomTile()
    }

    fun removeTile(r: Int, c: Int): Boolean {
        if (r !in 0 until GRID_SIZE || c !in 0 until GRID_SIZE) return false
        if (grid[r][c] == EMPTY) return false
        grid[r][c] = EMPTY
        return true
    }

    fun cleanupLowElements(): Boolean {
        var changed = false
        val threshold = getMinGeneratedVal()
        for (r in 0 until GRID_SIZE) {
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] != EMPTY && grid[r][c] <= threshold) {
                    grid[r][c] = EMPTY
                    changed = true
                }
            }
        }
        return changed
    }

    fun reset() {
        score = 0; hasWon = false
        for (r in 0 until GRID_SIZE) grid[r].fill(EMPTY)
        addFixedTile(2)
        addFixedTile(2)
    }

    private fun addFixedTile(value: Int) {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until GRID_SIZE)
            for (c in 0 until GRID_SIZE)
                if (grid[r][c] == EMPTY) emptyCells.add(Pair(r, c))
        if (emptyCells.isEmpty()) return
        val (r, c) = emptyCells.random()
        grid[r][c] = value
        _lastNewTile = NewTile(r, c, value)
    }
}
