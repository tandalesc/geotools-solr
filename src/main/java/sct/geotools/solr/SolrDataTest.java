package sct.geotools.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import java.util.Map;
import java.util.List;

/**
 * Created by standale on 6/13/16.
 *
 * Ingests Solr data into GeoMesa using built-in Solr GeoTools plugin
 * In general; retrieves Schema/Type-info from a Solr server using SolrJ API
 */

public class SolrDataTest {

    private static String solr_core_name;
    private static SolrClient solr_server;
    private static List<Map<String, Object>> solr_fields;

    private static int port_num = 8983;
    private static boolean debug = false;

    public static void main(String... args) {
        //parse arguments
        if(parseArgs(args) == -1)
            return; //if error parsing args, quit

        //set URL
        String solr_url = "http://localhost:"+port_num+"/solr/"+solr_core_name+"/";

        /////////////////////////////////////////////////////////////////////////
        if(debug)
            System.out.println("Your Solr URL is: " + solr_url);
        /////////////////////////////////////////////////////////////////////////

        //attach solr server
        solr_server = new HttpSolrClient(solr_url);

        //create *:* query to grab all entries
        SolrQuery all_query = new SolrQuery();
        all_query.setQuery("*:*");
        all_query.set("rows", "5");

        //issue request for schema
        SchemaRequest.Fields request = new SchemaRequest.Fields();
        SchemaResponse.FieldsResponse response;
        try {
            response = request.process(solr_server);
        } catch(Exception e) {
            System.err.println("Could not connect to server!");
            e.printStackTrace();
            return; //exit if error
        }
        //if successful, store as a List of Maps
        solr_fields = response.getFields();

        /////////////////////////////////////////////////////////////////////////
        if(debug) {
            System.out.println("Fields in Solr core \""+solr_core_name+"\": ");
            for (Map<String, Object> m : solr_fields)
                System.out.println("\t" + m.get("name") + "[" + m.get("type") + "]");
        }
        /////////////////////////////////////////////////////////////////////////

        //build SFT
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        b.setName(solr_core_name);
        for( Map<String, Object> m: solr_fields) {
            String name = (String) m.get("name"),
                    type = (String) m.get("type");
            //required for point geometry
            if(name.equals("geom"))
                b.setCRS(DefaultGeographicCRS.WGS84);
            /*TODO: add name,type pairs to SFT (needs a Java class as a type,
                so we need to parse the given name and type strings*/
            b.add(name, parseTypeName(name, type));
        }

        //execute query
        SolrResponse q_r;
        try {
            q_r = (new QueryRequest(all_query)).process(solr_server);
        } catch(Exception e) {
            System.err.println("Could not connect to server!");
            e.printStackTrace();
            return; //exit if error
        }

        /////////////////////////////////////////////////////////////////////////
        if(debug) {
            System.out.println("Executing Query: "+all_query.toString());
            System.out.println("Response: \""+solr_core_name+"\": ");
            System.out.println(q_r.getResponse().toString());
        }
        /////////////////////////////////////////////////////////////////////////

        //TODO: Retrieve the correct values in a format GeoTools likes.
    }

    /**
     * Parse passed arguments
     * @param args
     * List of arguments (just throw in your args param from your main method)
     * @return
     * Returns a negative number for an error, non-negative for success
     */
    private static int parseArgs(String... args) {
        switch(args.length) {
            case 0: // no args provided, print usage text
                printUsage();
                return -1; //error

            case 3:
                debug = Boolean.parseBoolean(args[2]);
            case 2:
                port_num = Integer.parseInt(args[1]);
            case 1:
                solr_core_name = args[0];
            default:
                return 0;
        }
    }

    /**
     * Prints usage text
     */
    private static void printUsage() {
        System.out.println("Usage: java SolrDataTest solr_core_name[string] (opt: solr_port_num[integer] (opt: debug flag[true/false]) )");
    }

    /**
     * Evaluates which Java class to map to each attribute
     * @param name
     * Name of field
     * @param typename
     * Solr type-name of field
     * @return
     * Java class which is a best match for the given Solr field
     */
    private static Class parseTypeName(String name, String typename) {
        //TODO: Actually finish this method stub and return the right Java.class for each name/type
        return Object.class;
    }
}
