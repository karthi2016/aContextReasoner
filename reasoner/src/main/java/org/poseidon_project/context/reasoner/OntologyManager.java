/*
 * Copyright 2015 POSEIDON Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.poseidon_project.context.reasoner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import org.poseidon_project.context.ContextReasonerCore;
import org.poseidon_project.context.utility.FileOperations;
import org.poseidon_project.contexts.IOntologyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage the extensible POSEIDON ontology
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class OntologyManager implements IOntologyManager{

    private Context mContext;
    private OntModel mModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    private static final String LOGTAG  = "OntologyManager";
    private static final String ONTOLOGY_PREFS = "OntologyPrefs";
    private ContextReasonerCore mReasonerCore;

    public OntologyManager(Context context, ContextReasonerCore core){
        mContext = context;
        mReasonerCore = core;
        runFirstTime();
        loadMappingFiles();
        loadOntologies();

        //Not a completely bad idea to do a GC after loading everything
        System.gc();
    }

    private void loadMappingFiles() {

        try {
            //Lets open POSEIDONs first, then deal with others.
            parseURLtoFileMappingFile(mContext.getAssets().open("ontologyMap.json"));




        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadOntologies() {
        //Read and Open all POSEIDON Related Ontologies
        for (String uri : POSEIDONOntologies.ONTOLOGIES_ARRAY) {
            mModel.read(uri);
        }
    }

    private void runFirstTime() {
        SharedPreferences settings = mContext.getSharedPreferences(ONTOLOGY_PREFS, 0);

        boolean beenRun = settings.getBoolean("ranFirst", false);

        if (! beenRun ) {
            try {
                File existingDir = new File(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/ontologies");

                FileOperations.deleteDirectory(existingDir);


                InputStream in = mContext.getAssets().open("ontologyMap.json");

                OntologyFileMapParser parser = new OntologyFileMapParser(in);
                HashMap<String, String> toBeCopied = parser.parse();

                for(String filepath : toBeCopied.values()) {
                    boolean copied = copyOntologyFile(filepath);

                    if (! copied ){
                        Log.e(LOGTAG, "Failed to copy to SD Card: " + filepath);
                    }
                }

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("ranFirst", true);

            } catch (IOException e) {
                Log.e(LOGTAG, e.getStackTrace().toString());
            }
        }

    }

    public boolean copyOntologyFile(String filepath) {

        try {
            String filename = filepath.substring(filepath.lastIndexOf("/") + 1);

            InputStream in = mContext.getAssets().open(filename);

            FileOperations.copyFile(in, filepath);

        } catch (IOException e) {
            Log.e(LOGTAG, e.getStackTrace().toString());
            return false;
        }

        return true;
    }

    public void loadOntologyFromFile(OntModel model, String location) {
        try {
            File ontFile = new File(location);
            InputStream in = new FileInputStream(ontFile);
            model.read(in, null);
            in.close();
        } catch (IOException e) {
            Log.e(LOGTAG, e.getStackTrace().toString());
        }
    }

    public void loadOntologyFromURL(OntModel model, String url) {
        model.read(url);
    }

    public void parseURLtoFileMappingFile(String location) {
        try {
            File mappingFile = new File(location);
            InputStream in = new FileInputStream(mappingFile);
            parseURLtoFileMappingFile(in);
        } catch(IOException e) {
            Log.e(LOGTAG, e.getStackTrace().toString());
        }

    }

    public void parseURLtoFileMappingFile(InputStream in) {

        OntologyFileMapParser parser = new OntologyFileMapParser(in);
        HashMap<String, String> toBeMapped = parser.parse();

        for(Map.Entry<String, String> entry : toBeMapped.entrySet()) {
            mapOntologyURLtoFile(entry.getKey(), entry.getValue());
        }

    }

    public void mapOntologyURLtoFile(String url, String fileLocation) {
        OntDocumentManager dm = mModel.getDocumentManager();
        dm.addAltEntry(url, "file:" + fileLocation);

    }


    public void runSPARQLQuery(String queryText) {
        runSPARQLQuery(queryText, mModel);
    }

    public void runSPARQLQuery(String queryText, OntModel model) {

        Query query = QueryFactory.create(queryText);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet results = queryExecution.execSelect();

        //close QueryExecution
        queryExecution.close();

    }

    public void updatePropertyValue(String property, String value) {

    }
}
