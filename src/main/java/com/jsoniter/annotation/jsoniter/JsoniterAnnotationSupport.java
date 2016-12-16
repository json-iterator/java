package com.jsoniter.annotation.jsoniter;

import com.jsoniter.Binding;
import com.jsoniter.EmptyExtension;
import com.jsoniter.Jsoniter;

public class JsoniterAnnotationSupport extends EmptyExtension {

    public static void enable() {
        Jsoniter.registerExtension(new JsoniterAnnotationSupport());
    }

    @Override
    public String[] getBindFrom(Binding field) {
        JsonIgnore jsonIgnore = field.field.getAnnotation(JsonIgnore.class);
        if (jsonIgnore != null) {
            return new String[0];
        }
        JsonProperty jsonProperty = field.field.getAnnotation(JsonProperty.class);
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
