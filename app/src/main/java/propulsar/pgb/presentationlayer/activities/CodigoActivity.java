package propulsar.pgb.presentationlayer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import propulsar.pgb.R;

public class CodigoActivity extends AppCompatActivity implements
        View.OnClickListener{

    Button buttonEntrar;
    Button buttonEligeLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo);

        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */

        instanciateObjects();

        setListeners();
    }

    // ----------------------------------
    // ---------- OWN METHODS ----------
    //-----------------------------------

    private void instanciateObjects(){
        buttonEntrar = findViewById(R.id.codigo_button_entrar);
        buttonEligeLista = findViewById(R.id.codigo_button_eligelsita);
    }

    private void setListeners(){
        buttonEntrar.setOnClickListener(this);
        buttonEligeLista.setOnClickListener(this);
    }

    // ----------------------------------
    // ------- ON CLICK LISTENERS -------
    //-----------------------------------


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.codigo_button_entrar:{
                Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.codigo_button_eligelsita:{
                Intent intent = new Intent(getApplicationContext(), ElegirVersionActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
