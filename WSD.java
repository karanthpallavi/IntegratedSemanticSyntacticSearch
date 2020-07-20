/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package WordSenseDisambiguator;


import java.util.*;
import java.sql.*;
import java.io.*;


/**
 *
 * @author Vishu
 */
public class WSD {

    String stemmedQuery;
    boolean o_bIsAmbig,o_binSemLex,o_bDisambiguated;
    String disambiguatedQueryWord;
    ArrayList<String> conceptNames;
    Vector ArrIndxOnto;
    String[] ambigWords;
    Map ConceptInOnto = new HashMap();
    //ArrayList<String> ambigWords;


    // Constructor
    public WSD(String inputStemmedWord, String userName, PrintWriter out)
    {

        //out.println("Calling WSD for Query word "+inputStemmedWord);

        o_bIsAmbig = false;
        o_binSemLex = false;
        o_bDisambiguated = false;

        DB db = new DB();
        Connection connect = db.dbConnect(
     "jdbc:jtds:sqlserver://localhost:1433/Lexicon","sa","sqladmin",out);

        if(connect != null)
        {
            o_binSemLex = getMatchingConcepts(connect,inputStemmedWord,out);
            if(o_binSemLex)
            {
                // If inputStemmedWord is present in SemanticLexicon,
                // We need to find if there are more ConceptNames for the same inputStemmedWord
                // If so, ranks are read from UserProfiles DB and inputStemmedWord is disambiguated

                /*out.println("There exists one or more word forms in Semantic Lexicon");
                out.println("Yet to disambiguate the word");*/
                disambiguatedQueryWord = " ";

                // Need to read ranks for the different concept forms of the inputStemmedWord
                DB dbUserProfiles = new DB();
                Connection upConnect = dbUserProfiles.dbConnect("jdbc:jtds:sqlserver://localhost:1433/UserProfiles", "sa","sqladmin",out);
                if(upConnect != null)
                {
                    // Check if atleast one form of the inputStemmedWord exists in Ontology
                    // Check if vector ArrIndxOnto is empty
                    if(ArrIndxOnto.isEmpty())
                    {
                        // inputStemmedWord exists in SemanticLexicon table
                        // But there is no form of the word in the Ontology
                        // inputStemmedWord has to be disambiguated by the User
                        o_bIsAmbig = true;
                        o_bDisambiguated = false;
                        disambiguatedQueryWord = " ";
                        //out.println("Word not in Ontology but present in Semantic Lexicon table, To be disambiguated by User");
                    }
                    else
                    {
                        // inputStemmedWord is present in Ontology
                        // Check how many instances exist in Ontology
                        // If more than one, we need to read ranks from ConceptRanking for user userName
                        if(ArrIndxOnto.size()>1)
                        {
                            // More than one word form in Ontology -- Read ranks
                            //out.println("Need to use ranks to disambiguate");
                            
                            o_bDisambiguated = ReadRanksAndDisambiguateQueryWord(upConnect,userName,out);
                        }
                        else
                        {
                            //out.println("Only one form of "+inputStemmedWord+" exists in Ontology");
                            disambiguatedQueryWord = inputStemmedWord;
                            //out.println("Disambiguated Query word in WSD is "+disambiguatedQueryWord);
                            o_bDisambiguated = true;
                        }
                    }
                }
                else
                {
                    out.println("Error, DB Connection to UserProfiles not successful");
                }


            }
            else
            {
                // inputStemmedWord not in the table SemanticLexicon
                // DisambiguatedQueryWord is the same as inputStemmedWord
                // Word is not ambiguous and not in Ontology
                disambiguatedQueryWord = inputStemmedWord;
                o_bIsAmbig = false;
                o_bDisambiguated = true;
                //out.println("Disambiguated Query word in WSD is "+disambiguatedQueryWord);
            }
        }
        else
        {
            out.println("Error, DB Connection to Lexicon not successful");
        }

        
    }

public String getDisambiguatedWord()
    {
        return disambiguatedQueryWord;
    }

public boolean getIsDisambiguated()
    {
        return o_bDisambiguated;
    }

