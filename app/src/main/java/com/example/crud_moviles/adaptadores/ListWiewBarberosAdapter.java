package com.example.crud_moviles.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.crud_moviles.R;
import com.example.crud_moviles.models.Barbero;

import java.util.ArrayList;

public class ListWiewBarberosAdapter extends BaseAdapter {
    Context contex;

    ArrayList<Barbero> barberoData;
    LayoutInflater layoutInflater;
    Barbero barberoModel;

    public ListWiewBarberosAdapter(Context contex, ArrayList<Barbero> barberoData) {
        this.contex = contex;
        this.barberoData = barberoData;
        layoutInflater = (LayoutInflater) contex.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
    }

    @Override
    public int getCount() {
        return barberoData.size();
    }

    @Override
    public Object getItem(int position) {
        return barberoData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){
            rowView = layoutInflater.inflate(R.layout.lista_barberos, null,true);
        }

        // enlazamos las vistas

        TextView nombres = rowView.findViewById(R.id.nombres);
        TextView telefonos = rowView.findViewById(R.id.telefono);
        TextView fechaRegistro = rowView.findViewById(R.id.fechaRegistro);

        barberoModel = barberoData.get(position);
        nombres.setText(barberoModel.getNombres());
        telefonos.setText(barberoModel.getTelefono());
        fechaRegistro.setText(barberoModel.getFechaRegistro());

        return rowView;
        // Ya esta creado el adaptador
    }
}
