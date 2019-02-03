package com.helloingob.gifter.handler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.data.connection.HTTPConnection;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.parser.SuspensionParser;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;
import com.helloingob.gifter.utilities.mail.MailClient;

public class SuspensionHandler {

    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private AdvUserTO user;

    public SuspensionHandler(AdvUserTO user) {
        this.user = user;
    }

    public void handleSuspension() {
        String suspensionNotification = SuspensionParser.getSuspensionComment(getSuspensionPageSource());
        new MailClient(user).sendSuspensionEmail(suspensionNotification);
        new ErrorLogHandler(user).error(user.getLoginName() + " has been suspended!", suspensionNotification, logger);
        user.setIsActive(false);
        new UserDAO().update(user);
    }

    private String getSuspensionPageSource() {
        HttpResponse response = new HTTPConnection(user.getUserAgent()).doGet(CentralSettings.Url.SUSPENSION_URL, user.getPhpsessionid());
        if (response != null) {
            try {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, logger);
            }
        }
        return null;
    }

}
