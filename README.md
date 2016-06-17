# GeoTools-Solr
#### Shishir Tandale

A simple JVM program to fetch data from Solr and ingest it into GeoTools.

## Usage
Add the generated JAR files to your class-path and execute class SolrDataTest.
SolrDataTest wants atleast one parameter -- the name of your Solr core.
Additionally, you can pass in a port-number. Finally, you can add a true or false
flag to print out debug information, like the full parsed schema and the
result of your query.

Exception stack traces are only printed when debugging is turned on. Other wise,
a concise but more general error message is printed to System.err.

## Functionality
SolrDataTest can currently execute arbitrary queries on arbitrary Solr cores
running on the local machine. While the queried data is not used, a GeoTools
schema is created based on the Solr schema.

## To-Do
Revise the type_spec to Java type code to use either GeoTools' or GeoMesa's
built-in type parsing. This may not accomplish everything you want to do,
but it's a big step towards a generic Solr->GeoTools class.

The parsed query needs to also be transformed into a data format that GeoTools 
can ingest easily without having to write to a file or any other I/O 
besides fetching the data from the server initially.
