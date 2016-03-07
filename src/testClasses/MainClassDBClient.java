/**
 * 
 */
package testClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author c38847
 *
 */
public class MainClassDBClient {
	
	private static Connection conn = null;
	private static Statement stmt = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getEnvironment((args!=null && args.length>0)?args[0]:"");
		
	}
	
	private static void getEnvironment(String env){
		if(!Utils.setEnv(env)){
			System.out.print("<<Enter a valid Environment.>> ");
			System.out.println(Utils.getEnvironments());
			Scanner in = new Scanner(System.in);
		    getEnvironment(in.nextLine());
		}else{
			receiveCommand();
		}
	}
	
	/**
	 * @param args
	 */
	public static void receiveCommand(){
		System.out.println("<<Enter sql to execute / type exit>>");
	    String sql = new Scanner(System.in).nextLine();
	    if(!sql.equalsIgnoreCase("exit")){
	    	runCommand(sql.toUpperCase(),sql.toUpperCase().contains("*"),sql.toUpperCase().contains("SELECT"));
	    	System.out.println("==============");
	    	receiveCommand();
	    }
	}
	
	public static void runCommand(String sql,boolean hasStar,boolean hasSelect) {
		
		try {
			getConnection();
			
			if(hasSelect)
				selectQuery(sql,hasStar);
			else
				updateQuery(sql);
			
			closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void updateQuery(String sql) throws SQLException {
			
			boolean rs = stmt.execute(sql);
			
		      System.out.println("Updated/ Inserted rows --- "+rs);
	}
	
	private static void selectQuery(String sql,boolean hasStar) throws SQLException {
		hasStar=false;
			List<String> cols = selectQueryMetaData(sql,hasStar);
			int size = cols.size();
			
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.println();
				
				for(int i=1;i<=size;i++){
					System.out.print(rs.getString(i)+" \t ");
				}
				
			}
		    rs.close();
		      
	}
	
	private static List<String> selectQueryMetaData(String sql,boolean hasStar) throws SQLException {
		String metasql="";
		List<String> cols = new ArrayList<String>();
		if(hasStar){
			String table = sql.substring(sql.indexOf("FROM ")+5,sql.length()).split(" ")[0];
			metasql = "SELECT * from SYSCAT.COLUMNS where TABSCHEMA='"+Utils.getProp(Constants.SCHEMA)+"' and TABNAME in ('" + table + "')";
			System.out.println(metasql);
			ResultSet rs = stmt.executeQuery(metasql);
			while (rs.next()) {
				cols.add(rs.getString("COLNAME"));
				System.out.print(rs.getString("COLNAME")+" \t ");
			}
		    rs.close();
		}else{
			cols = Arrays.asList(sql.substring(sql.indexOf("SELECT")+6,sql.indexOf("FROM")).split(","));
		}
		/**
		 * select * from all_constraints where constraint_type='R' and r_constraint_name in (select constraint_name from all_constraints where constraint_type in ('P','U') and table_name='loan_lvl_gfee_adjmt');
		 * select table_name from all_constraints where constraint_type='R' and table_name='loan_lvl_gfee_adjmt'
		 */
		System.out.println(cols);
	      
	    return cols;
	}
	
	private static void getConnection() {
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			System.out.println("Connecting to a selected database..."+Utils.getProp(Constants.CONN_URL));
			conn = DriverManager.getConnection(
					Utils.getProp(Constants.CONN_URL),
					Utils.getProp(Constants.USER),
					Utils.getProp(Constants.PASSWORD));
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		catch(SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void closeConnection() {
		try {
			if (stmt != null)
				conn.close();
		} catch (SQLException se) {
		}// do nothing
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}// end finally try
	}

}
