# Treasury Board Testbed: Solr

Search configuration/deployment files, along with test material related to the search.

With this project you can easily run Solr in a local environment to facilitate 
development and maintain schemas. 

How to run: 
===========

- Right click on ``ca.canada.treasury.testbed.solr.SolrLauncher`` and select
  Run As -> Java Application

- Modify the created Run Configuration and provide the missing arguments 
  printed out on the console and run again.

I recommend creating two run configurations.  One that cleans out the 
Solr collections and re-apply the schemas.  Another one to reuse 
already existing collections.


Agents SolrLauncher CLEAN:  

```
-clean \
-port 8983 \
-sourceHome "src/main/solr" \
-targetHome "temp/solr-home"
```

Agents SolrLauncher REUSE:  

```
-port 8983 \
-sourceHome "src/main/solr" \
-targetHome "temp/solr-home"
```


buildOnStartup: 
---------------

By default the Solr suggesters are not built on startup (but on commit, yes).
This is because there is no need on K8s since the first thing we
do when lanching Solr is index (commit) and we never restart Solr. Having
it set to true would throw an unwanted exception about not having 
any terms.  For local development, it could be useful to build on startup.
To do so without modifying the Solr config, add this JVM System Property: 

```
-Dsolr.suggest.buildOnStartup=true
```

Import/Export Data: 
===================

You can run as Java Applications the ``CollectionExporter`` 
and ``CollectionImporter`` classes to import/export as JSON.
 