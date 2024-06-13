package com.example.pdfmanagement.service;

import com.example.pdfmanagement.model.Log;
import com.example.pdfmanagement.model.AppUser;
import com.example.pdfmanagement.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public void createLog(AppUser user, String action) {
        Log log = new Log();
        log.setUser(user);
        log.setAction(action);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }

    public List<Log> getUserLogs(AppUser user) {
        return logRepository.findAllByUser(user);
    }

    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }
}
