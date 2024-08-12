package org.example;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
@Clase:
    Representa un producto que se vende en un supermercado.
@Atributos:
    Integer id,
    String nombre,
    Float precio
 */
class Producto {
    public Integer getId() {
        return id;
    }

    private Integer id;

    public String getNombre() {
        return nombre;
    }

    private String nombre;

    public Float getPrecio() {
        return precio;
    }

    private Float precio;

    public Producto(Integer id, String nombre, Float precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }
}

/*
@Clase:
    Representa la asociacion entre un producto y un supermercado, conteniendo el stock de dicho
    producto en ese supermercado.
@Atributos:
    Producto producto,
    Integer cantidad
 */
class StockProducto {
    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    private Producto producto;
    private Integer cantidad;

    public StockProducto(Producto producto, Integer cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Float cantidadXCosto() {
        return this.producto.getPrecio() * this.cantidad;
    }
}

/*
@Clase:
    Representa una venta de un producto en un supermercado.
@Atributos:
    Integer id,
    StockProducto productoVendido
 */
class Venta {
    private Integer id;

    public StockProducto getProductoVendido() {
        return productoVendido;
    }

    private StockProducto productoVendido;

    public Venta(Integer id, StockProducto productoVendido) {
        this.id = id;
        this.productoVendido = productoVendido;
    }

    public Float getTotalVenta() {
        return this.productoVendido.cantidadXCosto();
    }

    public Integer getCantidadVendida() {
        return this.productoVendido.getCantidad();
    }

    public Producto getProductoVenta() {
        return this.productoVendido.getProducto();
    }
}

/*
@Clase:
    Representa un supermercado de una cadena.
@Atributos:
    Integer id,
    String nombre,
    LocalTime horarioApertura,
    LocalTime horarioCierre,
    List<String> diasApertura,
    List<StockProducto> listaProductos,
    List<Venta> historialVenta
 */
class Supermercado {
    private Integer id;

    public String getNombre() {
        return nombre;
    }

    public Integer getId() {
        return id;
    }

    public LocalTime getHorarioApertura() {
        return horarioApertura;
    }

    public LocalTime getHorarioCierre() {
        return horarioCierre;
    }

    public List<String> getDiasApertura() {
        return diasApertura;
    }

    private LocalTime horarioApertura, horarioCierre;

    private String nombre;
    private List<StockProducto> listaProductos;
    private List<String> diasApertura;

    public List<Venta> getHistorialVenta() {
        return historialVenta;
    }

    private List<Venta> historialVenta;

    public Supermercado(Integer id, String nombre, List<Producto> listaProductos, Integer stockBase, LocalTime horarioApertura, LocalTime horarioCierre, List<String> diasApertura) {
        this.id = id;
        this.nombre = nombre;
        this.horarioCierre = horarioCierre;
        this.horarioApertura = horarioApertura;
        this.diasApertura = diasApertura;

        this.listaProductos = new ArrayList<>();
        for (Producto producto: listaProductos){
            StockProducto nuevoStock = new StockProducto(producto, stockBase);
            this.listaProductos.add(nuevoStock);
        }

        this.historialVenta = new ArrayList<>();
    }

    /*
    @Funcion:
        Se encarga de registrar una venta en el supermercado. En caso de no disponer del stock necesario o no encontrar
        el producto buscado, lo indica con un mensaje y devuelve 0.
    @Parametros:
        Integer id,
        Integer cantidad
    @TipoRetorno:
        Float
     */
    public Float registrarVenta(Integer id, Integer cantidad) {
        if (id < 0 || cantidad <= 0) {
            System.out.println("El id de producto debe ser 0 o mayor, y la cantidad debe mayor a 0.");
            return 0F;
        }

        List<StockProducto> listStockProducto = listaProductos.stream().filter(prod -> Objects.equals(prod.getProducto().getId(), id)).toList();
        if (!listStockProducto.isEmpty()) {
            StockProducto stockProducto = listStockProducto.getFirst();
            if (stockProducto.getCantidad() > cantidad) {
                Venta nuevaVenta = new Venta(this.historialVenta.size() + 1, new StockProducto(stockProducto.getProducto(), cantidad));
                this.historialVenta.add(nuevaVenta);
                return nuevaVenta.getTotalVenta();
            }
            else {
                System.out.println("No hay stock disponible para la venta que desea registrar. Stock actual: " + stockProducto.getCantidad());
                return 0F;
            }
        }
        else {
            System.out.println("No se encontro el producto ingresado.");
            return 0F;
        }
    }

