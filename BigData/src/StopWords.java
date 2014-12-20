import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;


public class StopWords
{
	private static HashSet<String> stopWords;
	
	static
	{
		stopWords = new HashSet<String>();
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("data/stopwords.txt"));
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
