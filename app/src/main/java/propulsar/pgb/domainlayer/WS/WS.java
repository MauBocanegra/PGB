package propulsar.pgb.domainlayer.WS;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.R;
import propulsar.pgb.domainlayer.firebase_objects.Mensaje_FirebaseObj;
import propulsar.pgb.domainlayer.firebase_objects.Usuario_FirebaseObj;
import propulsar.pgb.presentationlayer.activities.Login;

/**
 * Created by maubocanegra on 03/02/17.
 */

public class WS {
    private static WS instance;
    private static CallbackManager callbackManager;
    private static String email;

    public static Context context;
    //public static OnWSRequested provitionalListener;
    public static OnWSRequested facebookListener;
    private static HttpURLConnection con = null;

    private static Activity loginActivity;

    private static int userID=-1;

    private static FirebaseAuth mAuth;
    private static FirebaseUser firebaseUser;
    private static DatabaseReference mDatabase;
    private static DatabaseReference.CompletionListener mCompletionListener;

    private static final String USUARIOS = "usuarios";
    private static final String MENSAJES = "mensajes";

    private static AsyncTask<Void,Void,JSONObject> async;

    public synchronized static WS getInstance(Context c){
        if(instance==null){

            WS_URL = c.getString(R.string.ServicesLink);
            instance = new WS();
            context=c;
            mAuth = FirebaseAuth.getInstance();
            Log.d("fbLog","initFb");
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,
                getFbCallback()
            );

            FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
            fbdb.setPersistenceEnabled(false);
            mDatabase = fbdb.getReference();
        }
        return instance;
    }

    public static CallbackManager getCallbackManager(){
        return callbackManager;
    }

    public static void loginFb(Activity activity, OnWSRequested listener){

        loginActivity = activity;
        Log.d("WSDeb","pressedLogin");
        facebookListener=listener;
        AccessToken token = AccessToken.getCurrentAccessToken();
        /*
        if(token==null){
            Log.d("WSDeb","RequestedLogin");
            LoginManager.getInstance().logInWithReadPermissions(
                    activity,
                    Arrays.asList("email")
            );
        }else{
            Log.d("FBDeb","Logout!");
            LoginManager.getInstance().logOut();
        }
        */
        Log.d("WSDeb","RequestedLogin");
        LoginManager.getInstance().logInWithReadPermissions(
                activity,
                Arrays.asList("email")
        );
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

    public static void loginOrRegisterFirebaseMail(final String email, final String password, final Activity activity){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthException) {
                            String errCode = ((FirebaseAuthException) e).getErrorCode();
                            if(errCode.compareTo("ERROR_USER_NOT_FOUND")==0){
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d("FirebaseDebug", "createUserWithEmail:success");
                                                    firebaseUser = mAuth.getCurrentUser();
                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w("FirebaseDebug", "createUserWithEmail:failure", task.getException());
                                                }
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FirebaseDebug", "signInWithEmail:success");
                            firebaseUser = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                Log.w("FirebaseDebug", "signInWithEmail:failure", task.getException());
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        // ...
                    }
                });
    }

    // ------------------------------------------- //
    // -------------- WEB SERVICES --------------- //
    // ------------------------------------------- //

    private static void userSignIn(Map<String,Object> params){
        Log.d("userSignIn"," ----- userSignInRequested ----- ");
        String urlString = WS_URL+WS_registerFacebookURL;
        performRequest(urlString, WS_registerFacebook, params, POSTID, facebookListener);
    }

    public static void userSignIn(Map<String,Object> params, OnWSRequested listener){
        Log.d("userSignIn"," ----- userSignInRequested ----- ");
        //String urlString = "http://svc.procivica.com/api/"+WS_userLogin;
        String urlString = WS_URL+WS_userSignInURL;
        performRequest(urlString, WS_userSignIn, params, POSTID, listener);
    }

    public static void getMenu(Map<String,Object> params, OnWSRequested listener){
        Log.d("menuReq"," ----- menuRequested ----- ");
        String urlString = WS_URL+WS_getMenuURL;
        performRequest(urlString, WS_getMenu, params, GETID, listener);
    }

    public static void getEventDetails(Map<String,Object> params, OnWSRequested listener){
        Log.d("eventDetails"," ----- eventDetailsRequested ----- ");
        String urlString = WS_URL+WS_getEventDetailsURL;
        performRequest(urlString, WS_getEventDetails, params, GETID, listener);
    }

    public static void getUserProfile(Map<String,Object> params, OnWSRequested listener){
        Log.d("userProfile"," ----- userProfileRequested ----- ");
        String urlString = WS_URL+WS_getUserProfileURL;
        performRequest(urlString, WS_getUserProfile, params, GETID, listener);
    }

    public static void getProposalDetail(Map<String,Object> params, OnWSRequested listener){
        Log.d("proposalDetail"," ----- proposalDetailRequested ----- ");
        String urlString = WS_URL+WS_getProposalDetailURL;
        performRequest(urlString, WS_getProposalDetail, params, GETID, listener);
    }

    public static void getVotedProposals(Map<String,Object> params, OnWSRequested listener){
        Log.d("votedProposals"," ----- votedProposalsRequested ----- ");
        String urlString = WS_URL+WS_getVotedProposalsURL;
        performRequest(urlString, WS_getVotedProposals, params, GETID, listener);
    }

    public static void getPendingProposals(Map<String,Object> params, OnWSRequested listener){
        Log.d("pendingProposals"," ----- pendingProposalsRequested ----- ");
        String urlString = WS_URL+WS_getPendingProposalsURL;
        performRequest(urlString,WS_getPendingProposals,params,GETID, listener);
    }

    public static void getNotifs(Map<String,Object> params, OnWSRequested listener){
        Log.d("getNotifs", " ----- getNotifsRequested ----- ");
        String urlString = WS_URL+WS_getNotifsURL;
        performRequest(urlString, WS_getNotifs, params, GETID, listener);
    }

    public static void getCasesList(Map<String,Object> params, OnWSRequested listener){
        Log.d("getCasesList", " ----- getCasesListRequested ----- ");
        String urlString = WS_URL+WS_getCasesURL;
        performRequest(urlString, WS_getCases, params, GETID, listener);
    }

    public static void getCaseDetail(Map<String,Object> params, OnWSRequested listener){
        Log.d("getCaseDetail", " ----- getCaseDetailRequested ----- ");
        String urlString = WS_URL+WS_getCaseDetailURL;
        performRequest(urlString, WS_getCaseDetail, params, GETID, listener);
    }

    public static void getCaseByFolio(Map<String,Object> params, OnWSRequested listener){
        Log.d("getCaseByFolio", " ----- getCaseByFolioRequested ----- ");
        String urlString = WS_URL+WS_getCaseByFolioURL;
        performRequest(urlString, WS_getCaseByFolio, params, GETID, listener);
    }

    public static void getBenefitsList(Map<String,Object> params, OnWSRequested listener){
        Log.d("getBenefitsList", " ----- getBenefitsListRequested ----- ");
        String urlString = WS_URL+WS_getBenefitsListURL;
        performRequest(urlString, WS_getBenefitsList, params, GETID, listener);
    }

    public static void getBenefitDetails(Map<String,Object> params, OnWSRequested listener){
        Log.d("getBenefitDetails", " ----- getBenefitDetailsRequested ----- ");
        String urlString = WS_URL+WS_getBenefitDetailsURL;
        performRequest(urlString, WS_getBenefitDetails, params, GETID, listener);
    }

    public static void getMessages(Map<String,Object> params, OnWSRequested listener){
        Log.d("getMesssages", " ----- getMessagesRequested ----- ");
        String urlString = WS_URL+WS_getMessagesURL;
        performRequest(urlString, WS_getMessages, params, GETID, listener);
    }

    public static void sendMessage(Map<String,Object> params, OnWSRequested listener){
        Log.d("sendMesssage", " ----- sendMessageRequested ----- ");
        String urlString = WS_URL+WS_sendMessageURL;
        performRequest(urlString, WS_sendMessage, params, POSTID, listener);
    }

    public static void createProposal(Map<String,Object> params, OnWSRequested listener){
        Log.d("createProposal", " ----- createProposalRequested ----- ");
        String urlString = WS_URL+WS_createProposalURL;
        performRequest(urlString, WS_createProposal, params, POSTID, listener);
    }

    public static void registerMail(Map<String,Object> params, OnWSRequested listener){
        Log.d("registerMail", " ----- registerMailRequested ----- ");
        String urlString = WS_URL+WS_registerMailURL;
        performRequest(urlString, WS_registerMail, params, POSTID, listener);
    }

    public static void getEvents(Map<String,Object> params, OnWSRequested listener){
        Log.d("getEvents", " ----- getEventsRequested ----- ");
        String urlString = WS_URL+WS_getEventsURL;
        performRequest(urlString, WS_getEvents, params, GETID, listener);
    }

    public static void getSurveyDetail(Map<String,Object> params, OnWSRequested listener){
        Log.d("getSurvey", " ----- getSurveyRequested ----- ");
        String urlString = WS_URL+WS_getSurveyURL;
        performRequest(urlString, WS_getSurvey, params, GETID, listener);
    }

    public static void answerSurveyQuestion(Map<String,Object> params, OnWSRequested listener){
        Log.d("surveyQuestion", " ----- surveyQuestionRequested ----- ");
        String urlString = WS_URL+WS_answerSurveyURL;
        performRequest(urlString, WS_answerSurvey, params, POSTID, listener);
    }

    public static void voteProposal(Map<String,Object> params, OnWSRequested listener){
        Log.d("voteProposal", " ----- voteProposalRequested ----- ");
        String urlString = WS_URL+WS_voteProposalURL;
        performRequest(urlString, WS_voteProposal, params, POSTID, listener);
    }

    public static void saveProfile(Map<String,Object> params, OnWSRequested listener){
        Log.d("saveProfile", " ----- saveProfileRequested ----- ");
        String urlString = WS_URL+WS_saveProfileURL;
        performRequest(urlString, WS_saveProfile, params, POSTID, listener);
    }

    public static void getAboutHTML(Map<String,Object> params, OnWSRequested listener){
        Log.d("getAbout", " ----- getAboutRequested ----- ");
        String urlString = WS_URL+WS_getAboutURL;
        performRequest(urlString, WS_getAbout, params, GETID, listener);
    }

    public static void recoverPassword(Map<String,Object> params, OnWSRequested listener){
        Log.d("recoverPassword", " ----- recoverPasswordRequested ----- ");
        String urlString = WS_URL+WS_recoverPasswordURL;
        performRequest(urlString, WS_recoverPassword, params, POSTID, listener);
    }

    public static void updatePassword(Map<String,Object> params, OnWSRequested listener){
        Log.d("updatePassword", " ----- updatePasswordRequested ----- ");
        String urlString = WS_URL+WS_updatePasswordURL;
        performRequest(urlString, WS_updatePassword, params, POSTID, listener);
    }

    public static void getOfficerInfo(Map<String,Object> params, OnWSRequested listener){
        Log.d("getOfficerInfo", " ----- getOfficerInfo ----- ");
        String urlString = WS_URL+WS_getOfficerInfoURL;
        performRequest(urlString, WS_getOfficerInfo, params, GETID, listener);
    }

    public static void requestToken(Map<String,Object> params, OnWSRequested listener){
        Log.d("requestToken", " ----- requestTokenRequested ----- ");
        //String urlString = "http://svc.procivica.com/api/"+WS_getBotTokenURL;
        String urlString = WS.WS_URL+WS_getBotTokenURL;
        performRequest(urlString,  WS_getBotToken, params, GETID, listener);
    }

    //


    // ------------------------------------------- //
    // ------------- WEB IMPLEMENTS -------------- //
    // ------------------------------------------- //

    private static class Task extends AsyncTask<Void,Void,JSONObject>{
        final String urlString;
        final int wsReq;
        final Map<String,Object> params;
        final int postGet;
        final OnWSRequested provListener;

        public Task(final String urlString_, final int wsReq_, final Map<String,Object> params_, final int postGet_, final OnWSRequested provListener_){
            urlString = urlString_;
            wsReq = wsReq_;
            params = params_;
            postGet = postGet_;
            provListener = provListener_;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try{
                URL url = new URL(urlString);
                if(postGet==MULTIPARTID){
                    String restURL="";
                    for (Map.Entry<String,Object> param : params.entrySet()) {
                        if (param.getKey().equals("link")) {
                            restURL = param.getValue().toString();
                        }
                    }
                    url = new URL(urlString+"/"+restURL);
                    url = new URL(WS.WS_URL+"Message/SendImageMessages");
                    Log.d("urlString","FINALSTRING = "+url.toString());
                }
                StringBuilder postData = new StringBuilder();
                if(postGet!=MULTIPARTID)
                    for (Map.Entry<String,Object> param : params.entrySet()) {
                        Log.d("jsonData","length = "+postData.length());
                        if (postData.length() != 0) {
                            postData.append('&');
                        }

                        if (postData.length() == 0 && postGet==GETID){postData.append('?');}
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                Log.d("WSDeb","postData.toString()="+urlString+" postData = "+postData.toString());
                if(postGet==GETID){
                    url = new URL(urlString.concat(postData.toString()));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");
                con = (HttpURLConnection)url.openConnection();
                if(postGet==POSTID) {
                    Log.d("method","POST METHOD");
                    con.setRequestMethod("POST");
                }else if(postGet==GETID){
                    Log.d("method","GET METHOD");
                    con.setRequestMethod("GET");
                }
                con.setConnectTimeout(10 * 1000);
                if(postGet!=MULTIPARTID)
                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                else{
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Cache-Control", "no-cache");

                    con.setRequestProperty(
                            "Content-Type", "multipart/form-data;boundary=" + "*****");
                    DataOutputStream request = new DataOutputStream(con.getOutputStream());

                        /*
                        params:
                        link
                        name
                        namefull
                        image
                        * */
                    String attName=""; String attFileName="";
                    Bitmap bitmap=null;
                    for (Map.Entry<String,Object> param : params.entrySet()){
                        if(param.getKey().equals("name")){attName=param.getValue().toString();}
                        if(param.getKey().equals("namefull")){attFileName=param.getValue().toString();}
                        if(param.getKey().equals("image")){bitmap = (Bitmap)param.getValue();}
                    }
                    request.writeBytes("--"/*twohyphens*/ + "*****"/*boundary*/ + "\r\n"/*crlf*/);
                    request.writeBytes("Content-Disposition: form-data; name=\"" +
                            attName + "\";filename=\"" +
                            attFileName + "\"" + "\r\n"/*crlf*/);
                    request.writeBytes("\r\n"/*crlf*/);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    request.writeBytes("\r\n"/*crlf*/);
                    request.writeBytes("--"/*twohyphens*/ + "*****"/*boundary*/ +
                            "--"/*twohyphens*/ + "\r\n"/*crlf*/);
                    request.flush();
                    request.close();
                }
                if(postGet==POSTID)
                    con.setRequestProperty("Content-Length", postDataBytes.length + "");
                if(postGet!=MULTIPARTID) {
                    con.setDoInput(true);
                    con.setUseCaches(false);
                }
                if(postGet==POSTID) {
                    con.setDoOutput(true);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postDataBytes);
                    outputStream.close();
                }
                int statusCode = con.getResponseCode();

                if(statusCode==200) {
                    Log.d("WSDEB","RETURNED SUCCESFUL");
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
                    jsonRes.put("ws",wsReq);
                    jsonRes.put("data",json);
                    //createCache(wsReq,jsonRes);

                    //Toast.makeText(context, json.toString(), Toast.LENGTH_SHORT).show();

                    return jsonRes;
                }
                else{
                    Log.d("WSDEB","RETURNED FAILURE");
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
                    jsonRes.put("ws",wsReq);
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
                try {
                    e.printStackTrace();
                    JSONObject jsonRes = new JSONObject();
                    jsonRes.put("status", -1);
                    jsonRes.put("ws", wsReq);
                    jsonRes.put("data", null);
                    return jsonRes;
                }catch(Exception ex){}
                return null;
            }
        }

        @Override
        protected void onPostExecute(final JSONObject jsonRes) {
            super.onPostExecute(jsonRes);
            if(jsonRes!=null){
                //Toast.makeText(context,jsonRes.toString(), Toast.LENGTH_LONG).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        provListener.wsAnswered(jsonRes);
                        //async.execute();
                    }
                }, 1);
            }
        }
    }


    private static JSONObject performRequest(final String urlString, final int wsReq, final Map<String,Object> params, final int postGet, final OnWSRequested provListener){
        async = new Task(urlString,wsReq,params,postGet,provListener);
        async.execute();

        return null;
    }



    // ------------------------------------------- //
    // -------------- SOCIAL BACKEND ------------- //
    // ------------------------------------------- //

    private static FacebookCallback<LoginResult> getFbCallback(){
        Log.d("WSDeb","getFbCallback");
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("CallbackLog","onSuccess");
                final AccessToken token = AccessToken.getCurrentAccessToken();

                try {
                    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("FirebaseDebug", "signInWithCredential:success");
                                        firebaseUser = mAuth.getCurrentUser();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("FirebaseDebug", "signInWithCredential:failure", task.getException());

                                    }

                                    // ...
                                }
                            });
                }catch(Exception e){e.printStackTrace();}

                GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject fbJsonObj, GraphResponse response) {
                        try {

                            //Log.d("FBDEB",response.toString());
                            Map<String, Object> params = new LinkedHashMap<>();
                            //params.put("UserName","test@gmail.com");
                            //params.put("Password","9hD4CD27");

                            params.put("Token",fbJsonObj.getString("email"));
                            params.put("FacebookId", token.getUserId());
                            //params.put("UserName",fbJsonObj.getString("first_name")+" "+fbJsonObj.getString("last_name"));
                            /*
                            params.put("fbID", token.getUserId());
                            params.put("fbName", fbJsonObj.getString("first_name"));
                            params.put("fbSurname", fbJsonObj.getString("last_name"));
                            params.put("fbToken", token.getToken());
                            email = fbJsonObj.getString("email");
                            params.put("fbEmail", fbJsonObj.getString("email"));
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dateFormat.format(token.getExpires());
                            params.put("fbTokenExpDate", dateFormat.format(token.getExpires()));
                            */
                            userSignIn(params);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,first_name, last_name, location"); // Parámetros que pedimos a facebook
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d("CallbackLog","onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("CallbackLog","onError + "+error.toString());
            }
        };
    }

    // ------------------------------------------- //
    // -------------- SHOW MESSAGE --------------- //
    // ------------------------------------------- //

    public static void showMessage(String message, Activity activity){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setTitle(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void showSucces(String msg, View view){

        Snackbar snack=Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snack.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
        snack.setAction("Action", null).show();
        snack.show();

        /*
        Snackbar snackbar = Snackbar
                .make(view, "¡Perfil guardado exitósamente!", Snackbar.LENGTH_SHORT);
        snackbar.show();
        */
    }

    public static void showError(String msg, View view){

        Snackbar snack=Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snack.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.buttonRed));
        snack.setAction("Action", null).show();
        snack.show();

        /*
        Snackbar snackbar = Snackbar
                .make(view, "¡Perfil guardado exitósamente!", Snackbar.LENGTH_SHORT);
        snackbar.show();
        */
    }

    public static int getUserID() {
        return userID;
    }

    public static void setUserID(int userID) {
        WS.userID = userID;
    }

    // ------------------------------------------- //
    // -------------- FIREBASE METHODS --------------- //
    // ------------------------------------------- //

    public static void mandarMensajeFirebase(final int userIDToDownloadChats, Usuario_FirebaseObj usuario, final Mensaje_FirebaseObj mensaje, final FirebaseCompletionListener firebaseCompletionListener_){
        if(mDatabase==null){
            Log.e("ERROR","mDatabase IS NULL"); return;
        }

        mDatabase.child(USUARIOS).child(""+userIDToDownloadChats).child("lastTimestamp").setValue(usuario.getLastTimestamp());
        mDatabase.child(USUARIOS).child(""+userIDToDownloadChats).child("ultimoMensaje").setValue(mensaje);
        mDatabase.child(USUARIOS).child(""+userIDToDownloadChats).child("user").setValue(userIDToDownloadChats);
        mDatabase.child(USUARIOS).child(""+userIDToDownloadChats).child("nombre").setValue(usuario.getNombre());
        mDatabase.child(MENSAJES).child(""+ userIDToDownloadChats).push().setValue(mensaje, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                firebaseCompletionListener_.firebaseCompleted(true);
            }
        });
    }

    public static void setReaderListener(ChildEventListener childEventListener, int userIDToDownloadChats){
        mDatabase.child(MENSAJES).child(""+userIDToDownloadChats).addChildEventListener(childEventListener);
    }

    public static void removeReaderListener(ChildEventListener childEventListener){
        mDatabase.child(MENSAJES).removeEventListener(childEventListener);
    }

    public static void setReaderChatsListener(ChildEventListener childEventListener){
        mDatabase.child(USUARIOS).orderByChild("lastTimestamp").addChildEventListener(childEventListener);
    }

    public static void removeReaderChatsListener(ChildEventListener childEventListener){
        mDatabase.child(USUARIOS).removeEventListener(childEventListener);
    }

    // ------------------------------------------- //
    // -------------- OWN LISTENER --------------- //
    // ------------------------------------------- //

    public interface FirebaseCompletionListener{
        public void firebaseCompleted(boolean hasError);
    }

    public interface OnWSRequested{
        public void wsAnswered(JSONObject json);
    }

    //public static final String WS_URL = "http://testsvcyonayarit.iog.digital/api/";
    //public static final String WS_URL = "http://svcyonayarit.iog.digital/api/";
    //public static final String WS_TEST_URL = "http://testsvcyonayarit.iog.digital/api/";

    public static String WS_URL;
    //public static final String WS_URL = "http://svcjala.iog.digital/api/";

    public static final int WS_userSignIn = 100;
    public static final int WS_getMenu = 200;
    public static final int WS_getEventDetails = 300;
    public static final int WS_getUserProfile = 400;
    public static final int WS_getProposalDetail = 500;
    public static final int WS_getVotedProposals = 600;
    public static final int WS_getPendingProposals = 700;
    public static final int WS_getNotifs = 800;
    public static final int WS_getCases = 900;
    public static final int WS_getCaseDetail = 1500;
    public static final int WS_getBenefitsList = 1000;
    public static final int WS_getBenefitDetails = 1100;
    public static final int WS_getMessages=1200;
    public static final int WS_sendMessage=1300;
    public static final int WS_createProposal=1400;
    public static final int WS_registerMail = 1600;
    public static final int WS_getEvents=1700;
    public static final int WS_getSurvey=1800;
    public static final int WS_answerSurvey=1900;
    public static final int WS_voteProposal=2000;
    public static final int WS_saveProfile=2100;
    public static final int WS_registerFacebook=2200;
    public static final int WS_getAbout=2300;
    public static final int WS_updatePassword=2400;
    public static final int WS_recoverPassword=2500;
    public static final int WS_uploadPhoto=2600;
    public static final int WS_getOfficerInfo=2700;
    public static final int WS_getBotToken=2800;
    public static final int WS_getCaseByFolio=2900;

    private static final int GETID = 10;
    private static final int POSTID = 11;
    private static final int MULTIPARTID = 12;

    public static final String WS_userLogin = "Login";

    public static final String WS_userSignInURL = "UserSession/UserSignin";
    public static final String WS_getMenuURL = "User/GetHome";
    public static final String WS_getEventDetailsURL = "Event/GetEventDetails";
    public static final String WS_getUserProfileURL = "User/GetUser";
    public static final String WS_getProposalDetailURL = "Proposal/GetProposalDetails";
    public static final String WS_getVotedProposalsURL = "Proposal/GetVotedProposalsList2";
    public static final String WS_getPendingProposalsURL = "Proposal/GetPendingProposalsList";
    public static final String WS_getNotifsURL = "Notifications/GetNotifications";
    public static final String WS_getCasesURL= "Complaint/GetCasesList";
    public static final String WS_getBenefitsListURL = "User/GetBenefitsList";
    public static final String WS_getBenefitDetailsURL = "User/GetBenefitDetails";
    public static final String WS_getMessagesURL="Message/GetMessages";
    public static final String WS_sendMessageURL="Message/SendMessages";
    public static final String WS_createProposalURL="Proposal/CreateProposal";
    public static final String WS_getCaseDetailURL ="Complaint/GetComplaintDetail";
    public static final String WS_registerMailURL="User/UserRegistrationWithEmail ";
    public static final String WS_getEventsURL="Event/GetEvents";
    public static final String WS_getSurveyURL="Survey/GetSurveyDetails";
    public static final String WS_answerSurveyURL = "Survey/AnswerSurvey";
    public static final String WS_voteProposalURL = "Proposal/VoteProposal";
    public static final String WS_saveProfileURL = "User/SaveUserProfile";
    public static final String WS_registerFacebookURL = "User/UserRegistrationWithFaceboook";
    public static final String WS_getAboutURL = "Site/GetSiteConfiguration?Name=about";
    public static final String WS_updatePasswordURL = "User/ChangeUserPassword";
    public static final String WS_recoverPasswordURL = "User/RecoveryPassword";
    public static final String WS_getOfficerInfoURL = "User/GetBasicProfileOfficial";
    public static final String WS_getBotTokenURL = "Site/GetSiteConfiguration?Name=directLine";
    public static final String WS_getCaseByFolioURL = "Complaint/GetComplaintDetailWithFolio";

    //Usuarios = 1  ciudadano
}
