import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import kotlin.time.*

class MiniMaxAI(val WINNING_LENGTH: Int = 3): FiveInLineAI() {
    private val cache = MiniMaxCache()

    override suspend fun nextMove(board: Board): Pair<Int, Int> {
        return computeNextMove(board, CellValue.SECOND)
    }

    fun Board.longestLineLength(value: CellValue): Int {
        var currentMaxValue = 0

        for (row in rows) {
            val lineSize = longestLineSize(row, value)
            if (lineSize == WINNING_LENGTH) return lineSize
            else if (lineSize > currentMaxValue) currentMaxValue = lineSize
        }

        for (column in columns) {
            val lineSize = longestLineSize(column, value)
            if (lineSize == WINNING_LENGTH) return lineSize
            else if (lineSize > currentMaxValue) currentMaxValue = lineSize
        }

        for (diagonal in diagonals) {
            val lineSize = longestLineSize(diagonal, value)
            if (lineSize == WINNING_LENGTH) return lineSize
            else if (lineSize > currentMaxValue) currentMaxValue = lineSize
        }

        return currentMaxValue
    }

    fun longestLineSize(row: Iterable<CellValue>, value: CellValue): Int {
        var longest = 0
        var current = 0

        row.forEach {
            if (it == value) {
                current++
            }
            else {
                if (current > longest) longest = current
                current = 0
            }
        }

        if (current > longest) longest = current
        return longest
    }

    fun longestLineSize(row: Array<CellValue>, value: CellValue): Int {
        var longest = 0
        var current = 0

        row.forEach {
            if (it == value) {
                current++
            }
            else {
                if (current > longest) longest = current
                current = 0
            }
        }

        if (current > longest) longest = current
        return longest
    }

    @OptIn(ExperimentalTime::class)
    suspend fun computeNextMove(board: Board, value: CellValue): Pair<Int, Int> {
        var move: Pair<Int, Int>? = null

        return try {
            withTimeout(5.toDuration(DurationUnit.SECONDS)) {
                (2..10 step 2).forEach { depth ->
                    move = minimax(board, depth, value, Int.MIN_VALUE, Int.MAX_VALUE, true).second ?: Pair(-1, -1)
                    cache.clearCache()
                }

                move!!
            }
        } catch (ex: CancellationException) {
            move!!
        } finally {
            move!!
        }
    }

    suspend fun minimax(
        board: Board,
        depth: Int,
        value: CellValue,
        alpha: Int,
        beta: Int,
        maximizingPlayer: Boolean
    ): Pair<Int, Pair<Int, Int>?> {
        val otherValue = if (value == CellValue.FIRST) CellValue.SECOND else CellValue.FIRST

        if (maximizingPlayer) {
            if (
                depth == 0 ||
                board.getAllEmptyPositions().isEmpty() ||
                board.longestLineLength(value) == WINNING_LENGTH ||
                board.longestLineLength(otherValue) == WINNING_LENGTH
            ) {
                val score = boardStateValue(board, value, depth)

                return Pair(score, null)
            } else {
                var bestPosition: Pair<Int, Int>? = null
                var maxScore = Int.MIN_VALUE
                var newAlpha = alpha

                // we don't want to block the render loop forever
                yield()

                for ((x, y) in board.getAllEmptyPositionsSortedByNeighbours()) {
                    board[x, y] = value

                    val cachedScore = cache.getValueForBoard(board)
                    val score = if (cachedScore != null) {
                        cachedScore
                    } else {
                        val (computedScore, _) = minimax(board, depth - 1, otherValue, newAlpha, beta, false)
                        cache.cacheBoardValue(board, computedScore)
                        computedScore
                    }

                    if (score > maxScore) {
                        maxScore = score
                        bestPosition = Pair(x, y)
                    }

                    // alpha-beta pruning
                    if (maxScore > newAlpha) newAlpha = maxScore

                    if (beta <= newAlpha) {
                        board[x, y] = CellValue.EMPTY
                        break
                    }

                    board[x, y] = CellValue.EMPTY
                }

                return Pair(maxScore, bestPosition)
            }
        } else {
            if (
                depth == 0 ||
                board.getAllEmptyPositions().isEmpty() ||
                board.longestLineLength(value) == WINNING_LENGTH ||
                board.longestLineLength(otherValue) == WINNING_LENGTH
            ) {
                val score = boardStateValue(board, otherValue, depth)

                return Pair(score, null)
            } else {
                var bestPosition: Pair<Int, Int>? = null
                var minScore = Int.MAX_VALUE
                var newBeta = beta

                // we don't want to block the render loop forever
                yield()

                for ((x, y) in board.getAllEmptyPositionsSortedByNeighbours()) {
                    board[x, y] = value

                    val cachedScore = cache.getValueForBoard(board)
                    val score = if (cachedScore != null) {
                        cachedScore
                    } else {
                        val (computedScore, _) = minimax(board, depth - 1, otherValue, alpha, newBeta, true)
                        cache.cacheBoardValue(board, computedScore)
                        computedScore
                    }

                    if (score < minScore) {
                        minScore = score
                        bestPosition = Pair(x, y)
                    }

                    //alpha-beta pruning
                    if (minScore < newBeta) newBeta = minScore

                    if (newBeta <= alpha) {
                        board[x, y] = CellValue.EMPTY
                        break
                    }

                    board[x, y] = CellValue.EMPTY
                }

                return Pair(minScore, bestPosition)
            }
        }
    }

    private fun boardStateValue(board: Board, value: CellValue, depth: Int): Int {
        val otherValue = if (value == CellValue.FIRST) CellValue.SECOND else CellValue.FIRST

        return when (WINNING_LENGTH) {
            board.longestLineLength(value) -> WINNING_LENGTH * depth
            board.longestLineLength(otherValue) -> -WINNING_LENGTH * depth
            else -> board.longestLineLength(value)
        }
    }

    class AlphaBeta(var alpha: Int, var beta: Int)
}