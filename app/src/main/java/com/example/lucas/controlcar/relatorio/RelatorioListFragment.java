package com.example.lucas.controlcar.relatorio;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;

import com.example.lucas.controlcar.R;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RelatorioListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<Relatorio> relatorios;
    private RelatorioDAO relatorioDAO;
    private RelatorioListAdapter adapter;

    private TableLayout tl;
    private String TAG = getActivity().getLocalClassName();
    private boolean isServiceBound;
    private boolean preRequisites = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relatorio_list, container, false);

        tl = (TableLayout) view.findViewById(R.id.fragment_relatorio_data_table);
//         scrollView.setEmptyView(view.findViewById(android.R.id.empty));
//        relatorioDAO = new RelatorioDAO(getActivity());
//        relatorios = relatorioDAO.listar();
//
//        adapter = new RelatorioListAdapter(getActivity(),
//                R.layout.fragment_relatorio_list_item, relatorios);
//
//        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


}
