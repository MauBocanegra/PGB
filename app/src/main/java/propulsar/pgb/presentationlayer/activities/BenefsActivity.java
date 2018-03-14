package propulsar.pgb.presentationlayer.activities;

import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.Adapters.BenefsAdapter;
import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.domainlayer.objects.Benefs;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

public class BenefsActivity extends AppCompatActivity implements WS.OnWSRequested, SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Benefs> benefs;
    private int skip=0;
    private int take=10;
    int userID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefs);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Beneficios");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBenefs);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);

        mRecyclerView = (RecyclerView)findViewById(R.id.benefsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //Case (int id, String folio, String titulo, String categoria,String descripcion, String fecha, int status, int creatorID)
        benefs = new ArrayList<Benefs>();
        /*
        benefs.add(new Benefs(1,"Gana una beca en inglés.","Descripción","00/00/0000"));
        benefs.add(new Benefs(1,"Cambia a focos ahorradores gratis.","Descripción","00/00/0000"));
        */
        mAdapter = new BenefsAdapter(benefs, this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.benefsSwipeRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setRefreshing(true);
        getNotifications(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
    }

    // ----------------------------------------------- //
    // -------------- INTERNAL METHODS --------------- //
    // ----------------------------------------------- //

    private void getNotifications(boolean isBottomList){

        if(isBottomList){
            skip+=take;
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        params.put("Skip",skip);
        params.put("Take",take);
        WS.getInstance(BenefsActivity.this).getBenefitsList(params,this);
    }

    private void addToList(ArrayList<Benefs> newCases){

        for(int i=0; i<newCases.size(); i++){
            benefs.add(newCases.get(i));
        }
        mAdapter.notifyDataSetChanged();

        //Log.d("DebCases","casesLength="+benefs.size());
        mSwipeRefreshLayout.setRefreshing(false);
        //bottomRequested=false;
    }

    // ---------------------------------------------------------- //
    // -------------- SwipeRefresh Implementation --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void onRefresh() {
        benefs.clear();
        skip=0; take=10;
        getNotifications(false);
    }

    // ---------------------------------------------------------- //
    // -------------- WEB SERVICES IMPLEMENTATION --------------- //
    // ---------------------------------------------------------- //

    //public healthcare syst/program
    //Medical LOA (leave of abscence)

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("GETBenefsList",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try{
            ws = json.getInt("ws");
            switch (ws){
                case WS.WS_getBenefitsList:{
                    JSONObject data = json.getJSONObject("data");
                    JSONArray newCasesJArray = data.getJSONArray("Data");
                    ArrayList<Benefs> newBenefs = new ArrayList<Benefs>();

                    for(int i=0; i<newCasesJArray.length();i++){
                        JSONObject newBenefJSONObject = newCasesJArray.getJSONObject(i);
                        Benefs newBenef = new Benefs();
                        //SET BENEFS THINGS
                        newBenef.setId(newBenefJSONObject.getInt("BenefitId"));
                        newBenef.setTitulo(newBenefJSONObject.getString("Title"));
                        newBenef.setImageUrl(newBenefJSONObject.getString("ImageUrl"));
                        /*
                        newBenef.setFecha(newBenefJSONObject.getString("Date").split("T")[0]);
                        newBenef.setDesc(newBenefJSONObject.getString("Description"));
                        newBenef.setImageUrl(newBenefJSONObject.getString("ImageUrl"));
                        newBenef.setSurveyIdAssociated(newBenefJSONObject.getInt("SurveyId"));
                        */

                        newBenefs.add(newBenef);
                    }

                    if(newBenefs.size()==0){
                        findViewById(R.id.benefsNoHay).setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.benefsNoHay).setVisibility(View.GONE);
                    }

                    addToList(newBenefs);

                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
}
