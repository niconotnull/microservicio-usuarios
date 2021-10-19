package com.springboot.microservicios.usuarios.controller;

import com.springboot.microservicios.usuarios.entity.AlumnoEntity;
import com.springboot.microservicios.usuarios.exception.DBException;
import com.springboot.microservicios.usuarios.generic.GenericController;
import com.springboot.microservicios.usuarios.service.AlumnoService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
public class AlumnoController   extends GenericController<AlumnoEntity, AlumnoService> {



    @GetMapping(value = "/listar")
    public ResponseEntity<?> findAll(){
        try {
            return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
        }catch (DBException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/paginacion")
    public ResponseEntity<?> findAll(Pageable pageable){
        try {
            return new ResponseEntity<>(service.findAll(pageable), HttpStatus.OK);
        }catch (DBException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }



    @GetMapping(value = "/alumnos-por-curso")
    public ResponseEntity<?> obtenerAlumnosXCurso(@RequestParam List<Integer> ids) {
        try {
            return new ResponseEntity<>(service.findAllById(ids), HttpStatus.OK);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateAlumno(@Valid @RequestBody AlumnoEntity alumno, BindingResult result, @PathVariable Integer id){
        try{
            if (result.hasErrors()) {
                return this.validar(result);
            }
            return new ResponseEntity<AlumnoEntity>(service.update(alumno, id, null), HttpStatus.OK);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/filtrar/{termino}")
    public ResponseEntity<?> filtrar(@PathVariable String termino) {
        try {
            return new ResponseEntity<>(service.findByNombreOrApellido(termino), HttpStatus.OK);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping(value = "/crear-con-foto")
    public ResponseEntity<?> crearConFoto(@Valid AlumnoEntity alumno, BindingResult result, @RequestParam MultipartFile file) {
        try {
            if(!file.isEmpty()){
                alumno.setFoto(file.getBytes());
            }
            return super.save(alumno, result);
        } catch (DBException | IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/editar-con-foto/{id}")
    public ResponseEntity<?> updateAlumnoConFoto(@Valid  AlumnoEntity alumno, BindingResult result,
                                                 @PathVariable Integer id, @RequestParam MultipartFile file){
        try{
            if (result.hasErrors()) {
                return this.validar(result);
            }
            return new ResponseEntity<AlumnoEntity>(service.update(alumno, id, file), HttpStatus.OK);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/uploads/img/{id}")
    public ResponseEntity<?> verFoto(@PathVariable Integer id){
        try{
            Optional<AlumnoEntity> o = Optional.ofNullable(service.findById(id));
            if (!o.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el id: "+id);
            }
            Resource image = new ByteArrayResource(o.get().getFoto());
            return  ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


}
