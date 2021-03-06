package com.haskforce.parsing.jsonParser;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.haskforce.parsing.srcExtsDatatypes.AssocLeft;
import com.haskforce.parsing.srcExtsDatatypes.AssocNone;
import com.haskforce.parsing.srcExtsDatatypes.AssocRight;
import com.haskforce.parsing.srcExtsDatatypes.AssocTopType;
import com.haskforce.parsing.srcExtsDatatypes.SrcInfoSpan;

import java.lang.reflect.Type;

/**
 * Deserializes Assocs.
 */
public class AssocTopTypeDeserializer implements JsonDeserializer<AssocTopType> {
    @Override
    public AssocTopType deserialize(JsonElement jsonElement, Type type,
                                     JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject objType = jsonElement.getAsJsonObject();
        JsonObject stuff;
        if ((stuff = objType.getAsJsonObject("AssocNone")) != null) {
            AssocNone assocNone = new AssocNone();
            assocNone.srcInfoSpan = jsonDeserializationContext.deserialize(stuff.get("srcInfoSpan"), SrcInfoSpan.class);
            return assocNone;
        } else if ((stuff = objType.getAsJsonObject("AssocLeft")) != null) {
            AssocLeft assocLeft = new AssocLeft();
            assocLeft.srcInfoSpan = jsonDeserializationContext.deserialize(stuff.get("srcInfoSpan"), SrcInfoSpan.class);
            return assocLeft;
        } else if ((stuff = objType.getAsJsonObject("AssocRight")) != null) {
            AssocRight assocRight = new AssocRight();
            assocRight.srcInfoSpan = jsonDeserializationContext.deserialize(stuff.get("srcInfoSpan"), SrcInfoSpan.class);
            return assocRight;
        }
        throw new JsonParseException("Unexpected JSON object type: " + objType.toString());
    }
}
