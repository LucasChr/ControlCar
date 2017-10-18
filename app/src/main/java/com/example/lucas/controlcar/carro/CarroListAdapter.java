package com.example.lucas.controlcar.carro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucas.controlcar.R;

import java.util.List;

/**
 * Created by lucas on 31/07/17.
 */

public class CarroListAdapter extends ArrayAdapter<Carro>{

    private int layout;
    private Context context;
    private List<Carro> carroList;

    public CarroListAdapter(Context context, int layout, List<Carro> carroList) {
        super(context, layout, carroList);
        this.context = context;
        this.layout = layout;
        this.carroList = carroList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(layout, null);

        TextView tvNome = (TextView) itemView.findViewById(R.id.fragment_carro_list_item_tvNome);
        TextView tvMontadora = (TextView) itemView.findViewById(R.id.fragment_carro_list_item_tvMontadora);
        TextView tvPlaca = (TextView) itemView.findViewById(R.id.fragment_carro_list_item_tvPlaca);
        TextView tvAno = (TextView) itemView.findViewById(R.id.fragment_carro_list_item_tvAno);
        ImageView imgCarro = (ImageView) itemView.findViewById(R.id.fragment_carro_list_item_imgCarro);

        final Carro carro = carroList.get(position);
        tvNome.setText(carro.getNome());
        tvMontadora.setText(carro.getMontadora());
        tvPlaca.setText(carro.getPlaca());
        tvAno.setText(carro.getAno().toString());


        if (carro.getFoto() != null) {
            byte[] bytearray = Base64.decode(carro.getFoto(), Base64.DEFAULT);
            Bitmap bmimage = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
            imgCarro.setImageBitmap(bmimage);
        }

        return itemView;
    }

}
