package propulsar.qroo.DomainLayer.WS;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by maubocanegra on 05/07/17.
 */


public class BotWS {
    private static BotWS instance;
    public static Context context;

    public interface OnBotWSRequested{
        public void botAnswered(JSONObject json);
    }

    private static OnBotWSRequested botWSListener;
    private static HttpURLConnection con = null;
    private static AsyncTask<Void,Void,JSONObject> async;

    public static String currentToken = "";
    public static String convID = "";
    public static int expirationTime = 0;

    // ------------------------------------------------------ //
    // -------------- INSTANCE IMPLEMENTATION --------------- //
    // ------------------------------------------------------ //

    public synchronized static BotWS getInstance(Context c){
        if(instance==null){
            instance = new BotWS();
            context = c;
            Log.d("BotWS","createBotWS");
        }
        return instance;
    }

    public class NullInstanceException extends Exception{
        String message;

        public NullInstanceException(){super();}

        public NullInstanceException(String msg){
            super(msg);
            this.message=msg;
        }

        public NullInstanceException(String msg, Throwable cause){
            super(msg, cause);
            this.initCause(cause);
            this.message=msg;
        }
    }

    // ------------------------------------------------- //
    // -------------- CALLING FUNCTIONS  --------------- //
    // ------------------------------------------------- //

    public static void initBot(Map<String,Object> params, OnBotWSRequested listener){
        Log.d("initBOT", " ----- initBotRequested ----- ");
        String URLString = BOT_URL+BOT_URLINIT;
        performRequest(URLString, params, BOT_INIT, listener, null);
    }

    public static void initConver(Map<String,Object> params, OnBotWSRequested listener){
        Log.d("startConvBOT", " ----- startConvBotRequested ----- ");
        String URLString = BOT_URL+BOT_URLCONV;
        performRequest(URLString, params, BOT_CONV, listener, null);
    }

    public static void getAll(OnBotWSRequested listener){
        Log.d("getAllBOT", " ----- getAllBOTRequested ----- ");
        String URLString = BOT_URL+BOT_URLCONV+convID+"/activities/";
        performRequest(URLString, new LinkedHashMap<String,Object>(), BOT_GET, listener, null);
    }

