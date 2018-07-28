class Board(
    val sizeX: Int,
    val sizeY: Int
) {
    private val items: Array<Array<CellValue>>

    init {
        require(sizeX > 0)
        require(sizeY > 0)
        items = Array(sizeY) { Array(sizeX) { CellValue.EMPTY } }
    }

    fun clone(): Board {
        val clonedBoard = Board(sizeX, sizeY)
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                clonedBoard[x, y] = board[x, y]
            }
        }

        return clonedBoard
    }

    operator fun get(x: Int, y: Int): CellValue {
        return items[y][x]
    }

    operator fun set(x: Int, y: Int, value: CellValue) {
        items[y][x] = value
    }

    fun isEmptyAt(x: Int, y: Int): Boolean {
        return items[y][x] == CellValue.EMPTY
    }

    fun getAllEmptyPositions(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                if (isEmptyAt(x, y)) {
                    result.add(Pair(x, y))
                }
            }
        }

        return result
    }

    fun checkForWinner(winningLength: Int): CellValue {
        val rowsWinner = checkRows(winningLength)
        val columnsWinner = checkColumns(winningLength)
        val diagonalsWinner = checkDiagonals(winningLength)
        return when {
            rowsWinner != CellValue.EMPTY -> rowsWinner
            columnsWinner != CellValue.EMPTY -> columnsWinner
            diagonalsWinner != CellValue.EMPTY -> diagonalsWinner
            else -> CellValue.EMPTY
        }
    }

    val rows: List<List<CellValue>> get() {
        return items.map { it.toList() }
    }

    val columns: List<List<CellValue>> get() {
        return (0 until sizeX).map { x ->
            (0 until sizeY).map { y ->
                items[y][x]
            }
        }
    }

    val diagonals: List<List<CellValue>> get() {
        val diagonals = mutableListOf<List<CellValue>>()

        for (k in -sizeX + 1 until sizeY) {
            val currentDiagonal = mutableListOf<CellValue>()
            var i = k
            var j = 0
            while (i < sizeY && j < sizeX) {
                if (i >= 0 && j >= 0) currentDiagonal.add(board[j, i])
                i++
                j++
            }

            diagonals.add(currentDiagonal)
        }

        for (k in -sizeX + 1 until sizeY) {
            val currentDiagonal = mutableListOf<CellValue>()
            var i = k
            var j = sizeX - 1
            while (i < sizeY && j >= 0) {
                if (i >= 0 && j >= 0) currentDiagonal.add(board[j, i])
                i++
                j--
            }

            diagonals.add(currentDiagonal)
        }

        return diagonals
    }

    fun checkRows(winningLength: Int): CellValue {
        return rows
            .flatMap { it.windowed(winningLength, 1) }
            .filterNot { it.contains(CellValue.EMPTY) }
            .firstOrNull { it.allItemsSame() }?.first() ?: CellValue.EMPTY
    }

    fun checkColumns(winningLength: Int): CellValue {
        return columns
            .flatMap { it.windowed(winningLength, 1) }
            .filterNot { it.contains(CellValue.EMPTY) }
            .firstOrNull { it.allItemsSame() }?.first() ?: CellValue.EMPTY
    }

    fun checkDiagonals(winningLength: Int): CellValue {
        return diagonals
            .flatMap { it.windowed(winningLength, 1) }
            .filterNot { it.contains(CellValue.EMPTY) }
            .firstOrNull { it.allItemsSame() }?.first() ?: CellValue.EMPTY
    }

    fun <T> List<T>.allItemsSame(): Boolean {
        return this.zipWithNext().all { (x, y) -> x == y }
    }
}