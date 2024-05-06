package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserInOut;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        userRepository.save(user);

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Integer id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user id" + id));

        return user;
    }

    public void updateUser(Integer id, User user) {
        userRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user id" + id));

        user.setId(id);
        userRepository.save(user);
    }

    //in & out

//    public void registerEntry(User user) {
//        UserInOut record = new UserInOut();
//        record.setcheckInTime(new Date());
//        record.setUser(user);
//        entryExitRecordRepository.save(record);
//        System.out.println("Wejście zarejestrowane: " + record.getEntryTime());
//    }
//
//    public void registerExit() {
//        List<UserInOut> records = userRepository.findAll();
//        if (!records.isEmpty()) {
//            UserInOut lastRecord = records.get(records.size() - 1);
//            if (lastRecord.getExitTime() == null) {
//                lastRecord.setExitTime(new Date());
//                entryExitRecordRepository.save(lastRecord);
//                System.out.println("Wyjście zarejestrowane: " + lastRecord.getExitTime());
//            } else {
//                System.out.println("Już zarejestrowano wyjście.");
//            }
//        } else {
//            System.out.println("Nie można zarejestrować wyjścia, brak wejścia.");
//        }
    }

