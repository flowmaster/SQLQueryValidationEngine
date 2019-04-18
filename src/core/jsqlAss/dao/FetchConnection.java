package core.jsqlAss.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import core.jsqlAss.PropertyManager;

public class FetchConnection {
	
	private static final String DB_CLASSNAME = "oracle.jdbc.driver.OracleDriver" ;
	private final String GEN_URL = PropertyManager.getProperty("GEN_URL");
	private final String GEN_UN  = PropertyManager.getProperty("GEN_UN") ;
	private final String GEN_PWD = PropertyManager.getProperty("GEN_PWD");
	
	
	private final String PLE_URL = "jdbc:oracle:thin:@113.130.218.146:1521:fprtlstg" ;
	private final String PLE_UN  = "sspfloview" ;
	private final String PLE_PWD = "readonlystg";
	
	private final String SSPFLOWRPT_URL = "jdbc:oracle:thin:@138.83.169.174:1524:sspflorpt" ;
	private final String SSPFLOWRPT_UN  = "adhoc_view" ;
	private final String SSPFLOWRPT_PWD = "adhocviewrpt";
	
	private final String FTTP_SIT_URL = "jdbc:oracle:thin:@113.130.226.61:1521:fprtluat" ;
	private final String FTTP_SIT_UN  = "sspfloview" ;
	private final String FTTP_SIT_PWD = "readonlysit";
	
	private final String SSP_SIT_URL = "jdbc:oracle:thin:@113.130.226.61:1521:ssrtluat" ;
	private final String SSP_SIT_UN  = "sspfloview" ;
	private final String SSP_SIT_PWD = "readonlysit";
	
	private final String DEV_URL = "jdbc:oracle:thin:@113.130.193.96:1521:fprtlstg" ;
	private final String DEV_UN  = "ssp_flow" ;
	private final String DEV_PWD = "kpwds";
	
	private ArrayList<String> availableEnv = new ArrayList<String>() ;
	private HashMap<String,String> dbEnvMap = new HashMap<String,String>() ;
	
	public FetchConnection()
	{
		buildDBEnv();
	}

	private void buildDBEnv()
	{
		availableEnv.add("PLE");
		availableEnv.add("FTTP_SIT");
		availableEnv.add("SSP_SIT");
		availableEnv.add("DEV");
		availableEnv.add("SSPFLOWRPT_WORLD");
		availableEnv.add("GEN");
		
		dbEnvMap.put("PLE_URL", PLE_URL);
		dbEnvMap.put("PLE_UN",PLE_UN);
		dbEnvMap.put("PLE_PWD", PLE_PWD);
		
		dbEnvMap.put("FTTP_SIT_URL", FTTP_SIT_URL);
		dbEnvMap.put("FTTP_SIT_UN", FTTP_SIT_UN);
		dbEnvMap.put("FTTP_SIT_PWD", FTTP_SIT_PWD);
		
		dbEnvMap.put("SSP_SIT_URL", SSP_SIT_URL);
		dbEnvMap.put("SSP_SIT_UN",SSP_SIT_UN);
		dbEnvMap.put("SSP_SIT_UN", SSP_SIT_PWD);
		
		dbEnvMap.put("DEV_URL", DEV_URL);
		dbEnvMap.put("DEV_UN", DEV_UN);
		dbEnvMap.put("DEV_PWD",DEV_PWD);
		
		dbEnvMap.put("SSPFLOWRPT_WORLD_URL",SSPFLOWRPT_URL);
		dbEnvMap.put("SSPFLOWRPT_WORLD_UN", SSPFLOWRPT_UN);
		dbEnvMap.put("SSPFLOWRPT_WORLD_PWD",SSPFLOWRPT_PWD);
		
		dbEnvMap.put("GEN_URL",GEN_URL);
		dbEnvMap.put("GEN_UN", GEN_UN);
		dbEnvMap.put("GEN_PWD", GEN_PWD);
	}
	
	public Connection getConnection(String env)
	{
		Connection con = null ;
		
		if(!availableEnv.contains(env))
			return con ;
		
		try{
			Class.forName(DB_CLASSNAME);
			System.out.println("The environment ------------"+env);
			con = DriverManager.getConnection((String)dbEnvMap.get(env+"_URL"),(String)dbEnvMap.get(env+"_UN"),(String)dbEnvMap.get(env+"_PWD")) ;
		} catch(ClassNotFoundException cnfe){
			cnfe.getMessage() ;
		}
		catch(SQLException sqle){
			sqle.getMessage() ;
		}
		
		return con ;
	}
	
}
