package bigdata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

@SuppressWarnings("unchecked")
public class BayesianClassifier
{
	private static final String MODEL = "data/model";
	
	private static Hashtable<String, Double> pTheta;
	private static Hashtable<String, Hashtable<String, Double>> pXThetas;
	
	private static Hashtable<String, String> classNameFormatter;
	
	static
	{
		classNameFormatter = new Hashtable<String, String>();
		classNameFormatter.put("artsentertainment", "Arts & Entertainment");
		classNameFormatter.put("automotive", "Automotive");
		classNameFormatter.put("beautyspas", "Beauty & Spas");
		classNameFormatter.put("chinese", "Chinese");
		classNameFormatter.put("eventplanningservices", "Event Planning Services");
		classNameFormatter.put("grocery", "Grocery");
		classNameFormatter.put("hotelstravel", "Hotels & Travel");
		classNameFormatter.put("nightlife", "Night Life");
		classNameFormatter.put("restaurants", "Restaurants");
		classNameFormatter.put("shopping", "Shopping");
		
		try
		{
			ObjectInputStream istream = new ObjectInputStream(new FileInputStream(MODEL));
			pTheta = (Hashtable<String, Double>) istream.readObject();
			pXThetas = (Hashtable<String, Hashtable<String, Double>>) istream.readObject();
			istream.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the class of given word list
	 * @param wordList
	 * @return
	 */
	public static String classify(Vector<String> wordList)
	{
		Hashtable<String, Double> wordCount = new Hashtable<String, Double>();
		
		for (String word : wordList)
		{
			double count = wordCount.containsKey(word) ? wordCount.get(word) : 0;
			wordCount.put(word, count + 1);
		}
		
		String bestClassName = null;
		double bestP = Double.NaN;
		
		for (String className : pTheta.keySet())
		{
			double p = pTheta.get(className);
			Hashtable<String, Double> pXTheta = pXThetas.get(className);
			
			for (String word : wordCount.keySet())
			{
				if (!pXTheta.containsKey(word)) continue;
				
				double count = wordCount.get(word);
				double value = pXTheta.get(word);
				p += count * value;
			}
			
			if (Double.isNaN(bestP) || p > bestP)
			{
				bestP = p;
				bestClassName = className;
			}
		}
		
		return bestClassName;
	}
	
	/**
	 * Get recommended business id for a class
	 * @param className
	 * @return
	 * @throws IOException
	 */
	public static HashSet<Long> getRecommendedBusiness(String className) throws IOException
	{
		final int LIMIT = 5;
		
		HashSet<Long> businessSet = new HashSet<Long>();
		Vector<Long> businessList = new Vector<Long>();
		
		String path = "data/rec/classify/" + className + ".txt";
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line;
		while ((line = reader.readLine()) != null)
		{
			businessList.add(Long.parseLong(line));
		}
		reader.close();
		
		if (businessList.size() <= LIMIT)
		{
			businessSet.addAll(businessList);
		}
		else
		{
			for (int i = 0; i < LIMIT; ++i)
			{
				boolean flag = false;
				
				while (!flag)
				{
					int ran = (int) (Math.random() * businessList.size());
					long business = businessList.get(ran);
					if (businessSet.contains(business)) continue;
					
					businessSet.add(business);
					businessList.removeElement(business);
					flag = true;
				}
			}
		}
		
		return businessSet;
	}
	
	/**
	 * Get keywords for a class
	 * @param className
	 * @return
	 * @throws IOException
	 */
	public static HashSet<String> getKeywords(String className) throws IOException
	{
		HashSet<String> keywords = new HashSet<String>();
		String path = "data/keyword/classify/" + className + "/tfidf.txt";
		
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			line = line.trim();
			if (line.equals("")) continue;
			keywords.add(line);
		}
		reader.close();
		
		return keywords;
	}
	
	public static String formatClassName(String className)
	{
		if (!classNameFormatter.containsKey(className)) return className;
		return classNameFormatter.get(className);
	}
	
	public static void main(String[] args) throws IOException
	{
		TextParser parser = new TextParser();
		String className = classify(parser.parseText(""));
		System.out.println(Business.loadBusinesses(getRecommendedBusiness(className)));
	}
}
