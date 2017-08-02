package com.example.lucas.controlcar.carro;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lucas.controlcar.R;

import java.util.List;

public class CarrosListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private List<Carro> carros;
    private CarroListAdapter adapter;
    private CarroDAO carroDAO;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carros_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.fragment_carro_list_listview);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        listView.setOnItemClickListener(this);

        carroDAO = new CarroDAO(getActivity());
        List<Carro> carroList = carroDAO.listar();

        adapter = new CarroListAdapter(getActivity(), R.layout.fragment_carro_list_item, carroList);

        listView.setAdapter(adapter);

        CarroDAO carroDAO = new CarroDAO(getActivity());
        carros = carroDAO.listar();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Carro carro = carros.get(position);

        Intent it = new Intent(getActivity(), CarrosListFragment.class);
        String id1 = String.valueOf(carro.getId());
        it.putExtra(Carro.ID, id1);
        startActivityForResult(it, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        atualizaLista();
        if (resultCode == 1) {
            Toast.makeText(getActivity(), "Salvo com sucesso", Toast.LENGTH_LONG).show();
        } else if (resultCode == 2) {
            Toast.makeText(getActivity(), "Modificado com sucesso", Toast.LENGTH_LONG).show();
        } else if (resultCode == 3) {
            Toast.makeText(getActivity(), "Excluído com sucesso", Toast.LENGTH_LONG).show();
        }
    }

    public void atualizaLista() {
        List<Carro> cs = carroDAO.listar();
        adapter.clear();
        adapter.addAll(cs);
        adapter.notifyDataSetChanged();
    }
}
