package propulsar.pgb.presentationlayer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import propulsar.pgb.BuildConfig;
import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

public class Login extends AppCompatActivity implements
        WS.OnWSRequested, View.OnClickListener{

    EditText editTextCorreo;
    EditText editTextContra;
    TextInputLayout inputCorreo;
    TextInputLayout inputContra;
    View progress;
    View progressContra;
    View buttonLogin;
    View buttonContra;

    // ---------------------------------------- //
    // -------------- LIFECYCLE --------------- //
    // ---------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WS.getInstance(getApplicationContext());

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Login");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        buttonLogin = findViewById(R.id.buttonLoginNormal);
        progress=findViewById(R.id.progress);
        progressContra=findViewById(R.id.progressContra);
        buttonContra = findViewById(R.id.buttonContra);

        findViewById(R.id.fbLoginButton).setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        findViewById(R.id.buttonRegistrate).setOnClickListener(this);
        buttonContra.setOnClickListener(this);

        editTextCorreo = (EditText)findViewById(R.id.editTextCorreo);
        editTextContra = (EditText)findViewById(R.id.editTextContra);

        inputCorreo = (TextInputLayout)findViewById(R.id.inputLayoutCorreo);
        inputContra = (TextInputLayout)findViewById(R.id.inputLayoutContra);

        setInputTextLayoutColor(editTextCorreo, Color.WHITE);
    }

    public static void setInputTextLayoutColor(EditText editText, @ColorInt int color) {
        TextInputLayout til = (TextInputLayout) editText.getParentForAccessibility();
        try {
            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{ color }));

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(til, new ColorStateList(new int[][]{{0}}, new int[]{ color }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("WSDeb","ActivityResult");

        if(FacebookSdk.isFacebookRequestCode(requestCode)){
            WS.getInstance(getApplicationContext()).getCallbackManager().onActivityResult(requestCode, resultCode, data);
        }
    }

    // ------------------------------------------ //
    // -------------- OWN METHODS --------------- //
    // ------------------------------------------ //

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private int validateFields(){
        String correo = editTextCorreo.getEditableText().toString();
        String contra = editTextContra.getEditableText().toString();

        int error=0;

        if(correo.isEmpty() || !isValidEmail(correo)){
            inputCorreo.setError("Introduce un correo válido");
            error++;
        }else{ inputCorreo.setError(null); }

        if(contra.isEmpty()){
            inputContra.setError("Introduce tu contraseña");
            error++;
        }else if(contra.length()<6){
            inputContra.setError("Tu contraseña debe tener al menos 8 caracteres");
            error++;
        }else{ inputContra.setError(null); }

        return error;
    }

    private int validateContra(){
        String correo = editTextCorreo.getEditableText().toString();

        if(correo.isEmpty() || !isValidEmail(correo)){
            inputCorreo.setError("Introduce el correo del cual deseas recuperar tu contraseña");
            return 1;
        }else{ inputCorreo.setError(null); return 0;}
    }

    // ---------------------------------------------- //
    // -------------- OnClickListener --------------- //
    // ---------------------------------------------- //

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fbLoginButton:{
                findViewById(R.id.progressFacebook).setVisibility(View.VISIBLE);
                WS.loginFb(Login.this, Login.this);
                break;
            }

            case R.id.buttonLoginNormal:{
                Log.d("DEBRegister","CLICKED!");

                View someview = this.getCurrentFocus();
                if(someview!=null){
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(someview.getWindowToken(), 0);
                }

                String correo = editTextCorreo.getEditableText().toString();
                String contra = editTextContra.getEditableText().toString();

                if(validateFields()>0){
                    /*
                    Map<String, Object> params = new LinkedHashMap<>();
                    params.put("UserName","test@gmail.com");
                    params.put("Password","9hD4CD27");
                    WS.getInstance(Login.this).userSignIn(params,this);
                    */

                    //progress.setVisibility(View.VISIBLE);
                    //buttonLogin.setEnabled(false);

                    return;
                }

                progress.setVisibility(View.VISIBLE);
                buttonLogin.setEnabled(false);

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("UserName",correo);
                params.put("Password",contra);
                WS.getInstance(Login.this).userSignIn(params,this);

                break;
            }

            case R.id.buttonContra:{
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(Login.this, view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_contra, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(validateContra()==0){
                            progressContra.setVisibility(View.VISIBLE);
                            Map<String, Object> params = new LinkedHashMap<>();
                            params.put("Email",editTextCorreo.getEditableText().toString());
                            WS.recoverPassword(params,Login.this);
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
                break;
            }

            case R.id.buttonRegistrate:{
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    // ------------------------------------------- //
    // -------------- WEB SERVICES --------------- //
    // ------------------------------------------- //

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("WSDEB",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {

                case WS.WS_recoverPassword:{
                    JSONObject data = json.getJSONObject("data");
                    if(data.getInt("Updated")==1){
                        WS.showSucces("Se envió a tu correo la nueva contraseña",buttonLogin);
                        progressContra.setVisibility(View.GONE);
                    }else{
                        WS.showSucces("El correo ingresado no existe en la base de datos",buttonLogin);
                        progressContra.setVisibility(View.GONE);
                    }
                    break;
                }

                case WS.WS_userSignIn:{
                    JSONObject data = json.getJSONObject("data");

                    int authenticate = data.getInt("Authenticate");
                    if(authenticate!=1){
                        /*
                        switch(authenticate){
                            case -1:{
                                //No tiene contraseña
                                break;
                            }

                            case 0:{
                                WS.getInstance(Login.this).showMessage("Verifica que tu correo y contrseña sean correctos", Login.this);
                                break;
                            }
                        }
                        */

                        Snackbar snack=Snackbar.make(findViewById(R.id.toolbar), "Error al inicar sesión, verifica tus datos", Snackbar.LENGTH_LONG);
                        View snackBarView = snack.getView();
                        snackBarView.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorAccent));
                        snack.setAction("Action", null).show();
                        snack.show();
                        return;
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userID",data.getInt("UserId"));
                    Log.d("UserSignIn","UserSignIn = "+data.getInt("UserId"));
                    editor.putString("email",data.getString("Email"));
                    editor.putBoolean("loggedIn",true);
                    editor.commit();
                    String messageToShow=null;
                    try{
                        messageToShow=data.getString("Message");
                    }catch(Exception e){}
                    Toast.makeText(this, messageToShow==null ? getString(R.string.iniOKLogin) : messageToShow, Toast.LENGTH_SHORT).show();
                    Log.d("DEB DATA","id="+data.getInt("UserId")+" correo="+data.getString("Email"));
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    startActivity(intent);
                    break;
                }

                case WS.WS_registerFacebook:{

                    findViewById(R.id.progressFacebook).setVisibility(View.GONE);
                    JSONObject data = json.getJSONObject("data");
                    //Toast.makeText(Login.this, data.toString(), Toast.LENGTH_LONG).show();

                    if(data.getInt("UserId")<=0){
                        return;
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userID",data.getInt("UserId"));
                    Log.d("UserSignIn","UserSignIn = "+data.getInt("UserId"));
                    editor.putBoolean("loggedIn",true);
                    editor.commit();

                    Toast.makeText(this, data.getString("Message"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }catch(Exception e){e.printStackTrace();}
        finally {
            progress.setVisibility(View.GONE);
            buttonLogin.setEnabled(true);
        }

    }
}
