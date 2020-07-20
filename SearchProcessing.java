/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SearchProcessor;

/**
 *
 * @author Pallavi Karanth
 */
import java.io.*;

//import java.text.*;

import java.util.*;
import org.json.JSONArray;   // JSON library from http://www.json.org/java/
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.servlet.*;
import org.apache.commons.lang.StringUtils.*;

import javax.servlet.http.*;

public class SearchProcessing extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void destroy() {

	}

	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
        {
                response.setContentType("text/html");
		PrintWriter out = response.getWriter();

                //out.println("In Search Processing");

                int numOfQueryWords = Integer.parseInt(request.getParameter("NumOfQueryWords").trim());
                //out.println("Num of query words is "+numOfQueryWords);

                String finalDisambiguatedQueryParam = request.getParameter("DisAmbQuery");
                //out.println("Final disambiguated query word as parameter in Search Processing is "+finalDisambiguatedQueryParam);
                // Need to remove brackets [] and commas and include only non-empty strings for individual query words
                int idxOpenBracket = finalDisambiguatedQueryParam.indexOf("[");
                int idxCloseBracket = finalDisambiguatedQueryParam.indexOf("]");
                String finalDisambiguatedQueryWithoutBracket = finalDisambiguatedQueryParam.substring(idxOpenBracket+1, idxCloseBracket);
                //out.println("After removing brackets, query is "+finalDisambiguatedQueryWithoutBracket);
                String[] finalDisambiguatedQueryWithoutBracketWithoutComma = finalDisambiguatedQueryWithoutBracket.split(",");
                /*int numCommas = org.apache.commons.lang.StringUtils.countMatches(finalDisambiguatedQueryWithoutBracket, ",");
                String[] finalDisambiguatedQueryWithoutBracketWithoutComma = new String[numCommas];
                int[] IndicesComma = new int[numCommas];
                for(int i = 0 ; i < numCommas; i++)
                {
                    IndicesComma[i] = ArrayUtils.
                }*/
                //String[] finalDisambiguatedIndividualQueryWords = new String[numOfQueryWords];
                List<String> finalDisambiguatedIndividualQueryWords = new ArrayList<String>();
                String finalQuery = "";
                int lenOfIndivQueryWordsFromServlet = finalDisambiguatedQueryWithoutBracketWithoutComma.length;
                //out.println("Len of query words from servlet is "+lenOfIndivQueryWordsFromServlet);
                //out.println("Individual Query Words after Word Sense Disambiguation - Ready for Search Processing");
                for(int i = 0; i < lenOfIndivQueryWordsFromServlet; i++)
                {
                    //out.println("Without bracket without comma query word is "+finalDisambiguatedQueryWithoutBracketWithoutComma[i]);

                    if(!(finalDisambiguatedQueryWithoutBracketWithoutComma[i].equalsIgnoreCase("")))
                    {
                        String indivQueryWordFinal = finalDisambiguatedQueryWithoutBracketWithoutComma[i].trim();
                        //out.println("indivQueryWordFinal is "+indivQueryWordFinal);
                        //out.println(indivQueryWordFinal);
                        finalQuery=finalQuery+" "+indivQueryWordFinal;
                        finalDisambiguatedIndividualQueryWords.add(indivQueryWordFinal);
                    }
                }

                //out.println("Individual Query Words after Word Sense Disambiguation - Ready for Search Processing");
                /*for(int i = 0; i < numOfQueryWords; i++)
                {
                    out.println(finalDisambiguatedIndividualQueryWords.get(0));
                    out.println(finalDisambiguatedIndividualQueryWords.get(1));
                }*/

                String uName = request.getParameter("UserName");
                String inQuery = request.getParameter("Query");
                String userName = uName.trim();
                String inputQuery = inQuery.trim();
                /*out.println("User name is "+userName);
                out.println("Input Query is "+inputQuery);
                out.println("Final Query is "+finalQuery);*/
                
                String[] semRoles = {"Application","Use","Definition","Elaboration","Description","Illustration","Proof","Counter-example","Analogy","Generalization","Specialization","Generalisation","Specialisation","Instantiation","Translation","Transliteration","Exercise","Problem","Problem Solution","Images","Audio","Video","applications","application","use","definition","elaboration","description","illustration","proof","counterexample","counter-example","analogy","generalization","specialization","generalisation","specialisation","instantiation","translation","transliteration","exercise","problem","problem solution","images","audio","video"};

                int indexStrs = org.apache.commons.lang.StringUtils.indexOfAny(finalQuery, semRoles);

                if(indexStrs>0)
                {

                FutureTask task = new FutureTask(new YahooSearch(finalQuery,20,out));
                ExecutorService es = Executors.newSingleThreadExecutor();
                es.submit(task);
                try
                {
                    JSONObject objResultsYahoo = (JSONObject)task.get();
                    out.println("<html><head><title>Search Results</title></head>");
                    out.println("<body bgcolor=\"Cornsilk\">");
                    out.println("<form>");
                    out.println("<h3>Search Results for Query "+inputQuery+": </h3><br/>");
                    out.println("Total results = " +objResultsYahoo.getString("deephits"));
                    out.println("<br/><br/>");
                    //out.println("Got results");
                    //out.println("Total results = " +
           //objResultsYahoo.getString("deephits"));


           //out.println();

           JSONArray ja = objResultsYahoo.getJSONArray("resultset_web");

           //out.println("\nResults:");
           for (int i = 0; i < ja.length(); i++) {
             out.print((i+1) + ". ");
             JSONObject j = ja.getJSONObject(i);
             //out.println("Title: "+j.getString("title"));
             out.println(j.getString("title"));
             out.println("<br/>");
             //out.println("URL :" +j.getString("url"));
             out.println(j.getString("url"));
             out.println("<br/><br/>");
             //out.println(j.getString("url"));
             //out.println(j.getString("keyterms"));
             //System.out.println(j.getString("score"));
            }
            out.println("</form></body></html>");


  }
                catch (Exception e) {
   System.err.println("Something went wrong...");
   e.printStackTrace();
  }
            } // No semroles added
            else
            {
                    String queries[] = new String[3];
                    Collection<FutureTask> tasks = new ArrayList<FutureTask>();
                    queries[0] = finalQuery+" Definition";
                    queries[1] = finalQuery+" Description";
                    queries[2] = finalQuery+" Use";
                    ExecutorService es = Executors.newFixedThreadPool(4);

                 for(int i = 0; i < 3; i++ )
                 {

                    //finalQuery = finalQuery+queries[i];
                    FutureTask task = new FutureTask(new YahooSearch(queries[i],20,out));
                    es.submit(task);
                                 try
                {
                    JSONObject objResultsYahoo = (JSONObject)task.get();
                    out.println("<html><head><title>Search Results</title></head>");
                    out.println("<body bgcolor=\"Cornsilk\">");
                    out.println("<form>");
                    out.println("<h3>Search Results for Query "+queries[i]+": </h3><br/>");
                    out.println("Total results = " +objResultsYahoo.getString("deephits"));
                    out.println("<br/><br/>");
                    //out.println("Got results");
                    //out.println("Total results = " +
           //objResultsYahoo.getString("deephits"));


           //out.println();

           JSONArray ja = objResultsYahoo.getJSONArray("resultset_web");

           //out.println("\nResults:");
           for (int iter = 0; iter < ja.length(); iter++) {
             //out.print((iter+1) + ". ");
             JSONObject j = ja.getJSONObject(iter);
             //out.println("Title: "+j.getString("title"));
             out.println(j.getString("title"));
             out.println("<br/>");
             //out.println("URL :" +j.getString("url"));
             out.println(j.getString("url"));
             out.println("<br/><br/>");
             //out.println(j.getString("title"));
             //out.println(j.getString("url"));
             //out.println(j.getString("keyterms"));
             //System.out.println(j.getString("score"));
            }
            out.println("</form></body></html>"); 


  }
                catch (Exception e) {
   System.err.println("Something went wrong...");
   e.printStackTrace();
  }
                    //tasks.add(task);

                }
                    
            }
    }


                /*if(indexStrs<0)
                {
                    ScheduledSearch searchObj = new ScheduledSearch(userName,inputQuery,finalQuery,out,20,0,null);
                }
                else
                {
                    String queries[] = new String[3];
                    queries[0] = finalQuery+"Definition";
                    queries[1] = finalQuery+"Description";
                    queries[2] = finalQuery+"Use";
                    ScheduledSearch searchObj = new ScheduledSearch(userName,inputQuery,finalQuery,out,20,3,queries);
                }*/
               
                //SearchTaskQueue searchObj = new SearchTaskQueue(userName,inputQuery,finalQuery,out,20);


	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		doPost(request,response);
	}
}