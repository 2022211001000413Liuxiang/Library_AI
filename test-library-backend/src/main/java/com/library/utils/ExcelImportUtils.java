package com.library.utils;

import com.library.entity.Book;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelImportUtils {

    private static final Logger log = LoggerFactory.getLogger(ExcelImportUtils.class);

    private static final String[] HEADERS = {"书名", "作者", "分类", "出版社", "出版日期", "库存", "简介", "ISBN"};
//使用apach的poi解析excel
    public static Map<String, Object> parseBooks(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        List<Book> books = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            if (lastRow < 1) {
                result.put("success", false);
                result.put("message", "Excel 文件为空，无数据可导入");
                return result;
            }

            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) continue;

                try {
                    Book book = parseRow(row, i);
                    books.add(book);
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                    log.warn("Excel 第{}行解析失败: {}", i + 1, e.getMessage());
                }
            }

            result.put("success", true);
            result.put("books", books);
            result.put("errors", errors);
            result.put("totalRows", lastRow);
            result.put("validCount", books.size());
            result.put("errorCount", errors.size());

        } catch (Exception e) {
            log.error("Excel 解析异常", e);
            result.put("success", false);
            result.put("message", "文件解析失败: " + e.getMessage());
        }

        return result;
    }

    private static Book parseRow(Row row, int rowNum) {
        String name = getCellStringValue(row.getCell(0));
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("书名不能为空");
        }

        String author = getCellStringValue(row.getCell(1));
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("作者不能为空");
        }

        String category = getCellStringValue(row.getCell(2));
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("分类不能为空");
        }

        Book book = new Book();
        book.setName(name.trim());
        book.setAuthor(author.trim());
        book.setCategory(category.trim());
        book.setPublisher(getCellStringValue(row.getCell(3)));
        book.setDescription(getCellStringValue(row.getCell(6)));
        book.setIsbn(getCellStringValue(row.getCell(7)));

        // 出版日期
        Cell dateCell = row.getCell(4);
        if (dateCell != null) {
            //1.单元格类型是数字2.该数字被格式化为日期样式（如 "2022/1/1" 而非 "44562"）
            if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                book.setPublishDate(dateCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } else {
                String dateStr = getCellStringValue(dateCell);
                if (dateStr != null && !dateStr.isEmpty()) {
                    book.setPublishDate(LocalDate.parse(dateStr));
                }
            }
        }

        // 库存
        Cell stockCell = row.getCell(5);
        if (stockCell != null && stockCell.getCellType() == CellType.NUMERIC) {
            book.setStock((int) stockCell.getNumericCellValue());
        } else {
            book.setStock(0);
        }

        return book;
    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }

    private static boolean isEmptyRow(Row row) {
        for (int i = 0; i < 3; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public static String[] getHeaders() {
        return HEADERS;
    }
}
