import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI
class BoardView(
    val board: Board
) {
    private val gridCellSize = 50.0

    fun drawBoard(context: CanvasRenderingContext2D) {
        context.lineWidth = 2.0
        drawGrid(context)
        fillGrid(context)
    }

    fun mousePositionToCoord(x: Double, y: Double): Pair<Int, Int>? {
        if (x < 0.0 || x > board.sizeX * gridCellSize) return null
        if (y < 0.0 || y > board.sizeY * gridCellSize) return null

        return Pair((x / gridCellSize).toInt(), (y / gridCellSize).toInt())
    }

    private fun drawGrid(context: CanvasRenderingContext2D) {
        context.beginPath()
        context.strokeStyle = "black"

        // draw horizontally
        for (i in 0..board.sizeY) {
            context.moveTo(0.0, i * gridCellSize)
            context.lineTo(board.sizeX * gridCellSize, i * gridCellSize)
        }

        // draw vertically
        for (i in 0..board.sizeX) {
            context.moveTo(i * gridCellSize, 0.0)
            context.lineTo(i * gridCellSize, board.sizeY * gridCellSize)
        }

        context.stroke()
        context.closePath()
    }

    private fun fillGrid(context: CanvasRenderingContext2D) {
        for (y in 0 until board.sizeY) {
            for (x in 0 until board.sizeX) {
                when (board[x, y]) {
                    CellValue.FIRST -> {
                        drawCircleAt(context, x, y)
                    }
                    CellValue.SECOND -> {
                        drawCrossAt(context, x, y)
                    }
                }
            }
        }
    }

    private fun drawCircleAt(context: CanvasRenderingContext2D, x: Int, y: Int) {
        val centerX = (x + 0.5) * gridCellSize
        val centerY = (y + 0.5) * gridCellSize
        val radius = gridCellSize * 0.3

        context.beginPath()
        context.arc(centerX, centerY, radius, 0.0, 2 * PI, false)
        context.strokeStyle = "blue"
        context.fillStyle = "white"
        context.fill()
        context.stroke()
        context.closePath()
    }

    private fun drawCrossAt(context: CanvasRenderingContext2D, x: Int, y: Int) {
        context.beginPath()
        context.strokeStyle = "red"
        context.moveTo((x + 0.25) * gridCellSize, (y + 0.25) * gridCellSize)
        context.lineTo((x + 0.75) * gridCellSize, (y + 0.75) * gridCellSize)
        context.moveTo((x + 0.75) * gridCellSize, (y + 0.25) * gridCellSize)
        context.lineTo((x + 0.25) * gridCellSize, (y + 0.75) * gridCellSize)
        context.stroke()
        context.closePath()
    }
}