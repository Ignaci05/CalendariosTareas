package com.example.calendariostareas.database;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.calendariostareas.dao.MateriaDao;
import com.example.calendariostareas.dao.TareaDao;
import com.example.calendariostareas.modelos.Materia;
import com.example.calendariostareas.modelos.Tarea;

// 1. Definimos las Tablas y la Versión
@Database(entities = {Materia.class, Tarea.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // 2. Exponemos los DAOs (Para que el resto de la app los use)
    public abstract MateriaDao materiaDao();
    public abstract TareaDao tareaDao();

    // 3. Patrón Singleton (Para tener una única instancia de la BD)
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "agenda_escolar_db") // Nombre del archivo .db
                            .fallbackToDestructiveMigration() // Si cambias la BD, borra todo y empieza de cero (útil en desarrollo)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
