package com.example.lucas.controlcar.relatorio;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lucas.controlcar.R;

import java.util.List;

public class RelatorioListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<Relatorio> relatorios;
    private RelatorioDAO relatorioDAO;
    private RelatorioListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relatorio_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.fragment_relatorio_list_listview);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        listView.setOnItemClickListener(this);

        relatorioDAO = new RelatorioDAO(getActivity());
        relatorios = relatorioDAO.listar();

        adapter = new RelatorioListAdapter(getActivity(),
                R.layout.fragment_relatorio_list_item, relatorios);

        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

}
