package com.example.calendariostareas;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendariostareas.modelos.Tarea;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.ViewHolder> {

    private List<Tarea> listaTareas;
    private OnTareaActionListener listener;

    // Interfaz para comunicar eventos (Clic y Checkbox)
    public interface OnTareaActionListener {
        void onTareaCheckChanged(Tarea tarea); // Para tachar/destachar
        void onTareaClick(Tarea tarea);        // Para EDITAR
    }

    public TareasAdapter(List<Tarea> listaTareas, OnTareaActionListener listener) {
        this.listaTareas = listaTareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarea tarea = listaTareas.get(position);

        holder.tvDescripcion.setText(tarea.descripcion);
        holder.tvTipo.setText(tarea.tipo);

        // Formatear fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvFecha.setText(sdf.format(new Date(tarea.fechaEntrega)));

        // --- ESTADO DEL CHECKBOX ---
        holder.cbCompletada.setOnCheckedChangeListener(null); // Evitar bugs al reciclar
        holder.cbCompletada.setChecked(tarea.esCompletada);
        actualizarEstiloTachado(holder.tvDescripcion, tarea.esCompletada);

        // 1. LISTENER DEL CHECKBOX (Marcar como hecha)
        holder.cbCompletada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tarea.esCompletada = isChecked;
            actualizarEstiloTachado(holder.tvDescripcion, isChecked);
            listener.onTareaCheckChanged(tarea);
        });

        // 2. LISTENER DE TODA LA TARJETA (Para Editar) <--- ¡ESTO ES LO QUE FALTABA!
        holder.itemView.setOnClickListener(v -> {
            listener.onTareaClick(tarea);
        });
    }

    private void actualizarEstiloTachado(TextView textView, boolean completada) {
        if (completada) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setAlpha(0.5f);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setAlpha(1.0f);
        }
    }

    // Métodos auxiliares para Swipe
    public Tarea getTareaAt(int position) {
        return listaTareas.get(position);
    }

    public void eliminarTarea(int position) {
        listaTareas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() { return listaTareas.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcion, tvFecha, tvTipo;
        CheckBox cbCompletada;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionTarea);
            tvFecha = itemView.findViewById(R.id.tvFechaTarea);
            tvTipo = itemView.findViewById(R.id.tvTipoTarea);
            cbCompletada = itemView.findViewById(R.id.cbCompletada);
        }
    }
}