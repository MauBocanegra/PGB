package propulsar.qroo.PresentationLayer.Dialogs;

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
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;

import propulsar.qroo.DomainLayer.WS.WS;
import propulsar.qroo.R;

/**
 * Created by maubocanegra on 11/04/17.
 */


public class DiagShowImg extends AppCompatDialogFragment{

    View view;
    static String imgURL;
    static View.OnClickListener listenerEnviar;

    ImageView imagePreview;

    public static DiagShowImg newInstance(){
        DiagShowImg fragment = new DiagShowImg();
        return fragment;
    }

    public static void setImgURLandListener(String url, View.OnClickListener l){
        imgURL = url;
        listenerEnviar=l;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.alertdialog_confirm_image, container, false);
        Log.d("Y Existe?","existe? = "+imgURL);
        Glide
            .with(getContext())
            .load(new File(imgURL))
            .fitCenter()
            .into((ImageView)view.findViewById(R.id.imageToSendDialog));
        //Picasso.with(getContext()).load(new File(imgURL)).into((ImageView)view.findViewById(R.id.imageToSendDialog));

        view.findViewById(R.id.buttonCerrarDiag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.buttonEnviarDiag).setOnClickListener(listenerEnviar);
        return view;
    }

    // ------------------------------------------ //
    // -------------- OWN METHODS --------------- //
    // ------------------------------------------ //

    // ------------------------------------------- //
    // -------------- WEB SERVICES --------------- //
    // ------------------------------------------- //
}
