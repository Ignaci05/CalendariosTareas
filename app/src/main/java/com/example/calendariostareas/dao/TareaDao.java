package com.example.calendariostareas.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.calendariostareas.modelos.Tarea;

import java.util.List;

@Dao
public interface TareaDao {

    // Guardar tarea
    @Insert
    void insert(Tarea tarea);

    // Obtener TODAS las tareas
    @Query("SELECT * FROM tareas ORDER BY fechaEntrega ASC")
    List<Tarea> getAll();

    // Obtener tareas de UNA sola materia (Filtrado)
    @Query("SELECT * FROM tareas WHERE idMateria = :materiaId")
    List<Tarea> getTareasPorMateria(int materiaId);

    // Actualizar (Sirve para marcar como completada)
    @Update
    void update(Tarea tarea);

    // Borrar tarea
    @Delete
    void delete(Tarea tarea);
}
