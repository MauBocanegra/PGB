package propulsar.pgb.domainlayer.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import propulsar.pgb.domainlayer.objects.Case;
import propulsar.pgb.presentationlayer.activities.DetalleCase;
import propulsar.pgb.R;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class CasesAdapter extends RecyclerView.Adapter<CasesAdapter.ViewHolder>{

    private ArrayList<Case> mDataset;
    Activity activity;

    public CasesAdapter(ArrayList<Case> myDataset, Activity activity) {
        mDataset = myDataset;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folios, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtFolio.setText(mDataset.get(position).getFolio());
        holder.txtCategoria.setText(mDataset.get(position).getTitulo());
        holder.txtFecha.setText(mDataset.get(position).getFecha());
        try {
            holder.circle.setBackgroundColor(Color.parseColor(mDataset.get(position).getLasStatusColor()));
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View circle;
        public TextView txtFolio;
        public TextView txtCategoria;
        public TextView txtFecha;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, DetalleCase.class);
                    intent.putExtra("CaseId",mDataset.get(getLayoutPosition()).getId());
                    activity.startActivity(intent);
                }
            });
            circle = v.findViewById(R.id.item_case_circle);
            txtFolio = (TextView)v.findViewById(R.id.item_case_folio);
            txtCategoria = (TextView)v.findViewById(R.id.item_case_categoria);
            txtFecha = (TextView)v.findViewById(R.id.item_case_fecha);
        }
    }
}
