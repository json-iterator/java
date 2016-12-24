package com.jsoniter;

import com.jsoniter.spi.*;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestCustomizeCtor extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
    }

    public static class WithPublicCtor {
        String field1;

        public WithPublicCtor(String param1) {
            field1 = param1;
        }
    }

    public void test_codegen() throws IOException {
        ExtensionManager.registerExtension(new EmptyExtension() {
            @Override
            public void updateClassDescriptor(final ClassDescriptor desc) {
                if (desc.clazz == WithPublicCtor.class) {
                    desc.ctor = new ConstructorDescriptor() {{
                        parameters = (List) Arrays.asList(new Binding(desc.clazz, desc.lookup, String.class) {{
                            fromNames = new String[]{"param1"};
                            name="field1";
                        }});
                    }};
                    try {
                        desc.ctor.ctor = desc.clazz.getConstructor(String.class);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        JsonIterator iter = JsonIterator.parse("{'param1': 'hello'}".replace('\'', '"'));
        WithPublicCtor obj = iter.read(WithPublicCtor.class);
        assertEquals("hello", obj.field1);
    }

    public static class WithPrivateCtor {
        String field1;

        private WithPrivateCtor(String param1) {
            field1 = param1;
        }
    }

    public void test_reflection() throws IOException {
        ExtensionManager.registerExtension(new EmptyExtension() {
            @Override
            public void updateClassDescriptor(final ClassDescriptor desc) {
                if (desc.clazz == WithPrivateCtor.class) {
                    desc.ctor = new ConstructorDescriptor() {{
                        parameters = (List) Arrays.asList(new Binding(desc.clazz, desc.lookup, String.class) {{
                            fromNames = new String[]{"param1"};
                            name="param1";
                        }});
                        ctor = WithPrivateCtor.class.getDeclaredConstructors()[0];
                    }};
                }
                for (Binding field : desc.allDecoderBindings()) {
                    if (field.clazz == WithPrivateCtor.class && "field1".equals(field.name)) {
                        field.fromNames = new String[0];
                    }
                }
            }
        });
        ExtensionManager.registerTypeDecoder(WithPrivateCtor.class, new ReflectionDecoder(WithPrivateCtor.class));
        JsonIterator iter = JsonIterator.parse("{'param1': 'hello'}".replace('\'', '"'));
        WithPrivateCtor obj = iter.read(WithPrivateCtor.class);
        assertEquals("hello", obj.field1);
    }
}
