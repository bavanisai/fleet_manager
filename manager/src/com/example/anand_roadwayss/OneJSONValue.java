package com.example.anand_roadwayss;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class OneJSONValue {
    String adress = new IpAddress().getIpAddress();

    public String jsonParsing1(String response) {
        String jsonData = null;
        String status = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);
                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                status = d.getString("status");
                return status;

            } catch (JSONException e) {

            }
        return status;
    }
}
