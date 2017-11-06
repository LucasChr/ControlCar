package com.example.lucas.controlcar.carro;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucas.controlcar.R;

/**
 * Created by lucas on 05/11/17.
 */

public class LineHolder extends RecyclerView.ViewHolder {
    public TextView tvNome, tvMontadora, tvPlaca, tvAno;
    public ImageView imgCarro;

    public LineHolder(View itemView) {
        super(itemView);

        tvNome = (TextView) itemView.findViewById(R.id.fragment_carro_recyclelist_item_tvNome);
        tvMontadora = (TextView) itemView.findViewById(R.id.fragment_carro_recyclelist_item_tvMontadora);
        tvPlaca = (TextView) itemView.findViewById(R.id.fragment_carro_recyclelist_item_tvPlaca);
        tvAno = (TextView) itemView.findViewById(R.id.fragment_carro_recyclelist_item_tvAno);
        imgCarro = (ImageView) itemView.findViewById(R.id.fragment_carro_recyclelist_item_imgCarro);
    }
}
