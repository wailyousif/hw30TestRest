package com.ironyard.dto;

import com.ironyard.data.Game;

/**
 * Created by wailm.yousif on 3/8/17.
 */
public class ResponseObject
{
    private boolean success;
    private int responseCode;
    private String responseString;
    private Game game;

    public ResponseObject() { }

    /*
    public ResponseObject(boolean success, int responseCode, String responseString) {
        this.success = success;
        this.responseCode = responseCode;
        this.responseString = responseString;
    }
    */

    public ResponseObject(boolean success, int responseCode, String responseString, Game game) {
        this.success = success;
        this.responseCode = responseCode;
        this.responseString = responseString;
        this.game = game;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}