import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Hashtable;


public class RecommendedBusiness
{
	private static void getRecommendedBusinessByClass() throws IOException
	{
		String[] selectedTags = { "artsentertainment", "automotive", "beautyspas", "chinese", "eventplanningservices", "grocery", "hotelstravel", "nightlife", "restaurants", "shopping" };
		HashSet<Long> allBusinessSet = new HashSet<Long>();
		
		for (String tag : selectedTags)
		{
			String dirPath = "data/partall/filtered/" + tag;
			File dir = new File(dirPath);
			HashSet<Long> businessSet = new HashSet<Long>();
			
			for (File file : dir.listFiles())
			{
				String[] array = file.getName().split("_");
				
				int stars = Integer.parseInt(array[0]);
				if (stars < 4) continue;
				String businessString = array[3].replace(".txt", "");
				long business = Long.parseLong(businessString);
				
				businessSet.add(business);
				allBusinessSet.add(business);
			}
			
			String outDirPath = "data/rec/classify";
			File outDir = new File(outDirPath);
			if (!outDir.exists()) outDir.mkdirs();
			
			PrintWriter writer = new PrintWriter(new FileWriter(outDirPath + "/" + tag + ".txt"));
			for (long business : businessSet)
			{
				writer.println(business);
			}
			writer.close();			
		}
		
		PrintWriter writer = new PrintWriter(new FileWriter("data/rec/classify/all.txt"));
		for (long business : allBusinessSet)
		{
			writer.println(business);
		}
		writer.close();		
	}
	
	public static void getRecommendedBusinessByCluster() throws IOException
	{
		String[] selectedTags = { "artsentertainment", "automotive", "beautyspas", "chinese", "eventplanningservices", "grocery", "hotelstravel", "nightlife", "restaurants", "shopping" };
		HashSet<Long> allBusinessSet = new HashSet<Long>();
		
		for (String tag : selectedTags)
		{
			String dirPath = "data/partlda/" + tag;
			String labelPath = dirPath + "/" + "label.txt";
			String thetaPath = dirPath + "/" + "model-final.theta";
			
			BufferedReader labelReader = new BufferedReader(new FileReader(labelPath));
			BufferedReader thetaReader = new BufferedReader(new FileReader(thetaPath));
			
			Hashtable<Integer, HashSet<Long>> businessMap = new Hashtable<Integer, HashSet<Long>>();
			for (int i = 0; i < 5; ++i) businessMap.put(i, new HashSet<Long>());
			
			double sum = 0;
			double[] counts = new double[5];
			
			String label, theta;
			while ((label = labelReader.readLine()) != null)
			{
				theta = thetaReader.readLine();
				
				String[] array = label.split("_");
				long business = Long.parseLong(array[4]);
				allBusinessSet.add(business);
				
				array = theta.split(" ");
				
				double bestP = Double.NaN;
				int bestCluster = -1;
				
				for (int i = 0; i < 5; ++i)
				{
					double p = Double.parseDouble(array[i]);
					if (Double.isNaN(bestP) || p > bestP)
					{
						bestP = p;
						bestCluster = i;
					}
				}
				
				counts[bestCluster] += 1;
				sum += 1;
				businessMap.get(bestCluster).add(business);
			}
						
			labelReader.close();
			thetaReader.close();
			
			String outDirPath = "data/rec/lda/" + tag;
			File outDir = new File(outDirPath);
			if (!outDir.exists()) outDir.mkdirs();
			
			for (int i = 0; i < 5; ++i)
			{
				PrintWriter writer = new PrintWriter(new FileWriter(outDirPath + "/" + i + ".txt"));
				
				HashSet<Long> businessSet = businessMap.get(i);
				for (long business : businessSet)
				{
					writer.println(business);
				}
				writer.close();	
			}
			
			PrintWriter writer = new PrintWriter(new FileWriter(outDirPath + "/share.txt"));
			for (int i = 0; i < 5; ++i)
			{
				writer.println(counts[i] / sum);
			}
			writer.close();	
		}
		
		PrintWriter writer = new PrintWriter(new FileWriter("data/rec/lda/all.txt"));
		for (long business : allBusinessSet)
		{
			writer.println(business);
		}
		writer.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		getRecommendedBusinessByClass();
		getRecommendedBusinessByCluster();
	}
}
