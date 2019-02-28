package propulsar.pgb.presentationlayer.Frags;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.presentationlayer.activities.BenefsActivity;
import propulsar.pgb.presentationlayer.activities.ChatActivity;
import propulsar.pgb.presentationlayer.activities.ChatFirebase;
import propulsar.pgb.presentationlayer.activities.ChatListFirebase;
import propulsar.pgb.presentationlayer.activities.DetalleBenefActivity;
import propulsar.pgb.presentationlayer.activities.DetalleProp;
import propulsar.pgb.presentationlayer.activities.EventActivity;
import propulsar.pgb.presentationlayer.activities.VotaActivity;
import propulsar.pgb.R;

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

    View cardEvento;
    View cardEncuesta;
    View cardBenef;
    Button buttonVota;
    Button buttonCuentanos;
    Button buttonOportunidades;
    ProgressBar progressEvento;
    ProgressBar progressEncuesta;
    ProgressBar progressBenef;
    TextView tituloEvento;
    TextView tituloEncuesta;
    TextView tituloBenef;
    TextView fechaEvento;
    TextView fechaEncuesta;
    TextView fechaBenef;

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
        WS.getMenu(params, this);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_c, container, false);

        instanciateObjects();

        try {
            if(view.findViewById(R.id.homeButtonVota)!=null)
            view.findViewById(R.id.homeButtonVota).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), VotaActivity.class);
                    startActivity(intent);
                }
            });

            if(view.findViewById(R.id.home_card_evento)!=null)
            view.findViewById(R.id.home_card_evento).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //domingo sin carros
                    if (eventID == -1) {
                        return;
                    }
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("eventID", eventID);
                    Intent intent = new Intent(getActivity(), EventActivity.class);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }
            });

            if(view.findViewById(R.id.homeButtonBenefs)!=null)
            view.findViewById(R.id.homeButtonBenefs).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), BenefsActivity.class);
                    startActivity(intent);
                }
            });

            if(view.findViewById(R.id.homeButtonChat)!=null)
            view.findViewById(R.id.homeButtonChat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(getActivity(), ChatActivity.class);
                    Log.d("ChatDebug", "userID=" + WS.getUserID());
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
                    int userID = sharedPreferences.getInt("userID", -1);

                    //userID=1;

                    //TODO
                    //Arreglar lo del nombre
                    //Hacer las distros

                    Intent intent = new Intent(getActivity(), ChatActivity.class);

                /*
                Intent intent=null;
                if(userID==1) {
                    intent = new Intent(getActivity(), ChatListFirebase.class);
                }else{
                    intent = new Intent(getActivity(), ChatFirebase.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("userToDownloadChats",userID);
                    intent.putExtras(bundle);
                }
                */
                    startActivity(intent);
                }
            });

            view.findViewById(R.id.homeButtonMasVotada).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //masVotada
                    if (proposalID == -1) {
                        return;
                    }
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("proposalID", proposalID);
                    mBundle.putInt("comesFrom", 1);
                    Intent intent = new Intent(getActivity(), DetalleProp.class);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }
            });

            view.findViewById(R.id.homeButtonEncuesta).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (benefitID == -1) {
                        return;
                    }
                    Intent intent = new Intent(getActivity(), DetalleBenefActivity.class);
                    intent.putExtra("BenefitId", benefitID);
                    startActivity(intent);
                }
            });
        }catch(Exception e){e.printStackTrace();}

        return view;
    }

    private void instanciateObjects(){

        cardEvento = view.findViewById(R.id.home_card_evento);
        cardEncuesta = view.findViewById(R.id.home_card_encuesta);
        cardBenef = view.findViewById(R.id.home_card_beneficio);
        Button buttonVota;
        Button buttonCuentanos;
        Button buttonOportunidades;
        progressEvento = view.findViewById(R.id.home_progress_evento);
        progressEncuesta = view.findViewById(R.id.home_progress_encuesta);
        progressBenef = view.findViewById(R.id.home_progress_beneficio);
        tituloEvento = view.findViewById(R.id.home_evento_titulo);
        tituloEncuesta = view.findViewById(R.id.home_encuesta_titulo);
        tituloBenef = view.findViewById(R.id.home_benef_titulo);;
        fechaEvento = view.findViewById(R.id.home_evento_fecha);
        fechaEncuesta = view.findViewById(R.id.home_encuesta_fecha);
        fechaBenef = view.findViewById(R.id.home_benef_fecha);

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
                    progressEvento.setVisibility(View.GONE);
                    String[] fEventSplit = (data.getString("EventStartTime").split("T")[0]).split("-");
                    fechaEvento.setText(fEventSplit[2]+"-"+fEventSplit[1]+"-"+fEventSplit[0]);
                    tituloEvento.setText(data.getString("EventTitle"));
                    eventID = data.getInt("EventId");
                    progressEncuesta.setVisibility(View.GONE);
                    String [] fPropSplit = (data.getString("ProposalCreatedAt").split("T")[0]).split("-");
                    fechaEncuesta.setText(fPropSplit[2]+"-"+fPropSplit[1]+"-"+fPropSplit[0]);
                    tituloEncuesta.setText(data.getString("ProposalTitle"));
                    proposalID = data.getInt("ProposalId");
                    progressBenef.setVisibility(View.GONE);
                    String [] fBeneSplit = (data.getString("BenefitDate").split("T")[0]).split("-");
                    fechaBenef.setText(fBeneSplit[2]+"-"+fBeneSplit[1]+"-"+fBeneSplit[0]);
                    tituloBenef.setText(data.getString("BenefitTitle"));
                    benefitID=data.getInt("BenefitId");
                    Picasso.with(getActivity()).load(data.getString("EventImageUrl")).into((ImageView)view.findViewById(R.id.eventbghome));

                    /*
                    JSONObject data = json.getJSONObject("data");
                    view.findViewById(R.id.home_progress_beneficio).setVisibility(View.GONE);
                    String[] fEventSplit = (data.getString("EventStartTime").split("T")[0]).split("-");
                    ((TextView)view.findViewById(R.id.fechaBenefit)).setText(fEventSplit[2]+"-"+fEventSplit[1]+"-"+fEventSplit[0]);
                    ((TextView)view.findViewById(R.id.tituloBenefit)).setText(data.getString("EventTitle"));
                    eventID = data.getInt("EventId");
                    view.findViewById(R.id.home_progress_encuesta).setVisibility(View.GONE);
                    String [] fPropSplit = (data.getString("ProposalCreatedAt").split("T")[0]).split("-");
                    ((TextView)view.findViewById(R.id.fechaVotada)).setText(fPropSplit[2]+"-"+fPropSplit[1]+"-"+fPropSplit[0]);
                    ((TextView)view.findViewById(R.id.tituloVotada)).setText(data.getString("ProposalTitle"));
                    proposalID = data.getInt("ProposalId");
                    view.findViewById(R.id.home_progress_evento).setVisibility(View.GONE);
                    String [] fBeneSplit = (data.getString("BenefitDate").split("T")[0]).split("-");
                    ((TextView)view.findViewById(R.id.fechaEncuesta)).setText(fBeneSplit[2]+"-"+fBeneSplit[1]+"-"+fBeneSplit[0]);
                    ((TextView)view.findViewById(R.id.tituloEncuesta)).setText(data.getString("BenefitTitle"));
                    benefitID=data.getInt("BenefitId");
                    Picasso.with(getActivity()).load(data.getString("EventImageUrl")).into((ImageView)view.findViewById(R.id.eventbghome));
                    */

                    break;
                }
            }
        }catch(Exception e){e.printStackTrace();}

    }
}
