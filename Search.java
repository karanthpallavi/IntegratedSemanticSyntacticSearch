/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SearchProcessor;

import java.io.*;

/**
 *
 * @author Pallavi
 */
public interface Search
{
    void searchKB(String userName,String Query,String fQuery,PrintWriter out,int count) throws Exception;
    void searchWeb(String userName,String Query,String fQuery,PrintWriter out,int count,int numOfResults) throws Exception;
    //String[] getResults() throws Exception;
}
