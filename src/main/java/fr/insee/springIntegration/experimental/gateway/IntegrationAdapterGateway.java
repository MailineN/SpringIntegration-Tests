package fr.exp.integration.gateway;
import fr.exp.integration.model.Unit;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

// Permet à l'appli de fonctionner sans utiliser (ni connaitre) l'API fournie par Spring Integration
// Définit la passerelle (Gateway)
@MessagingGateway
public interface IntegrationAdapterGateway {
    @Gateway(requestChannel = "integration.unit.gateway.channel")
    public void sendMessage(Unit message);
}
