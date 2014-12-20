import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;


public class WordCount
{
	private class Pair implements Comparable<Pair>
	{
		private long key;
		private String value;
		
		public Pair(long key, String value)
		{
			this.key = key;
			this.value = value;
		}
		
		public long getKey()
		{
			return key;
		}
		
		public String getValue()
		{
			return value;
		}

		@Override
		public int compareTo(Pair o)
		{
			return Long.signum(key - o.key);
		}		
	}
	
	private class Pair2 implements Comparable<Pair2>
	{
		private double key;
		private String value;
		
		public Pair2(double key, String value)
		{
			this.key = key;
			this.value = value;
		}
		
		public double getKey()
		{
			return key;
		}
		
		public String getValue()
		{
			return value;
		}
		
		@Override
		public int compareTo(Pair2 o)
		{
			return Double.valueOf(key).compareTo(Double.valueOf(o.key));
		}		
	}
	
	public void increment(Hashtable<String, Long> map, String key)
	{
		long count = map.containsKey(key) ? map.get(key) : 0;
		map.put(key, count + 1);
	}
	
	public void increment(Hashtable<String, Long> map, String key, long val)
	{
		long count = map.containsKey(key) ? map.get(key) : 0;
		map.put(key, count + val);
	}
	
	/**
	 * Count words
	 * @param prefix
	 * @throws IOException
	 */
	public void countWordDoc(String prefix) throws IOException
	{
		Hashtable<String, Long> wordDocCount = new Hashtable<String, Long>();
		
		String rootPath = "data/part/" + prefix;
		File rootDir = new File(rootPath);
		
		for (String category : rootDir.list())
		{
			String categoryPath = rootPath + "/" + category;

			File categoryDir = new File(categoryPath);
			
			for (String stars : categoryDir.list())
			{
				String starPath = categoryPath + "/" + stars;
				File starDir = new File(starPath);			
				
				String goodbad = Integer.parseInt(stars) < 4 ? "bad" : "good";
				
				String outPath = "data/stat/" + prefix + "/" + category + "/" + goodbad;
				File outdir = new File(outPath);
				if (!outdir.exists()) outdir.mkdirs();
				
				Hashtable<String, Long> oneWordDocCount = new Hashtable<String, Long>();
				
				for (String filename : starDir.list())
				{
					String input, line;
					BufferedReader reader;
					
					input = starPath + "/" + filename;
					reader = new BufferedReader(new FileReader(input));
					line = reader.readLine();
					reader.close();
					
					String[] words = line.split(" ");
					HashSet<String> wordSet = new HashSet<String>();
					for (String word : words)
					{
						if (StopWords.checkStop(word)) continue;
						wordSet.add(word);
						increment(oneWordDocCount, word);
					}
					for (String word : wordSet) increment(wordDocCount, word);
				}
				
				Vector<Pair> pairList = new Vector<Pair>();
				for (String word : oneWordDocCount.keySet()) pairList.add(new Pair(oneWordDocCount.get(word), word));
				Collections.sort(pairList);
				Collections.reverse(pairList);
				
				PrintWriter writer = new PrintWriter(new FileWriter(outPath + "/worddoc.txt"));
				for (Pair pair : pairList) writer.println(pair.getValue() + "\t" + pair.getKey());
				writer.close();
			}
		}
		
		PrintWriter writer = new PrintWriter(new FileWriter("data/stat/" + prefix + "/worddoc.txt"));
		for (String word : wordDocCount.keySet()) writer.println(word + "\t" + wordDocCount.get(word));
		writer.close();
	}
	
	/**
	 * Compute TF-IDF weights and sort them
	 * @param prefix
	 * @throws IOException
	 */
	public void countTfIdf(String prefix) throws IOException
	{
		Hashtable<String, Double> wordIdfCount = new Hashtable<String, Double>();
		BufferedReader reader = new BufferedReader(new FileReader("data/stat/" + prefix + "/worddoc.txt"));
		String line;
		while ((line = reader.readLine()) != null)
		{
			String[] array = line.split("\t");
			String word = array[0];
			long count = Long.parseLong(array[1]);
			wordIdfCount.put(word, Math.log(count));
		}
		reader.close();
		
		String rootPath = "data/stat" + prefix;
		File rootDir = new File(rootPath);
		
		for (String category : rootDir.list())
		{
			String categoryPath = rootPath + "/" + category;

			File categoryDir = new File(categoryPath);
			
			if (categoryDir.list() == null) continue;
			for (String stars : categoryDir.list())
			{
				String starPath = categoryPath + "/" + stars;				
				Vector<Pair2> pairList = new Vector<Pair2>();
				
				reader = new BufferedReader(new FileReader(starPath + "/worddoc.txt"));
				while ((line = reader.readLine()) != null)
				{
					String[] array = line.split("\t");
					String word = array[0];
					long count = Long.parseLong(array[1]);
					pairList.add(new Pair2(count / wordIdfCount.get(word), word));
				}
				
				Collections.sort(pairList);
				Collections.reverse(pairList);
				
				PrintWriter writer = new PrintWriter(new FileWriter(starPath + "/tfidf.txt"));
				for (Pair2 pair : pairList)
				{
					if (Double.isInfinite(pair.getKey())) continue;
					writer.println(pair.getValue() + "\t" + pair.getKey());
				}
				writer.close();
			}
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		WordCount wordCount = new WordCount();
		wordCount.countWordDoc("full");
		wordCount.countWordDoc("filtered");
		wordCount.countTfIdf("full");
		wordCount.countTfIdf("filtered");
	}
}
