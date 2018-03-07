package com.jsoniter.spi;

import junit.framework.TestCase;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;

import static org.junit.Assert.*;

public class TestConfig extends TestCase {

    public void test_Setter_True(){
        // Contract: test the ClassDescriptor desc to have its setters as new String[0].
        // Pass test only if the all setters are set to new String[0].

        ClassInfo classInfo = new ClassInfo(int.class); // Try with Object.type

        ClassDescriptor desc = ClassDescriptor.getDecodingClassDescriptor(classInfo, true);

        Config conf = new Config(null, null);
        for (Binding binding : desc.allBindings()) {
            for (Binding setter : desc.setters) {
                setter.name = binding.field.getName();
            }
        }

        conf.updateBindings_helper(desc);

        for (Binding setter : desc.setters) {
            String[] s = new String[0];
            assertArrayEquals(s, setter.fromNames);
            assertArrayEquals(s, setter.toNames);
        }
    }

    public void test_Setter_False(){
        // Contract: test the ClassDescriptor desc to NOT have its setters as new String[0].
        // Pass test only if the all setters are NOT set to new String[0].

        ClassInfo classInfo = new ClassInfo(int.class); // Try with Object.type

        ClassDescriptor desc = ClassDescriptor.getDecodingClassDescriptor(classInfo, true);

        Config conf = new Config(null, null);
        //for (Binding binding : desc.allBindings()) {
            for (Binding setter : desc.setters) {
                setter.name = "cow";
            }
        //}

        conf.updateBindings_helper(desc);

        for (Binding setter : desc.setters) {
            String[] s = new String[0];
            assertThat(s, IsNot.not(IsEqual.equalTo(setter.fromNames)));
            assertThat(s, IsNot.not(IsEqual.equalTo(setter.toNames)));
        }
    }

}