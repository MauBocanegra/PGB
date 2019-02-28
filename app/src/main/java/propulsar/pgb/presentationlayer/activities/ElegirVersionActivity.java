package propulsar.pgb.presentationlayer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import propulsar.pgb.R;

public class ElegirVersionActivity extends AppCompatActivity {

    AutoCompleteTextView autoVersion;
    ArrayAdapter<String> adapterCompletado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_version);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instanciateObjects();

        setListeners();
    }

    private void instanciateObjects(){
        autoVersion = findViewById(R.id.elige_autocomplete);
    }

    private void setListeners(){
        String[] versiones = {"Cuautitlán","Cuautla","Cuauhtémoc"};
        adapterCompletado = new ArrayAdapter<>(ElegirVersionActivity.this, android.R.layout.simple_list_item_1, versiones);
        autoVersion.setAdapter(adapterCompletado);
        autoVersion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                startActivity(intent);
            }
        });
    }
}
