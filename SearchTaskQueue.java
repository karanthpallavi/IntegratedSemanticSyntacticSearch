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
public class SearchTaskQueue
{
    protected SearchTask thisSearch = null;
    protected SearchTask nextSearch = null;
    protected String currentQuery = "";
    protected final PrintWriter outt;

    protected final Semaphore available = new Semaphore(0);

    SearchTaskQueue(String uName, String query, String fQuery,PrintWriter out, int count)
    {
        //out.println("In SearchTaskQueue Constructor");
        KBSearchTask kbsearch = new KBSearchTask(uName,query,fQuery,out);
        YahooSearchTask websearch = new YahooSearchTask(uName,query,fQuery,out,count);
        thisSearch = websearch;
        nextSearch = kbsearch;
        outt = out;
    }

    SearchTaskQueue(String uName, String query, String fQuery,PrintWriter out, int count,int numOfQueries,String[] queries)
    {
        //out.println("In SearchTaskQueue Constructor");
        KBSearchTask kbsearch = new KBSearchTask(uName,query,fQuery,out);
        YahooSearchTask websearch = new YahooSearchTask(uName,query,fQuery,out,count,numOfQueries,queries);
        thisSearch = websearch;
        nextSearch = kbsearch;
        outt = out;
        //thisSearch.run();
        //nextSearch.run();
        /*try
        {
            searchKB(uName,query,fQuery,out,count);
        }
        catch(Exception e)
        {
            out.println("Exception at call to searchKB "+e.getMessage());
            e.printStackTrace();
        }*/

    }

    void put(SearchTask t)
    {
        insert(t);
        available.release();
    }

    SearchTask take() throws InterruptedException
    {
        //outt.println("In take");
        available.acquire();
        return extract();
    }

    synchronized void insert(SearchTask t)
    {
        SearchTask q;
        if(currentQuery.matches(t.finalQuery))
        {
            q = thisSearch;
            if(q == null)
            {
                thisSearch = t;
                return;
            }
        }
        else
        {
            q = nextSearch;
            if(q == null)
            {
                thisSearch = t;
                return;
            }
        }
        SearchTask trail = q;
        q = trail.next;
        for(;;)
        {
            // no extra condition here for ordering search tasks
            if(q == null)
            {
                trail.next = t;
                t.next = q;
                return;
            }
            else
            {
                trail = q;
                q = q.next;
            }

        }
    }

    synchronized SearchTask extract()
    {
        outt.println("In extract");
        if(thisSearch == null)
        {
            thisSearch = nextSearch;
            nextSearch = null;
        }
        SearchTask t = thisSearch;
        thisSearch = t.next;
        currentQuery = t.finalQuery;
        return t;
    }
}