    /*
    @Funcion:
        Devuelve la cantidad de unidades vendidas de un producto.
    @Parametros:
        Integer id
    @TipoRetorno:
        Integer
     */
    public Integer cantidadVendidaProducto(Integer id){
        if (id < 0) {
            System.out.println("El id de producto debe ser 0 o mayor.");
            return 0;
        }
        return this.historialVenta.stream().filter(venta -> Objects.equals(venta.getProductoVenta().getId(), id)).map(Venta::getCantidadVendida).reduce(0, Integer::sum);
    }

    /*
    @Funcion:
        Devuelve el valor total de ventas de un producto.
    @Parametros:
        Integer id
    @TipoRetorno:
        Float
     */
    public Float montoVendidoProducto(Integer id){
        if (id < 0) {
            System.out.println("El id de producto debe ser 0 o mayor.");
            return 0F;
        }
        return this.historialVenta.stream().filter(venta -> Objects.equals(venta.getProductoVenta().getId(), id)).map(Venta::getTotalVenta).reduce(0F, Float::sum);
    }

    /*
    @Funcion:
        Devuelve el valor total de ventas acumulado del supermercado. Si no existen ventas,
        devuelve 0.
    @Parametros:
        Ninguno
    @TipoRetorno:
        Float
     */
    public Float montoVendidoTotal(){
        if (this.historialVenta.isEmpty()) {
            return 0F;
        }
        return this.historialVenta.stream().map(Venta::getTotalVenta).reduce(0F, Float::sum);
    }
}

/*
@Clase:
    Representa una cadena de supermercados.
@Atributos:
    List<Supermercado> listaSupermercados
 */
class Cadena {
    private List<Supermercado> listaSupermercados;

    public Cadena() {
        this.listaSupermercados = new ArrayList<>();
    }

    /*
    @Funcion:
        Agrega un supermercado, pasado por parametro, a la cadena de supermercados.
    @Parametros:
        Supermercado supermercado
    @TipoRetorno:
        Ninguno
     */
    public void agregarSupermercado(Supermercado supermercado) {
        if (this.listaSupermercados.stream().filter(superm -> Objects.equals(superm.getId(), supermercado.getId())).toList().isEmpty())
            this.listaSupermercados.add(supermercado);
        else System.out.println("El id de supermercado que intenta ingresar ya existe en la cadena.");
    }

    /*
    @Funcion:
        Devuelve la cantidad de ventas de un producto en un supermercado seleccionado.
    @Parametros:
        Integer idSupermercado,
        Integer idProducto
    @TipoRetorno:
        Integer
     */
    public Integer cantidadVendidaProductoSupermercado(Integer idSupermercado, Integer idProducto) {
        List<Supermercado> listaSupermercado = this.listaSupermercados.stream().filter(superm -> Objects.equals(superm.getId(), idSupermercado)).toList();
        if (listaSupermercado.isEmpty()) return 0;
        else return listaSupermercado.getFirst().cantidadVendidaProducto(idProducto);
    }

    /*
    @Funcion:
        Devuelve el monto de ingresos de un producto en un supermercado seleccionado.
    @Parametros:
        Integer idSupermercado,
        Integer idProducto
    @TipoRetorno:
        Float
     */
    public Float montoVendidoProductoSupermercado(Integer idSupermercado, Integer idProducto) {
        List<Supermercado> listaSupermercado = this.listaSupermercados.stream().filter(superm -> Objects.equals(superm.getId(), idSupermercado)).toList();
        if (listaSupermercado.isEmpty()) return 0F;
        else return listaSupermercado.getFirst().montoVendidoProducto(idProducto);
    }

    /*
    @Funcion:
        Devuelve el monto de ingresos total de un supermercado seleccionado.
    @Parametros:
        Integer idSupermercado
    @TipoRetorno:
        Float
     */
    public Float montoVendidoSupermercado(Integer idSupermercado) {
        List<Supermercado> listaSupermercado = this.listaSupermercados.stream().filter(superm -> Objects.equals(superm.getId(), idSupermercado)).toList();
        if (listaSupermercado.isEmpty()) return 0F;
        else return listaSupermercado.getFirst().montoVendidoTotal();
    }

