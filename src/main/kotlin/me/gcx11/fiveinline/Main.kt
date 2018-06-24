import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document
import kotlin.browser.window

val board = Board(10, 10)
val boardView = BoardView(board)

fun main(args: Array<String>) {
    window.onload = {
        val canvas = document.createElement("canvas") as HTMLCanvasElement
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        canvas.width  = window.innerWidth
        canvas.height = window.innerHeight
        document.body!!.appendChild(canvas)

        launch {
            while (true) {
                context.clearRect(
                    0.0,
                    0.0,
                    context.canvas.width.toDouble(),
                    context.canvas.height.toDouble()
                )
                draw(context)
                delay(1000 / 60)
            }
        }

        canvas.addEventListener("click", { e ->
            val mouseEvent = e as MouseEvent

            val cellCoords = boardView.mousePositionToCoord(mouseEvent.offsetX, mouseEvent.offsetY)
            cellCoords?.let { (x, y) ->
                if (board.isEmptyAt(x, y)) {
                    board[x, y] = CellValue.FIRST
                } else {
                    board[x, y] = CellValue.SECOND
                }
            }
        })
    }
}

fun draw(context: CanvasRenderingContext2D) {
    boardView.drawBoard(context)
}