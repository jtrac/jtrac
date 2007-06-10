package info.jtrac.util;

import junit.framework.TestCase;

public class ItemUtilsTest extends TestCase {
    
    public void testHtmlEscaping() {
        assertEquals("&nbsp;&nbsp;&nbsp;&nbsp;", ItemUtils.fixWhiteSpace("    "));
        assertEquals("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", ItemUtils.fixWhiteSpace(" \t"));
        assertEquals("Hello World", ItemUtils.fixWhiteSpace("Hello World"));
        assertEquals("", ItemUtils.fixWhiteSpace(""));
        assertEquals("", ItemUtils.fixWhiteSpace(null));
        assertEquals("Hello<br/>World", ItemUtils.fixWhiteSpace("Hello\nWorld"));
        assertEquals("Hello<br/>&nbsp;&nbsp;World", ItemUtils.fixWhiteSpace("Hello\n  World"));
        assertEquals("Hello<br/>&nbsp;World<br/>&nbsp;&nbsp;&nbsp;&nbsp;Everyone", ItemUtils.fixWhiteSpace("Hello\n World\n\tEveryone"));
        assertEquals("Hello&nbsp;&nbsp;&nbsp;&nbsp;World", ItemUtils.fixWhiteSpace("Hello\tWorld"));
    }
    
}
