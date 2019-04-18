package core.jsqlAss;

import java.util.ArrayList;
import java.util.HashMap;

public class Query {

	private String query = null ;
	private ArrayList<StringBuffer> listQuery = null;
	private String fileName = null ;
	private String operation = null ;
	private HashMap<String,Integer> validationSQl = null ;  // keep the value as Query and value as the db return value.
	private Integer result = 0;
	private String validationQuery = null;
	private String message = null;
	
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	public String getValidationQuery() {
		return validationQuery;
	}
	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}
	public HashMap<String, Integer> getValidationSQl() {
		return validationSQl;
	}
	public void setValidationSQl(HashMap<String, Integer> validationSQl) {
		this.validationSQl = validationSQl;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public ArrayList<StringBuffer> getListQuery() {
		return listQuery;
	}
	public void setListQuery(ArrayList<StringBuffer> listQuery) {
		this.listQuery = listQuery;
	}
	
}
