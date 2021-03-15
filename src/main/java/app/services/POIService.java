package app.services;

import app.Models.Game;
import app.Models.Player;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class POIService {
    private HSSFWorkbook workbook;
    private Map<String, CellStyle> cellStyleMap = new HashMap<>();
    private HSSFSheet sheet;

    public void fillInReport(Game game) throws IOException {
        // Создать новый документ
        workbook = new HSSFWorkbook(new FileInputStream("src/main/resources/static/Report.xls"));
        // Создает стили документа
        createAllCellStyle();
        sheet = workbook.getSheetAt(0);
        fillInTeamTitle(game.getMasterTeam().getTeamName(), "B");
        fillInTeamTitle(game.getSlaveTeam().getTeamName(), "I");
        fillInTeamPlayers(game.getMasterTeam().getPlayers(), "B");
        fillInTeamPlayers(game.getSlaveTeam().getPlayers(), "H");
        fillInDate(game.getDate());
        saveFile();
    }

    private void fillInTeamTitle(String teamName, String columnName) {
        CellReference cr = new CellReference(columnName + 9);
        Row row = sheet.getRow(cr.getRow());
        Cell cell = row.getCell(cr.getCol());
        cell.setCellStyle(cellStyleMap.get("team"));
        cell.setCellValue(teamName.length() > 20 ? teamName.substring(0, 20) : teamName);
    }

    private void fillInTeamPlayers(Collection<Player> source, String columnName) {
        ArrayList<Player> list = new ArrayList<>(source);
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            CellReference cr = new CellReference(columnName + (i + 13));
            Row row = sheet.getRow(cr.getRow());
            Cell cell = row.getCell(cr.getCol());
            cell.setCellStyle(cellStyleMap.get("number"));
            cell.setCellValue((double) (i + 1));
            cell = row.getCell(cr.getCol() + 1);
            cell.setCellValue(list.get(i).getLastName() + " " + list.get(i).getFirstName());
        }
    }

    private void fillInDate(Date date) {
        CellReference cr = new CellReference("E5");
        Row row = sheet.getRow(cr.getRow());
        Cell cell = row.getCell(cr.getCol());
        cell.setCellStyle(cellStyleMap.get("date"));
        cell.setCellValue(date);
    }

    private void createAllCellStyle() {
        cellStyleMap.put("number", createCellStyleForTitle());
        cellStyleMap.put("team", createCellStyleForTeam());
        cellStyleMap.put("date", createCellStyleForDate());
    }

    private CellStyle createCellStyleForTitle() {
        Font newFont = workbook.createFont();
        newFont.setBold(false);
        newFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        newFont.setFontHeightInPoints((short) 11);
        newFont.setItalic(false);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createCellStyleForTeam() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        newFont.setFontHeightInPoints((short) 16);
        newFont.setItalic(false);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createCellStyleForDate() {
        Font newFont = workbook.createFont();
        newFont.setBold(true);
        newFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        newFont.setFontHeightInPoints((short) 11);
        newFont.setItalic(false);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(newFont);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));
        return cellStyle;
    }

    private void saveFile() {
        try {
            File file = new File("src/main/resources/static/Report+.xls");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream outFile = new FileOutputStream("src/main/resources/static/Report+.xls");
            workbook.write(outFile);
            outFile.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
