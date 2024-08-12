import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

data class Producto(val id: Int, val nombre: String, val precio: Double)

class StockProducto(val producto: Producto, var cantidad: Int) {
    fun cantidadXCosto(): Double {
        return producto.precio * cantidad
    }
}

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

    fun cantidadVendidaProducto(id: Int): Int {
        if (id < 0) {
            println("El id de producto debe ser 0 o mayor.")
            return 0
        }
        return historialVenta.filter { it.getProductoVenta().id == id }.sumOf { it.getCantidadVendida() }
    }

    fun montoVendidoProducto(id: Int): Double {
        if (id < 0) {
            println("El id de producto debe ser 0 o mayor.")
            return 0.toDouble()
        }
        return historialVenta.filter { it.getProductoVenta().id == id }.sumOf { it.getTotalVenta() }
    }

    fun montoVendidoTotal(): Double {
        return historialVenta.sumOf { it.getTotalVenta() }
    }

    fun getHistorialVenta(): List<Venta> = historialVenta.toList()
}

class Cadena {
    private val listaSupermercados = mutableListOf<Supermercado>()

    fun agregarSupermercado(supermercado: Supermercado) {
        if (listaSupermercados.none { it.id == supermercado.id }) {
            listaSupermercados.add(supermercado)
        } else {
            println("El id de supermercado que intenta ingresar ya existe en la cadena.")
        }
    }

    fun cantidadVendidaProductoSupermercado(idSupermercado: Int, idProducto: Int): Int {
        val supermercado = listaSupermercados.find { it.id == idSupermercado }
        return supermercado?.cantidadVendidaProducto(idProducto) ?: 0
    }

    fun montoVendidoProductoSupermercado(idSupermercado: Int, idProducto: Int): Double {
        val supermercado = listaSupermercados.find { it.id == idSupermercado }
        return supermercado?.montoVendidoProducto(idProducto) ?: 0.toDouble()
    }

    fun montoVendidoSupermercado(idSupermercado: Int): Double {
        val supermercado = listaSupermercados.find { it.id == idSupermercado }
        return supermercado?.montoVendidoTotal() ?: 0.toDouble()
    }

    fun montoVendidoTotal(): Double {
        return if (listaSupermercados.isEmpty()) {
            println("No existe ningún supermercado en la cadena.")
            0.toDouble()
        } else {
            listaSupermercados.sumOf { it.montoVendidoTotal() }
        }
    }

    fun supermercadosAbiertos(horario: LocalTime, dia: String): String {
        val supermercadosAbiertos = listaSupermercados.filter {
            horario.isAfter(it.horarioApertura) && horario.isBefore(it.horarioCierre) && it.diasApertura.contains(dia)
        }
        return supermercadosAbiertos.joinToString(", ") { "${it.nombre} (${it.id})" }
    }

    fun supermercadoMayorVentas(): String {
        return if (listaSupermercados.isEmpty()) {
            println("No existe ningún supermercado en la cadena.")
            ""
        } else {
            val supermercadoMayorVenta = listaSupermercados.maxByOrNull { it.montoVendidoTotal() }!!
            "${supermercadoMayorVenta.nombre} (${supermercadoMayorVenta.id}). Ingresos totales: $${supermercadoMayorVenta.montoVendidoTotal()}"
        }
    }

    fun productosMayorVentas(): String {
        val listaProductos = mutableMapOf<Int, StockProducto>()

        listaSupermercados.forEach { supermercado ->
            supermercado.getHistorialVenta().forEach { venta ->
                val idProducto = venta.getProductoVenta().id
                listaProductos[idProducto]?.let {
                    it.cantidad += venta.getCantidadVendida()
                } ?: run {
                    listaProductos[idProducto] = StockProducto(venta.getProductoVenta(), venta.getCantidadVendida())
                }
            }
        }

        val listaOrdenada = listaProductos.values.sortedByDescending { it.cantidad }.take(5)
        return listaOrdenada.joinToString(" - ") { "${it.producto.nombre}: ${it.cantidad}" }
    }
}

fun main() {

}