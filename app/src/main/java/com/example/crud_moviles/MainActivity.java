package com.example.crud_moviles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crud_moviles.adaptadores.ListWiewBarberosAdapter;
import com.example.crud_moviles.models.Barbero;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Barbero> listBarbero = new ArrayList<Barbero>();
    ArrayAdapter<Barbero> arrayAdapterBarbero;
    ListWiewBarberosAdapter listWiewBarberosAdapter;
    LinearLayout linearLayoutEditar;
    ListView listViewBarberos;

    EditText inputNombre, inputTelefono;
    Button btnCancelar;

    Barbero barberoSeleccionado;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputNombre = findViewById(R.id.inputNombre);
        inputTelefono = findViewById(R.id.inputTelefono);
        btnCancelar = findViewById(R.id.btnCancelar);

        listViewBarberos = findViewById(R.id.listViewPersonas);
        linearLayoutEditar = findViewById(R.id.linearLayoutEditar);

        listViewBarberos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                barberoSeleccionado = (Barbero) parent.getItemAtPosition(position);
                inputNombre.setText(barberoSeleccionado.getNombres());
                inputTelefono.setText(barberoSeleccionado.getTelefono());
                linearLayoutEditar.setVisibility(View.VISIBLE);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutEditar.setVisibility(View.GONE);
                barberoSeleccionado = null;
            }
        });

        inicializaliarFirebase();
        listarBarberos();
    }

    private void inicializaliarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void listarBarberos () {
        databaseReference.child("Barberos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listBarbero.clear();
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Barbero b = objSnaptshot.getValue(Barbero.class);
                    listBarbero.add(b);
                }
                //Iniciar nuestro propio adaptador
                listWiewBarberosAdapter = new ListWiewBarberosAdapter(MainActivity.this, listBarbero);
               /* arrayAdapterBarbero = new ArrayAdapter<Barbero>(
                        MainActivity.this, android.R.layout.simple_list_item_1,
                        listBarbero
                );*/
                //listViewBarberos.setAdapter(arrayAdapterBarbero);
                listViewBarberos.setAdapter(listWiewBarberosAdapter);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mi_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombres = inputNombre.getText().toString();
        String telefono = inputTelefono.getText().toString();
        switch (item.getItemId()){
            case R.id.menu_agregar:
                insertar();
                break;
            case R.id.menu_guardar:
                if(barberoSeleccionado != null){
                    if(validarInputs()==false){
                        Barbero b = new Barbero();
                        b.setIdPersona(barberoSeleccionado.getIdPersona());
                        b.setNombres(nombres);
                        b.setTelefono(telefono);
                        b.setFechaRegistro(barberoSeleccionado.getFechaRegistro());
                        b.setTimeStamp(barberoSeleccionado.getTimeStamp());
                        databaseReference.child("Barberos").child(b.getIdPersona()).setValue(b);
                        Toast.makeText(this, "Registro actualizado", Toast.LENGTH_LONG);
                        linearLayoutEditar.setVisibility(View.GONE);
                        barberoSeleccionado = null;
                    }
                } else {
                    Toast.makeText(this, "Seleccione un barbero", Toast.LENGTH_LONG);
                }
            case R.id.menu_eliminar:
                if(barberoSeleccionado != null){
                    Barbero b2 = new Barbero();
                    b2.setIdPersona(barberoSeleccionado.getIdPersona());
                    databaseReference.child("Barberos").child(b2.getIdPersona()).removeValue();
                    Toast.makeText(this,"Barbero eliminado", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(this,"Seleccione un barbero para eliminar", Toast.LENGTH_LONG);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public void insertar(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                MainActivity.this
        );
        View mView = getLayoutInflater().inflate(R.layout.insertar, null);
        Button btnInsertar = (Button) mView.findViewById(R.id.btnInsertar);
        final EditText mInputNombres = (EditText) mView.findViewById(R.id.inputNombre);
        final EditText mInputTelefono = (EditText) mView.findViewById(R.id.inputTelefono);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombres = mInputNombres.getText().toString();
                String telefono = mInputTelefono.getText().toString();
                if(nombres.isEmpty()||nombres.length()<3){
                    showError(mInputNombres,  "Nombre invalido minimo 3 letras");
                } else if(telefono.isEmpty() || telefono.length()<9){
                    showError(mInputTelefono,  "Telefono invalido minimo 8 digitos");
                } else {
                    Barbero b = new Barbero();
                    b.setIdPersona(UUID.randomUUID().toString());
                    b.setNombres(nombres);
                    b.setTelefono(telefono);
                    b.setFechaRegistro(getFechaNormal());
                    b.setTimeStamp(getFechaMilisegundos() * -1);
                    databaseReference.child("Barberos").child(b.getIdPersona()).setValue(b);
                    Toast.makeText(
                            MainActivity.this,
                            "Registrado correctamente",
                            Toast.LENGTH_LONG
                    ).show();
                    dialog.dismiss();
                }
            }
        });
    }

    public void showError(EditText input, String s){
        input.requestFocus();
        input.setError(s);
    }

    public long getFechaMilisegundos(){
        Calendar calendar = Calendar.getInstance();
        long tiempounix = calendar.getTimeInMillis();
        return tiempounix;
    }

    public String getFechaNormal(){
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        String fecha = sdf.format(getFechaMilisegundos());
        return fecha;
    }

    public boolean validarInputs () {
        String nombre = inputNombre.getText().toString();
        String telefono = inputTelefono.getText().toString();
        if(nombre.isEmpty() || nombre.length()<3){
            showError(inputNombre, "El nombre es invalido dbe ser minimo 3 letras");
            return  true;
        } else if (telefono.isEmpty()|| telefono.length()<9){
            showError(inputTelefono, "El telefono es invalido");
            return  true;
        } else {
            return false;
        }
    }
}