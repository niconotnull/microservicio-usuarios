package com.springboot.microservicios.usuarios.service;

import com.springboot.microservicios.usuarios.entity.AlumnoEntity;
import com.springboot.microservicios.usuarios.generic.GenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AlumnoService extends GenericService<AlumnoEntity> {

    AlumnoEntity update(AlumnoEntity alumno, Integer id, MultipartFile file);

    public List<AlumnoEntity> findByNombreOrApellido(String termino);

    List<AlumnoEntity> findAllById(Iterable<Integer> ids);

    void eliminarCursoAlumnoPorId(Integer id);

    Page<AlumnoEntity> findAll(Pageable pageable);

    List<AlumnoEntity> findAll();


}
