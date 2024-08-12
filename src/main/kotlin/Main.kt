import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/*
@Clase:
    Representa un producto que se vende en un supermercado.
@Atributos:
    Int id,
    String nombre,
    Double precio
 */
data class Producto(val id: Int, val nombre: String, val precio: Double)

/*
@Clase:
    Representa la asociacion entre un producto y un supermercado, conteniendo el stock de dicho
    producto en ese supermercado.
@Atributos:
    Producto producto,
    Int cantidad
 */
class StockProducto(val producto: Producto, var cantidad: Int) {
    fun cantidadXCosto(): Double {
        return producto.precio * cantidad
    }
}

/*
@Clase:
    Representa una venta de un producto en un supermercado.
@Atributos:
    Int id,
    StockProducto productoVendido
 */
class Venta(val id: Int, val productoVendido: StockProducto) {
    fun getTotalVenta(): Double {
        return productoVendido.cantidadXCosto()
    }

    fun getCantidadVendida(): Int {
        return productoVendido.cantidad
    }

    fun getProductoVenta(): Producto {
        return productoVendido.producto
    }
}

/*
@Clase:
    Representa un supermercado de una cadena.
@Atributos:
    Int id,
    String nombre,
    LocalTime horarioApertura,
    LocalTime horarioCierre,
    List<String> diasApertura,
    List<StockProducto> listaProductos,
    List<Venta> historialVenta
 */
class Supermercado(
    val id: Int,
    val nombre: String,
    val listaProductos: List<Producto>,
    val stockBase: Int,
    val horarioApertura: LocalTime,
    val horarioCierre: LocalTime,
    val diasApertura: List<String>
) {
    private val listaStockProductos = mutableListOf<StockProducto>()
    private val historialVenta = mutableListOf<Venta>()

    init {
        for (producto in listaProductos) {
            listaStockProductos.add(StockProducto(producto, stockBase))
        }
    }

    /*
    @Funcion:
        Se encarga de registrar una venta en el supermercado. En caso de no disponer del stock necesario o no encontrar
        el producto buscado, lo indica con un mensaje y devuelve 0.
    @Parametros:
        Int id,
        Int cantidad
    @TipoRetorno:
        Double
     */
    fun registrarVenta(id: Int, cantidad: Int): Double {
        if (id < 0 || cantidad <= 0) {
            println("El id de producto debe ser 0 o mayor, y la cantidad debe mayor a 0.")
            return 0.toDouble()
        }

        val stockProducto = listaStockProductos.find { it.producto.id == id }

        return if (stockProducto != null) {
            if (stockProducto.cantidad >= cantidad) {
                val nuevaVenta = Venta(historialVenta.size + 1, StockProducto(stockProducto.producto, cantidad))
                historialVenta.add(nuevaVenta)
                stockProducto.cantidad -= cantidad
                nuevaVenta.getTotalVenta()
            } else {
                println("No hay stock disponible para la venta que desea registrar. Stock actual: ".plus(stockProducto.cantidad))
                0.toDouble()
            }
        } else {
            println("No se encontró el producto ingresado.")
            0.toDouble()
        }
    }

    /*
    @Funcion:
        Devuelve la cantidad de unidades vendidas de un producto.
    @Parametros:
        Int id
    @TipoRetorno:
        Int
     */
    fun cantidadVendidaProducto(id: Int): Int {
        if (id < 0) {
            println("El id de producto debe ser 0 o mayor.")
            return 0
        }
        return historialVenta.filter { it.getProductoVenta().id == id }.sumOf { it.getCantidadVendida() }
    }

    /*
    @Funcion:
        Devuelve el valor total de ventas de un producto.
    @Parametros:
        Int id
    @TipoRetorno:
        Double
     */
    fun montoVendidoProducto(id: Int): Double {
        if (id < 0) {
            println("El id de producto debe ser 0 o mayor.")
            return 0.toDouble()
        }
        return historialVenta.filter { it.getProductoVenta().id == id }.sumOf { it.getTotalVenta() }
    }

    /*
    @Funcion:
        Devuelve el valor total de ventas acumulado del supermercado. Si no existen ventas,
        devuelve 0.
    @Parametros:
        Ninguno
    @TipoRetorno:
        Double
     */
    fun montoVendidoTotal(): Double {
        return historialVenta.sumOf { it.getTotalVenta() }
    }

    fun getHistorialVenta(): List<Venta> = historialVenta
}

/*
@Clase:
    Representa una cadena de supermercados.
@Atributos:
    List<Supermercado> listaSupermercados
 */
class Cadena {
    private val listaSupermercados = mutableListOf<Supermercado>()