    public static void sendMsg(Map<String,Object> params, final OnBotWSRequested listener, final JSONObject jsonMsg){
        Log.d("sendMsgBOT", " ----- sendMsgBotRequested ----- ");
        final String URLString = BOT_URL+BOT_URLCONV+convID+"/activities/";
        //performRequest(URLString, params, BOT_MSG, listener, jsonMsg);


        async = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {

                HttpURLConnection urlConnection;
                String data = jsonMsg.toString();
                String result = null;
                try {
                    //Connect
                    urlConnection = (HttpURLConnection) ((new URL(URLString).openConnection()));
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Authorization", "Bearer " + currentToken);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.connect();

                    //Write
                    OutputStream outputStream = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write(data);
                    writer.close();
                    outputStream.close();

                    //Read
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                    String line = null;
                    StringBuilder sb = new StringBuilder();

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    result = sb.toString();

                    JSONObject jsonRes = new JSONObject();
                    jsonRes.put("status",0);
                    jsonRes.put("ws",BOT_MSG);
                    jsonRes.put("data",new JSONObject(result));

                    return jsonRes;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final JSONObject jsonRes) {
                super.onPostExecute(jsonRes);
                Log.d("PostExec","PreNullCheck");
                if(jsonRes!=null){
                    //Toast.makeText(context,jsonRes.toString(), Toast.LENGTH_LONG).show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.botAnswered(jsonRes);
                            //async.execute();
                        }
                    }, 1);
                }else{
                }
            }
        };
        async.execute();


    }


    // ----------------------------------------------- //
    // -------------- JSON CONSUMPTION --------------- //
    // ----------------------------------------------- //

    private static JSONObject performRequest(final  String urlString, final Map<String,Object> params, final int botWSReq, final OnBotWSRequested provListener, final JSONObject jsonMsg){
        async = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                try{
                    URL url = new URL(urlString);

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String,Object> param : params.entrySet()) {
                        Log.d("jsonData","length = "+postData.length());
                        if (postData.length() != 0) {
                            postData.append('&');
                        }

                        if (postData.length() == 0 && botWSReq==BOT_GET){postData.append('?');}
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                    Log.d("WSDeb","postData.toString()="+urlString+postData.toString());
                    if(botWSReq==BOT_GET){
                        url = new URL(urlString.concat(postData.toString()));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");



                    con = (HttpURLConnection)url.openConnection();
                    if(botWSReq!=BOT_GET) {
                        Log.d("method","POST METHOD");
                        con.setRequestMethod("POST");
                    }else {
                        Log.d("method","GET METHOD");
                        con.setRequestMethod("GET");
                    }
                    con.setConnectTimeout(10 * 1000);


                    //if(botWSReq!=BOT_GET)
                        //con.setRequestProperty("Content-Length", postDataBytes.length + "");

                    if(botWSReq==BOT_INIT){
                        con.setRequestProperty("Authorization","Bearer "+BOT_TOKEN);
                    }else{
                        Log.d("BOT WS","will set Bearer in "+botWSReq+": "+currentToken);
                        con.setRequestProperty("Authorization","Bearer "+currentToken);
                    }

                    if(botWSReq!=BOT_MSG) {
                        con.setDoInput(true);
                        con.setUseCaches(false);
                    }

                    int statusCode = con.getResponseCode();

                    if(statusCode==200) {
                        Log.d("BOTDEB","RETURNED SUCCESFUL");
                        InputStream is = con.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line=null;
                        StringBuffer response = new StringBuffer();
                        while( (line = rd.readLine()) != null) {
                            if (line!=null)
                                response.append(line);
                        }
                        rd.close();
                        JSONObject json = new JSONObject(response.toString());
                        JSONObject jsonRes = new JSONObject();
                        jsonRes.put("status",0);
                        jsonRes.put("ws",botWSReq);
                        jsonRes.put("data",json);

                        try {
                            convID = json.getString("conversationId");
                            currentToken = json.getString("token");
                        }catch(Exception e){/*Nothing happens*/}

                        //Log.d("PRE BOT ANSWER","ProBotAnsw = "+json.toString());
                        //createCache(wsReq,jsonRes);

                        //Toast.makeText(context, json.toString(), Toast.LENGTH_SHORT).show();

                        return jsonRes;
                    }
                    else{
                        Log.d("BOTDEB","RETURNED FAILURE");
                        InputStream is = con.getErrorStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line=null;
                        StringBuffer response = new StringBuffer();
                        while( (line = rd.readLine()) != null) {
                            if (line!=null)
                                response.append(line);
                        }
                        rd.close();
                        Log.e("HTTPDEBBUG","StatusCode="+statusCode);
                        Log.e("HTTPDEBBUG","Response="+response);

                        JSONObject jsonRes = new JSONObject();
                        jsonRes.put("status",-1);
                        jsonRes.put("ws",botWSReq);
                        jsonRes.put("data",null);

                        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Ocurrió un error, intenta nuevamente más tarde", Toast.LENGTH_SHORT).show();
                            }
                        });

                        return jsonRes;
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final JSONObject jsonRes) {
                super.onPostExecute(jsonRes);
                Log.d("PostExec","PreNullCheck");
                if(jsonRes!=null){
                    //Toast.makeText(context,jsonRes.toString(), Toast.LENGTH_LONG).show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            provListener.botAnswered(jsonRes);
                            //async.execute();
                        }
                    }, 1);
                }else{
                }
            }
        };

        async.execute();

        return null;
    }

    // ------------------------------------------------ //
    // -------------- Getters & Setters --------------- //
    // ------------------------------------------------ //


    public static String getBotToken() {
        return BOT_TOKEN;
    }

    public static void setBotToken(String botToken) {
        BOT_TOKEN = botToken;
    }

    public static String getCurrentToken() {
        return currentToken;
    }

    public static void setCurrentToken(String currentToken) {
        BotWS.currentToken = currentToken;
    }

    public static String getConvID() {
        return convID;
    }

    public static void setConvID(String convID) {
        BotWS.convID = convID;
    }

    public static int getExpirationTime() {
        return expirationTime;
    }

    public static void setExpirationTime(int expirationTime) {
        BotWS.expirationTime = expirationTime;
    }

    // ------------------------------------------------------- //
    // -------------- Constants & Declarations --------------- //
    // ------------------------------------------------------- //

    public static final String BOT_URL = "https://directline.botframework.com/";
    public static final String BOT_URLINIT = "api/conversations";
    public static final String BOT_URLCONV = "v3/directline/conversations/";
    public static String BOT_TOKEN = "";

    public static final int BOT_INIT = 101;
    public static final int BOT_CONV = 102;
    public static final int BOT_MSG = 103;
    public static final int BOT_GET = 104;
    public static final int BOT_GET_TOKEN = 105;



    /*
    #define WebChatBotString @"https:directline.botframework.com/"

    #define URLInit @"api/conversations"
    #define URLConversations @"v3/directline/conversations"

    #define firstAuth @"Bearer h6uQJnZcmjY.cwA.HUs.1Pr5vfVrUmbbSJFNzH6NnRDkRxB1pSsBEB95cVj8CUc"
    * */
}
