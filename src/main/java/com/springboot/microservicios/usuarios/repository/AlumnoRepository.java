package com.springboot.microservicios.usuarios.repository;

import com.springboot.microservicios.usuarios.entity.AlumnoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AlumnoRepository extends PagingAndSortingRepository<AlumnoEntity, Integer> {

    @Query("select a from AlumnoEntity a where upper(a.nombre) like upper(concat('%',?1,'%')) or upper(a.apellido) like upper(concat('%',?1,'%')) ")
    List<AlumnoEntity> findByNombreOrApellido(String termino);

    List<AlumnoEntity> findAllByOrderByIdDesc();

    Page<AlumnoEntity> findAllByOrderByIdDesc(Pageable pageable);

}
