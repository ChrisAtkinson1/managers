/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.zos3270.common.screens.json;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import dev.galasa.zos3270.common.screens.Terminal;

/**
 * Handles the writing of a terminal into a json string or json object.
 */
public class TerminalJsonTransform {

    private Gson gson ;

    public TerminalJsonTransform( boolean isPrettyPrinting ) {
        if( isPrettyPrinting ) {
            this.gson = new GsonBuilder().setPrettyPrinting().create();
        } else {
            this.gson = new GsonBuilder().create();
        }
    }
    
    public JsonObject toJsonObject(Terminal terminal) {
        JsonObject json = (JsonObject) gson.toJsonTree(terminal);
        return json;
    }
    
    public String toJsonString(Terminal terminal) {
        JsonObject jsonObj = toJsonObject(terminal);
        String jsonString = gson.toJson(jsonObj);
        return jsonString;
    }

    public Terminal toTerminal(String tempJson) {
        return gson.fromJson(tempJson, Terminal.class);
    }
}
