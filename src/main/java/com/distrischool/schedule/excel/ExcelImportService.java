package com.distrischool.schedule.excel;

import com.distrischool.schedule.entity.Class;
import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.entity.Subject;
import com.distrischool.schedule.repository.ClassRepository;
import com.distrischool.schedule.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para importação de horários via Excel.
 */
@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public List<Schedule> importSchedulesFromExcel(MultipartFile file) throws IOException {
        List<Schedule> schedules = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Ignora a primeira linha (cabeçalho)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Schedule schedule = parseScheduleRow(row);
                    if (schedule != null) {
                        schedules.add(schedule);
                    }
                } catch (Exception e) {
                    // Log erro e continua com a próxima linha
                    System.err.println("Erro ao processar linha " + i + ": " + e.getMessage());
                }
            }
        }

        return schedules;
    }

    private Schedule parseScheduleRow(Row row) {
        // Formato esperado: Class Code | Subject Code | Day | Start Time | End Time | Room | Teacher ID
        String classCode = getCellValue(row.getCell(0));
        String subjectCode = getCellValue(row.getCell(1));
        String dayStr = getCellValue(row.getCell(2));
        String startTimeStr = getCellValue(row.getCell(3));
        String endTimeStr = getCellValue(row.getCell(4));
        String room = getCellValue(row.getCell(5));
        String teacherIdStr = getCellValue(row.getCell(6));

        if (classCode == null || subjectCode == null || dayStr == null || 
            startTimeStr == null || endTimeStr == null) {
            return null;
        }

        Class classEntity = classRepository.findByCode(classCode)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada: " + classCode));

        Subject subject = subjectRepository.findByCode(subjectCode)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada: " + subjectCode));

        DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayStr.toUpperCase());
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);
        Long teacherId = teacherIdStr != null && !teacherIdStr.isEmpty() ? Long.parseLong(teacherIdStr) : null;

        return Schedule.builder()
                .classEntity(classEntity)
                .subject(subject)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .room(room)
                .teacherId(teacherId)
                .active(true)
                .build();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}
