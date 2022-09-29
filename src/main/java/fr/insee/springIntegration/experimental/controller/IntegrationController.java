package fr.insee.springIntegration.experimental.controller;

import fr.insee.springIntegration.experimental.gateway.IntegrationAdapterGateway;
import fr.insee.springIntegration.experimental.model.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class IntegrationController {

    @Autowired
    private IntegrationAdapterGateway integrationAdapterGateway;

    @GetMapping("/integration/{name}")
    public String getMessage(@PathVariable String name){
        integrationAdapterGateway.sendMessage(name);
        return "Message sent";
    }

    @PostMapping(value = "/integration")
    public String processUnit(@RequestBody Unit unit){
        integrationAdapterGateway.processUnitDetails(unit);
        return "Unit sent";
    }
}
