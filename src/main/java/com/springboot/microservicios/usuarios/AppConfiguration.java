package com.springboot.microservicios.usuarios;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AppConfiguration {


    /**
     * Se realiza la configuración de la resilencia  modificando los valores por
     * default a través Resilience4JCircuitBreakerFactory, es posible configurar
     * para cada tipo de corto circuito
     *
     * NOTA: esta configuración se puede realizar a través del aplication.properties
     * o un archivo .yml, si se realiza en ambos tendrá mayor prioridad la configuración
     * establecida en el application.yml o en el properties que la clase AppConfiguration
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer(){
        return factory-> factory.configureDefault(id->{
            return new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(CircuitBreakerConfig.custom()
                    .slidingWindowSize(10)   //Se configura el tamaño de la ventana
                    .failureRateThreshold(50) // El porcentaje del umbral de errores
                    .waitDurationInOpenState(Duration.ofSeconds(10L)) // Tiempo de curacion que estara en estado abierto el corto circuito
                    .permittedNumberOfCallsInHalfOpenState(5)  // Determinar el numero de llamadas en el estado semi-abierto por defecto son 10  aqui se maneja en 5
                    .slowCallRateThreshold(50) // Se determina el porcentaje del umbral de las llamadas lentas
                    .slowCallDurationThreshold(Duration.ofSeconds(2L)) // Se configura el tiempo de duración es decir el tiempo maximo que debiera demorar una llamada en particular , el timeOut deberá ser mayor para procesar esta configuración
                    .build())
//                .timeLimiterConfig(TimeLimiterConfig.ofDefaults()) // Determinar el tiempo de espera de un TimeOut en Default que es igual aun segundo
//                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2L)).build()) // Determinar el tiempo de espera de un TimeOut de forma configurable
                    .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(6L)).build()) // Se subio a 6 segundo para que no mande el timeOut(depende del endppoin)
                 .build();
        });
    }
}
