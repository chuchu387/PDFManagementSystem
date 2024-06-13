package com.example.pdfmanagement.service;

import com.example.pdfmanagement.model.PDF;
import com.example.pdfmanagement.model.AppUser;
import com.example.pdfmanagement.repository.PDFRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PDFService {

    private static final long MAX_FILE_SIZE = 10485760; // 10 MB

    @Autowired
    private PDFRepository pdfRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public PDF uploadPDF(MultipartFile file, AppUser user) throws IOException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds the maximum limit");
        }

        Path userUploadPath = Paths.get(uploadDir, String.valueOf(user.getId()));
        if (!Files.exists(userUploadPath)) {
            Files.createDirectories(userUploadPath);
        }

        Path filePath = userUploadPath.resolve(file.getOriginalFilename());
        file.transferTo(filePath.toFile());

        PDF pdf = new PDF();
        pdf.setName(file.getOriginalFilename());
        pdf.setPath(filePath.toString());
        pdf.setSize(file.getSize());
        pdf.setUploadTime(LocalDateTime.now());
        pdf.setUser(user);

        return pdfRepository.save(pdf);
    }

    public List<PDF> getUserPDFs(AppUser user) {
        return pdfRepository.findAllByUser(user);
    }

    public PDF getPDFById(Long pdfId) {
        return pdfRepository.findById(pdfId).orElseThrow(() -> new RuntimeException("PDF not found"));
    }

    public byte[] getPDFContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public byte[] splitPDF(Long pdfId, int fromPage, int toPage) throws IOException {
        PDF pdf = pdfRepository.findById(pdfId).orElseThrow(() -> new RuntimeException("PDF not found"));
        PDDocument document = PDDocument.load(new File(pdf.getPath()));
        PDDocument splitDocument = new PDDocument();
        for (int i = fromPage - 1; i < toPage; i++) {
            splitDocument.addPage(document.getPage(i));
        }

        Path splitPath = Paths.get(pdf.getPath().replace(".pdf", "_split.pdf"));
        splitDocument.save(splitPath.toFile());
        splitDocument.close();
        document.close();

        return Files.readAllBytes(splitPath);
    }
}
