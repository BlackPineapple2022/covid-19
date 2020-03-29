package by.academy.it.covid19informer.util;

import org.json.JSONObject;

import java.util.Map;

public interface JSONProvider {

    JSONObject getJSON(String url, Map<String, String> headers);

}
