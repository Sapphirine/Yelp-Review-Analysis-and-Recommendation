package bigdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Business
{
	public static JSONArray loadBusinesses(HashSet<Long> hashSet)
	{
		JSONArray businessArray = new JSONArray();
		final String dbName = "data/business.db";

		Connection connection = null;
		PreparedStatement queryStatement = null;
		ResultSet resultSet = null;
		
		try
		{
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
			queryStatement = connection.prepareStatement("select * from business where id = ?;");
			
			for (long id : hashSet)
			{
				queryStatement.setLong(1, id);
				resultSet = queryStatement.executeQuery();
				
				if (resultSet.next())
				{
					JSONObject json = new JSONObject();
					
					String name = resultSet.getString("name");
					json.put("name", name);
					
					int review_count = resultSet.getInt("review_count");
					json.put("review_count", review_count);
					
					double stars = resultSet.getDouble("stars");
					json.put("stars", stars);
					
					String city = resultSet.getString("city");
					json.put("city", city);
					
					String state = resultSet.getString("state");
					json.put("state", state);
					
					String address = resultSet.getString("address");
					json.put("address", address);
					
					String categories = resultSet.getString("categories");
					json.put("categories", categories);
					
					businessArray.add(json);
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (connection != null) connection.close();
				if (queryStatement != null) queryStatement.close();
				if (resultSet != null) resultSet.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return businessArray;
	}
	
	public static void createDB() throws SQLException, ClassNotFoundException, IOException
	{
		final String inputName = "data/business.json";
		final String dbName = "data/business.db";
		
		Class.forName("org.sqlite.JDBC");
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
		Statement statement = connection.createStatement();
		statement.executeUpdate("create table if not exists business ("
				+ "id integer primary key, name text, review_count integer, stars real, "
				+ "city text, state text, address text, categories address);");
		
		BufferedReader reader = new BufferedReader(new FileReader(inputName));
		String line;
		
		PreparedStatement insertStatement = connection.prepareStatement(
				"insert into business values (?, ?, ?, ?, ?, ?, ?, ?);");
		
		while ((line = reader.readLine()) != null)
		{
			JSONObject json = JSONObject.fromObject(line);
			
			long id = json.getLong("id");
			insertStatement.setLong(1, id);
			
			String name = json.getString("name");
			insertStatement.setString(2, name);
			
			int review_count = json.getInt("review_count");
			insertStatement.setInt(3, review_count);
			
			double stars = json.getDouble("stars");
			insertStatement.setDouble(4, stars);
			
			String city = json.getString("city");
			insertStatement.setString(5, city);
			
			String state = json.getString("state");
			insertStatement.setString(6, state);
			
			String address = json.getString("address");
			insertStatement.setString(7, address);
			
			String categories = json.getString("categories");
			insertStatement.setString(8, categories);
			
			insertStatement.execute();
		}
		
		reader.close();
		
		connection.close();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
	{
		createDB();
	}
}
