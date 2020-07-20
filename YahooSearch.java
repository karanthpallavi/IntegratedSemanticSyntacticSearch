/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SearchProcessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONArray;   // JSON library from http://www.json.org/java/
import org.json.JSONObject;
import java.util.concurrent.*;
import java.io.*;

/**
 *
 * @author Pallavi
 */
public class YahooSearch implements Callable
{
    // Yahoo API key
 //private final String API_KEY = "Your Key Here";
 private final String API_KEY ="T7Nw_lLV34E2r_Llz_nGBJBLX9Zeskpc4r2FbwEekmW6ZyKw.LFzzTrGCAK8INJ7E1XyE8Q";
 String searchQuery ="";
 int numRes;
 PrintWriter out;


 public YahooSearch(String Query, int numResults,PrintWriter outt) {
     searchQuery = Query;
     numRes = numResults;
  //makeQuery(searchQuery,numResults);
  //makeQuery("url:http://frankmccown.blogspot.com/");
  //makeQuery("site:frankmccown.blogspot.com");
 }

 private JSONObject makeQuery() {

  //System.out.println("\nQuerying for " + searchQuery);
  JSONObject json = null;
  JSONObject retJsonObj= null;

  try
  {
   // Convert spaces to +, etc. to make a valid URL
   searchQuery = URLEncoder.encode(searchQuery, "UTF-8");

   // Give me back 10 results in JSON format
   URL url = new URL("http://boss.yahooapis.com/ysearch/web/v1/" + searchQuery +
     "?appid=" + API_KEY + "&count"+numRes+"&format=json&style=raw&view=keyterms&filter=-porn&filter=-hate");
   URLConnection connection = url.openConnection();

   String line;
   StringBuilder builder = new StringBuilder();
   BufferedReader reader = new BufferedReader(
     new InputStreamReader(connection.getInputStream()));
   while((line = reader.readLine()) != null) {
    builder.append(line);
   }

   String response = builder.toString();

   json = new JSONObject(response);
   retJsonObj = json.getJSONObject("ysearchresponse");

   //out.println("\nResults:");
   /*out.println("Total results = " +
           json.getJSONObject("ysearchresponse")
           .getString("deephits"));


           out.println();

           JSONArray ja = json.getJSONObject("ysearchresponse")
            .getJSONArray("resultset_web");

           out.println("\nResults:");
           for (int i = 0; i < ja.length(); i++) {
             out.print((i+1) + ". ");
             JSONObject j = ja.getJSONObject(i);
             out.println(j.getString("title"));
             out.println(j.getString("url"));
             out.println(j.getString("keyterms"));*/
             //System.out.println(j.getString("score"));
            }
  catch (Exception e) {
   System.err.println("Something went wrong...");
   e.printStackTrace();
  }
  
  return retJsonObj;
 }

 public JSONObject call()
 {
     return makeQuery();
 }

  /*public static void main(String args[]) {
      if(args.length<1)
      {
          System.err.println("Error - no search query specified");
      }
      else
      {
          String searchQuery = args[0];
          for(int i = 1; i < args.length; i++)
          {
            searchQuery = searchQuery + " " + args[i];
          }

         YahooSearch newSearch = new YahooSearch(searchQuery,20);
      }

 }*/

}