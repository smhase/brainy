import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;


import java.util.*;

/**
 * Created by Saurabh on 3/30/2017.
 */
public class CoucheDB extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
		
		response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
        try {

        //METHOD 1
        // couchdb-2.properties is on the classpath
        //CouchDbClient dbClient1 = new CouchDbClient("couchdb-2.properties");

        //METHOD 2
        CouchDbClient dbClient2 = new CouchDbClient("stanford", true, "http", "127.0.0.1", 5984, "brainy", "microbot");




        //CODE TO ADD DATA FROM JSON STRING
      //  String jsonstr="{\"name\":\"Saurabh\"}";
    //    JsonObject jsonobj = dbClient2.getGson().fromJson(jsonstr, JsonObject.class);
       // dbClient2.save(jsonobj);

        // CODE TO FETCH THE DATA FROM THE DOCUMENT WITH G

        //CODE TO FETCH URL
     //   String baseURI = dbClient2.getBaseUri().toString();
          String dbURI = dbClient2.getDBUri().toString();

          String uri = dbURI + "_all_docs";
     //   System.out.println(uri);

        // CODE TO FETCH DATA AT THAT URL (WHICH IS THE DOCUMENT ID, REV)
            JsonObject stats = dbClient2.findAny(JsonObject.class, uri);
            String printa = stats.toString();
			out.println(printa);
		}catch(Exception e){
			out.println(e);
		}
		finally{
			
		}
	}
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }

          
		}
		
