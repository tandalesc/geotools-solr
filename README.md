# GeoTools-Solr
#### Shishir Tandale

Collection of JVM Solr utilities.

SolrDataTest was intended as a quick tool to export data from Solr
and ingest it into GeoMesa.

WikimapiaToSolr is a tool to build a Wikimapia API request, get the data,
convert the raw JSON into SolrDocuments, and ingest it into an arbitrary Solr core.
It should also be noted that we are using the old Wikimapia box (not bbox) API so we
are not limited in the number of entries that can be read.

This project is a work-in-progress and not yet designed for use outside of Intellij/development.
