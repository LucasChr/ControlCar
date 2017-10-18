package com.example.lucas.controlcar.usuario;

import android.content.Intent;
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

public class UsuarioListFragment extends Fragment implements AdapterView.OnItemClickListener {


    private List<Usuario> usuarios;
    private UsuarioListAdapter adapter;
    private UsuarioDAO usuarioDAO;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuario_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.fragment_usuario_list_listview);
        listView.setEmptyView(view.findViewById(android.R.id.empty));
        listView.setOnItemClickListener(this);

        usuarioDAO = new UsuarioDAO(getActivity());
        usuarios = usuarioDAO.listar();

        adapter = new UsuarioListAdapter(getActivity(), R.layout.fragment_usuario_list_item, usuarios);

        listView.setAdapter(adapter);

        UsuarioDAO usuarioDAO = new UsuarioDAO(getActivity());
        usuarios = usuarioDAO.listar();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Usuario usuario = usuarios.get(position);

        Intent it = new Intent(getActivity(), UsuarioListFragment.class);
        String id1 = String.valueOf(usuario.getId());
        it.putExtra(Usuario.ID, id1);
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
        List<Usuario> cs = usuarioDAO.listar();
        adapter.clear();
        adapter.addAll(cs);
        adapter.notifyDataSetChanged();
    }

}
