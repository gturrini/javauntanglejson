package org.javauntanglejson;

import java.util.Comparator;

class doCompare implements Comparator<jsonFinal> {
    @Override
    public int compare(jsonFinal o1, jsonFinal o2) {
        return o1.key.compareTo(o2.key);
    }
}
class jsonFinal {
    String key;
    Object value;

    jsonFinal(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}

class jsonProgress {
    String treatAs;
    String key;
    Object value;

    jsonProgress(String treatAs, String key, Object value) {
        this.treatAs = treatAs;
        this.key = key;
        this.value = value;
    }
}

class filter{
    /*
    ==
    !=
    <
    <=
    >
    >=
    like
    !like

    */
    String filterKey;
    String filterOperator;
    Object filterValue;
    filter(String filterKey, String filterOperator, Object filterValue) {
        this.filterKey = filterKey;
        this.filterOperator = filterOperator;
        this.filterValue = filterValue;
    }

}