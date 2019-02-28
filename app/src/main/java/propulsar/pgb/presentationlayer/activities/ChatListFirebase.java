package propulsar.pgb.presentationlayer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import propulsar.pgb.R;
import propulsar.pgb.domainlayer.Adapters.MessageListAdapter;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.domainlayer.firebase_objects.Usuario_FirebaseObj;

public class ChatListFirebase extends AppCompatActivity implements ChildEventListener{

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private MessageListAdapter mMessageAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist_firebase);

        instanciatoObjects();

        setListeners();
    }

    private void instanciatoObjects(){
        mMessageListView = findViewById(R.id.messageListView);

        List<Usuario_FirebaseObj> messages = new ArrayList<>();
        mMessageAdapter = new MessageListAdapter(ChatListFirebase.this, R.layout.item_message, messages);
        mMessageListView.setAdapter(mMessageAdapter);
    }

    private void setListeners(){
        findViewById(R.id.chatBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getSupportFragmentManager().beginTransaction().replace(R.id.container,new ChatListFirebase(), ChatListFirebase.TAG).commit();
                onBackPressed();
            }
        });
    }

    /*
    @Override
    public void firebaseCompleted(boolean hasError) {

    }
    */

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        //Log.d("chatListFirebase", ""+dataSnapshot.getChildren().toString());
        //mMessageAdapter.add(dataSnapshot.getValue(Usuario_FirebaseOBJ.class));
        mMessageAdapter.insert(dataSnapshot.getValue(Usuario_FirebaseObj.class),0);
        mMessageAdapter.notifyDataSetChanged();
        //mMessageListView.smoothScrollToPosition(0);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        //mMessageAdapter.remove(dataSnapshot.getValue(Mensaje_FirebaseObj.class));
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMessageAdapter.clear();
        mMessageAdapter.notifyDataSetChanged();
        WS.setReaderChatsListener(ChatListFirebase.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMessageAdapter.clear();
        mMessageAdapter.notifyDataSetChanged();
        WS.removeReaderChatsListener(ChatListFirebase.this);
    }
}
