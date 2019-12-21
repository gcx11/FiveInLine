import kotlin.math.max
import kotlin.math.min

class MiniMaxAI(val WINNING_LENGTH: Int = 3): FiveInLineAI() {

    override suspend fun nextMove(board: Board): Pair<Int, Int> {
        return computeNextMove(board, CellValue.SECOND)
    }

    fun Board.longestLineLength(value: CellValue): Int {
        return maxOf(
                rows.map { longestLineSize(it.asIterable(), value) }.max() ?: 0,
                columns.map { longestLineSize(it.asIterable(), value) }.max() ?: 0,
                diagonals.map { longestLineSize(it, value) }.max() ?: 0
        )
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

    suspend fun computeNextMove(board: Board, value: CellValue): Pair<Int, Int> {
        val otherValue = if (value == CellValue.FIRST) CellValue.SECOND else CellValue.FIRST
        val moves = board.getAllEmptyPositions().map { (x, y) ->
            board[x, y] = value
            val move = minimax(board, 4, otherValue, false) to Pair(x, y)
            board[x, y] = CellValue.EMPTY
            move
        }

        //println(moves)
        return moves.maxBy { it.first }?.second ?: Pair(-1, -1)
    }

    suspend fun minimax(board: Board, depth: Int, value: CellValue, maximizingPlayer: Boolean): Int {
        val otherValue = if (value == CellValue.FIRST) CellValue.SECOND else CellValue.FIRST
        val currentValue = board.longestLineLength(value)

        if (depth == 0 || currentValue == WINNING_LENGTH) {
            return currentValue
        } else if (board.getAllEmptyPositions().isEmpty()) {
            return 0
        } else if (maximizingPlayer) {
            var points = Int.MIN_VALUE
            board.getAllEmptyPositions().forEach { (x, y) ->
                board[x, y] = value
                points = max(points, minimax(board, depth - 1, otherValue, false))
                board[x, y] = CellValue.EMPTY
            }
            return points
        } else {
            var points = Int.MAX_VALUE
            board.getAllEmptyPositions().forEach { (x, y) ->
                board[x, y] = value
                points = min(points, minimax(board, depth - 1, otherValue, true))
                board[x, y] = CellValue.EMPTY
            }
            return points
        }
    }
}