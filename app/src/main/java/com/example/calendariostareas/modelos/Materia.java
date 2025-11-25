package com.example.calendariostareas.modelos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "materias") // Esto le dice a Android: "Esta clase es una Tabla SQL"
public class Materia {

    @PrimaryKey(autoGenerate = true) // Clave única que se suma sola (1, 2, 3...)
    public int idMateria;

    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "color")
    public String color; // Guardaremos el Hexadecimal (ej: "#FF0000")

    // Constructor vacío (Obligatorio para Room)
    public Materia() {
    }

    // Constructor para cuando nosotros creamos una materia nueva
    public Materia(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
    }

    // Getters y Setters (Opcional si usas variables publicas, pero buena practica tenerlos)
    public String getNombre() { return nombre; }
    public String getColor() { return color; }
}
