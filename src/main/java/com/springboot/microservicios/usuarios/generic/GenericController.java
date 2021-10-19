package com.springboot.microservicios.usuarios.generic;

import com.springboot.microservicios.usuarios.exception.DBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericController<E, S extends GenericService<E>> {

    @Autowired
    protected S service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable(required = true) Integer id) {
        try {
            return new ResponseEntity<E>(service.findById(id), HttpStatus.OK);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(required = true) Integer id ){
        try{
            service.deleteById(id);
            return new ResponseEntity<>( HttpStatus.NO_CONTENT);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping(value = "/crear")
    public ResponseEntity<?> save(@Valid @RequestBody E entity, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return this.validar(result);
            }
            return new ResponseEntity<E>(service.save(entity), HttpStatus.CREATED);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    protected ResponseEntity<?>validar(BindingResult result){
        Map<String, Object> errores = new HashMap<>();
        result.getFieldErrors().forEach(err->{
            errores.put(err.getField(), " El campo "+err.getField()+" "+err.getDefaultMessage() );
        });
        return ResponseEntity.badRequest().body(errores);
    }
}
