package com.txt.convertor.service.impl;

import com.txt.convertor.service.ConversionService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConversionServiceImpl implements ConversionService {

    private String normalizeSeparator(String separator) {
        if (separator == null)
            return null;

        return switch (separator) {
            case "\\t" -> "\t";
            case "\\n" -> "\n";
            case "\\r" -> "\r";
            default -> separator;
        };
    }

    private List<String[]> parseFile(MultipartFile file, String separator) throws Exception {

        List<String[]> rows = new ArrayList<>();

        String content = new String(file.getBytes());

        // Escape separator for regex
        String sep = Pattern.quote(separator);

        // Split records using ID pattern: number|Name|City|Salary
        Pattern rowPattern = Pattern
                .compile("(\\d+" + sep + "[^" + sep + "]+" + sep + "[^" + sep + "]+" + sep + "\\d+)");
        Matcher matcher = rowPattern.matcher(content);

        while (matcher.find()) {
            String row = matcher.group(1);
            rows.add(row.split(sep));
        }

        // Add header separately (first occurrence before first number)
        if (!rows.isEmpty()) {
            String header = content.substring(0, matcher.reset().find() ? matcher.start() : content.length()).trim();
            if (header.contains(separator))
                rows.add(0, header.split(sep));
        }

        return rows;
    }

    @Override
    public void convertToCSV(MultipartFile file, String separator, OutputStream os) throws Exception {
        List<String[]> rows = parseFile(file, normalizeSeparator(separator));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

        for (String[] row : rows) {
            writer.write(String.join(",", row));
            writer.newLine();
        }
        writer.flush();
    }

    @Override
    public void convertToExcel(MultipartFile file, String separator, OutputStream os) throws Exception {
        List<String[]> rows = parseFile(file, normalizeSeparator(separator));
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        int r = 0;
        for (String[] rowData : rows) {
            Row row = sheet.createRow(r++);
            for (int c = 0; c < rowData.length; c++) {
                row.createCell(c).setCellValue(rowData[c]);
            }
        }

        workbook.write(os);
        workbook.close();
    }
}