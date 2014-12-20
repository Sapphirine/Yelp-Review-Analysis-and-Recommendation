<%@ page language="java" contentType="application/json; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java" 
import="bigdata.*"
import="net.sf.json.*"
import="java.net.URLEncoder"
import="java.io.*"
import="java.util.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%
	JSONObject json = new JSONObject();
	String text = request.getParameter("text");
	
	try
	{
		TextParser parser = new TextParser();
		Vector<String> wordList = parser.parseText(text);
		
		String className = BayesianClassifier.classify(wordList);
		HashSet<String> classKeywordSet = BayesianClassifier.getKeywords(className);
		HashSet<Long> classIds = BayesianClassifier.getRecommendedBusiness(className);
		JSONArray classArray = Business.loadBusinesses(classIds);
		
		JSONObject classObj = new JSONObject();
		classObj.put("name", BayesianClassifier.formatClassName(className));
		classObj.put("business", classArray);
		
		JSONArray classKeywords = new JSONArray();
		for (String keyword : classKeywordSet) classKeywords.add(keyword);
		classObj.put("keyword", classKeywords);
		
		int cluster = ClusterClassifier.classify(className, wordList);
		HashSet<String> clusterKeywordSet = ClusterClassifier.getKeywords(className, cluster);
		HashSet<Long> clusterIds = ClusterClassifier.getRecommendedBusiness(className, cluster);
		JSONArray clusterArray = Business.loadBusinesses(clusterIds);
		
		JSONObject clusterObj = new JSONObject();
		clusterObj.put("business", clusterArray);
		
		JSONArray clusterKeywords = new JSONArray();
		for (String keyword : clusterKeywordSet) clusterKeywords.add(keyword);
		clusterObj.put("keyword", clusterKeywords);
		
		json.put("class", classObj);
		json.put("cluster", clusterObj);
		
		json.put("success", true);
	}
	catch (Exception e)
	{
		json.put("success", false);
	}
	
	out.print(json);
	out.flush();
%>
