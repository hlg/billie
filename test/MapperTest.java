import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class MapperTest extends MappingTestCase {
    @Before
    public void setUp(){
         super.setUp();
    }

    @Test
    public void testMostSpecific(){
        Object test = d;
        d.getClass();
    }

}
