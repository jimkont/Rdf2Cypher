# Owl2Neo4j
An experimental OWL/RDF to Neo4j converter

This is a general (experimental) OWL/RDF to Neo4j converter. The program takes as input an RDF graph and returns a cypher file

for example the following RDF file
```
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

<http://dbpedia.org/ontology/Person> a owl:Class ;
    rdfs:label "Person"@en ;
    rdfs:label "Person"@en ;
    owl:equivalentClass <http://schema.org/Person> .

<https://github.com/jimkont> a <http://dbpedia.org/ontology/Person> .
```
Generates the following cypher script

```
CREATE (dbpediaowl_Person:owl_Class {name:"dbpediaowl_Person", uri:"http://dbpedia.org/ontology/Person", rdfs_label_en:"Person" })
CREATE (schema_Person {name:"schema_Person", uri:"http://schema.org/Person" })
CREATE (p312307766_jimkont:dbpediaowl_Person:owl_Class {name:"p312307766_jimkont", uri:"https://github.com/jimkont" })
CREATE (owl_Class {name:"owl_Class", uri:"http://www.w3.org/2002/07/owl#Class" })
CREATE
(dbpediaowl_Person)-[:owl_equivalentClass]->(schema_Person),
(dbpediaowl_Person)-[:rdf_type]->(owl_Class),
(p312307766_jimkont)-[:rdf_type]->(dbpediaowl_Person)
```

## How it works
For every non-Literal RDF node a new Node is created. 
To avoid name clashes the program names the nodes with the local name of the URI and a prefix: `prefix_localName`.
For the prefix we try the local prefix declarations in the RDF graph (if they exist, e.g. in turtle or n3) or consolidate with the LOV service.
If no prefix is found, a numeric based prefix is used.

Whenever there is an `rdf:type` statement the program tries to assign the class to the node e.g. `jimkont:Person`.
For every node we havee 2 additional values, a `label` that matches the node name and a `uri` with the full URI.

we follow the same approach for naming the property labels, e.g. `-[:rdf_type]->`

## How to run it
Using maven use
```
owl2neo4j <owl/rdf uri> <outputfile>

e.g.
owl2neo4j http://protege.stanford.edu/ontologies/pizza/pizza.owl pizza.cypher
owl2neo4j http://downloads.dbpedia.org/2015-10/dbpedia_2015-10.owl dbpedia.cypher
```

## TODOs
this is a very first draft and there can be many cases where it fails e.g.
 * blank nodes are handles but, as always they do not look nice.
 * When a node has multiple types, we need move clever consolidation.
 * prefix handling can be improved to enhance readability. 
   This is a general purpose converter and in special cases prefixing can be redundant.
 * String escaping needs improvements for proper 