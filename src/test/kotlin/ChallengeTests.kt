import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChallengeTests {

    /**
     * Datos de prueba:
     *  # Productos [id, nombre, precio]
     *      - 1, "Carne", 10.0
     *      - 2, "Pescado", 20.0
     *      - 3, "Pollo", 30.0
     *      - 4, "Cerdo", 45.0
     *      - 5, "Ternera", 50.0
     *      - 6, "Cordero", 65.0
     *
     *  # Supermercado [id, nombre]
     *    - 1, "Supermercado A"
     *    - 2, "Supermercado B"
     *    - 3, "Supermercado C"
     * */

    @BeforeEach
    fun setUp() {
        // Tests set-up, if required
    }

    @Test
    fun assertEqualsExampleTest() {
        assertEquals("TEST_VALUE", "TEST_VALUE")
    }

    @Test
    fun assertThrowsExampleTest() {
        assertThrows(Exception::class.java) {
            throw Exception("Test exception example")
        }
    }

}