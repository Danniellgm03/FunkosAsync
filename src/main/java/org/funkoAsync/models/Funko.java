package org.funkoAsync.models;

import org.funkoAsync.enums.Modelo;
import org.funkoAsync.locale.MyLocale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record Funko(UUID COD, Long myId, String nombre, Modelo modelo, double precio, LocalDate fecha, LocalDateTime created_at, LocalDateTime updated_at) {
    @Override
    public String toString() {
        return "Funko{" +
                "COD=" + COD +
                ", myId=" + myId +
                ", nombre='" + nombre + '\'' +
                ", modelo=" + modelo +
                ", precio=" + precio +
                ", fecha=" + fecha +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
