# Owl2Neo4j
An experimental OWL/RDF to Neo4j converter

This is a general (experimental) OWL/RDF to Neo4j converter. The program takes as input an RDF graph and returns a cypher file

for example the following RDF file
```
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

<http://dbpedia.org/ontology/Person> a owl:Class ;
    rdfs:label "person"@en ;
    rdfs:label "persona"@it ;
    owl:equivalentClass <http://schema.org/Person>, <http://schema.org/Person> .

<https://github.com/jimkont> a <http://dbpedia.org/ontology/Person> .
```
Generates the following cypher script

```
CREATE (dbpediaowl_Person:owl_Class {rdfs_label_it:"persona", rdfs_label_en:"person", _id:"dbpediaowl_Person", _uri:"http://dbpedia.org/ontology/Person" })
CREATE (schema_Person {_id:"schema_Person", _uri:"http://schema.org/Person" })
CREATE (p312307766_jimkont:schema_Person:dbpediaowl_Person {_id:"p312307766_jimkont", _uri:"https://github.com/jimkont" })
CREATE (owl_Class {_id:"owl_Class", _uri:"http://www.w3.org/2002/07/owl#Class" })
CREATE
(dbpediaowl_Person)-[:owl_equivalentClass]->(schema_Person),
(dbpediaowl_Person)-[:rdf_type]->(owl_Class),
(p312307766_jimkont)-[:rdf_type]->(schema_Person),
(p312307766_jimkont)-[:rdf_type]->(dbpediaowl_Person)
```

## How it works
For every non-Literal RDF node a new Node is created. 
To avoid name clashes the program names the nodes with the local name of the URI and a prefix: `prefix_localName`.
For the prefix we try the local prefix declarations in the RDF graph (if they exist, e.g. in turtle or n3) or consolidate with the LOV service.
If no prefix is found, a numeric based prefix is used.

Whenever there is an `rdf:type` statement the program tries to assign the class to the node as label e.g. `jimkont:Person`.
For every node we have 2 additional values, a `_id` that matches the node name and a `_uri` with the full URI.

we follow the same approach for naming the property labels, e.g. `-[:rdf_type]->`

## How to run it
Use the following commands (needs maven)
```
owl2neo4j <owl/rdf uri> <outputfile>

e.g.
owl2neo4j test.ttl test.cypher
owl2neo4j http://protege.stanford.edu/ontologies/pizza/pizza.owl pizza.cypher
owl2neo4j http://downloads.dbpedia.org/2015-10/dbpedia_2015-10.owl dbpedia.cypher
```

## TODOs
this is a very first draft and there can be many cases where it fails e.g.
 * blank nodes are handled but, as always they do not look nice.
 * prefix handling can be improved to enhance readability. 
   This is a general purpose converter and in special cases prefixing can be redundant.
 * String escaping needs improvements to avoid syntax errors 
 * It is quite rough with datatypes, needs proper alignment
 * Inference is always tricky, this implementation does not perform any inferencing, this should be done as a prior step
