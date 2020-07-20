/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SearchProcessor;

import java.util.concurrent.*;
import java.io.*;
import org.json.*;
/**
 *
 * @author Pallavi
 */
public class YahooSearchTask extends SearchTask implements Runnable
{
    protected final int numOfResults;
    protected final int numQueries;
    protected final String[] queriesToSearch;

    public YahooSearchTask(String uName, String query, String fQuery,PrintWriter outt, int count)
    {
        //outt.println("in yahoo search constructor");
        super(uName,query,fQuery,outt);
        numOfResults = count;
        numQueries = 0;
        queriesToSearch = null;
    }

    public YahooSearchTask(String uName, String query, String fQuery,PrintWriter outt, int count,int numOfQueries,String[] queries)
    {
        //outt.println("in yahoo search constructor");
        super(uName,query,fQuery,outt);
        numOfResults = count;
        numQueries = numOfQueries;
        queriesToSearch = new String[queries.length];
        for(int i = 0; i < queries.length; i++)
        {
            queriesToSearch[i] = queries[i];
        }
    }

    public void run()
    {
        try
        {
            out.println("In run");
            access();
        }
        catch(Exception e)
        {
            out.println("Error - Exception in SearchTask "+e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            done.countDown();
        }
    }

    void access() throws Exception
    {
        out.println("in access");
        // Call to function KBSearch
        // To read the file - Superlinks.xml and search for relevant superlink and locator
        //out.println("To call Yahoo Search using BOSS Api");
        searchAndGetResults();
    }

    protected void searchAndGetResults() throws Exception
    {

        out.println("In searchAndGetResults()");
        try
        {
            this.awaitCompletion();
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new Exception();
        }

    }
}
