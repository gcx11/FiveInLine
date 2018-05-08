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

    operator fun get(x: Int, y: Int): CellValue {
        return items[y][x]
    }

    operator fun set(x: Int, y: Int, value: CellValue) {
        items[y][x] = value
    }

    fun isEmptyAt(x: Int, y: Int): Boolean {
        return items[y][x] == CellValue.EMPTY
    }
}