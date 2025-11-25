package com.example.calendariostareas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ocultar la Action Bar si existe para que se vea pantalla completa
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Esperar 2 segundos (2000 milisegundos) y saltar
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Matar el Splash para que no puedas volver atrás con el botón 'Atrás'
        }, 2000);
    }
}