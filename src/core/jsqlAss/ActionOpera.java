package core.jsqlAss;

import core.jsqlAss.dao.ActionDAO;

public class ActionOpera {
	
	ActionDAO actionDAO =null ;

	public int getExecutionCount(String query)
	{
		int executeCount =0 ;
		actionDAO = new ActionDAO();
		executeCount = actionDAO.getExeQuery(query);
		return executeCount;
	}
	
	private int executeonIdealResource(String expression)
	{
		int executeCount =0 ;
		//TODO call to bo
		return executeCount ;
	}
}
