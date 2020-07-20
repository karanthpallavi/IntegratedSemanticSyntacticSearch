/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package UserSelectQueryWord;

import java.io.*;

//import java.text.*;

import java.util.*;
import org.apache.commons.lang.StringUtils.*;

import javax.servlet.*;

import javax.servlet.http.*;

/**
 *
 * @author Pallavi Karanth
 */

public class GetUserOption extends HttpServlet
{

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void destroy() {

	}

	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
        {
                response.setContentType("text/html");
		PrintWriter out = response.getWriter();

                //out.println("In GetUserOption Servlet");

                // Get all parameters
                String strStem = request.getParameter("StemmedQuery");
                //out.println("Stemmed query as param is "+strStem);

                //int lenStemQuery = strStem.length();
                int numOfCommasInStemmedQuery = org.apache.commons.lang.StringUtils.countMatches(strStem, ",");
                int numOfIndivQueryWords = numOfCommasInStemmedQuery + 1;
                //out.println("Length of Indiv query words is "+numOfIndivQueryWords);
                //String strStemWords = strStem.substring(1, lenStemQuery-3);
                //out.println("Individual stemmed query words with comma are "+strStemWords);
                String inputQuery = request.getParameter("Query");
                //out.println("Input Query is "+inputQuery);
                String interQuery = request.getParameter("InterimQuery");
                //out.println("Intermediate Query after stop word removal is "+interQuery);
                String userName = request.getParameter("UserName");
                //out.println("User Name is "+userName);

                int numOfAmbigWords = Integer.parseInt(request.getParameter("numIndices").trim());
                //out.println("Number of ambiguous words are "+numOfAmbigWords);
                String[] userOptions = new String[numOfAmbigWords];

                for(int i = 0; i < numOfAmbigWords; i++)
                {
                    String userOption = request.getParameter("AmbiguousWords"+i);
                    String userSelectedQueryWord = userOption.trim();
                    userOptions[i] = userSelectedQueryWord;
                    //out.println("User selected option is "+userOptions[i]);
                }

                List<String> finalDisambiguatedQuery = new ArrayList<String>();
                for(int i = 0; i < numOfIndivQueryWords; i++)
                {
                    finalDisambiguatedQuery.add("");
                }

                //out.println("User chosen query word is "+userOptions[0]);

                String idxVector = request.getParameter("idxAmbig").trim();
                //out.println("Idx as Param is "+idxVector);
                int NumOfCommas = org.apache.commons.lang.StringUtils.countMatches(idxVector,",");
                int numOfIndices = NumOfCommas + 1;
                //out.println("Length of idx vector is "+numOfIndices);
                int Idx = 0;
                boolean oneIndx = false;
                boolean moreIndices = false;
                int[] idxsArray = new int[numOfIndices];
                if(numOfIndices==1)
                {
                    oneIndx = true;
                    moreIndices = false;
                    int LenIdxVector = idxVector.length();
                    Idx = Integer.parseInt(idxVector.substring(1, LenIdxVector-1));
                    // Not adding userOption here to list - not working as not inserting at correct index
                    //finalDisambiguatedQuery.add(Idx,userOptions[0]);
                }
                else if(numOfIndices==0)
                {
                    // No ambiguous words in query .. we should not have come here .. some problem..
                    //out.println("No ambiguous words in query .. we should not have come here .. some problem..");
                }
                else if(numOfIndices>1)
                {
                    oneIndx = false;
                    moreIndices = true;
                
                int LenIdxVector = idxVector.length();
                //out.println("Length of idx vector is "+LenIdxVector);

                String idxVectorWithComma = idxVector.substring(0, LenIdxVector);
                //out.println("Without trimming idxVectorwithcomma is "+idxVectorWithComma);
                String idxVectorWithCommaProper = idxVectorWithComma.trim();
                //out.println("Proper Idx Vector with comma is "+idxVectorWithCommaProper);

                String[] idxArrVector = idxVectorWithCommaProper.split(",");
                //int[] idxsArray = new int[idxArrVector.length];
                for(int i = 0; i < idxArrVector.length;i++)
                {
                    idxsArray[i] = Integer.parseInt(idxArrVector[i].toString().trim());
                    //out.println("Each index element is "+idxArrVector[i]);
                    //out.println("Integer index element is "+idxsArray[i]);
                }

                /*for(int i = 0; i < idxsArray.length;i++)
                {
                    //finalDisambiguatedQuery.add(idxsArray[i],userOptions[i]);
                }*/
                }


                String disambiguatedQuery = request.getParameter("DisAmbQuery");
                //out.println("Disambiguated Query is "+disambiguatedQuery);

                int idxBracketClose = disambiguatedQuery.indexOf("]");
                //out.println("Index of ] is "+idxBracketClose);
                
                String disambiguatedQueryProper="";
                if(idxBracketClose>0)
                {
                    disambiguatedQueryProper = disambiguatedQuery.substring(0, idxBracketClose);
                    //out.println("Dismabiguated Query with comma is "+disambiguatedQueryProper);
                }

                java.util.StringTokenizer a_stDisambQueryTokens = new java.util.StringTokenizer(disambiguatedQueryProper,",");
                int numOfDisambQueryTokens = a_stDisambQueryTokens.countTokens();
                //out.println("Number of tokens in disambiguated query is "+numOfDisambQueryTokens);
                List<String> disambQueryWords = new ArrayList<String>();
                //int NumWordsMachineDisambiguated = 0;
                while(a_stDisambQueryTokens.hasMoreElements())
                {
                    String individQueryWord = a_stDisambQueryTokens.nextToken();
                    //out.println("Each final disambiguated query word is "+individQueryWord);
                    String individQueryWordFinal = individQueryWord.trim();
                    if(!individQueryWordFinal.isEmpty())
                    {
                        disambQueryWords.add(individQueryWordFinal);
                        //NumWordsMachineDisambiguated++;
                        //out.println("Individual Query word is "+individQueryWordFinal);
                    }

                }
                int iter = 0;
                int numUserOptions = numOfAmbigWords;
                for(int i = 0; i < numOfIndivQueryWords; i++)
                {
                    if(oneIndx)
                    {
                        // Only one ambiguous word
                        if(i==Idx)
                        {
                            //out.println("One ambiguous word - added");
                            finalDisambiguatedQuery.add(userOptions[0]);
                            numUserOptions--;
                        }
                        else
                        {
                            //out.println("Adding ambiguous word at index "+i);
                            finalDisambiguatedQuery.add(disambQueryWords.get(iter));
                            iter++;
                        }
                    }
                    else
                    {
                        // Many ambiguous words
                        if(moreIndices && (numUserOptions>0))
                        {
                            //out.println("Adding ambiguous word at index "+i);
                            finalDisambiguatedQuery.add(userOptions[i]);
                            numUserOptions--;
                        }
                        else
                        {
                            //out.println("Adding ambiguous word at index "+i);
                            finalDisambiguatedQuery.add(disambQueryWords.get(iter));
                            iter++;
                        }
                    }
                }


                //out.println("Final Disambiguated Query is ");
                /*for(int i = 0; i < finalDisambiguatedQuery.size(); i++)
                {
                    out.println(finalDisambiguatedQuery.get(i));
                }*/

                // Send required parameters to SearchProcessing servlet
                out.println("<html><head><title>To Search Processing</title>");
                out.println("<script type = \"text/javascript\">function fnSubmit(){document.finalDisambiguation.submit();}");
                out.println("</script>");
                out.println("</head>");
                out.println("<body bgcolor=\"cornsilk\">");
                out.println("<form name=\"finalDisambiguation\" id=\"finalDisambiguation\" action=\"SearchProcessing\">");
                out.println("<input type=\"hidden\" name=\"Query\" id=\"Query\" value=\""+inputQuery);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"InterimQuery\" id=\"InterimQuery\" value=\""+interQuery);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"StemmedQuery\" id=\"StemmedQuery\" value=\""+strStem);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"UserName\" id=\"UserName\" value=\""+userName);
                    out.println("\"/>");
                    out.println("<input type=\"hidden\" name=\"DisAmbQuery\" id=\"DisAmbQuery\" value=\""+finalDisambiguatedQuery);
                    out.println("\"/>");
                out.println("<input type=\"hidden\" name=\"NumOfQueryWords\" id=\"NumOfQueryWords\" value=\""+numOfIndivQueryWords);
                out.println("\"/>");
                out.println("</form>");
                out.println("<script type=\"text/javascript\">fnSubmit();</script>");
                out.println("</body></html>");
                
        }

	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
        {
		doPost(request,response);
	}
}

