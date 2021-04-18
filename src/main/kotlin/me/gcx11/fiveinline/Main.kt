import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAnimationFrame
import kotlinx.coroutines.launch
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import kotlinx.browser.document
import kotlinx.browser.window

object Game {
    val board = Board(5, 5)
    val winningLength = 4
    val boardView = BoardView(board)
    var nextMoveComputation: Job? = null
    var isGameOver = false
}

fun main() {
    window.onload = {
        val canvas = document.createElement("canvas") as HTMLCanvasElement
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        canvas.width  = window.innerWidth
        canvas.height = window.innerHeight
        document.body!!.appendChild(canvas)

        GlobalScope.launch {
            while (true) {
                window.awaitAnimationFrame()
                clearCanvas(context)
                draw(context)
            }
        }

        canvas.addEventListener("click", { e ->
            val mouseEvent = e as MouseEvent

            val cellCoords = Game.boardView.mousePositionToCoord(mouseEvent.offsetX, mouseEvent.offsetY)
            cellCoords?.let { (x, y) ->
                if (Game.board.isEmptyAt(x, y) &&
                    Game.nextMoveComputation?.isActive != true &&
                    !Game.isGameOver
                ) {
                    Game.board[x, y] = CellValue.FIRST
                    checkForWinner()
                    if (!Game.isGameOver) computeNextMoveAsync()
                }
            }
        })
    }
}

fun computeNextMoveAsync() {
    Game.nextMoveComputation = GlobalScope.launch {
        val ai = MiniMaxAI(Game.winningLength)
        val (x, y) = ai.nextMove(Game.board.clone())
        Game.board[x, y] = CellValue.SECOND
    }

    Game.nextMoveComputation?.invokeOnCompletion {
        checkForWinner()
    }
}

fun checkForWinner() {
    val winnerValue = Game.board.checkForWinner(Game.winningLength)
    if (winnerValue != CellValue.EMPTY) {
        println("WINNER IS: $winnerValue")
        Game.isGameOver = true
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
    Game.boardView.drawBoard(context)
}