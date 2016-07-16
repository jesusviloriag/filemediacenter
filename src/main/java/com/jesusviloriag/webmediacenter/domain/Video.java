package com.jesusviloriag.webmediacenter.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Video.
 */
@Entity
@Table(name = "video")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;

    @Lob
    @Column(name = "archivo", nullable = false)
    private byte[] archivo;

    @Column(name = "archivo_content_type", nullable = false)
    private String archivoContentType;

    @NotNull
    @Column(name = "direccion_en_servidor", nullable = false)
    private String direccionEnServidor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public String getArchivoContentType() {
        return archivoContentType;
    }

    public void setArchivoContentType(String archivoContentType) {
        this.archivoContentType = archivoContentType;
    }

    public String getDireccionEnServidor() {
        return direccionEnServidor;
    }

    public void setDireccionEnServidor(String direccionEnServidor) {
        this.direccionEnServidor = direccionEnServidor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Video video = (Video) o;
        if(video.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, video.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Video{" +
            "id=" + id +
            ", titulo='" + titulo + "'" +
            ", ano='" + ano + "'" +
            ", archivo='" + archivo + "'" +
            ", archivoContentType='" + archivoContentType + "'" +
            ", direccionEnServidor='" + direccionEnServidor + "'" +
            '}';
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
}
