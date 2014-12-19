package bigdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Vector;

import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import jgibblda.Model;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class ClusterClassifier
{
	public static int classify(String className, Vector<String> wordList) throws CmdLineException, IOException
	{
		String[] args = { "-inf", "-model", "model-final", "-niters", "30", "-twords", "20", "-dfile", "test.txt", "-dir", "" };
		String path = "data/lda/" + className;
		args[10] = path;
		
		if (wordList.size() == 0) wordList.add("a");
		
		PrintWriter writer = new PrintWriter(new FileWriter(path + "/test.txt"));
		writer.println(1);
		for (String word : wordList) writer.print(word + " ");
		writer.close();
		
		LDACmdOption option = new LDACmdOption();
		CmdLineParser parser = new CmdLineParser(option);
		parser.parseArgument(args);
		
		Inferencer inferencer = new Inferencer();
		inferencer.init(option);
		
		Model newModel = inferencer.inference();
		int bestCluster = 0;
		for (int i = 1; i < 5; ++i)
		{
			if (newModel.theta[0][i] > newModel.theta[0][bestCluster]) bestCluster = i;
		}
		
		return bestCluster;
	}
	
	/**
	 * Get recommended business id for a class
	 * @param className
	 * @param cluster
	 * @return
	 * @throws IOException
	 */
	public static HashSet<Long> getRecommendedBusiness(String className, int cluster) throws IOException
	{
		final int LIMIT = 5;
		
		HashSet<Long> businessSet = new HashSet<Long>();
		Vector<Long> businessList = new Vector<Long>();
		
		String path = "data/rec/lda/" + className + "/" + cluster + ".txt";
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
	 * @param cluster
	 * @return
	 * @throws IOException
	 */
	public static HashSet<String> getKeywords(String className, int cluster) throws IOException
	{
		HashSet<String> keywords = new HashSet<String>();
		String path = "data/keyword/lda/" + className + "/" + cluster;
		
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
	
	public static void main(String[] args) throws IOException, CmdLineException
	{
		String className = "restaurants";
		TextParser parser = new TextParser();
		int cluster = classify(className, parser.parseText(""));
		System.out.println(Business.loadBusinesses(getRecommendedBusiness(className, cluster)));
	}
}
