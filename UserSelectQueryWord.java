package UserSelectQueryWord;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Pallavi Karanth
 */


import java.io.*;

//import java.text.*;

import java.util.*;

import javax.servlet.*;
import org.apache.commons.lang.StringUtils.*;
import javax.servlet.http.*;
import WordSenseDisambiguator.*;
//import PreferredQueryWord.*;


public class UserSelectQueryWord extends HttpServlet {

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void destroy() {

	}

	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
        {
                response.setContentType("text/html");
		PrintWriter out = response.getWriter();
                
                //out.println("In UserSelectQueryWord");

                // Get all parameters
                String strStem = request.getParameter("StemmedQuery");
                //out.println("Stemmed query as param is "+strStem);
                String inputQuery = request.getParameter("Query");
                //out.println("Input Query is "+inputQuery);
                String interQuery = request.getParameter("InterimQuery");
                //out.println("Intermediate Query after stop word removal is "+interQuery);
                String userName = request.getParameter("UserName");
                //out.println("User Name is "+userName);
                String disambQuery = request.getParameter("DisAmbQuery");
                //out.println("Disambiguated Query is "+disambQuery);
                int lengthDisambQuery = disambQuery.length();
                //out.println("Length of query is " +lengthDisambQuery);

                String disambQuerySubStr = disambQuery.substring(1, lengthDisambQuery-1);
                String disambQueryProper = disambQuerySubStr.trim();
                //out.println("Proper disambiguated query is "+disambQueryProper);
                int lenOfDisAmbQueryWords = disambQuery.split("\\,",-1).length;
                String[] eachDisambQueryWord = new String[lenOfDisAmbQueryWords];
                List<String> listDisambQueryWords = new ArrayList<String>();
                eachDisambQueryWord = disambQueryProper.split(",");
                /*for(int i = 0; i < lenOfDisAmbQueryWords; i++)
                {
                    if(eachDisambQueryWord[i].isEmpty())
                    {
                        listDisambQueryWords.add("");
                    }
                    else
                    {
                        listDisambQueryWords.add(eachDisambQueryWord[i]);
                    }
                    //out.println("Each query word is "+eachDisambQueryWord[i]);
                    out.println("Each query word is "+listDisambQueryWords.get(i));
                }*/

                String ambigWordsList = request.getParameter("AmbWordsList");
                //out.println("Ambiguous words list is "+ambigWordsList);
                int lenAmbigWordsList = ambigWordsList.length();
                //out.println("Length of ambig words list is "+lenAmbigWordsList);

                // Removing [] brackets
                String ambigWordsWithComma = ambigWordsList.substring(2, lenAmbigWordsList-2);
                String ambigWordsWithCommaProper = ambigWordsWithComma.trim();
                //out.println("List of ambig words with comma proper is "+ambigWordsWithCommaProper);

                // need to remove extra [] brackets in case there are many lists within the list
                // many query words are ambiguous
                //ambigWordsWithCommaProper


                /*String sample = "apple, mango], [banana, orange, arjuna], [orange,peepal";
                List newSampleList = new ArrayList();

                java.util.StringTokenizer tokenisedsampleWords = new java.util.StringTokenizer(sample,"]");
                int lenNumTokens = tokenisedsampleWords.countTokens();
                String[] tokenArr = new String[lenNumTokens];
                int cter = 0;
                while(tokenisedsampleWords.hasMoreElements())
                {
                    String token = tokenisedsampleWords.nextToken();
                    out.println("Tokens are : "+token);
                    tokenArr[cter] = token;
                    cter++;
                }


                for(int i = 0; i < lenNumTokens; i++)
                {
                     int sampleArrLen = tokenArr[i].split("\\,",-1).length;
                     String[] newArrayAmbigWords = new String[sampleArrLen];
                     newArrayAmbigWords = tokenArr[i].split(",");
                     Vector ambigWordsCleaned = new Vector();


                     out.println("Array " + i + " elements: ");
                     for(int j = 0; j < sampleArrLen;j++)
                     {
                         if(newArrayAmbigWords[j].isEmpty())
                         {
                             // Do nothing
                         }
                        else
                         {
                         //out.println("Each sample: "+samplearray[i]);
                    int idxBracketOpen = newArrayAmbigWords[j].indexOf("[");
                    //out.println("Index is "+idxBracketOpen);
                    if(idxBracketOpen > 0)
                    {
                        String newsample = newArrayAmbigWords[j].substring(idxBracketOpen+1, newArrayAmbigWords[j].length());
                        //out.println("New sample is "+newsample);
                        newArrayAmbigWords[j] = newsample;
                    }
                    int idxBracketClose = newArrayAmbigWords[j].indexOf("]");
                    //out.println("Index is "+idxBracketClose);
                    if(idxBracketClose > 0)
                    {
                        String newsample = newArrayAmbigWords[j].substring(0, newArrayAmbigWords[j].length()-1);
                        //out.println("New sample is "+newsample);
                        newArrayAmbigWords[j] = newsample;
                    }

                        out.println(newArrayAmbigWords[j]);
                        ambigWordsCleaned.add(newArrayAmbigWords[j]);
                     } // end of else
                    } // end of for j
                    newSampleList.add(ambigWordsCleaned);

                }*/

                int numAmbigWords = Integer.parseInt(request.getParameter("NumAmbigWords").trim());

                String idxVector = request.getParameter("idxAmbig").trim();
                //out.println("Idx Vector is "+idxVector);
                int NumOfCommas = org.apache.commons.lang.StringUtils.countMatches(idxVector,",");
                int numOfIndices = NumOfCommas + 1;
                int Idx = 0;
                boolean oneIndx = false;
                boolean moreIndices = false;
                int[] idxsArray = new int[numOfIndices];
                String idxVectorWithCommaProper = "";

                if(numOfIndices==1)
                {
                    oneIndx = true;
                    moreIndices = false;
                    int LenIdxVector = idxVector.length();
                    Idx = Integer.parseInt(idxVector.substring(1, LenIdxVector-1));
                    idxVectorWithCommaProper = idxVector.trim();
                    // Not adding userOption here to list - not working as not inserting at correct index
                    //finalDisambiguatedQuery.add(Idx,userOptions[0]);
                }
                else if(numOfIndices==0)
                {
                    // No ambiguous words in query .. we should not have come here .. some problem..
                    //out.println("No ambiguous words in query .. we should not have come here .. some problem..");
                    idxVectorWithCommaProper = idxVector.trim();
                }
                else if(numOfIndices>1)
                {
                    oneIndx = false;
                    moreIndices = true;

                int LenIdxVector = idxVector.length();
                //out.println("Length of idx vector is "+LenIdxVector);

                String idxVectorWithComma = idxVector.substring(1, LenIdxVector-1);
                idxVectorWithCommaProper = idxVectorWithComma.trim();
                //out.println("Proper Idx Vector with comma is "+idxVectorWithCommaProper);

                String[] idxArrVector = idxVectorWithCommaProper.split(",");
                //int[] idxsArray = new int[idxArrVector.length];
                for(int i = 0; i < idxArrVector.length;i++)
                {
                    idxsArray[i] = Integer.parseInt(idxArrVector[i].toString().trim());
                    //out.println("Each index element is "+idxArrVector[i]);
                    //out.println("Integer index element is "+idxsArray[i]);
                }
                }


                //List newSampleListAmbigWords = new ArrayList();
                String[][] newSampleListAmbigWords = new String[numAmbigWords][];
                java.util.StringTokenizer tokenisedsampleWordsAmbig = new java.util.StringTokenizer(ambigWordsWithCommaProper,"]");
                int lenNumTokensAmbig = tokenisedsampleWordsAmbig.countTokens();
                String[] tokenArrAmbig = new String[lenNumTokensAmbig];
                int counter = 0;
                while(tokenisedsampleWordsAmbig.hasMoreElements())
                {
                    String token = tokenisedsampleWordsAmbig.nextToken();
                    //out.println("Tokens are : "+token);
                    tokenArrAmbig[counter] = token;
                    counter++;
                }

                Vector ambigWordsLen = new Vector();
                for(int i = 0; i < lenNumTokensAmbig; i++)
                {
                     int sampleArrLen = tokenArrAmbig[i].split("\\,",-1).length;
                     String[] newArrayAmbigWords = new String[sampleArrLen];
                     newArrayAmbigWords = tokenArrAmbig[i].split(",");
                     //Vector ambigWordsCleaned = new Vector();

                     // Limitation - number of ambiguous words for a word - max - 20
                     String[] ambigWordsCleaned = new String[20];
                     int iterAmbigWords = 0;

                     //out.println("Array " + i + " elements: ");
                     for(int j = 0; j < sampleArrLen;j++)
                     {
                         if(newArrayAmbigWords[j].isEmpty())
                         {
                             // Do nothing
                         }
                        else
                         {
                         //out.println("Each sample: "+samplearray[i]);
                    int idxBracketOpen = newArrayAmbigWords[j].indexOf("[");
                    //out.println("Index is "+idxBracketOpen);
                    if(idxBracketOpen > 0)
                    {
                        String newsample = newArrayAmbigWords[j].substring(idxBracketOpen+1, newArrayAmbigWords[j].length());
                        //out.println("New sample is "+newsample);
                        newArrayAmbigWords[j] = newsample;
                    }
                    int idxBracketClose = newArrayAmbigWords[j].indexOf("]");
                    //out.println("Index is "+idxBracketClose);
                    if(idxBracketClose > 0)
                    {
                        String newsample = newArrayAmbigWords[j].substring(0, newArrayAmbigWords[j].length()-1);
                        //out.println("New sample is "+newsample);
                        newArrayAmbigWords[j] = newsample;
                    }

                        //out.println(newArrayAmbigWords[j]);
                        ambigWordsCleaned[iterAmbigWords] = newArrayAmbigWords[j];
                        iterAmbigWords++;
                        //ambigWordsCleaned.add(newArrayAmbigWords[j]);
                     } // end of else
                    } // end of for j
                    //newSampleListAmbigWords.add(ambigWordsCleaned);
                    newSampleListAmbigWords[i] = ambigWordsCleaned;
                    ambigWordsLen.add(iterAmbigWords);
                }

                //int numIndices = idxArray.length;
                int numIndices = numAmbigWords;

                // Read each of the ambiguous query words into array
                // Limitation - number of words which can be ambiguous is limited to 20
                String[] ambiguousQueryWordsArray = new String[20];
                for(int i = 0; i < numAmbigWords; i++)
                {
                    String ambigquerywordAsParam = request.getParameter("AmbigWord"+i).trim();
                    ambiguousQueryWordsArray[i] = ambigquerywordAsParam;
                }


                // HTML to take select user input
                out.println("<html><head><title>User Select Query Word for resolving ambiguity</title>");
                out.println("<script type = \"text/javascript\">function fnSubmit(){document.userOption.submit();}");
                out.println("</script>");
                out.println("</head>");
                out.println("<body bgcolor=\"cornsilk\">");
                out.println("<form name=\"userOption\" id=\"userOption\" action=\"GetUserOption\">");
                out.println("<h2>Please resolve ambiguity in the following query words</h2>");
                for(int i = 0; i < numIndices; i++)
                {
                    out.println("<h3>Please choose one of the words as Query word for ambiguous term "+ambiguousQueryWordsArray[i]+"</h3><br/>");
                    out.println("<select name=\"AmbiguousWords"+i+"\" id=\"AmbiguousWords"+i+"\">");
                    String[] asStrList = newSampleListAmbigWords[i];
                    //out.println("First Ambiguous Word List: ");

                    int lenOfAmbgWords = Integer.parseInt(ambigWordsLen.get(i).toString());
                    //out.println("Length of list is "+lenOfAmbgWords);
                    for(int j = 0; j < lenOfAmbgWords; j++)
                    {
                        String a_strOption = asStrList[j];
                        //if(!asStrList[j].matches("null"))
                        //{
                            //out.println(asStrList[j]);
                            out.println("<option>"+a_strOption+"</option>");
                        //}
                    }
                    out.println("</select>");

                }
                out.println("<input type=\"hidden\" name=\"Query\" id=\"Query\" value=\""+inputQuery);
                out.println("\"/>");
                out.println("<input type=\"hidden\" name=\"InterimQuery\" id=\"InterimQuery\" value=\""+interQuery);
                out.println("\"/>");
                out.println("<input type=\"hidden\" name=\"StemmedQuery\" id=\"StemmedQuery\" value=\""+strStem);
                out.println("\"/>");
                out.println("<input type=\"hidden\" name=\"UserName\" id=\"UserName\" value=\""+userName);
                out.println("\"/>");
                out.println("<input type=\"hidden\" name=\"DisAmbQuery\" id=\"DisAmbQuery\" value=\""+disambQueryProper);
                out.println("\"/>");
                //out.println("<input type=\"text\" name=\"AmbWordsList\" id=\"AmbWordsList\" value=\""+allAmbigWords);
                //out.println("\"/>");
                out.println("<input type=\"hidden\" name=\"idxAmbig\" id=\"idxAmbig\" value=\""+idxVectorWithCommaProper);
                out.println("\"/>");
                out.println("<input type=\"hidden\" name=\"numIndices\" id=\"numIndices\" value=\""+numIndices);
                out.println("\"/>");
                out.println("<input type=\"submit\" name=\"submit\" id=\"submit\"/>");
                out.println("</form></body></html>");
                
                /*for(int i = 0; i < lenOfAmbigWordsList; i++ )
                {
                    out.println("<option>");
                    out.println(ambigWordsProper)
                }*/

        }

	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
        {
		doPost(request,response);
	}

        public static String removeCharAt(String s, int pos)
        {
            StringBuffer buf = new StringBuffer( s.length() - 1 );
            buf.append( s.substring(0,pos) ).append( s.substring(pos+1) );
            return buf.toString();
        }
}

