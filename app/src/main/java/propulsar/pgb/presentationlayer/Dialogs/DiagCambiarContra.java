package propulsar.pgb.presentationlayer.Dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

/**
 * Created by maubocanegra on 11/04/17.
 */


public class DiagCambiarContra extends AppCompatDialogFragment implements ViewSwitcher.ViewFactory, WS.OnWSRequested{

    View view;

    ImageSwitcher imageSwitcher;
    ImageSwitcher imageSwitcherNueva;
    int visibilityInt=R.drawable.ic_visibility_off;
    EditText editTextContra;
    TextInputLayout inputContra;
    EditText editTextContraNueva;
    TextInputLayout inputContraNueva;
    View buttonCambiarContra;
    ProgressBar progressButtonCambiarContra;
    public String email;

    public static DiagCambiarContra newInstance(){
        DiagCambiarContra fragment = new DiagCambiarContra();
        return fragment;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.diag_cambiar_contra, container, false);

        editTextContra = (EditText)view.findViewById(R.id.editTextContra);
        inputContra = (TextInputLayout)view.findViewById(R.id.inputLayoutContra);
        editTextContraNueva = (EditText)view.findViewById(R.id.editTextContraNueva);
        inputContraNueva = (TextInputLayout)view.findViewById(R.id.inputLayoutContraNueva);
        buttonCambiarContra = view.findViewById(R.id.buttonCambiarContra);
        progressButtonCambiarContra = (ProgressBar)view.findViewById(R.id.progressButtonCambiarContra);

        Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);

        imageSwitcher = (ImageSwitcher)view.findViewById(R.id.imageSwitcherContra);
        imageSwitcher.setFactory(this);
        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);
        imageSwitcher.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_visibility_off));
        ImageView imageView = ((ImageView)imageSwitcher.getChildAt(imageSwitcher.getDisplayedChild()));

        imageSwitcherNueva = (ImageSwitcher)view.findViewById(R.id.imageSwitcherContraNueva);
        imageSwitcherNueva.setFactory(this);
        imageSwitcherNueva.setInAnimation(in);
        imageSwitcherNueva.setOutAnimation(out);
        imageSwitcherNueva.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_visibility_off));
        ImageView imageViewNueva = ((ImageView)imageSwitcherNueva.getChildAt(imageSwitcherNueva.getDisplayedChild()));


        DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(getContext(), R.color.buttonGray));
        DrawableCompat.setTint(imageViewNueva.getDrawable(),ContextCompat.getColor(getContext(), R.color.buttonGray));

        imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(visibilityInt==R.drawable.ic_visibility_off){
                    int newIcon = R.drawable.ic_visibility_on;
                    imageSwitcher.setImageDrawable(ContextCompat.getDrawable(getContext(),newIcon));
                    ImageView imageView = ((ImageView)imageSwitcher.getChildAt(imageSwitcher.getDisplayedChild()));
                    DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(getContext(), R.color.buttonGray));
                    visibilityInt=newIcon;
                    editTextContra.setTransformationMethod(null);
                    editTextContra.setSelection(editTextContra.getText().length());
                }else{
                    int newIcon = R.drawable.ic_visibility_off;
                    imageSwitcher.setImageDrawable(ContextCompat.getDrawable(getContext(),newIcon));
                    ImageView imageView = ((ImageView)imageSwitcher.getChildAt(imageSwitcher.getDisplayedChild()));
                    DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(getContext(), R.color.buttonGray));
                    visibilityInt=newIcon;
                    editTextContra.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextContra.setSelection(editTextContra.getText().length());
                }
            }
        });

        imageSwitcherNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(visibilityInt==R.drawable.ic_visibility_off){
                    int newIcon = R.drawable.ic_visibility_on;
                    imageSwitcherNueva.setImageDrawable(ContextCompat.getDrawable(getContext(),newIcon));
                    ImageView imageView = ((ImageView)imageSwitcherNueva.getChildAt(imageSwitcherNueva.getDisplayedChild()));
                    DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(getContext(), R.color.buttonGray));
                    visibilityInt=newIcon;
                    editTextContraNueva.setTransformationMethod(null);
                    editTextContraNueva.setSelection(editTextContraNueva.getText().length());
                }else{
                    int newIcon = R.drawable.ic_visibility_off;
                    imageSwitcherNueva.setImageDrawable(ContextCompat.getDrawable(getContext(),newIcon));
                    ImageView imageView = ((ImageView)imageSwitcherNueva.getChildAt(imageSwitcherNueva.getDisplayedChild()));
                    DrawableCompat.setTint(imageView.getDrawable(),ContextCompat.getColor(getContext(), R.color.buttonGray));
                    visibilityInt=newIcon;
                    editTextContraNueva.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextContraNueva.setSelection(editTextContraNueva.getText().length());
                }
            }
        });

        view.findViewById(R.id.buttonCambiarContra).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(validateFields()>0){return;}
                progressButtonCambiarContra.setVisibility(View.VISIBLE);

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("Email",email);
                params.put("Password",editTextContra.getEditableText().toString());
                params.put("NewPassword",editTextContraNueva.getEditableText().toString());

                WS.updatePassword(params, DiagCambiarContra.this);
            }
        });

        return view;
    }

    // --------------------------------------------- //
    // -------------- IMAGE SWITCHER --------------- //
    // --------------------------------------------- //

    @Override
    public View makeView() {
        ImageView  imageView = new ImageView(getContext());
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return imageView;
    }

    // ------------------------------------------ //
    // -------------- OWN METHODS --------------- //
    // ------------------------------------------ //

    private int validateFields(){
        String contra = editTextContra.getEditableText().toString();
        String contraNueva = editTextContraNueva.getEditableText().toString();

        int error=0;

        if(contra.isEmpty()){
            inputContra.setError("Introduce tu contraseña");
            error++;
        }else if(contra.length()<6){
            inputContra.setError("Tu contraseña debe tener al menos 6 caracteres");
            error++;
        }else{ inputContra.setError(null); }

        if(contraNueva.isEmpty()){
            inputContraNueva.setError("Introduce la contraseña nueva");
            error++;
        }else if(contra.length()<6){
            inputContraNueva.setError("Tu contraseña nueva debe tener al menos 6 caracteres");
            error++;
        }else{ inputContraNueva.setError(null); }

        return error;
    }

    public void setEmail (String email_){
        this.email = email_;
    }

    // ------------------------------------------- //
    // -------------- WEB SERVICES --------------- //
    // ------------------------------------------- //


    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("UpdatePassword",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_updatePassword: {
                    JSONObject data = json.getJSONObject("data");
                    int success = data.getInt("Successful");
                    if(success==1){
                        Toast.makeText(getActivity(), "¡Contraseña actualizada!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }else{
                        Toast.makeText(getActivity(), "Error al actualizar la contraseña, verifica que tus datos sean correctos", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
        finally {
            progressButtonCambiarContra.setVisibility(View.GONE);
            buttonCambiarContra.setEnabled(true);
        }
    }
}
