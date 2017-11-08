package propulsar.pgb.presentationlayer.activities;

import android.content.SharedPreferences;
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

import propulsar.pgb.domainlayer.Adapters.VotadasAdapter;
import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.domainlayer.objects.Case;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

public class PropsVotadas extends AppCompatActivity implements WS.OnWSRequested{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Case> cases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_props_votadas);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Votadas");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPropsVotadas);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.votadas_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //Case (int id, String folio, String titulo, String categoria,String descripcion, String fecha, int status, int creatorID)
        cases = new ArrayList<Case>();
        /*
        cases.add(new Case(1,"ABC123DE", "Título(000)", "Categoria", "Descripción", "00/00/0000", 3, 1) );
        cases.add(new Case(1,"DEF456GH", "Título(001)", "Categoria", "Descripción", "00/00/0000", 1, 2) );
        cases.add(new Case(1,"GHI789JK", "Título(002)", "Categoria", "Descripción", "00/00/0000", 5, 1) );
        cases.add(new Case(1,"JKL123MN", "Título(003)", "Categoria", "Descripción", "00/00/0000", 4, 3) );
        cases.add(new Case(1,"MNO456PQ", "Título(004)", "Categoria", "Descripción", "00/00/0000", 2, 2) );
        */
        mAdapter = new VotadasAdapter(cases, this);
        mRecyclerView.setAdapter(mAdapter);

        //http://svcyonayarit.iog.digital/api/Proposal/GetVotedProposalsList?UserId=1&Skip=0&Take=10
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        int userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        params.put("Skip",0);
        params.put("Take",10);
        WS.getInstance(PropsVotadas.this).getVotedProposals(params,this);
    }

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("WSDEB",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try{
            ws = json.getInt("ws");
            switch (ws){
                case WS.WS_getVotedProposals:{
                    JSONObject data = json.getJSONObject("data");
                    JSONArray votedProposals = data.getJSONArray("Data");
                    ArrayList<Case> newCases = new ArrayList<Case>();
                    for(int i=0; i<votedProposals.length(); i++){
                        JSONObject votedProp = votedProposals.getJSONObject(i);
                        Case newCase = new Case();
                        newCase.setId(votedProp.getInt("ProposalId"));
                        newCase.setCreatorID(votedProp.getInt("UserId"));
                        newCase.setTitulo(votedProp.getString("Title"));
                        newCase.setDescripcion(votedProp.getString("Description"));
                        newCase.setVotesUp(votedProp.getInt("VoteUp"));
                        newCase.setVotesDown(votedProp.getInt("VoteDown"));
                        newCase.setVotesUpPercent(votedProp.getDouble("VoteUpPorcent"));
                        newCase.setVotesDownPercent(votedProp.getDouble("VoteDownPorcent"));
                        newCase.setFecha(votedProp.getString("CreatedAt").split("T")[0]);
                        newCase.setImageUrl(votedProp.getString("ImageUrl"));
                        newCase.setCreatorName(votedProp.getString("UploaderUser"));
                        newCase.setCreatorType(votedProp.getInt("UserTypeId"));
                        newCases.add(newCase);
                    }

                    if(newCases.size()==0){
                        findViewById(R.id.votadasNoHay).setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.votadasNoHay).setVisibility(View.GONE);
                    }

                    addToList(newCases);

                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private void addToList(ArrayList<Case> newCases){
        for(int i=0; i<newCases.size(); i++){
            cases.add(newCases.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }
}
