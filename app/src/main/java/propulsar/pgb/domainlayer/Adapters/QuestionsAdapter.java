package propulsar.pgb.domainlayer.Adapters;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import propulsar.pgb.domainlayer.objects.SurveyQuestion;
import propulsar.pgb.R;

/**
 * Created by maubocanegra on 09/02/17.
 */

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder>{

    private ArrayList<SurveyQuestion> mDataset;
    Activity activity;

    public QuestionsAdapter(ArrayList<SurveyQuestion> myDataset, AppCompatActivity activity) {
        mDataset = myDataset;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_survey, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        holder.txtDescripcion.setText(mDataset.get(position).getDescription());
        if(mDataset.get(position).getAnswers().size()>0)
            holder.radio1.setText(mDataset.get(position).getAnswers().get(0).getDescription());
        else
            holder.radio1.setVisibility(View.GONE);
        if(mDataset.get(position).getAnswers().size()>1)
            holder.radio2.setText(mDataset.get(position).getAnswers().get(1).getDescription());
        else
            holder.radio2.setVisibility(View.GONE);
        if(mDataset.get(position).getAnswers().size()>2)
            holder.radio3.setText(mDataset.get(position).getAnswers().get(2).getDescription());
        else
            holder.radio3.setVisibility(View.GONE);
        if(mDataset.get(position).getAnswers().size()>3)
            holder.radio4.setText(mDataset.get(position).getAnswers().get(3).getDescription());
        else
            holder.radio4.setVisibility(View.GONE);
        if(mDataset.get(position).getAnswers().size()>4)
            holder.radio5.setText(mDataset.get(position).getAnswers().get(4).getDescription());
        else
            holder.radio5.setVisibility(View.GONE);
        if(mDataset.get(position).getAnswers().size()>5)
            holder.radio6.setText(mDataset.get(position).getAnswers().get(5).getDescription());
        else
            holder.radio6.setVisibility(View.GONE);

        View.OnClickListener listen = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();
                int chosen = -1;
                switch(view.getId()){
                    case R.id.survey_radio_1:{ if(checked && mDataset.get(position).getAnswers().size()>1) chosen=0; break; }
                    case R.id.survey_radio_2:{ if(checked && mDataset.get(position).getAnswers().size()>1) chosen=1; break; }
                    case R.id.survey_radio_3:{ if(checked && mDataset.get(position).getAnswers().size()>2) chosen=2; break; }
                    case R.id.survey_radio_4:{ if(checked && mDataset.get(position).getAnswers().size()>1) chosen=3; break; }
                    case R.id.survey_radio_5:{ if(checked && mDataset.get(position).getAnswers().size()>1) chosen=4; break; }
                    case R.id.survey_radio_6:{ if(checked && mDataset.get(position).getAnswers().size()>2) chosen=5; break; }
                }
                mDataset.get(position).setAnswerChosen(mDataset.get(position).getAnswers().get(chosen));
                mDataset.get(position).getAnswerChosen().setSurvey(mDataset.get(position).getSurveyID());
            }
        };

        if(mDataset.get(position).getAnswers().size()>0)
            holder.radio1.setOnClickListener(listen);
        if(mDataset.get(position).getAnswers().size()>1)
            holder.radio2.setOnClickListener(listen);
        if(mDataset.get(position).getAnswers().size()>2)
            holder.radio3.setOnClickListener(listen);
        if(mDataset.get(position).getAnswers().size()>3)
            holder.radio4.setOnClickListener(listen);
        if(mDataset.get(position).getAnswers().size()>4)
            holder.radio5.setOnClickListener(listen);
        if(mDataset.get(position).getAnswers().size()>5)
            holder.radio6.setOnClickListener(listen);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtDescripcion;
        public RadioButton radio1;
        public RadioButton radio2;
        public RadioButton radio3;
        public RadioButton radio4;
        public RadioButton radio5;
        public RadioButton radio6;

        public ViewHolder(View v) {
            super(v);
            txtDescripcion = (TextView)v.findViewById(R.id.item_survey_pregunta);
            radio1 = (RadioButton) v.findViewById(R.id.survey_radio_1);
            radio2 = (RadioButton) v.findViewById(R.id.survey_radio_2);
            radio3 = (RadioButton) v.findViewById(R.id.survey_radio_3);
            radio4 = (RadioButton) v.findViewById(R.id.survey_radio_4);
            radio5 = (RadioButton) v.findViewById(R.id.survey_radio_5);
            radio6 = (RadioButton) v.findViewById(R.id.survey_radio_6);
        }
    }
}
