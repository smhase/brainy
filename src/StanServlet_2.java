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
public class StanServlet extends HttpServlet {
   
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String a[]={"hi","hello","hii","good morning","gm","good evening","ge","good night","bye","gn","good afternoon"};
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here*/




            FileWriter fileOut;
        String jsonString;

        HashMap<String,Integer> Scoring = new HashMap<String, Integer>();
        HashMap<String,Integer> ScoringTemp = new HashMap<String, Integer>();

        int tempScore;
        int Score;

        int type = 1;
        String answer = "";
        String question = "";
        //Scoring Value Final Variable
        final int NN_POSITIVE_SCORE = 50;
        final int NN_NEGATIVE_SCORE = -30;

        final int NNP_POSITIVE_SCORE = 80;
        final int NNP_NEGATIVE_SCORE = -60;

        final int VB_POSITIVE_SCORE = 60;
        final int VB_NEGATIVE_SCORE = -50;

		final int PRP_POSITIVE_SCORE = 40;
        final int PRP_NEGATIVE_SCORE = -30;

        //json variables
        int index;
        String word, lemma, pos, ner;



        // Create a CoreNLP pipeline. To build the default pipeline, you can just use:
        //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // Here's a more complex setup example:
        //   Properties props = new Properties();
        //   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
        //   props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
        //   props.put("ner.applyNumericClassifiers", "false");
        //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Add in sentiment

