package de.tudresden.cib.vis.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class CollectionAccessorTests {
    @Test
    public void testSingletonCollection(){
        CollectionAccessor<String> collectionAccessor = new CollectionAccessor<String>(Collections.singleton("abc"));
        Iterable<? extends String> result = collectionAccessor.filter(collectionAccessor.getDefaultCondition());
        int count = 0;
        for(String resultElement: result){ count++; }
        Assert.assertEquals(1, count);
    }
}
