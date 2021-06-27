import kotlin.math.max

class MiniMaxCache {
    private val cache = mutableMapOf<Long, Int>()

    fun clearCache() {
        cache.clear()
    }

    fun getValueForBoard(board: Board): Int? {
        return cache[computeIdForBoard(board)]
    }

    fun cacheBoardValue(board: Board, value: Int) {
        cache[computeIdForBoard(board)] = value
    }

    private fun computeIdForBoard(board: Board): Long {
        var id = 0L
        var currentBoard = board

        repeat(4) {
            id = max(boardToId(currentBoard), id)
            currentBoard = rotateBoard(board)
        }

        currentBoard = transposeBoard(board)

        repeat(4) {
            id = max(boardToId(currentBoard), id)
            currentBoard = rotateBoard(board)
        }

        return id
    }

    private fun transposeBoard(board: Board): Board {
        require(board.sizeX == board.sizeY)

        val transposedBoard = board.clone()

        for (i in 0 until board.sizeX) {
            for (j in 0 until board.sizeY) {
                transposedBoard[i, j] = board[j, i]
            }
        }

        return transposedBoard
    }

    private fun rotateBoard(board: Board): Board {
        require(board.sizeX == board.sizeY)

        val rotatedBoard = board.clone()

        for (i in 0 until board.sizeX) {
            for (j in 0 until board.sizeY) {
                rotatedBoard[i, j] = board[board.sizeX - j - 1, i]
            }
        }

        return rotatedBoard
    }

    private fun boardToId(board: Board): Long {
        var result = 0L

        for (y in 0 until board.sizeY) {
            for (x in 0 until board.sizeX) {
                val cellInt = board[x, y].ordinal

                // could fit base of three, but base of four should be faster
                result = 4 * result + cellInt
            }
        }

        return result
    }
}