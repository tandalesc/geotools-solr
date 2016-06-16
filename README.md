# GeoTools-Solr
Shishir Tandale
CCRi

A simple JVM program to fetch data from Solr and ingest it into GeoTools.

## Usage
Add the generated JAR files to your class-path and execute class SolrDataTest.
SolrDataTest wants atleast one parameter -- the name of your Solr core.
Additionally, you can pass in a port-number. Finally, you can add a true or false
flag to print out debug information, like the full parsed schema and the
result of your query.

## Functionality
SolrDataTest can currently execute arbitrary queries on arbitrary Solr cores
running on the local machine. As of *June 16, 2016*, neither the schema nor the
query being run make it to GeoTools. Further parsing needs to be done. See **To-Do**
for more information.

## To-Do
The schema needs to be parsed further to resolve the type names into Java classes
which can then be used to create a Simple Feature Type using GeoTools. The parsed
query needs to also be transformed into a data format that GeoTools can ingest
easily without having to write to a file or any other I/O besides fetching the
data from the server initially.
