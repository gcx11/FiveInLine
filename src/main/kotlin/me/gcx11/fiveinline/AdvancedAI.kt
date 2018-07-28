class AdvancedAI: FiveInLineAI() {

    override suspend fun nextMove(board: Board): Pair<Int, Int> {
        val emptyPositions = board.getAllEmptyPositions().shuffled()
        return emptyPositions.maxBy { (x, y) ->
            val moves = listOf(Triple(x, y, CellValue.SECOND))
            board.clone().applyMoves(moves).computeRank()
        }!!
    }

    fun Board.computeRank(): Int {
        return maxOf(
            rows.map { value(it) }.max() ?: 0,
            columns.map { value(it) }.max() ?: 0,
            diagonals.map { value(it) }.max() ?: 0
        )
    }

    private fun value(values: List<CellValue>): Int {
        var best = 0
        var current = 0
        var wasStartEmpty = false
        values.forEach {
            if (it == CellValue.SECOND) current++
            else {
                val currentEmpty = (it == CellValue.EMPTY)
                if (current >= best) {
                    best = current
                    if (currentEmpty || wasStartEmpty) {
                        best++
                    }
                }
                current = 0
                wasStartEmpty = currentEmpty
            }
        }

        if (current >= best) best = current
        if (wasStartEmpty) best++
        return best
    }

    private fun longestStreakLength(values: List<CellValue>): Int {
        var longest = 0
        var current = 0
        values.forEach {
            if (it == CellValue.SECOND) current++
            else {
                if (current >= longest) longest = current
                current = 0
            }
        }

        if (current >= longest) longest = current
        return longest
    }

    fun Board.applyMoves(moves: List<Triple<Int, Int, CellValue>>): Board {
        return this.apply {
            moves.forEach { (x, y, value) ->
                this[x, y] = value
            }
        }
    }
}