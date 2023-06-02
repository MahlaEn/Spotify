package org.example;

import org.json.JSONObject;

import java.io.Serializable;

public class Request implements Serializable{
    private JSONObject json;
    public JSONObject getJson() {
        return json;
    }
    public void setJson(JSONObject json) {
        this.json = json;
    }
}
