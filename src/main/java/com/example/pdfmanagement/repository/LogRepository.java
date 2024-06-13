package com.example.pdfmanagement.repository;

import com.example.pdfmanagement.model.Log;
import com.example.pdfmanagement.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByUser(AppUser user);
}
