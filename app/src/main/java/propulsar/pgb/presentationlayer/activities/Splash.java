package propulsar.pgb.presentationlayer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import android.app.NotificationManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import domainlayer.Services.MyHandler;
import domainlayer.Services.WSConstant;
import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ImageView gifDrawable;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Splash.this.NOTIFICATION_SERVICE);
        notificationManager.cancel(MyHandler.NOTIFICATION_ID);

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Splash");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());



        gifDrawable = (ImageView) findViewById(R.id.videoView);
        Glide.with(this).load(R.drawable.splash).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(gifDrawable));



        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //WS.loginFb();
                SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                if(sharedPreferences.getBoolean("loggedIn",false)){
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, WSConstant.flavorID==2 ? 3400 : 2000);

    }

}
