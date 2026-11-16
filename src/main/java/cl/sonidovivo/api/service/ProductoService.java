package cl.sonidovivo.api.service;

import cl.sonidovivo.api.dto.ProductoRequest;
import cl.sonidovivo.api.dto.ProductoResponse;
import cl.sonidovivo.api.entity.Producto;
import cl.sonidovivo.api.exception.RecursoNoEncontradoException;
import cl.sonidovivo.api.mapper.ProductoMapper;
import cl.sonidovivo.api.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    public ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> listar(String categoria, Pageable pageable) {
        Page<Producto> pagina = (categoria == null || categoria.isBlank())
                ? productoRepository.findByActivoTrue(pageable)
                : productoRepository.findByActivoTrueAndCategoriaIgnoreCase(categoria.trim(), pageable);
        return pagina.map(productoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        return productoMapper.toResponse(producto);
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = productoMapper.toEntity(request);
        Producto guardado = productoRepository.save(producto);
        return productoMapper.toResponse(guardado);
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .filter(Producto::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        productoMapper.actualizarEntity(producto, request);
        return productoMapper.toResponse(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .filter(Producto::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        producto.setActivo(false);
    }
}