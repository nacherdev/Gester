package com.example.gester.ui.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gester.R;
import com.example.gester.dao.models.Notificacion;

import java.util.ArrayList;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.ViewHolder> {

    private final ArrayList<Notificacion> lista;

    public NotificacionesAdapter(ArrayList<Notificacion> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacion n = lista.get(position);
        holder.tvTitulo.setText(n.getTitulo());
        holder.tvMensaje.setText(n.getMensaje());
        holder.tvFecha.setText(n.getFechaEnvio());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public ArrayList<Notificacion> getLista() {
        return lista;
    }

    public void eliminarItem(int posicion) {
        if (posicion >= 0 && posicion < lista.size()) {
            lista.remove(posicion);
            notifyItemRemoved(posicion);
        }
    }

    public void eliminarTodo() {
        lista.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvMensaje, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvItemTitulo);
            tvMensaje = itemView.findViewById(R.id.tvItemMensaje);
            tvFecha = itemView.findViewById(R.id.tvItemFecha);
        }
    }
}