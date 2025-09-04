package com.tanre.document_register.service;


import com.tanre.document_register.repository.ReportDAO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final ReportDAO dao;

    public ReportService(ReportDAO dao) {
        this.dao = dao;
    }

    public ByteArrayInputStream fetchClaimDocumentReport(
            LocalDate startDate,
            LocalDate endDate
    ) throws IOException {
        List<Map<String, Object>> rows = dao.getClaimDocumentReport(startDate
                , endDate);

        // Create workbook with tracking options
        SXSSFWorkbook wb = new SXSSFWorkbook();
        wb.setCompressTempFiles(true);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Cast to SXSSFSheet to access streaming-specific methods
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("Claim Document " +
                    "Report");

            // Enable tracking all columns for auto-sizing
            sheet.trackAllColumnsForAutoSizing();

            if (!rows.isEmpty()) {

                CellStyle headerStyle = wb.createCellStyle();
                headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(128, 0, 128), new DefaultIndexedColorMap()));
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                Map<String, Object> first = rows.get(0);

                // 1) Title row at index 0
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("Claim Document Report");

                // 2) Style the title
                CellStyle titleStyle = wb.createCellStyle();
                Font titleFont = wb.createFont();
                titleFont.setFontHeightInPoints((short) 16);
                titleFont.setBold(true);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);
                titleCell.setCellStyle(titleStyle);

                // 3) Merge cells from col 0 → last column (first.keySet()
                // .size()-1)
                int lastCol = first.keySet().size() - 1;
                sheet.addMergedRegion(new CellRangeAddress(
                        /*firstRow=*/0, /*lastRow=*/0,
                        /*firstCol=*/0, /*lastCol=*/lastCol
                ));

                // Header
                Row header = sheet.createRow(2);
                int ci = 0;
                for (String col : first.keySet()) {
                    header.createCell(ci++).setCellValue(col);
                    Font headerFont = wb.createFont();
                    headerFont.setBold(true);
                    CellStyle headerCellStyle = wb.createCellStyle();
                    headerCellStyle.setFont(headerFont);
                    headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                    header.getCell(ci - 1).setCellStyle(headerCellStyle);
                }

                // Data
                for (int ri = 0; ri < rows.size(); ri++) {
                    Row row = sheet.createRow(ri + 3);
                    Map<String, Object> data = rows.get(ri);
                    int cj = 0;
                    for (Object val : data.values()) {
                        Cell cell = row.createCell(cj++);
                        if (val instanceof Number n) {
                            cell.setCellValue(n.doubleValue());
                        } else if (val instanceof Date d) {
                            CellStyle style = wb.createCellStyle();
//                            style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
//                            style.setFillPattern(FillPatternType.ALT_BARS);
                            style.setDataFormat(
                                    wb.getCreationHelper()
                                            .createDataFormat()
                                            .getFormat("yyyy-MM-dd"));
                            cell.setCellStyle(style);
                            cell.setCellValue(d);

                        } else {
                            cell.setCellValue(val == null ? "" : val.toString());
                        }
                    }
                }

                // Auto-size
                for (int c = 0; c < first.size(); c++) {
                    sheet.autoSizeColumn(c);
                }
            }

            wb.write(out);
            byte[] bytes = out.toByteArray();

            // Close the workbook manually after writing to the output stream
            wb.close();

            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            // Make sure to close the workbook even if an exception occurs
            wb.close();
            throw e;
        }

    }


    public ByteArrayInputStream fetchDebitNoteReport(
            LocalDate startDate,
            LocalDate endDate,
            String userName
    ) throws IOException {
        List<Map<String, Object>> rows = dao.getDebitNoteReport(
                startDate,
                endDate,
                userName
        );

        // Create workbook with tracking options
        SXSSFWorkbook wb = new SXSSFWorkbook();
        wb.setCompressTempFiles(true);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Cast to SXSSFSheet to access streaming-specific methods
            SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(userName +
                    " Debit-Credit-Note Report");

            // Enable tracking all columns for auto-sizing
            sheet.trackAllColumnsForAutoSizing();

            if (!rows.isEmpty()) {

                CellStyle headerStyle = wb.createCellStyle();
                headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(128, 0, 128), new DefaultIndexedColorMap()));
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                Map<String, Object> first = rows.get(0);

                // 1) Title row at index 0
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("Debit-Credit-Note Report");

                // 2) Style the title
                CellStyle titleStyle = wb.createCellStyle();
                Font titleFont = wb.createFont();
                titleFont.setFontHeightInPoints((short) 16);
                titleFont.setBold(true);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);
                titleCell.setCellStyle(titleStyle);

                // 3) Merge cells from col 0 → last column (first.keySet().size()-1)
                int lastCol = first.keySet().size() - 1;
                sheet.addMergedRegion(new CellRangeAddress(
                        /*firstRow=*/0, /*lastRow=*/0,
                        /*firstCol=*/0, /*lastCol=*/lastCol
                ));

                // Header
                Row header = sheet.createRow(2);
                int ci = 0;
                for (String col : first.keySet()) {
                    header.createCell(ci++).setCellValue(col);
                    Font headerFont = wb.createFont();
                    headerFont.setBold(true);
                    CellStyle headerCellStyle = wb.createCellStyle();
                    headerCellStyle.setFont(headerFont);
                    headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                    header.getCell(ci - 1).setCellStyle(headerCellStyle);
                }

                // Data
                for (int ri = 0; ri < rows.size(); ri++) {
                    Row row = sheet.createRow(ri + 3);
                    Map<String, Object> data = rows.get(ri);
                    int cj = 0;
                    for (Object val : data.values()) {
                        Cell cell = row.createCell(cj++);
                        if (val instanceof Number n) {
                            cell.setCellValue(n.doubleValue());
                        } else if (val instanceof Date d) {
                            CellStyle style = wb.createCellStyle();
//                            style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
//                            style.setFillPattern(FillPatternType.ALT_BARS);
                            style.setDataFormat(
                                    wb.getCreationHelper()
                                            .createDataFormat()
                                            .getFormat("yyyy-MM-dd"));
                            cell.setCellStyle(style);
                            cell.setCellValue(d);

                        } else {
                            cell.setCellValue(val == null ? "" : val.toString());
                        }
                    }
                }

                // Auto-size
                for (int c = 0; c < first.size(); c++) {
                    sheet.autoSizeColumn(c);
                }
            }

            wb.write(out);
            byte[] bytes = out.toByteArray();

            // Close the workbook manually after writing to the output stream
            wb.close();

            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            // Make sure to close the workbook even if an exception occurs
            wb.close();
            throw e;
        }

    }


}
