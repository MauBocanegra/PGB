package propulsar.pgb.presentationlayer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;
import propulsar.pgb.domainlayer.objects.Case;

import static propulsar.pgb.domainlayer.WS.WS.context;

public class DetalleProp extends AppCompatActivity implements WS.OnWSRequested, View.OnClickListener{

    int proposalID=-1;
    int userID;

    ShareDialog shareDialog;

    CardView buttonAFavor;
    CardView buttonEnContra;

    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_prop);

        buttonAFavor = findViewById(R.id.buttonAFavor);
        buttonEnContra = findViewById(R.id.buttonEnContra);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("PropuestasDetalle");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetalleProp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        buttonEnContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                votadaEnContra();
            }
        });

        buttonAFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                votadaFavor();
            }
        });

        proposalID = getIntent().getExtras().getInt("proposalID");
        Log.d("fdnjskfdskn","propsalID="+proposalID);

        shareDialog = new ShareDialog(DetalleProp.this);

        /*
        findViewById(R.id.fab_share_prop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Toast.makeText(DetalleProp.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            SharePhoto photo = new SharePhoto.Builder()
                                    .setBitmap(bitmap)
                                    .build();
                            SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(photo)
                                    .build();
                            shareDialog.show(content);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Toast.makeText(DetalleProp.this, "FAILED", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };

                Picasso.with(DetalleProp.this).load("http://qroo.iog.digital/images/logo.png").into(target);


            }
        });
        */

        findViewById(R.id.buttonMasProps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetalleProp.this, VotaActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.fab_share_prop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(DetalleProp.this, view);

                popupMenu.getMenuInflater().inflate(R.menu.menu_social, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_facebook:{
                                if (ShareDialog.canShow(ShareLinkContent.class)) {
                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                            .setQuote(title+" #"+getString(R.string.app_name))
                                            .setContentUrl(Uri.parse("http://iog.digital"))
                                            .build();
                                    shareDialog.show(linkContent);
                                }
                                break;
                            }
                            case R.id.action_twitter:{

                                TweetComposer.Builder builder = new TweetComposer.Builder(DetalleProp.this)
                                        .text(title+" http://iog.digital #"+getString(R.string.app_name));
                                builder.show();


                                /*
                                final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                                        .getActiveSession();
                                final Intent intent = new ComposerActivity.Builder(EventActivity.this)
                                        .session(session)
                                        .text("Love where you work")
                                        .hashtags("#twitter")
                                        .createIntent();
                                startActivity(intent);
                                */
                                break;
                            }
                        }
                        return true;
                    }
                });
                setForceShowIcon(popupMenu);
                popupMenu.show();
                /*
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(event.getTitle())
                            .setContentDescription(event.getDescription())
                            .setContentUrl(Uri.parse(event.getUrl()))
                            .setImageUrl(Uri.parse(event.getImageUrl()))
                            .build();
                    shareDialog.show(linkContent);
                }
                */
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ProposalId",proposalID);
        params.put("UserId",userID);
        WS.getProposalDetail(params,this);
    }

    private void votadaEnContra(){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ProposalId",proposalID);
        params.put("UserId",userID);
        params.put("VoteTypeId",2);
        WS.voteProposal(params,DetalleProp.this);
        Toast.makeText(DetalleProp.this, "Votada EN CONTRA", Toast.LENGTH_SHORT).show();

        buttonAFavor.setVisibility(View.GONE);
        buttonEnContra.setVisibility(View.GONE);
    }

    private void votadaFavor(){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ProposalId",proposalID);
        params.put("UserId",userID);
        params.put("VoteTypeId",1);
        WS.voteProposal(params,DetalleProp.this);
        Toast.makeText(DetalleProp.this, "Votada A FAVOR", Toast.LENGTH_SHORT).show();

        buttonAFavor.setVisibility(View.GONE);
        buttonEnContra.setVisibility(View.GONE);
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------- //
    // -------------- WEB SERVICES IMPLEMENTATION --------------- //
    // ---------------------------------------------------------- //

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("WSDEB",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_getProposalDetail:{
                    JSONObject data = json.getJSONObject("data");
                    ((TextView)findViewById(R.id.detPropTitulo)).setText(data.getString("Title"));
                    ((TextView)findViewById(R.id.detPropDesc)).setText(data.getString("Description"));
                    ((TextView)findViewById(R.id.detPropSubidaPor)).setText("Subida por "+data.getString("UploaderUser"));
                    String[] fCreated = (data.getString("CreatedAt").split("T")[0]).split("-");
                    ((TextView)findViewById(R.id.detPropFecha)).setText(fCreated[2]+"-"+fCreated[1]+"-"+fCreated[0]);
                    ((TextView)findViewById(R.id.detPropTitUp)).setText("Votos a favor ("+data.getDouble("VoteUpPorcent")+"%)");
                    ((TextView)findViewById(R.id.detPropTitDown)).setText("Votos en contra ("+data.getDouble("VoteDownPorcent")+"%)");

                    title = data.getString("Title");

                    ((ImageView)findViewById(R.id.detPropIconPerfil)).setImageResource(
                            data.getInt("UserId")==userID ? R.mipmap.ic_perfil_c
                                    : data.getInt("UserTypeId")==2 ? R.mipmap.ic_gobernador :
                                    R.mipmap.ic_perfil_v
                    );

                    boolean voted = data.getBoolean("UserVoted");
                    if(voted){
                        findViewById(R.id.buttonAFavor).setVisibility(View.GONE);
                        findViewById(R.id.buttonEnContra).setVisibility(View.GONE);
                    }else{

                    }

                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    entries.add(new Entry((float)data.getDouble("VoteUpPorcent"),0));
                    entries.add(new Entry((float)data.getDouble("VoteDownPorcent"),1));
                    PieDataSet dataset = new PieDataSet(entries, "");
                    ArrayList<String> labels = new ArrayList<String>();
                    labels.add("A favor");
                    labels.add("En contra");
                    PieData pieData = new PieData(labels, dataset);
                    PieChart pieChart = ((PieChart)findViewById(R.id.perceView));
                    dataset.setColors(new int[]{ContextCompat.getColor(context, R.color.colorAccent), ContextCompat.getColor(context, R.color.colorYellow)});
                    pieChart.setData(pieData);
                    pieChart.setDescription("");
                    pieChart.animateY(5000);
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    // ------------------------------------------------------ //
    // -------------- ON CLICK IMPLEMENTATION --------------- //
    // ------------------------------------------------------ //


    @Override
    public void onClick(View view) {

    }

    // ------------------------------------------------------------- //
    // -------------- PARENT ACTIVITY IMPLEMENTATION --------------- //
    // ------------------------------------------------------------- //

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent i = null;
        int comesFrom = getIntent().getExtras().getInt("comesFrom");
        if (comesFrom==1) {
            i = new Intent(this, TabActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            i = new Intent(this, PropsVotadas.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        return i;
    }
}
