import kotlinx.coroutines.yield

class MiniMaxAI(val WINNING_LENGTH: Int = 3): FiveInLineAI() {

    override suspend fun nextMove(board: Board): Pair<Int, Int> {
        return computeNextMove(board, CellValue.SECOND)
    }

    fun Board.longestLineLength(value: CellValue): Int {
        return maxOf(
                rows.map { longestLineSize(it.asIterable(), value) }.maxOrNull() ?: 0,
                columns.map { longestLineSize(it.asIterable(), value) }.maxOrNull() ?: 0,
                diagonals.map { longestLineSize(it, value) }.maxOrNull() ?: 0
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
        return minimax(board, 4, value, true).second ?: Pair(-1, -1)
    }

    suspend fun minimax(board: Board, depth: Int, value: CellValue, maximizingPlayer: Boolean): Pair<Int, Pair<Int, Int>?> {
        val otherValue = if (value == CellValue.FIRST) CellValue.SECOND else CellValue.FIRST

        if (maximizingPlayer) {
            if (
                depth == 0 ||
                board.getAllEmptyPositions().isEmpty() ||
                board.longestLineLength(value) == WINNING_LENGTH ||
                board.longestLineLength(otherValue) == WINNING_LENGTH
            ) {
                return Pair(boardStateValue(board, value), null)
            } else {
                var bestPosition: Pair<Int, Int>? = null
                var maxScore = Int.MIN_VALUE

                // we don't want to block the render loop forever
                yield()

                for ((x, y) in board.getAllEmptyPositions()) {
                    board[x, y] = value

                    val (score, _) = minimax(board, depth - 1, otherValue, false)
                    if (score > maxScore) {
                        maxScore = score
                        bestPosition = Pair(x, y)
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
                return Pair(boardStateValue(board, otherValue), null)
            } else {
                var bestPosition: Pair<Int, Int>? = null
                var minScore = Int.MAX_VALUE

                // we don't want to block the render loop forever
                yield()

                for ((x, y) in board.getAllEmptyPositions()) {
                    board[x, y] = value

                    val (score, _) = minimax(board, depth - 1, otherValue, true)
                    if (score < minScore) {
                        minScore = score
                        bestPosition = Pair(x, y)
                    }

                    board[x, y] = CellValue.EMPTY
                }

                return Pair(minScore, bestPosition)
            }
        }
    }

    private fun boardStateValue(board: Board, value: CellValue): Int {
        val otherValue = if (value == CellValue.FIRST) CellValue.SECOND else CellValue.FIRST

        return if (board.longestLineLength(otherValue) == WINNING_LENGTH) -WINNING_LENGTH else board.longestLineLength(value)
    }
}