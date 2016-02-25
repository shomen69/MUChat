package com.shomen.MUChat.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by server on 1/20/2016.
 */
public class ServerResponse {

    private String status;
    private String message;

    private List<Results> results = new ArrayList<>();

    public ServerResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "MyResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", results=" + results +
                '}';
    }
}
