package propulsar.qroo.PresentationLayer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import propulsar.qroo.R;

public class WebViewEventActivity extends AppCompatActivity{

    private WebView mWebview ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarWeb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebview  = (WebView)findViewById(R.id.webviewID);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("WEBVIEW",getIntent().getExtras().getString("URL"));

        mWebview .loadUrl(getIntent().getExtras().getString("URL"));

        /*
        Map<String, Object> params = new LinkedHashMap<>();
        WS.getInstance(WebViewEventActivity.this).getAboutHTML(params,WebViewEventActivity.this);
        */

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
            i = new Intent(this, ChatActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            i = new Intent(this, EventActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        return i;
    }

}
