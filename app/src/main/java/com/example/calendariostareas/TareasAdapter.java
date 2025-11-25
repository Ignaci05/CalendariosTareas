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

    // Interfaz para comunicar eventos a la Actividad
    public interface OnTareaActionListener {
        void onTareaCheckChanged(Tarea tarea); // Cuando marcan el checkbox
        void onTareaClick(Tarea tarea);        // (Opcional) Para ver detalles o borrar después
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
        // Quitamos el listener temporalmente para evitar bugs al reciclar vistas
        holder.cbCompletada.setOnCheckedChangeListener(null);
        holder.cbCompletada.setChecked(tarea.esCompletada);

        // Efecto visual: Tachado si está completada
        actualizarEstiloTachado(holder.tvDescripcion, tarea.esCompletada);

        // Activamos el listener de nuevo
        holder.cbCompletada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tarea.esCompletada = isChecked; // Actualizamos el objeto local
            actualizarEstiloTachado(holder.tvDescripcion, isChecked);
            listener.onTareaCheckChanged(tarea); // Avisamos a la base de datos
        });
    }

    private void actualizarEstiloTachado(TextView textView, boolean completada) {
        if (completada) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setAlpha(0.5f); // Hacemos el texto un poco transparente
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setAlpha(1.0f); // Texto normal
        }
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

    // Método 1: Para obtener el objeto Tarea según su posición
    public Tarea getTareaAt(int position) {
        return listaTareas.get(position);
    }

    // Método 2: Para eliminarla visualmente de la lista y refrescar
    public void eliminarTarea(int position) {
        listaTareas.remove(position);
        notifyItemRemoved(position); // Esto hace la animación bonita de borrado
    }
}