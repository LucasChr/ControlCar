package com.example.lucas.controlcar.usuario;

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
 * Created by lucas on 01/08/17.
 */

public class UsuarioListAdapter extends ArrayAdapter<Usuario> {

    private int layout;
    private Context context;
    private List<Usuario> usuarioList;

    public UsuarioListAdapter(Context context, int layout, List<Usuario> usuarioList) {
        super(context, layout, usuarioList);
        this.context = context;
        this.layout = layout;
        this.usuarioList = usuarioList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(layout, null);

        TextView tvNome = (TextView) itemView.findViewById(R.id.fragment_usuario_list_item_tvNome);
        TextView tvTelefone = (TextView) itemView.findViewById(R.id.fragment_usuario_list_item_tvTelefone);
        TextView tvEmail = (TextView) itemView.findViewById(R.id.fragment_usuario_list_item_tvEmail);
        //Remover depois
        TextView tvUser = (TextView) itemView.findViewById(R.id.fragment_usuario_list_item_tvUsuario);
        TextView tvSenha = (TextView) itemView.findViewById(R.id.fragment_usuario_list_item_tvSenha);
        ImageView imgUsuario = (ImageView) itemView.findViewById(R.id.fragment_usuario_list_item_imgUsuario);

        final Usuario usuario = usuarioList.get(position);
        tvNome.setText(usuario.getNome());
        tvTelefone.setText(usuario.getTelefone().toString());
        tvEmail.setText(usuario.getEmail());
        //Remover depois
        tvUser.setText(usuario.getUsuario());
        tvSenha.setText(usuario.getSenha());

        if (usuario.getFoto() != null) {
            byte[] bytearray = Base64.decode(usuario.getFoto(), Base64.DEFAULT);
            Bitmap bmimage = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
            imgUsuario.setImageBitmap(bmimage);
        }

        return itemView;
    }

}
