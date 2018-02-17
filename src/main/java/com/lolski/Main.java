package com.lolski;

import ai.grakn.Grakn;
import ai.grakn.GraknSession;
import ai.grakn.GraknTx;
import ai.grakn.GraknTxType;
import ai.grakn.concept.Concept;
import ai.grakn.graql.admin.Answer;

import java.util.HashMap;
import java.util.stream.Stream;

import static ai.grakn.graql.Graql.*;

public class Main {
    public static void main(String[] args) {
        GraknSession session = Grakn.session(Grakn.IN_MEMORY, "grakn");
        try (GraknTx tx = session.open(GraknTxType.WRITE)) {
//            tx.graql().define(label("person").sub("entity")).execute();
//            tx.graql().insert(var().isa("person")).execute();
            tx.commit();
        }

        try (GraknTx tx = session.open(GraknTxType.READ)) {
            Stream<Answer> thing = tx.graql().match(var("x").isa("thing")).get().stream();
            thing.map(e -> {
                Concept c = e.get("x");
                String typeOf;
                if (c.isType()) {
                    typeOf = "[" + c.asType().getLabel().getValue() + "/" + c.asType().getLabelId().getValue() + "]";
                }
                else if (c.isEntity()) {
                    typeOf = "[" + c.asEntity().type().getLabel().getValue() + "/" + c.asEntity().type().getLabelId().getValue() + "]";
                }
                else if (c.isAttribute()) {
                    typeOf = "[" + c.asAttribute().type().getLabel().getValue() + "/" + c.asAttribute().type().getLabelId().getValue() + "]";
                }
                else if (c.isRelationship()) {
                    typeOf = "[" + c.asRelationship().type().getLabel().getValue() + "/" + c.asRelationship().type().getLabelId().getValue() + "]";
                }
                else if (c.isRole()) {
                    typeOf = "[" + c.asRole().getLabel().getValue() + "/" + c.asRole().getLabelId().getValue() + "]";
                }
                else if (c.isRule()) {
                    typeOf = "[" + c.asRule().getLabel().getValue() + "/" + c.asRule().getLabelId().getValue() + "]";
                }
                else {
                    throw new RuntimeException();
                }
                return new HashMap.SimpleImmutableEntry<>(typeOf, c.getId().getValue());
            }).forEach(System.out::println);
        }

        System.out.println(); // ((GraknTxTinker) ((GraknSessionImpl) session).tx).rootGraph.vertices
    }
}
