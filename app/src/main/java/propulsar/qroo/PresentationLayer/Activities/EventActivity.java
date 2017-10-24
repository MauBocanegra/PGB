package propulsar.qroo.PresentationLayer.Activities;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import propulsar.qroo.DomainLayer.Adapters.EventsAdapter;
import propulsar.qroo.DomainLayer.Objects.AnalyticsApplication;
import propulsar.qroo.DomainLayer.Objects.Event;
import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.Manifest;
import propulsar.qroo.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class EventActivity extends AppCompatActivity implements
        WS.OnWSRequested,
        SwipeRefreshLayout.OnRefreshListener,
        EventsAdapter.EventClickedListener,
        EasyPermissions.PermissionCallbacks {

    int eventID=-1;
    int userID=-1;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private ArrayList<Event> events;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };
    WS ws;
    Event displayedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Twitter.initialize(EventActivity.this);

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

        mCredential = GoogleAccountCredential.usingOAuth2(EventActivity.this, Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
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

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void displayEvent(final Event event){
        displayedEvent = event;
        ((TextView)findViewById(R.id.event_title)).setText(event.getTitle());
        ((TextView)findViewById(R.id.event_description)).setText(event.getDescription());
        //((TextView)findViewById(R.id.event_fecha)).setText(event.getInsDate().split("T")[0]);
        String[] iniSplit = (event.getStartTime().split("T")[0]).split("-");
        String iniString = iniSplit[2]+"-"+iniSplit[1]+"-"+iniSplit[0];
        ((TextView)findViewById(R.id.event_inicia)).setText("Inicia: "+iniString+" a las "+event.getStartTime().split("T")[1]);
        String[] finSplit = (event.getEndTime().split("T")[0]).split("-");
        String finString = finSplit[2]+"-"+finSplit[1]+"-"+finSplit[0];
        ((TextView)findViewById(R.id.event_termina)).setText("Termina: "+finString+" a las "+event.getEndTime().split("T")[1]);
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

                PopupMenu popupMenu = new PopupMenu(EventActivity.this, view);

                popupMenu.getMenuInflater().inflate(R.menu.menu_social, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_facebook:{
                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setQuote(event.getTitle())
                                            .setContentUrl(Uri.parse(event.getUrl()))
                                            .build();
                                    shareDialog.show(linkContent);
                                }
                                break;
                            }
                            case R.id.action_twitter:{
                                shortenLink(event.getUrl().toString(), event.getTitle());
                                break;
                            }
                        }
                        return true;
                    }
                });
                setForceShowIcon(popupMenu);
                popupMenu.show();
            }
        });


        findViewById(R.id.fab_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getResultsFromApi();
                showQuestion();
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

    private void shortenLink(final String link, final String titleTweet){

        AsyncTask<Void,Void,String> asyn = new AsyncTask<Void,Void,String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(EventActivity.this, "Creando tweet...", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println("JSON RESP:" + s);
                String response=s;
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String id=jsonObject.getString("id");
                    TweetComposer.Builder builder = new TweetComposer.Builder(EventActivity.this)
                            .text(titleTweet+" "+Uri.parse(id)+" #ConectaQRoo");

                    builder.show();
                    //System.out.println("ID:"+id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                BufferedReader reader;
                StringBuffer buffer;
                String res=null;
                String json = "{\"longUrl\": \""+link+"\"}";
                try {
                    URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key="+getString(R.string.urlShortnerKey));
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(40000);
                    con.setConnectTimeout(40000);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    OutputStream os = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));

                    writer.write(json);
                    writer.flush();
                    writer.close();
                    os.close();

                    int status=con.getResponseCode();
                    InputStream inputStream;
                    if(status==HttpURLConnection.HTTP_OK)
                        inputStream=con.getInputStream();
                    else
                        inputStream = con.getErrorStream();

                    reader= new BufferedReader(new InputStreamReader(inputStream));

                    buffer= new StringBuffer();

                    String line="";
                    while((line=reader.readLine())!=null)
                    {
                        buffer.append(line);
                    }

                    res= buffer.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;

            }
        };
        try {
            asyn.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class newShortAsync extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("JSON RESP:" + s);
            String response=s;
            try {
                JSONObject jsonObject=new JSONObject(response);
                String id=jsonObject.getString("id");
                System.out.println("ID:"+id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res=null;
            String json = "{\"longUrl\": \""+params[0]+"\"}";
            try {
                URL url = new URL("https://www.googleapis.com/urlshortener/v1/url?key="+getString(R.string.urlShortnerKey));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(40000);
                con.setConnectTimeout(40000);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                OutputStream os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(json);
                writer.flush();
                writer.close();
                os.close();

                int status=con.getResponseCode();
                InputStream inputStream;
                if(status==HttpURLConnection.HTTP_OK)
                    inputStream=con.getInputStream();
                else
                    inputStream = con.getErrorStream();

                reader= new BufferedReader(new InputStreamReader(inputStream));

                buffer= new StringBuffer();

                String line="";
                while((line=reader.readLine())!=null)
                {
                    buffer.append(line);
                }

                res= buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;

        }
    }

    // ----------------------------------------------------- //
    // -------------- GoogleCalendar Methods --------------- //
    // ----------------------------------------------------- //

    private void showQuestion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);

        builder.setTitle("¿Deseas agregar el evento \""+displayedEvent.getTitle()+"\" a tu calendario?");
        // Add the buttons
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getResultsFromApi();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            Log.e("CalendarDebug","AcquireGooglePlayServices");
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            Log.d("CalendarDebug","Choose Account");
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Log.e("CalendarDebug","No connection Error");
            Toast.makeText(EventActivity.this, "Conectate a 4G o Wifi", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("CalendarDebug","MakeRequestTask");
            //showQuestion();
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {

            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            Log.d("CalendarDebug","Account STRING set to= "+accountName);
            if (accountName != null) {
                Log.d("CalendarDebug","mCredential wil set to= "+accountName);
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                Log.d("CalendarDebug","DialogChooser");
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            Log.d("CalendarDebug","RequestPermission Account");
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    // --------------------------------------------------------- //
    // -------------- GoogleCalendar Permissions --------------- //
    // --------------------------------------------------------- //

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                EventActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(EventActivity.this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                   /* mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                                    */
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }



    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequest extends  AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("CalendarDebug","MakeRequest inBackground");
            try {


                com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event()
                        .setSummary(displayedEvent.getTitle())
                        //.setLocation("800 Howard St., San Francisco, CA 94103")
                        .setDescription(displayedEvent.getDescription());

                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                //DateTime startDateTime = new DateTime("2017-10-09T08:00:00");
                DateTime startDateTime = new DateTime(outputFormat.parse(displayedEvent.StartTime));
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("America/Cancun");
                event.setStart(start);

                DateTime endDateTime = new DateTime(outputFormat.parse(displayedEvent.EndTime));
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("America/Cancun");
                event.setEnd(end);

                String calendarId = "primary";
                event = mService.events().insert(calendarId, event).execute();
                Log.d("EventDebug"," - - - - - - - Event created: "+event.getHtmlLink());
                EventActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(EventActivity.this, "¡Agregaste \""+displayedEvent.getTitle()+"\" a tu calendario!", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
                //return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws Exception {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(30)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<com.google.api.services.calendar.model.Event> items = events.getItems();

            for (com.google.api.services.calendar.model.Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
            Log.d("CalendarDebug","eventStrings="+eventStrings.toString());
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            //mProgress.show();
        }

        /*
        @Override
        protected void onPostExecute(List<String> output) {
            //mProgress.hide();
            if (output == null || output.size() == 0) {
                Log.d("CalendarDebug","No results");
                Toast.makeText(EventActivity.this, "No results returned.", Toast.LENGTH_SHORT).show();
                //mOutputText.setText("No results returned.");
            } else {
                Log.d("CalendarDebug","Some data retrieved!");
                output.add(0, "Data retrieved using the Google Calendar API:");
                Toast.makeText(EventActivity.this, "Data retrieved using Google Calendar", Toast.LENGTH_SHORT).show();
            }
        }
        */

        @Override
        protected void onCancelled() {
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            EventActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(EventActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("EventLog","The following error occurred: "+ mLastError.getMessage());
                    //mOutputText.setText("The following error occurred:\n"
                            //+ mLastError.getMessage());
                }
            } else {
                Log.d("CalendarDebug","Request cancelled");
                //mOutputText.setText("Request cancelled.");
            }
        }
    }
}
