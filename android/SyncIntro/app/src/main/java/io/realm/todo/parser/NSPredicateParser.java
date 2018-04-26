package io.realm.todo.parser;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class NSPredicateParser <T extends RealmObject> {

    private String predicate;
    private ArrayList<RealmQueryPart> realmQueryParts;
    private Class<T> objectClass;
    private RealmQuery<T> realmQuery;
    private Realm realm;
    private EnumSet<NSPredicateEnum> predicateEnums;


    public NSPredicateParser(Realm realm, String predicate, Class<T> objectClass) {
        this.realm = realm;
        this.predicate = predicate;
        this.objectClass = objectClass;

        this.realmQueryParts = new ArrayList<>();
        this.realmQuery = realm.where(objectClass);
        this.predicateEnums = EnumSet.allOf(NSPredicateEnum.class);
    }

    public RealmQuery<T> parsePredicate() {
        String[] predicateParts = this.predicate.split("\\s+");
        Boolean lookingForPieces = false;
        String uncaughtPiece = "";
        NSPredicateEnum currentPartPredicateEnum = null;
        List currentPartVariables = null;
        int piecesToLookFor = 0;
        for (String predicatePart : predicateParts) {
            if (lookingForPieces) {
                Log.d("Piece we're looking for", predicatePart);
                lookingForPieces = false;
                currentPartVariables = parseAndCastVariables(uncaughtPiece, predicatePart);
                this.realmQueryParts.add(new RealmQueryPart(currentPartPredicateEnum, currentPartVariables));
            } else {
                NSPredicateEnum predicateEnum = knownCommand(predicatePart);
                if (predicateEnum != null) {
                    Log.d("Hey I know that guy", predicatePart);
                    currentPartPredicateEnum = predicateEnum;
                    if (predicateEnum.getPiecesNeeded() == 0) {
                        this.realmQueryParts.add(new RealmQueryPart(currentPartPredicateEnum, currentPartVariables));
                    } else {
                        lookingForPieces = true;
                    }
                } else {
                    Log.d("Uncaught Piece", predicatePart);
                    uncaughtPiece = predicatePart;
                }
            }
        }
        realmQuery = buildRealmQuery();
        return realmQuery;
    }

    private NSPredicateEnum knownCommand(String predicatePart) {
        NSPredicateEnum knownPredicateEnum = null;
        for (NSPredicateEnum predicateEnum : predicateEnums) {
            String[] predicateValues = predicateEnum.getPredicateValue();
            if (predicateValues[0].equals(predicatePart)) {
                knownPredicateEnum = predicateEnum;
                break; // I know using break practice is bad but who cares
            } else if (predicateValues.length > 1 && predicateValues[1].equals(predicatePart)) {
                knownPredicateEnum = predicateEnum;
                break; // Same here
            }
        }
        return knownPredicateEnum;
    }

    private RealmQuery<T> buildRealmQuery() {
        for (RealmQueryPart realmQueryPart : this.realmQueryParts) {
            switch(realmQueryPart.predicateEnum) {
                case EQUAL_TO: {
                    Object var0 = realmQueryPart.variables.get(0);
                    Object var1 = realmQueryPart.variables.get(1);
                    if (var0 instanceof String) {
                        if (var1 instanceof String) {
                            realmQuery.equalTo((String) var0, (String) var1);
                        } else if (var1 instanceof Boolean) {
                            realmQuery.equalTo((String) var0, (Boolean) var1);
                        }
                        // TODO: add the million other possibilities
                    } else {
                        Log.w("Failed Query Build", "failed in equal_to: " + realmQueryPart.toString());
                    }
                    break;
                }
                case NOT_EQUAL_TO: {

                }
                case IN: {

                }
                case GREATER_THAN: {

                }
                case LESS_THAN: {

                }
                case GREATER_THAN_OR_EQUAL_TO: {

                }
                case LESS_THAN_OR_EQUAL_TO: {

                }
                case AND: {
                    realmQuery.and();
                }
                case OR: {
                    realmQuery.or();
                }
                case NOT: {

                }
                case BEGINS_WITH: {

                }
                case ENDS_WITH: {

                }
                case CONTAINS: {

                }
                case LIKE: {

                }
                case ANY: {

                }
                case SOME: {

                }
                case BEGIN_GROUP: {

                }
                case END_GROUP: {

                }
                default: {
                    Log.d("BuildRealmQuery", "defaulted: " + realmQueryPart.toString());
                }
            }
        }
        return realmQuery;
    }

    private List parseAndCastVariables(String var0, String var1) {
        if (var1.startsWith("'") && var1.endsWith("'")) {
            var1 = var1.substring(1, var1.length()-1);
            return Arrays.asList(var0, var1);
        } else if (var1.startsWith("\"") && var1.endsWith("\"")) {
            var1 = var1.substring(1, var1.length()-1);
            return Arrays.asList(var0, var1);
        } else if (var1.equalsIgnoreCase("true")) {
            return Arrays.asList(var0, true);
        } else if (var1.equalsIgnoreCase("false")) {
            return Arrays.asList(var0, false);
        }
        return Arrays.asList(var0, var1);
    }

}
