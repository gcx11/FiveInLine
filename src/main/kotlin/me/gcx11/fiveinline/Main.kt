import kotlinx.atomicfu.atomic
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.awaitAnimationFrame
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document
import kotlin.browser.window

val board = Board(10, 10)
val boardView = BoardView(board)

var nextMoveComputation: Job? = null
val isGameOver = atomic(false)

fun main(args: Array<String>) {
    window.onload = {
        val canvas = document.createElement("canvas") as HTMLCanvasElement
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        canvas.width  = window.innerWidth
        canvas.height = window.innerHeight
        document.body!!.appendChild(canvas)

        launch {
            while (true) {
                window.awaitAnimationFrame()
                clearCanvas(context)
                draw(context)
            }
        }

        canvas.addEventListener("click", { e ->
            val mouseEvent = e as MouseEvent

            val cellCoords = boardView.mousePositionToCoord(mouseEvent.offsetX, mouseEvent.offsetY)
            cellCoords?.let { (x, y) ->
                if (board.isEmptyAt(x, y)
                    && nextMoveComputation?.isActive != true
                    && isGameOver.value == false) {
                    board[x, y] = CellValue.FIRST
                    checkForWinner()
                    if (isGameOver.value == false) computeNextMoveAsync()
                }
            }
        })
    }
}

fun computeNextMoveAsync() {
    nextMoveComputation = launch {
        val ai = AdvancedAI()
        val (x, y) = ai.nextMove(board)
        board[x, y] = CellValue.SECOND
    }

    nextMoveComputation?.invokeOnCompletion {
        checkForWinner()
    }
}

fun checkForWinner() {
    val winnerValue = board.checkForWinner(5)
    if (winnerValue != CellValue.EMPTY) {
        println("WINNER IS: $winnerValue")
        isGameOver.value = true
    }
}

fun clearCanvas(context: CanvasRenderingContext2D) {
    context.clearRect(
        0.0,
        0.0,
        context.canvas.width.toDouble(),
        context.canvas.height.toDouble()
    )
}

fun draw(context: CanvasRenderingContext2D) {
    boardView.drawBoard(context)
}