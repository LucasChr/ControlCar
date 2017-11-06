package com.example.lucas.controlcar.carro;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.lucas.controlcar.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by lucas on 05/11/17.
 */

public class LineAdapter extends RecyclerView.Adapter<LineHolder> {

    private final List<Carro> carros;

    public LineAdapter(ArrayList carros) {
        this.carros = carros;
    }

    @Override
    public LineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_carros_recyclelist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolder holder, int position) {
        holder.tvNome.setText(String.format(Locale.getDefault(), "%s, %d - %s",
                carros.get(position).getNome(),
                carros.get(position).getAno(),
                carros.get(position).getMontadora(),
                carros.get(position).getPlaca()
        ));

        //holder.moreButton.setOnClickListener(view -> updateItem(position));
        //holder.deleteButton.setOnClickListener(view -> removerItem(position));
    }

    @Override
    public int getItemCount() {
        return carros != null ? carros.size() : 0;
    }

}
