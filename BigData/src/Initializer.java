import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Initializer
{
	private static TextParser parser = new TextParser();
	
	private static Hashtable<String, Long> userMap = new Hashtable<String, Long>();
	private static Hashtable<String, Long> businessMap = new Hashtable<String, Long>();
	
	/**
	 * Convert review json to csv
	 * @throws IOException
	 */
	public static void parseReview() throws IOException
	{
		final String inputName = "data/yelp_academic_dataset_review.json";
		final String outputName = "data/review.csv";
		
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		writer.println("review,user,business,votes_funny,votes_useful,votes_cool,stars,date,text");
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		
		long review = 1;
		String line;
		while ((line = reader.readLine()) != null)
		{
			JSONObject json = JSONObject.fromObject(line);
			
			String userString = json.getString("user_id");
			String businessString = json.getString("business_id");
			
			if (!userMap.containsKey(userString) || !businessMap.containsKey(businessString)) continue;
			long user = userMap.get(userString);
			long business = businessMap.get(businessString);
			
			JSONObject votes = json.getJSONObject("votes");
			int votes_funny = votes.getInt("funny");
			int votes_useful = votes.getInt("useful");
			int votes_cool = votes.getInt("cool");

			int stars = json.getInt("stars");
			String date = json.getString("date");
			String text = json.getString("text").replace(",", "||").replace("\n", " ");
			
			writer.println(review + "," + user + "," + business + "," + votes_funny + "," + votes_useful + "," + votes_cool
					+ "," + stars + "," + date + "," + text);
			
			++review;
		}
		
		writer.close();
		reader.close();
	}

	/**
	 * Convert friend json to csv
	 * @throws IOException
	 */
	public static void parseFriend() throws IOException
	{
		final String inputName = "data/yelp_academic_dataset_user.json";
		final String outputName = "data/friend.csv";
		
		HashSet<String> idSet = new HashSet<String>();
		Hashtable<String, HashSet<String>> allFriends = new Hashtable<String, HashSet<String>>();
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		String line;
		while ((line = reader.readLine()) != null)
		{
			JSONObject json = JSONObject.fromObject(line);
			
			String user = json.getString("user_id");
			idSet.add(user);
			
			HashSet<String> friends = new HashSet<String>();
			allFriends.put(user, friends);
			
			JSONArray array = json.getJSONArray("friends");
			int size = array.size();
			for (int i = 0; i < size; ++i)
			{
				String friend = array.getString(i);
				friends.add(friend);
			}
		}
		reader.close();
		
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		
		String[] idArray = idSet.toArray(new String[idSet.size()]);
		int size = idArray.length;
		
		for (int i = 0; i < size - 1; ++i)
		{
			String first = idArray[i];
			HashSet<String> friends = allFriends.get(first);
			
			for (int j = i + 1; j < size; ++j)
			{
				String second = idArray[j];
				if (friends.contains(second))
				{
					if (userMap.containsKey(first) && userMap.containsKey(second))
					{
						writer.println(userMap.get(first) + "," + userMap.get(second));
					}
				}
			}
		}
		
		writer.close();
	}

	/**
	 * Convert user json to csv
	 * @throws IOException
	 */
	public static void parseUser() throws IOException
	{
		final String inputName = "data/yelp_academic_dataset_user.json";
		final String outputName = "data/user.csv";
		
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		writer.println("user,yelping_since,votes_funny,votes_useful,votes_cool,review_count,name,average_stars");
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		
		long user = 1;
		String line;
		while ((line = reader.readLine()) != null)
		{
			JSONObject json = JSONObject.fromObject(line);
			
			String userString = json.getString("user_id");
			userMap.put(userString, user);
			
			String yelping_since = json.getString("yelping_since");
			
			JSONObject votes = json.getJSONObject("votes");
			int votes_funny = votes.getInt("funny");
			int votes_useful = votes.getInt("useful");
			int votes_cool = votes.getInt("cool");
			
			int review_count = json.getInt("review_count");
			String name = json.getString("name").replace(",", "||");
			double average_stars = json.getDouble("average_stars");
			
			writer.println(user + "," + yelping_since + "," + votes_funny + "," + votes_useful + "," + votes_cool
					+ "," + review_count + "," + name + "," + average_stars);
			
			++user;
		}
		
		writer.close();
		reader.close();
	}

	/**
	 * Convert category json to csv
	 * @throws IOException
	 */
	public static void parseCategory() throws IOException
	{
		Vector<String> idList = new Vector<String>();
		Vector<HashSet<String>> categoriesList = new Vector<HashSet<String>>();
		HashSet<String> allCategories = new HashSet<String>();
		
		final String inputName = "data/yelp_academic_dataset_business.json";
		final String outputName = "data/category.csv";
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		
		String line;
		while ((line = reader.readLine()) != null)
		{
			JSONObject json = JSONObject.fromObject(line);
			
			String business = json.getString("business_id");
			JSONArray array = json.getJSONArray("categories");
			
			idList.add(business);
			HashSet<String> categories = new HashSet<String>();
			categoriesList.add(categories);
			
			int size = array.size();
			for (int i = 0; i < size; ++i)
			{
				String category = array.getString(i).replace(",", "||");
				allCategories.add(category);
				categories.add(category);
			}
		}
		
		String[] allCategoriesArray = allCategories.toArray(new String[allCategories.size()]);
		
		writer.print("business");
		for (String category : allCategoriesArray) writer.print("," + category);
		writer.println();
		
		int size = idList.size();
		for (int i = 0; i < size; ++i)
		{
			String businessString = idList.get(i);
			HashSet<String> categories = categoriesList.get(i);
			
			if (!businessMap.containsKey(businessString)) continue;
			long business = businessMap.get(businessString);
			
			writer.print(business);
			for (String category : allCategoriesArray) writer.print("," + (categories.contains(category) ? "1" : "0"));
			writer.println();
		}

		reader.close();
		writer.close();
	}

	/**
	 * Convert business json to csv
	 * @throws IOException
	 */
	public static void parseBusiness() throws IOException
	{
		final String inputName = "data/yelp_academic_dataset_business.json";
		final String outputName = "data/business.csv";
		
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		writer.println("business,open,city,review_count,name,state,stars");
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		
		long business = 1;
		String line;
		while ((line = reader.readLine()) != null)
		{
			JSONObject json = JSONObject.fromObject(line);
			
			String businessString = json.getString("business_id");
			businessMap.put(businessString, business);
			
			boolean open = json.getBoolean("open");
			String city = json.getString("city").replace(",", "||");
			int review_count = json.getInt("review_count");
			String name = json.getString("name").replace(",", "||");
			String state = json.getString("state");
			double stars = json.getDouble("stars");
			
			writer.println(business + "," + open + "," + city + "," + review_count + "," + name + "," + state + "," + stars);
			
			++business;
		}
		
		writer.close();
		reader.close();
	}
	
	/**
	 * Filter selected business
	 * @throws IOException
	 */
	public static void filterBusiness() throws IOException
	{
		final String allBusiness1 = "data/rec/lda/all.txt";
		final String allBusiness2 = "data/rec/classify/all.txt";
		
		HashSet<Long> allBusinessSet = new HashSet<Long>();
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(allBusiness1));
		while ((line = reader.readLine()) != null) allBusinessSet.add(Long.parseLong(line));
		reader.close();
		reader = new BufferedReader(new FileReader(allBusiness2));
		while ((line = reader.readLine()) != null) allBusinessSet.add(Long.parseLong(line));
		reader.close();
		
		final String inputName = "data/yelp_academic_dataset_business.json";
		final String outputName = "data/business.json";
		
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		reader = new BufferedReader(new FileReader(inputName));
		
		long business = 1;
		while ((line = reader.readLine()) != null)
		{
			if (allBusinessSet.contains(business))
			{
				JSONObject json = JSONObject.fromObject(line);
				JSONObject newJson = new JSONObject();
				
				newJson.put("id", business);
				String name = json.getString("name");
				newJson.put("name", name);				
				int review_count = json.getInt("review_count");
				newJson.put("review_count", review_count);
				double stars = json.getDouble("stars");
				newJson.put("stars", stars);
				String city = json.getString("city");
				newJson.put("city", city);
				String state = json.getString("state");
				newJson.put("state", state);
				String address = json.getString("full_address").replace("\n", " ");
				newJson.put("address", address);
				
				JSONArray array = json.getJSONArray("categories");
				String categories = null;
				
				int len = array.size();
				for (int i = 0; i < len; ++i)
				{
					if (i == 0) categories = array.getString(i);
					else categories += ", " + array.getString(i);
				}
				newJson.put("categories", categories);
				
				writer.println(newJson);
			}
			++business;
		}
		
		writer.close();
		reader.close();
	}

	/**
	 * Convert tip json to csv
	 * @throws IOException
	 */
	public static void parseTip() throws IOException
	{
		final String inputName = "data/yelp_academic_dataset_tip.json";
		final String outputName = "data/tip.csv";
		
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		writer.println("review,user,business,date,text");
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		
		long review = 1;
		String line;
		while ((line = reader.readLine()) != null)
		{
			JSONObject json = JSONObject.fromObject(line);
			
			String userString = json.getString("user_id");
			String businessString = json.getString("business_id");
			
			if (!userMap.containsKey(userString) || !businessMap.containsKey(businessString)) continue;
			long user = userMap.get(userString);
			long business = businessMap.get(businessString);
			
			
			String date = json.getString("date");
			String text = json.getString("text").replace(",", "||").replace("\n", " ");
			
			writer.println(review + "," + user + "," + business + "," + date + "," + text);
			
			++review;
		}
		
		writer.close();
		reader.close();
	}

	/**
	 * Add POS tags to tips
	 * @throws IOException
	 */
	public static void parseTipText() throws IOException
	{
		final String inputName = "data/tip.csv";
		final String outputName = "data/parsed_tip.csv";
		
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		writer.println("review,user,business,date,text,tag");
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		
		String line;
		reader.readLine();
		while ((line = reader.readLine()) != null)
		{
			String[] array = line.split(",");
			if (array.length < 5) continue;
			
			String text = array[4].replace("||", ",").toLowerCase();
			
			String newline = "";
			for (int i = 0; i < 4; ++i) newline += array[i] + ",";
			newline += parser.parseText(text);
			
			writer.println(newline);
		}
		
		writer.close();
		reader.close();
	}

	/**
	 * Add POS tags to reviews
	 * @throws IOException
	 */
	public static void parseReviewText() throws IOException
	{
		final String inputName = "data/review.csv";
		final String outputName = "data/parsed_review.csv";
		
		PrintWriter writer = new PrintWriter(new FileWriter(outputName));
		writer.println("review,user,business,votes_funny,votes_useful,votes_cool,stars,date,text,tag");
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		
		String line;
		reader.readLine();
		while ((line = reader.readLine()) != null)
		{
			String[] array = line.split(",");
			if (array.length < 9) continue;
			
			String text = array[8].replace("||", ",").toLowerCase();
			
			String newline = "";
			for (int i = 0; i < 8; ++i) newline += array[i] + ",";
			newline += parser.parseText(text);
			
			writer.println(newline);
		}
		
		writer.close();
		reader.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		/*
		System.out.println("Parsing Users...");
		parseUser();
		System.out.println("Parsing Friends...");
		parseFriend();
		
		System.out.println("Parsing Businesses...");
		parseBusiness();
		System.out.println("Parsing Categories...");
		parseCategory();
		
		System.out.println("Parsing Reviews...");
		parseReview();
		System.out.println("Parsing Tips...");
		parseTip();
				
		System.out.println("Parsing Tip Text...");
		parseTipText();
		System.out.println("Parsing Review Text...");
		parseReviewText();
		*/
		
		filterBusiness();
	}
}
