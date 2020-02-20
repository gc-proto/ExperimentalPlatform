curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"paraid",
     "type":"pint",
     "stored":true,
     "indexed":true,
     "multiValued":true
      }
}' http://localhost:8983/solr/opt/schema
curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"sectionid",
     "type":"pint",
     "stored":true,
     "indexed":true,
     "multiValued":true
      }
}' http://localhost:8983/solr/opt/schema
curl -X POST -H 'Content-type:application/json' --data-binary '{
  "replace-field":{
     "name":"paraid",
     "type":"pint",
     "stored":true,
     "indexed":true,
     "multiValued":true
      }
}' http://localhost:8983/solr/opt/schema
curl -X POST -H 'Content-type:application/json' --data-binary '{
  "replace-field":{
     "name":"sectionid",
     "type":"pint",
     "stored":true,
     "indexed":true,
     "multiValued":true
      }
}' http://localhost:8983/solr/opt/schema