    /*
    @Funcion:
        Devuelve el valor total de ventas acumulado de todos los supermercados de la cadena. Si no existen supermercados,
        devuelve 0.
    @Parametros:
        Ninguno
    @TipoRetorno:
        Float
     */
    public Float montoVendidoTotal() {
        if (this.listaSupermercados.isEmpty()) {
            System.out.println("No existe ningún supermercado en la cadena.");
            return 0F;
        }
        else {
            return this.listaSupermercados.stream().map(Supermercado::montoVendidoTotal).reduce(0F, Float::sum);
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
    public String supermercadosAbiertos(LocalTime horario, String dia) {
        StringBuilder retornar = new StringBuilder();
        List<Supermercado> listaSupermercadosAbiertos = this.listaSupermercados.stream().filter(supermercado -> horario.isAfter(supermercado.getHorarioApertura()) && horario.isBefore(supermercado.getHorarioCierre()) && supermercado.getDiasApertura().contains(dia)).toList();
        for (Supermercado supermercado : listaSupermercadosAbiertos) {
            retornar.append(supermercado.getNombre()).append(" (").append(supermercado.getId()).append("), ");
        }
        return retornar.isEmpty() ? "" : retornar.deleteCharAt(retornar.length()-1).toString();
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
    public String supermercadoMayorVentas() {
        if (this.listaSupermercados.isEmpty()) {
            System.out.println("No existe ningún supermercado en la cadena.");
            return "";
        }
        else {
            List<Supermercado> listaOrdenada = this.listaSupermercados;
            listaOrdenada.sort(Comparator.comparing(Supermercado::montoVendidoTotal).reversed());
            Supermercado supermercado = listaOrdenada.getFirst();
            return supermercado.getNombre() + " (" + supermercado.getId() + "). Ingresos totales: $" + supermercado.montoVendidoTotal();
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
    public String productosMayorVentas() {
        List<StockProducto> listaProductos = new ArrayList<>();
        StringBuilder retornar = new StringBuilder();

        //Recorro cada supermercado
        for (Supermercado supermercado : this.listaSupermercados) {
            //Recorro cada venta del supermercado
            for (Venta venta : supermercado.getHistorialVenta()) {
                Boolean encontrado = false;
                for (StockProducto stockProducto : listaProductos) {
                    //Si el producto ya existe en la lista, actualizo su cantidad
                    if (Objects.equals(stockProducto.getProducto().getId(), venta.getProductoVenta().getId())) {
                        encontrado = true;
                        stockProducto.setCantidad(stockProducto.getCantidad() + venta.getCantidadVendida());
                    }
                }
                //Si el producto no existe, se lo agrega a la lista
                if (!encontrado) listaProductos.add(venta.getProductoVendido());
            }
        }
        listaProductos.sort(Comparator.comparing(StockProducto::getCantidad).reversed());
        List<StockProducto> listaRecortada = listaProductos.stream().limit(5).toList();

        for (StockProducto stockProducto : listaRecortada) {
            retornar.append(stockProducto.getProducto().getNombre()).append(": ").append(stockProducto.getCantidad()).append(" - ");
        }
        return retornar.deleteCharAt(retornar.length()-2).toString();
    }
}

public class Main {
    public static void main(String[] args){
        List<Producto> listaProductos = new ArrayList<>();
        listaProductos.add(new Producto(1, "Computadora", 130.6F));
        listaProductos.add(new Producto(3, "Pelota", 34.7F));
        listaProductos.add(new Producto(7, "Plato", 13.2F));
        listaProductos.add(new Producto(16, "Monopatin", 100F));

        List<String> diasHabiles = Arrays.asList("Lunes", "Martes", "Miercoles", "Jueves", "Viernes");

        Supermercado supermercadoCarrefour = new Supermercado(3, "Carrefour", listaProductos,10, LocalTime.of(8, 0), LocalTime.of(20, 0), diasHabiles);
        Supermercado supermercadoDia = new Supermercado(5, "Dia", listaProductos,5, LocalTime.of(8, 30), LocalTime.of(20, 30), diasHabiles);

        supermercadoCarrefour.registrarVenta(3, 4);
        supermercadoCarrefour.registrarVenta(7, 5);
        supermercadoDia.registrarVenta(16, 2);
        supermercadoDia.registrarVenta(16, 3);

        Cadena supermercadosArgentinos = new Cadena();

        supermercadosArgentinos.agregarSupermercado(supermercadoCarrefour);
        supermercadosArgentinos.agregarSupermercado(supermercadoDia);

        //DEFINO UNA METODOLOGIA SIMPLE PARA UTILIZAR, POR CONSOLA, LAS FUNCIONALIDADES

        Scanner scanner = new Scanner(System.in);
        String mensajeOpciones = """
                Seleccione una de las siguientes opciones:\s
                1) 5 productos mas vendidos de la cadena\s
                2) Supermercado con mayores ingresos\s
                3) Supermercados abiertos\s
                4) Monto total vendido\s
                5) Cantidad vendida de un producto en un supermercado\s
                6) Monto vendido de un producto en un supermercado\s
                7) Monto total vendido en un supermercado\s
                \s
                0) Finalizar\s
                """;
        Integer seleccion = -1;

        System.out.println("Bienvenido!");

        while (seleccion != 0) {
            System.out.println(mensajeOpciones);
            System.out.print("Ingrese la opcion seleccionada: ");
            seleccion = scanner.nextInt();

            if (seleccion < 0 || seleccion > 7) System.out.println("Por favor, ingrese una opcion valida ");
            else {
                Integer idSupermercado;
                Integer idProducto;
                switch (seleccion) {
                    case 1:
                        System.out.println(supermercadosArgentinos.productosMayorVentas());
                        break;
                    case 2:
                        System.out.println(supermercadosArgentinos.supermercadoMayorVentas());
                        break;
                    case 3:
                        System.out.println("Ingrese el horario de apertura con el siguiente formato: HH:MM");
                        String horario = scanner.next();
                        System.out.print("Ingrese el dia: ");
                        String dia = scanner.next();

                        DateTimeFormatter formato = DateTimeFormatter.ofPattern("H:mm");
                        System.out.println(supermercadosArgentinos.supermercadosAbiertos(LocalTime.parse(horario, formato), dia.substring(0, 1).toUpperCase() + dia.substring(1)));
                        break;
                    case 4:
                        System.out.println("Monto total: $" + supermercadosArgentinos.montoVendidoTotal());
                        break;
                    case 5:
                        System.out.print("Ingrese el ID del supermercado: ");
                        idSupermercado = scanner.nextInt();
                        System.out.print("Ingrese el ID del producto: ");
                        idProducto = scanner.nextInt();
                        System.out.println("Cantidad: " + supermercadosArgentinos.cantidadVendidaProductoSupermercado(idSupermercado, idProducto));
                        break;
                    case 6:
                        System.out.print("Ingrese el ID del supermercado: ");
                        idSupermercado = scanner.nextInt();
                        System.out.print("Ingrese el ID del producto: ");
                        idProducto = scanner.nextInt();
                        System.out.println("Monto: $" + supermercadosArgentinos.montoVendidoProductoSupermercado(idSupermercado, idProducto));
                        break;
                    case 7:
                        System.out.print("Ingrese el ID del supermercado: ");
                        idSupermercado = scanner.nextInt();
                        System.out.println("Monto: $" + supermercadosArgentinos.montoVendidoSupermercado(idSupermercado));
                        break;
                }
            }
        }
        scanner.close();
    }
}

/*
    CONSIDERACIONES:
    - NO SE DEFINIERON TODOS LOS GETTER Y SETTER QUE DEBERIAN PARA REDUCIR EL CODIGO, ASI COMO
      LA SOBRESCRITURA DE FUNCIONES COMO HASH(), TOSTRING(), ETC.
    - DEBIDO AL ALCANCE DEL PROYECTO, SE UTILIZO UN FORMATO SIMPLIFICADO DE DOCUMENTACION. ADEMAS, SOLO
      SE DOCUMENTARON LAS FUNCIONALIDADES SOLICITADAS, OMITIENDO FUNCIONES BASICAS COMO GETTERS, SETTERS, ETC.
 */