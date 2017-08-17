package propulsar.qroo.PresentationLayer.Frags;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.qroo.DomainLayer.Adapters.CasesAdapter;
import propulsar.qroo.DomainLayer.Objects.Case;
import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoliosFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoliosFrag extends Fragment implements WS.OnWSRequested, SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;

    View view;

    ArrayList<Case> cases;
    private int skip=0;
    private int take=10;

    public FoliosFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoliosFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static FoliosFrag newInstance(String param1, String param2) {
        FoliosFrag fragment = new FoliosFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static FoliosFrag newInstance() {
        FoliosFrag fragment = new FoliosFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_folios, container, false);
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) view.findViewById(R.id.foliosRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //Case (int id, String folio, String titulo, String categoria,String descripcion, String fecha, int status, int creatorID)
        cases = new ArrayList<Case>();
        /*
        cases.add(new Case(1,"ABC123DE", "Folio", "Categoria", "Descripción", "00/00/0000", 3, 123) );
        cases.add(new Case(1,"DEF456GH", "Folio", "Categoria", "Descripción", "00/00/0000", 1, 123) );
        cases.add(new Case(1,"GHI789JK", "Folio", "Categoria", "Descripción", "00/00/0000", 5, 123) );
        cases.add(new Case(1,"JKL123MN", "Folio", "Categoria", "Descripción", "00/00/0000", 4, 123) );
        cases.add(new Case(1,"MNO456PQ", "Folio", "Categoria", "Descripción", "00/00/0000", 2, 123) );
        */
        mAdapter = new CasesAdapter(cases, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        getNotifications(false);

        return view;
    }

    // ----------------------------------------------- //
    // -------------- INTERNAL METHODS --------------- //
    // ----------------------------------------------- //

    private void getNotifications(boolean isBottomList){

        if(isBottomList){
            skip+=take;
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        int userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        params.put("Skip",skip);
        params.put("Take",take);
        WS.getInstance(getActivity()).getCasesList(params,this);
    }

    private void addToList(ArrayList<Case> newCases){

        for(int i=0; i<newCases.size(); i++){
            cases.add(newCases.get(i));
        }
        mAdapter.notifyDataSetChanged();

        Log.d("DebCases","casesLength="+cases.size());
        //mSwipeRefreshLayout.setRefreshing(false);
        //bottomRequested=false;
    }

    // ---------------------------------------------------------- //
    // -------------- SwipeRefresh Implementation --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void onRefresh() {

    }

    // ---------------------------------------------------------- //
    // -------------- WEB SERVICES IMPLEMENTATION --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("GETCASESLIST",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try{
            ws = json.getInt("ws");
            switch (ws){
                case WS.WS_getCases:{
                    JSONObject data = json.getJSONObject("data");
                    JSONArray newCasesJArray = data.getJSONArray("Data");
                    ArrayList<Case> newCases = new ArrayList<Case>();

                    for(int i=0; i<newCasesJArray.length();i++){
                        JSONObject newNotifJSONObject = newCasesJArray.getJSONObject(i);
                        Case newCase = new Case();
                        newCase.setFolio(newNotifJSONObject.getString("Folio"));
                        newCase.setDescripcion(newNotifJSONObject.getString("Description"));
                        newCase.setFecha(newNotifJSONObject.getString("Date").split("T")[0]);
                        newCase.setId(newNotifJSONObject.getInt("ComplaintId"));
                        newCase.setCategoria(newNotifJSONObject.getString("CategoryDescription"));
                        JSONArray complaintStatus = newNotifJSONObject.getJSONArray("ComplaintsStatus");
                        newCase.setLasStatusColor(
                                complaintStatus.getJSONObject(complaintStatus.length()-1).getString("StatusColor")
                        );
                        /*
                        newNotif.setFecha(newNotifJSONObject.getString("InsDate").split("T")[0]);
                        newNotif.setId(newNotifJSONObject.getInt("NotificationId"));
                        newNotif.setNotif(newNotifJSONObject.getString("Description"));
                        */

                        newCases.add(newCase);
                    }

                    addToList(newCases);

                    if(newCases.size()==0){
                        view.findViewById(R.id.casesNoHay).setVisibility(View.VISIBLE);
                    }else{
                        view.findViewById(R.id.casesNoHay).setVisibility(View.GONE);
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
}
