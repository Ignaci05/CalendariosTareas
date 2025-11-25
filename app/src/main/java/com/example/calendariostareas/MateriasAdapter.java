package com.example.calendariostareas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendariostareas.modelos.Materia;

import java.util.List;

public class MateriasAdapter extends RecyclerView.Adapter<MateriasAdapter.ViewHolder> {

    private List<Materia> listaMaterias;
    private OnMateriaClickListener listener; // 1. Variable del listener

    // 2. Interfaz para comunicar el click
    public interface OnMateriaClickListener {
        void onMateriaClick(Materia materia);
    }

    // 3. Actualizamos el constructor para pedir el listener
    public MateriasAdapter(List<Materia> listaMaterias, OnMateriaClickListener listener) {
        this.listaMaterias = listaMaterias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Materia materia = listaMaterias.get(position);
        holder.tvNombre.setText(materia.nombre);
        try {
            holder.viewColor.setBackgroundColor(Color.parseColor(materia.color));
        } catch (Exception e) {
            holder.viewColor.setBackgroundColor(Color.GRAY);
        }

        // 4. Configurar el click de la tarjeta
        holder.itemView.setOnClickListener(v -> {
            listener.onMateriaClick(materia);
        });
    }

    @Override
    public int getItemCount() { return listaMaterias.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        View viewColor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMateria);
            viewColor = itemView.findViewById(R.id.viewColorMateria);
        }
    }
}