package br.edu.ifspsaocarlos.agendafirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import br.edu.ifspsaocarlos.agendafirebase.R;
import br.edu.ifspsaocarlos.agendafirebase.model.Contato;


public class DetalheActivity extends AppCompatActivity {
    private Contato c;

    private DatabaseReference databaseReference;
    String FirebaseID;
    private List<String> tiposContato;

    private EditText nomeText;
    private EditText foneText;
    private EditText emailText;
    private Spinner tipoSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        tiposContato = Arrays.asList(getResources().getStringArray(R.array.tipos_contato));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bindViews();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (getIntent().hasExtra("FirebaseID")) {

            FirebaseID=getIntent().getStringExtra("FirebaseID");


              databaseReference.child(FirebaseID).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    c = snapshot.getValue(Contato.class);

                    if (c != null) {
                        nomeText.setText(c.getNome());
                        foneText.setText(c.getFone());
                        emailText.setText(c.getEmail());
                        if (tiposContato.contains(c.getTipo())) {
                            tipoSpinner.setSelection(tiposContato.indexOf(c.getTipo()));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }

    }

    private void bindViews() {
        nomeText = findViewById(R.id.editTextNome);
        foneText = findViewById(R.id.editTextFone);
        emailText = findViewById(R.id.editTextEmail);
        tipoSpinner = findViewById(R.id.spinnerTipo);

        // Configurar adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tiposContato);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoSpinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe, menu);
        if (FirebaseID==null)
        {
            MenuItem item = menu.findItem(R.id.delContato);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salvarContato:
                salvar();
                return true;
            case R.id.delContato:
                apagar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void apagar() {

        databaseReference.child(FirebaseID).removeValue();
        Intent resultIntent = new Intent();
        setResult(3,resultIntent);
        finish();
    }

    private void salvar() {
        final DatabaseReference contatoReference;
        if (c==null) {
            c = new Contato();
            contatoReference = databaseReference.push();
        } else {
            contatoReference = databaseReference.child(FirebaseID);
        }

        c.setNome(nomeText.getText().toString());
        c.setFone(foneText.getText().toString());
        c.setEmail(emailText.getText().toString());
        c.setTipo((String) tipoSpinner.getSelectedItem());
        contatoReference.setValue(c);

        Intent resultIntent = new Intent();
        setResult(RESULT_OK,resultIntent);
        finish();
    }
}

