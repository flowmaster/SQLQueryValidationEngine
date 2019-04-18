package core.jsqlAss;

public class Sequence {

	//TODO all the functionality for the sequence 
	//create , delete and alter .
	
	int ADD =1 ;
	int DELETE =2 ;
	int ALTER = 3 ;
	public static boolean isSequnceInstance(String statement)
	{
		return statement.toString().trim().toUpperCase().contains(ValidationConstants.SEQUENCE);
	}
	
	private String getSequenceName(String statement,int opera)
	{
		String seqName = null ;
		if(opera == ADD || opera == ALTER)
		{
			seqName = statement.substring(statement.indexOf(ValidationConstants.SEQUENCE)+ValidationConstants.SEQUENCE.length(), statement.indexOf(ValidationConstants.INCREMENT)).trim() ;
		}
		else if(opera == DELETE)
		{
			seqName = statement.substring(statement.indexOf(ValidationConstants.SEQUENCE)+ValidationConstants.SEQUENCE.length(), statement.indexOf(ValidationConstants.STATEMENT_END)).trim() ;
		}

		return seqName ;
	}
	
	private int getsequenceKind(String statement)
	{
		int seqKind = 0;
		if(statement.toString().trim().toUpperCase().startsWith(ValidationConstants.CREATE))
		{
			seqKind = ADD ;
		}
		else if(statement.toString().trim().toUpperCase().startsWith(ValidationConstants.ALTER))
		{
			seqKind = ALTER ;
		}
		else if(statement.toString().trim().toUpperCase().startsWith(ValidationConstants.DROPE_ACTION))
		{
			seqKind = DELETE ;
		}
		
		return seqKind ;
	}
	
	public String getQualifyValQuery(String statement)
	{
		int kindSeq = getsequenceKind(statement);
		String validationQuery = null ;
		validationQuery = ValidationConstants.SEQUENCE_VAL_SQL.replace(ValidationConstants.SEQ_NAME, getSequenceName(statement,kindSeq)) ;
		return validationQuery ;
	}
	
	
}
