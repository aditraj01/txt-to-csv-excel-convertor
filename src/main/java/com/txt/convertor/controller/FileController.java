package com.txt.convertor.controller;

import com.txt.convertor.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class FileController {

    @Autowired
    private ConversionService conversionService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/convert")
    public void convertFile(@RequestParam("file") MultipartFile file,
            @RequestParam("separator") String separator,
            @RequestParam("type") String type,
            HttpServletResponse response) throws Exception {

        /* ---------- VALIDATIONS ---------- */

        if (file.isEmpty())
            throw new RuntimeException("File is empty");

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".txt"))
            throw new RuntimeException("Only .txt files are allowed");

        if (separator == null || separator.isBlank())
            throw new RuntimeException("Separator is required");

        if (!type.equals("csv") && !type.equals("excel"))
            throw new RuntimeException("Invalid output type");

        /* ---------- OUTPUT FILE NAME ---------- */

        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        /* ---------- STREAM FILE ---------- */

        if (type.equals("csv")) {

            String output = baseName + ".csv";

            response.setContentType("text/csv");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + output + "\"");

            conversionService.convertToCSV(file, separator, response.getOutputStream());

        } else {

            String output = baseName + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + output + "\"");

            conversionService.convertToExcel(file, separator, response.getOutputStream());
        }

        response.flushBuffer();
    }
}