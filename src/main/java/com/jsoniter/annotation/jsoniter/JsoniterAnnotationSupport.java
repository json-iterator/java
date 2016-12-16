package com.jsoniter.annotation.jsoniter;

import com.jsoniter.Binding;
import com.jsoniter.EmptyExtension;
import com.jsoniter.JsonIterator;

public class JsoniterAnnotationSupport extends EmptyExtension {

    public static void enable() {
        JsonIterator.registerExtension(new JsoniterAnnotationSupport());
    }

    @Override
    public String[] getBindFrom(Binding field) {
        JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
        if (jsonIgnore != null) {
            return new String[0];
        }
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
            String alternativeField = jsonProperty.value();
            if (alternativeField.equals(JsonProperty.USE_DEFAULT_NAME)) {
                alternativeField = field.name;
            }
            return new String[]{alternativeField};
        }
        return null;
    }
}
