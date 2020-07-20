/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package WordSenseDisambiguator;

import java.io.*;

//import java.text.*;

import java.util.*;

import javax.servlet.*;

import javax.servlet.http.*;
import UserSelectQueryWord.*;

public class WordSenseDisambiguation extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void destroy() {

	}

	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
                response.setContentType("text/html");
		PrintWriter out = response.getWriter();
                //out.println("Word Sense Disambiguator");

                // Get all parameters
                String strStem = request.getParameter("StemmedQuery");
                int lenStr = (strStem.length());


                //out.println("Length is "+lenStr);
                
                //out.println("Stemmed query as param is "+strStem);
                String inputQuery = request.getParameter("Query");
                //out.println("Input Query is "+inputQuery);
                String interQuery = request.getParameter("InterimQuery");
                //out.println("Intermediate Query after stop word removal is "+interQuery);
                String userName = request.getParameter("UserName");
                //out.println("User Name is "+userName);

                int numOfCommasInStemmedQuery = org.apache.commons.lang.StringUtils.countMatches(strStem, ",");
                int numOfIndivQueryWords = numOfCommasInStemmedQuery + 1;
                int lenOfStemQuery = strStem.split("\\,",-1).length;
                
                //out.println("Len is "+lenOfStemQuery);
                String n = strStem.substring(1, lenStr-3);
                //out.println("New is "+n);
                String[] a_stStemmedQuery = new String[lenOfStemQuery];
                a_stStemmedQuery = n.split(",");

                String[] a_stStemQuery = new String[lenOfStemQuery];
                int stemQueryLen = a_stStemmedQuery.length;
                for(int i= 0; i < stemQueryLen;i++)
                {
                    //out.println("Before : " +a_stStemmedQuery[i]);
                    a_stStemQuery[i] = a_stStemmedQuery[i].trim();
                    //out.println("After : " +a_stStemQuery[i]);
                }

                List<String> disambiguatedQueryList = new ArrayList<String>();
                ArrayList allAmbigWords = new ArrayList();
                ArrayList<String> ambigWords = new ArrayList<String>();
                String disambiguatedQuery;
                Vector a_idxUserSelection = new Vector();
                //boolean a_bStillAmbiguous = false;

                /*for(int i=0;i<lenOfStemQuery;i++)
                {
                    out.println("Each of query word [" + i + "] is " + a_stStemmedQuery[i]);
                }*/

                boolean a_bIsDisambiguated = false;
                // Limitation - number of words which can be ambiguous is limited to 20
                String[] ambiguousQueryWordsArray = new String[20];
                int iter = 0;
                //String ambiguousQueryWord ="";
                for(int a_unIter = 0; a_unIter < lenOfStemQuery; a_unIter++)
                {
                    //String inputStemmedWord = a_stStemmedQuery[a_unIter];
                    String inputStemmedWord = a_stStemQuery[a_unIter];
                    PrintWriter outt = response.getWriter();
                    // Calling WSD to disambiguate each of the query word
                    WSD disambiguator = new WSD(inputStemmedWord,userName,outt);
                    String disWord = disambiguator.getDisambiguatedWord();
                    //out.println("Disambiguated Query word is "+disWord);
                    //out.println("Length of disambiguated query word is "+disWord.length());
                    disambiguatedQueryList.add(disWord);
                    a_bIsDisambiguated = disambiguator.getIsDisambiguated();
                    //if(disWord.isEmpty())
                    if(!a_bIsDisambiguated)
                    {
                        ambiguousQueryWordsArray[iter] = inputStemmedWord;
                        iter++;
                        //a_bStillAmbiguous = true;
                        a_idxUserSelection.add(a_unIter);
                        ambigWords = disambiguator.getAmbiguousWords();
                        //out.println("Index where word is ambiguous is "+ a_unIter);
                        allAmbigWords.add(ambigWords);
                        //out.println("Ambiguous words are ");
                        /*for(int i=0;i<ambigWords.size();i++)
                        {
                            out.println(ambigWords.get(i));
                        }*/
                    }
                    else
                    {
                        //out.println("Query term ["+a_unIter+"] after disambiguation is "+disWord);
                    }


                } // End of for lenOfStemQuery

                disambiguatedQuery = disambiguatedQueryList.toString();
                //out.println("Query after disambiguation is "+disambiguatedQuery);

                // If vector is empty, then all query words are disambiguated
                boolean a_buserSelectReqd = !(a_idxUserSelection.isEmpty());

                //out.println(a_buserSelectReqd);
                int NumAmbigWords = a_idxUserSelection.size();


                // Go to UserChoiceServlet or Synonym Expansion servlet depending on
                // whether the query contains ambiguous words which could not be
                // disambiguated by WSD

                if(!a_idxUserSelection.isEmpty())
                {
                    // Go to next servlet UserSelectQueryWord as query still contains ambiguity
                    // User dialog required
                    //out.println("Going to UserSelectQueryWord Servlet to resolve ambiguity");

                    out.println("<html><head><title>User Preference for Query Word</title>");
                    out.println("<script type=\"text/javascript\">function fnSubmit(){");
                    out.println("document.UserSelect.submit();}</script>");
                    out.println("</head>");
                    out.println("<body><form name=\"UserSelect\" id=\"UserSelect\" method=\"get\" action=\"UserSelectQueryWord\"/>");
                    out.println("<input type=\"hidden\" name=\"Query\" id=\"Query\" value=\""+inputQuery);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"InterimQuery\" id=\"InterimQuery\" value=\""+interQuery);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"StemmedQuery\" id=\"StemmedQuery\" value=\""+strStem);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"UserName\" id=\"UserName\" value=\""+userName);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"DisAmbQuery\" id=\"DisAmbQuery\" value=\""+disambiguatedQueryList);
                    out.println("\"/>");
                    //for(int i = 0; i < a_idxUserSelection.size(); i++)
                    //{
                        out.println("<input type=\"hidden\" name=\"AmbWordsList\" id=\"AmbWordsList\" value=\""+allAmbigWords);
                        out.println("\"/>");
                    //}
                    out.println("<input type=\"hidden\" name=\"idxAmbig\" id=\"idxAmbig\" value=\""+a_idxUserSelection);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"NumAmbigWords\" id=\"NumAmbigWords\" value=\""+NumAmbigWords);
                    out.println("\"/>");
                    for(int i = 0; i < NumAmbigWords; i++)
                    {
                        out.println("<input type=\"hidden\" name=\"AmbigWord"+i+"\" id=\"AmbigWord"+i+"\" value=\""+ambiguousQueryWordsArray[i]);
                        out.println("\"/>");
                    }
                    out.println("</form>");
                    out.println("<script type=\"text/javascript\">fnSubmit();</script>");
                    out.println("</body></html>");
                }
                else
                {
                    // Go to servlet Synonym Expansion directly as no ambiguity exists --- Future extension
                    //out.println("Going to Synonym Expansion Servlet");
                    
                    // Go to SearchProcessing servlet to continue with search
                    out.println("<html><head><title>Search Processing</title>");
                    out.println("<script type=\"text/javascript\">function fnSubmit(){");
                    out.println("document.UserSelect.submit();}</script>");
                    out.println("</head>");
                    out.println("<body><form name=\"UserSelect\" id=\"UserSelect\" method=\"get\" action=\"SearchProcessing\"/>");
                    out.println("<input type=\"hidden\" name=\"Query\" id=\"Query\" value=\""+inputQuery);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"InterimQuery\" id=\"InterimQuery\" value=\""+interQuery);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"StemmedQuery\" id=\"StemmedQuery\" value=\""+strStem);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"UserName\" id=\"UserName\" value=\""+userName);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"DisAmbQuery\" id=\"DisAmbQuery\" value=\""+disambiguatedQueryList);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"NumOfQueryWords\" id=\"NumOfQueryWords\" value=\""+numOfIndivQueryWords);
                    out.println("\"/>");
                    //for(int i = 0; i < a_idxUserSelection.size(); i++)
                    //{
                        //out.println("<input type=\"hidden\" name=\"AmbWordsList\" id=\"AmbWordsList\" value=\""+allAmbigWords);
                        //out.println("\"/>");
                    //}
                    //out.println("<input type=\"hidden\" name=\"idxAmbig\" id=\"idxAmbig\" value=\""+a_idxUserSelection);
                    //out.println("\"/>");
                    out.println("</form>");
                    out.println("<script type=\"text/javascript\">fnSubmit();</script>");
                    out.println("</body></html>");
                }

        }

	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
		doPost(request,response);
	}

        public static String removeCharAt(String s, int pos)
        {
            StringBuffer buf = new StringBuffer( s.length() - 1 );
            buf.append( s.substring(0,pos) ).append( s.substring(pos+1) );
            return buf.toString();
        }

        public static String removeChar(String s, char c)
        {

   String r = "";

   for (int i = 0; i < s.length(); i ++) {
      if (s.charAt(i) != c) r += s.charAt(i);
   }

   return r;
}


}