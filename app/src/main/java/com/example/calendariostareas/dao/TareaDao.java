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

    // Crear una tarea nueva
    @Insert
    void insert(Tarea tarea);

    // ACTUALIZAR UNA TAREA EXISTENTE (Esto es lo que usaremos al editar)
    // Room busca la tarea por su 'idTarea' y actualiza el resto de campos.
    @Update
    void update(Tarea tarea);

    // Borrar tarea
    @Delete
    void delete(Tarea tarea);

    // Obtener todas para la lista general
    @Query("SELECT * FROM tareas ORDER BY fechaEntrega ASC")
    List<Tarea> getAllTareas();

    // Obtener tareas de una materia específica
    @Query("SELECT * FROM tareas WHERE idMateria = :materiaId")
    List<Tarea> getTareasPorMateria(int materiaId);

    // Obtener tareas por rango de fecha (Para el calendario)
    @Query("SELECT * FROM tareas WHERE fechaEntrega >= :desde AND fechaEntrega <= :hasta")
    List<Tarea> getTareasPorFecha(long desde, long hasta);
}