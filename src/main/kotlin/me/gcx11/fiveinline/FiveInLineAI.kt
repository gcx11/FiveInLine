abstract class FiveInLineAI {
    abstract suspend fun nextMove(board: Board): Pair<Int, Int>
}