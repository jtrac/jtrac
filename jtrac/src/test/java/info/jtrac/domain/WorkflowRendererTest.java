package info.jtrac.domain;

import info.jtrac.util.XmlUtils;
import junit.framework.TestCase;

public class WorkflowRendererTest extends TestCase {
    
    public void testXmlConversion() {
        Metadata m = new Metadata();
        m.initRoles();
        Role r = m.getRoles().get("DEFAULT");
        WorkflowRenderer wr = new WorkflowRenderer(r, m.getStates());
        System.out.println(XmlUtils.getAsPrettyXml(wr.getDocument()));
    }    
}
