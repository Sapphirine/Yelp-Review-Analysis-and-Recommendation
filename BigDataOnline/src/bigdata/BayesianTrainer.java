package bigdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

public class BayesianTrainer
{
	private static final String MODEL = "data/model";
	private static final String DIR = "data/classify";
	
	private Hashtable<String, Double> pTheta;
	private Hashtable<String, Hashtable<String, Double>> pXThetas;
	
	private void increment(Hashtable<String, Double> map, String key)
	{
		double value = map.containsKey(key) ? map.get(key) : 1;
		map.put(key, value + 1);
	}
	
	private void normalize(Hashtable<String, Double> map)
	{
		double sum = 0;
		for (double value : map.values()) sum += value;
		double logSum = Math.log(sum);
		
		for (String key : map.keySet())
		{
			double logValue = Math.log(map.get(key));
			map.put(key, logValue - logSum);			
		}
	}
	
	/**
	 * Output the model 
	 * @throws IOException
	 */
	private void output() throws IOException
	{
		ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(MODEL));
		ostream.writeObject(pTheta);
		ostream.writeObject(pXThetas);
		ostream.close();
	}
	
	/**
	 * Generate trained Bayesian model
	 * @throws IOException
	 */
	public void train() throws IOException
	{
		pTheta = new Hashtable<String, Double>();
		pXThetas = new Hashtable<String, Hashtable<String, Double>>();
		
		File dir = new File(DIR);
		for (File subdir : dir.listFiles())
		{
			if (!subdir.isDirectory()) continue;
			
			String className = subdir.getName();
			File[] files = subdir.listFiles();
			
			pTheta.put(className, (double) files.length);
			
			Hashtable<String, Double> pXTheta = new Hashtable<String, Double>();
			pXThetas.put(className, pXTheta);
			
			for (File file : files)
			{
				String text = "", line;
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while ((line = reader.readLine()) != null)
				{
					text += line + " ";
				}
				reader.close();
				
				String[] words = text.split(" ");
				for (String word : words)
				{
					word = word.trim();
					if (word.equals("")) continue;
					
					increment(pXTheta, word);
				}
			}
			
			normalize(pXTheta);
		}
		
		normalize(pTheta);
		output();
	}
	
	public static void main(String[] args) throws IOException
	{
		BayesianTrainer trainer = new BayesianTrainer();
		trainer.train();
	}
}
