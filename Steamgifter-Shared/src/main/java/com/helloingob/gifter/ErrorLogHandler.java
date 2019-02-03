package com.helloingob.gifter;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.dao.ErrorLogDAO;
import com.helloingob.gifter.to.ErrorLogTO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.utilities.TimeHelper;

public class ErrorLogHandler {

    private UserTO user;

    public ErrorLogHandler(UserTO user) {
        this.user = user;
    }

    public ErrorLogHandler() {
        this.user = null;
    }

    private void createEntry(String message, String value) {
        ErrorLogTO errorLog = new ErrorLogTO();
        errorLog.setDate(TimeHelper.getCurrentTimestamp());
        if (user != null) {
            errorLog.setUserPk(this.user.getPk());
        } else {
            errorLog.setUserPk(null);
        }
        errorLog.setMessage(message);
        errorLog.setValue(value);
        new ErrorLogDAO().save(errorLog);
    }

    private String stackTraceToString(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public void error(Exception exception, Logger logger) {
        createEntry(stackTraceToString(exception), null);
        logger.error(exception, exception);
    }

    public void error(Exception exception, String value, Logger logger) {
        createEntry(stackTraceToString(exception), value);
        logger.error(exception + "\n\n" + value, exception);
    }

    public void error(String message, String value, Logger logger) {
        createEntry(message, value);
        logger.error(message + ": '" + value + "'");
    }

    public void error(String message, Logger logger) {
        createEntry(message, null);
        logger.error(message);
    }

    //DEBUG
    public void writeFile(Exception exception, Logger logger) {
        logger.error(exception, exception);
    }

    public void writeFile(String message, Logger logger) {
        logger.error(message);
    }

}
