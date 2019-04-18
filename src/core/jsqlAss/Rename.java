package core.jsqlAss;

import net.sf.jsqlparser.statement.Statement;

public class Rename {
	
public Rename(String queryName)
{
	
}

public static boolean isRenameInstance(String statement)
{
	 return statement.trim().toUpperCase().startsWith(ValidationConstants.RENAME);
}

public static String getTableName (String statement)
{
	return statement.trim().split(ValidationConstants.RENAME_CONJUCTION)[ValidationConstants.SEC_IND].replace(ValidationConstants.STATEMENT_END, ValidationConstants.SPACE).trim();
}

}
