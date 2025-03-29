package com.example.HomeAutomation.service;

import com.example.HomeAutomation.models.*;
import com.example.HomeAutomation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InPutService {

        @Autowired
        InPutRepo inPutRepo;

        @Autowired
        UserRepo userRepo;

        @Autowired
        InPutAlertRepo inPutAlertRepo;

        public Inputs addInputs(Inputs inPuts,String username){
            Users users=userRepo.findByUsername(username);
            inPuts.setUsers(users);
            return inPutRepo.save(inPuts);
        }
        public List<Inputs> get(String username){
            return inPutRepo.findByUsers_UserId(userRepo.findByUsername(username).getUserId());
        }
        public Inputs getInputName(long id){
            return inPutRepo.findById(id).get();
        }
        public List<Inputs> allInPuts(){
            return inPutRepo.findAll();
        }
        public Inputs editInPut(Inputs inPuts,Long id){
            Optional<Inputs> inPuts1= inPutRepo.findById(id);
            if(inPuts1.isEmpty()){
                return null;
            }
            inPuts1.get().setName(inPuts.getName());
            return inPutRepo.save(inPuts1.get());
        }
        public void saveAlert(Long id,String message){
            Optional<Inputs> inPuts1= inPutRepo.findById(id);
            if (inPuts1.isEmpty()){
                return;
            }
            inPutAlertRepo.save(new InPutAlert(LocalDateTime.now(),message,inPuts1.get(),false));
        }
        public void resolveAlert(Long id){
            List<InPutAlert> inPuts1= inPutAlertRepo.findByInPutId(id);

            inPuts1.forEach(inPutAlert -> inPutAlert.setRead(true));


        }
        public List<InPutAlert> getAlerts(String username){

            return inPutAlertRepo.findAlertsByUserId(userRepo.findByUsername(username).getUserId());
        }

        public  List<InPutAlert> getUnresolvedAlerts(String username){
            return inPutAlertRepo.findByInPut_User_IdAndReadFalse(userRepo.findByUsername(username).getUserId());
        }
}
