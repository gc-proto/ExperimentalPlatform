# Treasury Board Testbed: Indexer

Custom indexing code to populate Solr.  There are different "indexers". 
In Eclipse, right click on each and "Run as -> Java Application".  This will
create a Run and "Debug Configuration".

## In the Eclippse Debug Configuration
Go to `Debug`->`Debug Configurations...` then select tab 
`(x) Arguments` and in the `Program Arguments` add the examples
below for each indexer:

## File-based indexers

### ca.canada.treasury.testbed.indexer.impl.NaicsIndexer

```
-batch 1000
-file "src/test/data/naics-scian-2017-structure-v3-eng.csv.gz"
-solr http://localhost:8983/solr/naics
-levels 5
```

### ca.canada.treasury.testbed.indexer.impl.OpenDataVehicleRecallsIndexer

```
-batch 10000
-file "src/test/data/vrdb_full_monthly-2019-10-06.csv.gz"
-solr http://localhost:8983/solr/recalls
```

### Notes

* The Solr host/port may need to be replaced depending where you deploy.
* Make sure to update the `.gz` file argument to match the one in the test folder.


## Database indexer

### ca.canada.treasury.testbed.indexer.impl.DBHealthRecallsIndexer

```
-solr "http://localhost:8983/solr/recalls"
-batch 1000
-max -1
-dbuser root
-dburl "jdbc:mysql://localhost:3306/awr_schema?zeroDateTimeBehavior=convertToNull"
```

This one requires a MySQL dump file from the client.  We have a snapshot we 
use found one under our Google Drive ``TreasuryBoard`` "projects" folder.
To first import the data into MySQL, you can run something like this:

```
unzip -p dbdump.sql.zip | mysql -u root -pPASSWORD awr_schema
```

### Notes
* The Solr and MySQL host/port may need to be replaced depending where you deploy.
* The password has no space after the ``-p``.  We do not care to have the
  password there for this testbed since the DB can die after indexing. If you 
  want though, you can create a ``~/.my.cnf`` file with the username and 
  password in it. See MySQL documentation for this.
