package propulsar.qroo.DomainLayer.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import propulsar.qroo.DomainLayer.Objects.Case;
import propulsar.qroo.R;

/**
 * Created by maubocanegra on 15/03/17.
 */

public class SwipeArrayAdapter extends ArrayAdapter<Case> {
    private List<Case> mDataset;
    int userID;

    public SwipeArrayAdapter(Context context, int resource, List<Case> objects) {
        super(context, resource, objects);
        mDataset=objects;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
    }


    @Override
    public int getCount() {
        return mDataset==null ? 10 : mDataset.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_swipe_prop, viewGroup, false);
        ((TextView)rootView.findViewById(R.id.detPropDesc)).setText(mDataset.get(i).getDescripcion());
        ((TextView)rootView.findViewById(R.id.detPropTitulo)).setText(mDataset.get(i).getTitulo());
        ((TextView)rootView.findViewById(R.id.detPropSubidaPor)).setText("Creador: "+mDataset.get(i).getCreatorName());
        ((TextView)rootView.findViewById(R.id.detPropFecha)).setText(mDataset.get(i).getFecha().split("T")[0]);
        ((ImageView)rootView.findViewById(R.id.detPropIconPerfil)).setImageResource(
                mDataset.get(i).getCreatorID()==1 ? R.mipmap.ic_perfil_v
                        : mDataset.get(i).getCreatorID()==2 ? R.mipmap.ic_perfil_c :
                        R.mipmap.ic_gobernador
        );
        ((ImageView)rootView.findViewById(R.id.detPropIconPerfil)).setImageResource(
                mDataset.get(i).getCreatorID()==userID ? R.mipmap.ic_perfil_c
                : mDataset.get(i).getCreatorType()==2 ? R.mipmap.ic_gobernador :
                R.mipmap.ic_perfil_v
        );
        Log.d("fdsafdsa","who's from ? = "+(
                mDataset.get(i).getCreatorID()==userID ? "mine"
                : mDataset.get(i).getCreatorType()==2 ? "staff" :
                "normal"));
        return rootView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}