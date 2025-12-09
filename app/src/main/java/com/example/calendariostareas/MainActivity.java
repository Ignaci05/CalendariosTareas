package com.example.calendariostareas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.calendariostareas.fragments.CalendarioFragment;
import com.example.calendariostareas.fragments.MateriasFragment;
import com.example.calendariostareas.fragments.TodasTareasFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView tvTitulo;
    private FloatingActionButton fabAgregar;
    private NavigationView navigationView; // Referencia global para actualizar el header

    // Instancias de los Fragments (para no recrearlos y perder estado)
    private final MateriasFragment materiasFragment = new MateriasFragment();
    private final TodasTareasFragment todasTareasFragment = new TodasTareasFragment();
    private final CalendarioFragment calendarioFragment = new CalendarioFragment();

    // Variable para controlar qué hace el botón (+) según la sección
    private int seccionActual = R.id.nav_materias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. VINCULAR VISTAS ---
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        ImageView btnMenu = findViewById(R.id.btnMenuHamburguesa);
        fabAgregar = findViewById(R.id.fabAgregar);
        tvTitulo = findViewById(R.id.tvTituloPagina);

        // --- 2. CONFIGURACIÓN INICIAL ---
        // Cargar Materias por defecto
        cargarFragmento(materiasFragment);
        tvTitulo.setText("Mis Materias");

        // --- 3. MENÚ LATERAL (DRAWER) ---

        // Botón Hamburguesa (Arriba a la izquierda)
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Clics en las opciones del Menú Lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_perfil) {
                // Abrir la pantalla de Perfil
                Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_ajustes) {
                // Abrir la pantalla de Ajustes
                Intent intent = new Intent(MainActivity.this, AjustesActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_acerca) {
                Toast.makeText(this, "Agenda Escolar v1.0", Toast.LENGTH_SHORT).show();
            }

            // Cerrar el menú después de seleccionar
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // --- 4. MENÚ INFERIOR (BOTTOM NAV) ---
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            seccionActual = id; // Actualizamos dónde estamos

            if (id == R.id.nav_materias) {
                tvTitulo.setText("Mis Materias");
                fabAgregar.show();
                cargarFragmento(materiasFragment);
                return true;

            } else if (id == R.id.nav_tareas) {
                tvTitulo.setText("Todas las Tareas");
                fabAgregar.show();
                cargarFragmento(todasTareasFragment);
                return true;

            } else if (id == R.id.nav_calendario) {
                tvTitulo.setText("Calendario");
                fabAgregar.hide(); // Ocultamos el (+) en calendario para limpiar la vista
                cargarFragmento(calendarioFragment);
                return true;
            }
            return false;
        });

        // --- 5. BOTÓN FLOTANTE (+) INTELIGENTE ---
        fabAgregar.setOnClickListener(v -> {
            if (seccionActual == R.id.nav_materias) {
                // Si estoy viendo materias, quiero agregar MATERIA
                startActivity(new Intent(MainActivity.this, AgregarMateriaActivity.class));
            } else {
                // Si estoy viendo tareas, quiero agregar TAREA
                startActivity(new Intent(MainActivity.this, AgregarTareaActivity.class));
            }
        });
    }

    // --- CICLO DE VIDA ---

    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que la pantalla se vuelve visible (ej. al volver de Perfil),
        // actualizamos los datos del header del menú lateral.
        actualizarHeader();
    }

    // --- MÉTODOS AUXILIARES ---

    private void actualizarHeader() {
        View headerView = navigationView.getHeaderView(0);

        TextView tvNombre = headerView.findViewById(R.id.tvNombreHeader);
        TextView tvCarrera = headerView.findViewById(R.id.tvCarreraHeader);
        ImageView ivHeader = headerView.findViewById(R.id.ivFotoHeader); // ¡NUEVO!

        SharedPreferences prefs = getSharedPreferences("MisDatos", MODE_PRIVATE);

        String nombre = prefs.getString("nombre", "Estudiante");
        String carrera = prefs.getString("carrera", "Bienvenido");
        String rutaFoto = prefs.getString("ruta_foto", ""); // Leemos la ruta

        if (tvNombre != null) tvNombre.setText(nombre);
        if (tvCarrera != null) tvCarrera.setText(carrera);

        // CARGAR FOTO
        if (ivHeader != null && !rutaFoto.isEmpty()) {
            File imgFile = new File(rutaFoto);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivHeader.setImageBitmap(myBitmap);
            }
        }
    }

    private void cargarFragmento(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        // Reemplazamos el contenido del contenedor por el nuevo fragmento
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }
}