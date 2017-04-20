package com.twitterBackEnd;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import javax.jws.WebService;

@WebService
public class HomeJava 
{
	public Boolean signup(String inputEmail, String inputFirstname, String inputLastname,String encrypted) throws ClassNotFoundException, SQLException
	{
		System.out.println("inside boolean");
		mysql sql = new mysql();
		String query = "insert into Account(password,email,firstname,lastname) values('" + encrypted + "','" + inputEmail + "','" + inputFirstname+ "','" + inputLastname + "')";
		System.out.println(query);
		int i = sql.insertData(query);
		System.out.println("inside signup after query");
		if(i<0){
			System.out.println("no insert operation performed");
			return false;
		}
		else{
			System.out.println("values inserted succefully");
			return true;
		}
	}
			
	public Boolean username(String inputUsername, String inputEmail) throws ClassNotFoundException, SQLException
	{
		System.out.println("inside username");
		mysql sql = new mysql();
		System.out.println("hello");
		String query1 ="UPDATE Account SET UserName ='"+inputUsername+"' WHERE Email='"+inputEmail+"'";
		System.out.println(query1);
		int ii = sql.insertData(query1);
		
		if(ii<0){
			return false;
		}
		else{
			System.out.println("inserted");
			return true;
		}
	}
	
	public Boolean signin(String inputEmail, String hash) throws ClassNotFoundException, SQLException
	{
		System.out.println("inside signin");
		mysql sql = new mysql();
		String query = "select * from Account where Email='"+inputEmail+"' and Password='" + hash +"'";
		System.out.println(query);
		ResultSet rs = sql.fetchData(query);
		System.out.println("result set consist of: "+rs.last());
		rs.last();
		String user=rs.getString(2);
		if(user == null){
			System.out.println("no user found");
			return false;
		}
		else{
			System.out.println("user information retrieved succefully");
			return true;
		}
	}
	
	public String onLoadData(String inputEmail) throws ClassNotFoundException, SQLException
	{
		mysql sql = new mysql();
		String s;
		
		JSONArray tweets = new JSONArray();
		ResultSet rs = null,rs1 = null,rs2 = null,rs3= null,rs4 = null,rs5=null;
		String fetchTweets = "SELECT Tweet.Tweet, Account.FirstName, Account.LastName, Account.UserName FROM Tweet INNER JOIN Account ON Tweet.Email = Account.Email WHERE Tweet.Email IN (SELECT Followed FROM Follow WHERE Follows = '"+inputEmail+"') OR Tweet.Email = '"+inputEmail+"' ORDER BY  Tweet.Time DESC";
		System.out.println(fetchTweets);
		rs = sql.fetchData(fetchTweets);
		while(rs.next())
		{
			JSONObject json = new JSONObject();
			json.put("Tweet", rs.getString("Tweet"));
			json.put("FirstName", rs.getString("FirstName"));
			json.put("LastName", rs.getString("LastName"));
			json.put("UserName", rs.getString("UserName"));
			tweets.add(json);
		}
		s = tweets.toString();
		System.out.println(tweets+":string:"+s);
		
		JSONArray userInfo = new JSONArray();
		String userDetail = "SELECT * FROM Account WHERE Email ='"+inputEmail+"'";
		System.out.println(userDetail);
		rs1 = sql.fetchData(userDetail);
		while(rs1.next())
		{
			JSONObject json = new JSONObject();
			json.put("FirstName", rs1.getString("FirstName"));
			json.put("LastName", rs1.getString("LastName"));
			json.put("UserName", rs1.getString("UserName"));
			System.out.println(json);
			s+="\n";
			s+=json.toString();
		}
		
		String numOfFollowing = "SELECT COUNT(*) AS num1 FROM Follow WHERE Follows ='"+inputEmail+"'";
		System.out.println(numOfFollowing);
		rs2 = sql.fetchData(numOfFollowing);
		rs2.next();
		s+="\n";
		System.out.println(rs2.getInt(1));
		s+=rs2.getString("num1");
		
		String numOfFollowers = "SELECT COUNT(*) AS num2 FROM Follow WHERE Followed ='"+inputEmail+"'";
		System.out.println(numOfFollowers);
		rs3 = sql.fetchData(numOfFollowers);
		rs3.next();
		s+="\n";
		s+=rs3.getString("num2");
		
		String numOfTweets = "SELECT COUNT(*) AS num3 FROM Tweet WHERE Email ='"+inputEmail+"'";
		System.out.println(numOfTweets);
		rs4 = sql.fetchData(numOfTweets);
		rs4.next();
		s+="\n";
		s+=rs4.getString("num3");
		
		JSONArray suggestedUsers = new JSONArray();
		String fetchUsers = "SELECT * FROM Account WHERE Email NOT IN (SELECT Followed FROM Follow WHERE Follows = '"+inputEmail+"') AND Email != '"+inputEmail+"' LIMIT 5";
		System.out.println(fetchUsers);
		rs5 = sql.fetchData(fetchUsers);
		while(rs5.next())
		{
			JSONObject json = new JSONObject();
			json.put("Email", rs5.getString("Email"));
			json.put("FirstName", rs5.getString("FirstName"));
			json.put("LastName", rs5.getString("LastName"));
			json.put("UserName", rs5.getString("UserName"));
			suggestedUsers.add(json);
		}
		s+="\n";
		s+=suggestedUsers.toString();
		return s;
	}
	
