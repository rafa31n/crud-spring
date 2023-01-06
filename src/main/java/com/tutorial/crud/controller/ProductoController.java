package com.tutorial.crud.controller;

import com.tutorial.crud.dto.Mensaje;
import com.tutorial.crud.dto.ProductoDto;
import com.tutorial.crud.entity.Producto;
import com.tutorial.crud.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductoController {
    @Autowired
    ProductoService productoService;

    @GetMapping("")
    public ResponseEntity<List<Producto>> findAll() {
        List<Producto> list = productoService.list();
        return new ResponseEntity<List<Producto>>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") int id) {
        if (!productoService.existsById(id)) {
            return new ResponseEntity<>(new Mensaje("El producto solicitado no existe"), HttpStatus.NOT_FOUND);
        }
        Producto producto = productoService.getOne(id).get();
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @GetMapping("/detail-name/{nombre}")
    public ResponseEntity<?> getByName(@PathVariable("nombre") String nombre) {
        if (!productoService.existsByNombre(nombre)) {
            return new ResponseEntity<>(new Mensaje("El producto solicitado no existe"), HttpStatus.NOT_FOUND);
        }
        Producto producto = productoService.getByNombre(nombre).get();
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Mensaje> create(@RequestBody ProductoDto productDto) {
        if (productDto.getNombre() == null) {
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre del producto es obligatorio"), HttpStatus.BAD_REQUEST);
        }
        if (productDto.getPrecio() == null || productDto.getPrecio() < 0) {
            return new ResponseEntity<Mensaje>(new Mensaje("El precio debe ser mayor que 0.0"), HttpStatus.BAD_REQUEST);
        }
        if (productoService.existsByNombre(productDto.getNombre())) {
            return new ResponseEntity<Mensaje>(new Mensaje("El producto ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }
        Producto producto = new Producto(productDto.getNombre(), productDto.getPrecio());
        productoService.save(producto);
        return new ResponseEntity<Mensaje>(new Mensaje("Producto creado con éxito"), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mensaje> update(@PathVariable("id") int id, @RequestBody ProductoDto productDto) {
        if (!productoService.existsById(id)) {
            return new ResponseEntity<Mensaje>(new Mensaje("El El producto no existe"), HttpStatus.NOT_FOUND);
        }
        if (productoService.existsByNombre(productDto.getNombre()) && productoService.getByNombre(productDto.getNombre()).get().getId() != id) {
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }
        if (productDto.getNombre() == null) {
            return new ResponseEntity<Mensaje>(new Mensaje("El nombre del producto es obligatorio"), HttpStatus.BAD_REQUEST);
        }
        if (productDto.getPrecio() == null || productDto.getPrecio() < 0) {
            return new ResponseEntity<Mensaje>(new Mensaje("El precio debe ser mayor que 0.0"), HttpStatus.BAD_REQUEST);
        }
        Producto producto = productoService.getOne(id).get();
        producto.setNombre(productDto.getNombre());
        producto.setPrecio(productDto.getPrecio());
        productoService.save(producto);
        return new ResponseEntity<Mensaje>(new Mensaje("Producto actualizado con éxito"), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Mensaje> delete(@PathVariable("id") int id) {
        if (!productoService.existsById(id)) {
            return new ResponseEntity<Mensaje>(new Mensaje("El producto a eliminar no existe"), HttpStatus.NOT_FOUND);
        }
        productoService.delete(id);
        return new ResponseEntity<Mensaje>(new Mensaje("Producto Eliminado"), HttpStatus.OK);
    }
}
