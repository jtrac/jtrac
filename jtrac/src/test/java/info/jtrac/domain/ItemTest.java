package info.jtrac.domain;

import info.jtrac.domain.Item;
import junit.framework.TestCase;

public class ItemTest extends TestCase {

    public void testSetAndGetForCustomInteger() {
        Item item = new Item();
        item.setCusInt01(5);
        assertEquals(item.getCusInt01().intValue(), 5);
    }
    
}
