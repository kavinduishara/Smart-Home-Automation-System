package com.example.HomeAutomation.controller;

import com.example.HomeAutomation.models.ControlMessage;
import com.example.HomeAutomation.service.InPutService;
import com.example.HomeAutomation.service.OutPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Objects;

@Controller
@CrossOrigin
public class MessageController {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    OutPutService outPutService;

    @Autowired
    InPutService inPutService;

    @MessageMapping("/fromhome")
    public void sendToUser(@Payload ControlMessage controlMessage){
        System.out.println("fromhome");
        System.out.println(controlMessage.getContent());
        simpMessagingTemplate.convertAndSendToUser(controlMessage.getUser(),"/user", controlMessage);
        if(Objects.equals(controlMessage.getType(), "outputs")){
            String[] message =controlMessage.getContent().split(" ");
            String output = message[0];
            String status = message[1];
            System.out.println(output);
            System.out.println(status);
            outPutService.flip(Long.parseLong(output),Objects.equals(status, "1"));
        } else if (Objects.equals(controlMessage.getType(), "inputs")) {
            String[] message =controlMessage.getContent().split(" ");
            String input = message[0];
            String messageBody = message[1];
            System.out.println(input);
            System.out.println(messageBody);
            inPutService.saveAlert(Long.valueOf(input),messageBody);
        }
    }
    @MessageMapping("/fromuser")
    public void sendToHome(@Payload ControlMessage controlMessage){
        System.out.println(controlMessage.getContent()+"fromuser");
        simpMessagingTemplate.convertAndSendToUser(controlMessage.getUser(),"/home", controlMessage);
    }
}
