# Se define el nombre del microservicio, este será el identificador del microservicio
spring.application.name=microservicio-usuarios

# Se asigna un puerto de manera automática de forma aleatoria
#  Spring Boot asigna un número de puerto disponible aleatorio a cada instancia que ejecutamos
server.port=${PORT:0}

# Se configura la instancia id del microservicio en Eureka
# Se asigna un ID de instancia único, dinámicamente, a cada una de las instancias para que las use Eureka.
eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}

# Se configura la ruta de Eureka donde el microservicio registrará la información de su ubicación para
# para que séa ubicado por otros microservicios a través de una señal de latido, en el servidor de nombre de Eureka
# cada 30 s  el microservicio manda una señal a Eureka para indicar que esta vigente
# Cuando la instancia de un microservicio se baja, pasan tres periodos cada uno de 30 segundos en total 90 segundos
# en que no se envía la señal Eureka lo elimina del registro.
# Cuando el microservicio se vuelva a levantar pasarán 90 segundos para que Eureka lo vuelva a registrar (tres latidos)
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

spring.datasource.url=jdbc:mysql://localhost/bd_examenes?useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL57Dialect
spring.jpa.hibernate.ddl=true
logging.level.org.hibernate.SQL=debug
#spring.jpa.hibernate.ddl-auto=create-drop
