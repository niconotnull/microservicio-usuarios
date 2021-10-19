package com.springboot.microservicios.usuarios.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.Date;

@Data
@Entity
@Table(name = "alumnos")
public class AlumnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private String nombre;

    @NotEmpty
    private String apellido;

    @NotEmpty
    @Email
    private String email;

    @Column(name = "create_at")
    private Date createAt;


    @Lob
    @JsonIgnore
    private byte[] foto;

    @Column(name= "url_Foto")
    private String urlFoto;

    @PrePersist
    public void prePersist(){
        this.createAt = new Date();
    }

    public Integer getFotoHashCode(){
        return (this.foto != null) ? Arrays.hashCode(this.foto) : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AlumnoEntity)) {
            return false;
        }

        AlumnoEntity a = (AlumnoEntity) obj;

        return this.id != null && this.id.equals(a.getId());
    }
}
