package com.helloingob.gifter.utilities;

import com.helloingob.gifter.dao.DevDAO;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.utilities.mail.MailClient;

public class PollWatchDog {

    public static void checkLastPolls() {
        Integer lastPolls = new DevDAO().getLatestTwoPollingsCount();
        if (lastPolls != null && lastPolls <= CentralSettings.Developer.WATCHDOG_LATEST_POLLS_COUNT) {
            AdvUserTO user = new AdvUserTO();
            user.setLoginName("Administrator");
            user.setNotificationEmail("Admins");
            new MailClient(user).notifyAdmins("Last polls: '" + lastPolls + "'");
        }
    }

}
