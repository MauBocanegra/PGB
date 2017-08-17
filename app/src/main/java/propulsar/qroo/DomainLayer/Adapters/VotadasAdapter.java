package propulsar.qroo.DomainLayer.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import propulsar.qroo.DomainLayer.Objects.Case;
import propulsar.qroo.PresentationLayer.Activities.DetalleProp;
import propulsar.qroo.R;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class VotadasAdapter extends RecyclerView.Adapter<VotadasAdapter.ViewHolder>{

    private ArrayList<Case> mDataset;
    Activity activity;
    int userID;

    public VotadasAdapter(ArrayList<Case> myDataset, AppCompatActivity activity) {
        mDataset = myDataset;
        this.activity=activity;
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_votadas, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Log.d("fdsafadsfdsa","userID="+userID+" creatorID="+mDataset.get(position).getCreatorID());
        holder.icon.setImageResource(
                mDataset.get(position).getCreatorID()==userID ? R.mipmap.ic_perfil_c
                        : mDataset.get(position).getCreatorType()==2 ? R.mipmap.ic_gobernador :
                        R.mipmap.ic_perfil_v
        );

        /*
        if(mDataset.get(position).getCreatorID()==userID){
            holder.icon.setImageResource(R.mipmap.ic_perfil_c);
        }else{
            holder.icon.setImageResource(R.mipmap.ic_perfil_v);
        }

        if(mDataset.get(position).getCreatorType()==2){
            holder.icon.setImageResource(R.mipmap.ic_gobernador);
        }
        */
        holder.txtTitulo.setText(mDataset.get(position).getTitulo());
        holder.txtFecha.setText(mDataset.get(position).getFecha());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView icon;
        public TextView txtTitulo;
        public TextView txtFecha;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, DetalleProp.class);
                    intent.putExtra("proposalID",mDataset.get(getLayoutPosition()).getId());
                    activity.startActivity(intent);
                }
            });

            icon = (ImageView)v.findViewById(R.id.item_votadas_icon);
            txtTitulo = (TextView)v.findViewById(R.id.item_votadas_titulo);
            txtFecha = (TextView)v.findViewById(R.id.item_votadas_fecha);
        }
    }
}