    //public String[] getAmbiguousWords()
public ArrayList<String> getAmbiguousWords()
    {
        //return ambigWords;
        return conceptNames;
    }

    public boolean isAmbig()
    {
        return o_bIsAmbig;
    }

    public boolean IsInSemLex()
    {
        return o_binSemLex;
    }

    public boolean getMatchingConcepts(Connection connect,String inputStemmedWord,PrintWriter out)
    {
        ResultSet resSet = null;
        PreparedStatement prepStmnt;
        //String selectFromSemLexicon = "SELECT * from SemanticLexicon WHERE SemanticLexicon.Word=?";
        /*ArrayList concepts = new ArrayList<String>();
        concepts.add("Arjuna");
        concepts.add("Arjuna Legendary Hero");
        concepts.add("Arjun MBT");

        ArrayList words = new ArrayList<String>();
        words.add("Arjuna");
        words.add("Arjuna");
        words.add("Arjuna");

        Vector isInOntology = new Vector();
        isInOntology.add(1);
        isInOntology.add(0);
        isInOntology.add(0);*/



        //String insertToSemLexicon = "INSERT into SemanticLexicon (ConceptNames,Word,InOntology) values(?,?,?)";

        String selectFromSemLexicon = "SELECT * from SemanticLexicon WHERE Word=?";
        try
        {
            //For inserting into table from this program - just for testing
        /*prepStmnt = connect.prepareStatement(insertToSemLexicon);
        prepStmnt.clearParameters();
        int[] update;
        for(int i=0;i<words.size();i++)
        {
        prepStmnt.setString(1,concepts.get(i).toString());
        prepStmnt.setString(2,words.get(i).toString());
        prepStmnt.setInt(3,Integer.parseInt(isInOntology.get(i).toString()));
        prepStmnt.addBatch();
        update = prepStmnt.executeBatch();

            }*/

        prepStmnt = connect.prepareStatement(selectFromSemLexicon);
       
        prepStmnt.setString(1, inputStemmedWord);
        resSet = prepStmnt.executeQuery();
        int it = 0;
        int isInOnto = 0;
        int a_unCount = 0;
        String concept="";
        conceptNames = new ArrayList<String>();
        ArrIndxOnto = new Vector();

        while(resSet.next())
        {
            // Read the ConceptNames from result set
            concept = resSet.getString(2);
            //out.println(concept.length());
            //out.println(concept);
            
            conceptNames.add(concept);
            //out.println(conceptNames.get(it));
            
            // Read IsOntology field and note the index/number of the
            // inputStemmedWord among it's various concept forms
            isInOnto = resSet.getInt(4);
            //ConceptInOnto.put(isInOnto, concept);
            //out.println("Is in Ontology :"+isInOnto);
            if(isInOnto == 1)
            {
                // If inputStemmedWord exists in Ontology as indicated by isInOnto,
                // mark the index of the term among various concept word forms
                ArrIndxOnto.add(it);
                a_unCount++;
            }

            it++;
        }

        
        //out.println("Number of rows selected "+it);
        // it - gives number of rows selected
        // If inputStemmedWord is present in SemanticLexicon, 
        // We need to find if there are more ConceptNames for the same inputStemmedWord
        // If so, ranks are read from UserProfiles DB and inputStemmedWord is disambiguated
        if(it>0)
        {
            // Set o_binSemLex to true
            o_binSemLex = true;

            out.println("Various forms of word "+inputStemmedWord + " are");
            for(int i = 0; i < conceptNames.size();i++)
            {
                out.println(conceptNames.get(i));
            }
        }
        else
        {
            // inputStemmedWord not in the table SemanticLexicon
            // DisambiguatedQueryWord is the same as inputStemmedWord
            o_binSemLex = false;
        }
        }
        catch(SQLException e)
        {
            out.println("Exception while executing select from SemanticLexicon "+e.getMessage());
            e.printStackTrace(out);
        }
        return IsInSemLex();

    }
    
    public boolean ReadRanksAndDisambiguateQueryWord(Connection upConnect,String userName,PrintWriter out)
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
           prepstmnt = upConnect.prepareStatement(userIDSelect);
           //prepstmnt.setString(1, userName);

