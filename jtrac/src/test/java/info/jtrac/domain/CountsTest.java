package info.jtrac.domain;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class CountsTest extends TestCase {
    
    public void testCountsLogic() {
        Counts c = new Counts(false);
        c.add(Counts.ASSIGNED_TO_ME, 1, 5);        
        assertEquals(0, c.getTotal());
        assertEquals(5, c.getAssignedToMe());
    }    
}
