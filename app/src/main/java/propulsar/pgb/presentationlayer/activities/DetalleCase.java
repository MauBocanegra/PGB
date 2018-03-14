package propulsar.pgb.presentationlayer.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.Adapters.StateCaseAdapter;
import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.domainlayer.objects.StateCase;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

public class DetalleCase extends AppCompatActivity implements WS.OnWSRequested, SwipeRefreshLayout.OnRefreshListener{

    TextView folio;
    TextView categoria;
    TextView tipo;
    TextView titulo;
    TextView desc;
    TextView fecha;

    int caseID;
    String caseFolio;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<StateCase> states;

    // ---------------------------------------- //
    // -------------- LIFECYCLE --------------- //
    // ---------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_case);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("CasesDetalle");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetalleCase);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        folio = (TextView)findViewById(R.id.detCase_folio);
        categoria = (TextView)findViewById(R.id.detCase_categoria);
        desc = (TextView)findViewById(R.id.detCase_descripcion);
        fecha = (TextView)findViewById(R.id.detCase_fecha);
        tipo = folio = (TextView)findViewById(R.id.detCase_tipo);
        titulo = folio = (TextView)findViewById(R.id.detCase_titulo);

        caseID = getIntent().getIntExtra("CaseId",-1);
        caseFolio = getIntent().getStringExtra("caseFolio");

        mRecyclerView = (RecyclerView)findViewById(R.id.statesRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        states = new ArrayList<StateCase>();
        mAdapter = new StateCaseAdapter(states, this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.statesSwipeRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setRefreshing(true);

        getDetail();
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

        mSwipeRefreshLayout.setRefreshing(false);

        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_getCaseByFolio:{}
                case WS.WS_getCaseDetail:{
                    JSONObject data = json.getJSONObject("data");

                    folio.setText("Folio: "+data.getString("Folio"));
                    //categoria.setText(data.getString("CategoryDescription"));
                    categoria.setText(data.getString("Title"));
                    desc.setText(data.getString("Description"));
                    fecha.setText(data.getString("Date").split("T")[0]);
                    //titulo.setText(data.getString("Title"));
                    titulo.setText(data.getString("CategoryDescription"));
                    tipo.setText(data.getString("ComplaintTypeDescription"));

                    setDetailsList(data.getJSONArray("ChangeStatus"));
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    // ------------------------------------------ //
    // -------------- OWN METHODS --------------- //
    // ------------------------------------------ //

    private void getDetail(){
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        int userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        if (caseID == -1) {
            params.put("ComplaintFolio", caseFolio);
            WS.getInstance(DetalleCase.this).getCaseByFolio(params, this);
        }else {
            params.put("ComplaintId", caseID);
            WS.getInstance(DetalleCase.this).getCaseDetail(params, this);
        }
    }

    private void setDetailsList(JSONArray arrayStates) throws Exception {
        for(int i=0; i<arrayStates.length(); i++){
            JSONObject jsonState = arrayStates.getJSONObject(i);
            Log.d("adjfhdska","will print = "+jsonState.toString());
            StateCase stateCase = new StateCase();
            stateCase.setComplaintID(""+jsonState.getInt("ComplaintId"));
            stateCase.setComplaintStatusID(""+jsonState.getInt("ComplaintStatusId"));
            stateCase.setColor(jsonState.getString("StatusColor"));
            stateCase.setDate(jsonState.getString("ChangeDate"));
            stateCase.setDescription(jsonState.getString("Description"));

            states.add(stateCase);
            /*
            try {
                JSONObject jsonState = arrayStates.getJSONObject(i);
            }catch(Exception e){e.printStackTrace();}
            */
        }

        mAdapter.notifyDataSetChanged();
    }

    // ----------------------------------------------------------- //
    // -------------- SWIPE REFRESH IMPLEMENTATION --------------- //
    // ----------------------------------------------------------- //

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        states.clear();
        getDetail();
    }
}
