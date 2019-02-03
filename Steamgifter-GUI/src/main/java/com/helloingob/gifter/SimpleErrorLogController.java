package com.helloingob.gifter;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;

import com.helloingob.gifter.to.ErrorLogTO;

public class SimpleErrorLogController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;
    private Label lblContent;

    private ErrorLogTO errorLog;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init() {
        errorLog = (ErrorLogTO) arg.get("errorLog");
        lblContent.setValue(errorLog.getMessage());
    }

    public void onClick$btnMessage() {
        lblContent.setValue(errorLog.getMessage());
    }

    public void onClick$btnValue() {
        lblContent.setValue(errorLog.getValue());
    }

}
