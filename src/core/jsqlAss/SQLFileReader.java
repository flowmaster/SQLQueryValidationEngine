package core.jsqlAss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jsql.repository.MasterRepository;

public class SQLFileReader {
	
	
	String strQuery = null;
	BufferedReader brReader = null ;
	StringBuffer strBuff = new StringBuffer();
	List <Query> listQueryModel = null ;
	ArrayList<StringBuffer> listQuery1 = new ArrayList<StringBuffer>();
	public List readFromFile()
	{
		String filePath= "E:/Files/" ;
		listQueryModel = new ArrayList<Query>();
		try{
			File[] files = new File(filePath).listFiles();
			//If this pathname does not denote a directory, then listFiles() returns null. 

			for (File file : files) {
				ArrayList<StringBuffer> listQuery = new ArrayList<StringBuffer>();
				Query query = new Query();
				if (file.isFile()) {
					FileReader reader = new FileReader(filePath+file.getName());
					brReader = new BufferedReader(reader);

					while ((strQuery = brReader.readLine())!= null )
					{
						if(strQuery != null && strQuery.trim().length() > 0)
						{
							strBuff.append(strQuery);
						}
						if(strBuff.toString().toUpperCase().contains(ValidationConstants.COMMIT))
						{
							continue ;
						}
						if((strBuff.toString()).endsWith(";"))
						{
							listQuery.add(strBuff);
							strBuff = new StringBuffer();
						}
						strBuff.append(ValidationConstants.SPACE);	
					}
					query.setListQuery(listQuery);
					query.setFileName(file.getName());
					listQueryModel.add(query); // populate the model class
				}
			}

		}catch(FileNotFoundException ffx)
		{
			System.err.println("the file not found in the path");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listQueryModel ;
		//return listQuery;
	}
	
	@SuppressWarnings("unchecked")
	public void displayQuery(List<StringBuffer> queryList)
	{
		
		listQuery1 = (ArrayList<StringBuffer>)queryList ;
		for (StringBuffer bufQuery : listQuery1) {
		System.out.println(bufQuery);
		}
	}
	
	public void getOperation (List queryModelList)
	{
		String validationSQL = null ;
		SelectAss selectAss = new SelectAss();
		ActionOpera actOpera = new ActionOpera();
		Iterator<Query> queryItr = queryModelList.iterator();
		HashMap<String,Integer> validationSQl = null ;
		Integer queryOccurance = 0 ;
		listQueryModel = new ArrayList<Query>();
		while(queryItr.hasNext())
		{
			Query obj = queryItr.next();
			validationSQl = new HashMap<String,Integer>() ;
			System.out.println("------------------------------------------------------------");
			System.out.println(obj.getFileName());
			System.out.println("------------------------------------------------------------");
			ArrayList<StringBuffer> lobjArray = obj.getListQuery();
			Iterator<StringBuffer> listQueryItr = lobjArray.iterator();
			Query writeQuery = null;
			while(listQueryItr.hasNext())
			{
				 writeQuery = new Query();
				
				writeQuery.setFileName(obj.getFileName());
				
				StringBuffer query = listQueryItr.next();
				writeQuery.setQuery(query.toString());
				System.out.println(query);//This is the actual executed query
				validationSQL = selectAss.createValidator(query.toString()); //This is the validation query
				queryOccurance = actOpera.getExecutionCount(validationSQL+ValidationConstants.SEMI_COLON);
				if(validationSQL==null)
				{
					validationSQL ="FAILED TO CREATE VALIDATION SQL" ;
				}
				writeQuery.setValidationQuery(validationSQL+ValidationConstants.SEMI_COLON);
				writeQuery.setResult(queryOccurance);
				listQueryModel.add(writeQuery);
			}
			obj.setValidationSQl(validationSQl);
			
		}
		//System.out.println("length of querymodel list - "+listQueryModel.size()+"//");
		//TODO if the file is open during execution too , it will delete the existing one create a new one.
		MasterRepository repo = MasterRepository.getInstance();
		String excelFilePath = "ValidationSheet.xlsx";
		try {
			repo.writeExcel(listQueryModel, excelFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		SQLFileReader red = new SQLFileReader();
		List listquery=  red.readFromFile();
		//red.displayQuery(listquery);				// for update statement
		red.getOperation(listquery);				// for delete statement
	}
}
