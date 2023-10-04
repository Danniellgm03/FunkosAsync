package org.funkoAsync.models;

import org.funkoAsync.enums.Modelo;
import org.funkoAsync.locale.MyLocale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record Funko(Integer id, UUID COD, String nombre, Modelo modelo, double precio, LocalDate fecha, LocalDateTime created_at, LocalDateTime updated_at) {
    @Override
    public String toString() {
        return "Funko{" +
                "id=" + id +
                ", COD=" + COD +
                ", nombre='" + nombre + '\'' +
                ", modelo=" + modelo +
                ", precio=" + MyLocale.toLocalMoney(precio) +
                ", fecha=" + MyLocale.toLocalDate(fecha) +
                ", created_at=" + MyLocale.toLocalDateTime(created_at) +
                ", updated_at=" + MyLocale.toLocalDateTime(updated_at) +
                '}';
    }
}
