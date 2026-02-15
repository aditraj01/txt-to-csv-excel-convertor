package com.txt.convertor.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;

public interface ConversionService {
    void convertToCSV(MultipartFile file, String separator, OutputStream os) throws Exception;
    void convertToExcel(MultipartFile file, String separator, OutputStream os) throws Exception;
}