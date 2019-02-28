package propulsar.pgb.presentationlayer.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import propulsar.pgb.domainlayer.Adapters.QuestionsAdapter;
import propulsar.pgb.domainlayer.objects.AnalyticsApplication;
import propulsar.pgb.domainlayer.objects.SurveyAnswer;
import propulsar.pgb.domainlayer.objects.SurveyQuestion;
import propulsar.pgb.domainlayer.WS.WS;
import propulsar.pgb.R;

public class SurveyActivity extends AppCompatActivity implements WS.OnWSRequested {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private android.support.v7.widget.LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<SurveyQuestion> questions;
    private ProgressBar progress;
    private View buttonAnswerSurvey;

    int surveyID;
    int userID;
    int surveysToAnswer;
    int surveysAnsweredSuccesfull=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSurvey);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Tracker mTracker = ((AnalyticsApplication) getApplication()).getDefaultTracker();
        //Log.i("AnalyticsDeb", "Setting screen name: " + "Splash");
        mTracker.setScreenName("Survey");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        surveyID=getIntent().getIntExtra("SurveyID",-1);

        mRecyclerView = (RecyclerView)findViewById(R.id.surveyRecyclerView);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        questions = new ArrayList<SurveyQuestion>();

        mAdapter = new QuestionsAdapter(questions,SurveyActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        progress = (ProgressBar)findViewById(R.id.surveyProgress);
        buttonAnswerSurvey = findViewById(R.id.buttonSendSurveyAnswers);

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPrefName), 0);
        userID = sharedPreferences.getInt("userID",0);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("SurveyId",surveyID);
        params.put("UserId",userID);
        WS.getSurveyDetail(params,this);

        findViewById(R.id.buttonSendSurveyAnswers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswers();
            }
        });
    }

    // -------------------------------------------------- //
    // -------------- OWN IMPLEMENTATIONS --------------- //
    // -------------------------------------------------- //

    private void checkAnswers(){

        //Si alguna no está contestada , muestra Toast y sale de la funcion
        for(int i=0; i<questions.size(); i++){
            if(questions.get(i).getAnswerChosen()==null){
                Toast.makeText(SurveyActivity.this, "Debes contestar todas las preguntas", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        buttonAnswerSurvey.setEnabled(false);
        progress.setVisibility(View.VISIBLE);

        for(int i=0; i<questions.size(); i++){
            surveysToAnswer++;
            SurveyAnswer ans = questions.get(i).getAnswerChosen();
            Log.d("fdsafdsaf","sur="+ans.getSurvey()+" q="+ans.getQuestion()+" a="+ans.getId());
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("Survey",ans.getSurvey());
            params.put("Question",ans.getQuestion());
            params.put("Answer",ans.getId());
            params.put("UserId",userID);
            WS.answerSurveyQuestion(params,this);
        }
    }

    private void addToList(ArrayList<SurveyQuestion> newQuestions){

        for(int i=0; i<newQuestions.size(); i++){
            questions.add(newQuestions.get(i));
        }

        mAdapter.notifyDataSetChanged();
    }

    // ---------------------------------------------------------- //
    // -------------- WEB SERVICES IMPLEMENTATION --------------- //
    // ---------------------------------------------------------- //


    @Override
    public void wsAnswered(JSONObject json) {
        Log.d("Survey",json.toString());
        int ws=0; int status=-1;
        try{status=json.getInt("status");}catch(Exception e){e.printStackTrace();}
        if(status!=0){/*ERRRRRRROOOOOOOORRRRRRR*/}

        try {
            ws = json.getInt("ws");
            switch (ws) {

                case WS.WS_answerSurvey:{

                    surveysAnsweredSuccesfull++;

                    if(surveysAnsweredSuccesfull==surveysToAnswer) {
                        Toast.makeText(SurveyActivity.this, "¡Encuesta contestada exitosamente!", Toast.LENGTH_LONG).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // ----------
                                onBackPressed();
                            }
                        }, 500);
                    }
                }

                case WS.WS_getSurvey: {
                    ArrayList<SurveyQuestion> newQuestions = new ArrayList<SurveyQuestion>();
                    JSONObject data = json.getJSONObject("data");
                    ((TextView)findViewById(R.id.tituloSurvey)).setText(data.getString("Title"));
                    ((TextView)findViewById(R.id.descriptionSurvey)).setText(data.getString("Description"));
                    JSONArray JArrayQuestions =  data.getJSONArray("SurveyQuestions");
                    for(int i=0; i<JArrayQuestions.length(); i++){
                        JSONObject jsonQuestion = JArrayQuestions.getJSONObject(i);
                        SurveyQuestion question = new SurveyQuestion();
                        question.setQuestionID(jsonQuestion.getInt("SurveyQuestionId"));
                        question.setSurveyID(jsonQuestion.getInt("SurveyId"));
                        question.setDescription(jsonQuestion.getString("QuestionDescription"));
                        JSONArray JArrayAnsw = jsonQuestion.getJSONArray("SurveyAnswers");
                        for(int j=0; j<JArrayAnsw.length(); j++){
                            JSONObject jsonAns = JArrayAnsw.getJSONObject(j);
                            SurveyAnswer ans = new SurveyAnswer();
                            ans.setId(jsonAns.getInt("SurveyAnswerId"));
                            ans.setSurvey(jsonQuestion.getInt("SurveyId"));
                            ans.setQuestion(jsonAns.getInt("Question"));
                            ans.setDescription(jsonAns.getString("AnswerDescription"));
                            question.addAnswer(ans);
                        }

                        newQuestions.add(question);
                    }

                    addToList(newQuestions);
                    break;
                }
            }
        }catch(Exception e){e.printStackTrace();}
        finally {
            buttonAnswerSurvey.setEnabled(true);
            progress.setVisibility(View.GONE);
        }
    }
}
