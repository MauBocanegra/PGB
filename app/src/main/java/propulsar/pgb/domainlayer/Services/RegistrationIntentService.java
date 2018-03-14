package propulsar.pgb.domainlayer.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.microsoft.windowsazure.messaging.NotificationHub;
import domainlayer.Services.NotificationSettings;

import propulsar.pgb.presentationlayer.activities.TabActivity;
import propulsar.pgb.R;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    private NotificationHub hub;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String resultString = null;
        String regID = null;
        String storedToken = null;

        try {
            String FCM_token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "FCM Registration Token: " + FCM_token);

            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if (((regID=sharedPreferences.getString("registrationID", null)) == null)){

                SharedPreferences sharedPreferencesUser = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                int userID = sharedPreferencesUser.getInt("userID",0);
                NotificationHub hub = new NotificationHub(NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString, this);
                Log.d(TAG, "Attempting a new registration with NH using FCM token : " + FCM_token);
                //regID = hub.register(FCM_token,"ALL",userID).getRegistrationId();
                Log.d(TAG, "NH Registration refreshing with token : " + FCM_token);
                if (userID == 0) {
                    regID = hub.register(FCM_token,"Unregistered").getRegistrationId();
                }else{
                    regID = hub.register(FCM_token,"ALL",""+userID).getRegistrationId();
                }

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.d(TAG, resultString);

                Log.d("RegistrationIntent",regID);
                sharedPreferences.edit().putString("registrationID", regID ).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token ).apply();
            }

            // Check if the token may have been compromised and needs refreshing.
            else if ((storedToken=sharedPreferences.getString("FCMtoken", "")) != FCM_token) {

                SharedPreferences sharedPreferencesUser = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
                int userID = sharedPreferencesUser.getInt("userID",0);
                NotificationHub hub = new NotificationHub(NotificationSettings.HubName,
                        NotificationSettings.HubListenConnectionString, this);
                Log.d(TAG, "NH Registration refreshing with token : " + FCM_token);
                if (userID == 0) {
                    regID = hub.register(FCM_token,"Unregistered").getRegistrationId();
                }else{
                    regID = hub.register(FCM_token,"ALL",""+userID).getRegistrationId();
                }

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.d(TAG, resultString);

                Log.d("RegistrationIntent",regID);
                sharedPreferences.edit().putString("registrationID", regID ).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token ).apply();
            }

            else {
                resultString = "Previously Registered Successfully - RegId : " + regID;
                Log.d("RegistrationIntent",regID);
            }
        } catch (Exception e) {
            Log.e(TAG, resultString="Failed to complete registration", e);
            e.printStackTrace();
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed.
        if (TabActivity.isVisible) {
            TabActivity.tabActivity.ToastNotify(resultString);
        }
    }
}