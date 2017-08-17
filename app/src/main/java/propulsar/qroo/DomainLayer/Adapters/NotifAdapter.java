package propulsar.qroo.DomainLayer.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import propulsar.qroo.DomainLayer.Objects.Notifs;
import propulsar.qroo.R;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.ViewHolder>{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private ArrayList<Notifs> mDataset;

    public NotifAdapter(ArrayList<Notifs> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position)!=null? VIEW_ITEM: VIEW_PROG;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifs, parent, false);
        //ViewHolder vh = new ViewHolder(v);

        ViewHolder vh;
        View v;
        if (viewType == VIEW_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_notifs, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_progress, parent, false);

        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.txtTitulo.setText(mDataset.get(position).getNotif());
            holder.txtFecha.setText(mDataset.get(position).getFecha());
        }catch(Exception e){}
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtTitulo;
        public TextView txtFecha;

        public ViewHolder(View v) {
            super(v);
            txtTitulo = (TextView)v.findViewById(R.id.item_notif_titulo);
            txtFecha = (TextView)v.findViewById(R.id.item_notif_fecha);
        }
    }
}
