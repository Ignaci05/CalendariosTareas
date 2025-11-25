package com.example.calendariostareas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendariostareas.database.AppDatabase;
import com.example.calendariostareas.modelos.Tarea;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class DetalleMateriaActivity extends AppCompatActivity {

    private int idMateria;
    private String nombreMateria;
    private RecyclerView rvTareas;
    private TextView tvTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_materia);

        idMateria = getIntent().getIntExtra("ID_MATERIA", -1);
        nombreMateria = getIntent().getStringExtra("NOMBRE_MATERIA");

        tvTitulo = findViewById(R.id.tvTituloMateria);
        rvTareas = findViewById(R.id.rvTareas);
        FloatingActionButton fab = findViewById(R.id.fabAgregarTarea);

        tvTitulo.setText(nombreMateria != null ? nombreMateria : "Tareas");
        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        configurarSwipe();

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgregarTareaActivity.class);
            // AGREGAMOS ESTA LÍNEA:
            intent.putExtra("ID_MATERIA_PRESELECCIONADA", idMateria);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTareas();
    }

    private void cargarTareas() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Tarea> tareas = AppDatabase.getInstance(this)
                    .tareaDao()
                    .getTareasPorMateria(idMateria);

            runOnUiThread(() -> {
                // Implementamos la interfaz del Adapter aquí mismo
                TareasAdapter adapter = new TareasAdapter(tareas, new TareasAdapter.OnTareaActionListener() {
                    @Override
                    public void onTareaCheckChanged(Tarea tarea) {
                        actualizarEstadoTarea(tarea); // Llamamos al método que guarda en BD
                    }

                    @Override
                    public void onTareaClick(Tarea tarea) {
                        // Aquí pondremos el código para ELIMINAR más adelante
                        Toast.makeText(DetalleMateriaActivity.this, "Mantén presionado para borrar", Toast.LENGTH_SHORT).show();
                    }
                });
                rvTareas.setAdapter(adapter);
            });
        });
    }

    // Método para guardar el cambio de checkbox en la BD
    private void actualizarEstadoTarea(Tarea tarea) {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).tareaDao().update(tarea);
        });
    }
    // Método para configurar el deslizamiento
    private void configurarSwipe() {
        // 1. Definimos la acción: Arriba/Abajo (0, no queremos mover) | Izquierda/Derecha (Borrar)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No vamos a implementar mover de lugar (drag & drop)
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // AQUÍ OCURRE EL BORRADO

                // 1. Obtener la posición del item deslizado
                int position = viewHolder.getAdapterPosition();

                // 2. Obtener el adaptador y la tarea
                TareasAdapter adapter = (TareasAdapter) rvTareas.getAdapter();
                Tarea tareaABorrar = adapter.getTareaAt(position);

                // 3. Borrar de la Base de Datos (Segundo plano)
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase.getInstance(DetalleMateriaActivity.this).tareaDao().delete(tareaABorrar);

                    // 4. Mostrar mensaje (Hilo principal)
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleMateriaActivity.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                    });
                });

                // 5. Borrar visualmente de la lista (Inmediatamente para que se vea rápido)
                adapter.eliminarTarea(position);
            }
        }).attachToRecyclerView(rvTareas); // ¡Importante! Aquí conectamos el helper al RecyclerView
    }
}