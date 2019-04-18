package core.jsqlAss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Merge {

	public static boolean isMergeInstance(String statement)
	{
		 return statement.toString().trim().toUpperCase().contains(ValidationConstants.MERGE);
	}
	//TODO find the where clause when the merge table name is different(i.e table name and column name are different but having same structure) ,
	//although the scenario is rare. 
	public String getWhereClause(String statement)
	{
		List<String> keyRuleList = mergeParser(statement.toString().trim());
		String whereClause = "" ;
		Iterator<String> keyRuleItr = keyRuleList.iterator() ;
		while(keyRuleItr.hasNext())
		{
			whereClause =whereClause+keyRuleItr.next()+ValidationConstants.AND;
		}
		if(whereClause.length()>ValidationConstants.AND.length())
		{
			whereClause=whereClause.substring(0,whereClause.trim().length()-ValidationConstants.AND.length());
		}
		return whereClause ;
	}
	
	private List<String> mergeParser(String mergeQuery)
	{
		mergeQuery = mergeQuery.toUpperCase();
		String[] getOnSplit = mergeQuery.split(ValidationConstants.MERGE_ON);
		String getOnClause = getOnSplit[ValidationConstants.SEC_IND];
		String[] getWhenOnSplit = getOnClause.split(ValidationConstants.MERGE_WHEN);
		String getOnCond = getWhenOnSplit[ValidationConstants.FST_IND];  //contains the values which is the condition for merge .
		String[] allCondition = getOnCond.split(ValidationConstants.AND);
		String[] aliasArr = getMergeAliasNames(getOnSplit[ValidationConstants.FST_IND]); // all table name aliases
		return getNumOccurance(allCondition,aliasArr) ; // contains all the conditions by which we can validate the new rules
	}
	
	private List<String> getNumOccurance(String[] targetArr ,String[] compareArr)
	{
		List<String> mergeKeyRules = new ArrayList<String>(); 
		for(String lObj : targetArr)					//condition key iteration 
		{
			int numOfOccurance = 0 ;
			String key = null;
			for (String lObjComp : compareArr)			//Alias iteration
			{
				if(lObj.contains(lObjComp.concat(ValidationConstants.ALIAS_DELIMETER)))
				{
					numOfOccurance++ ;	
					key = lObj.replace(lObjComp.concat(ValidationConstants.ALIAS_DELIMETER), ValidationConstants.SPACE).trim() ;
				}
			}
			if(numOfOccurance == 1)
			{
				mergeKeyRules.add(key);
			}
		}
		return mergeKeyRules;
	}
	
	public String getTableName(String statement)
	{
		String tableNmWithAlias = statement.toUpperCase().substring(statement.indexOf(ValidationConstants.MERGE_USING)+ValidationConstants.MERGE_USING.length(), statement.indexOf(ValidationConstants.MERGE)) ;
		return tableNmWithAlias.trim().split(ValidationConstants.SPACE)[ValidationConstants.FST_IND] ;
	}
	private String[] getMergeAliasNames(String getOnSplit)
	{
		String[] tableAliasArr = null ;
		String[] getUsingSplit = getOnSplit.split(ValidationConstants.MERGE_USING);
		String[] getMergeIntoSplit = getUsingSplit[ValidationConstants.SEC_IND].split(ValidationConstants.MERGE_INTO);
		if(getMergeIntoSplit.length > 0)
		{
			//do with into as both merge and into in same statement
			tableAliasArr = getAliasFromQualifyTableName(getMergeIntoSplit);
		}
		else
		{
			String[] getMergeSplit = getUsingSplit[ValidationConstants.SEC_IND].split(ValidationConstants.MERGE);
			for(int i = 0; i<getMergeSplit.length ; i++)
			{
				getMergeSplit[i] = getMergeSplit[i].replace(ValidationConstants.INTO, ValidationConstants.SPACE);
			}
			
			tableAliasArr = getAliasFromQualifyTableName(getMergeSplit);
		}
		return tableAliasArr ;
	}
	
	private String[] getAliasFromQualifyTableName(String[] tableQualifyNames)
	{
		String[] aliasArr = new String[tableQualifyNames.length];
		for(int i =0 ; i < tableQualifyNames.length ;i ++ )
		{
			String[] aliasNameArr = tableQualifyNames[i].trim().split(ValidationConstants.SPACE);
			aliasArr[i] = aliasNameArr[aliasNameArr.length-1];
		}
		return aliasArr ;
	}
	
}
