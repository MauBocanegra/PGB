package propulsar.pgb.presentationlayer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

public class RegisterActivity extends AppCompatActivity implements
        ViewSwitcher.ViewFactory,
        View.OnClickListener,
        WS.OnWSRequested{

    TextInputLayout inputCorreo;
    TextInputLayout inputContra;
    EditText editTextCorreo;
    EditText editTextContra;
    View buttonRegister;
    View progressButtonRegister;
    ImageSwitcher imageSwitcher;
    int visibilityInt=R.drawable.ic_visibility_off;

    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Registro");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputCorreo = (TextInputLayout)findViewById(R.id.inputLayoutCorreo);
        inputContra = (TextInputLayout)findViewById(R.id.inputLayoutContra);

        editTextCorreo = (EditText)findViewById(R.id.editTextCorreo);
        editTextContra = (EditText)findViewById(R.id.editTextContra);

        buttonRegister = findViewById(R.id.buttonRegister);
        progressButtonRegister = findViewById(R.id.progressButtonRegistrate);

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcherContra);
        imageSwitcher.setFactory(this);
        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);

        imageSwitcher.setImageDrawable(ContextCompat.getDrawable(RegisterActivity.this,R.drawable.ic_visibility_off));
        ImageView imageView = ((ImageView)imageSwitcher.getChildAt(imageSwitcher.getDisplayedChild()));
        DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(RegisterActivity.this, R.color.buttonGray));

        imageSwitcher.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
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
            inputContra.setError("Tu contraseña debe tener al menos 6 caracteres");
            error++;
        }else{ inputContra.setError(null); }

        return error;
    }

    // --------------------------------------------- //
    // -------------- IMAGE SWITCHER --------------- //
    // --------------------------------------------- //

    @Override
    public View makeView() {
        ImageView  imageView = new ImageView(RegisterActivity.this);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return imageView;
    }

    // --------------------------------------- //
    // -------------- ONCLICKS --------------- //
    // --------------------------------------- //

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imageSwitcherContra:{
                if(visibilityInt==R.drawable.ic_visibility_off){
                    int newIcon = R.drawable.ic_visibility_on;
                    imageSwitcher.setImageDrawable(ContextCompat.getDrawable(RegisterActivity.this,newIcon));
                    ImageView imageView = ((ImageView)imageSwitcher.getChildAt(imageSwitcher.getDisplayedChild()));
                    DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(RegisterActivity.this, R.color.buttonGray));
                    visibilityInt=newIcon;
                    editTextContra.setTransformationMethod(null);
                    editTextContra.setSelection(editTextContra.getText().length());
                }else{
                    int newIcon = R.drawable.ic_visibility_off;
                    imageSwitcher.setImageDrawable(ContextCompat.getDrawable(RegisterActivity.this,newIcon));
                    ImageView imageView = ((ImageView)imageSwitcher.getChildAt(imageSwitcher.getDisplayedChild()));
                    DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(RegisterActivity.this, R.color.buttonGray));
                    visibilityInt=newIcon;
                    editTextContra.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextContra.setSelection(editTextContra.getText().length());
                }
                break;
            }

            case R.id.buttonRegister:{

                Log.d("DEBRegister","CLICKED!");

                String correo = editTextCorreo.getEditableText().toString();
                String contra = editTextContra.getEditableText().toString();

                if(validateFields()>0){return;}

                progressButtonRegister.setVisibility(View.VISIBLE);
                buttonRegister.setEnabled(false);

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("UserName",correo);
                params.put("Password",contra);
                WS.userSignIn(params,this);

                /*
                //---ORIGINAL REGISTER
                params.put("Email",correo);
                params.put("Password",contra);
                WS.registerMail(params,this);
                */

                break;
            }
        }
    }

    // ------------------------------------------- //
    // -------------- WEB SERVICES --------------- //
    // ------------------------------------------- //


    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("RegisterActivity",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {

                case WS.WS_userSignIn:{
                    JSONObject data = json.getJSONObject("data");

                    int authenticate = data.getInt("Authenticate");
                    //boolean errorBool = data.getBoolean("error");
                    if(authenticate!=1){
                        /*
                        switch(authenticate){
                            case -1:{
                                //No tiene contraseña
                                break;
                            }

                            case 0:{
                                WS.showMessage("Verifica que tu correo y contrseña sean correctos", Login.this);
                                break;
                            }
                        }
                        */

                        Snackbar snack=Snackbar.make(findViewById(R.id.toolbar), "Error al iniciar sesión, verifica tus datos", Snackbar.LENGTH_LONG);
                        View snackBarView = snack.getView();
                        snackBarView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorAccent));
                        snack.setAction("Action", null).show();
                        snack.show();
                        return;
                    }

                    String correo = editTextCorreo.getEditableText().toString();
                    String contra = editTextContra.getEditableText().toString();
                    WS.loginOrRegisterFirebaseMail(correo, contra, RegisterActivity.this);

                    SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putInt("userID",data.getInt("UserId"));
                    Log.d("UserSignIn","UserSignIn = "+data.getInt("UserId"));
                    Log.d("UserSignIn","UserEmail = "+data.getString("Email"));
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

                case WS.WS_registerMail: {

                    JSONObject data = json.getJSONObject("data");

                    mTracker.send(
                            new HitBuilders.EventBuilder()
                            .setCategory("Usuarios").setAction("RegistroUsuario").setLabel("userID").setValue(data.getInt("UserId"))
                            .build()
                    );

                    if(!data.getBoolean("Success")) {
                        WS.showMessage(data.getString("ErrorMessage"), RegisterActivity.this);
                        return;
                    }

                    String correo = editTextCorreo.getEditableText().toString();
                    String contra = editTextContra.getEditableText().toString();

                    WS.loginOrRegisterFirebaseMail(correo, contra, RegisterActivity.this);

                    Toast.makeText(this, getString(R.string.iniOKRegistro), Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    /*
                    editor.putInt("userID",data.getInt("UserId"));
                    editor.putString("email",data.getString("Email"));
                    editor.putBoolean("loggedIn",data.getBoolean("Success"));
                    */
                    editor.putInt("userID",data.getInt("UserId"));
                    Log.d("UserSignIn","UserSignIn = "+data.getInt("UserId"));
                    editor.putString("email",data.getString("Email"));
                    editor.putBoolean("loggedIn",true);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    startActivity(intent);
                }
            }
        }catch(Exception e){e.printStackTrace();}
        finally {
            progressButtonRegister.setVisibility(View.GONE);
            buttonRegister.setEnabled(true);
        }
    }
}
