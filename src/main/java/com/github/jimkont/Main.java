package com.github.jimkont;

import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.FileWriter;
import java.util.List;

/**
 * @author Dimitris Kontokostas
 * @since 29/7/2016 6:36 μμ
 */
public class Main {

    private Main() {}

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Usage: $ owl2Neo <URI> <cypher output location>");
            System.exit(0);
        }

        String ontologyUri = args[0];
        String outputLocation = args[1];

        // TODO specify reasoning
        //OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RULES_INF);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        RdfReaderFactory.createResourceOrFileOrDereferenceReader(ontologyUri).read(model);

        // generate cypher statements
        List<String> cypherStatements = Owl2Neo4J.create(model).generate();

        FileWriter writer = new FileWriter(outputLocation);
        for(String str: cypherStatements) {
            writer.write(str);
        }
        writer.close();

    }
}
