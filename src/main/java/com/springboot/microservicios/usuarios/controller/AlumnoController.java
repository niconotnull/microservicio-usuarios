package com.springboot.microservicios.usuarios.controller;

import com.springboot.microservicios.usuarios.entity.AlumnoEntity;
import com.springboot.microservicios.usuarios.exception.DBException;
import com.springboot.microservicios.usuarios.generic.GenericController;
import com.springboot.microservicios.usuarios.service.AlumnoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

// RefreshScope nos permite refrescar las variables de entorno configuradas en el microservicio-config
// es decir nos permite actualizar los componentes, controladores, services etc que se inyectan con @Value y
// también el Enviroment actualiza, refresca el contexto y vulve a inyectar y se vulve a inicializar el componente
// con los cambios reflejados en tiempo real    sin tener que reinicial la aplicación, se debéra de agregar la
// dependencia de spring-actutor
@RefreshScope
@RestController
public class AlumnoController   extends GenericController<AlumnoEntity, AlumnoService> {

    private static final Logger log = LoggerFactory.getLogger(AlumnoController.class);

    @Autowired
    private Environment env;

    // Inyectamos el manejo de la resilencia
    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

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


    // En este método se aplico la resilencia para el manejo del patrón del corto-circuito
    @CircuitBreaker(name="cursos", fallbackMethod = "metodoAlternativo2")
    @TimeLimiter(name="cursos")
    @GetMapping(value = "/alumnos-por-curso")
    public CompletableFuture<ResponseEntity> obtenerAlumnosXCursoConAnotacionesTimeLimiter(@RequestParam List<Integer> ids) {
        System.out.println("Lista de ids : "+ids.toString());
        int cont = 0; // Solo se implemento para simular la resilencia

        return CompletableFuture.supplyAsync(()-> {
            try {
                if(ids.get(0) == 127 ){
                    throw new IllegalStateException("Se simula el error para ejecutar el corto circuito");
                }
                if(ids.get(0) == 92){
                    TimeUnit.SECONDS.sleep(5L);
                }
                return new ResponseEntity<>(service.findAllById(ids), HttpStatus.OK);
            } catch (DBException | InterruptedException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
        });

    }

    // En este método se aplico la resilencia para el manejo del patrón del corto-circuito
    @CircuitBreaker(name="cursos", fallbackMethod = "metodoAlternativo")
    @GetMapping(value = "/alumnos-por-curso-no-aplica")
    public ResponseEntity<?> obtenerAlumnosXCursoConAnotacionesCircuitBreaker(@RequestParam List<Integer> ids) {
        System.out.println("Lista de ids : "+ids.toString());
        int cont = 0; // Solo se implemento para simular la resilencia

        try {
            if(ids.get(0) == 127 ){
                throw new IllegalStateException("Se simula el error para ejecutar el corto circuito");
            }
            if(ids.get(0) == 92){
                TimeUnit.SECONDS.sleep(5L);
            }
            return new ResponseEntity<>(service.findAllById(ids), HttpStatus.OK);
        } catch (DBException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


    // En este método se aplico la resilencia para el manejo del patrón del corto-circuito
    @GetMapping(value = "/alumnos-por-curso-no-aplica-1")
    public ResponseEntity<?> obtenerAlumnosXCursoSinAnotacionCircuitBreaker(@RequestParam List<Integer> ids) {
        System.out.println("Lista de ids : "+ids.toString());
        int cont = 0; // Solo se implemento para simular la resilencia
      return circuitBreakerFactory.create("cursos")
              .run(()->{
        try {
            if(ids.get(0) == 127 ){
                throw new IllegalStateException("Se simula el error para ejecutar el corto circuito");
            }
            if(ids.get(0) == 92){
                TimeUnit.SECONDS.sleep(5L);
            }

            return new ResponseEntity<>(service.findAllById(ids), HttpStatus.OK);
        } catch (DBException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
      }, e->  new ResponseEntity<>(metodoAlternativo(e), HttpStatus.OK));
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

    @GetMapping(value = "/obtener-config")
    public ResponseEntity<?> obtenerConfig(@Value("${server.port}") String puerto) {
        log.info("Puerto : " + puerto);
        log.info( env.getActiveProfiles()[0]);
        Map<String, String> map = new HashMap<>();

        map.put("ambiente", env.getProperty("configuracion.texto"));
        map.put("autor", env.getProperty("configuracion.autor.nombre"));
        map.put("email", env.getProperty("configuracion.autor.email"));

        return new ResponseEntity<Map<String, String>>(map, HttpStatus.OK);
    }


    private List<AlumnoEntity> metodoAlternativo(Throwable e) {
        log.info("Error desde el metod alternativo : "+e.getMessage());
        AlumnoEntity alumnoAlternativo = new AlumnoEntity();
        alumnoAlternativo.setNombre("CircuitBreakerFactory");
        alumnoAlternativo.setApellido("CircuitB reakerFactory");
        List<AlumnoEntity> listAlternativa = new ArrayList<>();
        listAlternativa.add(alumnoAlternativo);
        return  listAlternativa;
    }

    private CompletableFuture< List<AlumnoEntity>> metodoAlternativo2(Throwable e) {
        log.info("Error desde el metod alternativo : "+e.getMessage());
        AlumnoEntity alumnoAlternativo = new AlumnoEntity();
        alumnoAlternativo.setNombre("CircuitBreakerFactory");
        alumnoAlternativo.setApellido("CircuitBreakerFactory");
        List<AlumnoEntity> listAlternativa = new ArrayList<>();
        listAlternativa.add(alumnoAlternativo);
        return  CompletableFuture.supplyAsync(()-> listAlternativa);
    }


}
