package core.jsqlAss;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyManager {
	
	private static PropertyManager propManager ;
	public Properties valProps = null ;

	PropertyManager(){
		initialize() ;
	}
	
	public static synchronized PropertyManager getPropertyManger()
	{
		if(propManager == null)
		{
			propManager = new PropertyManager();
		}
		return propManager;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException(); 
	}
	public void initialize()
	{

		FileInputStream validationFin = null;
		valProps = new Properties();
		setValProps(valProps);
		try {
			validationFin = new FileInputStream(".\\src\\Validation.properties");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(validationFin != null)
			try {
				valProps.load(validationFin);
				//System.out.println(valProps);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}
	
	public Properties getValProps() {
		return valProps;
	}

	public void setValProps(Properties valProps) {
		this.valProps = valProps;
	}
	
	public static String getProperty(String key) {
		return getPropertyManger().getValProps().getProperty(key) ;
	}
}
