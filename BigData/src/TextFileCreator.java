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

public class TextFileCreator
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
	
	public Hashtable<Long, HashSet<Integer>> categoryMap;
	public String[] categoryNames;
	
	public String removeSpecialCharacter(String text)
	{
		StringBuffer buffer = new StringBuffer();
		
		text = text.toLowerCase();
		int length = text.length();
		
		for (int i = 0; i < length; ++i)
		{
			char c = text.charAt(i);
			if (Character.isLetterOrDigit(c))
			{
				buffer.append(c);
			}
		}
		
		if (buffer.length() == 0)
		{
			return null;
		}
		else
		{
			return buffer.toString();
		}
	}
	
	/**
	 * Load categories
	 * @throws IOException
	 */
	public void readCategories() throws IOException
	{
		categoryMap = new Hashtable<Long, HashSet<Integer>>();
		
		final String inputName = "data/category.csv";
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		
		String line = reader.readLine();
		categoryNames = line.split(",");
		
		int length = categoryNames.length - 1;
		for (int i = 1; i <= length; ++i)
		{
			categoryNames[i] = removeSpecialCharacter(categoryNames[i]);
		}
		
		while ((line = reader.readLine()) != null)
		{
			String[] array = line.split(",");
			long business = Long.parseLong(array[0]);
			
			HashSet<Integer> categorySet = new HashSet<Integer>();
			categoryMap.put(business, categorySet);
			
			for (int i = 1; i <= length; ++i)
			{
				if (array[i].equals("1"))
				{
					categorySet.add(i);
				}
			}
		}
		
		reader.close();
	}
	
	/**
	 * Create full and filtered reviews and distribute them to classes of categories and ratings
	 * @throws IOException
	 */
	public void parseReview() throws IOException
	{
		final String inputName = "data/parsed_review.csv";
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		
		String line;
		reader.readLine();
		
		while ((line = reader.readLine()) != null)
		{
			String[] array = line.split(",");
			if (array.length < 10) continue;

			String reviewString = array[0];
			String userString = array[1];
			String businessString = array[2];
			long business = Long.parseLong(businessString);
			
			String starsString = array[6];
			
			String text = array[8];
			String tagString = array[9];
			
			String[] wordArray = text.split(" ");
			String[] tagArray = tagString.split(" ");
			
			int len = wordArray.length;
			if (len != tagArray.length) continue;
			StringBuffer filteredBuffer = new StringBuffer();
			
			for (int i = 0; i < len; ++i)
			{
				String word = wordArray[i];
				String tag = tagArray[i];
				
				if (POSChecker.checkTag(tag))
				{
					filteredBuffer.append(word).append(" ");
				}
			}
			
			String filteredText = filteredBuffer.toString().trim();
			if (filteredText.equals("")) continue;
			if (!categoryMap.containsKey(business)) continue;
			
			HashSet<Integer> categorySet = categoryMap.get(business);
			String filename = reviewString + "_" + userString + "_" + businessString + ".txt";
			
			for (int i : categorySet)
			{
				String categoryName = categoryNames[i];
				String dirString = "data/full/" + categoryName + "/" + starsString;
				File dir = new File(dirString);
				if (!dir.exists()) dir.mkdirs();
				
				PrintWriter writer = new PrintWriter(new FileWriter(dirString + "/" + filename));
				writer.println(text);
				writer.close();
				
				dirString = "data/filtered/" + categoryName + "/" + starsString;
				dir = new File(dirString);
				if (!dir.exists()) dir.mkdirs();
				
				writer = new PrintWriter(new FileWriter(dirString + "/" + filename));
				writer.println(text);
				writer.close();
			}
		}
		
		reader.close();
	}
	
	/**
	 * Collect reviews in the same category from different ratings 
	 * @throws IOException
	 */
	public void collectReview() throws IOException
	{
		Vector<Pair> pairList = new Vector<Pair>();
		
		String fullRootPath = "data/part/full";
		String filteredRootPath = "data/part/filtered";
		
		File fullRootDir = new File(fullRootPath);
		for (String category : fullRootDir.list())
		{
			String fullCategoryPath = fullRootPath + "/" + category;
			String filteredCategoryPath = filteredRootPath + "/" + category;
			
			String fullOutputPath = "data/partall/full/" + category;
			File fullOutputDir = new File(fullOutputPath);
			if (!fullOutputDir.exists()) fullOutputDir.mkdirs();
			
			String filteredOutputPath = "data/partall/filtered/" + category;
			File filteredOutputDir = new File(filteredOutputPath);
			if (!filteredOutputDir.exists()) filteredOutputDir.mkdirs();
			
			File fullCategoryDir = new File(fullCategoryPath);
			int size = 0;
			for (String stars : fullCategoryDir.list())
			{
				String fullStarPath = fullCategoryPath + "/" + stars;
				String filteredStarPath = filteredCategoryPath + "/" + stars;
				
				File fullStarDir = new File(fullStarPath);				
				size += fullStarDir.list().length;
				
				for (String filename : fullStarDir.list())
				{
					String input, output, line;
					BufferedReader reader;
					PrintWriter writer;
					
					input = fullStarPath + "/" + filename;
					output = fullOutputPath + "/" + stars + "_" + filename;
					
					reader = new BufferedReader(new FileReader(input));
					writer = new PrintWriter(new FileWriter(output));

					line = reader.readLine();
					writer.println(line);
					
					reader.close();
					writer.close();					
					
					input = filteredStarPath + "/" + filename;
					output = filteredOutputPath + "/" + stars + "_" + filename;
					
					reader = new BufferedReader(new FileReader(input));
					writer = new PrintWriter(new FileWriter(output));

					line = reader.readLine();
					writer.println(line);
					
					reader.close();
					writer.close();
				}
			}
			
			pairList.add(new Pair(size, category));
		}
		
		Collections.sort(pairList);
		Collections.reverse(pairList);
		
		PrintWriter writer = new PrintWriter(new FileWriter("data/count_partall.txt"));
		for (Pair pair : pairList) writer.println(pair.getValue() + "\t" + pair.getKey());
		writer.close();
	}
	
	/**
	 * Create text files for LDA clustering
	 * @throws IOException
	 */
	public void createLdaTextFiles() throws IOException
	{
		String[] selectedTags = { "artsentertainment", "automotive", "beautyspas", "chinese", "eventplanningservices", "grocery", "hotelstravel", "nightlife", "restaurants", "shopping" };
		HashSet<String> tagSet = new HashSet<String>();
		for (String tag : selectedTags) tagSet.add(tag);
		
		final int RANDOM = 5;
		
		String rootPath = "data/nostop/part/filtered";
		
		File rootDir = new File(rootPath);
		for (String category : rootDir.list())
		{
			if (!tagSet.contains(category)) continue;
			
			String outputPath1 = "data/partlda/" + category;
			File outputDir1 = new File(outputPath1);
			if (!outputDir1.exists()) outputDir1.mkdirs();
			
			String outputPath2 = "data/partlda2/" + category;
			File outputDir2 = new File(outputPath2);
			if (!outputDir2.exists()) outputDir2.mkdirs();
			
			Vector<String> list = new Vector<String>();
			
			PrintWriter writer = new PrintWriter(new FileWriter(outputPath1 + "/label.txt"));
			
			String categoryPath = rootPath + "/" + category;
			
			File categoryDir = new File(categoryPath);
			for (String stars : categoryDir.list())
			{
				if (!stars.equals("4") && !stars.equals("5")) continue;
				
				String starPath = categoryPath + "/" + stars;
				
				File starDir = new File(starPath);
				
				for (String filename : starDir.list())
				{
					String input, line;
					BufferedReader reader;
					
					input = starPath + "/" + filename;
					reader = new BufferedReader(new FileReader(input));
					line = reader.readLine();
					list.add(line);
					reader.close();
					
					writer.println(category + "_" + stars + "_" + filename.replace(".txt", ""));
					
					int ran = (int) (Math.random() * RANDOM);
					if (ran > 0) continue;
					
					PrintWriter writer2 = new PrintWriter(new FileWriter(outputPath2 + "/" + stars + "_" + filename));
					writer2.println(line);
					writer2.close();
				}
			}
			
			writer.close();
			
			writer = new PrintWriter(new FileWriter(outputPath1 + "/doc.txt"));
			writer.println(list.size());
			for (String line : list) writer.println(line);
			writer.close();
		}
	}
	
	public void filterStopWords(File file) throws IOException
	{
		String path = file.getAbsolutePath();
		String outPath = path.replace("\\data\\", "\\data\\nostop\\");
		
		if (file.isDirectory())
		{
			File outDir = new File(outPath);
			if (!outDir.exists()) outDir.mkdirs();
			
			for (File subfile : file.listFiles()) filterStopWords(subfile);
		}
		else
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			reader.close();
			
			String[] words = line.split(" ");
			StringBuffer buffer = new StringBuffer();
			for (String word : words)
			{
				if (!StopWords.checkStop(word)) buffer.append(word).append(" ");
			}
			
			PrintWriter writer = new PrintWriter(new FileWriter(outPath));
			writer.println(buffer.toString().trim());
			writer.close();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		TextFileCreator creator = new TextFileCreator();
		
		creator.filterStopWords(new File("E:\\Programming\\Eclipse\\BigData\\data\\part\\filtered"));
		creator.createLdaTextFiles();
	}

}
