import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;


public class CeitbaConnection {

	private String DB_URL = "jdbc:postgresql://veruca.itba.edu.ar/ceitba";
//	private String DB_URL = "jdbc:postgresql://localhost/ceitba";
	private Connection con = null;
	private String username = "";
	private String password = "";
//	private File file = null;
//	private FileWriter fw = null;
//	private BufferedWriter bw = null;
	
	public CeitbaConnection(String username, String password){
		this.username = username;
		this.password = password;		
	}

	private void setupConnection(){
		try{
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(DB_URL, username, password);			
		} catch(Exception e){
			new ErrorWindow("Error connecting to the database! Check connection status...");
			System.exit(1);
		}
		try {
			con.setAutoCommit(false); //to be able to rollback!
		} catch (SQLException e) {
			logError(e, "Fatal: could not get the settings for the database");
			new ErrorWindow("Fatal: could not get the settings for the database");
			tearDown();
		} 
		return;
	}

	private void tearDown(){
		try {
			con.close();
		} catch (SQLException e) {
			logError(e, "Fatal: could not close connection!");
			new ErrorWindow("Fatal: could not close the database connection!");
			System.exit(1);
		}	
	}

	private void insertQueryException(String[] queryStrings){
		try {
			con.rollback();
		} catch (SQLException e1) {
			logError("Fatal: could not rollback: querys -> ", queryStrings);
		}
		logError("Query exception, could not run querys:", queryStrings);
	}

	private void logError(Exception e, String s){
		logErrorLine("----------- NEW EXCEPTION ----------");
		logErrorLine("\n" + s + "\n");
		logErrorLine(e.toString());
		logErrorLine("------------------------------------");
		return;
	}
	
	private void logErrorLine(String s){
		try{
			File file = new File("database_error_log.txt");
			if ( !file.exists() ) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(new Date() + "->" + s + "\n");
			bw.close();
			fw.close();
		}catch (Exception e2){
			
		}
		return;
	}
	
	private void logExcecutionLine(String s){
		try{
			File file = new File("database_log.txt");
			if ( !file.exists() ) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(new Date() + " -> " + s + "\n");
			bw.close();
			fw.close();
		}catch (Exception e2){
			
		}
		return;
	}
	
	private void logError(String s, String[] strings){
		logErrorLine("************* NEW ERROR *************\n");
		logErrorLine(s);
		for(String str: strings)
			logErrorLine(str);
		logErrorLine("*************************************\n");
		return;
	}

	public  Object[][] executeSelectQuery(String queryString, String[] columnNames){
		try{
			setupConnection();

			Statement stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(queryString.toLowerCase());
			ArrayList<Object> ans = new ArrayList<Object>();
			Object[] tuple = null;
			int i;
			while (rs.next()){
				tuple = new Object[columnNames.length];
				i = 0;
				for(String column: columnNames){
					tuple[i] = rs.getString(column);
					i++;
				}
				ans.add(tuple);
			}
			logExcecutionLine(queryString.toLowerCase());
			return (Object[][]) ans.toArray( new Object[ans.size()][] );

		} catch(Exception e){
			logError(e, "Error retrieving the data: " + queryString);
			new ErrorWindow("An error unexpected error occurred");
		} finally{
			tearDown();
		}
		return null;
	}

	private void executeInsertQuery(String queryString) throws SQLException{
		Statement stmt = con.createStatement(); // TODO: USE PREPARED STATEMENT
		stmt.executeUpdate(queryString.toLowerCase());
		logExcecutionLine(queryString.toLowerCase());
	}
	
	public void executeInsertQuerys(String[] queryStrings){
		setupConnection();
		try {
			for(String s: queryStrings){
				executeInsertQuery(s);
			}
			con.commit();
	        new MessageWindow("Data inserted correctly");
		}catch (SQLException e) {
			insertQueryException(queryStrings);
			new ErrorWindow("An error unexpected error occurred");
		} finally{
			tearDown();
		}
        return;
	}
}