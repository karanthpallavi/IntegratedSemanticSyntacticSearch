/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import java.sql.*;
//import com.microsoft.sqlserver.jdbc.*;
import Porter.*;
import WordSenseDisambiguator.*;



/**
 *
 * @author oeldbsql
 */

public class StopWordRemoval extends HttpServlet {

    String preferredQueryWord;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void destroy() {

	}
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
        {
        String inputQuery;
        String userName;
        boolean a_bUnknownUser = false;
        List<String> disambiguatedQuery = new ArrayList<String>();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
           //out.print("Hello");
           inputQuery = request.getParameter("query");
           userName = request.getParameter("UName");
           //out.println(inputQuery);
           //out.println(userName);

           // Check if the user is valid
           DB db = new DB();
           Connection connect = db.dbConnect(
            "jdbc:jtds:sqlserver://localhost:1433/UserProfiles","sa","sqladmin",out);

           if(connect != null)
           {
                PreparedStatement prepstmnt = null;
        ResultSet resUP = null;
        int a_unUserID = 0;
        String uName = userName.trim();
        String a_stCurrConcept = "";
        Vector ranks;

        String userIDSelect = "SELECT UserID from UserLogin WHERE UserLogin.UserName= '" + uName + "'";
        try
        {
           prepstmnt = connect.prepareStatement(userIDSelect);
           //prepstmnt.setString(1, userName);

           resUP = prepstmnt.executeQuery();
           int numOfRows = resUP.getRow();
           if(numOfRows==0)
           {
               a_bUnknownUser = true;
           }

           while(resUP.next())
           {
               a_unUserID = resUP.getInt(1);
               a_bUnknownUser = false;
               /*if(a_unUserID==0)
               {

               a_bUnknownUser = true;
               }*/
           }
           //out.println("User name "+ userName + "is "+a_bUnknownUser);
        }
        catch(Exception e)
        {
            out.println("Exception at user login select" + e.getMessage());
        }
            } // end of connect

        if(!a_bUnknownUser)
            {
           java.util.List<String> stopWords = java.util.Arrays.asList("a","able","about","above","abroad","according","accordingly","across","actually","adj","after","afterwards","again","against","ago","ahead","ain\"t","all","allow","allows","almost","alone","along","alongside","already","also","although","always","am","amid","amidst","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","aren\"t","around","as","a\"s","aside","ask","asking","associated","at","available","away","awfully","b","back","backward","backwards","be","became","because","become","becomes","becoming","been","before","beforehand","begin","behind","being","believe","below","beside","besides","best","better","between","beyond","both","bring","bringing","brief","but","by","c","came","can","cannot","cant","can\"t","caption","cause","causes","certain","certainly","changes","clearly","c\"mon","co","co.","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldn\"t","course","c\"s","currently","d","dare","daren\"t","definitely","described","despite","did","didn\"t","different","directly","do","does","doesn\"t","doing","done","don\"t","down","downwards","during","e","each","edu","eg","eight","eighty","either","else","elsewhere","end","ending","enough","entirely","especially","et","etc","even","ever","evermore","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","f","fairly","far","farther","few","fewer","fifth","first","five","followed","following","follows","for","forever","former","formerly","forth","forward","found","four","from","further","furthermore","g","get","gets","getting","given","gives","giving","go","goes","going","gone","got","gotten","greetings","h","had","hadn\"t","half","happens","hardly","has","hasn\"t","have","haven\"t","having","he","he\"d","he\"ll","hello","help","hence","her","here","hereafter","hereby","herein","here\"s","hereupon","hers","herself","he\"s","hi","him","himself","his","hither","hopefully","how","howbeit","however","hundred","i","i\"d","ie","if","ignored","i\"ll","i\"m","immediate","in","inasmuch","inc","inc.","indeed","indicate","indicated","indicates","inner","inside","insofar","instead","into","inward","is","isn\"t","it","it\"d","it\"ll","its","it\"s","itself","i\"ve","j","just","k","keep","keeps","kept","know","known","knows","l","last","lately","later","latter","latterly","least","less","lest","let","let\"s","like","liked","likely","likewise","little","look","looking","looks","low","lower","ltd","m","made","mainly","make","makes","many","may","maybe","mayn\"t","me","mean","meantime","meanwhile","merely","might","mightn\"t","mine","minus","miss","more","moreover","most","mostly","mr","mrs","much","must","mustn\"t","my","myself","n","name","namely","nd","near","nearly","necessary","need","needn\"t","needs","neither","never","neverf","neverless","nevertheless","new","next","nine","ninety","no","nobody","non","none","nonetheless","noone","no-one","nor","normally","not","nothing","notwithstanding","novel","now","nowhere","o","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","one\"s","only","onto","opposite","or","other","others","otherwise","ought","oughtn\"t","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","past","per","perhaps","placed","please","plus","possible","presumably","probably","provided","provides","q","que","quite","qv","r","rather","rd","re","really","reasonably","recent","recently","regarding","regardless","regards","relatively","respectively","right","round","s","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","shan\"t","she","she\"d","she\"ll","she\"s","should","shouldn\"t","since","six","so","some","somebody","someday","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t","take","taken","taking","tell","tends","th","than","thank","thanks","thanx","that","that\"ll","thats","that\"s","that\"ve","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","there\"d","therefore","therein","there\"ll","there\"re","theres","there\"s","thereupon","there\"ve","these","they","they\"d","they\"ll","they\"re","they\"ve","thing","things","think","third","thirty","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","till","to","together","too","took","toward","towards","tried","tries","truly","try","trying","t\"s","twice","two","u","un","under","underneath","undoing","unfortunately","unless","unlike","unlikely","until","unto","up","upon","upwards","us","use","used","useful","uses","using","usually","v","value","various","versus","very","via","viz","vs","w","want","wants","was","wasn\"t","way","we","we\"d","welcome","well","we\"ll","went","were","we\"re","weren\"t","we\"ve","what","whatever","what\"ll","what\"s","what\"ve","when","whence","whenever","where","whereafter","whereas","whereby","wherein","where\"s","whereupon","wherever","whether","which","whichever","while","whilst","whither","who","who\"d","whoever","whole","who\"ll","whom","whomever","who\"s","whose","why","will","willing","wish","with","within","without","wonder","won\"t","would","wouldn\"t","x","y","yes","yet","you","you\"d","you\"ll","your","you\"re","yours","yourself","yourselves","you\"ve","z","zero");
           String[] queryWords = new String[50];
           queryWords = inputQuery.split(" ");
           String StopWordsQuery = new String(inputQuery);
           //String removedStopWordsQuery[] = queryWords;
           List<String> interimQuery = new ArrayList<String>();
           //out.println(queryWords[0]);
           int a_unWordsAfterStRemoval = 0;
           int wordIter = 0;
           //out.println(queryWords.length);
           for(int iterator = 0; iterator < queryWords.length; iterator++)
           {
                
                if(stopWords.contains(queryWords[iterator]))
                {
                    StopWordsQuery = StopWordsQuery.replaceAll(queryWords[iterator], "");
                    wordIter++;
                    /*if(StopWordsQuery.length()>0)
                    {
                    interimQuery.add(StopWordsQuery);
                    //out.print(interimQuery.get(wordIter)+" ");
                    

                    }*/
                }
                a_unWordsAfterStRemoval = iterator;
           }
           //out.println(wordIter);
           //out.println("Stop word removed - single string is "+StopWordsQuery);
           //String[] newTempArr = StopWordsQuery.split(" ",queryWords.length-wordIter);
           //out.println("newtemparr length is "+newTempArr.length);
           StringTokenizer newTempArr = new StringTokenizer(StopWordsQuery);
           //out.println("CountTokens is " + newTempArr.countTokens());
           if(newTempArr.countTokens() == queryWords.length-wordIter)
           {
                //out.println("split query after stop word removal is ");
                while(newTempArr.hasMoreTokens())
                {
                    interimQuery.add(newTempArr.nextToken());
                }
           }
           else
           {
               out.println("Error");
           }

           //out.println("List contents after stop word removal is ");
           /*for(int it = 0; it < interimQuery.size();it++ )
           {
            //out.println("[" + it + "] " +interimQuery.get(it));
           }*/
           String intQuery = interimQuery.toString();
           String interQuery;
           List<String> stemmedQuery = new ArrayList<String>();
           String[] a_strExcludePorter = {"Application","Use","Definition","Elaboration","Description","Illustration","Proof","Counter-example","Analogy","Generalization","Specialization","Generalisation","Specialisation","Instantiation","Translation","Transliteration","Exercise","Problem","Problem Solution","Images","Audio","Video","applications","application","use","definition","elaboration","description","illustration","proof","counterexample","counter-example","analogy","generalization","specialization","generalisation","specialisation","instantiation","translation","transliteration","exercise","problem","problem solution","images","audio","video","apple","turmeric","Turmeric"};
           Porter porterObj = new Porter();
           Arrays.sort(a_strExcludePorter);
           for(int newIter = 0; newIter < interimQuery.size(); newIter++)
           {
                //out.println("Current word is "+interimQuery.get(newIter));
                //String curWord = interimQuery.get(newIter);
                /*String[] newWords = curWord.split(" ");
                //out.println("New words are " +newWords[newIter]);
                //out.println("New words are " +newWords[newIter+1]);*/
                String intermediateQueryWord = interimQuery.get(newIter);
                if(Arrays.binarySearch(a_strExcludePorter,intermediateQueryWord,String.CASE_INSENSITIVE_ORDER)>=0)
                {
                    interQuery = intermediateQueryWord;
                }
                else
                {
                   interQuery = porterObj.stripAffixes(interimQuery.get(newIter));
                }
                out.println("After stemming " +interQuery);
                stemmedQuery.add(interQuery);

           }
           String stemQuery = stemmedQuery.toString();
           //out.println("Stemmed query as string is " + stemQuery);
           //out.println("Inter query is "+intQuery);
           //out.println("Input Query is "+inputQuery);
           //out.println("User name is "+userName);
           //out.println("");
           //out.println("Stemmed query is "+stemmedQuery);

           int a_unNumQueryWords = stemmedQuery.size();
           // Get the servlet context to call method from servlet UserPreferredQuerySelect through
           // PreferredQueryWord interface
           /*ServletContext context = getServletConfig().getServletContext();
           int a_unNumQueryWords = stemmedQuery.size();
           RequestDispatcher rd = context.getRequestDispatcher("/UserSelectQueryWord");
           String a_stNumQueryWords = Integer.toString(a_unNumQueryWords);
           context.setAttribute(a_stNumQueryWords, "NumOfQueryWords");
           // Limitation - number of ambiguous words - 10
           Vector a_LiIndexAmbigWords = new Vector();*/


           // WordSense Disambiguation
           /*for(int a_unIter = 0; a_unIter < a_unNumQueryWords; a_unIter++)
           {
               //out.println("Calling WSD");
                WSD disambiguator = new WSD(stemmedQuery.get(a_unIter),userName,out);
                String disambiguatedQueryWord = disambiguator.getDisambiguatedWord();
                // If query is disambiguated by WSD Constructor, add directly
                if(!disambiguatedQueryWord.isEmpty())
                {
                    disambiguatedQuery.add(disambiguatedQueryWord);
                }
                // This case is not used as WSD always returns the disambiguated query word
                else
                {
                    out.println("We are in else part, error in WSD Module, Please check!");
                    // Query word - ambiguous - same ranked concepts in Ontology
                    // User dialog - user selection of preferred query word
                    // to be read by a servlet. Add just a blank space in this position
                    disambiguatedQuery.add(" ");
                    //rd.include(request, response);
                    //String userPreference = context.getAttribute("PreferredQueryWord").toString();
                    //disambiguatedQuery.add(userPreference);
                }
            }
           
           out.println("Disambiguated Query is");
           for(int a_unIterator = 0; a_unIterator < disambiguatedQuery.size(); a_unIterator++)
           {
               out.println(disambiguatedQuery.get(a_unIterator));
           }*/

           // Calling another servlet WSD
           out.println("<html><head><title>Word Sense Dismabiguator</title>");
           out.println("<script type=\"text/javascript\">");
                /*out.println("function getValue(varname){");
                out.println("var url = window.location.href;var qparts = url.split(\"?\");");
                out.println("if (qparts.length == 0){return \"\";}");
                out.println("var query = qparts[1];var vars = query.split(\"&\");");
                out.println("var value = \"\";for (i=0;i<vars.length;i++){");
                out.println("var parts = vars[i].split(\"=\");if (parts[0] == varname){");
                out.println("value = parts[1];break;}}value = unescape(value);");

                // Need to check if this is right
                out.println("value.replace(/\\+/g,\" \");");
                out.println("return value;}");*/
           out.println("function fnsubmit(){document.WSD.submit();}</script>");
           out.println("</head>");
           out.println("<body><form name=\"WSD\" id=\"WSD\" method=\"get\" action=\"WordSenseDisambiguation\">");
           out.println("<input type=\"hidden\" name=\"Query\" id=\"Query\" value=\""+inputQuery);
           out.println("\"/>");
           out.println("<input type=\"hidden\" name=\"InterimQuery\" id=\"InterimQuery\" value=\""+interimQuery);
           out.println("\"/>");
           out.println("<input type=\"hidden\" name=\"StemmedQuery\" id=\"StemmedQuery\" value=\""+stemmedQuery);
           out.println("\"/>");
           out.println("<input type=\"hidden\" name=\"UserName\" id=\"UserName\" value=\""+userName);
           out.println("\"/>");
           out.println("</form>");
           out.println("<script type=\"text/javascript\">fnsubmit();</script>");
           out.println("</body></html>");
        }
        else
        {
            out.println("Unknown User - "+userName);
            out.println("Kindly register as user using UserProfileManager application ");
        }
    }

    finally 
    {
            out.close();
    }
}

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
        {
                if(request.getParameter("query")!=null)
                {
		doPost(request,response);
                }
	} // End of doGet

    public void setPreferredQueryWord(String userSelectedQueryWord)
    {
        preferredQueryWord = userSelectedQueryWord;
    }

    public String getPreferredQueryWord()
    {
        return preferredQueryWord;
    }



}
    
 
    