    /*
    @Funcion:
        Agrega un supermercado, pasado por parametro, a la cadena de supermercados.
    @Parametros:
        Supermercado supermercado
    @TipoRetorno:
        Ninguno
     */
    fun agregarSupermercado(supermercado: Supermercado) {
        if (listaSupermercados.none { it.id == supermercado.id }) {
            listaSupermercados.add(supermercado)
        } else {
            println("El id de supermercado que intenta ingresar ya existe en la cadena.")
        }
    }

    /*
    @Funcion:
        Devuelve la cantidad de ventas de un producto en un supermercado seleccionado.
    @Parametros:
        Int idSupermercado,
        Int idProducto
    @TipoRetorno:
        Int
     */
    fun cantidadVendidaProductoSupermercado(idSupermercado: Int, idProducto: Int): Int {
        val supermercado = listaSupermercados.find { it.id == idSupermercado }
        return supermercado?.cantidadVendidaProducto(idProducto) ?: 0
    }

    /*
    @Funcion:
        Devuelve el monto de ingresos de un producto en un supermercado seleccionado.
    @Parametros:
        Int idSupermercado,
        int idProducto
    @TipoRetorno:
        Double
     */
    fun montoVendidoProductoSupermercado(idSupermercado: Int, idProducto: Int): Double {
        val supermercado = listaSupermercados.find { it.id == idSupermercado }
        return supermercado?.montoVendidoProducto(idProducto) ?: 0.toDouble()
    }

    /*
    @Funcion:
        Devuelve el monto de ingresos total de un supermercado seleccionado.
    @Parametros:
        Int idSupermercado
    @TipoRetorno:
        Double
     */
    fun montoVendidoSupermercado(idSupermercado: Int): Double {
        val supermercado = listaSupermercados.find { it.id == idSupermercado }
        return supermercado?.montoVendidoTotal() ?: 0.toDouble()
    }

    /*
    @Funcion:
        Devuelve el valor total de ventas acumulado de todos los supermercados de la cadena. Si no existen supermercados,
        devuelve 0.
    @Parametros:
        Ninguno
    @TipoRetorno:
        Double
     */
    fun montoVendidoTotal(): Double {
        return if (listaSupermercados.isEmpty()) {
            println("No existe ningún supermercado en la cadena.")
            0.toDouble()
        } else {
            listaSupermercados.sumOf { it.montoVendidoTotal() }
        }
    }

    /*
    @Funcion:
        Devuelve un String listando los supermercados que se encuentran abiertos en el dia y horario ingresado
        por el usuario. Si ningun supermercado se encuentra abierto, se devuelve una cadena vacia.
    @Parametros:
        LocalTime horario,
        String dia
    @TipoRetorno:
        String
     */
    fun supermercadosAbiertos(horario: LocalTime, dia: String): String {
        val supermercadosAbiertos = listaSupermercados.filter {
            horario.isAfter(it.horarioApertura) && horario.isBefore(it.horarioCierre) && it.diasApertura.contains(dia)
        }
        return supermercadosAbiertos.joinToString(", ") { "${it.nombre} (${it.id})" }
    }

    /*
    @Funcion:
        Devuelve un String listando el supermercado que mayores ingresos obtuvo de toda la cadena de supermercados,
        junto con el monto obtenido.
    @Parametros:
        Ninguno
    @TipoRetorno:
        String
     */
    fun supermercadoMayorVentas(): String {
        return if (listaSupermercados.isEmpty()) {
            println("No existe ningún supermercado en la cadena.")
            ""
        } else {
            val supermercadoMayorVenta = listaSupermercados.maxByOrNull { it.montoVendidoTotal() }!!
            "${supermercadoMayorVenta.nombre} (${supermercadoMayorVenta.id}). Ingresos totales: $${supermercadoMayorVenta.montoVendidoTotal()}"
        }
    }

    /*
    @Funcion:
        Devuelve un String listando los 5 productos con mayor cantidad de ventas en toda la cadena de supermercados,
        junto con la cantidad vendida, separados por '-'.
        En caso de haber menos productos, muestra los disponibles
    @Parametros:
        Ninguno
    @TipoRetorno:
        String
     */
    fun productosMayorVentas(): String {
        val listaProductos = mutableMapOf<Int, StockProducto>()

        //Recorro cada supermercado
        listaSupermercados.forEach { supermercado ->
            //Recorro cada venta del supermercado
            supermercado.getHistorialVenta().forEach { venta ->
                val idProducto = venta.getProductoVenta().id
                listaProductos[idProducto]?.let {
                    //Si el producto ya existe en la lista, actualizo su cantidad
                    it.cantidad += venta.getCantidadVendida()
                } ?: run {
                    //Si el producto no existe, se lo agrega a la lista
                    listaProductos[idProducto] = StockProducto(venta.getProductoVenta(), venta.getCantidadVendida())
                }
            }
        }

        val listaOrdenada = listaProductos.values.sortedByDescending { it.cantidad }.take(5)
        return listaOrdenada.joinToString(" - ") { "${it.producto.nombre}: ${it.cantidad}" }
    }
}

