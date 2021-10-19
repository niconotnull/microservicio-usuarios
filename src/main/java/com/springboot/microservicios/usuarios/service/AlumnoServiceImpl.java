package com.springboot.microservicios.usuarios.service;

import com.springboot.microservicios.usuarios.client.CursoFeignClient;
import com.springboot.microservicios.usuarios.entity.AlumnoEntity;
import com.springboot.microservicios.usuarios.exception.DBException;
import com.springboot.microservicios.usuarios.exception.HttpException;
import com.springboot.microservicios.usuarios.generic.GenericServiceImpl;
import com.springboot.microservicios.usuarios.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AlumnoServiceImpl extends GenericServiceImpl<AlumnoEntity, AlumnoRepository> implements AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private CursoFeignClient cursoClient;

    @Override
    public AlumnoEntity update(AlumnoEntity alumno, Integer id, MultipartFile file) {
        try {
            AlumnoEntity alumnoRes = alumnoRepository.findById(id).orElse(null);
            if (alumnoRes == null) {
                throw new DBException("El alumno no existe  con el id : " + id);
            }
            alumnoRes.setNombre(alumno.getNombre());
            alumnoRes.setApellido(alumno.getApellido());
            alumnoRes.setEmail(alumno.getEmail());
            alumnoRes.setUrlFoto(alumno.getUrlFoto());
            if(file != null && !file.isEmpty()){
                alumnoRes.setFoto(file.getBytes());
            }

            return alumnoRepository.save(alumnoRes);
        } catch (DataAccessException | IOException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getMessage()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoEntity> findByNombreOrApellido(String termino) {
        try {
            return alumnoRepository.findByNombreOrApellido(termino);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        }
    }

    @Override
    public List<AlumnoEntity> findAllById(Iterable<Integer> ids) {
       try{
           return (List<AlumnoEntity>) alumnoRepository.findAllById(ids);
       } catch (DataAccessException e) {
           throw new DBException(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
       }

    }

    @Override
    @Transactional
    public void eliminarCursoAlumnoPorId(Integer id) {
        try {
            cursoClient.eliminarCursoAlumnoPorId(id);
        } catch (HttpException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getLocalizedMessage()));
        }
    }

    @Override
    @Transactional
    @Primary
    public void deleteById(Integer id) {
        super.deleteById(id);
        this.eliminarCursoAlumnoPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoEntity> findAll() {
        try{
            return  alumnoRepository.findAllByOrderByIdDesc();
        } catch (HttpException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getLocalizedMessage()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlumnoEntity> findAll(Pageable pageable) {
        try{
            return  alumnoRepository.findAllByOrderByIdDesc(pageable);
        } catch (HttpException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getLocalizedMessage()));
        }
    }
}
