import kotlin.math.max
import kotlin.math.min

class MiniMaxAI(val WINNING_LENGTH: Int = 3): FiveInLineAI() {

    override suspend fun nextMove(board: Board): Pair<Int, Int> {
        return computeNextMove(board, CellValue.SECOND)
    }

    fun Board.longestLineLength(value: CellValue): Int {
        return maxOf(
                rows.map { longestLineSize(it, value) }.max() ?: 0,
                columns.map { longestLineSize(it, value) }.max() ?: 0,
                diagonals.map { longestLineSize(it, value) }.max() ?: 0
        )
    }

    fun longestLineSize(row: List<CellValue>, value: CellValue): Int {
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
        val moves = board.getAllEmptyPositions().map { (x, y) ->
            val newBoard = board.clone()
            newBoard[x, y] = value
            minimax(newBoard, 9, true) to Pair(x, y)
        }

        //println(moves)

        return moves.minBy { it.first }?.second ?: Pair(-1, -1)
    }

    suspend fun minimax(board: Board, depth: Int, maximizingPlayer: Boolean): Int {
        val value = if (maximizingPlayer) CellValue.FIRST else CellValue.SECOND
        val sign = if (maximizingPlayer) 1 else -1

        val currentValue = board.longestLineLength(value)

        if (depth == 0 || currentValue == WINNING_LENGTH) {
            return currentValue * sign
        }
        else if (board.getAllEmptyPositions().isEmpty()) {
            return 0
        } else if (maximizingPlayer) {
            var points = Int.MIN_VALUE
            board.getAllEmptyPositions().forEach { (x, y) ->
                val newBoard = board.clone()
                newBoard[x, y] = value
                points = max(points, minimax(newBoard, depth - 1, false))
            }
            return points
        } else {
            var points = Int.MAX_VALUE
            board.getAllEmptyPositions().forEach { (x, y) ->
                val newBoard = board.clone()
                newBoard[x, y] = value
                points = min(points, minimax(newBoard, depth - 1, true))
            }
            return points
        }
    }
}