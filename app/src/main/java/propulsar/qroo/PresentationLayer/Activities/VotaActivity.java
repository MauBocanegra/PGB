package propulsar.qroo.PresentationLayer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.qroo.DomainLayer.Adapters.SwipeArrayAdapter;
import propulsar.qroo.DomainLayer.Objects.AnalyticsApplication;
import propulsar.qroo.DomainLayer.Objects.Case;
import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.R;

public class VotaActivity extends AppCompatActivity implements WS.OnWSRequested{

    ArrayList<Case> propuestas;
    SwipeArrayAdapter swipeArrayAdapter;
    SwipeFlingAdapterView flingContainer;

    WS.OnWSRequested activity;

    private int skip=0;
    private int take=30;
    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vota);

        activity = VotaActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarVota);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Vota");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.cardStack);

        propuestas = new ArrayList<Case>();

        //choose your favorite adapter
        swipeArrayAdapter = new SwipeArrayAdapter(VotaActivity.this, 0, propuestas);

        flingContainer.setAdapter(swipeArrayAdapter);
        flingContainer.setFlingListener(flingListener);

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        params.put("Skip",skip);
        params.put("Take",take);
        WS.getInstance(VotaActivity.this).getPendingProposals(params,this);

        findViewById(R.id.buttonSubirProp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubirPropActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonVerVotadas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PropsVotadas.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonVotarSI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flingContainer.getTopCardListener().selectRight();
            }
        });

        findViewById(R.id.buttonVotarNO).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });
    }

    // ---------------------------------------------------------- //
    // -------------- OWN METHODS --------------- //
    // ---------------------------------------------------------- //

    private void addToList(ArrayList<Case> newCases){

        for(int i=0; i<newCases.size(); i++){
            propuestas.add(newCases.get(i));
        }
        swipeArrayAdapter.notifyDataSetChanged();

        //Log.d("DebCases","casesLength="+benefs.size());
        //mSwipeRefreshLayout.setRefreshing(false);
        //bottomRequested=false;
    }

    // ------------------------------------------------ //
    // -------------- SwipeFlingAdapter --------------- //
    // ------------------------------------------------ //

    private SwipeFlingAdapterView.onFlingListener flingListener = new SwipeFlingAdapterView.onFlingListener() {
        @Override
        public void removeFirstObjectInAdapter() {
            // this is the simplest way to delete an object from the Adapter (/AdapterView)
            Log.d("LIST", "removed object!");
            propuestas.remove(0);
            swipeArrayAdapter.notifyDataSetChanged();

            if(propuestas.size()==0){
                findViewById(R.id.cardButtonNo).setVisibility(View.GONE);
                findViewById(R.id.buttonVotarSI).setVisibility(View.GONE);
                findViewById(R.id.votaNoHay).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.cardButtonNo).setVisibility(View.VISIBLE);
                findViewById(R.id.buttonVotarSI).setVisibility(View.VISIBLE);
                findViewById(R.id.votaNoHay).setVisibility(View.GONE);
            }
        }

        @Override
        public void onLeftCardExit(Object dataObject) {
            //Do something on the left!
            //propuestas.get(0).getId()
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ProposalId",((Case)dataObject).getId());
            params.put("UserId",userID);
            params.put("VoteTypeId",2);
            WS.getInstance(VotaActivity.this).voteProposal(params,activity);
            Toast.makeText(VotaActivity.this, "Votada EN CONTRA", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRightCardExit(Object dataObject) {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("ProposalId",((Case)dataObject).getId());
            params.put("UserId",userID);
            params.put("VoteTypeId",1);
            WS.getInstance(VotaActivity.this).voteProposal(params,activity);
            Toast.makeText(VotaActivity.this, "Votada A FAVOR", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdapterAboutToEmpty(int i) {
            // Ask for more data here
                /*
                al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
                */

           // findViewById(R.id.votaNoHay).setVisibility(View.VISIBLE);
        }

        @Override
        public void onScroll(float v) {
            Log.d("DEB FLING","v="+v);
        }
    };

    // ---------------------------------------------------------- //
    // -------------- WEB SERVICES IMPLEMENTATION --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("Proposals",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_getPendingProposals: {
                    JSONObject data = json.getJSONObject("data");
                    JSONArray newCasesJArray = data.getJSONArray("Data");
                    ArrayList<Case> newCases = new ArrayList<Case>();

                    for(int i=0; i<newCasesJArray.length();i++){
                        JSONObject newCaseJSONObject = newCasesJArray.getJSONObject(i);
                        Case newCase = new Case();
                        newCase.setId(newCaseJSONObject.getInt("ProposalId"));
                        newCase.setTitulo(newCaseJSONObject.getString("Title"));
                        newCase.setDescripcion(newCaseJSONObject.getString("Description"));
                        //newCase.setVotesUp(newCaseJSONObject.getInt("VoteUp"));
                        //newCase.setVotesDown(newCaseJSONObject.getInt("VoteDown"));
                        //newCase.setVotesUpPercent(newCaseJSONObject.getInt("VoteUpPorcent"));
                        //newCase.setVotesDownPercent(newCaseJSONObject.getInt("VoteDownPorcent"));
                        newCase.setFecha(newCaseJSONObject.getString("CreatedAt"));
                        newCase.setImageUrl(newCaseJSONObject.getString("ImageUrl"));
                        newCase.setCreatorName(newCaseJSONObject.getString("UploaderUser"));
                        newCase.setCreatorType(newCaseJSONObject.getInt("UserTypeId"));
                        newCase.setCreatorID(newCaseJSONObject.getInt("UserId"));
                        newCases.add(newCase);
                    }

                    if(newCases.size()==0){
                        findViewById(R.id.cardButtonNo).setVisibility(View.GONE);
                        findViewById(R.id.buttonVotarSI).setVisibility(View.GONE);
                        findViewById(R.id.votaNoHay).setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.cardButtonNo).setVisibility(View.VISIBLE);
                        findViewById(R.id.buttonVotarSI).setVisibility(View.VISIBLE);
                        findViewById(R.id.votaNoHay).setVisibility(View.GONE);
                    }

                    addToList(newCases);

                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
}
