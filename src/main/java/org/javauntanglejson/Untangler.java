package org.javauntanglejson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Untangler {

    private static ArrayList<String> untangledJSON = new ArrayList<>();
    private static ArrayList<jsonFinal> finalJSON = new ArrayList<>();
    private static ArrayList<jsonFinal> finalJSONArray = new ArrayList<>();
    private static ArrayList<jsonProgress> progressJSON = new ArrayList<>();
    private static ArrayList<Integer> listProgressToRemove = new ArrayList<>();
    private static ArrayList<Integer> listArrayToRemove = new ArrayList<>();
    private static ArrayList<String> matchKeyValue = new ArrayList<>();

    public ArrayList<String> untangleWithFilter(String jsonString, filter fo) {
        ArrayList<String> res = new ArrayList<>();
        ArrayList<String> wip = untangle(jsonString);
        for (String jsonObject: wip) {
            String resTemp = filterString(jsonObject, fo);
            if (!resTemp.isEmpty())
                res.add(jsonObject);
        }
        return res;
    }

    private String filterString(String input, filter fo) {
        String res="";
        JSONObject jo = new JSONObject(input);
        switch(fo.filterOperator) {
            case "==":
                if (jo.get(fo.filterKey).toString().compareToIgnoreCase(fo.filterValue.toString())==0)
                    res = input;
                break;
            case "!=":
                if (jo.get(fo.filterKey).toString().compareToIgnoreCase(fo.filterValue.toString())!=0)
                    res = input;
                break;
            case "<":
                if (jo.get(fo.filterKey).toString().compareToIgnoreCase(fo.filterValue.toString())==-1)
                    res = input;
                break;
            case "<=":
                if ((jo.get(fo.filterKey).toString().compareToIgnoreCase(fo.filterValue.toString())==-1)||(jo.get(fo.filterKey).toString().compareToIgnoreCase(fo.filterValue.toString())==0))
                    res = input;
                break;
            case ">":
                if (jo.get(fo.filterKey).toString().compareToIgnoreCase(fo.filterValue.toString())==1)
                    res = input;
                break;
            case ">=":
                if ((jo.get(fo.filterKey).toString().compareToIgnoreCase(fo.filterValue.toString())==1)||(jo.get(fo.filterKey).toString().compareToIgnoreCase(fo.filterValue.toString())==0))
                    res = input;
                break;
            case "contains":
                if (jo.get(fo.filterKey).toString().contains(fo.filterValue.toString()))
                    res = input;
                break;
            case "!contains":
                if (!jo.get(fo.filterKey).toString().contains(fo.filterValue.toString()))
                    res = input;
                break;
            default:
                break;
        }
        return res;
    }

    public ArrayList<String> untangle(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        ArrayList<String>res = new ArrayList<>();
        init(json);
        while (!progressJSON.isEmpty()) {
            for (jsonProgress Progress : progressJSON) {
                if (Progress.value instanceof JSONObject) {
                    Nested(Progress.treatAs, Progress.key, Progress.value);
                    listProgressToRemove.add(progressJSON.indexOf(Progress));
                } else if (Progress.value instanceof JSONArray) {
                    Array(Progress.treatAs, Progress.key, Progress.value);
                    listProgressToRemove.add(progressJSON.indexOf(Progress));
                } else {
                    System.out.println("MAIN-ITEM");
                    if (!Progress.treatAs.equals("A")) {
                        finalJSON.add(new jsonFinal(Progress.key, Progress.value));
                        listProgressToRemove.add(progressJSON.indexOf(Progress));
                    }
                }
            }
            for (Integer pointer: listProgressToRemove) {
                progressJSON.set(pointer, null);
            }
            progressJSON.removeIf(Objects::isNull);
        }
        finalJSONArray.sort(new doCompare());

        //expand array matrix into untangledJSON array list (of array list) then add jsonFinal
        untangledJSON = arrayMatrix();
        for (int k=0; k<untangledJSON.size();k++){
            String rowString = "";
            String strSeparator = "";
            for (jsonFinal jsonFinal:finalJSON ) {
                if (!rowString.isEmpty())
                    strSeparator = ",";
                rowString = rowString + strSeparator + jsonRow(jsonFinal.key, jsonFinal.value);
            }
            res.add("{" + untangledJSON.get(k) + "," + rowString + "}");
        }
        return res;
    }

    private static boolean evalKeyValue(String key1, Object value1, String key2, Object value2) {
        boolean res = false;
        if (key1.equals(key2)) {
            res = false;
        } else if (matchKeyValue.contains(key1 + "___" + value1 + "___" + key2 + "___" + value2)) {
            res = false;
        } else if (matchKeyValue.contains(key2 + "___" + value2 + "___" + key1 + "___" + value1)) {
            res = false;
        } else if (!key1.equals(key2)) {
            res = true;
        }
        return res;
    }
    private static ArrayList<String> arrayMatrix() {
        ArrayList<String> jsonRows = new ArrayList<>();
        ArrayList<String> processedKeys = new ArrayList<>();
        ArrayList<String> arrayKeys = new ArrayList<>();
        for (jsonFinal finalJSON: finalJSONArray) { arrayKeys.add(finalJSON.key); }
        List<String> uniqueKeys = arrayKeys.stream().distinct().collect(Collectors.toList());
        for (int y = 0; y < finalJSONArray.size(); y++) {
            if (finalJSONArray.get(y).key.equals(uniqueKeys.get(0))) {
                String rowString = "";
                String strSeparator = "";
                if (finalJSONArray.get(y).value instanceof JSONObject) {
                    for (String finalKey : ((JSONObject) finalJSONArray.get(y).value).keySet()) {
                        Object finalValue = ((JSONObject) finalJSONArray.get(y).value).get(finalKey);
                        if (!rowString.isEmpty())
                            strSeparator = ",";
                        rowString = rowString + strSeparator + jsonRow(finalJSONArray.get(y).key + "." + finalKey, finalValue);
                    }
                } else {
                    if (!rowString.isEmpty())
                        strSeparator = ",";
                    rowString = rowString + strSeparator + jsonRow(finalJSONArray.get(y).key, finalJSONArray.get(0).value);
                }
                jsonRows.add(rowString);
            }
        }
        for (int x = 1; x < uniqueKeys.size(); x++) {
            ArrayList<String> wipRows = new ArrayList<>();
            for (int y = 0; y < finalJSONArray.size(); y++) {
                if (finalJSONArray.get(y).key.equals(uniqueKeys.get(x))) {
                    String rowString = "";
                    String strSeparator = "";
                    if (finalJSONArray.get(y).value instanceof JSONObject) {
                        for (String finalKey : ((JSONObject) finalJSONArray.get(y).value).keySet()) {
                            Object finalValue = ((JSONObject) finalJSONArray.get(y).value).get(finalKey);
                            if (!rowString.isEmpty())
                                strSeparator = ",";
                            rowString = rowString + strSeparator + jsonRow(finalJSONArray.get(y).key + "." + finalKey, finalValue);
                        }
                    } else {
                        if (!rowString.isEmpty())
                            strSeparator = ",";
                        rowString = rowString + strSeparator + jsonRow(finalJSONArray.get(y).key, finalJSONArray.get(y).value);
                    }
                    for (int j = 0 ; j < jsonRows.size(); j++) {
                        wipRows.add(jsonRows.get(j) + "," + rowString);
                    }
                }
            }
            jsonRows.clear();
            jsonRows = wipRows;
        }
        return jsonRows;
    }
    private static ArrayList<String> arrayMatrix1() {
        ArrayList<String> jsonRows = new ArrayList<>();
        for (int x = 0; x < finalJSONArray.size(); x++) {
            for (int y = 0; y < finalJSONArray.size(); y++) {
                String rowString = "";
                String strSeparator = "";
                if (evalKeyValue(finalJSONArray.get(x).key, finalJSONArray.get(x).value, finalJSONArray.get(y).key, finalJSONArray.get(y).value)) {
                    if (finalJSONArray.get(x).value instanceof JSONObject) {
                        for (String finalKey : ((JSONObject) finalJSONArray.get(x).value).keySet()) {
                            Object finalValue = ((JSONObject) finalJSONArray.get(x).value).get(finalKey);
                            if (!rowString.isEmpty())
                                strSeparator = ",";
                            rowString = rowString + strSeparator + jsonRow(finalJSONArray.get(x).key + "." + finalKey, finalValue);
                        }
                    } else {
                        if (!rowString.isEmpty())
                            strSeparator = ",";
                        rowString = rowString + strSeparator + jsonRow(finalJSONArray.get(x).key, finalJSONArray.get(x).value);
                    }
                    if (finalJSONArray.get(y).value instanceof JSONObject) {
                        for (String finalKey : ((JSONObject) finalJSONArray.get(y).value).keySet()) {
                            Object finalValue = ((JSONObject) finalJSONArray.get(y).value).get(finalKey);
                            if (!rowString.isEmpty())
                                strSeparator = ",";
                            rowString = rowString + strSeparator + jsonRow(finalJSONArray.get(y).key + "." + finalKey, finalValue);
                        }
                    } else {
                        if (!rowString.isEmpty())
                            strSeparator = ",";
                        rowString = rowString + strSeparator + jsonRow(finalJSONArray.get(y).key, finalJSONArray.get(y).value);
                    }
                    jsonRows.add(rowString);
                    matchKeyValue.add(finalJSONArray.get(x).key + "___" + finalJSONArray.get(x).value + "___" + finalJSONArray.get(y).key + "___" + finalJSONArray.get(y).value);
                }
            }
        }
        return jsonRows;
    }
    private static String jsonRow(String key, Object value) {
            String res="";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(key, value);
            res=jsonObject.toString();
            res=res.replace("{","").replace("}","");
            return res;
    }
    private static void init(JSONObject json) {
        System.out.println("INIT");
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value instanceof JSONObject) {
                progressJSON.add(new jsonProgress("N", key, value));
            } else if (value instanceof JSONArray) {
                progressJSON.add(new jsonProgress("A", key, value));
            } else {
                progressJSON.add(new jsonProgress("I", key, value));
            }
        }
    }
    private static void Nested(String treatAs, String key, Object value) {
        System.out.print("NESTED");
        for (String nestedKey : ((JSONObject) value).keySet()) {
            Object nestedValue = ((JSONObject) value).get(nestedKey);
            if (nestedValue instanceof JSONObject) {
                if (treatAs.equals("A")) {
                    progressJSON.add(new jsonProgress("A", key + "." + nestedKey, value));
                } else {
                    progressJSON.add(new jsonProgress("N", key + "." + nestedKey, value));
                }
            } else if (nestedValue instanceof JSONArray) {
                Array( treatAs,  key + "." + nestedKey,  nestedValue);
            } else {
                if (!treatAs.equals("A")) {
                    finalJSON.add(new jsonFinal(key + "." + nestedKey, value));
                } else {
                    finalJSONArray.add(new jsonFinal(key + "." + nestedKey, nestedValue));
                }
            }
        }
    }
    private static void Array(String treatAs, String key, Object value) {
        System.out.println("ARRAY");
        for (Object arrayValue : (JSONArray) value) {
            if (arrayValue instanceof JSONObject) {
                if (containsNestedOrArray(arrayValue))
                    Nested("A", key, arrayValue);
                else
                    finalJSONArray.add(new jsonFinal(key, arrayValue));
            } else if (arrayValue instanceof JSONArray) {
                Array("A", key, arrayValue);
            } else {
                finalJSONArray.add(new jsonFinal(key, arrayValue));
            }
        }
    }
    private static boolean containsNestedOrArray(Object value) {
        boolean res=false;
        for (String key : ((JSONObject) value).keySet()) {
            Object item = ((JSONObject) value).get(key);
            if (item instanceof JSONObject) {
                res = true;
                break;
            } else if (item instanceof JSONArray) {
                res = true;
                break;
            }
        }
        return res;
    }

}