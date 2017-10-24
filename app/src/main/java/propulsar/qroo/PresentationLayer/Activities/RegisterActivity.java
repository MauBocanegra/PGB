package propulsar.qroo.PresentationLayer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import propulsar.qroo.DomainLayer.Objects.AnalyticsApplication;
import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
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
                params.put("Email",correo);
                params.put("Password",contra);
                WS.getInstance(RegisterActivity.this).registerMail(params,this);

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
                case WS.WS_registerMail: {
                    JSONObject data = json.getJSONObject("data");

                    if(!data.getBoolean("Success")) {
                        WS.getInstance(RegisterActivity.this).showMessage(data.getString("ErrorMessage"), RegisterActivity.this);
                        return;
                    }

                    Toast.makeText(this, getString(R.string.iniOKRegistro), Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("userID",data.getInt("UserId"));
                    editor.putString("email",data.getString("Email"));
                    editor.putBoolean("loggedIn",data.getBoolean("Success"));
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
