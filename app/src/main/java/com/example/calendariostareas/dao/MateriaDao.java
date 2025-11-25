package com.example.calendariostareas.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.calendariostareas.modelos.Materia;

import java.util.List;

@Dao
public interface MateriaDao {

    // Insertar una nueva materia
    @Insert
    void insert(Materia materia);

    // Obtener todas las materias (para mostrarlas en la lista)
    @Query("SELECT * FROM materias")
    List<Materia> getAll();

    // Borrar una materia
    @Delete
    void delete(Materia materia);
}
