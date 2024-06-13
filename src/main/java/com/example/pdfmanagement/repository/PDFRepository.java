package com.example.pdfmanagement.repository;

import com.example.pdfmanagement.model.PDF;
import com.example.pdfmanagement.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PDFRepository extends JpaRepository<PDF, Long> {
    List<PDF> findAllByUser(AppUser user);
}
