package fr.insee.springIntegration.experimental.config;

import fr.insee.springIntegration.experimental.model.Unit;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.messaging.Message;

import java.util.List;

@MessageEndpoint
public class Waiter {

    @Aggregator(inputChannel = "inputAggregator",outputChannel = "outboundAggregator",sendPartialResultsOnExpiry = "true")
    public Unit aggregatingMethod(List<Unit> items) {
        // Retourne le premier de la liste (pour l'exemple) -> Le but c'est d'agrégger les messages en un seul
        return items.get(0);
    }

    @ReleaseStrategy
    public boolean canRelease(List<Message<?>> messages){

        return messages.size()>2;
    }

    @CorrelationStrategy
    public String correlateBy(Message<Unit> message) {
        // Définit comment les messages sont regroupés entre eux : Ici le même id
        return (String) message.getHeaders().get("id");
    }


}
