package propulsar.pgb.domainlayer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import propulsar.pgb.domainlayer.objects.Msg;
import propulsar.pgb.domainlayer.objects.OwnMsg;
import propulsar.pgb.presentationlayer.activities.ChatActivity;
import propulsar.pgb.presentationlayer.Dialogs.DiagShowOnly;
import propulsar.pgb.R;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private final int VIEW_OWN = 1;
    private final int VIEW_OTHER = 0;

    private ArrayList<Msg> mDataset;
    private ChatActivity activity;

    public MsgAdapter(ArrayList<Msg> myDataset, ChatActivity a) {
        mDataset = myDataset;
        activity=a;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) instanceof OwnMsg ? VIEW_OWN : VIEW_OTHER;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifs, parent, false);
        //ViewHolder vh = new ViewHolder(v);

        ViewHolder vh;
        View v;
        if (viewType == VIEW_OWN) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_msg_own, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_msg_other, parent, false);

        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        final Context c = holder.link.getContext();
        holder.msg.setText(mDataset.get(position).getMsg());
        holder.fecha.setText(mDataset.get(position).getTimeStamp().split("T")[0]);

        try {
            if (mDataset.get(position).isBot()) {
                Picasso.with(activity).load(R.mipmap.ic_bot).into(holder.iconProfile);
            }else if (!mDataset.get(position).getAvatarURL().isEmpty()) {
                Picasso.with(activity).load(mDataset.get(position).getAvatarURL()).into(holder.iconProfile);
            }
        }catch(Exception e){}

        Log.d("mDATASET(GETPOSITION)",mDataset.get(position).toString());

        int msgType=mDataset.get(position).getType();
        /*
        1 texto
        2 ligas
        3 ubicación
        4 archivo
        */
        if(mDataset.get(position).isBot()){
            holder.msg.setVisibility(View.VISIBLE);
            holder.fecha.setVisibility(View.VISIBLE);
            holder.mapPreview.setVisibility(View.GONE);
        }

        switch (msgType){
            //Ubicacion
            case 3:{
                holder.msg.setVisibility(View.GONE);
                holder.fecha.setVisibility(View.GONE);
                holder.mapPreview.setVisibility(View.VISIBLE);

                Log.d("MapsScreenshot","https://maps.googleapis.com/maps/api/staticmap?center=" +mDataset.get(position).getMsg()+ "&zoom=17&size=500x400&maptype=roadmap" + "&markers=color:red%7C" +mDataset.get(position).getMsg()+"&key=AIzaSyC3XcHPwMDu1cFvZA2sVjkILMZ3q_ieuPQ");
                Picasso.with(activity).load("https://maps.googleapis.com/maps/api/staticmap?center=" +mDataset.get(position).getMsg()+ "&zoom=17&size=500x400&maptype=roadmap" + "&markers=color:red%7C" +mDataset.get(position).getMsg()+"&key=AIzaSyC3XcHPwMDu1cFvZA2sVjkILMZ3q_ieuPQ").into(holder.mapPreview);
                final String loc = mDataset.get(position).getMsg();
                holder.mapPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri gmmIntentUri = Uri.parse("geo:" + loc + "?q=" + loc + "(Ubicación definida");
                        //geo:<lat>,<long>?q=<lat>,<long>(Label+Name)
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(c.getPackageManager()) != null) {
                            c.startActivity(mapIntent);
                        }
                    }
                });
                break;
            }

            case 4:{
                holder.msg.setVisibility(View.GONE);
                holder.fecha.setVisibility(View.GONE);
                holder.mapPreview.setVisibility(View.VISIBLE);
                Picasso.with(activity).load(mDataset.get(position).getUrl()).into(holder.mapPreview);

                holder.mapPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DiagShowOnly diag = new DiagShowOnly();
                        diag.setImgURL(mDataset.get(pos).getUrl());
                        diag.show(activity.getSupportFragmentManager(),"");
                    }
                });
                break;
            }

            case 1:{
                holder.msg.setVisibility(View.VISIBLE);
                holder.fecha.setVisibility(View.VISIBLE);
                holder.mapPreview.setVisibility(View.GONE);

                //Deteccion y presentacion de paginas web
                Pattern pattern = Pattern.compile("[^ \\s]+\\.[^ \\s\\d]+");
                Matcher matcher = pattern.matcher(mDataset.get(position).getMsg());
                if (matcher.find()) {
                    final String urlLink = matcher.group();
                    //holder.divider.setVisibility(View.VISIBLE);
                    //holder.link.setVisibility(View.VISIBLE);
                    String sub1 = mDataset.get(position).getMsg().substring(0, matcher.start());
                    String sub2 = mDataset.get(position).getMsg().substring(matcher.start(), matcher.end());
                    String sub3 = mDataset.get(position).getMsg().substring(matcher.end(), mDataset.get(position).getMsg().length());
                    String underlinedSt = sub1 + "<u><b>" + sub2 + "</b></u>" + sub3;
                    String[] underlinedArr = underlinedSt.split("\n");
                    underlinedSt = "";
                    for (String st : underlinedArr) {
                        underlinedSt += st + "<br>";
                    }
                    underlinedSt = underlinedSt.substring(0, underlinedSt.length() - 4);
                    holder.msg.setText(Html.fromHtml(underlinedSt));
                    //holder.link.setText(matcher.group());
                    //holder.link.setPaintFlags(holder.link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    holder.msg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                    /*
                    Bundle mBundle = new Bundle();
                    if(urlLink.startsWith("http://")||urlLink.startsWith("https://"))
                        mBundle.putString("URL",urlLink);
                    else
                        mBundle.putString("URL","http://"+urlLink);
                    mBundle.putInt("comesFrom",1);
                    Intent intent = new Intent(c, WebViewEventActivity.class);
                    intent.putExtras(mBundle);
                    c.startActivity(intent);
                    */

                            String linkURL = "";
                            if (urlLink.startsWith("http://") || urlLink.startsWith("https://"))
                                linkURL = urlLink;
                            else
                                linkURL = "http://" + urlLink;

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkURL));
                            c.startActivity(browserIntent);
                        }
                    });
                }

                if(mDataset.get(position).getButtons()!=null){
                if(mDataset.get(position).getButtons().size()>0){
                    ArrayList<String> buttons = mDataset.get(position).getButtons();

                    if(buttons.size()>=1){
                        holder.button1.setVisibility(View.VISIBLE);
                        holder.button1.setText(buttons.get(0));
                        holder.button1.setOnClickListener(mDataset.get(position).getOnClickListener());
                    }else{
                        holder.button1.setVisibility(View.GONE);
                        holder.button1.setText("");
                    }

                    if(buttons.size()>=2){
                        holder.button2.setVisibility(View.VISIBLE);
                        holder.button2.setText(buttons.get(1));
                        holder.button2.setOnClickListener(mDataset.get(position).getOnClickListener());
                    }else{
                        holder.button2.setVisibility(View.GONE);
                        holder.button2.setText("");
                    }

                    if(buttons.size()>=3){
                        holder.button3.setVisibility(View.VISIBLE);
                        holder.button3.setText(buttons.get(2));
                        holder.button3.setOnClickListener(mDataset.get(position).getOnClickListener());
                    }else{
                        holder.button3.setVisibility(View.GONE);
                        holder.button3.setText("");
                    }

                    if(buttons.size()>=4){
                        holder.button4.setVisibility(View.VISIBLE);
                        holder.button4.setText(buttons.get(3));
                        holder.button4.setOnClickListener(mDataset.get(position).getOnClickListener());
                    }else{
                        holder.button4.setVisibility(View.GONE);
                        holder.button4.setText("");
                    }

                    if(buttons.size()>=5){
                        holder.button5.setVisibility(View.VISIBLE);
                        holder.button5.setText(buttons.get(4));
                        holder.button5.setOnClickListener(mDataset.get(position).getOnClickListener());
                    }else{
                        holder.button5.setVisibility(View.GONE);
                        holder.button5.setText("");
                    }

                    if(buttons.size()>=6){
                        holder.button6.setVisibility(View.VISIBLE);
                        holder.button6.setText(buttons.get(5));
                        holder.button6.setOnClickListener(mDataset.get(position).getOnClickListener());
                    }else{
                        holder.button6.setVisibility(View.GONE);
                        holder.button6.setText("");
                    }

                    if(buttons.size()>=7){
                        holder.button7.setVisibility(View.VISIBLE);
                        holder.button7.setText(buttons.get(6));
                        holder.button7.setOnClickListener(mDataset.get(position).getOnClickListener());
                    }else{
                        holder.button7.setVisibility(View.GONE);
                        holder.button7.setText("");
                    }
                }else{
                    holder.button1.setVisibility(View.GONE);
                    holder.button1.setText("");
                    holder.button2.setVisibility(View.GONE);
                    holder.button2.setText("");
                    holder.button3.setVisibility(View.GONE);
                    holder.button3.setText("");
                    holder.button4.setVisibility(View.GONE);
                    holder.button4.setText("");
                    holder.button5.setVisibility(View.GONE);
                    holder.button5.setText("");
                    holder.button6.setVisibility(View.GONE);
                    holder.button6.setText("");
                    holder.button7.setVisibility(View.GONE);
                    holder.button7.setText("");
                }}else if(holder.button1!=null){
                    holder.button1.setVisibility(View.GONE);
                    holder.button1.setText("");
                    holder.button2.setVisibility(View.GONE);
                    holder.button2.setText("");
                    holder.button3.setVisibility(View.GONE);
                    holder.button3.setText("");
                    holder.button4.setVisibility(View.GONE);
                    holder.button4.setText("");
                    holder.button5.setVisibility(View.GONE);
                    holder.button5.setText("");
                    holder.button6.setVisibility(View.GONE);
                    holder.button6.setText("");
                    holder.button7.setVisibility(View.GONE);
                    holder.button7.setText("");
                }
                break;
            }

            case 2:{
                holder.msg.setVisibility(View.VISIBLE);
                holder.fecha.setVisibility(View.VISIBLE);
                holder.mapPreview.setVisibility(View.GONE);
                break;
            }
        }

        /*
        if(mDataset.get(position).getMsg().length()==0 && mDataset.get(position).getUrl().length()>0){
            holder.msg.setVisibility(View.GONE);
            holder.fecha.setVisibility(View.GONE);
            holder.mapPreview.setVisibility(View.VISIBLE);
            Picasso.with(activity).load(mDataset.get(position).getUrl()).into(holder.mapPreview);
        }else {
        *(



            //Deteccion y presentacion de ubicaciones
            /*
            Pattern locationPattern = Pattern.compile("\\-?[\\d]+\\.[\\d]+\\,\\-?[\\d]+\\.[\\d]+");
            Matcher locationMatcher = locationPattern.matcher(mDataset.get(position).getMsg());
            if (locationMatcher.find()) {
                holder.msg.setVisibility(View.GONE);
                holder.fecha.setVisibility(View.GONE);
                holder.mapPreview.setVisibility(View.VISIBLE);
            //https://maps.googleapis.com/maps/api/staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&maptype=roadmap
//&markers=color:blue%7Clabel:S%7C40.702147,-74.015794&markers=color:green%7Clabel:G%7C40.711614,-74.012318
//&markers=color:red%7Clabel:C%7C40.718217,-73.998284
//(&key=YOUR_API_KEY
                Picasso.with(activity).load("https://maps.googleapis.com/maps/api/staticmap?center=" + locationMatcher.group() + "&zoom=17&size=500x400&maptype=roadmap" + "&markers=color:red%7C" + locationMatcher.group()).into(holder.mapPreview);
                final String loc = locationMatcher.group();
                holder.mapPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri gmmIntentUri = Uri.parse("geo:" + loc + "?q=" + loc + "(Ubicación definida");
                        //geo:<lat>,<long>?q=<lat>,<long>(Label+Name)
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(c.getPackageManager()) != null) {
                            c.startActivity(mapIntent);
                        }
                    }
                });
            }
            */
       // }

        try {
            Log.d("DebugMsg", "[" + mDataset.get(position).getMsg() + "]msgLen=" + mDataset.get(position).getMsg().length() + " url[" + mDataset.get(position).getUrl() + "]Len=" + mDataset.get(position).getUrl().length());
        }catch(Exception e){}

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView msg;
        public TextView fecha;
        public View divider;
        public TextView link;
        public ImageView mapPreview;
        public ImageView iconProfile;

        public Button button1;
        public Button button2;
        public Button button3;
        public Button button4;
        public Button button5;
        public Button button6;
        public Button button7;


        public ViewHolder(View v) {
            super(v);
            fecha = (TextView)v.findViewById(R.id.msg_fecha);
            msg = (TextView)v.findViewById(R.id.msg_msg);
            divider = v.findViewById(R.id.chatSeparator);
            link = (TextView)v.findViewById(R.id.chat_link_text);
            mapPreview = (ImageView)v.findViewById(R.id.mapPreview);
            iconProfile = (ImageView)v.findViewById(R.id.iconPerfil);

            button1 = v.findViewById(R.id.button2);
            button2 = v.findViewById(R.id.button3);
            button3 = v.findViewById(R.id.button4);
            button4 = v.findViewById(R.id.button5);
            button5 = v.findViewById(R.id.button6);
            button6 = v.findViewById(R.id.button7);
            button7 = v.findViewById(R.id.button8);
        }
    }
}
