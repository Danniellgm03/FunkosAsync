package org.funkoAsync.models;

import org.funkoAsync.enums.Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record Funko(Integer id, UUID COD, String nombre, Modelo modelo, double precio, LocalDate fecha, LocalDateTime created_at, LocalDateTime updated_at) {
    
}
