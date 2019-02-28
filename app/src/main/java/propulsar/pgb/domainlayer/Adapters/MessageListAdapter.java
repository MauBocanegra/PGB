package propulsar.pgb.domainlayer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import propulsar.pgb.R;
import propulsar.pgb.domainlayer.firebase_objects.Usuario_FirebaseObj;
import propulsar.pgb.presentationlayer.activities.ChatFirebase;

public class MessageListAdapter extends ArrayAdapter<Usuario_FirebaseObj>{

    Context context;

    public MessageListAdapter(Context context, int resource, List<Usuario_FirebaseObj> objects) {
        super(context, resource, objects);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final Usuario_FirebaseObj message = getItem(position);
        Log.d("fullMessage","fullMessage="+message);


        convertView = ((Activity) getContext()).getLayoutInflater().inflate( R.layout.item_message_list, parent, false);

        TextView textViewNombre = convertView.findViewById(R.id.labelNombre);
        TextView textViewFecha = convertView.findViewById(R.id.labelFecha);
        TextView textViewMensaje = convertView.findViewById(R.id.labelMensaje);

        try {
            textViewNombre.setText(message.getNombre() == null ? "Usuario" : message.getNombre());
        }catch(Exception e){
            Log.e("ERRORMEssageList","ERROR obteniendo Nombre");}

        textViewMensaje.setText(message.getUltimoMensaje().getMensaje());
        Log.d("MessageListAdapter",""+message);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("UserIDDebug",""+message.getUser()/*.intValue()*/);
                try {
                    Intent intent=new Intent(context, ChatFirebase.class);
                    Bundle bundle = new Bundle();
                    Log.d("fdjskla","userToShowChat="+message.getUser().intValue());
                    bundle.putInt("userToDownloadChats",message.getUser().intValue());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    /*
                    ChatFirebase chatFirebaseFrag = new ChatFirebase();
                    chatFirebaseFrag.setUserIDToDownloadChats(message.getUser().intValue());
                    ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFirebaseFrag, ChatFirebase.TAG).commit();
                    */
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Ocurrió un error al descargar la lista de mensajes, Contacte al administrador de sistemas para verificar el estado", Toast.LENGTH_LONG).show();
                }
            }
        });

        return convertView;
    }
}
