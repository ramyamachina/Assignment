package com.example.shinobipiechart;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.util.Log;

/* 
 * Description: AsyncTask background task for HTTP POSt the data
 * 				to the server and getting the response back
 * 				for further processing
 * 
 * Async<Parameters> : String - URL of the TOTANGO server to process the data
 * 						Void
 * 						List<List<String>> - HTTP Response data
 * 
 */

public class ServerHelper extends AsyncTask<String,Void,List<List<String>>>
{
	// For sending the data back to the activity from onPostExecute()
	public AsyncResponse delegate=null; 
	
	protected void onPreExecute() 
	{
	  //display progress dialog if any.
	
	}
	
	
	protected List<List<String>> doInBackground(String... params) 
	{ 
		
		//Establish HTTP Connection, POST data and get the response back
		List<List<String>> chartInput = openHttpConnection(params[0]);
		 
		return chartInput;
	}
	
	
	/*	Method:		Establishes HTTP Connection, 
	 * 				POST data and get the response back
	 * parameter:	String server url
	 * returns :	List<List<String>> HTTP Response data
	 */
	private List<List<String>> openHttpConnection(String aUrl)  {
		 URL url = null;
		 HttpsURLConnection urlConnection = null;
		 
		 String requestBody = null;
		 String responseDataStr = null;
		 
		 List<List<String>> chartData = null;
		 
		 if(aUrl!=null)
		 {
		 	try {
					// Connection setup
			 		url = new URL(aUrl);
				
					urlConnection = (HttpsURLConnection) url.openConnection();
					urlConnection.setInstanceFollowRedirects(false);
					
					// HTTP header and body
					urlConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
					urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					urlConnection.setRequestProperty("Authorization", Constants.AUTORIZATION_HEADER);
					urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
					urlConnection.setRequestMethod("POST");
					if(aUrl.equalsIgnoreCase(Constants.TOTANGO_SERVER_URL_CHART))
					{
						requestBody = URLEncoder.encode("query", "UTF-8")
							 + "=" + URLEncoder.encode(generateJSONStringForChart().toString(), "UTF-8");
					}
					else
					{
						requestBody = URLEncoder.encode("query", "UTF-8")
								 + "=" + URLEncoder.encode(generateJSONStringForList().toString(), "UTF-8");
					}
					
					OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
					writer.write(requestBody);
					writer.flush();
					
					//Get the response code and parse it
					int responseCode = urlConnection.getResponseCode();
					if(responseCode != HttpsURLConnection.HTTP_OK ) {
						Log.e("openHttpConnection", "Error creating requestBody. Response Code = " + responseCode);
					}
					else
					{
						
						InputStream in = new BufferedInputStream(urlConnection.getInputStream());
						byte[] responseData = null;
						int len = 0;
						if(aUrl.equalsIgnoreCase(Constants.TOTANGO_SERVER_URL_CHART))
						{
							responseData = new byte[Constants.MAX_RESPONSE_LENGTH];
							len = in.read(responseData, 0, Constants.MAX_RESPONSE_LENGTH);
						}
						
						    
					    if(aUrl.equalsIgnoreCase(Constants.TOTANGO_SERVER_URL_CHART))
						{
					    	responseDataStr = new String(responseData, 0, len);
					    	chartData = extractJSONFromresponseForChart(responseDataStr);
						}
					    else
					    {
					    	chartData = extractJSONFromresponseForList(urlConnection);
					    }
						
					    in.close();
				}
				
				
		 	} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 	finally {
	             urlConnection.disconnect();
	         }
		 }
		 return chartData;
	}
	
	
 
	/*	Method:		Call the function that converts HTTP response to a jSON string
	 * parameter:	HttpsURLConnection with response data
	 * returns :	List<List<String>> HTTP Response data to be displayed in the list
	 */

