package propulsar.pgb.presentationlayer.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

public class WebViewActivity extends AppCompatActivity implements WS.OnWSRequested{

    private WebView mWebview ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarWeb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mWebview  = (WebView)findViewById(R.id.webviewID);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        Map<String, Object> params = new LinkedHashMap<>();
        WS.getAboutHTML(params,WebViewActivity.this);

    }

    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("GetAboutHTML--------",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}

        if(status==0)
        try {
            ws = json.getInt("ws");
            switch (ws) {
                case WS.WS_getAbout:{
                    JSONObject jsondata = json.getJSONObject("data");
                    JSONObject jsonData = jsondata.getJSONArray("Data").getJSONObject(0);

                    Log.d("fdsafdsafdsa","url="+jsonData.getString("Value"));

                    mWebview .loadUrl(jsonData.getString("Value"));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }

    }

}