	public Boolean insertTweet(String Email, String Tweet, String Time) throws ClassNotFoundException, SQLException
	{
		mysql sql = new mysql();
		String insertTweet = "INSERT INTO Tweet(Email, Tweet, Time) VALUES ('"+Email+"', '"+Tweet+"', '"+Time+"')";
		System.out.println(insertTweet);
		String[] hash;
		int i = sql.insertData(insertTweet);
		System.out.println("inside insertTweet after query");
		if(i<0){
			System.out.println("no insert operation performed");
			return false;
		}
		else{
			System.out.println("values inserted succefully");
			
			hash = Tweet.split("#");
			int n = 1;
			while(n < hash.length){
				String hashTag = hash[n].substring(0, hash[n].indexOf(' '));
				String insertHashtag = "INSERT INTO HashTable(Hash, Tweet, Email, Time) VALUES('"+hashTag+"', '"+Tweet+"', '"+Email+"', '"+Time+"')";
				System.out.println(insertHashtag);
				int ii = sql.insertData(insertTweet);
				System.out.println("inside insertTweet after query");
				if(ii<0){
					System.out.println("no insert operation performed");
					return false;
				}
				else{
					return true;
				}
			}
		}
		return true;
	}
	
	public String getProfile(String inputEmail) throws ClassNotFoundException, SQLException
	{
		mysql sql = new mysql();
		String s;
		
		JSONArray tweets = new JSONArray();
		ResultSet rs = null,rs1 = null,rs2 = null,rs3= null,rs4 = null,rs5 = null,rs6 = null, rs7=null;
		String fetchTweets = "SELECT Tweet.Tweet, Account.FirstName, Account.LastName, Account.Email FROM Tweet INNER JOIN Account ON Tweet.Email = Account.Email WHERE Tweet.Email = '"+inputEmail+"' ORDER BY  Tweet.Time DESC";
		System.out.println(fetchTweets);
		rs = sql.fetchData(fetchTweets);
		while(rs.next())
		{
			JSONObject json = new JSONObject();
			json.put("Tweet", rs.getString("Tweet"));
			json.put("FirstName", rs.getString("FirstName"));
			json.put("LastName", rs.getString("LastName"));
			tweets.add(json);
		}
		s = tweets.toString();
		System.out.println(tweets+":string:"+s);
		
		JSONArray userInfo = new JSONArray();
		String userDetail = "SELECT * FROM Account WHERE Email ='"+inputEmail+"'";
		System.out.println(userDetail);
		rs1 = sql.fetchData(userDetail);
		while(rs1.next())
		{
			JSONObject json = new JSONObject();
			json.put("FirstName", rs1.getString("FirstName"));
			json.put("LastName", rs1.getString("LastName"));
			json.put("UserName", rs1.getString("UserName"));
			json.put("location", rs1.getString("location"));
			json.put("dob", rs1.getString("dob"));
			json.put("phone", rs1.getString("phone"));
			System.out.println(json);
			s+="\n";
			s+=json.toString();
		}
		
		String numOfFollowing = "SELECT COUNT(*) AS num1 FROM Follow WHERE Follows ='"+inputEmail+"'";
		System.out.println(numOfFollowing);
		rs2 = sql.fetchData(numOfFollowing);
		rs2.next();
		s+="\n";
		System.out.println(rs2.getInt(1));
		s+=rs2.getString("num1");
		
		String numOfFollowers = "SELECT COUNT(*) AS num2 FROM Follow WHERE Followed ='"+inputEmail+"'";
		System.out.println(numOfFollowers);
		rs3 = sql.fetchData(numOfFollowers);
		rs3.next();
		s+="\n";
		s+=rs3.getString("num2");
		
		String numOfTweets = "SELECT COUNT(*) AS num3 FROM Tweet WHERE Email ='"+inputEmail+"'";
		System.out.println(numOfTweets);
		rs4 = sql.fetchData(numOfTweets);
		rs4.next();
		s+="\n";
		s+=rs4.getString("num3");
		
		JSONArray following = new JSONArray();
		String followingU = "SELECT Follow.Followed, Account.FirstName, Account.LastName, Account.UserName, Account.Email FROM Follow INNER JOIN Account ON Follow.Followed = Account.Email WHERE Follow.Follows = '"+inputEmail+"'" ;
		System.out.println(followingU);
		rs5 = sql.fetchData(followingU);
		while(rs5.next())
		{
			JSONObject json = new JSONObject();
			json.put("Followed", rs5.getString("Followed"));
			json.put("FirstName", rs5.getString("FirstName"));
			json.put("LastName", rs5.getString("LastName"));
			json.put("UserName", rs5.getString("UserName"));
			json.put("Email", rs5.getString("Email"));
			following.add(json);
		}
		s+="\n";
		s+=following.toString();
		
		JSONArray followers = new JSONArray();
		String followersU = "SELECT Follow.Follows, Account.FirstName, Account.LastName, Account.UserName, Account.Email FROM Follow INNER JOIN Account ON Follow.Follows = Account.Email WHERE Follow.Followed = '"+inputEmail+"'" ;
		System.out.println(followersU);
		rs6 = sql.fetchData(followersU);
		while(rs6.next())
		{
			JSONObject json = new JSONObject();
			json.put("Follows", rs6.getString("Follows"));
			json.put("FirstName", rs6.getString("FirstName"));
			json.put("LastName", rs6.getString("LastName"));
			json.put("UserName", rs6.getString("UserName"));
			json.put("Email", rs6.getString("Email"));
			followers.add(json);
		}
		s+="\n";
		s+=followers.toString();
		
		JSONArray suggestedUsers = new JSONArray();
		String fetchUsers = "SELECT * FROM Account WHERE Email NOT IN (SELECT Followed FROM Follow WHERE Follows = '"+inputEmail+"') AND Email != '"+inputEmail+"' LIMIT 5";
		System.out.println(fetchUsers);
		rs7 = sql.fetchData(fetchUsers);
		while(rs7.next())
		{
			JSONObject json = new JSONObject();
			json.put("Email", rs7.getString("Email"));
			json.put("FirstName", rs7.getString("FirstName"));
			json.put("LastName", rs7.getString("LastName"));
			json.put("UserName", rs7.getString("UserName"));
			suggestedUsers.add(json);
		}
		s+="\n";
		s+=suggestedUsers.toString();
		return s;
	}
	
