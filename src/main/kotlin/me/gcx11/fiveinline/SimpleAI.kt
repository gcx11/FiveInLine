import kotlinx.coroutines.delay

class SimpleAI: FiveInLineAI() {
    override suspend fun nextMove(board: Board): Pair<Int, Int> {
        delay(200L)
        return board.getAllEmptyPositions().shuffled().first()
    }
}