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

    fun clear() {
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                this[x, y] = CellValue.EMPTY
            }
        }
    }

    fun clone(): Board {
        val clonedBoard = Board(sizeX, sizeY)
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                clonedBoard[x, y] = this[x, y]
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

    fun getAllEmptyPositionsSortedByNeighbours(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Pair<Int, Int>, Int>>()
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                if (isEmptyAt(x, y)) {
                    result.add(Pair(Pair(x, y), neighbourcount(x, y)))
                }
            }
        }

        result.shuffle()
        result.sortByDescending { it.second }
        return result.filter{ it.second > 0 }.map { it.first }
    }

    private fun neighbourcount(x: Int, y: Int): Int {
        var count = 0

        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue

                if ((x+i in 0 until sizeX) && (y+j in 0 until sizeY)) {
                    if (!isEmptyAt(x+i, y+j)) count++
                }
            }
        }

        return count
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

    val rows: Sequence<Array<CellValue>> get() {
        return items.asSequence()
    }

    val columns: Sequence<Array<CellValue>> get() {
        return sequence {
            (0 until sizeX).map { x ->
                yield(Array(sizeY) { y ->
                    items[y][x]
                })
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
                if (i >= 0 && j >= 0) currentDiagonal.add(this[j, i])
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
                if (i >= 0 && j >= 0) currentDiagonal.add(this[j, i])
                i++
                j--
            }

            diagonals.add(currentDiagonal)
        }

        return diagonals
    }

    fun checkRows(winningLength: Int): CellValue {
        return rows
            .flatMap { it.asSequence().windowed(winningLength, 1) }
            .filterNot { CellValue.EMPTY in it }
            .firstOrNull { it.allItemsSame() }?.first() ?: CellValue.EMPTY
    }

    fun checkColumns(winningLength: Int): CellValue {
        return columns
            .flatMap { it.asSequence().windowed(winningLength, 1) }
            .filterNot { CellValue.EMPTY in it }
            .firstOrNull { it.allItemsSame() }?.first() ?: CellValue.EMPTY
    }

    fun checkDiagonals(winningLength: Int): CellValue {
        return diagonals
            .flatMap { it.windowed(winningLength, 1) }
            .filterNot { CellValue.EMPTY in it }
            .firstOrNull { it.allItemsSame() }?.first() ?: CellValue.EMPTY
    }

    private fun <T: Any> Iterable<T>.allItemsSame(): Boolean {
        //return this.zipWithNext().all { (x, y) -> x == y }
        var lastValue: T? = null
        for (value in this) {
            if (lastValue != null && lastValue != value) return false
            lastValue = value
        }

        return true
    }

    override fun toString(): String {
        return rows.joinToString("\r\n") { row ->
            row.joinToString("|") {
                when (it) {
                    CellValue.FIRST -> "o"
                    CellValue.SECOND -> "x"
                    else -> " "
                }
            }
        }
    }
}