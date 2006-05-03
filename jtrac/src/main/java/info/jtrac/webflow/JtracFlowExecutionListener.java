package info.jtrac.webflow;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.webflow.RequestContext;
import org.springframework.webflow.State;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.EnterStateVetoException;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;

public class JtracFlowExecutionListener extends FlowExecutionListenerAdapter {
    
    protected final Log logger = LogFactory.getLog(getClass());
    
//    @Override
//    public void stateEntering(RequestContext context, State nextState) throws EnterStateVetoException {
//        if (nextState.getId().equals("itemSearchExportView")) {
//                throw new EnterStateVetoException(context.getCurrentState(), nextState, "Excel Export");
//        }
//    }
    
}
