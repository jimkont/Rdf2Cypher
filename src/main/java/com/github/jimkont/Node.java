package com.github.jimkont;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.jimkont.LabelUtils.*;

/**
 * @author Dimitris Kontokostas
 * @since 29/7/2016 11:11 μμ
 */
public class Node {

    private final Resource resource;
    private Node(Resource resource) {this.resource = resource;}
    public static Node create(Resource resource) {return new Node(resource);}

    public String getNodeWithType() {
        return getResourceLabelWithType(resource);
    }

    /**
     * return property <-> literal pairs, no relations here
     */
    public List<PropertyLiteral> getPropertyLiteralPairs(){
        List<PropertyLiteral> propertyLiteralPairs = new LinkedList<>();

        resource.listProperties().toList().stream()
                .filter( s -> s.getObject().isLiteral())
                .forEach( rwl -> {
                    Property predicate = rwl.getPredicate();
                    RDFNode object = rwl.getObject();
                    propertyLiteralPairs.add(
                            new PropertyLiteral(
                                    getPropertyLabel(predicate, object),
                                    getLiteralValue(object)));
                });

        propertyLiteralPairs.add(new PropertyLiteral("_id", "\""+getResourceLabel(resource)+"\""));
        propertyLiteralPairs.add(new PropertyLiteral("_uri", "\""+resource.getURI()+"\""));

        return propertyLiteralPairs;
    }

    /**
     * return relationships
     */
    public List<Relation> getRelationships(){

        return resource.listProperties().toList().stream()
                .filter( s -> s.getObject().isResource())
                .map( rwl -> {
                    String e1 = getResourceLabel(rwl.getSubject());
                    String e2 = getResourceLabel(rwl.getObject().asResource());
                    String prop = getPropertyLabel(rwl.getPredicate());

                    return new Relation(e1, prop, e2 );
                })
                .collect(Collectors.toList());
    }

    public final class PropertyLiteral {
        public final String property;
        public final String value;

        public PropertyLiteral(String property, String value) {
            this.property = property;
            this.value = value;
        }
    }

    public final class Relation {
        public final String e1;
        public final String prop;
        public final String e2;


        public Relation(String e1, String prop, String e2) {
            this.e1 = e1;
            this.prop = prop;
            this.e2 = e2;
        }
    }
}