            String greeting;
            String greetingJSON;
            for(int i=0;i<a.length;i++){
                if(request.getParameter("questionSet").toLowerCase().equals(a[i]))
                {
                    greeting = request.getParameter("questionSet")+"!!! I am BRaiNY.";
                    greetingJSON = "{\"score\": \"100\",\"answer\":\" "+greeting+"\"}";
                    out.println(greetingJSON);
                    break;
                }
            }
            if(request.getParameter("questionSet").toLowerCase().equals("thanks")){
                out.println("{\"score\": \"100\",\"answer\":\" "+"Welcome!"+"\"}");
            }else{
                String questionSet = request.getParameter("questionSet");
                Properties props = new Properties();
                props.setProperty("annotators", "tokenize, ssplit, pos, lemma");

                StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

                //WORDNET
                System.setProperty("wordnet.database.dir", "C:\\dict");
                NounSynset nounSynset;
                VerbSynset verbSynset;
                WordNetDatabase database = WordNetDatabase.getFileInstance();



                // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
                Annotation annotation;

                //out.println("something");


                // out.println(questionSet);
                // JSONObject set = new JSONObject(questionSet);
                // question = set.getString("question");

                //out.println(question);


                annotation = new Annotation(questionSet);

                // run all the selected Annotators on this text
                pipeline.annotate(annotation);
                //File to save the current user Question
                FileWriter fcurrent = new FileWriter("current.txt");
                // this prints out the results of sentence analysis to file(s) in good formats
                //out.println("asdad");

                pipeline.jsonPrint(annotation, fcurrent);


                //Reading the data from the current file
                BufferedReader br = new BufferedReader(new FileReader("current.txt"));
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

                // JSONObject jsonObjectRootADD_DB = new JSONObject(jsonString);
                //      jsonObjectRootADD_DB.put("answer", answer);

                //      String DataSetToSave = jsonObjectRootADD_DB.toString();


                // out.println(DataSetToSave);
                //CODE TO ADD DATA FROM JSON STRING
                //     JsonObject jsonobj = dbClient2.getGson().fromJson(DataSetToSave, JsonObject.class);

                //      dbClient2.save(jsonobj);




                //DATA COMPUTATION CODE

                if(type == 1) {
                    //JSON EXTRACTION
                    // out.print(type);
                    JSONObject jsonObjectRoot = new JSONObject(jsonString);
                    JSONArray jsonArraySentences = jsonObjectRoot.getJSONArray("sentences");
                    JSONObject jsonObjectZero = jsonArraySentences.getJSONObject(0);
                    JSONArray jsonArrayTokens = jsonObjectZero.getJSONArray("tokens");

                    int tempIndex = 0;
                    // SCORE COUNTING CODE


                    String dbURI = dbClient2.getDBUri().toString();
                    String uri = dbURI + "_all_docs";
                    // CODE TO FETCH DATA AT THAT URL (WHICH IS THE DOCUMENT ID, REV)
                    JsonObject stats = dbClient2.findAny(JsonObject.class, uri);
                    String printa = stats.toString();
                    // CODE TO FETCH DOC-ID IN THAT DATA
                    JSONObject statsJSON = new JSONObject(printa);
                    JSONArray rows = statsJSON.getJSONArray("rows");
                    // out.println("hi4");
                    for (int k = 0; k < rows.length(); k++) {

                        JSONObject docObject = rows.getJSONObject(k);
                        String doc_id = docObject.getString("id");

                        Scoring.put(doc_id, 0);
                        ScoringTemp.put(doc_id, 0);
                        Score = 0;

                        JsonObject json = dbClient2.find(JsonObject.class, doc_id);
                        String printz = json.toString();

                        JSONObject document = new JSONObject(printz);

                        JSONArray documentSentences = document.getJSONArray("sentences");
                        JSONObject documentObjectZero = documentSentences.getJSONObject(0);
                        JSONArray documentTokens = documentObjectZero.getJSONArray("tokens");

                        for (int i = 0; i < jsonArrayTokens.length(); i++) {
                            JSONObject temp = jsonArrayTokens.getJSONObject(i);
                            index = temp.getInt("index");
                            lemma = temp.getString("lemma");
                            pos = temp.getString("pos");

                            for (int j = 0; j < documentTokens.length(); j++) {
                                JSONObject documentTokenElement = documentTokens.getJSONObject(j);

                                if (pos.equals(documentTokenElement.getString("pos"))) {
                                    if (pos.equals("VB") || pos.equals("VBG") || pos.equals("VBN") || pos.equals("VBD")) {
                                        ArrayList<String> s = new ArrayList<String>();
                                        //  out.println(lemma);
                                        Synset[] synsets = database.getSynsets(lemma, SynsetType.VERB);
                                        for (int b = 0; b < synsets.length; b++) {
                                            verbSynset = (VerbSynset) (synsets[b]);
                                            {
                                                s.add(verbSynset.getWordForms()[0]);
                                                // System.out.println(nounSynset.getWordForms()[0]);
                                            }

                                        }

                                        for (String lemmaz : s) {
                                            if (lemmaz.equals(documentTokenElement.getString("lemma"))) {
                                                if (ScoringTemp.get(doc_id) == 0 || ScoringTemp.get(doc_id) == VB_NEGATIVE_SCORE) {
                                                    ScoringTemp.replace(doc_id, VB_POSITIVE_SCORE);
                                                    Scoring.replace(doc_id, Scoring.get(doc_id) + ScoringTemp.get(doc_id));
                                                    ScoringTemp.replace(doc_id, 0);
                                                    break;
                                                }
                                            } else {
                                                if (ScoringTemp.get(doc_id) == 0) {
                                                    ScoringTemp.replace(doc_id, VB_NEGATIVE_SCORE);
                                                }
                                            }
                                        }

                                    }
                                    if (pos.equals("NNS") || pos.equals("NN")) {
                                        ArrayList<String> s = new ArrayList<String>();
                                        // out.println(lemma);
                                        Synset[] synsets = database.getSynsets(lemma, SynsetType.NOUN);
                                        for (int b = 0; b < synsets.length; b++) {
                                            nounSynset = (NounSynset) (synsets[b]);
                                            {
                                                s.add(nounSynset.getWordForms()[0]);
                                                // System.out.println(nounSynset.getWordForms()[0]);
                                            }

                                        }

                                        for (String lemmaz : s) {
                                            // out.println(lemmaz);
                                            if (lemmaz.equals(documentTokenElement.getString("lemma"))) {
                                                if (ScoringTemp.get(doc_id) == 0 || ScoringTemp.get(doc_id) == NN_NEGATIVE_SCORE) {
                                                    ScoringTemp.replace(doc_id, NN_POSITIVE_SCORE);
                                                    Scoring.replace(doc_id, Scoring.get(doc_id) + ScoringTemp.get(doc_id));
                                                    ScoringTemp.replace(doc_id, 0);
                                                    break;
                                                }
                                            } else {
                                                if (ScoringTemp.get(doc_id) == 0) {
                                                    ScoringTemp.replace(doc_id, NN_NEGATIVE_SCORE);

                                                }
                                            }
                                        }
                                    }


                                    if (pos.equals("NNP") || pos.equals("NNPS")) {
                                        if (lemma.equals(documentTokenElement.getString("lemma"))) {
                                            if (ScoringTemp.get(doc_id) == 0 || ScoringTemp.get(doc_id) == NNP_NEGATIVE_SCORE) {
                                                ScoringTemp.replace(doc_id, NNP_POSITIVE_SCORE);
                                                Scoring.replace(doc_id, Scoring.get(doc_id) + ScoringTemp.get(doc_id));
                                                ScoringTemp.replace(doc_id, 0);
                                                break;
                                            }
                                        } else {
                                            if (ScoringTemp.get(doc_id) == 0) {
                                                ScoringTemp.replace(doc_id, NNP_NEGATIVE_SCORE);
                                            }
                                        }
                                    }
									if (pos.equals("PRP")) {
                                        if (lemma.equals(documentTokenElement.getString("lemma"))) {
                                            if (ScoringTemp.get(doc_id) == 0 || ScoringTemp.get(doc_id) == PRP_NEGATIVE_SCORE) {
                                                ScoringTemp.replace(doc_id, PRP_POSITIVE_SCORE);
                                                Scoring.replace(doc_id, Scoring.get(doc_id) + ScoringTemp.get(doc_id));
                                                ScoringTemp.replace(doc_id, 0);
                                                break;
                                            }
                                        } else {
                                            if (ScoringTemp.get(doc_id) == 0) {
                                                ScoringTemp.replace(doc_id, PRP_NEGATIVE_SCORE);
                                            }
                                        }
                                    }

                                }


                            }
                        }

                    }


                    // SCORE EVAL

                    int HighScore = 0;
                    boolean isHighSet = false;
                    String HighScoreDoc_ID = "";
                    for (HashMap.Entry<String, Integer> entry : Scoring.entrySet()) {
                        if (entry.getValue() > HighScore) {
                            isHighSet = true;
                            HighScore = entry.getValue();
                            HighScoreDoc_ID = entry.getKey();
                        }
                    }


                    int FC_HighScore = 0;
                    float percent;

                    JsonObject json = dbClient2.find(JsonObject.class, HighScoreDoc_ID);
                    String z = json.toString();
                    JSONObject document = new JSONObject(z);
                    String Answer = document.getString("answer");
                    JSONArray FC_sentences = document.getJSONArray("sentences");
                    JSONObject FC_zero = FC_sentences.getJSONObject(0);
                    JSONArray FC_tokens = FC_zero.getJSONArray("tokens");

                    for (int l = 0; l < FC_tokens.length(); l++) {
                        JSONObject FC_SingleElement = FC_tokens.getJSONObject(l);
                        if (FC_SingleElement.getString("pos").equals("VB") || FC_SingleElement.getString("pos").equals("VBD") || FC_SingleElement.getString("pos").equals("VBG") || FC_SingleElement.getString("pos").equals("VBN")) {
                            FC_HighScore = FC_HighScore + VB_POSITIVE_SCORE;
                        }
                        if (FC_SingleElement.getString("pos").equals("NN") || FC_SingleElement.getString("pos").equals("NNS")) {
                            FC_HighScore = FC_HighScore + NN_POSITIVE_SCORE;
                        }
                        if (FC_SingleElement.getString("pos").equals("NNP") || FC_SingleElement.getString("pos").equals("NNPS")) {
                            FC_HighScore = FC_HighScore + NNP_POSITIVE_SCORE;
                        }

                    }

                    //  out.println(HighScore);
                    //  out.println(FC_HighScore);
                    percent = (float) HighScore / FC_HighScore * 100;
                    // out.println("Score is : " + percent + "%");
                    // out.println("Answer is : " + Answer);

                    JSONObject as = new JSONObject();
                    as.put("answer",Answer);
                    as.put("score",percent);

                    String answering = as.toString();
                    out.println(answering);



                }



            }



           
        } finally { 
            out.close();
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
