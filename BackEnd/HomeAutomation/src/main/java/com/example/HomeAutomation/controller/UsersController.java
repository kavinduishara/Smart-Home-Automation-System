package com.example.HomeAutomation.controller;

import com.example.HomeAutomation.models.FirebaseToken;
import com.example.HomeAutomation.models.Users;
import com.example.HomeAutomation.repository.FirebaseTokenRepository;
import com.example.HomeAutomation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UsersController {

    @Autowired
    UserService service;

    @Autowired
    FirebaseTokenRepository firebaseTokenRepository;
    @PostMapping("/register")
    public String register(@RequestBody Users users){
        Users s;
        String responce;
        try{
            s= service.register(users);
            responce= s.getUsername()+" successfully registered";
        }catch(Exception e){
            responce=e.getMessage();
        }
        return responce;
    }
    @PostMapping("/login")
    public String login(@RequestBody Users users){
        return service.verify(users);
    }
    @PostMapping("/save-token")
    public String saveToken(@RequestBody FirebaseToken firebaseToken) {
        FirebaseToken existingToken = firebaseTokenRepository.findByUsername(firebaseToken.getUsername());

        if (existingToken != null) {
            existingToken.setToken(firebaseToken.getToken());
            firebaseTokenRepository.save(existingToken);
        } else {
            firebaseTokenRepository.save(firebaseToken);
        }

        return "Token saved successfully!";
    }

}
