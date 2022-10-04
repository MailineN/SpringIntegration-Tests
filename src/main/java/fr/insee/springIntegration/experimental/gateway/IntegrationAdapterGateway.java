package fr.insee.springIntegration.experimental.gateway;

import fr.insee.springIntegration.experimental.model.SurveyUnit;
import fr.insee.springIntegration.experimental.model.Unit;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

// Permet à l'appli de fonctionner sans utiliser (ni connaitre) l'API fournie par Spring Integration
// Définit la passerelle (Gateway)
@MessagingGateway
public interface IntegrationAdapterGateway {

    @Gateway(requestChannel = "integration.gateway.channel")
    public String sendMessage(String message);
    @Gateway(requestChannel = "integration.unit.gateway.channel")
    public String processUnitDetails(Unit unit);


}
