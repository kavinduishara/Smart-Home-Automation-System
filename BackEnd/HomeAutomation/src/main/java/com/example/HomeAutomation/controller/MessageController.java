package com.example.HomeAutomation.controller;

import com.example.HomeAutomation.models.ControlMessage;
import com.example.HomeAutomation.models.FirebaseToken;
import com.example.HomeAutomation.repository.FirebaseTokenRepository;
import com.example.HomeAutomation.service.InPutService;
import com.example.HomeAutomation.service.NotificationService;
import com.example.HomeAutomation.service.OutPutService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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
    private final NotificationService notificationService;


    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    OutPutService outPutService;

    @Autowired
    InPutService inPutService;
    @Autowired
    FirebaseTokenRepository firebaseTokenRepository;

    public MessageController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

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
        sendPushNotification(controlMessage.getUser(), controlMessage.getType(), controlMessage.getContent());

    }
    @MessageMapping("/fromuser")
    public void sendToHome(@Payload ControlMessage controlMessage){
        System.out.println(controlMessage.getContent()+"fromuser");
        simpMessagingTemplate.convertAndSendToUser(controlMessage.getUser(),"/home", controlMessage);
    }
    public void sendPushNotification(String username, String title, String messageBody) {
        FirebaseToken userToken = firebaseTokenRepository.findByUsername(username);

        if (userToken != null) {
            String deviceToken = userToken.getToken();

            Message message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(messageBody)
                            .build())
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No token found for user: " + username);
        }
    }

}
