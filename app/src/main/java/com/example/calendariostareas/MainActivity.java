package com.example.calendariostareas;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendariostareas.database.AppDatabase;
import com.example.calendariostareas.modelos.Materia;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMaterias;
    private FloatingActionButton fabAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Vincular las vistas del XML
        rvMaterias = findViewById(R.id.rvMaterias);
        fabAgregar = findViewById(R.id.fabAgregar);

        // 2. Configurar el RecyclerView para que sea una lista vertical
        rvMaterias.setLayoutManager(new LinearLayoutManager(this));

        // 3. Configurar el botón (+) para ir a AGREGAR MATERIA
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarMateriaActivity.class);
            startActivity(intent);
        });

        // La carga inicial de datos se hace en onResume para cubrir todos los casos
    }

    // Este método se ejecuta cada vez que la pantalla se muestra (al iniciar y al volver)
    @Override
    protected void onResume() {
        super.onResume();
        cargarMaterias();
    }

    private void cargarMaterias() {
        // Usamos un hilo secundario para no congelar la pantalla mientras consultamos la BD
        Executors.newSingleThreadExecutor().execute(() -> {

            // A. Consultar la base de datos
            List<Materia> lista = AppDatabase.getInstance(this).materiaDao().getAll();

            // B. Volver al hilo principal para mostrar los datos en la pantalla
            runOnUiThread(() -> {
                // Creamos el adaptador pasándole la lista y el COMPORTAMIENTO DEL CLIC
                MateriasAdapter adapter = new MateriasAdapter(lista, materia -> {

                    // Lógica al hacer clic en una materia:
                    // Abrir la pantalla de detalles (lista de tareas)
                    Intent intent = new Intent(MainActivity.this, DetalleMateriaActivity.class);

                    // Pasamos el ID y el Nombre para usarlos en la otra pantalla
                    intent.putExtra("ID_MATERIA", materia.idMateria);
                    intent.putExtra("NOMBRE_MATERIA", materia.nombre);

                    startActivity(intent);
                });

                // Asignamos el adaptador al RecyclerView
                rvMaterias.setAdapter(adapter);
            });
        });
    }
}