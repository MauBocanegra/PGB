package propulsar.pgb.domainlayer.Adapters;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import propulsar.pgb.domainlayer.objects.Event;
import propulsar.pgb.presentationlayer.activities.EventActivity;
import propulsar.pgb.R;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder>{

    private ArrayList<Event> mDataset;
    public EventClickedListener eventClickedListener;
    Activity activity;

    public EventsAdapter(ArrayList<Event> myDataset, AppCompatActivity activity) {
        eventClickedListener=(EventActivity)activity;
        mDataset = myDataset;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_events, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtTitulo.setText(mDataset.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView txtTitulo;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eventClickedListener.onEventClicked(mDataset.get(getLayoutPosition()));
                    /*
                    Intent intent = new Intent(activity, DetalleBenefActivity.class);
                    intent.putExtra("BenefitId",mDataset.get(getLayoutPosition()).getId());
                    activity.startActivity(intent);
                    */
                }
            });
            txtTitulo = (TextView)v.findViewById(R.id.item_events_titulo);
        }
    }

    public interface EventClickedListener{
        public void onEventClicked(Event event);
    }
}
