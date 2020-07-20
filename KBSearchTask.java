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
public class KBSearchTask extends SearchTask implements Runnable
{
    KBSearchTask(String uName, String query, String fQuery,PrintWriter outt)
    {
        super(uName,query,fQuery,outt);
    }
    void access() throws Exception
    {
        // Call to function KBSearch
        // To read the file - Superlinks.xml and search for relevant superlink and locator
        //out.println("To call KBSearch");
        searchAndGetResults();
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
