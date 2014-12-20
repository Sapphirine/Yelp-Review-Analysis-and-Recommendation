package bigdata;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;


public class POSChecker
{
	private static HashSet<String> importantTags;
	private static final String TAGFILE = "data/selected_tags.txt";
	
	static
	{
		importantTags = new HashSet<String>();
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(TAGFILE));
			
			String line;
			while ((line = reader.readLine()) != null)
			{
				importantTags.add(line);
			}
			
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Check whether the POS tag is important
	 * @param tag
	 * @return
	 */
	public static boolean checkTag(String tag)
	{
		return importantTags.contains(tag);
	}
}
