package propulsar.pgb.presentationlayer.Frags;


import android.content.Intent;
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

import propulsar.pgb.domainlayer.Adapters.NotifAdapter;
import propulsar.pgb.domainlayer.objects.Notifs;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.presentationlayer.activities.BenefsActivity;
import propulsar.pgb.presentationlayer.activities.ChatActivity;
import propulsar.pgb.presentationlayer.activities.DetalleCase;
import propulsar.pgb.R;
import propulsar.pgb.presentationlayer.activities.EventActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotifFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotifFrag extends Fragment implements WS.OnWSRequested, SwipeRefreshLayout.OnRefreshListener, NotifAdapter.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String keyChatOrBot = "keyChatOrBot";
    public static final int keyGoToChat = 1;
    public static final int keyGoToBot = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<Notifs> notifs;
    private int skip=0;
    private int take=10;

    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    private boolean bottomRequested=false;

    View view;

    public NotifFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotifFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static NotifFrag newInstance(String param1, String param2) {
        NotifFrag fragment = new NotifFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static NotifFrag newInstance() {
        NotifFrag fragment = new NotifFrag();
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
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_notif, container, false);
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView) view.findViewById(R.id.notifsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //Case (int id, String folio, String titulo, String categoria,String descripcion, String fecha, int status, int creatorID)
        notifs = new ArrayList<Notifs>();

        mAdapter = new NotifAdapter(notifs, NotifFrag.this);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.notifsSwipeRefreshLayout);

        mRecyclerView.addOnScrollListener(setScrollListener());

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setRefreshing(true);
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
        WS.getNotifs(params,this);
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

        try{
            ws = json.getInt("ws");
            switch (ws){
                case WS.WS_getNotifs:{
                    JSONObject data = json.getJSONObject("data");
                    JSONArray newNotifsJArray = data.getJSONArray("Data");
                    ArrayList<Notifs> newNotifs = new ArrayList<Notifs>();

                    for(int i=0; i<newNotifsJArray.length();i++){
                        JSONObject newNotifJSONObject = newNotifsJArray.getJSONObject(i);
                        Notifs newNotif = new Notifs();
                        String[] insSplit = (newNotifJSONObject.getString("InsDate").split("T")[0]).split("-");
                        newNotif.setFecha(insSplit[2]+"-"+insSplit[1]+"-"+insSplit[0]);
                        newNotif.setId(newNotifJSONObject.getInt("NotificationId"));
                        newNotif.setNotif(newNotifJSONObject.getString("Description"));

                        newNotifs.add(newNotif);
                    }

                    addToList(newNotifs);

                    if(newNotifs.size()>0){
                        Log.d("NotifsDebug","Notifs HAY!");
                        view.findViewById(R.id.notifsNoHay).setVisibility(View.GONE);
                    }else{
                        Log.d("NotifsDebug","Notifs==0 SHOW LEGEND");
                        view.findViewById(R.id.notifsNoHay).setVisibility(View.VISIBLE);
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private void addToList(ArrayList<Notifs> newNotifs){
        for(int i=0; i<newNotifs.size(); i++){
            notifs.add(newNotifs.get(i));
        }
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
        bottomRequested=false;
    }

    // ---------------------------------------------------------- //
    // -------------- SwipeRefresh Implementation --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void onRefresh() {
        notifs.clear();
        skip=0; take=10;
        getNotifications(false);
    }

    // --------------------------------------------------------- //
    // -------------- OnClickItem IMPLEMENTATION --------------- //
    // --------------------------------------------------------- //

    @Override
    public void onItemClick(int position) {
        String notif = notifs.get(position).getNotif();
        //String folio = notif.replaceAll("[^0-9]+","");
        //Log.d("ClickDebug","Folio="+notif.replaceAll("[^0-9]+","")+" length="+(notif.replaceAll("[^0-9]+","")).length());

        String[] arrSplit = notif.split(" ");
        String folioNew="";
        for(int i=0; i<arrSplit.length; i++){
            if(arrSplit[i].compareTo("folio")==0){
                folioNew=arrSplit[i+1];
            }
        }

        Log.d("folioDebug","folio="+folioNew);


        if(!folioNew.isEmpty()) {
            //FOLIO
            Log.d("NotifDebug", "Folio=" + folioNew);
            Intent intent = new Intent(getActivity(), DetalleCase.class);
            intent.putExtra("caseFolio", folioNew);
            getActivity().startActivity(intent);
        }else
       // }else{
            if(notif.toLowerCase().contains("mensaje")){
                //MENSAJE
                Log.d("NotifDebug","Mensaje");
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(keyChatOrBot,keyGoToChat);
                startActivity(intent);
            }else if(notif.toLowerCase().contains("beneficio")){
                Intent intent = new Intent(getActivity(), BenefsActivity.class);
                startActivity(intent);
            }else if(notif.toLowerCase().contains("evento")){
                Intent intent = new Intent(getActivity(), EventActivity.class);
                startActivity(intent);
            }else{
                //MASIVO
                Log.d("NotifDebug","Masivo");
            }
        //}

    }


    // ---------------------------------------------------- //
    // -------------- SCROLL IMPLEMENTATION --------------- //
    // ---------------------------------------------------- //

    private RecyclerView.OnScrollListener setScrollListener(){
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(bottomRequested){return;}

                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    //Log.d("fdsafdsafds","visibleItemCount="+visibleItemCount+" total="+totalItemCount+" pastVisible"+pastVisiblesItems);

                    if((visibleItemCount+pastVisiblesItems)==totalItemCount){
                        bottomRequested=true;
                        getNotifications(true);
                    }

                    if(totalItemCount==0){
                        Log.d("NotifsDebug","Notifs==0 SHOW LEGEND");
                        view.findViewById(R.id.notifsNoHay).setVisibility(View.VISIBLE);
                    }else{
                        Log.d("NotifsDebug","Notifs==1 will hide");
                        view.findViewById(R.id.notifsNoHay).setVisibility(View.GONE);
                    }
                }
            }
        };
    }

}
