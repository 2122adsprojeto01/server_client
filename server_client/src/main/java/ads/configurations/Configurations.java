package ads.configurations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ini4j.Config;
import org.ini4j.Ini;


/**
 * Base class to read ini configuration files
 * @author Susana Polido
 * @version 1
 */
public class Configurations {
	private Ini file;
	
	
	
	/**
	 * Creates Configurations object from the name of the file that contains the configurations we want
	 * @param file_name name of the configuration file
	 * @since 1
	 */
	public Configurations(String file_name) {
		try {
			file = new Ini( new File(file_name));
			Config config = new Config();
			config.setMultiOption(true);
			file.setConfig(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Returns a single value of a key in an ini file section
	 * @param key contained in the section
	 * @param section of the ini file
	 * @return the single value contained in the key of the section
	 * @since 1
	 */
	public String getValueFromKeySection(String key, String section) {
		return file.get(section, key);
	}
	
	
	
	/**
	 * Returns all the values of a key in an ini file section
	 * @param key contained in the section
	 * @param section of the ini file
	 * @return a List of Strings of all the values contained in the key of the section
	 * @since 1
	 */
	public List<String> getAllValuesFromKeySection(String key, String section){
		Ini.Section ini_section = file.get(section);
		return ini_section.getAll(key);
	}
	
	
	
	//Should be moved to a proper test section
	public static void main(String[] args) {
		Configurations test = new Configurations("tests_configs.ini");
		System.out.println(test.getValueFromKeySection("host", "email").equals("smtp.gmail.com"));
		System.out.println(test.getAllValuesFromKeySection("curator", "github").size());
		System.out.println(test.getAllValuesFromKeySection("curator", "github").get(0).equals("adsprojet01@gmail.com"));
		System.out.println(test.getAllValuesFromKeySection("curator", "github").get(1).equals("boop@gmail.com"));
	}
}
