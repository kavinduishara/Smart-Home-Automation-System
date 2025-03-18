package com.example.HomeAutomation.service;

import com.example.HomeAutomation.models.OutPuts;
import com.example.HomeAutomation.models.StatusHistory;
import com.example.HomeAutomation.models.Users;
import com.example.HomeAutomation.repository.OutPutRepo;
import com.example.HomeAutomation.repository.StatusHistoryRepo;
import com.example.HomeAutomation.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class OutPutService {

    @Autowired
    OutPutRepo outPutRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    StatusHistoryRepo statusHistoryRepo;

    public OutPuts addOutputs(OutPuts outPuts,String username){
        Users users=userRepo.findByUsername(username);
        outPuts.setUsers(users);
        return outPutRepo.save(outPuts);
    }
    public List<OutPuts> get(String username){
        return outPutRepo.findByUsers_UserId(userRepo.findByUsername(username).getUserId());
    }
    public List<OutPuts> allOutPuts(){
        return outPutRepo.findAll();
    }
    public OutPuts editOutPut(OutPuts outPuts,Long id){
        Optional<OutPuts> outPuts1= outPutRepo.findById(id);
        if(outPuts1.isEmpty()){
            return null;
        }
        outPuts1.get().setName(outPuts.getName());
        outPuts1.get().setWattage(outPuts.getWattage());

        return outPutRepo.save(outPuts1.get());
    }
    public void flip(Long id,Boolean status){
        Optional<OutPuts> outPuts1= outPutRepo.findById(id);
        outPuts1.ifPresent(outPuts-> {
            if(outPuts.isSwitchOn()!=status){
                outPuts.setSwitchOn(status);
                outPutRepo.save(outPuts);
                if(outPuts.isSwitchOn()){
                    statusHistoryRepo.save(new StatusHistory(LocalDateTime.now(),null,outPuts));
                }
                else{
                    StatusHistory s=statusHistoryRepo.findById(id)
                                    .stream()
                                    .max(Comparator.comparing(StatusHistory::getOnTime))
                                    .orElse(new StatusHistory(LocalDateTime.now(),null,outPuts));
                    s.setOffTime(LocalDateTime.now());
                    statusHistoryRepo.save(s);
                }
            }
            else{
                System.out.println("no change");
            }
        });


    }
}
