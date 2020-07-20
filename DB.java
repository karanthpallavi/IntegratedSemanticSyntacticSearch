/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
import java.io.*;
/**
 *
 * @author Pallavi
 */


public class DB
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
}
