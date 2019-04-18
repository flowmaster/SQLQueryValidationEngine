package core.jsqlAss;

import java.util.HashMap;
import java.util.Set;

import net.sf.jsqlparser.statement.Statement;

public class AlterTable implements ValidationConstants {


	AlterTable(String invoke)
	{
		
	}
	
	public static boolean isAlterTableInstance(String statement)
	{
		 return statement.toString().trim().toUpperCase().startsWith(ValidationConstants.ALTER);
	}
	
	public static String getAlterType(String statement)
	{
		String alterQuery = statement.toString() ;
		int altOperTypevalue = getAlterOperationType(alterQuery);
		String alterType = null;
		switch(altOperTypevalue)
		{
		case ValidationConstants.ADD :
		{
			alterType = ValidationConstants.ALTER_ADD ;
			break;
		}
		case ValidationConstants.DROP :
		{
			alterType = ValidationConstants.DROPE_ACTION ;
			break;
		}
		case ValidationConstants.MODIFY :
		{
			alterType = ValidationConstants.ALTER_MODIFY ;
			break;
		}
		}
		return alterType ;
	}
	
	private static int getAlterOperationType(String alterQuery)
	{
		String[] altQueryArr = alterQuery.split(ValidationConstants.SPACE);
		int alterOperName =0 ;
		for(String altQueryStr : altQueryArr )
		{
			if(altQueryStr.equalsIgnoreCase(ValidationConstants.ALTER_ADD)
					|| altQueryStr.startsWith(ValidationConstants.ALTER_ADD+ValidationConstants.SBRA) )
			{
				alterOperName =ValidationConstants.ADD;
			}
			
			else if(altQueryStr.equalsIgnoreCase(ValidationConstants.DROPE_ACTION)
					|| altQueryStr.startsWith(ValidationConstants.DROPE_ACTION))
			{
				alterOperName =ValidationConstants.DROP;
			}
			else if(altQueryStr.equalsIgnoreCase(ValidationConstants.ALTER_MODIFY)
					|| altQueryStr.startsWith(ValidationConstants.ALTER_MODIFY+ValidationConstants.SBRA))
			{
				alterOperName =ValidationConstants.MODIFY;
			}
		}
		return alterOperName ;
	}
	
	public static String getWholeTableName(String statement)
	{
		String alterQuery = statement.toString() ;
		String[] altQueryArr = alterQuery.split(ValidationConstants.SPACE);
		String wholeTableName = null ;
		for(int count =0 ;count < altQueryArr.length ;count ++)
		{
			if(altQueryArr[count].equalsIgnoreCase(ValidationConstants.TABLE))
			{
				wholeTableName = altQueryArr[count+1] ;
				break;
			}
		}
		return wholeTableName ;
	}
	public static String getAlterColumnName(String statement)
	{
		String alterColName = null ;
		String alterOperName= null ;
		alterOperName =getAlterType(statement);
		if(alterOperName != null)
		{
			if(alterOperName.equalsIgnoreCase(ALTER_ADD))
			{
				alterColName= getQualifyColName(getParseColumn(statement,getWholeTableName(statement),ALTER_ADD)) ;
			}
			
			if(alterOperName.equalsIgnoreCase(DROPE_ACTION))
			{
				alterColName = getParseColumn(statement, getWholeTableName(statement)) ;
			}
			if(alterOperName.equalsIgnoreCase(ALTER_MODIFY))
			{
				alterColName= getQualifyColName(getParseColumn(statement,getWholeTableName(statement),ALTER_MODIFY)) ; // TODO check the data type size from all tab column and reframe the val sql 
			}
		}
		alterColName = alterColName.substring(0, alterColName.length() -1);
		return alterColName ;
	}
	
	private static String getQualifyColName(HashMap<String ,String> qualColNameHm)
	{
		String qualifyColName = ValidationConstants.SPACE ;
		Set<String> colNameSet = qualColNameHm.keySet();
		for(String name : colNameSet)
		{
			qualifyColName  +=SCOLON+ name+SCOLON +ValidationConstants.ALTER_COL_NAME_CONJUCTION ;
		}
		
		return qualifyColName.trim() ;
	}
	
	private static HashMap<String, String> getParseColumn(String alterQuery, String tableName,String operation)
	{
		HashMap <String ,String> colDefmap = new HashMap<String ,String>(); 
		String[] parseColArray = alterQuery.split(tableName);
		String columnDef = parseColArray[1].replaceFirst(operation, ValidationConstants.SPACE).replace(ValidationConstants.STATEMENT_END, ValidationConstants.SPACE).trim();
		String qualifyColDef = columnDef.substring(1, columnDef.length() -1);		//remove simple bracket from the column list
		String[] qualifyColDefArr = qualifyColDef.split(ValidationConstants.SEC_COMMA);
		for(String colDef : qualifyColDefArr)
		{
			String[] colDefArr = colDef.trim().split(ValidationConstants.SPACE);
			colDefmap.put(colDefArr[0], colDefArr[1]);// add the param to map as column name and its data type 
		}
		return colDefmap ;
	}
	
	private static String getParseColumn(String alterQuery ,String tableName)
	{
		String[] parseColArray = alterQuery.split(tableName);
		String columnDef = parseColArray[1].replaceFirst(DROPE_ACTION, SPACE).replaceFirst(DROP_COLUMN, SPACE).replace(STATEMENT_END, SPACE).trim();
		String[] columnDefArr = columnDef.split(SEC_COMMA);
		String allColName= SPACE ;
		for(String colaName : columnDefArr)
		{
			allColName +=SCOLON+ colaName+SCOLON +ValidationConstants.ALTER_COL_NAME_CONJUCTION  ; 
		}
		
		return allColName.trim();
	}
	public static void main(String[] args) {
		String x= "ALTER TABLE SSP_FLOW.SSP_ORDER_DETAILS "
				+ "ADD(VISION_ID VARCHAR2(30))" ;
		String[] arr = x.split(ValidationConstants.ALTER_ADD) ;
		for(String ax :arr)
		{
			System.out.println(ax);
		}
	}
	
	public static String formValAlterQeury(String statement)
	{
		String vapQuery = ALTER_VAL_SQL +AlterTable.getAlterColumnName(statement) + EBRA +SPACE+AND +TABLE_NAME+EQUAL+SCOLON+AlterTable.getWholeTableName(statement)+SCOLON ;
		return vapQuery ;
	}
	

}
