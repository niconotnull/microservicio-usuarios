package com.springboot.microservicios.usuarios.generic;

import com.springboot.microservicios.usuarios.exception.DBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public class GenericServiceImpl<E, R extends PagingAndSortingRepository<E, Integer>> implements GenericService<E> {

    @Autowired
    protected R repository;

    @Override
    public E save(E entity) {
        try {
            return repository.save(entity);
        } catch (
                DataAccessException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        }
    }



    @Override
    public E findById(Integer id) {
        try{
            E entity = repository.findById(id).orElse(null);
            if(entity == null ){
                throw new DBException("No existe registro con el id : "+id);
            }
            return  entity;
        }catch (DataAccessException e){
            throw new DBException(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        }
    }

    @Override
    public void deleteById(Integer id) {
        try{
            E entity = repository.findById(id).orElse(null);
            if(entity == null ){
                throw new DBException("No se puede eliminar no existe registro con el id : "+id);
            }
            repository.deleteById(id);
        }catch (DataAccessException e){
            throw new DBException(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        }
    }

    public GenericServiceImpl() {
        super();
    }
}
