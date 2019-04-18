package core.jsqlAss;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import core.jsqlAss.ValidationConstants.MISCELLANEOUS;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class SelectAss implements ItemsListVisitor,ValidationConstants{

	String tableName = null ;
	List<String> columnsList = null ;
	List<String> expressionList = null ;
	ExpressionList exprList= null;
	//String whereClause = null ;
	PropertyManager propsMgr = PropertyManager.getPropertyManger();
	
	String validationSysQuery = null ;
	
	
	private void queryDispatcher(Statement statement) throws JSQLParserException
	{
		if (statement instanceof Select) {
			Select selectStatement = (Select) statement;
			TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
			List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
			System.out.println(selectStatement.getSelectBody());
			System.out.println(tableList);
		}
		
		if (statement instanceof Update) {
			Update upd = (Update) statement;
			/*System.out.println(upd.getColumns());
			System.out.println(upd.getWhere());// we are getting directly with and
			System.out.println(upd.getExpressions());
			System.out.println(upd.getTable().toString());*/
			updateExecutor(upd);
		}
		
		if(statement instanceof Delete)
		{
			Delete del = (Delete) statement ;
			deleteExecutor(del);
			/*System.out.println(del.getTable());
			System.out.println(del.getWhere());*/
		}
		if(statement instanceof Insert)
		{
			Insert ins = (Insert) statement;
			
			/*System.out.println(ins.getColumns());
			System.out.println(ins.getItemsList());
			System.out.println(ins.getTable());*/
			insertExecutor(ins);
		}
		
		if(statement instanceof Drop) 
		{
			Drop drop = (Drop)statement ;
			dropExecutor(drop);
		}
		if(statement instanceof Truncate)
		{
			Truncate trunc = (Truncate)statement ;
			truncateExecutor(trunc);
		}
		if(statement instanceof CreateTable)
		{
			CreateTable createTbl = (CreateTable)statement;
			createTableExecutor(createTbl);
		}
		if(statement instanceof ColumnDefinition)
		{
			ColumnDefinition colDef = (ColumnDefinition)statement;
			columnDefExecutor(colDef);
		}
		if(statement instanceof Replace)
		{
			System.out.println("Rename statement found");
		}
		
		
	}
	
	private void insertExecutor(Insert ins)
	{
		
		//inside an subselct statement=class net.sf.jsqlparser.statement.select.SubSelect
		if(ins.getItemsList() instanceof SubSelect){
			insertSubquery(ins);
		}
		else
		{
			insertExpression(ins);
		}
		
	}
	
	private void deleteExecutor(Delete del)
	{
		String whereClause = null ;
		tableName = del.getTable().getWholeTableName();
		if(del.getWhere()!= null)
		{
			whereClause = del.getWhere().toString();
		}
		String valQuery = formValidationStatement(tableName, whereClause);
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);
	}
	
	private void updateExecutor(Update upd)
	{
		String whereClause = null ;
		tableName = upd.getTable().getWholeTableName();
		String setClause = "" ;
		columnsList = upd.getColumns();
		expressionList = upd.getExpressions();
		if(columnsList != null && expressionList != null)
		{
			Iterator itrColums = columnsList.iterator();
			Iterator itrExpression = expressionList.iterator();
			while(itrColums.hasNext() && itrExpression.hasNext())
			{
				setClause += itrColums.next()+ValidationConstants.EQUAL+itrExpression.next()+ValidationConstants.AND;
			}

			if(upd.getWhere() == null)
			{
				whereClause =setClause ;
				whereClause = whereClause.substring(0, whereClause.length()-4);
			}
			else{
				whereClause =setClause+upd.getWhere();
			}
			
			//formValidationStatement(tableName, whereClause);
		}
		String valQuery = formValidationStatement(tableName, whereClause);  
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);
		
	}
	
	private String formValidationStatement(String tableName,String whereclause )
	{
		
		String whereClauseInSQL = null ;

		if(isNull(whereclause)){
			whereClauseInSQL = ValidationConstants.SPACE;
		}
		else{
			whereClauseInSQL =ValidationConstants.WHERE_CLAUSE +ValidationConstants.SPACE+ whereclause ;
		}
		String validationStmt = ValidationConstants.SELECT_CLAUSE +ValidationConstants.SPACE+tableName +ValidationConstants.SPACE+ whereClauseInSQL ;
		return validationStmt ; 
	}
	
	public String createValidator(String query)
	{
		if(nonParser(query))
		{
			return getValidationSysQuery();
		}
		else{
			CCJSqlParserManager pm = new CCJSqlParserManager();
			try {
				Statement statement = pm.parse(new StringReader(query));
				queryDispatcher(statement);

			} catch (JSQLParserException e) {
				// TODO Auto-generated catch block
			}

		}

		return getValidationSysQuery();
	}
	
	private boolean nonParser (String statement)
	{
		boolean isParser = false ;
		if(AlterTable.isAlterTableInstance(statement))
		{
			alterTableExecutor(statement);
			isParser = true ;
		}
		if(Rename.isRenameInstance(statement))
		{
			renameExecutor(statement);
			isParser = true ;
		}
		if(Merge.isMergeInstance(statement))
		{
			//TODO the merge functionality need to write
			mergeQueryExecutor(statement);
			isParser = true ;
		}
		if(Sequence.isSequnceInstance(statement))
		{
			sequenceExecutor(statement);
			isParser = true ;
		}
		if(Miscellaneous.isMiscInstance(statement))
		{
			miscellaneousExecutor(statement);
			isParser = true ;
		}
		return isParser ;
	}
	

	public static void main(String[] args) {
		CCJSqlParserManager pm = new CCJSqlParserManager();
		SelectAss selectAss = new SelectAss();
		String sql = "SELECT * FROM MY_TABLE1, MY_TABLE2, (SELECT * FROM MY_TABLE3) LEFT OUTER JOIN MY_TABLE4 "+
		" WHERE ID = (SELECT MAX(ID) FROM MY_TABLE5) AND ID2 IN (SELECT * FROM MY_TABLE6)" ;
		
		String sql1 = "UPDATE SSP_FLOW.SSP_QUERY_MASTER SET ACTIVE= 'Y',QUERY='SELECT ORDER_ID FROM SSP_MAIN_CURRENT WHERE REC_DATE BETWEEN (''12/10/2014'',''12/11/2014'')' WHERE TRIGGER_RULE_ID = '1247' and query_id= '127'";
		
		String sql2 = "DELETE FROM SSP_FLOW.SSP_ORDER_DETAILS WHERE ORDER_ID = 'CCOG12781721827' and ACCT_ID = '4536743'; ";
		
		String sql3 = "INSERT INTO "+
"SSP_FLOW.SSP_QUERY_MASTER"+"(ID,QUERY,COMP,DATE,OWNER,ACTIVE)VALUES((SELECT MAX(ID)+1 FROM SSP_FLOW.SSP_QUERY_MASTER) ,'SELECT IS_FIOS FROM SSP_FLOW.SSP_ORDER_DETAILS','FLOWENGINE',SYSTIMESTAMP,'v523129','Y')" ;
		try {
			Statement statement = pm.parse(new StringReader(sql));
			selectAss.queryDispatcher(statement);
			selectAss.getPropoerties();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
		/*now you should use a class that implements StatementVisitor to decide what to do
		based on the kind of the statement, that is SELECT or INSERT etc. but here we are only
		interested in SELECTS
		
		{
		
		if (statement instanceof Select) {
			Select selectStatement = (Select) statement;
			TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
			List tableList = tablesNamesFinder.getTableList(selectStatement);
			for (Iterator iter = tableList.iterator(); iter.hasNext();) {
				System.out.println(tableName);
			}*/
			
			
			
		//Statement statement = CCJSqlParserUtil.parse("SELECT * FROM MY_TABLE1");
		
		}

	@Override
	public void visit(SubSelect arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ExpressionList arg0) {
		// TODO Auto-generated method stub
		
	}

	public static boolean isNull(String stringVal)
	{
		if((stringVal==null)||(stringVal==""))
			return true;
		else 
			return false;
		
	}
	public void getPropoerties()
	{
		FileInputStream validationFin = null;
		try {
			validationFin = new FileInputStream(".\\src\\Validation.properties");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Properties valProps = new Properties();
		if(validationFin != null)
			try {
				valProps.load(validationFin);
				System.out.println(valProps);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private boolean intelegenceCheck(String itrExpr,Properties props)
	{
		boolean secondLevelValidation = false ;
		if(itrExpr.equalsIgnoreCase((String) props.get("date"))){
			secondLevelValidation = true ;
		}
		else if(!itrExpr.trim().startsWith(ValidationConstants.SCOLON))
		{
			if(itrExpr.toUpperCase().contains((String)props.get("select")) && itrExpr.toUpperCase().contains((String)props.get("max")))
			{
				secondLevelValidation = true ;
			}
		}
		return secondLevelValidation ;
	}
	
	private void insertSubquery(Insert ins)
	{
		SubSelect insSubSel = (SubSelect) ins.getItemsList() ;
		PlainSelect plainSelect=(PlainSelect)insSubSel.getSelectBody();
		String alias = null ;
		String subquery = null ;
		String valColName = null;
		TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
		List<String> tableList = tablesNamesFinder.getTableList(insSubSel);
		String subTableName = getSubTableName(tableList);
		String subColName = ((Column)((SelectExpressionItem)plainSelect.getSelectItems().get(0)).getExpression()).getColumnName().toString() ;
		String subWhere = plainSelect.getWhere().toString() ;
		alias = ((SelectExpressionItem)plainSelect.getSelectItems().get(0)).getAlias() ;
		columnsList = ins.getColumns();
		valColName = subColName ;
		//System.out.println(((Table)((PlainSelect)((SubSelect)ins.getItemsList()).getSelectBody()).getFromItem()).getName().toString()); // give the table name
		//System.out.println(((Column)((SelectExpressionItem)plainSelect.getSelectItems().get(0)).getExpression()).getColumnName()); // give the select Column name
		//System.out.println(((SelectExpressionItem)plainSelect.getSelectItems().get(0)).getAlias()); //give the alias name
		//System.out.println(plainSelect.getWhere());//WHERE CLAUSE
		//System.out.println("All table Name ="+getSubTableName(tableList));
		if (alias != null)
		{
			valColName = alias ;
			subColName += ValidationConstants.SPACE +alias ;	
		}
		tableName = ins.getTable().toString() ;
		if(columnsList != null)
		{
			Iterator itrColums = columnsList.iterator();
			if(itrColums.hasNext())
			{
				valColName = itrColums.next().toString();
			}
		}
		subquery = formSubQuery(subTableName,subColName,subWhere);
		String valQuery = forValQueryOnSub(tableName, subquery, valColName);
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);

	}
	
	private String forValQueryOnSub(String tableName,String whereclause,String valColName)
	{
		String whereClauseInSQL = null ;

		if(isNull(whereclause)){
			whereClauseInSQL = ValidationConstants.SPACE;
		}
		else{
			whereClauseInSQL =ValidationConstants.WHERE_CLAUSE +ValidationConstants.SPACE+valColName+ValidationConstants.SPACE+ValidationConstants.IN+ ValidationConstants.SBRA+ whereclause +ValidationConstants.EBRA;
		}
		String validationStmt = ValidationConstants.SELECT_CLAUSE +ValidationConstants.SPACE+tableName +ValidationConstants.SPACE+ whereClauseInSQL ;
		return validationStmt ; 
	}
	
	private String getSubTableName(List subTableNames)
	{
		String allTableNames = "" ;
		if(subTableNames != null)
		{
			Iterator subIt = subTableNames.iterator();
			while(subIt.hasNext())
			{
				allTableNames += subIt.next() +ValidationConstants.COMMA ;
			}
		}
		
		allTableNames = allTableNames.substring(0,allTableNames.length()-2) ;
		return allTableNames;
	}
	
	private String formSubQuery(String tableName,String columnName,String whereclause)
	{
		String valSubQuery = ValidationConstants.SELECT +ValidationConstants.SPACE + columnName + ValidationConstants.SPACE +ValidationConstants.FROM +ValidationConstants.SPACE+tableName+ ValidationConstants.SPACE ;
		if(whereclause != null)
		{
			valSubQuery+= ValidationConstants.WHERE_CLAUSE+ValidationConstants.SPACE+whereclause ;
		}
		return valSubQuery ;
	}
	
	private void insertExpression(Insert ins)
	{
		String whereClause = null ;
		expressionList=((ExpressionList)ins.getItemsList()).getExpressions();
		String setClause = " ";
		//System.out.println("expressionList= "+expressionList);
		tableName = ins.getTable().toString() ;
		columnsList = ins.getColumns();
		if(columnsList != null && expressionList != null)
		{
			Iterator itrColums = columnsList.iterator();
			Iterator itrExpression = expressionList.iterator();
			
			Properties props = propsMgr.getValProps();
			String itrExpr = null ;
			String itrCol = null ;
			while(itrColums.hasNext() && itrExpression.hasNext())
			{
				itrExpr = itrExpression.next().toString();
				itrCol = itrColums.next().toString();
				if(intelegenceCheck(itrExpr,props))
				{
					//System.out.println("this the true in 2nd level validation so ignore");
					continue ;
				}
				setClause += itrCol+ValidationConstants.EQUAL+itrExpr+ValidationConstants.AND;
			}

			whereClause =setClause.substring(0, setClause.length()-4);
			//formValidationStatement(tableName, whereClause);
		}
		String valQuery = formValidationStatement(tableName, whereClause);
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);
	}
	
	private void dropExecutor(Drop drop) 
	{
		String whereClause = null ;
		String dropTableName = drop.getName() ;			// table name which is going to drop 
		List listParam = drop.getParameters() ;			// the clause on which drop like purge
		String valQuery = formValidationStatement(dropTableName, whereClause);
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);
		
	}
	
	private void truncateExecutor(Truncate trunc)
	{
		String whereClause = null ;
		String truncTableName = trunc.getTable().getWholeTableName();
		String valQuery = formValidationStatement(truncTableName, whereClause);
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);
	}

	private void createTableExecutor(CreateTable createTbl)
	{
		String whereClause = null ;
		String createTblName = createTbl.getTable().getWholeTableName() ;
		whereClause = ValidationConstants.TABLE_NAME+ValidationConstants.SPACE+ValidationConstants.SCOLON+createTblName+ValidationConstants.SCOLON+ValidationConstants.AND +
				ValidationConstants.COLUMN+ValidationConstants.SBRA+getQualifyColName(createTbl.getColumnDefinitions())+ValidationConstants.EBRA;  
		//System.out.println(createTbl.getColumnDefinitions());
		//System.out.println(getQualifyColName(createTbl.getColumnDefinitions()));
		String valQuery = formValidationStatement(ValidationConstants.ALL_TAB, whereClause); 
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);
		
	}
	
	private static String getQualifyColName(List<String> qualColNameHm)
	{
		String qualifyColName = ValidationConstants.SPACE ;
		Iterator crtIt = qualColNameHm.iterator();
		while(crtIt.hasNext())
		{
			qualifyColName  +=ValidationConstants.SCOLON+ crtIt.next().toString().split(ValidationConstants.SPACE)[ValidationConstants.FST_IND].trim()+ValidationConstants.SCOLON +ValidationConstants.ALTER_COL_NAME_CONJUCTION ;
		}

		return qualifyColName.trim().substring(0, qualifyColName.length() -2) ;
	}
	private void columnDefExecutor(ColumnDefinition colDef)
	{
		System.out.println(colDef.getColumnName());
		setValidationSysQuery(colDef.getColumnName());
	}
	
	private void alterTableExecutor(String statement)
	{
		String valQuery =AlterTable.formValAlterQeury(statement) ;
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);
	}
	
	private void renameExecutor (String renameQuery)
	{
		String whereClause = null ;
		String renameTabName=Rename.getTableName(renameQuery);
		whereClause = ValidationConstants.TABLE_NAME+ValidationConstants.EQUAL+ValidationConstants.SCOLON+renameTabName+ValidationConstants.SCOLON;
		String valQuery = formValidationStatement(ValidationConstants.ALL_TAB, whereClause) ;
		System.out.println(valQuery);
		setValidationSysQuery(valQuery);
	}
	
	public String getValidationSysQuery() {
		return validationSysQuery;
	}

	public void setValidationSysQuery(String validationSysQuery) {
		this.validationSysQuery = validationSysQuery;
	}
	
	private void mergeQueryExecutor(String statement)
	{
		Merge merg = new Merge() ;
		String whereClause = merg.getWhereClause(statement);
		String qualifyTableName  = merg.getTableName(statement);
		String validationQuery = formValidationStatement(qualifyTableName, whereClause);
		System.out.println(validationQuery);
		setValidationSysQuery(validationQuery);
	}
	
	private void sequenceExecutor (String query)
	{
		//TODO execute the sequence create ,alter & delete. 
		Sequence seq = new Sequence();
		String seqValQuery = seq.getQualifyValQuery(query);
		System.out.println(seqValQuery);
		setValidationSysQuery(seqValQuery);
	}
	
	private void miscellaneousExecutor(String query)
	{
		Miscellaneous miscObj = new Miscellaneous();
		HashMap<String, String> stamtDetMap = new HashMap<String, String>() ;
		stamtDetMap = miscObj.getTableName(query);
		miscellaneousvalExecutor(stamtDetMap);
	}
	
	//TODO update the validation sql for grant based upon the type of previledge along with table name (currently it is working on only table name) 
	private void miscellaneousvalExecutor(HashMap<String, String> miseleneousMap)
	{
		String miscAction = miseleneousMap.get(ValidationConstants.ACTION);
		if(miscAction == null)
		{
			return;
		}
		if(miscAction.equalsIgnoreCase(MISCELLANEOUS.GRANT.toString()))
		{
			//SELECT 1 FROM DBA_TAB_PRIVS WHERE TABLE_NAME = '';
			System.out.println(formValidationSQL(miseleneousMap, GRANT_VAL_SQL));
			setValidationSysQuery(formValidationSQL(miseleneousMap, GRANT_VAL_SQL));
			 
		}
		else if(miscAction.equalsIgnoreCase(MISCELLANEOUS.CREATE_INDEX.toString()))
		{
			//SELECT TABLE_NAME,INDEX_NAME,COLUMN_NAME FROM DBA_IND_COLUMNS WHERE TABLE_NAME ='' AND INDEX_NAME ='';
			System.out.println(formValidationSQL(miseleneousMap, CR_INDEX_VAL_SQL));
			setValidationSysQuery(formValidationSQL(miseleneousMap, CR_INDEX_VAL_SQL));
			
		}
		else if(miscAction.equalsIgnoreCase(MISCELLANEOUS.DELETE_INDEX.toString()))
		{
			System.out.println(formValidationSQL(miseleneousMap, DE_INDEX_VAL_SQL));
			setValidationSysQuery(formValidationSQL(miseleneousMap, DE_INDEX_VAL_SQL));
		}
		else if(miscAction.equalsIgnoreCase(MISCELLANEOUS.CREATE_VIEW.toString()))
		{
			//SELECT 1 FROM ALL_VIEWS WHERE VIEW_NAME ='';
			System.out.println(formValidationSQL(miseleneousMap, VIEW_VAL_SQL));
			setValidationSysQuery(formValidationSQL(miseleneousMap, VIEW_VAL_SQL));
		}
		else if(miscAction.equalsIgnoreCase(MISCELLANEOUS.DROP_VIEW.toString()))
		{
			System.out.println(formValidationSQL(miseleneousMap, VIEW_VAL_SQL));
			setValidationSysQuery(formValidationSQL(miseleneousMap, VIEW_VAL_SQL));
		}
		else if(miscAction.equalsIgnoreCase(MISCELLANEOUS.CREATE_SYNONYM.toString()))
		{
			//SELECT 1 FROM ALL_SYNONYMS WHERE SYNONYM_NAME = '';
			System.out.println(formValidationSQL(miseleneousMap,SYNONYM_VAL_SQL));
			setValidationSysQuery(formValidationSQL(miseleneousMap,SYNONYM_VAL_SQL));
			
		}
		else if(miscAction.equalsIgnoreCase(MISCELLANEOUS.DROP_SYNONYM.toString()))
		{
			//SELECT 1 FROM ALL_SYNONYMS WHERE SYNONYM_NAME = '';
			System.out.println(formValidationSQL(miseleneousMap,SYNONYM_VAL_SQL));
			setValidationSysQuery(formValidationSQL(miseleneousMap,SYNONYM_VAL_SQL));
		}
	}
	
	private String formValidationSQL(HashMap<String, String> miseleneousMap,String staticValidationSQL)
	{
		Set<String> miscParamSet = miseleneousMap.keySet();
		for (String lObjValue : miscParamSet)
		{
			if(staticValidationSQL.contains(SET_PREFIX+lObjValue.trim()+REPLACE_IDENTIFIER))
			{
				staticValidationSQL = staticValidationSQL.replace(SET_PREFIX+lObjValue.trim()+REPLACE_IDENTIFIER, miseleneousMap.get(lObjValue).trim());
			}
		}
		return staticValidationSQL;
		
	}
	
	
	}
