package fr.insee.springIntegration.experimental.model;

public class SurveyUnit extends Unit{
    private int surveyId;
    private String surveyName;

    public SurveyUnit(Unit u, String surveyName) {
        super(u);

        this.surveyName = surveyName;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }
}
