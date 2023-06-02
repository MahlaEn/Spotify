package Shared;

import org.json.JSONObject;

import java.io.Serializable;

public class Response implements Serializable {
    private JSONObject json;
    public JSONObject getJson() {
        return json;
    }
    public void setJson(JSONObject json) {
        this.json = json;
    }
}