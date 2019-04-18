package core.jsqlAss.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import core.jsqlAss.PropertyManager;

public class ActionDAO {
	
	Connection conAction = null ;
	
	public ActionDAO()
	{
		connectionPolulation(); 
	}
	
	private void connectionPolulation()
	{
		FetchConnection fcon = new FetchConnection() ;
		conAction = fcon.getConnection(PropertyManager.getProperty("ENV")) ;
	}
	
	public synchronized ResultSet query(String expression)
	{
		Statement stmt = null;
		ResultSet result = null ;
		
		try {
			stmt = conAction.createStatement() ;
			result = stmt.executeQuery(expression);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException npe){
			//System.out.println("The Connection Obeject get NULL");
		}
		return result ;
	}
	
	public int resOper(ResultSet rs) 
	{
		int rowCount = 0 ;
		try {
			while(rs.next())
			{
				rowCount ++ ;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rowCount ;
	}
	
	
	public int getExeQuery(String expression)
	{
		int count = 0;
		ResultSet reSet = query(expression);
		if(reSet != null)
		{
			count = resOper(reSet) ;
		}
		return count ;
	}
	

}
