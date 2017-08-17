package propulsar.qroo.DomainLayer.Objects;

/**
 * Created by maubocanegra on 20/03/17.
 */

public class SurveyAnswer {
    public int id;
    public int survey;
    public int question;
    public String description;

    public SurveyAnswer(){};

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurvey() {
        return survey;
    }

    public void setSurvey(int survey) {
        this.survey = survey;
    }

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
