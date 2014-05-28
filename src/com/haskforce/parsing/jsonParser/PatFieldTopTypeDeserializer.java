package com.haskforce.parsing.jsonParser;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.haskforce.parsing.srcExtsDatatypes.*;

import java.lang.reflect.Type;

/**
 * Deserializes pattern fields.
 */
public class PatFieldTopTypeDeserializer implements JsonDeserializer<PatFieldTopType> {
    @Override
    public PatFieldTopType deserialize(JsonElement jsonElement, Type type,
                                     JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject objType = jsonElement.getAsJsonObject();
        JsonArray stuff;
        if ((stuff = objType.getAsJsonArray("PFieldPat")) != null) { // TODO: Test.
            PFieldPat pFieldPat = new PFieldPat();
            pFieldPat.srcInfoSpan = jsonDeserializationContext.deserialize(stuff.get(0), SrcInfoSpan.class);
            pFieldPat.qName = jsonDeserializationContext.deserialize(stuff.get(1), QNameTopType.class);
            pFieldPat.pat = jsonDeserializationContext.deserialize(stuff.get(2), PatTopType.class);
            return pFieldPat;
        } else if ((stuff = objType.getAsJsonArray("PFieldPun")) != null) { // TODO: Test.
            PFieldPun pFieldPun = new PFieldPun();
            pFieldPun.srcInfoSpan = jsonDeserializationContext.deserialize(stuff.get(0), SrcInfoSpan.class);
            pFieldPun.name = jsonDeserializationContext.deserialize(stuff.get(1), NameTopType.class);
            return pFieldPun;
        } else if ((stuff = objType.getAsJsonArray("PFieldWildcard")) != null) { // TODO: Test.
            PFieldWildcard pFieldWildcard = new PFieldWildcard();
            pFieldWildcard.srcInfoSpan = jsonDeserializationContext.deserialize(stuff.get(0), SrcInfoSpan.class);
            return pFieldWildcard;
        }
        throw new JsonParseException("Unexpected JSON object type: " + objType.toString());
    }
}
