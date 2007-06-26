package info.jtrac.wicket.yui;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class YuiUtilsTest extends TestCase {
    
    public void testJsonConversion() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", true);
        map.put("bar", false);        
        assertEquals("{foo : true, bar : false}", YuiUtils.getJson(map));
    }
    
}
