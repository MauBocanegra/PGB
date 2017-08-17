package propulsar.qroo.PresentationLayer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.qroo.DomainLayer.Objects.AnalyticsApplication;
import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.R;

import static propulsar.qroo.DomainLayer.WS.WS.context;

public class DetalleProp extends AppCompatActivity implements WS.OnWSRequested, View.OnClickListener{

    int proposalID=-1;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_prop);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("PropuestasDetalle");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetalleProp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        proposalID = getIntent().getExtras().getInt("proposalID");
        Log.d("fdnjskfdskn","propsalID="+proposalID);

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ProposalId",proposalID);
        params.put("UserId",userID);
        WS.getInstance(DetalleProp.this).getProposalDetail(params,this);
    }

    // ---------------------------------------------------------- //
    // -------------- WEB SERVICES IMPLEMENTATION --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("WSDEB",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_getProposalDetail:{
                    JSONObject data = json.getJSONObject("data");
                    ((TextView)findViewById(R.id.detPropTitulo)).setText(data.getString("Title"));
                    ((TextView)findViewById(R.id.detPropDesc)).setText(data.getString("Description"));
                    ((TextView)findViewById(R.id.detPropSubidaPor)).setText("Subida por "+data.getString("UploaderUser"));
                    ((TextView)findViewById(R.id.detPropFecha)).setText(data.getString("CreatedAt").split("T")[0]);
                    ((TextView)findViewById(R.id.detPropTitUp)).setText("Votos a favor ("+data.getDouble("VoteUpPorcent")+"%)");
                    ((TextView)findViewById(R.id.detPropTitDown)).setText("Votos en contra ("+data.getDouble("VoteDownPorcent")+"%)");

                    ((ImageView)findViewById(R.id.detPropIconPerfil)).setImageResource(
                            data.getInt("UserId")==userID ? R.mipmap.ic_perfil_c
                                    : data.getInt("UserTypeId")==2 ? R.mipmap.ic_gobernador :
                                    R.mipmap.ic_perfil_v
                    );

                    boolean voted = data.getBoolean("UserVoted");
                    if(voted){
                        findViewById(R.id.buttonAFavor).setVisibility(View.GONE);
                        findViewById(R.id.buttonEnContra).setVisibility(View.GONE);
                    }else{

                    }

                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    entries.add(new Entry((float)data.getDouble("VoteUpPorcent"),0));
                    entries.add(new Entry((float)data.getDouble("VoteDownPorcent"),1));
                    PieDataSet dataset = new PieDataSet(entries, "");
                    ArrayList<String> labels = new ArrayList<String>();
                    labels.add("A favor");
                    labels.add("En contra");
                    PieData pieData = new PieData(labels, dataset);
                    PieChart pieChart = ((PieChart)findViewById(R.id.perceView));
                    dataset.setColors(new int[]{ContextCompat.getColor(context, R.color.colorAccent), ContextCompat.getColor(context, R.color.buttonRed)});
                    pieChart.setData(pieData);
                    pieChart.setDescription("");
                    pieChart.animateY(5000);
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    // ------------------------------------------------------ //
    // -------------- ON CLICK IMPLEMENTATION --------------- //
    // ------------------------------------------------------ //


    @Override
    public void onClick(View view) {

    }

    // ------------------------------------------------------------- //
    // -------------- PARENT ACTIVITY IMPLEMENTATION --------------- //
    // ------------------------------------------------------------- //

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent i = null;
        int comesFrom = getIntent().getExtras().getInt("comesFrom");
        if (comesFrom==1) {
            i = new Intent(this, TabActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            i = new Intent(this, PropsVotadas.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        return i;
    }
}
