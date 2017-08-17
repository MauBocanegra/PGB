package propulsar.qroo.PresentationLayer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.qroo.DomainLayer.Adapters.EventsAdapter;
import propulsar.qroo.DomainLayer.Objects.AnalyticsApplication;
import propulsar.qroo.DomainLayer.Objects.Event;
import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.R;

public class EventActivity extends AppCompatActivity implements
        WS.OnWSRequested,
        SwipeRefreshLayout.OnRefreshListener,
        EventsAdapter.EventClickedListener {

    int eventID=-1;
    int userID=-1;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private ArrayList<Event> events;
    WS ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Eventos");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEvent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle mBundle = getIntent().getExtras();
        eventID=mBundle.getInt("eventID");
        Log.d("EventDeb","eventID="+eventID);

        events = new ArrayList<Event>();

        mRecyclerView = (RecyclerView)findViewById(R.id.eventsRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new EventsAdapter(events, this);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.eventsSwipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        /*

        mAdapter = new MsgAdapter(messages);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatSwipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);

        editText = (EditText)findViewById(R.id.editText_mensaje);
        progress = (ProgressBar)findViewById(R.id.progress_chat);

        fab = (FloatingActionButton) findViewById(R.id.fab_sendMessage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSend(view);
            }
        });

        mRecyclerView.addOnScrollListener(setScrollListener());
        * */

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("EventId",eventID);
        params.put("CreatorUserId",userID);
        //WS.getInstance(EventActivity.this).getEventDetails(params, this);
        getEventsHere();
    }

    // ----------------------------------------------- //
    // -------------- INTERNAL METHODS --------------- //
    // ----------------------------------------------- //

    private void addToList(ArrayList<Event> newEvents){

        for(int i=0; i<newEvents.size(); i++){
            events.add(newEvents.get(i));
        }
        mAdapter.notifyDataSetChanged();
        mLayoutManager.scrollToPosition(events.size());

        swipeRefreshLayout.setRefreshing(false);
    }

    private void getEventsHere(){
        Map<String, Object> params = new LinkedHashMap<>();
        //params.put("UserId",userID);
        WS.getInstance(EventActivity.this).getEvents(params, this);
    }

    private void displayEvent(final Event event){
        ((TextView)findViewById(R.id.event_title)).setText(event.getTitle());
        ((TextView)findViewById(R.id.event_description)).setText(event.getDescription());
        //((TextView)findViewById(R.id.event_fecha)).setText(event.getInsDate().split("T")[0]);
        String iniString = event.getStartTime();
        ((TextView)findViewById(R.id.event_inicia)).setText("Inicia: "+iniString.split("T")[0]+" a las "+iniString.split("T")[1]);
        String finString = event.getEndTime();
        ((TextView)findViewById(R.id.event_termina)).setText("Termina: "+finString.split("T")[0]+" a las "+finString.split("T")[1]);
        ((TextView)findViewById(R.id.event_lugar)).setText("Lugar: "+event.getPlace());
        ((TextView)findViewById(R.id.event_url)).setText("Más información en: "+event.getUrl());
        Picasso.with(EventActivity.this).load(event.getImageUrl()).into((ImageView)findViewById(R.id.event_bg_image));

        findViewById(R.id.event_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle mBundle = new Bundle();
                mBundle.putString("URL",event.getUrl());
                Intent intent = new Intent(EventActivity.this, WebViewEventActivity.class);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        findViewById(R.id.fab_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(event.getTitle())
                            .setContentDescription(event.getDescription())
                            .setContentUrl(Uri.parse(event.getUrl()))
                            .setImageUrl(Uri.parse(event.getImageUrl()))
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });
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
                case WS.WS_getEventDetails:{
                    JSONObject data = json.getJSONObject("data");
                    Event event = new Event();
                    event.setID(data.getInt("EventId"));
                    event.setTitle(data.getString("Title"));
                    event.setStartTime(data.getString("StartTime").split("T")[0]);
                    event.setDescription(data.getString("Description"));
                    event.setImageUrl(data.getString("ImageUrl"));
                    displayEvent(event);
                    //findViewById(R.id.event_progress).setVisibility(View.GONE);
                    break;
                }

                case WS.WS_getEvents:{

                    JSONObject data = json.getJSONObject("data");
                    JSONArray newEventsJArray = data.getJSONArray("Data");
                    ArrayList<Event> newEvents = new ArrayList<Event>();

                    for(int i=0; i<newEventsJArray.length();i++){
                        JSONObject newEventJSONObject = newEventsJArray.getJSONObject(i);
                        Event newEvent = new Event();

                        newEvent.setID(newEventJSONObject.getInt("EventId"));
                        newEvent.setCategory(newEventJSONObject.getInt("EventCategoryId"));
                        newEvent.setTitle(newEventJSONObject.getString("Title"));
                        newEvent.setDescription(newEventJSONObject.getString("Description"));
                        newEvent.setUrl(newEventJSONObject.getString("Url"));
                        newEvent.setImageUrl(newEventJSONObject.getString("ImageUrl"));
                        newEvent.setInsDate(newEventJSONObject.getString("InsDate"));
                        newEvent.setStartTime(newEventJSONObject.getString("StartTime"));
                        newEvent.setEndTime(newEventJSONObject.getString("EndTime"));
                        newEvent.setPlace(newEventJSONObject.getString("Place"));

                        newEvents.add(newEvent);
                    }

                    addToList(newEvents);

                    if(newEvents.size()==0){
                        findViewById(R.id.eventosNoHay).setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.eventosNoHay).setVisibility(View.GONE);
                    }

                    displayEvent(newEvents.get(0));
                    break;
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    // -------------------------------------------- //
    // -------------- SWIPE REFRESH --------------- //
    // -------------------------------------------- //

    @Override
    public void onRefresh() {
        events.clear();
        getEventsHere();
    }

    // ------------------------------------------- //
    // -------------- EventClicked --------------- //
    // ------------------------------------------- //

    @Override
    public void onEventClicked(Event event) {
        displayEvent(event);
    }
}
