package com.helloingob.gifter;

import java.util.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;

import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.TimeHelper;
import com.helloingob.gifter.utilities.manager.NotificationManager;
import com.helloingob.gifter.utilities.manager.SessionManager;

public class LoginController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;

    private Textbox tbxName;
    private Textbox tbxPw;

    private UserDAO userDao;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        userDao = new UserDAO();
    }

    public void onClick$btnLogin() {
        Optional<UserTO> optUser = userDao.login(tbxName.getText(), DigestUtils.sha1Hex(tbxPw.getText()));
        if (optUser.isPresent()) {
            UserTO user = optUser.get();
            SessionManager.setUser(user);
            user.setLastLogin(TimeHelper.getCurrentTimestamp());
            userDao.update(user);
            Events.postEvent(GUISettings.LOGIN_EVENT, this.self, null);
            this.self.detach();
        } else {
            NotificationManager.showWarning("Wrong Logininformation", this.self, NotificationManager.BEFORE_CENTER);
        }
    }

    public void onOK$tbxPw() {
        onClick$btnLogin();
    }

    public void onOK$tbxName() {
        onClick$btnLogin();
    }
}
