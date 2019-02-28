package propulsar.pgb.presentationlayer.activities;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

public class SubirPropActivity extends AppCompatActivity implements WS.OnWSRequested{

    CardView buttonSubir;
    EditText titulo;
    EditText desc;

    View textButtonSubir;
    View progressButtonSubir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_prop);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("PropuestaCrear");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarVota);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        titulo = (EditText)findViewById(R.id.subirProp_titulo_edittext);
        desc = (EditText)findViewById(R.id.subirProp_desc_edittext);
        buttonSubir = (CardView)findViewById(R.id.buttonSubirProp);
        buttonSubir.setOnClickListener(onClickSubir());
        textButtonSubir = findViewById(R.id.textButtonSubirProp);
        progressButtonSubir = findViewById(R.id.progressButtonSubirProp);
    }

    // -------------------------------------------------- //
    // -------------- OWN IMPLEMENTATIONS --------------- //
    // -------------------------------------------------- //

    private View.OnClickListener onClickSubir(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringTitulo = titulo.getEditableText().toString();
                String stringDesc = desc.getEditableText().toString();

                if(stringDesc.isEmpty() || stringTitulo.isEmpty()){
                    Snackbar snack=Snackbar.make(view, "Debes completar todos los campos", Snackbar.LENGTH_SHORT);
                    View snackBarView = snack.getView();
                    snackBarView.setBackgroundColor(ContextCompat.getColor(SubirPropActivity.this, R.color.colorAccent));
                    snack.setAction("Action", null).show();
                    snack.show();
                    return;
                }

                titulo.setEnabled(false);
                desc.setEnabled(false);

                progressButtonSubir.setVisibility(View.VISIBLE);

                SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                int userID = sharedPreferences.getInt("userID",0);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("UserId",userID);
                params.put("Title",stringTitulo);
                params.put("Description",stringDesc);
                WS.createProposal(params,SubirPropActivity.this);
            }
        };
    }

    // ------------------------------------------- //
    // -------------- WEB SERVICES --------------- //
    // ------------------------------------------- //

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("SubirProps",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){
            titulo.setEnabled(true);

            Snackbar snack=Snackbar.make(findViewById(R.id.textButtonSubirProp), "No se creo de manera correcta tu propuesta, vuelve a intentar", Snackbar.LENGTH_SHORT);
            View snackBarView = snack.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(SubirPropActivity.this, R.color.colorAccent));
            snack.setAction("Action", null).show();
            snack.show();
        }
        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_createProposal:{
                    titulo.setText("");
                    desc.setText("");
                    titulo.setEnabled(true);

                    WS.showSucces("¡Propueseta creada exitosamente!",findViewById(R.id.main_container));

                    Toast.makeText(this, json.getJSONObject("data").getString("Message"), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "¡Propueseta creada exitosamente!", Toast.LENGTH_SHORT).show();
                    progressButtonSubir.setVisibility(View.GONE);
                    onBackPressed();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            desc.setEnabled(true);
            textButtonSubir.setVisibility(View.VISIBLE);
        }
    }
}
