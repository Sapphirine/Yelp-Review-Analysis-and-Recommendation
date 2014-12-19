package bigdata;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;


public class StopWords
{
	private static HashSet<String> stopWords;
	private static final String STOPWORD_FILE = "data/stopwords.txt";
	
	static
	{
		stopWords = new HashSet<String>();
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(STOPWORD_FILE));
			String line;
			while ((line = reader.readLine()) != null) stopWords.add(line);
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean checkStop(String word)
	{
		return stopWords.contains(word);
	}
}