	public String searchHash(String simplestr) throws ClassNotFoundException, SQLException
	{
		mysql sql = new mysql();
		String s;
		
		JSONArray tweets = new JSONArray();
		ResultSet rs = null,rs1 = null,rs2 = null,rs3= null,rs4 = null;
		String serchHashtag = "SELECT HashTable.Tweet, Account.FirstName, Account.LastName, Account.Email FROM HashTable INNER JOIN Account ON HashTable.Email = Account.Email WHERE Hash LIKE '"+simplestr+"'";
		System.out.println(serchHashtag);
		rs = sql.fetchData(serchHashtag);
		while(rs.next())
		{
			JSONObject json = new JSONObject();
			System.out.println(rs.getString("Tweet"));
			
			json.put("Tweet", rs.getString("Tweet"));
			json.put("FirstName", rs.getString("FirstName"));
			json.put("LastName", rs.getString("LastName"));
			json.put("Email", rs.getString("Email"));
			tweets.add(json);
		}
		s = tweets.toString();
		return s;
	}
	
	public boolean follow (String inputEmail, String followEmail) throws ClassNotFoundException, SQLException
	{
		mysql sql = new mysql();
		
		String followUser = "INSERT INTO Follow(Follows, Followed) VALUES('"+inputEmail+"', '"+followEmail+"')";
		System.out.println(followUser);
		int i = sql.insertData(followUser);
		
		if(i<0){
			System.out.println("no insert operation performed");
			return false;
		}
		else{
			System.out.println("follower inserted succefully");
			return true;
		}
	}
	
	public boolean addBio (String inputEmail, String location, String dob, String phone) throws ClassNotFoundException, SQLException
	{
		mysql sql = new mysql();
		String insertBio = "UPDATE Account SET location= '"+location+"', dob = '"+dob+"', phone = '"+phone+"'  WHERE Email = '"+inputEmail+"'";
		System.out.println(insertBio);
		int i = sql.insertData(insertBio);
		if(i<0){
			System.out.println("no insert operation performed");
			return false;
		}
		else{
			System.out.println("insertBio inserted succefully");
			return true;
		}
	}
	
}
