package propulsar.qroo.PresentationLayer.Frags;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.PresentationLayer.Activities.BenefsActivity;
import propulsar.qroo.PresentationLayer.Activities.ChatActivity;
import propulsar.qroo.PresentationLayer.Activities.DetalleBenefActivity;
import propulsar.qroo.PresentationLayer.Activities.DetalleProp;
import propulsar.qroo.PresentationLayer.Activities.EventActivity;
import propulsar.qroo.PresentationLayer.Activities.VotaActivity;
import propulsar.qroo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFrag extends Fragment implements WS.OnWSRequested{



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int eventID=-1;
    int proposalID=-1;
    int benefitID=-1;

    View view;

    public HomeFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Propuestas.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFrag newInstance(String param1, String param2) {
        HomeFrag fragment = new HomeFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static HomeFrag newInstance(){
        HomeFrag fragment = new HomeFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        int userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        WS.getInstance(getActivity()).getMenu(params, this);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.homeButtonVota).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VotaActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.homeButtonEventoHoy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //domingo sin carros
                if(eventID==-1){return;}
                Bundle mBundle = new Bundle();
                mBundle.putInt("eventID",eventID);
                Intent intent = new Intent(getActivity(), EventActivity.class);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.homeButtonBenefs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BenefsActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.homeButtonChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.homeButtonMasVotada).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //masVotada
                if(proposalID==-1){return;}
                Bundle mBundle = new Bundle();
                mBundle.putInt("proposalID",proposalID);
                mBundle.putInt("comesFrom",1);
                Intent intent = new Intent(getActivity(), DetalleProp.class);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.homeButtonEncuesta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(benefitID==-1){return;}
                Intent intent = new Intent(getActivity(), DetalleBenefActivity.class);
                intent.putExtra("BenefitId",benefitID);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("WSDEB",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_getMenu: {
                    JSONObject data = json.getJSONObject("data");
                    view.findViewById(R.id.progressBenefit).setVisibility(View.GONE);
                    ((TextView)view.findViewById(R.id.fechaBenefit)).setText(data.getString("EventStartTime").split("T")[0]);
                    ((TextView)view.findViewById(R.id.tituloBenefit)).setText(data.getString("EventTitle"));
                    eventID = data.getInt("EventId");
                    view.findViewById(R.id.progressVotada).setVisibility(View.GONE);
                    ((TextView)view.findViewById(R.id.fechaVotada)).setText(data.getString("ProposalCreatedAt").split("T")[0]);
                    ((TextView)view.findViewById(R.id.tituloVotada)).setText(data.getString("ProposalTitle"));
                    proposalID = data.getInt("ProposalId");
                    view.findViewById(R.id.progressEncuesta).setVisibility(View.GONE);
                    ((TextView)view.findViewById(R.id.fechaEncuesta)).setText(data.getString("BenefitDate").split("T")[0]);
                    ((TextView)view.findViewById(R.id.tituloEncuesta)).setText(data.getString("BenefitTitle"));
                    benefitID=data.getInt("BenefitId");

                    Picasso.with(getActivity()).load(data.getString("EventImageUrl")).into((ImageView)view.findViewById(R.id.eventbghome));
                    break;
                }
            }
        }catch(Exception e){e.printStackTrace();}

    }
}