           resUP = prepstmnt.executeQuery();

           while(resUP.next())
           {
               a_unUserID = resUP.getInt(1);
           }
           out.println("User Id for "+userName+" is "+a_unUserID);

           ranks = new Vector();

           for(int a_unIter = 0; a_unIter < ArrIndxOnto.size(); a_unIter++)
           {
               // Need to read the conceptName in Ontology - a word form of inputStemmedWord
               int IdxOfConcept = Integer.parseInt(ArrIndxOnto.get(a_unIter).toString());
               a_stCurrConcept = conceptNames.get(IdxOfConcept);
               out.println("Current Concept for which rank has to be read is "+a_stCurrConcept);
               String readRankUserProfilesConceptRanking  = "SELECT Rank from ConceptRanking WHERE ConceptRanking.UserID='"+a_unUserID+"' AND ConceptRanking.ConceptName='"+a_stCurrConcept+"'";

               prepstmnt = upConnect.prepareStatement(readRankUserProfilesConceptRanking);
               resUP = prepstmnt.executeQuery();

               while(resUP.next())
               {
                   ranks.add(resUP.getInt(1));
               }

           }

           // Ranks read from DB
           int rankSize = ranks.size();
           out.println("Number of rank values are "+rankSize);

           int[] arrRanks = new int[rankSize];
           for(int i = 0; i <rankSize;i++)
           {
               arrRanks[i] = Integer.parseInt(ranks.get(i).toString());
               out.println("Rank : "+arrRanks[i]);
           }

           if(rankSize<3)
           {
               // Only two values..
               // Check which is higher and dismabiguate corresponding Concept
               if(arrRanks[0]<arrRanks[1])
               {
                   int idxConcept = Integer.parseInt(ArrIndxOnto.get(0).toString());
                   disambiguatedQueryWord = conceptNames.get(idxConcept);
                   o_bDisambiguated = true;
                   o_bIsAmbig = false;
                   out.println("Disambiguated Query word in WSD is "+disambiguatedQueryWord);
               }
               else if(arrRanks[0]>arrRanks[1])
               {
                   int idxConcept = Integer.parseInt(ArrIndxOnto.get(1).toString());
                   disambiguatedQueryWord = conceptNames.get(idxConcept);
                   o_bDisambiguated = true;
                   o_bIsAmbig = false;
                   out.println("Disambiguated Query word in WSD is "+disambiguatedQueryWord);
               }
                else if(arrRanks[0]==arrRanks[1])
               {
                    disambiguatedQueryWord="";
                    o_bDisambiguated = false;
                    o_bIsAmbig = true;
                    out.println("Disambiguated Query word in WSD is "+disambiguatedQueryWord);
                }
           }
           else
           {
              
                Arrays.sort(arrRanks);
                Set newSet = new HashSet();
                boolean retained = newSet.addAll(ranks);
                // If array size less than rank size, then duplicate - concepts with same ranks
                // User need to disambiguate
                // Word not yet disambiguated
                if(!retained)
                {
                    disambiguatedQueryWord="";
                    o_bDisambiguated = false;
                    o_bIsAmbig = true;
                    out.println("Disambiguated Query word in WSD is "+disambiguatedQueryWord);
                }
                else
                {
                    // Take last element in sorted arrRanks as index for concept - disambiguated Query
                    int idxConcept = arrRanks[rankSize-1];
                    disambiguatedQueryWord = conceptNames.get(idxConcept);
                    o_bDisambiguated = true;
                    o_bIsAmbig = false;
                    out.println("Disambiguated Query word in WSD is "+disambiguatedQueryWord);
                }
           }

           
        }
        catch(SQLException e)
        {
            out.println("Exception while executing select from UserProfiles DB"+e.getMessage());
            e.printStackTrace();
        }


        return getIsDisambiguated();
    }

}

class DB
{
    public DB() {}

    public Connection dbConnect(String db_connect_string,
  String db_userid, String db_password,PrintWriter out)
    {
        Connection conn = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
             conn = DriverManager.getConnection(
    db_connect_string, db_userid, db_password);
            //out.println("connected");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return conn;
    }


};





