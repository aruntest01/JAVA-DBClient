/**
 * 
 */
package testClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author c38847
 * 
 */
public class Utils {

	private static Properties props;
	private static final String DBfileName = "DB.properties";
	private static String environment;
	private static Map<String, HashMap<String, String>> propMap;
	
	public static boolean setEnv(String env) {
		// TODO Auto-generated constructor stub
		System.out.println(env);
		try {
			props= new Properties();
			loadProps();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(propMap.containsKey(env))
			environment=env;
		else
			return false;
		return true;
	}
	
	public static Set<String> getEnvironments(){
		return propMap.keySet();
	}

	public static String getProp(String key) {
		if (key.equals(Constants.CONN_URL)) 
			return "jdbc:db2://" + getProp(Constants.HOST) + ":"
					+ getProp(Constants.PORT) + "/"
					+ getProp(Constants.DATABASE);
		else if (props.containsKey(key))
			return props.getProperty(key);
		else if (propMap != null && propMap.containsKey(environment))
			return (String) propMap.get(environment).get(key);
		else
			return null;
	}
	
	private static void loadAppProps(){
		try {
			props.load(new FileInputStream(new File("").getAbsolutePath()+"\\App.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void loadProps()
			throws Exception {
		BufferedReader fs = null;
		propMap = new HashMap<String, HashMap<String, String>>();
		try {
			String section = null;
			HashMap<String, String> map = null;

			InputStream input = new FileInputStream(
					new File("").getAbsolutePath() + "/" + DBfileName);
			if (input != null) {
				fs = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = fs.readLine()) != null) {
					if (line.indexOf("[") == -1) {
						if (line.trim().length() > 0) {
							line = line.trim();
							String value;
							String name;
							if (line.indexOf("=") > -1) {
								name = line.substring(0, line.indexOf("="))
										.trim();
								value = line.substring(line.indexOf("=") + 1,
										line.length()).trim();
							} else {
								name = line.trim();
								value = "";
							}
							map.put(name, value);
						}
					} else {
						if (map != null && map.size() > 0) {
							propMap.put(section, map);
						}
						line = line.trim();
						section = line.substring(1, line.length() - 1);
						map = new HashMap<String, String>();
					}
				}
				if (map.size() != 0) {
					propMap.put(section, map);
				}
			}

		} catch (FileNotFoundException f) {
			throw new Exception("Property File Not Found.", f);
		} catch (IOException i) {
			throw new Exception("Error Reading property file.", i);
		} catch (NullPointerException e) {
			throw new Exception("Section(s) was not found in property file.", e);
		} finally {
			try {
				if (fs != null) {
					fs.close();
				}
			} catch (Exception e) {
				throw new Exception("Error closing property file. ", e);
			}
		}
	}

}
