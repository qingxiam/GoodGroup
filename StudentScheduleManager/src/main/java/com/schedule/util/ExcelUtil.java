package com.schedule.util;

import com.schedule.model.Course;
import com.schedule.model.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

/**
 * Excel工具类
 */
public class ExcelUtil {

    /**
     * 从Excel文件导入课程数据
     */
    public static List<Course> importCoursesFromExcel(String filePath, User user) {
        List<Course> courses = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                System.err.println("Excel文件中未找到工作表");
                return courses;
            }

            // 打印Excel原始数据（用于调试）
            printExcelData(sheet);

            // 遍历行（从第2行开始，跳过表头）
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                Course course = parseCourseFromRow(row, user);
                if (course != null) {
                    courses.add(course);
                }
            }

        } catch (IOException e) {
            System.err.println("读取Excel文件时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * 从Excel行解析课程数据
     */
    private static Course parseCourseFromRow(Row row, User user) {
        try {
            // 读取各列数据（索引0-7对应：课程名称、教师、地点、星期、开始时间、结束时间、类型、描述）
            String name = getCellValueAsString(row.getCell(0));
            String teacher = getCellValueAsString(row.getCell(1));
            String location = getCellValueAsString(row.getCell(2));
            String dayOfWeekStr = getCellValueAsString(row.getCell(3));
            String startTimeStr = getCellValueAsString(row.getCell(4));
            String endTimeStr = getCellValueAsString(row.getCell(5));
            String typeStr = getCellValueAsString(row.getCell(6));
            String description = getCellValueAsString(row.getCell(7));

            // 验证必填字段
            if (name.isEmpty() || dayOfWeekStr.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                System.err.println("第" + (row.getRowNum() + 1) + "行数据不完整，跳过");
                return null;
            }

            // 解析星期
            DayOfWeek dayOfWeek = parseDayOfWeek(dayOfWeekStr);
            if (dayOfWeek == null) {
                System.err.println("第" + (row.getRowNum() + 1) + "行星期格式错误: " + dayOfWeekStr);
                return null;
            }

            // 解析时间
            LocalTime startTime = parseLocalTime(startTimeStr);
            LocalTime endTime = parseLocalTime(endTimeStr);

            // 验证时间顺序
            if (startTime.isAfter(endTime)) {
                System.err.println("第" + (row.getRowNum() + 1) + "行开始时间晚于结束时间");
                return null;
            }

            // 解析课程类型
            Course.CourseType type = parseCourseType(typeStr);

            // 创建课程对象
            Course course = new Course();
            course.setUserId(user.getId());
            course.setName(name);
            course.setTeacher(teacher);
            course.setLocation(location);
            course.setDayOfWeek(dayOfWeek);
            course.setStartTime(startTime);
            course.setEndTime(endTime);
            course.setType(type);
            course.setDescription(description);
            course.setReminderEnabled(true);  // 默认启用提醒

            return course;

        } catch (Exception e) {
            System.err.println("解析第" + (row.getRowNum() + 1) + "行数据时出错: " + e.getMessage());
            return null;
        }
    }

    /**
     * 解析星期字符串为DayOfWeek枚举
     */
    private static DayOfWeek parseDayOfWeek(String dayStr) {
        if (dayStr == null) return null;

        String trimmedDay = dayStr.trim().toUpperCase();

        // 尝试直接解析英文星期（如MONDAY）
        try {
            return DayOfWeek.valueOf(trimmedDay);
        } catch (IllegalArgumentException e) {
            // 处理中文星期（如"星期一"、"周一"）
            switch (trimmedDay) {
                case "星期一": case "周一": return DayOfWeek.MONDAY;
                case "星期二": case "周二": return DayOfWeek.TUESDAY;
                case "星期三": case "周三": return DayOfWeek.WEDNESDAY;
                case "星期四": case "周四": return DayOfWeek.THURSDAY;
                case "星期五": case "周五": return DayOfWeek.FRIDAY;
                case "星期六": case "周六": return DayOfWeek.SATURDAY;
                case "星期日": case "周日": case "星期天": return DayOfWeek.SUNDAY;
                default: return null;
            }
        }
    }

    /**
     * 解析课程类型
     */
    private static Course.CourseType parseCourseType(String typeStr) {
        if (typeStr == null || typeStr.isEmpty()) {
            return Course.CourseType.REQUIRED;  // 默认必修
        }

        String trimmedType = typeStr.trim().toUpperCase();

        for (Course.CourseType type : Course.CourseType.values()) {
            if (type.name().equals(trimmedType) ||
                    type.getDisplayName().contains(trimmedType) ||
                    trimmedType.contains(type.getDisplayName())) {
                return type;
            }
        }

        return Course.CourseType.REQUIRED;  // 默认必修
    }

    /**
     * 获取单元格值作为字符串
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 日期时间格式转换为字符串
                    return cell.getDateCellValue().toString();
                } else {
                    // 数字格式转为字符串（保留原始格式）
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 解析时间字符串为LocalTime（支持多种格式）
     */
//    private static LocalTime parseLocalTime(String timeStr) {
//        if (timeStr == null || timeStr.isEmpty()) {
//            throw new IllegalArgumentException("时间字符串为空");
//        }
//
//        String trimmedTime = timeStr.trim();
//
//        try {
//            // 尝试按HH:MM格式解析（推荐格式，如"08:55"）
//            return LocalTime.parse(trimmedTime);
//        } catch (Exception e1) {
//            try {
//                // 处理Excel日期时间格式（如"Sun Dec 31 08:55:00 CST 1899"）
//                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
//                java.util.Date date = sdf.parse(trimmedTime);
//                return LocalTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
//            } catch (Exception e2) {
//                try {
//                    // 处理简化时间格式（如"8:55"、"8.55"）
//                    if (trimmedTime.contains(":") || trimmedTime.contains(".")) {
//                        // 替换点号为冒号（如8.55 -> 8:55）
//                        String formattedTime = trimmedTime.replace('.', ':');
//                        // 补全小时和分钟（如8:55 -> 08:55）
//                        if (formattedTime.split(":").length == 2) {
//                            String[] parts = formattedTime.split(":");
//                            if (parts[0].length() == 1) parts[0] = "0" + parts[0];
//                            formattedTime = parts[0] + ":" + parts[1];
//                            return LocalTime.parse(formattedTime);
//                        }
//                    } else {
//                        // 处理纯数字时间（如"855"表示8:55）
//                        int minutes = Integer.parseInt(trimmedTime);
//                        int hour = minutes / 100;
//                        int minute = minutes % 100;
//                        return LocalTime.of(hour, minute);
//                    }
//                } catch (Exception e3) {
//                    throw new IllegalArgumentException("无法解析时间格式: " + trimmedTime, e3);
//                }
//            }
//        }
//    }
    /**
     * 解析时间字符串为LocalTime（支持多种格式）
     */
    private static LocalTime parseLocalTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            throw new IllegalArgumentException("时间字符串为空");
        }

        String trimmedTime = timeStr.trim();

        // 尝试按HH:MM格式解析（推荐格式，如"08:55"）
        try {
            return LocalTime.parse(trimmedTime);
        } catch (Exception e1) {
            // 处理Excel日期时间格式（如"Sun Dec 31 08:55:00 CST 1899"）
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                java.util.Date date = sdf.parse(trimmedTime);
                return LocalTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            } catch (Exception e2) {
                // 处理简化时间格式（如"8:55"、"8.55"）
                try {
                    if (trimmedTime.contains(":") || trimmedTime.contains(".")) {
                        // 替换点号为冒号（如8.55 -> 8:55）
                        String formattedTime = trimmedTime.replace('.', ':');
                        // 补全小时和分钟（如8:55 -> 08:55）
                        if (formattedTime.split(":").length == 2) {
                            String[] parts = formattedTime.split(":");
                            if (parts[0].length() == 1) parts[0] = "0" + parts[0];
                            formattedTime = parts[0] + ":" + parts[1];
                            return LocalTime.parse(formattedTime);
                        }
                    }

                    // 处理纯数字时间（如"855"表示8:55）
                    int minutes = Integer.parseInt(trimmedTime);
                    int hour = minutes / 100;
                    int minute = minutes % 100;
                    return LocalTime.of(hour, minute);

                } catch (Exception e3) {
                    // 所有解析方法都失败时，抛出明确的异常
                    throw new IllegalArgumentException("无法解析时间格式: " + trimmedTime, e3);
                }
            }
        }
    }
    /**
     * 打印Excel原始数据（用于调试）
     */
    public static void printExcelData(Sheet sheet) {
        System.out.println("=== Excel原始数据 ===");
        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                System.out.println("行 " + rowIndex + ": 空行");
                continue;
            }

            StringBuilder sb = new StringBuilder("行 " + rowIndex + ": ");
            for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                sb.append("[列 ").append(colIndex).append(": ");
                sb.append(getCellValueAsString(cell)).append("] ");
            }
            System.out.println(sb.toString());
        }
        System.out.println("=====================");
    }

    /**
     * 导出课程数据到Excel文件
     */
    public static boolean exportCoursesToExcel(String filePath, List<Course> courses) {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            // 创建工作表
            Sheet sheet = workbook.createSheet("课程表");

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"课程名称", "教师", "地点", "星期", "开始时间", "结束时间", "课程类型", "描述"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            int rowNum = 1;
            for (Course course : courses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(course.getName());
                row.createCell(1).setCellValue(course.getTeacher());
                row.createCell(2).setCellValue(course.getLocation());
                row.createCell(3).setCellValue(course.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINESE));
                row.createCell(4).setCellValue(course.getStartTime().toString());
                row.createCell(5).setCellValue(course.getEndTime().toString());
                row.createCell(6).setCellValue(course.getType().getDisplayName());
                row.createCell(7).setCellValue(course.getDescription());
            }

            // 调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入文件
            workbook.write(fos);
            return true;

        } catch (IOException e) {
            System.err.println("导出Excel文件时出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成Excel模板文件
     */
    public static boolean generateExcelTemplate(String filePath) {
        List<Course> emptyCourses = new ArrayList<>();
        return exportCoursesToExcel(filePath, emptyCourses);
    }
}
