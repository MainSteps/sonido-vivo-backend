package cl.sonidovivo.api.mapper;

import cl.sonidovivo.api.dto.ProductoRequest;
import cl.sonidovivo.api.dto.ProductoResponse;
import cl.sonidovivo.api.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequest request) {
        Producto producto = new Producto();
        aplicarRequest(producto, request);
        producto.setActivo(true);
        return producto;
    }

    public void actualizarEntity(Producto producto, ProductoRequest request) {
        aplicarRequest(producto, request);
    }

    public ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria(),
                producto.getImagenUrl(),
                producto.isActivo(),
                producto.getCreadoEn(),
                producto.getActualizadoEn());
    }

    private void aplicarRequest(Producto producto, ProductoRequest request) {
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(request.categoria());
        producto.setImagenUrl(request.imagenUrl());
    }
}