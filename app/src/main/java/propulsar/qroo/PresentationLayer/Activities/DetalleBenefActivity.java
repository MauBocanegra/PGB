package propulsar.qroo.PresentationLayer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.qroo.DomainLayer.Objects.AnalyticsApplication;
import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.R;

public class DetalleBenefActivity extends AppCompatActivity implements WS.OnWSRequested{

    View encuestaContestada;
    View buttonEncuesta;
    int benefitID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_benef);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("BeneficiosDetalle");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetalleBenefs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        benefitID=getIntent().getIntExtra("BenefitId",-1);

        Log.d("DEBBENEF","benefID="+benefitID);

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        int userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("BenefitId",benefitID);
        params.put("UserId",userID);
        WS.getInstance(DetalleBenefActivity.this).getBenefitDetails(params,this);

        encuestaContestada = findViewById(R.id.benefs_encuestaContestada);
        buttonEncuesta = findViewById(R.id.buttonContestarEncuesta);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        int userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("BenefitId",benefitID);
        params.put("UserId",userID);
        WS.getInstance(DetalleBenefActivity.this).getBenefitDetails(params,this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("debugSaved","WILLSAVE="+benefitID);
        outState.putInt("BenefitId", benefitID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("debugSaved","WILLRESTORE="+benefitID);
        benefitID = savedInstanceState.getInt("BenefitId");
        Log.d("debugSaved","DIDRESTORE="+benefitID);
    }

    // ------------------------------------------ //
    // -------------- OWN METHODS --------------- //
    // ------------------------------------------ //

    private View.OnClickListener onClickEncuesta(final boolean isAnswered, final int surveyID){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAnswered){
                    Toast.makeText(DetalleBenefActivity.this, "Debes completar primero tu perfil para participar", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
                    intent.putExtra("SurveyID",surveyID);
                    startActivity(intent);
                }
            }
        };
    }

    // ---------------------------------------------------------- //
    // -------------- WEB SERVICES IMPLEMENTATION --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("GETBenefDetails",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_getBenefitDetails: {
                    JSONObject data = json.getJSONObject("data");
                    ((TextView)findViewById(R.id.detBenefFecha)).setText(data.getString("Date").split("T")[0]);
                    ((TextView)findViewById(R.id.detBenefTitulo)).setText(data.getString("Title"));
                    ((TextView)findViewById(R.id.detBenefDesc)).setText(data.getString("Description")
                    +"\n\n"+"Dinámica de selección: "+data.getString("Dynamic")
                    +"\n\n"+"Fecha de premiación: "+data.getString("AwardDate").split("T")[0]
                    +"\n\n"+"Lugar de premiación: "+data.getString("Place")
                    +"\n\n"+"Premio: "+data.getString("Award"));

                    if(data.getBoolean("UserAnswered")){
                        buttonEncuesta.setVisibility(View.INVISIBLE);
                        encuestaContestada.setVisibility(View.VISIBLE);
                    }else{
                        buttonEncuesta.setVisibility(View.VISIBLE);
                        encuestaContestada.setVisibility(View.INVISIBLE);
                    }

                    buttonEncuesta.setOnClickListener(onClickEncuesta(data.getBoolean("UserCompleteProfile"), data.getInt("SurveyId")));
                    Picasso.with(DetalleBenefActivity.this).load(data.getString("ImageUrl")).into((ImageView)findViewById(R.id.detBenefImagen));
                    ((ImageView)findViewById(R.id.detBenefImagen)).setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
}
