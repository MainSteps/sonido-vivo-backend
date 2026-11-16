package cl.sonidovivo.api.service;

import cl.sonidovivo.api.dto.ProductoRequest;
import cl.sonidovivo.api.dto.ProductoResponse;
import cl.sonidovivo.api.entity.Producto;
import cl.sonidovivo.api.exception.RecursoNoEncontradoException;
import cl.sonidovivo.api.mapper.ProductoMapper;
import cl.sonidovivo.api.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void obtenerPorIdLanza404SiNoExiste() {
        when(productoRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminarMarcaInactivo() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setActivo(true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.eliminar(1L);

        assertThat(producto.isActivo()).isFalse();
        verify(productoRepository, never()).delete(any());
    }

    @Test
    void listarFiltraPorCategoria() {
        Pageable pageable = PageRequest.of(0, 10);
        Producto producto = new Producto();
        when(productoRepository.findByActivoTrueAndCategoriaIgnoreCase("guitarras", pageable))
                .thenReturn(new PageImpl<>(List.of(producto)));
        when(productoMapper.toResponse(producto)).thenReturn(respuestaEjemplo());

        var pagina = productoService.listar("guitarras", pageable);

        assertThat(pagina.getContent()).hasSize(1);
        verify(productoRepository).findByActivoTrueAndCategoriaIgnoreCase("guitarras", pageable);
    }

    private ProductoResponse respuestaEjemplo() {
        return new ProductoResponse(
                1L, "Guitarra", "Desc", BigDecimal.TEN, 5, "guitarras", null, true,
                Instant.now(), Instant.now());
    }
}