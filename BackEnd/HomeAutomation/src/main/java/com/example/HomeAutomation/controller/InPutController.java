package com.example.HomeAutomation.controller;

import com.example.HomeAutomation.models.InPutAlert;
import com.example.HomeAutomation.models.Inputs;
import com.example.HomeAutomation.service.InPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/inputs")
@CrossOrigin
public class InPutController {
    @Autowired
    private InPutService inPutService;


    @GetMapping("/getall")
    public List<Inputs> getInPuts(){
        return inPutService.allInPuts();
    }
    @GetMapping("/get/{username}")
    public List<Inputs> getInPut(@PathVariable String username){

        return inPutService.get(username);
    }

    @PutMapping("/edit/{id}")
    public Inputs editInPut(@RequestBody Inputs inPuts,@PathVariable long id){
        return inPutService.editInPut(inPuts,id);
    }
    @PostMapping("/add/{username}")
    public Inputs addOutPut(@RequestBody Inputs inPuts,@PathVariable String username){
        return inPutService.addInputs(inPuts,username);
    }
    @GetMapping("/unread/{username}")
    public List<String> newAlerts(@PathVariable String username) {
        List<String> alerts = new ArrayList<>();
        for (InPutAlert inPutAlert : inPutService.getUnresolvedAlerts(username)) {
            alerts.add(inPutAlert.getInPut() + "   " + inPutAlert.getAlertMessage() + "   " +
                    inPutAlert.getAlertTime() + "   " + inPutService.getInputName(inPutAlert.getId()));
        }
        return alerts;
    }
    @GetMapping("/{username}")
    public List<InPutAlert> Alerts(@PathVariable String username){
        return inPutService.getAlerts(username);
    }

    @GetMapping("/")
    public String alertcontroler(){
        return "hi there";
    }

}
