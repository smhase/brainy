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
public class StanfordAdmin extends HttpServlet {
   
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



            //Scoring Value Final Variable
            final int NN_POSITIVE_SCORE = 50;
            final int NN_NEGATIVE_SCORE = -30;

            final int NNP_POSITIVE_SCORE = 80;
            final int NNP_NEGATIVE_SCORE = -60;

            final int VB_POSITIVE_SCORE = 60;
            final int VB_NEGATIVE_SCORE = -50;

            String question;
            String answer;

            // Add in sentiment
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma");

            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);



            // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
            Annotation annotation;

            //out.println("something");
            String questionSet = request.getParameter("questionSet");



            JSONObject set = new JSONObject(questionSet);
            question = set.getString("question");
            answer = set.getString("answer");
            //out.println(question);


            annotation = new Annotation(question);

            // run all the selected Annotators on this text
            pipeline.annotate(annotation);
            //File to save the current user Question
            FileWriter fcurrent = new FileWriter("current2.txt");
            // this prints out the results of sentence analysis to file(s) in good formats
            //out.println("asdad");

            pipeline.jsonPrint(annotation, fcurrent);


            //Reading the data from the current file
            BufferedReader br = new BufferedReader(new FileReader("current2.txt"));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                jsonString = sb.toString();
                //System.out.println(jsonString);
            } finally {
                br.close();
            }

            //out.println("asdad");

// CODE FOR COUCHEDB DATABASE
            // INSERTION OF DATASETS IN DATABASE
            //DATABASE CONNECTION
            CouchDbClient dbClient2 = new CouchDbClient("stanford", true, "http", "127.0.0.1", 5984, "brainy", "microbot");

            /// out.println("asdad");

            JSONObject jsonObjectRootADD_DB = new JSONObject(jsonString);
            jsonObjectRootADD_DB.put("answer", answer);

            String DataSetToSave = jsonObjectRootADD_DB.toString();


           // out.println(DataSetToSave);
            //CODE TO ADD DATA FROM JSON STRING
            JsonObject jsonobj = dbClient2.getGson().fromJson(DataSetToSave, JsonObject.class);

            dbClient2.save(jsonobj);

            out.println("Daata inserted Successfully!");

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
