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
abstract class SearchTask implements Runnable
{
    protected final String userName;
    protected final String inputQuery;
    protected final String finalQuery;
    protected final PrintWriter out;
    protected JSONObject result;
    //protected final int numOfResults;

    protected Exception e = null;
    protected SearchTask next = null;
    protected final java.util.concurrent.CountDownLatch done = new java.util.concurrent.CountDownLatch(2);

    SearchTask(String uName, String query, String fQuery,PrintWriter outt)
    {
       //outt.println("in search constructor");
       userName = uName;
       inputQuery = query;
       finalQuery = fQuery;
       out = outt;
       result = new JSONObject();
       //numOfResults = count;
    }

    abstract void access() throws Exception;

    public void run()
    {
        try
        {
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

    public JSONObject getResults()
    {
        return result;
    }

    void setResults(JSONObject obj)
    {
        result = obj;
    }

    void awaitCompletion() throws java.lang.InterruptedException
    {
        done.await();
    }
}
