package com.example.calendariostareas.modelos;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

// AQUÍ CONECTAMOS LAS TABLAS
@Entity(tableName = "tareas",
        foreignKeys = @ForeignKey(
                entity = Materia.class,       // Tabla papá
                parentColumns = "idMateria",  // Columna del papá
                childColumns = "idMateria",   // Columna en esta tabla (hija)
                onDelete = ForeignKey.CASCADE // Si borras la materia, se borran sus tareas
        ))
public class Tarea {

    @PrimaryKey(autoGenerate = true)
    public int idTarea;

    @ColumnInfo(name = "idMateria", index = true)
    // 'index = true' hace que las búsquedas sean rápidas
    public int idMateria;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    @ColumnInfo(name = "fechaEntrega")
    public long fechaEntrega; // Guardamos fecha en milisegundos (timestamp)

    @ColumnInfo(name = "tipo")
    public String tipo; // "Tarea", "Examen", "Proyecto"

    @ColumnInfo(name = "esCompletada")
    public boolean esCompletada;

    // Constructor vacío (Obligatorio)
    public Tarea() {
    }

    // Constructor para crear nueva tarea
    @Ignore
    public Tarea(int idMateria, String descripcion, long fechaEntrega, String tipo) {
        this.idMateria = idMateria;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.tipo = tipo;
        this.esCompletada = false; // Por defecto nace "no completada"
    }
}
