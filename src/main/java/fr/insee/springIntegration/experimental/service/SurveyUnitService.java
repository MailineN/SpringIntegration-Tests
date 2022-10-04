package fr.insee.springIntegration.experimental.service;

import fr.insee.springIntegration.experimental.model.SurveyUnit;
import fr.insee.springIntegration.experimental.model.Unit;

public class SurveyUnitService {

    public SurveyUnit setSurveyToUnitUnit( Unit unit, String surveyName) {
        SurveyUnit surveyUnit = new SurveyUnit(unit,  surveyName);
        return surveyUnit;
    }
}
