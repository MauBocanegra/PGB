package propulsar.qroo.DomainLayer.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import propulsar.qroo.DomainLayer.Objects.StateCase;
import propulsar.qroo.R;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class StateCaseAdapter extends RecyclerView.Adapter<StateCaseAdapter.ViewHolder>{

    private ArrayList<StateCase> mDataset;
    Activity activity;

    public StateCaseAdapter(ArrayList<StateCase> myDataset, AppCompatActivity activity) {
        mDataset = myDataset;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statecase, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //holder.txtTitulo.setText(mDataset.get(position).getTitulo());
        holder.textViewtitulo.setText(mDataset.get(position).getDescription());
        holder.textViewfecha.setText(mDataset.get(position).getDate().split("T")[0]);
        holder.circle.setColorFilter(Color.parseColor(mDataset.get(position).getColor()));
        //holder.circle.setBackgroundColor(Color.parseColor(mDataset.get(position).getColor()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public TextView txtTitulo;
        public View view;
        public ImageView circle;
        public TextView textViewtitulo;
        public TextView textViewfecha;

        public ViewHolder(View v){
            super(v);
            view = v;
            textViewtitulo = (TextView)v.findViewById(R.id.detCase_item_rev_tit);
            textViewfecha = (TextView)v.findViewById(R.id.detCase_item_rev_fecha);
            circle = (ImageView)v.findViewById(R.id.detCase_item_rev_img);
        }

        /*
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, DetalleBenefActivity.class);
                    intent.putExtra("BenefitId",mDataset.get(getLayoutPosition()).getId());
                    activity.startActivity(intent);
                }
            });
            txtTitulo = (TextView)v.findViewById(R.id.item_benefs_titulo);
        }
        */
    }
}