fun main() {
    val listaProductos = mutableListOf<Producto>()
    listaProductos.add(Producto(1, "Computadora", 130.6))
    listaProductos.add(Producto(3, "Pelota", 34.7))
    listaProductos.add(Producto(7, "Plato", 13.2))
    listaProductos.add(Producto(16, "Monopatin", 100.toDouble()))

    val diasHabiles = listOf("Lunes", "Martes", "Miercoles", "Jueves", "Viernes")

    val supermercadoCarrefour = Supermercado(3, "Carrefour", listaProductos, 10, LocalTime.of(8, 0), LocalTime.of(20, 0), diasHabiles)
    val supermercadoDia = Supermercado(5, "Dia", listaProductos, 5, LocalTime.of(8, 30), LocalTime.of(20, 30), diasHabiles)

    supermercadoCarrefour.registrarVenta(3, 4)
    supermercadoCarrefour.registrarVenta(7, 5)
    supermercadoDia.registrarVenta(16, 2)
    supermercadoDia.registrarVenta(16, 3)

    val supermercadosArgentinos = Cadena()

    supermercadosArgentinos.agregarSupermercado(supermercadoCarrefour)
    supermercadosArgentinos.agregarSupermercado(supermercadoDia)

    // DEFINO UNA METODOLOGÍA SIMPLE PARA UTILIZAR, POR CONSOLA, LAS FUNCIONALIDADES

    val scanner = Scanner(System.`in`)
    val mensajeOpciones = """
    Seleccione una de las siguientes opciones:
    1) 5 productos más vendidos de la cadena
    2) Supermercado con mayores ingresos
    3) Supermercados abiertos
    4) Monto total vendido
    5) Cantidad vendida de un producto en un supermercado
    6) Monto vendido de un producto en un supermercado
    7) Monto total vendido en un supermercado
    
    0) Finalizar
    """

    var seleccion = -1

    println("Bienvenido!")

    while (seleccion != 0) {
        println(mensajeOpciones)
        print("Ingrese la opción seleccionada: ")
        seleccion = scanner.nextInt()

        if (seleccion < 0 || seleccion > 7) {
            println("Por favor, ingrese una opción válida")
        } else {
            when (seleccion) {
                1 -> println(supermercadosArgentinos.productosMayorVentas())
                2 -> println(supermercadosArgentinos.supermercadoMayorVentas())
                3 -> {
                    println("Ingrese el horario de apertura con el siguiente formato: HH:MM")
                    val horario = scanner.next()
                    print("Ingrese el día: ")
                    val dia = scanner.next()

                    val formato = DateTimeFormatter.ofPattern("H:mm")
                    println(supermercadosArgentinos.supermercadosAbiertos(LocalTime.parse(horario, formato),
                        dia.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }))
                }
                4 -> println("Monto total: $" + supermercadosArgentinos.montoVendidoTotal())
                5 -> {
                    print("Ingrese el ID del supermercado: ")
                    val idSupermercado = scanner.nextInt()
                    print("Ingrese el ID del producto: ")
                    val idProducto = scanner.nextInt()
                    println("Cantidad: " + supermercadosArgentinos.cantidadVendidaProductoSupermercado(idSupermercado, idProducto))
                }
                6 -> {
                    print("Ingrese el ID del supermercado: ")
                    val idSupermercado = scanner.nextInt()
                    print("Ingrese el ID del producto: ")
                    val idProducto = scanner.nextInt()
                    println("Monto: $" + supermercadosArgentinos.montoVendidoProductoSupermercado(idSupermercado, idProducto))
                }
                7 -> {
                    print("Ingrese el ID del supermercado: ")
                    val idSupermercado = scanner.nextInt()
                    println("Monto: $" + supermercadosArgentinos.montoVendidoSupermercado(idSupermercado))
                }
            }
        }
    }
    scanner.close()
}

/*
    CONSIDERACIONES:
    - DEBIDO AL ALCANCE DEL PROYECTO, SE UTILIZO UN FORMATO SIMPLIFICADO DE DOCUMENTACION. ADEMAS, SOLO
      SE DOCUMENTARON LAS FUNCIONALIDADES SOLICITADAS, OMITIENDO OTRAS FUNCIONES BASICAS.
 */