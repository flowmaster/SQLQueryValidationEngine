package core.jsqlAss;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.jsqlAss.common.Util;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import core.jsqlAss.ValidationConstants.MISCELLANEOUS;

public class Miscellaneous {

	private MISCELLANEOUS misc = null ;
	
	public static String substring(String statement,String arg0,String arg1)
	{
		return statement.substring(statement.indexOf(arg0)+arg0.length(),statement.indexOf(arg1));
	}
	
	public static String stringOnList(List<String> arg1 )
	{
		String conversionString = "";
		if(arg1!= null)
		{
			for(String arg0 :arg1)
			{
				conversionString+=ValidationConstants.COMMA+arg0;
			}
			conversionString=conversionString.replaceFirst(ValidationConstants.COMMA, ValidationConstants.SPACE);
		}
		return conversionString;
	}
	
	public static boolean isMiscInstance(String statement)
	{
		boolean isMiscellaneous = false;
		if(statement.toUpperCase().trim().startsWith(ValidationConstants.GRANT))
		{
			isMiscellaneous = true ;
		}
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.CREATE)
				&& statement.toUpperCase().trim().contains(ValidationConstants.INDEX))
		{
			isMiscellaneous = true ;
		}
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.DROPE_ACTION)
				&& statement.toUpperCase().trim().contains(ValidationConstants.INDEX))
		{
			isMiscellaneous = true ;
		}
		
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.CREATE)
				&& statement.toUpperCase().trim().contains(ValidationConstants.VIEW))
		{
			isMiscellaneous = true ;
		}
		
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.CREATE)
				&& statement.toUpperCase().trim().contains(ValidationConstants.VIEW))
		{
			isMiscellaneous = true ;
		}
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.DROPE_ACTION)
				&& statement.toUpperCase().trim().contains(ValidationConstants.VIEW))
		{
			isMiscellaneous = true ;
		}
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.CREATE)
				&& statement.toUpperCase().trim().contains(ValidationConstants.SYNONYM))
		{
			isMiscellaneous = true ;
		}
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.DROPE_ACTION)
				&& statement.toUpperCase().trim().contains(ValidationConstants.SYNONYM))
		{
			isMiscellaneous = true ;
		}
		return isMiscellaneous ;
	}
	
	private MISCELLANEOUS getQueryType(String statement)
	{
		if(statement.toUpperCase().trim().startsWith(ValidationConstants.GRANT))
		{
			misc= MISCELLANEOUS.GRANT ;
		}
		
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.CREATE)
				&& statement.toUpperCase().trim().contains(ValidationConstants.INDEX))
		{
			misc= MISCELLANEOUS.CREATE_INDEX ;
		}
		
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.DROPE_ACTION)
				&& statement.toUpperCase().trim().contains(ValidationConstants.INDEX))
		{
			misc= MISCELLANEOUS.DELETE_INDEX ;
		}
		
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.CREATE)
				&& statement.toUpperCase().trim().contains(ValidationConstants.VIEW))
		{
			misc= MISCELLANEOUS.CREATE_VIEW ;
		}
		
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.CREATE)
				&& statement.toUpperCase().trim().contains(ValidationConstants.SYNONYM))
		{
			misc= MISCELLANEOUS.CREATE_SYNONYM ;
		}
		
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.DROPE_ACTION)
				&& statement.toUpperCase().trim().contains(ValidationConstants.SYNONYM))
		{
			misc= MISCELLANEOUS.DROP_SYNONYM ;
		}
		
		else if(statement.toUpperCase().trim().startsWith(ValidationConstants.DROPE_ACTION)
			//	&& statement.toUpperCase().trim().contains(ValidationConstants.VIEW))
				&& Util.conjucative(statement,ValidationConstants.DROPE_ACTION,ValidationConstants.VIEW))
		{
			misc= MISCELLANEOUS.DROP_VIEW ;
		}
		
		return misc;
	}
	
	public HashMap<String, String> getTableName(String statement)
	{
		//TODO add a generic param to map like action and set appropriate enum value there .
		HashMap<String, String> stamtDetMap = new HashMap<String, String>() ;
		statement = statement.toUpperCase().trim();
		misc = getQueryType(statement);
		if(misc == MISCELLANEOUS.GRANT)
		{
			stamtDetMap = getGrantParse(statement);
		}
		else if(misc == MISCELLANEOUS.CREATE_INDEX )
		{
			stamtDetMap = getCRIndexParse(statement);
		}
		else if(misc == MISCELLANEOUS.DELETE_INDEX)
		{
			stamtDetMap = getDRIndexParse(statement);
		}
		else if(misc == MISCELLANEOUS.CREATE_VIEW)
		{
			stamtDetMap = getDetailsOnCreateView(statement);
		}
		else if(misc == MISCELLANEOUS.DROP_VIEW)
		{
			stamtDetMap = dropViewName(statement);
		}
		else if(misc == MISCELLANEOUS.CREATE_SYNONYM)
		{
			stamtDetMap = synonymCreate(statement);
		}
		else if(misc == MISCELLANEOUS.DROP_SYNONYM)
		{
			stamtDetMap = synonymDrop(statement);
		}
		if(misc !=null)
		{
			stamtDetMap.put(ValidationConstants.ACTION, misc.toString());
		}
		
		return stamtDetMap;
	}
	
	private HashMap<String, String> getGrantParse(String statement)
	{
		HashMap<String, String> tableDet = new HashMap<String, String>() ;
		String[] toParseArr = statement.split(ValidationConstants.TO);
		String[] onParseArr = toParseArr[ValidationConstants.SEC_IND].split(ValidationConstants.MERGE_ON);
		tableDet.put(ValidationConstants.USER, onParseArr[ValidationConstants.SEC_IND].replace(ValidationConstants.STATEMENT_END, ValidationConstants.SPACE).trim());
		tableDet.put(ValidationConstants.TABLE_NAME, onParseArr[ValidationConstants.FST_IND]);
		tableDet.put(ValidationConstants.GRANT_ACCESS, toParseArr[ValidationConstants.FST_IND].replace(ValidationConstants.GRANT, ValidationConstants.SPACE).trim());
		return tableDet ;
	}
	
	private HashMap<String, String> getCRIndexParse(String statement)
	{
		HashMap<String, String> indDet = new HashMap<String, String>() ;
		boolean isReverse = false ;
		boolean isNonReverse = false ;
		String REV_PROP = null;
		statement =statement.toUpperCase().trim();
		String[] indexParseArr = statement.split(ValidationConstants.INDEX);
		String[] onParseArr = indexParseArr[ValidationConstants.SEC_IND].split(ValidationConstants.MERGE_ON);
		String[] SBRAParserArr = onParseArr[ValidationConstants.SEC_IND].split(ValidationConstants.INDX_SBRA) ;
		String indexColName = SBRAParserArr[ValidationConstants.SEC_IND].replace(ValidationConstants.INDX_EBRA, ValidationConstants.SPACE).
				replace(ValidationConstants.STATEMENT_END, ValidationConstants.SPACE).trim();
		if(indexColName.contains(ValidationConstants.INDEX_REVESRE))
		{
			isReverse = true ;
			REV_PROP = ValidationConstants.INDEX_REVESRE;
			indexColName = indexColName.replace(ValidationConstants.INDEX_REVESRE, ValidationConstants.SPACE).trim();
		}
		else if(indexColName.contains(ValidationConstants.INDEX_NOREVESRE))
		{
			isNonReverse = true;
			REV_PROP = ValidationConstants.INDEX_NOREVESRE;
			indexColName = indexColName.replace(ValidationConstants.INDEX_NOREVESRE, ValidationConstants.SPACE).trim();
		}
		indDet.put(ValidationConstants.INDEX_NAME, onParseArr[ValidationConstants.FST_IND].trim());
		indDet.put(ValidationConstants.TABLE_NAME, SBRAParserArr[ValidationConstants.FST_IND].trim());
		indDet.put(ValidationConstants.INDEX_COL_NAME, indexColName);
		if(isNonReverse || isReverse)										//REVERSE or NOREVERSE if exist  
		{
			indDet.put(ValidationConstants.INDEX_REVESRE, REV_PROP);
		}
		return indDet;
	}
	
	private HashMap<String, String> getDRIndexParse(String statement)
	{
		HashMap<String, String> indDet = new HashMap<String, String>() ;
		String[] indexParseArr = statement.split(ValidationConstants.SPACE);
		indDet.put(ValidationConstants.INDEX_NAME,indexParseArr[indexParseArr.length-1].replace(ValidationConstants.STATEMENT_END, ValidationConstants.SPACE));
		return indDet;
	}
	
	private HashMap<String, String> getDetailsOnCreateView(String statement)
	{
		String viewName  = substring(statement,ValidationConstants.VIEW, ValidationConstants.AS);
		statement  = statement.split(ValidationConstants.AS)[ValidationConstants.SEC_IND];
		List<String> tableList = null;
		String tableName = null ;
		try {
			CCJSqlParserManager pm = new CCJSqlParserManager();
			Statement selectStatement = pm.parse(new StringReader(statement));
			if (selectStatement instanceof Select) {
				Select tatement = (Select) selectStatement;
				TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
				tableList=  tablesNamesFinder.getTableList(tatement);
				System.out.println(tableList);
			}

		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			System.out.println("Error as "+ e.getMessage());
		}
		if(tableList != null)
		{
			tableName = stringOnList(tableList);
		}
		HashMap<String, String> viewDet = new HashMap<String, String>() ;
		viewDet.put(ValidationConstants.VIEW_NAME, viewName);
		viewDet.put(ValidationConstants.TABLE_NAME, tableName);
		System.out.println("View name = "+viewName);
		return viewDet;
	}
	
	private HashMap<String, String> dropViewName(String statement)
	{
		String viewName = statement.split(ValidationConstants.VIEW)[ValidationConstants.SEC_IND].replace(ValidationConstants.STATEMENT_END, ValidationConstants.SPACE) ;
		HashMap<String, String> viewDet = new HashMap<String, String>() ;
		viewDet.put(ValidationConstants.VIEW_NAME, viewName);
		System.out.println("View Name = "+viewName);
		return viewDet ;
		
	}
	
	private HashMap<String, String> synonymCreate(String statement)
	{
		String synoName  = substring(statement,ValidationConstants.SYNONYM, ValidationConstants.AS).trim();
		HashMap<String, String> synonymDet = new HashMap<String, String>() ;
		synonymDet.put(ValidationConstants.SYNONYM_NAME, synoName);
		System.out.println("synonym Name = "+synoName);
		return synonymDet ;
	}
	
	private HashMap<String, String> synonymDrop(String statement)
	{
		String synoName  =statement.split(ValidationConstants.SYNONYM)[ValidationConstants.SEC_IND].replace(ValidationConstants.STATEMENT_END, ValidationConstants.SPACE).trim();
		HashMap<String, String> synonymDet = new HashMap<String, String>() ;
		synonymDet.put(ValidationConstants.SYNONYM_NAME, synoName);
		System.out.println("synonym Name = "+synoName);
		return synonymDet ;
	}
}
