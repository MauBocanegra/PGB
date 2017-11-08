package propulsar.pgb.presentationlayer.Frags;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.Services.RegistrationIntentService;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.presentationlayer.activities.Login;
import propulsar.pgb.presentationlayer.activities.WebViewActivity;
import propulsar.pgb.presentationlayer.Dialogs.DiagCambiarContra;
import propulsar.pgb.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFrag extends Fragment implements WS.OnWSRequested, View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View fragView;
    int userID;

    EditText editTextNombre;
    EditText editTextCorreo;
    EditText editTextEdad;
    EditText editTextResid;
    EditText editTextProfesion;
    Spinner spinnerGenero;
    //SwitchCompat switchNotifs;
    View progress;

    GoogleApiClient mGoogleApiClient;
    double lat=0.0; double lon=0.0;

    public PerfilFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFrag newInstance(String param1, String param2) {
        PerfilFrag fragment = new PerfilFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static PerfilFrag newInstance() {
        PerfilFrag fragment = new PerfilFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        */

        /*
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                    mGoogleApiClient);
                            if (mLastLocation != null) {
                                mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                                mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
        */

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("UserId",userID);
        Log.d("PERFILDEBUG","userID="+userID);
        WS.getInstance(getActivity()).getUserProfile(params, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_perfil, container, false);
        fragView.findViewById(R.id.perfilButtonCerrarSesion).setOnClickListener(this);
        fragView.findViewById(R.id.perfilButtonGuardar).setOnClickListener(this);
        fragView.findViewById(R.id.perfilButtonContra).setOnClickListener(this);
        editTextNombre = ((EditText) fragView.findViewById(R.id.perfil_editText_nombre));
        editTextCorreo = ((EditText) fragView.findViewById(R.id.perfil_editText_correo));
        editTextEdad = ((EditText) fragView.findViewById(R.id.perfil_editText_edad));
        //editTextGenero = ((EditText)fragView.findViewById(R.id.perfil_editText_genero));
        editTextResid = ((EditText) fragView.findViewById(R.id.perfil_editText_resid));
        editTextProfesion = ((EditText) fragView.findViewById(R.id.perfil_editText_profesion));
        spinnerGenero = (Spinner)fragView.findViewById(R.id.spinnerGenero);
        //switchNotifs = ((SwitchCompat)fragView.findViewById(R.id.switchNotifs));
        progress = fragView.findViewById(R.id.perfilProgress);
        fragView.findViewById(R.id.buttonAcercade).setOnClickListener(this);
        return fragView;
    }

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("WSDEB",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_getUserProfile:{
                    setProfileInfo(json.getJSONObject("data"));
                    break;
                }

                case WS.WS_saveProfile:{
                    WS.getInstance(getContext()).showSucces("¡Perfil guardado exitosamente!",fragView);
                    progress.setVisibility(View.GONE);
                    break;
                }
            }
        }catch(Exception e){e.printStackTrace();}
        finally {
            progress.setVisibility(View.GONE);
        }
    }

    private void setProfileInfo(JSONObject data){
        try {
            String prov = data.getString("Name");
            editTextNombre.setText(prov==null?"":prov);

            prov = data.getString("Email");
            editTextCorreo.setText(prov==null?"":prov);

            prov = ""+(data.getInt("Age")==0?"":data.getInt("Age"));
            editTextEdad.setText(prov);

            //prov = data.getInt("Gender")==1 ? "Masculino" : "Femenino";
            //editTextGenero.setText(prov);
            spinnerGenero.setSelection(data.getInt("Gender")-1);

            prov = data.getString("Address");
            editTextResid.setText(prov==null?"":prov);

            prov = data.getString("Profession");
            editTextProfesion.setText(prov==null?"":prov);

            /*
            boolean notif = data.getBoolean("ToNotify");
            switchNotifs.setChecked(notif);
            */

        }catch(Exception e){e.printStackTrace();}
    }

    // --------------------------------------- //
    // -------------- ONCLICKS --------------- //
    // --------------------------------------- //


    @Override
    public void onClick(View view) {
        Log.d("DebCerrar","Cerrar Sesion!");
        switch(view.getId()){

            case R.id.perfilButtonContra:{
                FragmentManager fragmentManager = getFragmentManager();
                AppCompatDialogFragment diag = new DiagCambiarContra();
                diag.show(fragmentManager,"");
                break;
            }

            case R.id.perfilButtonCerrarSesion:{

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("¿Deseas cerrar sesión?");
                // Add the buttons
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userID",0);
                        editor.putString("email","");
                        editor.putBoolean("loggedIn",false);
                        Intent intent = new Intent(getActivity(), Login.class);
                        editor.commit();

                        Intent intentReg = new Intent(getActivity(), RegistrationIntentService.class);
                        getActivity().startService(intentReg);

                        startActivity(intent);
                        getActivity().finish();
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
                break;
            }

            case R.id.perfilButtonGuardar:{

                String nombre =  editTextNombre.getEditableText().toString();
                String correo = editTextCorreo.getEditableText().toString();
                String edad = editTextEdad.getEditableText().toString();
                String res = editTextResid.getEditableText().toString();
                String profesion =  editTextProfesion.getEditableText().toString();
                int gender = spinnerGenero.getSelectedItemPosition()+1;
                //boolean notif = switchNotifs.isChecked();

                if(nombre.isEmpty() || correo.isEmpty() || edad.isEmpty() || res.isEmpty() || profesion.isEmpty()){
                    Toast.makeText(getActivity(), "Verifica que ninguno de los campos venga vacio", Toast.LENGTH_SHORT).show();
                    return;
                }

                View someview = getActivity().getCurrentFocus();
                if(someview!=null){
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(someview.getWindowToken(), 0);
                }

                progress.setVisibility(View.VISIBLE);

                Map<String, Object> params = new LinkedHashMap<>();
                params.put("UserId",userID);
                params.put("ProfileId",3);
                params.put("UserTypeId",1);
                params.put("Name",nombre);
                params.put("Email",correo);
                params.put("Age",edad);
                params.put("Gender",gender);
                params.put("Address",res);
                params.put("Lat",(double)0.0);
                params.put("Lon",(double)0.0);
                params.put("GoogleAddress","GoogleAddress");
                params.put("Profession",profesion);
                //params.put("ToNotify",notif);
                params.put("ToNotify",true);

                WS.getInstance(getActivity()).saveProfile(params, this);

                break;
            }

            case R.id.buttonAcercade:{
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
