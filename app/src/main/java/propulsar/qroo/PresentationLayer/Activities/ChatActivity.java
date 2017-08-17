package propulsar.qroo.PresentationLayer.Activities;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import propulsar.qroo.DomainLayer.Adapters.GalleryAdapter;
import propulsar.qroo.DomainLayer.Adapters.MsgAdapter;
import propulsar.qroo.DomainLayer.Objects.AnalyticsApplication;
import propulsar.qroo.DomainLayer.Objects.Msg;
import propulsar.qroo.DomainLayer.Objects.OtherMsg;
import propulsar.qroo.DomainLayer.Objects.OwnMsg;
import propulsar.qroo.DomainLayer.Services.UserClient;
import propulsar.qroo.DomainLayer.WS.BotWS;
import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity implements
        WS.OnWSRequested,
        BotWS.OnBotWSRequested,
        OnMapReadyCallback,
        GalleryAdapter.GalleryLoadedListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=123;
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=321;
    final int MY_PERMISSIONS_REQUEST_CAMERA=231;
    final int PHOTO_ACTIVITY_REQUEST=31;
    final int GALLERY_CHOOSER=12;
    GoogleApiClient mGoogleApiClient;

    private ArrayList<Msg> messages;
    private ArrayList<Msg> respChat;
    private ArrayList<Msg> respBot;
    //private SwitchCompat switchBot;
    private boolean isBotOn;

    private ImageView switchButtonBotImg;
    private ImageView switchButtonStaffImg;
    private String officerName;
    private String officerURLImage;
    private TextView textStaff;
    private TextView textBot;
    int userID;
    private int skipIni=0; private int takeIni=10;
    private int skip=skipIni;
    private int take=takeIni;

    int scaleIni = 0;
    int scaleFin = 1;
    boolean enabled = false;

    Handler h;
    Runnable getMessagesRepeatedly;

    private EditText editText;
    private FloatingActionButton fab;
    private ProgressBar progress;
    private View buttonLocation;
    private View buttonImg;
    private FloatingActionButton buttonCam;
    private FloatingActionButton buttonGallery;
    private View cardEditText;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private View mapContainer;
    private MarkerOptions mMarkerOptions;
    private Marker mMarker;
    private boolean locationJustSent;
    private double lat; private double lon;
    private int initTime;

    private View galleryContainer;
    private GalleryAdapter mGalleryAdapter;
    private GridView gallery;
    private View progressGalleryLoaded;

    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    private boolean topRequested=false;

    //region LIFE CYCLE
    // ----------------------------------------------- //
    // ----------------- LIFE CYCLE ------------------ //
    // ----------------------------------------------- //


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    (WS.getInstance(ChatActivity.this)).showSucces("Para enviar tu ubicación debes permitirnos obtenerla",buttonLocation);
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGallery();
                }else{
                    (WS.getInstance(ChatActivity.this)).showSucces("Para enviar una imágen debes permitir el acceso a tus archivos",buttonImg);
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCamera();
                }else{
                    (WS.getInstance(ChatActivity.this)).showSucces("Para capturar una foto debes permitir el acceso a la misma",buttonImg);
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Chat");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);

        //Primero instanciamos el mapa ya que es lo mas tardado
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ChatActivity.this);
        mapContainer = findViewById(R.id.mapContainer);


        //Asignamos el toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Preparamos el adapter de los mensajes asi como las vistas que lo contienen
        messages = new ArrayList<Msg>();
        respBot = new ArrayList<Msg>();
        respChat = new ArrayList<Msg>();
        mRecyclerView = (RecyclerView)findViewById(R.id.msgRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MsgAdapter(messages, ChatActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatSwipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);

        //asignamos las vistas a los objetos de la clase
        fab = (FloatingActionButton) findViewById(R.id.fab_sendMessage);
        editText = (EditText)findViewById(R.id.editText_mensaje);
        progress = (ProgressBar)findViewById(R.id.progress_chat);
        buttonLocation = findViewById(R.id.buttonLocation);
        buttonImg = findViewById(R.id.buttonImage);
        cardEditText = findViewById(R.id.cardMensaje);
        galleryContainer = findViewById(R.id.galleryContainer);
        gallery = (GridView)findViewById(R.id.gridview);
        buttonCam = (FloatingActionButton) findViewById(R.id.fab_camera);
        buttonGallery = (FloatingActionButton)findViewById(R.id.fab_gallery);
        progressGalleryLoaded = findViewById(R.id.progressGalleryLoading);

        //Establecemos los clicks de lo clickeable
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSend(view);
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.d("TextEditListener","i="+i+" key="+keyEvent.toString());
                return false;
            }
        });
        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLocation();
            }
        });
        buttonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(galleryContainer.getVisibility()==View.VISIBLE){
                    hideGallery();
                }else{
                    clickGallery();
                }
            }
        });
        buttonCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCamera();
            }
        });
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickGalleryChooser();
            }
        });

        //Listener del scroll
        mRecyclerView.addOnScrollListener(setScrollListener());
        Map<String, Object> params = new LinkedHashMap<>();
        WS.getOfficerInfo(params,ChatActivity.this);
        //getMessages(false);


        //Por ultimo realizamos la conexion con google para el awareness
        mGoogleApiClient = new GoogleApiClient.Builder(ChatActivity.this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        //switchBot = (SwitchCompat)findViewById(R.id.switchChat);
        //switchBot.setOnCheckedChangeListener(this);
        switchButtonBotImg = (ImageView)findViewById(R.id.switchButtonBotImg);
        switchButtonStaffImg = (ImageView)findViewById(R.id.switchButtonStaffImg);
        textBot = (TextView)findViewById(R.id.textBot);
        textStaff = (TextView)findViewById(R.id.textStaff);
        findViewById(R.id.buttonChat_bot).setOnClickListener(chatSwitcherListener());
        findViewById(R.id.buttonChat_staff).setOnClickListener(chatSwitcherListener());

        findViewById(R.id.buttonChat_bot).callOnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Mandamos a actualizar los mensajes cada n segundos
        h = new Handler(){
            @Override
            public void  handleMessage(Message msg){
                switch(msg.what){
                    case 0:
                        this.removeMessages(0);
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        };

        getMessagesRepeatedly = new Runnable(){
            public void run(){
                //do something
                if(!isBotOn) {
                    getMessages(false);
                    h.postDelayed(this, initTime += 30000);
                }
            }
        };
        h.postDelayed(getMessagesRepeatedly, 30000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        h.removeCallbacks(getMessagesRepeatedly);
        BotWS.setCurrentToken("");
    }

    //endregion


    // ----------------------------------------------- //
    // ----------------- OWN METHODS ----------------- //
    // ----------------------------------------------- //

    private void checkInitBot(){
        String currentToken = BotWS.getCurrentToken();
        if(currentToken.isEmpty()){
            swipeRefreshLayout.setRefreshing(true);
            Map<String, Object> params = new LinkedHashMap<>();
            WS.requestToken(params, ChatActivity.this);
            //BotWS.initBot(params,ChatActivity.this);
        }
    }

    private View.OnClickListener chatSwitcherListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.buttonChat_bot:{
                        findViewById(R.id.msgNoHay).setVisibility(View.GONE);

                        checkInitBot();

                        buttonLocation.setVisibility(View.GONE);
                        buttonImg.setVisibility(View.GONE);
                        isBotOn=true;
                        break;
                    }
                    case R.id.buttonChat_staff:{
                        //getMessages(false);
                        if(respChat.size()==0)
                            findViewById(R.id.msgNoHay).setVisibility(View.VISIBLE);
                        buttonLocation.setVisibility(View.VISIBLE);
                        buttonImg.setVisibility(View.VISIBLE);
                        isBotOn=false;
                        break;
                    }
                }

                switchButtonBotImg.setImageDrawable(ContextCompat.getDrawable(
                        ChatActivity.this,
                        isBotOn ? R.drawable.bot_on : R.drawable.bot_off
                ));
                /*
                switchButtonStaffImg.setImageDrawable(ContextCompat.getDrawable(
                        ChatActivity.this,
                        !isBotOn ? R.drawable.user_on : R.drawable.user_off
                ));
                */

                textBot.setTypeface(null, isBotOn ? Typeface.BOLD : Typeface.NORMAL);
                textStaff.setTypeface(null, !isBotOn ? Typeface.BOLD : Typeface.NORMAL);

                textBot.setTextColor(ContextCompat.getColor(
                        ChatActivity.this,
                        isBotOn ? R.color.colorAccent : R.color.buttonGray)
                );
                textStaff.setTextColor(ContextCompat.getColor(
                        ChatActivity.this,
                        !isBotOn ? R.color.colorAccent : R.color.buttonGray)
                );

                if(!isBotOn){
                    //messages = respChat;
                    messages.clear();
                    for(Msg msg : respChat){
                        messages.add(msg);
                    }
                    mAdapter.notifyDataSetChanged();
                    getMessages(false);

                    Log.d("SwitchLOG","checked!");
                }else{
                    //messages = respBot;

                    messages.clear();
                    for(Msg msg : respBot){
                        messages.add(msg);
                    }

                    if(messages.size()==0){
                        Msg newMsg = new OtherMsg();
                        newMsg.setId(-1);
                        newMsg.setSenderId(3);
                        newMsg.setMsg("¡Hola! Mi nombre es Probot" +
                                "\nSoy el Chatbot inteligente de YoNayarit y te puedo" +
                                " ayudar a generar de manera INMEDIATA Denuncias, Sugerencias, Quejas y Consultar " +
                                "el estatus de tus casos." +
                                "\n¿Qué deseas hacer?");
                        newMsg.setTimeStamp(" T ");
                        newMsg.setUrl("");
                        newMsg.setBot(true);
                        messages.add(newMsg);
                    }

                    mAdapter.notifyDataSetChanged();
                    Log.d("SwitchLOG","unChecked");
                }
            }
        };
    }

    private void clickGalleryChooser(){

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(pickIntent, "Seleccionar imágen");
        startActivityForResult(chooserIntent, GALLERY_CHOOSER);
    }

    private void showCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, PHOTO_ACTIVITY_REQUEST);
        }
    }

    private void clickCamera(){
        if (ContextCompat.checkSelfPermission(ChatActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }else{
            showCamera();
        }
    }

    private void clickGallery(){
        if (ContextCompat.checkSelfPermission(ChatActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }else{
            showGallery();
        }
    }

    @NonNull
    private RequestBody createPartFromString (String descriptionString){
        return RequestBody.create(MultipartBody.FORM, descriptionString);
    }


    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, String fileLocation){
        File file = new File(fileLocation);

        Log.d("asdfg","mimeType = "+getMimeType(fileLocation));
        Log.d("asdfg","contentResolver="+getContentResolver().getType(Uri.fromFile(file)));
        Log.d("asdfg","URI="+Uri.fromFile(file).toString());

        Uri myUri = Uri.parse(fileLocation);
        RequestBody requestFile = RequestBody.create(
                //MediaType.parse(getContentResolver().getType(Uri.fromFile(file))), file
                MediaType.parse(getMimeType(fileLocation)), file
        );

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    private void uploadFileRetrofit(String fileLocation){
        File file = new File(fileLocation);

        //Create retrofit instance
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://testsvcyonayarit.iog.digital/api/Message/SendImageMessages/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        //Get client & call object for the request
        UserClient client = retrofit.create(UserClient.class);

        //finally execute the request
        Log.d("asdfg","userID="+userID+"fileName="+file.getName());
        Call<ResponseBody> call = client.uploadPhoto(
                createPartFromString(""+userID),
                createPartFromString("3"),
                createPartFromString("4"),
                prepareFilePart("fileContents",fileLocation)
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("asdfg","CORRECTO");
                //Toast.makeText(ChatActivity.this, "YES!", Toast.LENGTH_SHORT).show();

                hideGallery();
                getMessages(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("asdfg","ERROR");
                //Toast.makeText(ChatActivity.this, "NO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGallery(){

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        hideMap();

        if(mGalleryAdapter == null){
            mGalleryAdapter = new GalleryAdapter(ChatActivity.this);
            gallery.setAdapter(mGalleryAdapter);
            mGalleryAdapter.setGalleryLoadedListener(ChatActivity.this);
            gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

                    builder.setTitle("¿Deseas enviar la imagen seleccionada?");
                    // Add the buttons
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final String tag = ((ImageView)view).getTag().toString();
                            Log.d("UPLOADING","------- requested : "+tag);
                            uploadFileRetrofit(tag);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });

                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();

                    dialog.show();
                }
            });
        }

        galleryContainer.setVisibility(View.VISIBLE);
    }

    private void hideGallery(){
        galleryContainer.setVisibility(View.GONE);
    }

    private void showMap(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        hideGallery();

        Handler h3 = new Handler();
        h3.postDelayed(new Runnable(){
            public void run(){
                fab.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this, R.drawable.ic_add_location));
                buttonImg.setVisibility(View.GONE);
                cardEditText.setVisibility(View.GONE);
                ((ImageView)buttonLocation).setImageDrawable(ContextCompat.getDrawable(ChatActivity.this, R.drawable.ic_back));
                //tituloAccion.setVisibility(View.VISIBLE);
            }
        }, 100);
    }

    private void hideMap(){
        fab.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this, R.drawable.ic_send_material));
        buttonImg.setVisibility(View.VISIBLE);
        cardEditText.setVisibility(View.VISIBLE);
        ((ImageView)buttonLocation).setImageDrawable(ContextCompat.getDrawable(ChatActivity.this, R.drawable.ic_place));


        Handler h3 = new Handler();
        h3.postDelayed(new Runnable(){
            public void run(){
                mapContainer.setVisibility(View.GONE);

                try{ mMap.setMyLocationEnabled(false);}
                catch (SecurityException e){e.printStackTrace();}
            }
        }, 300);
    }

    private void getLocation(){

        if(mapContainer.getVisibility()==View.VISIBLE){
            hideMap();
            return;
        }

        try {
        Awareness.SnapshotApi.getLocation(mGoogleApiClient).setResultCallback(new ResultCallback<LocationResult>() {
            @Override
            public void onResult(@NonNull LocationResult locationResult) {
                if (!locationResult.getStatus().isSuccess()) {
                    Log.e("LocationAwareness", "Could not detect user location");
                    (WS.getInstance(ChatActivity.this)).showError("No pudimos obtener tu ubicación, intenta nuevamente", buttonLocation);
                    return;
                }

                if(mMap==null){
                    (WS.getInstance(ChatActivity.this)).showError("Ocurrió un error al mostrar el mapa, intenta nuevamente", buttonLocation);
                    return;
                }

                showMap();

                try{ mMap.setMyLocationEnabled(true);}
                catch (SecurityException e){e.printStackTrace();}

                Location location = locationResult.getLocation();
                lat=location.getLatitude();
                lon=location.getLongitude();
                mMarkerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude()));
                mapContainer.setVisibility(View.VISIBLE);
                CameraPosition mCameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(),location.getLongitude()))
                        .zoom(15)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
                if(mMarker==null)
                mMarker = mMap.addMarker(mMarkerOptions);

                Handler h2 = new Handler();
                h2.postDelayed(new Runnable(){
                    public void run(){
                        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                            @Override
                            public void onCameraMove() {
                                LatLng latlon = mMap.getCameraPosition().target;
                                mMarker.setPosition(latlon);
                                lat=latlon.latitude; lon=latlon.longitude;
                            }
                        });
                    }
                }, 3000);

            }
        });
        }catch(SecurityException ex){}
    }

    private void onClickLocation(){
        if (ContextCompat.checkSelfPermission(ChatActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else{
            getLocation();
        }
    }

    private void getMessages(boolean isTopList){

        swipeRefreshLayout.setRefreshing(true);

        if(isTopList){
            skip+=take;
        }else{
            skip=skipIni; take=takeIni;
            messages.clear();
            mAdapter.notifyDataSetChanged();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        params.put("Skip",skip);
        params.put("Take",take);
        WS.getMessages(params,this);
    }

    private void addToList(ArrayList<Msg> newMsgs){

        for(int i=0; i<newMsgs.size(); i++){
            messages.add(newMsgs.get(i));
        }

        for(Msg msg : messages){
            respChat.add(msg);
        }

        mAdapter.notifyDataSetChanged();
        mLayoutManager.scrollToPosition(messages.size());

        swipeRefreshLayout.setRefreshing(false);

        //Log.d("DebCases","casesLength="+benefs.size());
        //mSwipeRefreshLayout.setRefreshing(false);
        //bottomRequested=false;
    }

    //Gestionar el envio de mensajes mediante la accion del FAB
    private void clickSend(View view){

        if(mapContainer.getVisibility()==View.VISIBLE){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

            builder.setTitle("¿Deseas enviar la ubicación establecida por el marcador rojo");
            // Add the buttons
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                scaleIni = 1;
                scaleFin = 0;
                fabAnimate(false);
                locationJustSent=true;

                SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                userID = sharedPreferences.getInt("userID",0);
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("UserId",userID);
                params.put("DestinationId",3);
                params.put("Text",""+String.format("%.6f", lat)+","+ String.format("%.6f", lon));
                params.put("MessageTypeId",3);
                WS.getInstance(ChatActivity.this).sendMessage(params,ChatActivity.this);

                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {}
            });

            // Create the AlertDialog
            AlertDialog dialog = builder.create();

            dialog.show();
            return;
        }

        //Obtenemos el texto
        String mensaje = editText.getEditableText().toString();
        if(mensaje.isEmpty()){
            Snackbar snack=Snackbar.make(view, "No puedes mandar mensajes vacios", Snackbar.LENGTH_SHORT);
            View snackBarView = snack.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(ChatActivity.this, R.color.colorAccent));
            snack.setAction("Action", null).show();
            snack.show();
            return;
        }

        scaleIni = 1;
        scaleFin = 0;
        fabAnimate(false);

        swipeRefreshLayout.setRefreshing(true);

        if(isBotOn){
            //@{@"type":@"message",@"text":mensaje,@"from":@{@"id":@(userID)}};
            try {
                Map<String, Object> params = new LinkedHashMap<String, Object>();
                JSONObject jsonMsg = new JSONObject();
                jsonMsg.put("type", "message");
                jsonMsg.put("text",mensaje);
                    JSONObject jsonID = new JSONObject();
                    jsonID.put("id",userID);
                jsonMsg.put("from",jsonID);

                BotWS.sendMsg(params, ChatActivity.this, jsonMsg);
            }catch(Exception e){
                e.printStackTrace();
            }
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        params.put("DestinationId",3);
        params.put("MessageTypeId",1);
        params.put("Text",mensaje);
        WS.sendMessage(params,ChatActivity.this);
    }

    //ESCONDE DE MANERA ANIMADA EL FAB Y MUESTRA EL PROGRESS QUE ESTA DETRAS SIEMPRE VISIBLE
    private void fabAnimate(final boolean visible){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);

            fab.animate()
                .scaleX(scaleIni)
                .scaleY(scaleIni)
                .setInterpolator(interpolador)
                .setDuration(600)
                .setStartDelay(0)
                .setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation){}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.animate()
                                .scaleY(scaleFin)
                                .scaleX(scaleFin)
                                .setInterpolator(interpolador)
                                .setDuration(600)
                                .start();
                        editText.setEnabled(visible);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation){}
                    @Override
                    public void onAnimationRepeat(Animator animation){}
                });
        }
    }

    // ---------------------------------------------------- //
    // -------------- SWITCH IMPLEMENTATION --------------- //
    // ---------------------------------------------------- //

    /*
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        //staffChat
        if(isChecked){
            //messages = respChat;
            messages.clear();
            for(Msg msg : respChat){
                messages.add(msg);
            }
            mAdapter.notifyDataSetChanged();
            Log.d("SwitchLOG","checked!");
        }else{
            //messages = respBot;

            messages.clear();
            for(Msg msg : respBot){
                messages.add(msg);
            }

            if(messages.size()==0){
                Msg newMsg = new OtherMsg();
                newMsg.setId(-1);
                newMsg.setSenderId(3);
                newMsg.setMsg("¡Hola!\nSoy el ChatBot inteligente de YoNayarit y te puedo ayudar a generar Denuncias, Sugerencias, Quejas y Consultar el estatus de tus casos.\n¿Qué deseas hacer?");
                newMsg.setTimeStamp(" T ");
                newMsg.setUrl("");
                newMsg.setBot(true);
                messages.add(newMsg);
            }

            mAdapter.notifyDataSetChanged();
            Log.d("SwitchLOG","unChecked");
        }
    }
*/

    // ---------------------------------------------------------- //
    // -------------- WEB SERVICES IMPLEMENTATION --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("Messages",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try{
            ws = json.getInt("ws");
            switch (ws){

                case WS.WS_getBotToken:{
                    Log.d("BOT GETTOKEN",json.toString());
                    Map<String, Object> params = new LinkedHashMap<>();
                    JSONObject data = json.getJSONObject("data");
                    JSONArray dataArray = data.getJSONArray("Data");
                    JSONObject dataData = dataArray.getJSONObject(0);
                    String firstToken = dataData.getString("Value");
                    //Log.d("BOT GETTOKEN","initialToken = "+dataData.getString("Value"));

                    BotWS.setBotToken(firstToken);
                    BotWS.initBot(params, ChatActivity.this);
                    break;
                }

                case WS.WS_getMessages:{

                    JSONObject data = json.getJSONObject("data");
                    JSONArray newMsgJArray = data.getJSONArray("Data");
                    ArrayList<Msg> newMsgs = new ArrayList<Msg>();

                    if(newMsgJArray.length()==0 && topRequested){
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }

                    for(int i=0; i<newMsgJArray.length();i++){
                        JSONObject newMsgJSONObject = newMsgJArray.getJSONObject(i);
                        Log.d("MSGDEB","sentfrom="+((newMsgJSONObject.getInt("UserId")==userID) ? "mine" : "other"));
                        Msg newMsg=null;
                        if(newMsgJSONObject.getInt("UserId")==userID){
                            newMsg = new OwnMsg();
                        }else{
                            newMsg = new OtherMsg();
                            newMsg.setAvatarURL(officerURLImage);
                        }

                        newMsg.setId(newMsgJSONObject.getInt("MessageId"));
                        newMsg.setSenderId(newMsgJSONObject.getInt("UserId"));
                        newMsg.setMsg(newMsgJSONObject.getString("Text"));
                        newMsg.setTimeStamp(newMsgJSONObject.getString("CreatedAt"));
                        newMsg.setUrl(newMsgJSONObject.getString("Url"));

                        newMsgs.add(newMsg);
                    }

                    if(newMsgs.size()==0 && !topRequested){
                        findViewById(R.id.msgNoHay).setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.msgNoHay).setVisibility(View.GONE);
                    }

                    addToList(newMsgs);
                    //respBot = messages;

                    topRequested=false;
                    break;
                }

                case WS.WS_sendMessage:{
                    scaleIni = 0;
                    scaleFin = 1;
                    fabAnimate(true);
                    getMessages(false);
                    if(locationJustSent){locationJustSent=false; hideMap();}
                    editText.setText("");
                    break;
                }

                case WS.WS_getOfficerInfo:{
                    JSONObject data = json.getJSONObject("data");
                    officerName=data.getString("Name");
                    officerURLImage=data.getString("ImageUrl");

                    textStaff.setText(officerName);
                    Picasso.with(ChatActivity.this).load(officerURLImage).into(switchButtonStaffImg);
                    break;
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void botAnswered(JSONObject json) {
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try{
            ws = json.getInt("ws");
            switch (ws){

                case BotWS.BOT_INIT :{
                    Log.d("BOT INIT ANSW",json.toString());
                    Map<String, Object> params = new LinkedHashMap<>();
                    BotWS.initConver(params,ChatActivity.this);
                    break;
                }

                case BotWS.BOT_CONV:{
                    Log.d("BOT CONV ANSW",json.toString());
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                }

                case BotWS.BOT_MSG:{
                    scaleIni = 0;
                    scaleFin = 1;
                    fabAnimate(true);

                    Log.d("BOT MSG ANSW",json.toString());

                    BotWS.getAll(ChatActivity.this);

                    break;
                }

                case BotWS.BOT_GET:{
                    Log.d("BOT MSG ANSW",json.toString());

                    respBot.clear();

                    JSONObject data = json.getJSONObject("data");
                    Log.d("dataSHOW",data.toString());
                    JSONArray arrayMsgs = data.getJSONArray("activities");
                    Log.d("activitiesSHOW",arrayMsgs.toString());

                    /*
                    Msg newMsg = new OtherMsg();
                        newMsg.setId(-1);
                        newMsg.setSenderId(3);
                        newMsg.setMsg("¡Hola! Mi nombre es Probot" +
                                "\nSoy el ChatBot inteligente de YoNayarit y te puedo" +
                                " ayudar a generar de manera INMEDIATA Denuncias, Sugerencias, Quejas y Consultar " +
                                "el estatus de tus casos." +
                                "\n¿Qué deseas hacer?");
                        newMsg.setTimeStamp(" T ");
                        newMsg.setUrl("");
                        newMsg.setBot(true);
                        messages.add(newMsg);
                        */
                    for(int i=0; i<arrayMsgs.length(); i++){

                        JSONObject jsonMsg = arrayMsgs.getJSONObject(i);
                        Log.e("MESSAGE",jsonMsg.toString());
                        int idSender;
                        try {

                            idSender = Integer.parseInt(jsonMsg.getJSONObject("from").getString("id"));

                            Msg newMsg = new OwnMsg();
                            newMsg.setSenderId(idSender==userID ? userID : 3);
                            newMsg.setMsg(jsonMsg.getString("text"));
                            newMsg.setTimeStamp(" T ");
                            newMsg.setUrl("");
                            newMsg.setBot(true);
                            respBot.add(0,newMsg);
                        } catch(NumberFormatException nfe) {
                            //System.out.println("Could not parse " + nfe);
                            Msg newMsg = new OtherMsg();
                            newMsg.setSenderId(3);
                            newMsg.setMsg(jsonMsg.getString("text"));
                            newMsg.setTimeStamp(" T ");
                            newMsg.setUrl("");
                            newMsg.setBot(true);
                            respBot.add(0,newMsg);
                        }
                    }
                    //Collections.reverse(respBot);

                    messages.clear();

                    Log.d("MESSAGES LOG","respBot size="+respBot.size());

                    for(Msg respMsg : respBot){
                        Log.d("ReturnAllMsg",respMsg.toString());
                        messages.add(respMsg);
                    }
                    mAdapter.notifyDataSetChanged();
                    mLayoutManager.scrollToPosition(messages.size());

                    Log.d("MESSAGES LOG","now messagesSize="+messages.size());

                    swipeRefreshLayout.setRefreshing(false);
                    editText.setText("");
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------- //
    // -------------- SCROLL IMPLEMENTATION --------------- //
    // ---------------------------------------------------- //

    private RecyclerView.OnScrollListener setScrollListener(){
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //Log.d("fdjsklfds","dy="+dy);

                if(topRequested){return;}

                if(dy < 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    //Log.d("fdsafdsafds","visibleItemCount="+visibleItemCount+" total="+totalItemCount+" pastVisible"+pastVisiblesItems);

                    if((visibleItemCount+pastVisiblesItems)==totalItemCount){
                        topRequested=true;
                        getMessages(true);
                    }
                }
            }
        };
    }

    // ------------------------------------------------------- //
    // -------------- MAP READY IMPLEMENTATION --------------- //
    // ------------------------------------------------------- //

    @Override
    public void onMapReady(GoogleMap map) {
        mMap=map;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(17.0f);
    }

    // ------------------------------------------------------------ //
    // -------------- GALLERY LOADED IMPLEMENTATION --------------- //
    // ------------------------------------------------------------ //

    /*
    un num inf comienzo
    vividor prof bosque
    ojo falta tatmea
    omellete asemeja caspa
    */

    @Override
    public void onGalleryLoaded() {
        progressGalleryLoaded.setVisibility(View.GONE);
    }

}
