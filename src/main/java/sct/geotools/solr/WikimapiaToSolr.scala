package sct.geotools.solr

import org.json._
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.client.solrj.response.UpdateResponse
import org.apache.solr.common.SolrInputDocument

import scala.collection.mutable.ListBuffer

object WikimapiaSrc {
  def wmBoxApiCall(lon_min: Double, lat_min: Double, lon_max: Double, lat_max: Double, count: Int): JSONArray = {

    //build request string
    val api_str = "http://api.wikimapia.org/?function=box" +
      "&key=70EBA9E6-5CAA7D33-1E04602B-7BED0AA6-D3570B34-DD5D02C6-A74AD66F-D2EA8A8F" +
      "&lon_min=" + lon_min + "&lat_min=" + lat_min +
      "&lon_max=" + lon_max + "&lat_max=" + lat_max +
      "&format=json&count=" + count + "&language=en"
    println("\nAPI Request String:"+api_str)

    //create JSON object from request
    val json_obj = new JSONObject(scala.io.Source.fromURL(api_str).mkString)

    //print count of elements retrieved
    val json_count = json_obj.getInt("found")
    println("Found: "+json_count)

    return json_obj.getJSONArray("folder")
  }

  def ingestToSolr(solr_core: String, solr_docs: List[SolrInputDocument]): UpdateResponse = {
    val solr_client = new HttpSolrClient(solr_core)
    solr_docs.foreach(solr_client.add)
    solr_client.commit()
  }

  def jsonToSolrDoc(json: JSONArray, num_elem: Int): List[SolrInputDocument] = {
    val solr_docs = new ListBuffer[SolrInputDocument]()

    //iterate through passed JSONArray
    for (i <- 0 until num_elem) {
      //each element in the JSONArray is a JSONObject
      val doc_i = new SolrInputDocument()
      val jsonobj_i = json.getJSONObject(i)

      //iterate through the keys and use that to add fields to the Solr Document
      val keys = jsonobj_i.keys()
      while(jsonobj_i.keys().hasNext) {
        val key = keys.next()
        doc_i.addField(key, parseJSONType(jsonobj_i, key))
        keys.remove()
      }

      solr_docs+=doc_i
    }

    return solr_docs.toList
  }

  def parseJSONType(jsonObj: JSONObject, key: String): String = {
    //needs massive clean up and refactoring, as well as intelligent parsing of arrays
    try{
      return jsonObj.getString(key)
    } catch {
      case e: Exception => {
        try {
          return jsonObj.getJSONObject(key).toString()
        } catch {
          case f: Exception => {
            return jsonObj.getJSONArray(key).toString
          }
        }
      }
    }
  }

  def wikimapiaFullIngest(lon_min: Double, lat_min: Double, lon_max: Double, lat_max: Double, count: Int, solr_core: String): Unit = {
    println("Fetching Wikimapia Data...")
    val json_data = wmBoxApiCall(lon_min, lat_min, lon_max, lat_max, count)
    println("Converting to Solr Documents...")
    val solr_docs = jsonToSolrDoc(json_data , count)
    println("Ingesting into Solr...")
    ingestToSolr(solr_core, solr_docs)
  }

}