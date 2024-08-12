import org.junit.jupiter.api.BeforeEach
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.Test

class ChallengeTests {

    private lateinit var supermercadoDia: Supermercado
    private lateinit var supermercadoCarrefour: Supermercado
    private lateinit var supermercadoAlvear: Supermercado
    private lateinit var cadenaSupermercados: Cadena

    @BeforeEach
    fun setUp() {
        val productos = listOf(
            Producto(1, "Carne", 10.0),
            Producto(2, "Pescado", 20.0),
            Producto(3, "Pollo", 30.0),
            Producto(4, "Cerdo", 45.0),
            Producto(5, "Ternera", 50.0),
            Producto(6, "Cordero", 65.0)
        )

        supermercadoDia = Supermercado(
            id = 1,
            nombre = "Dia",
            listaProductos = productos,
            stockBase = 100,
            horarioApertura = LocalTime.of(8, 0),
            horarioCierre = LocalTime.of(22, 0),
            diasApertura = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        )

        supermercadoCarrefour = Supermercado(
            id = 2,
            nombre = "Carrefour",
            listaProductos = productos,
            stockBase = 200,
            horarioApertura = LocalTime.of(8, 30),
            horarioCierre = LocalTime.of(21, 30),
            diasApertura = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        )

        supermercadoAlvear = Supermercado(
            id = 3,
            nombre = "Alvear",
            listaProductos = productos,
            stockBase = 150,
            horarioApertura = LocalTime.of(7, 0),
            horarioCierre = LocalTime.of(23, 0),
            diasApertura = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")
        )

        cadenaSupermercados = Cadena().apply {
            agregarSupermercado(supermercadoDia)
            agregarSupermercado(supermercadoCarrefour)
            agregarSupermercado(supermercadoAlvear)
        }
    }

    @Test
    fun testRegistrarVentaExitosa() {
        val totalVenta = supermercadoDia.registrarVenta(1, 10)
        assertEquals(100.0, totalVenta)
        assertEquals(10, supermercadoDia.cantidadVendidaProducto(1))
    }

    @Test
    fun testRegistrarVentaSinStock() {
        supermercadoDia.registrarVenta(1, 100)
        val totalVenta = supermercadoDia.registrarVenta(1, 10)
        assertEquals(0.0, totalVenta)
    }

    @Test
    fun testRegistrarVentaProductoNoExistente() {
        val totalVenta = supermercadoDia.registrarVenta(999, 10)
        assertEquals(0.0, totalVenta)
    }

    @Test
    fun testMontoVendidoTotalSupermercado() {
        supermercadoDia.registrarVenta(1, 10)
        supermercadoDia.registrarVenta(2, 5)
        assertEquals(200.0, supermercadoDia.montoVendidoTotal())
    }

    @Test
    fun testMontoVendidoTotalCadena() {
        supermercadoDia.registrarVenta(1, 10)
        supermercadoCarrefour.registrarVenta(2, 5)
        supermercadoAlvear.registrarVenta(3, 2)
        assertEquals(260.0, cadenaSupermercados.montoVendidoTotal())
    }

    @Test
    fun testSupermercadosAbiertos() {
        val resultado = cadenaSupermercados.supermercadosAbiertos(LocalTime.of(10, 0), "Lunes")
        assertEquals("Dia (1), Carrefour (2), Alvear (3)", resultado)
    }

    @Test
    fun testSupermercadoMayorVentas() {
        supermercadoDia.registrarVenta(1, 10)
        supermercadoCarrefour.registrarVenta(2, 5)
        val resultado = cadenaSupermercados.supermercadoMayorVentas()
        assertEquals("Dia (1). Ingresos totales: $100.0", resultado)
    }

    @Test
    fun testProductosMayorVentas() {
        supermercadoDia.registrarVenta(1, 10)
        supermercadoCarrefour.registrarVenta(1, 20)
        supermercadoAlvear.registrarVenta(2, 5)
        val resultado = cadenaSupermercados.productosMayorVentas()
        assertEquals("Carne: 30 - Pescado: 5", resultado)
    }
}