	private List<List<String>> extractJSONFromresponseForList(HttpsURLConnection responseDataStr) 
	{
		// TODO Auto-generated method stub
		String line;
		String rData = "";
		
		List<List<String>> arguments =  new ArrayList<List<String>>(200);
		
		BufferedReader br=null;
		try 
			{
				br = new BufferedReader(new InputStreamReader(responseDataStr.getInputStream()));
			} catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		try {
				while ((line = br.readLine()) != null) 
				{
					rData+=line;
				}	
				arguments =  extractValuesFromResponse(rData);
			
				br.close();
			} catch (IOException e) 
			{
			// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		return arguments;
	}
	
	
	/*	Method:		Extract the required fileds from HTTP response
	 * parameter:	String  response data
	 * returns :	List<List<String>> HTTP Response data to be displayed in the list
	 */

	private List<List<String>> extractValuesFromResponse(String rData) {
		// TODO Auto-generated method stub
		JSONObject jsObject = null;
		List<List<String>> finalString = new ArrayList<List<String>>(200);
		List<String> collection;// = new ArrayList<String>();
		
		try {
			
				jsObject = (JSONObject) new JSONTokener(rData).nextValue();
				JSONObject accounts = jsObject.getJSONObject("response").getJSONObject("accounts");
				JSONArray hits = accounts.getJSONArray("hits");
				
				for(int i = 0 ; i < hits.length() ; i++)
				{
					//get the each object under the object 'hits'
					JSONObject p = (JSONObject)hits.get(i);
					
				    //Name
					String name = p.getString("name");
					
					//Time
					String duration;
					long time = p.getLong("last_activity_time");
					
					// Convert the time in milisecond to week / month
					long inDays = (time/(1000*60*60*24))%24;
					long week = inDays/7;
					
					if(week<4)
						 duration = week + " weeks ago improved to good";
					else
						 duration = (week/4)+ " months ago improved to good";
					
					
					//Dollar spent
					String spent;
					JSONArray fields = p.getJSONArray("selected_fields");
					String thousands = fields.get(3).toString();
					
					if(thousands.equalsIgnoreCase("null"))
						spent = "0.0";
					else
						spent = format(Double.parseDouble(thousands));
					   
					
					//Engagment
					//TODO: find out the logic to calculate the engagement
					String engagment = "0/100";
					
					//Active users
					//TODO: Find out the logic to print the active users
					String aUser = "0";
					    
					
					collection = new ArrayList<String>();
					collection.add(name);
					collection.add(duration);
					collection.add(spent);
					collection.add(engagment);
					collection.add(aUser);
					 
					finalString.add(collection);
				}
				        
			} catch (JSONException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		return finalString;
	}

	
	/*	Method:		extracts the required fields from HTTP response 
	 * parameter:	String  response data
	 * returns :	List<List<String>> HTTP Response data to be displayed in the chart
	 */
	private List<List<String>> extractJSONFromresponseForChart(String responseDataStr) throws JSONException {
		// TODO Auto-generated method stub
		
		JSONObject jsObject = null;
		List<List<String>> arguments = new ArrayList<List<String>>(1);
		List<String> collection = new ArrayList<String>();
		
		jsObject = (JSONObject) new JSONTokener(responseDataStr).nextValue();
		
		String redTotal = jsObject.getJSONObject("hits").getJSONObject("health").getJSONObject("red").getString("total_hits");
		String redContract = jsObject.getJSONObject("hits").getJSONObject("health").getJSONObject("red").getJSONObject("contract_value").getString("sum");
	
		String greenTotal = jsObject.getJSONObject("hits").getJSONObject("health").getJSONObject("green").getString("total_hits");
		String greenContract = jsObject.getJSONObject("hits").getJSONObject("health").getJSONObject("green").getJSONObject("contract_value").getString("sum");
	
		String yellowTotal = jsObject.getJSONObject("hits").getJSONObject("health").getJSONObject("yellow").getString("total_hits");
		String yellowContract = jsObject.getJSONObject("hits").getJSONObject("health").getJSONObject("yellow").getJSONObject("contract_value").getString("sum");
	       
	    collection.add(redTotal);
	    collection.add(redContract);
	    collection.add(greenTotal);
	    collection.add(greenContract);
	    collection.add(yellowTotal);
	    collection.add(yellowContract);
		        
	    arguments.add(0, collection);
	    
	    return arguments;
	}
	
	/*	Method:		Generate jSon string for sending it
	 * 				in a request body of HTTP POST of a chart window
	 * parameter:	
	 * returns :	JSONStringer
	 */

	private JSONStringer generateJSONStringForChart() 
	{
			// TODO Auto-generated method stub
		JSONStringer jsonStr = null;
		try {
			 jsonStr  = new JSONStringer().object()
			        	.key("terms").array().object()
			.key("type").value("totango_user_scope")
			.key("is_one_of").array().value("mobile+testme@totango.com")
				.endArray()
			.endObject().endArray()
			
			.key("group_fields").array().object()
			.key("type").value("health")
				.endObject().endArray()
			.endObject();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}
	
	/*	Method:		Generate jSon string for sending it
	 * 				in a request body of HTTP POST of a list window
	 * parameter:	
	 * returns :	JSONStringer
	 */
	
	private JSONStringer generateJSONStringForList() {
		// TODO Auto-generated method stub
		JSONStringer jsonStr = null;
		try {
			jsonStr  = new JSONStringer().object()
                .key("offset").value(0)
                .key("count").value(1000)
                .key("scope").value("all")
                .key("terms").array().object()
                    .key("type").value("string")
                    .key("term").value("health")
                        .key("in_list").array().value("green")
                        .value("red").value("yellow")
                        .endArray()
                .endObject()    
                .object()
                    .key("type").value("totango_user_scope")
                    .key("is_one_of").array().value("mobile+testme@totango.com").endArray()
                .endObject().endArray()

                .key("fields").array()
                    .object()
                        .key("type").value("health_trend")
                        .key("field_display_name").value("Health last change")
                        .key("desc").value("true")
                    .endObject()
                    .object()
                        .key("type").value("health_reason")
                    .endObject()
                    .object()
                        .key("type").value("date_attribute")
                        .key("attribute").value("Contract Renewal Date")
                        .key("field_display_name").value("Contract Renewal Date")
                    .endObject()
                    .object()
                        .key("type").value("number")
                        .key("term").value("contract_value")
                        .key("field_display_name").value("Value")
                    .endObject()
                    .object()
                        .key("type").value("string")
                        .key("term").value("status")
                        .key("field_display_name").value("Status")
                    .endObject()
                    .object()
                        .key("type").value("number")
                        .key("term").value("score")
                        .key("field_display_name").value("Engagement")
                    .endObject()
                    .object()
                        .key("type").value("on_attention")
                        .key("user_id").value("mobile+testme@totango.com")
                    .endObject()
                    .object()
                        .key("type").value("named_aggregation")
                        .key("aggregation").value("unique_users")
                        .key("duration").value(14)
                        .key("field_display_name").value("Active users (14d)")
                    .endObject()
                    .object()
                        .key("type").value("number_metric_change")
                        .key("metric").value("unique_users")
                        .key("duration").value(14)
                        .key("relative_to").value(14)
                        .key("field_display_name").value("Active users % change (14d)")
                    .endObject()
                    .object()
                        .key("type").value("last_touch")
                    .endObject()
                .endArray()
                .key("date_term").object()
                    .key("type").value("date")
                    .key("term").value("date")
                    .key("eq").value(0)
                .endObject().endObject();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return jsonStr;
	}
	

	/*	Method:		Format the dollar spent into kmbt format
	 * parameter:	Double dollar spent
	 * 				
	 * returns :	String
	 */

	private static String format(double number) 
	{
		int power; 
	    String suffix = " kmbt";
	    String formattedNumber = "";
	
	    NumberFormat formatter = new DecimalFormat("#,###.#");
	    power = (int)StrictMath.log10(number);
	    number = number/(Math.pow(10,(power/3)*3));
	    formattedNumber=formatter.format(number);
	    formattedNumber = formattedNumber + suffix.charAt(power/3);
	    return formattedNumber.length()>4 ?  formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber; 
	}
	

	/*	Method:		Called when the task ends and the result is
	 * 				sent to the respective activity through interface
	 * parameter:	List<List<String> result data
	 * 				
	 * returns :	void
	 */
	protected void onPostExecute(List<List<String>> result) 
	{
	   // Update the ui/ activity
		if(delegate !=null || (!result.isEmpty()))
				delegate.processFinish(result);
	  
	 }
}
