package com.example.gester.ui.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gester.R;
import com.example.gester.controller.Controller;
import com.example.gester.dao.models.Notificacion;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotificationsFragment extends Fragment {

    private RecyclerView rvNotificaciones;
    private TextView tvEstadoCargando;
    private NotificacionesAdapter adapter;
    private Controller controller;
    private ArrayList<Notificacion> listaNotificaciones;
    private Button btn_eliminar_todo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        rvNotificaciones = root.findViewById(R.id.rvNotificaciones);
        tvEstadoCargando = root.findViewById(R.id.tvEstadoCargando);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        btn_eliminar_todo = root.findViewById(R.id.btn_eliminar_todo);

        controller = Controller.getInstancia();
        listaNotificaciones = new ArrayList<>();

        configurarGestoDeslizar();
        cargarBandejaNotificaciones();

        btn_eliminar_todo.setOnClickListener(v -> {
            executor.execute(() -> {
                boolean exito = controller.eliminarTodasLasNotificacion();
                handler.post(() -> {
                    if (exito) {
                        adapter.eliminarTodo();
                        Toast.makeText(getContext(), "Notificaciones borradas", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al borrar las notificaciones", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        return root;
    }

    private void cargarBandejaNotificaciones() {
        if (tvEstadoCargando != null) {
            tvEstadoCargando.setVisibility(View.VISIBLE);
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            controller.verificarCitasProximas();
            ArrayList<Notificacion> alertas = controller.obtenerNotificaciones();

            handler.post(() -> {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                if (tvEstadoCargando != null) {
                    tvEstadoCargando.setVisibility(View.GONE);
                    if (listaNotificaciones.isEmpty()) {
                        tvEstadoCargando.setText("No hay notificaciones...");
                        tvEstadoCargando.setVisibility(View.VISIBLE);
                    }
                }
                if (alertas != null) {
                    listaNotificaciones.clear();
                    listaNotificaciones.addAll(alertas);
                    adapter = new NotificacionesAdapter(listaNotificaciones);
                    rvNotificaciones.setAdapter(adapter);
                }

            });
        });
    }

    private void configurarGestoDeslizar() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int posicion = viewHolder.getBindingAdapterPosition();
                Notificacion notificacionAEliminar = adapter.getLista().get(posicion);

                adapter.eliminarItem(posicion);

                Executor executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                executor.execute(() -> {
                    boolean eliminadoDb = controller.eliminarNotificacion(notificacionAEliminar.getId());

                    handler.post(() -> {
                        if (!isAdded() || getContext() == null) {
                            return;
                        }
                        if (eliminadoDb) {
                            Toast.makeText(getContext(), "Notificación borrada", Toast.LENGTH_SHORT).show();
                        } else {
                            listaNotificaciones.add(posicion, notificacionAEliminar);
                            adapter.notifyItemInserted(posicion);
                            Toast.makeText(getContext(), "Error de red: No se pudo borrar en el servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }

            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
                    android.graphics.Paint p = new android.graphics.Paint();
                    p.setColor(android.graphics.Color.parseColor("#E11D48"));

                    float radius = 14 * itemView.getContext().getResources().getDisplayMetrics().density;

                    c.drawRoundRect((float) itemView.getLeft(), (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), radius, radius, p);

                    android.graphics.drawable.Drawable icon = androidx.core.content.ContextCompat.getDrawable(getContext(), R.drawable.ic_delete);
                    if (icon != null) {
                        int itemHeight = itemView.getBottom() - itemView.getTop();
                        int intrinsicWidth = icon.getIntrinsicWidth();
                        int intrinsicHeight = icon.getIntrinsicHeight();

                        int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                        int iconMargin = (itemHeight - intrinsicHeight) / 2;
                        int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconBottom = iconTop + intrinsicHeight;

                        if (dX < -(iconMargin * 2 + intrinsicWidth)) {
                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            icon.draw(c);
                        }
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvNotificaciones);
    }
}