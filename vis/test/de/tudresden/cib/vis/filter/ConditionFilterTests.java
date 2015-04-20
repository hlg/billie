package de.tudresden.cib.vis.filter;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

public class ConditionFilterTests {

    @Test
    public void testFilter(){
        ConditionFilter<String> filter = new ConditionFilter<String>();
        Collection<String> collection = Collections.singleton("abc");
        int count = 0;
        for ( String element : filter.filter(new Condition<String>() {
            @Override
            public boolean matches(String data) {
                return true;
            }
        }, collection)){
            count ++;
        }
        Assert.assertEquals(1, count);
    }
}
