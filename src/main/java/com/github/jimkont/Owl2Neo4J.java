package com.github.jimkont;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dimitris Kontokostas
 * @since 29/7/2016 5:11 μμ
 */
public class Owl2Neo4J {
    private final OntModel model;
    private Owl2Neo4J(OntModel model) {this.model = model;}
    public static Owl2Neo4J create(OntModel model) {return new Owl2Neo4J(model);}

    public List<String> generate() {

        // cypher output line by line
        List<String> cypherStatements = new LinkedList<>();


        // index all resources
        Set<Resource> resources = new HashSet<>();
        model.listStatements().toList()
                .forEach( statement -> {
                    resources.add(statement.getSubject());
                    RDFNode object = statement.getObject();
                    if (object.isResource()) {
                        resources.add(object.asResource());
                    }
                });

        List<Node> nodes = resources.stream().map(Node::create).collect(Collectors.toList());

        // For every resource get literal statements to create Nodes
        nodes.forEach(node -> {
            // Node with Type

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("CREATE (").append(node.getNodeWithType()).append(" {");

            // get literal values
            String propertyValues = node.getPropertyLiteralPairs().stream()
                    .map(pv -> pv.property + ":" + pv.value)
                    .collect(Collectors.joining(", "));

            stringBuilder.append(propertyValues);

            stringBuilder.append(" })\n");
            cypherStatements.add(stringBuilder.toString());
        });

        cypherStatements.add("CREATE");

        cypherStatements.add(
                nodes.stream().flatMap(r -> r.getRelationships().stream())
                    .map(r -> "(" + r.e1 + ")-[:" + r.prop + "]->(" + r.e2 + ")")
                    .collect(Collectors.joining(",\n")));


        return cypherStatements;
    }



}
