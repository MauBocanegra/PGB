package propulsar.pgb.domainlayer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import propulsar.pgb.R;
import propulsar.pgb.domainlayer.firebase_objects.Mensaje_FirebaseObj;

public class MessageAdapter extends ArrayAdapter<Mensaje_FirebaseObj>{

    int userID;

    public MessageAdapter(Context context, int resource, List<Mensaje_FirebaseObj> objects) {
        super(context, resource, objects);
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.sharedPrefName), Context.MODE_PRIVATE);
        userID= sharedPreferences.getInt("userID",-1);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Mensaje_FirebaseObj message = getItem(position);

        //SIEMPRE debe instanciarse la vista, si se recicla puede mantener la vista anterior y se mostraría como del candidato siendo que venga de usuario
        boolean vieneDeUsuario = message.isVieneDeUsuario();
        if(userID==1){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate( vieneDeUsuario ? R.layout.item_message : R.layout.item_message_user, parent, false);
        }else {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(vieneDeUsuario ? R.layout.item_message_user : R.layout.item_message, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);

        messageTextView.setVisibility(View.VISIBLE);
        //photoImageView.setVisibility(View.GONE);
        messageTextView.setText(message.getMensaje());
        //authorTextView.setText(DateFormat.format("dd/MM", new Date(message.getTimestamp()*1000)).toString());


        return convertView;
    }
}
