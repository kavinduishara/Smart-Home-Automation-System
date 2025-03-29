package com.example.HomeAutomation.controller;

import com.example.HomeAutomation.models.InPutAlert;
import com.example.HomeAutomation.repository.InPutAlertRepo;
import com.example.HomeAutomation.service.InPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inputsAlerts")
@CrossOrigin
public class InputAlertController {

    @Autowired
    InPutService inPutService;
    @GetMapping("/{username}")
    public List<InPutAlert> Alerts(@PathVariable String username){
        return inPutService.getAlerts(username);
    }

    @GetMapping("/unread/{username}")
    public List<String> newAlerts(@PathVariable String username){
        List<String> alerts = null;
        inPutService.getUnresolvedAlerts(username).forEach(
                inPutAlert -> {
                    alerts.add(inPutAlert.getInPut()+"   "+inPutAlert.getAlertMessage()+"   "+inPutAlert.getAlertTime()+"   "+inPutService.getInputName(inPutAlert.getId()));
                });
        return alerts;
    }
}
