package fr.exp.integration.service;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

@Component
public class IntegrationService {

    // Activator : Lier objet Spring à un canal (in/out)-> Permet de se comporter comme un service
    // Indique que le service est capable de traiter des messages "comme" des objets
    @ServiceActivator(inputChannel = "integration.unit.objectToJson.channel", outputChannel = "integration.unit.jsonToObject.channel")
    public Message<?> recieveMessage(Message<?> message) throws MessagingException {
        System.out.println(message);
        return message;
    }

    @ServiceActivator(inputChannel = "integration.unit.jsonToObject.fromTransformer.channel")
    public void processJsonToObject(Message<?> message) throws MessagingException {
        System.out.println(message);
    }
}
