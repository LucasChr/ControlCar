package com.example.lucas.controlcar.relatorio;

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
 * Created by lucas on 26/08/17.
 */

public class RelatorioListAdapter extends ArrayAdapter<Relatorio>{

    private int layout;
    private Context context;
    private List<Relatorio> relatorioList;

    public RelatorioListAdapter(Context context, int layout, List<Relatorio> relatorioList) {
        super(context, layout, relatorioList);
        this.context = context;
        this.layout = layout;
        this.relatorioList = relatorioList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(layout, null);

        TextView tvData = (TextView) itemView.findViewById(R.id.fragment_relatorio_list_item_tvData);
        TextView tvKmmax = (TextView) itemView.findViewById(R.id.fragment_relatorio_list_item_tvKmmax);
        TextView tvMediakm = (TextView) itemView.findViewById(R.id.fragment_relatorio_list_item_tvMediakm);

        final Relatorio relatorio = relatorioList.get(position);
        tvData.setText(relatorio.getData());
        tvKmmax.setText(relatorio.getKmmax().toString());
        tvMediakm.setText(relatorio.getMediaKm().toString());

        return itemView;
    }
}
