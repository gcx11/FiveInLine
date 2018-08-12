import kotlin.test.Test
import kotlin.test.assertTrue

class BoardTests {

    @Test
    fun foo() {
        val ai = MiniMaxAI()
        assertTrue(ai.WINNING_LENGTH == 3)
    }
}