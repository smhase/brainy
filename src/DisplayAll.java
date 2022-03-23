/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.gson.JsonArray;


import org.lightcouch.CouchDbProperties;

import java.util.*;

import com.google.gson.JsonObject;
import edu.smu.tspell.wordnet.*;

import edu.stanford.nlp.io.*;

import edu.stanford.nlp.pipeline.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lightcouch.CouchDbClient;

/**
 *
 * @author Engineering
 */
public class DisplayAll extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here*/

            FileWriter fileOut;
            String jsonString;

            JSONObject displayData;

            String answer = "";
            String word = "";
            String question = "";


            //DATABASE CONNECTION
            CouchDbClient dbClient2 = new CouchDbClient("stanford", true, "http", "127.0.0.1", 5984, "brainy", "microbot");

            //out.print("Hello");
            String dbURI = dbClient2.getDBUri().toString();
            String uri = dbURI + "_all_docs";
           // out.println(uri);
            // CODE TO FETCH DATA AT THAT URL (WHICH IS THE DOCUMENT ID, REV)
            JsonObject stats = dbClient2.findAny(JsonObject.class, uri);
            String printa = stats.toString();
            // CODE TO FETCH DOC-ID IN THAT DATA
            JSONObject statsJSON = new JSONObject(printa);
            JSONArray rows = statsJSON.getJSONArray("rows");
            JSONArray jsk = new JSONArray();
            String dataq = "";
            for ( int k=0; k < rows.length(); k++) {
                question = "";
                answer = "";
                JSONObject docObject = rows.getJSONObject(k);
                String doc_id = docObject.getString("id");
                //out.println(doc_id);
                JsonObject json = dbClient2.find(JsonObject.class, doc_id);
                String printz = json.toString();

                //out.println(printz);
                JSONObject document = new JSONObject(printz);

                JSONArray documentSentences = document.getJSONArray("sentences");
                answer = document.getString("answer");
                JSONObject documentObjectZero = documentSentences.getJSONObject(0);
                JSONArray documentTokens = documentObjectZero.getJSONArray("tokens");

                for (int j = 0; j < documentTokens.length(); j++) {
                    JSONObject documentTokenElement = documentTokens.getJSONObject(j);
                    word = documentTokenElement.getString("originalText");
                    //out.println(word);
                    question = question +" "+ word;
                }

                //out.println(question);
                //out.println(answer);
                displayData = new JSONObject();
                displayData.put("question",question);
                displayData.put("answer",answer);

                jsk.put(displayData);



            }
            dataq = jsk.toString();

            out.println(dataq);


        } finally {

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String questionSet = request.getParameter("questionSet");
        PrintWriter out = response.getWriter();
        //out.println(questionSet);
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
    // </editor-fold>
}
