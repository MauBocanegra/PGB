package propulsar.pgb.presentationlayer.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import propulsar.pgb.R;
import propulsar.pgb.domainlayer.Adapters.MessageAdapter;
import propulsar.pgb.domainlayer.WS.BotWS;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.domainlayer.firebase_objects.Mensaje_FirebaseObj;
import propulsar.pgb.domainlayer.firebase_objects.Usuario_FirebaseObj;
import propulsar.pgb.domainlayer.objects.Msg;
import propulsar.pgb.domainlayer.objects.OtherMsg;
import propulsar.pgb.domainlayer.objects.OwnMsg;

public class ChatFirebase extends AppCompatActivity implements
        WS.FirebaseCompletionListener,
        ChildEventListener,
        View.OnClickListener,
        WS.OnWSRequested,
        BotWS.OnBotWSRequested{

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private ListView mMessageListView;
    //private MessageAdapter mMessageAdapter;
    private EditText mMessageEditText;
    private FloatingActionButton mSendButton;
    private CircleImageView buttonChat;
    private CircleImageView buttonBot;
    private ProgressBar progress;
    private TextView labelProgress;

    private MessageAdapter mMessageAdapter;

    List<Mensaje_FirebaseObj> messages;
    List<Mensaje_FirebaseObj> respChatMessages;
    List<Mensaje_FirebaseObj> respBotMessages;

    int userToDownloadChats=-1;
    boolean comesFromOnPause;
    boolean isBotOn;
    int userID=-1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_firebase);

        Bundle extras = getIntent().getExtras();
        userToDownloadChats = extras.getInt("userToDownloadChats");

        instanciateObjects();

        setListeners();
    }

    // --------------------------- //
    // ------- OWN METHODS ------- //
    //---------------------------- //

    private void instanciateObjects(){

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",-1);

        mMessageListView = findViewById(R.id.messageListView);
        mMessageEditText = findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);

        messages = new ArrayList<>();
        respChatMessages = new ArrayList<>();
        respBotMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(ChatFirebase.this, R.layout.item_message, messages);
        mMessageListView.setAdapter(mMessageAdapter);

        buttonBot = findViewById(R.id.chatfirebase_button_bot);
        buttonChat = findViewById(R.id.chatfirebase_button_chat);
        progress = findViewById(R.id.chatfirebase_progress);
        labelProgress = findViewById(R.id.chatfirebase_label_progress);
    }

    private void setListeners(){

        buttonChat.setOnClickListener(ChatFirebase.this);
        buttonBot.setOnClickListener(ChatFirebase.this);

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setVisibility(View.VISIBLE);
                } else {
                    mSendButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //int userID=userToDownloadChats;

                if(isBotOn){
                    //@{@"type":@"message",@"text":mensaje,@"from":@{@"id":@(userID)}};
                    try {
                        Map<String, Object> params = new LinkedHashMap<String, Object>();
                        JSONObject jsonMsg = new JSONObject();
                        jsonMsg.put("type", "message");
                        jsonMsg.put("text",mMessageEditText.getEditableText().toString());
                        JSONObject jsonID = new JSONObject();
                        jsonID.put("id",userID);
                        jsonMsg.put("from",jsonID);

                        setLoaderOnOff(getString(R.string.chat_enviando_mensaje));
                        blockEditText(true);

                        BotWS.sendMsg(params, ChatFirebase.this, jsonMsg);

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return;
                }


                SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
                int userID= sharedPreferences.getInt("userID",-1);

                Mensaje_FirebaseObj mensaje = new Mensaje_FirebaseObj();
                mensaje.setMensaje(mMessageEditText.getEditableText().toString());
                mensaje.setTimestamp(System.currentTimeMillis()/1000);
                mensaje.setUser((long)userID);
                mensaje.setVieneDeUsuario(userID!=1);

                Usuario_FirebaseObj usuarioFirebase = new Usuario_FirebaseObj();
                usuarioFirebase.setUser(Long.valueOf(userToDownloadChats));
                usuarioFirebase.setUltimoMensaje(mensaje);
                usuarioFirebase.setLastTimestamp(System.currentTimeMillis()/1000);

                WS.mandarMensajeFirebase(userToDownloadChats, usuarioFirebase,mensaje, ChatFirebase.this);
            }
        });

        findViewById(R.id.chatBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSupportFragmentManager().beginTransaction().replace(R.id.container,new ChatListFirebase(), ChatListFirebase.TAG).commit();
                onBackPressed();
            }
        });
    }

    private void setLoaderOnOff(String message){
        if(message==null){
            labelProgress.setText("");
            progress.setVisibility(View.INVISIBLE);
        }else{
            labelProgress.setText(message);
            progress.setVisibility(View.VISIBLE);
        }
    }

    private void blockEditText(boolean willBeBlocked){
        if(willBeBlocked){
            mMessageEditText.setEnabled(false);
            mSendButton.setVisibility(View.GONE);
        }else{
            mMessageEditText.setEnabled(true);
            mSendButton.setVisibility(View.VISIBLE);
        }
    }

    // ------------------------------------------------- //
    // ------- BUTTONS CHAT CLICK IMPLEMENTATION ------- //
    //-------------------------------------------------- //

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.chatfirebase_button_bot:{
                isBotOn=true;

                //ponemos en pantalla los mensajes del bot
                messages.clear();
                messages.addAll(respBotMessages);
                mMessageAdapter.notifyDataSetChanged();

                //cambiamos a seleccionado el boton del bot
                buttonBot.setBorderColor(ContextCompat.getColor(ChatFirebase.this, R.color.colorAccent));
                //deseleccionamos el del chat
                buttonChat.setBorderColor(ContextCompat.getColor(ChatFirebase.this, android.R.color.transparent));

                checkInitBot();
                break;

            }

            case R.id.chatfirebase_button_chat:{
                isBotOn=false;

                //ponemos en pantalla los mensajes del chat
                messages.clear();
                messages.addAll(respChatMessages);
                mMessageAdapter.notifyDataSetChanged();

                //cambiamos a seleccionado el boton del chat
                buttonChat.setBorderColor(ContextCompat.getColor(ChatFirebase.this, R.color.colorAccent));
                //deseleccionamos el del bot
                buttonBot.setBorderColor(ContextCompat.getColor(ChatFirebase.this, android.R.color.transparent));

                setLoaderOnOff(null);
                break;
            }
        }
    }


    // --------------------------------------- //
    // ------- FIREBASE IMPLEMENTATION ------- //
    //---------------------------------------- //

    @Override
    public void firebaseCompleted(boolean hasError) {
        mMessageEditText.setText("");
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        respChatMessages.add(dataSnapshot.getValue(Mensaje_FirebaseObj.class));
        if(!isBotOn) {
            messages.add(dataSnapshot.getValue(Mensaje_FirebaseObj.class));
            mMessageAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    // --------------------------------------- //
    // ------- LIFECYCLE IMPLEMENTATION ------- //
    //---------------------------------------- //

    @Override
    protected void onResume() {
        if(comesFromOnPause){comesFromOnPause=false;}else {
            WS.setReaderListener(ChatFirebase.this, userToDownloadChats);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        comesFromOnPause=true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        WS.removeReaderListener(ChatFirebase.this);
        respChatMessages.clear();
        messages.clear();
        mMessageAdapter.notifyDataSetChanged();

        super.onDestroy();
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // ++++++++ Aquí abajo va la implementación del BOT ++++++++
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private void checkInitBot(){
        String currentToken = BotWS.getCurrentToken();
        if(currentToken.isEmpty()){
            setLoaderOnOff("Iniciando ProBot");
            Map<String, Object> params = new LinkedHashMap<>();
            WS.requestToken(params, ChatFirebase.this);
            //BotWS.initBot(params,ChatActivity.this);
        }
    }

    @Override
    public void wsAnswered(JSONObject json) {
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
                    BotWS.initBot(params, ChatFirebase.this);
                    break;
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
                    BotWS.initConver(params,ChatFirebase.this);
                    break;
                }

                case BotWS.BOT_CONV:{
                    Log.d("BOT CONV ANSW",json.toString());
                    setLoaderOnOff(null);
                    break;
                }

                case BotWS.BOT_MSG:{

                    Log.d("BOT MSG ANSW",json.toString());

                    BotWS.getAll(ChatFirebase.this);

                    break;
                }

                case BotWS.BOT_GET: {
                    Log.d("BOT MSG ANSW", json.toString());

                    respBotMessages.clear();

                    JSONObject data = json.getJSONObject("data");
                    Log.d("dataSHOW", data.toString());
                    JSONArray arrayMsgs = data.getJSONArray("activities");
                    Log.d("activitiesSHOW", arrayMsgs.toString());

                    JSONArray invertedJsonArray = new JSONArray();
                    for (int i = arrayMsgs.length()-1; i>=0; i--) {
                        invertedJsonArray.put(arrayMsgs.get(i));
                    }

                    for(int i=0; i<invertedJsonArray.length(); i++){

                        JSONObject jsonMsg = invertedJsonArray.getJSONObject(i);
                        Log.e("MESSAGE",jsonMsg.toString());
                        int idSender;
                        try {

                            idSender = Integer.parseInt(jsonMsg.getJSONObject("from").getString("id"));

                            Mensaje_FirebaseObj mensaje = new Mensaje_FirebaseObj();
                            mensaje.setUser((long)(idSender==userID ? userID : 3));
                            mensaje.setMensaje(jsonMsg.getString("text"));
                            mensaje.setVieneDeUsuario(idSender==userID);

                            respBotMessages.add(0,mensaje);
                        } catch(NumberFormatException nfe) {

                            Mensaje_FirebaseObj mensaje = new Mensaje_FirebaseObj();
                            mensaje.setUser((long)3);
                            mensaje.setMensaje(jsonMsg.getString("text"));
                            mensaje.setVieneDeUsuario(false);

                            respBotMessages.add(0,mensaje);
                        }
                    }

                    messages.clear();
                    messages.addAll(respBotMessages);
                    mMessageAdapter.notifyDataSetChanged();
                    //mMessageListView.scrollTo(0,mMessageListView.getHeight());

                    //layout.scrollToPosition(messages.size());

                    blockEditText(false);

                    setLoaderOnOff(null);
                    mMessageEditText.setText("");

                    break;
                }

            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
