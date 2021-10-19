package com.springboot.microservicios.usuarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="microservicio-cursos")
public interface CursoFeignClient {

    @DeleteMapping(value = "/eliminar-alumno-curso/{id}")
    void eliminarCursoAlumnoPorId(@PathVariable Integer id);
}
