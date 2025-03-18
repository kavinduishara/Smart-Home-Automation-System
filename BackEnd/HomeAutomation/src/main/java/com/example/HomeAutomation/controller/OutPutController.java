package com.example.HomeAutomation.controller;

import com.example.HomeAutomation.models.OutPuts;
import com.example.HomeAutomation.service.OutPutService;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/outputs")
@CrossOrigin
public class OutPutController {
    @Autowired
    private OutPutService outPutService;


    @GetMapping("/getall")
    public List<OutPuts> getOutPuts(){
        return outPutService.allOutPuts();
    }
    @GetMapping("/get/{username}")
    public List<OutPuts> getOutPut(@PathVariable String username){

        return outPutService.get(username);
    }

    @PutMapping("/edit/{id}")
    public OutPuts editPut(@RequestBody OutPuts outPuts,@PathVariable long id){
        return outPutService.editOutPut(outPuts,id);
    }
    @PostMapping("/add/{username}")
    public OutPuts addOutPut(@RequestBody OutPuts outPuts,@PathVariable String username){
        return outPutService.addOutputs(outPuts,username);
    }

    @RequestMapping(value = "/getall", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        return ResponseEntity.ok().build();
    }


}
