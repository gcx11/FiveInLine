import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BoardTests {

    var miniMaxAI = MiniMaxAI()

    @Test
    fun testLineCheck() {
        assertEquals(0,
            miniMaxAI.longestLineSize(
                listOf(CellValue.EMPTY, CellValue.EMPTY, CellValue.EMPTY), CellValue.FIRST
            )
        )
        assertEquals(1,
            miniMaxAI.longestLineSize(
                listOf(CellValue.FIRST, CellValue.EMPTY, CellValue.EMPTY), CellValue.FIRST
            )
        )
    }

    @Test
    fun testClone() {
        val b = Board(3, 3)
        b[1, 1] = CellValue.FIRST
        b[2, 2] = CellValue.SECOND

        val clone = b.clone()

        assertEquals(CellValue.FIRST, clone[1, 1])
        assertEquals(CellValue.SECOND, clone[2, 2])

    }

    @Test
    fun testRows() {
        val temp = Board(5, 5)

        assertEquals(25, temp.rows.sumBy { it.size })
    }

    @Test
    fun testColumns() {
        val temp = Board(5, 5)

        assertEquals(25, temp.columns.sumBy { it.size })
    }

    @Test
    fun testDiagonals() {
        val temp = Board(5, 5)

        assertEquals(50, temp.diagonals.sumBy { it.size })
    }

    @Test
    fun testEmptyBoard() {
        val emptyBoard = Board(5, 5)

        with(miniMaxAI) {
            assertEquals(0, emptyBoard.longestLineLength(CellValue.FIRST))
            assertEquals(0, emptyBoard.longestLineLength(CellValue.SECOND))
            assertEquals(5, emptyBoard.longestLineLength(CellValue.EMPTY))
        }
    }

    @Test
    fun testMiniMax() = runWaiting {
        val b = Board(3, 3)

        b[0, 0] = CellValue.FIRST
        b[0, 1] = CellValue.SECOND
        b[1, 1] = CellValue.FIRST
        b[1, 0] = CellValue.SECOND

        assertEquals(Pair(2, 2), miniMaxAI.computeNextMove(b, CellValue.FIRST))
    }

    @Test
    fun testFoo() {
        val b = Board(3, 3)

        b[0, 0] = CellValue.FIRST
        b[0, 1] = CellValue.SECOND
        b[1, 1] = CellValue.FIRST
        b[1, 0] = CellValue.SECOND

        b.getAllEmptyPositions().map { (x, y) ->
            val newBoard = b.clone()
            newBoard[x, y] = CellValue.FIRST
            with(miniMaxAI) {
                println("$x $y " + newBoard.longestLineLength(CellValue.FIRST))
            }
        }
    }

    @Test
    fun testEmptySpots() {
        val b = Board(3, 3)

        b[0, 0] = CellValue.FIRST
        b[0, 1] = CellValue.SECOND
        b[1, 1] = CellValue.FIRST
        b[1, 0] = CellValue.SECOND

        assertEquals(5, b.getAllEmptyPositions().size)
    }

    @Test
    fun testWinningLength(): Int? {
        assertEquals(3, miniMaxAI.WINNING_LENGTH)
        return null
    }

    @Test
    fun testNextMove() = runWaiting {
        val board = Board(3, 3)

        val (x, y) = miniMaxAI.computeNextMove(board, CellValue.FIRST)

        println("x: $x, y: $y")

        assertTrue(x == 1 && y == 1)
    }

    fun runWaiting(block: suspend () -> Unit) = GlobalScope.promise { block() }
}