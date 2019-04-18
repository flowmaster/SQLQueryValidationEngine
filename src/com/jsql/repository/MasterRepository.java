package com.jsql.repository;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
 













import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
/**
 * A very simple program that writes some data to an Excel file
 * using the Apache POI library.
 * @author www.codejava.net
 *
 */












import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import core.jsqlAss.Query;

public class MasterRepository {
	
	public static MasterRepository getInstance()
	{
		return RepositoryHolder.INSTANCE;
	}

	public void writeExcel(List<Book> listBook, String excelFilePath,boolean x) throws IOException {
		Workbook workbook = getWorkbook(excelFilePath);
		Sheet sheet = workbook.createSheet();
		
		createHeaderRow(sheet);
		
		int rowCount = 0;
		
		for (Book aBook : listBook) {
			Row row = sheet.createRow(++rowCount);
			writeBook(aBook, row);
		}
		
		try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
			workbook.write(outputStream);
		}		
	}
	
	public void writeExcel(List<Query> queryModelList,String excelFilePath) throws IOException 
	{

		Workbook workbook = getWorkbook(excelFilePath);
		Sheet sheet = workbook.createSheet();
		
		createHeaderRow(sheet);
		
		int rowCount = 0;
		
		for (Query query : queryModelList) {
			Row row = sheet.createRow(++rowCount);
			writeQuery(query, row);
		}
		
		try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
			workbook.write(outputStream);
			outputStream.close();
		}		
	
	}
	
	private void writeQuery(Query query,Row row)
	{

		Cell cell = row.createCell(1);
		cell.setCellValue(query.getFileName());

		cell = row.createCell(2);
		cell.setCellValue(query.getQuery());
		
		cell = row.createCell(3);
		cell.setCellValue(query.getValidationQuery());
		
		cell = row.createCell(4);
		cell.setCellValue(query.getResult().doubleValue());
	
	}
	
	private void writeBook(Book aBook, Row row) {
		Cell cell = row.createCell(1);
		cell.setCellValue(aBook.getTitle());

		cell = row.createCell(2);
		cell.setCellValue(aBook.getAuthor());
		
		cell = row.createCell(3);
		cell.setCellValue(aBook.getPrice());
		
	}
	
private void createHeaderRow(Sheet sheet) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		cellStyle.setFont(font);
				
		Row row = sheet.createRow(0);
		Cell cellFLName = row.createCell(1);
		cellFLName.setCellStyle(cellStyle);
		cellFLName.setCellValue("File Name");
		
		Cell cellExeQuery = row.createCell(2);
		cellExeQuery.setCellStyle(cellStyle);
		cellExeQuery.setCellValue("Executed Query");
		
		Cell cellValQuery = row.createCell(3);
		cellValQuery.setCellStyle(cellStyle);
		cellValQuery.setCellValue("Validation Query");
		
		Cell cellResult = row.createCell(4);
		cellResult.setCellStyle(cellStyle);
		cellResult.setCellValue("Validation Result");
		
	}
	
	private List<Book> getListBook() {
		Book book1 = new Book("Head First Java", "Kathy Serria", 79);
		Book book2 = new Book("Effective Java", "Joshua Bloch", 36);
		Book book3 = new Book("Clean Code", "Robert Martin", 42);
		Book book4 = new Book("Thinking in Java", "Bruce Eckel", 35);
		
		List<Book> listBook = Arrays.asList(book1, book2, book3, book4);
		
		return listBook;
	}
	
	private Workbook getWorkbook(String excelFilePath) 
			throws IOException {
		Workbook workbook = null;
		
		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook();
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook();
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}
		
		return workbook;
	}
	
	public void doSome() throws IOException
	{

		MasterRepository excelWriter = new MasterRepository();
		List<Book> listBook = excelWriter.getListBook();
		/*String excelFilePath = "JavaBooks1.xls";
		excelWriter.writeExcel(listBook, excelFilePath);
		*/
		String excelFilePath = "JavaBooks2.xlsx";
		excelWriter.writeExcel(listBook, excelFilePath,true);
	
	}
	
	private static class RepositoryHolder{
		
		private static final MasterRepository INSTANCE = new MasterRepository();
	}
	
	public void readConfig(String fileName)
	{
		try
		{
			FileInputStream file = new FileInputStream(new File(fileName));

			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			Map<String, String> configMap = new HashMap<String, String>();
			while (rowIterator.hasNext()) 
			{
				Row row = rowIterator.next();
				configMap.put(row.getCell(0).toString(), row.getCell(0).toString());
			}
			System.out.println(configMap.size());
			file.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) throws IOException {
		MasterRepository excelWriter = new MasterRepository();
		/*List<Book> listBook = excelWriter.getListBook();
		String excelFilePath = "JavaBooks1.xls";
		excelWriter.writeExcel(listBook, excelFilePath);
		
		String excelFilePath = "JavaBooks2.xlsx";
		excelWriter.writeExcel(listBook, excelFilePath,true);*/
		
		excelWriter.readConfig("E:/Sambed/OperationConfig/RobotConfig.xlsx");
	}
	
}



