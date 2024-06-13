package com.example.pdfmanagement.controller;

import com.example.pdfmanagement.model.PDF;
import com.example.pdfmanagement.model.AppUser;
import com.example.pdfmanagement.service.CustomUserDetails;
import com.example.pdfmanagement.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PDFService pdfService;

    @GetMapping("/upload")
    public String uploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadPDF(@RequestParam("file") MultipartFile file, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AppUser user = userDetails.getAppUser();

        try {
            pdfService.uploadPDF(file, user);
        } catch (IOException e) {
            model.addAttribute("error", "File upload failed: " + e.getMessage());
            return "upload";
        }
        return "redirect:/user/pdfs";
    }

    @GetMapping("/pdfs")
    public String viewPDFs(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AppUser user = userDetails.getAppUser();

        List<PDF> pdfs = pdfService.getUserPDFs(user);
        model.addAttribute("pdfs", pdfs);
        return "pdfs";
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<byte[]> viewPDF(@PathVariable("id") Long id) {
        try {
            PDF pdf = pdfService.getPDFById(id);
            byte[] fileContent = pdfService.getPDFContent(pdf.getPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", pdf.getName());
            return ResponseEntity.ok().headers(headers).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/split")
    public ResponseEntity<byte[]> splitPDF(@RequestParam("pdfId") Long pdfId,
                                           @RequestParam("fromPage") int fromPage,
                                           @RequestParam("toPage") int toPage) {
        try {
            byte[] splitPDF = pdfService.splitPDF(pdfId, fromPage, toPage);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "split.pdf");
            return ResponseEntity.ok().headers(headers).body(splitPDF);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPDF(@RequestParam("pdfId") Long pdfId) {
        try {
            PDF pdf = pdfService.getPDFById(pdfId);
            byte[] fileContent = pdfService.getPDFContent(pdf.getPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", pdf.getName());
            return ResponseEntity.ok().headers(headers).body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
