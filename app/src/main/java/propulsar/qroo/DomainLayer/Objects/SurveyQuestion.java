package propulsar.qroo.DomainLayer.Objects;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by maubocanegra on 20/03/17.
 */

public class SurveyQuestion {
    public int questionID;
    public int surveyID;
    public String description;
    public ArrayList<SurveyAnswer> answers;
    public View.OnClickListener radioListener;
    public SurveyAnswer answerChosen;

    public SurveyQuestion(){}

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getSurveyID() {
        return surveyID;
    }

    public void setSurveyID(int surveyID) {
        this.surveyID = surveyID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<SurveyAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<SurveyAnswer> answers) {
        this.answers = answers;
    }

    public void addAnswer(SurveyAnswer newAnsw){
        if(answers==null){
            answers = new ArrayList<SurveyAnswer>();
        }

        answers.add(newAnsw);
    }

    public SurveyAnswer getAnswerChosen() {
        return answerChosen;
    }

    public void setAnswerChosen(SurveyAnswer answerChosen) {
        this.answerChosen = answerChosen;
    }

    public View.OnClickListener getRadioListener() {
        return radioListener;
    }

    public void setRadioListener(View.OnClickListener radioListener) {
        this.radioListener = radioListener;
    }
}
