package fr.insee.springIntegration.experimental.config;

import fr.insee.springIntegration.experimental.model.Unit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.advice.IdempotentReceiverInterceptor;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.selector.MetadataStoreSelector;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.MessageChannel;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
@EnableIntegration
@IntegrationComponentScan
public class IntegrationConfig {
    @Bean
    public MessageChannel messageChannel(){
        return new DirectChannel();
    }

    // Transformer : Permet de transformer un objet en un autre
    @Bean
    @Transformer(inputChannel = "integration.unit.gateway.channel", outputChannel = "integration.unit.objectToJson.channel")
    public ObjectToJsonTransformer objectToJsonTransformer(){
        return new ObjectToJsonTransformer(getMapper());
    }

    @Bean
    public Jackson2JsonObjectMapper getMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return new Jackson2JsonObjectMapper(mapper);
    }

    @Bean
    @Transformer(inputChannel = "integration.unit.jsonToObject.channel", outputChannel = "integration.unit.jsonToObject.fromTransformer.channel")
    JsonToObjectTransformer jsonToObjectTransformer() {
        return new JsonToObjectTransformer(Unit.class);
    }

    // Interceptor : Permet de gérer les doublons de messages
    // Ici on filtre par l'id du message, par contre je ne sais pas comment tester
    // MetadataStoreSelector : permet de selec le composant à filtrer
    @Bean
    public IdempotentReceiverInterceptor idempotentReceiverInterceptor() {
        IdempotentReceiverInterceptor idempotentReceiverInterceptor =
                new IdempotentReceiverInterceptor(new MetadataStoreSelector(m -> m.getPayload().toString()));
        idempotentReceiverInterceptor.setDiscardChannelName("integration.idempotentDiscardChannel");
        idempotentReceiverInterceptor.setThrowExceptionOnRejection(true);
        return idempotentReceiverInterceptor;
    }



}
