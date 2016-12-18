package com.jsoniter;

import com.jsoniter.spi.Binding;
import com.jsoniter.spi.ClassDescriptor;
import com.jsoniter.spi.ConstructorDescriptor;
import com.jsoniter.spi.EmptyExtension;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestCustomizeCtor extends TestCase {

    static {
//        JsonIterator.enableStrictMode();
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
            public void updateClassDescriptor(ClassDescriptor desc) {
                if (desc.clazz == WithPublicCtor.class) {
                    desc.ctor = new ConstructorDescriptor() {{
                        parameters = (List) Arrays.asList(new Binding() {{
                            fromNames = new String[]{"param1"};
                            name="field1";
                            valueType = String.class;
                        }});
                    }};
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
            public void updateClassDescriptor(ClassDescriptor desc) {
                if (desc.clazz == WithPrivateCtor.class) {
                    desc.ctor = new ConstructorDescriptor() {{
                        parameters = (List) Arrays.asList(new Binding() {{
                            fromNames = new String[]{"param1"};
                            name="param1";
                            valueType = String.class;
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
