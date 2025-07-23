package com.examen.productos_api.service;

import com.examen.productos_api.model.Producto;
import com.examen.productos_api.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.DoubleSummaryStatistics;

@Service
public class ProductoService {
    
    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<Producto> obtenerTodos() {
        return repo.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public Producto crearProducto(Producto producto) {
        if (producto.getPrecio() <= 0 || producto.getCantidad() < 0) {
            throw new IllegalArgumentException("Precio debe ser > 0 y cantidad >= 0.");
        }
        return repo.save(producto);
    }

    public Optional<Producto> actualizarProducto(Long id, Producto producto) {
        return repo.findById(id).map(p -> {
            if (producto.getPrecio() <= 0 || producto.getCantidad() < 0) {
                throw new IllegalArgumentException("Precio debe ser > 0 y cantidad >= 0.");
            }
            p.setNombre(producto.getNombre());
            p.setDescripcion(producto.getDescripcion());
            p.setCantidad(producto.getCantidad());
            p.setPrecio(producto.getPrecio());
            return repo.save(p);
        });
    }

    public void eliminarProducto(Long id) {
        repo.deleteById(id);
    }

    public Estadisticas calcularEstadisticas() {
        List<Producto> productos = repo.findAll();
        long total = productos.size();
        long disponibles = productos.stream().filter(p -> p.getCantidad() > 0).count();
        long agotados = total - disponibles;

        DoubleSummaryStatistics stats = productos.stream().mapToDouble(Producto::getPrecio).summaryStatistics();
        double promedio = stats.getAverage();

        return new Estadisticas(total, promedio, disponibles, agotados);
    }

    public record Estadisticas(long total, double promedioPrecio, long disponibles, long agotados) {}
}