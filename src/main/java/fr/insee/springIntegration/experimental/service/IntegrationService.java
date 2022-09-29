package fr.insee.springIntegration.experimental.service;

import fr.insee.springIntegration.experimental.model.Unit;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

@Component
public class IntegrationService {

    // Activator : Lier objet Spring à un canal (in/out)-> Permet de se comporter comme un service
    // Indique que le service est capable de traiter des messages "comme" des objets
    @ServiceActivator(inputChannel = "integration.unit.objectToJson.channel", outputChannel = "integration.unit.jsonToObject.channel")
    public Message<?> recieveMessage(Message<?> message) throws MessagingException {
        System.out.println(">>> Message recu : ");
        System.out.println("\t > "+ message);
        System.out.println(">>> Object to JSON : " + message.getPayload());
        System.out.println(" ----------------- -----------------");
        return message;
    }

    @ServiceActivator(inputChannel = "integration.unit.jsonToObject.fromTransformer.channel")
    public void processJsonToObject(Message<?> message) throws MessagingException {
        MessageChannel channel = (MessageChannel) message.getHeaders().getReplyChannel();
        MessageBuilder.fromMessage(message);
        System.out.println(">>> Process JSON to Object : ");
        System.out.println("\t > "+ message);
        System.out.println(" ----------------- -----------------");

        Unit unit = (Unit) message.getPayload();
        Message<?> newMessage = MessageBuilder.withPayload(unit.toString()).build();
        channel.send(newMessage);
    }
}
