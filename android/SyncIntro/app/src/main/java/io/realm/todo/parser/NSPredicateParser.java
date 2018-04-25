package io.realm.todo.parser;

import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class NSPredicateParser <T extends RealmObject> {

    private String predicate;
    private ArrayList<RealmQueryPart> realmQueryParts;
    private Class<T> objectClass;
    private RealmQuery<T> realmQuery;
    private Realm realm;

    public NSPredicateParser(Realm realm, String predicate, Class<T> objectClass) {
        this.realm = realm;
        this.predicate = predicate;
        this.objectClass = objectClass;

        this.realmQueryParts = new ArrayList<>();
        this.realmQuery = realm.where(objectClass);
    }

    public RealmQuery<T> parsePredicate() {
        ArrayList<String> predicateParts = this.predicate.split("\\s+");
        Boolean lookingForPieces = false;
        int piecesToLookFor = 0;
        for (String predicatePart : predicateParts) {
            if (lookingForPieces) {

            } else if (knownCommand(predicatePart)) {

            } else {
                Log.d("Uncaught Piece", predicatePart);
            }
        }
    }

    private Boolean knownCommand(String predicatePart) {

    }

}
