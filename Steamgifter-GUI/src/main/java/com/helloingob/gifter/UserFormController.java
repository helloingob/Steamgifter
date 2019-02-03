package com.helloingob.gifter;

import java.util.Optional;
import org.apache.commons.codec.digest.DigestUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

import com.helloingob.gifter.dao.AlgorithmDAO;
import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.to.AlgorithmTO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.utilities.GUIHelper;
import com.helloingob.gifter.utilities.GUISettings;
import com.helloingob.gifter.utilities.manager.NotificationManager;
import com.helloingob.gifter.utilities.manager.SessionManager;

public class UserFormController extends GenericForwardComposer<Component> {
    private static final long serialVersionUID = 1L;

    private Textbox tbxLoginName;
    private Textbox tbxProfileName;
    private Textbox tbxPassword;
    private Textbox tbxSessionId;
    private Textbox tbxEmail;
    private Combobox cbxAlgorithm;
    private Combobox cbxSkipDLC;
    private Combobox cbxSkipSub;
    private Combobox cbxSkipWishlist;
    private Combobox cbxIsActive;
    private Combobox cbxIsAdmin;

    private UserTO user;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init() {
        user = (UserTO) arg.get("user");
        fillComboboxes();
        if (user != null) {
            fillUserData();
        }
    }

    private void fillUserData() {
        tbxLoginName.setText(user.getLoginName());
        tbxProfileName.setText(user.getProfileName());
        tbxSessionId.setText(user.getPhpsessionid());
        tbxEmail.setText(user.getNotificationEmail());

        Optional<Comboitem> optItem = cbxAlgorithm.getItems().stream().filter(item -> item.getValue() == user.getAlgorithmPk()).findFirst();
        if (optItem.isPresent()) {
            cbxAlgorithm.setSelectedItem(optItem.get());
        }

        cbxSkipDLC.setText(GUIHelper.convertBoolean(user.getSkipDlc()));
        cbxSkipSub.setText(GUIHelper.convertBoolean(user.getSkipSub()));
        cbxSkipWishlist.setText(GUIHelper.convertBoolean(user.getSkipWishlist()));
        cbxIsActive.setText(GUIHelper.convertBoolean(user.getIsActive()));
        cbxIsAdmin.setText(GUIHelper.convertBoolean(user.getIsAdmin()));
    }

    private void fillComboboxes() {
        cbxAlgorithm.getItems().clear();
        for (AlgorithmTO algorithm : new AlgorithmDAO().get()) {
            Comboitem comboitem = new Comboitem(algorithm.getName());
            comboitem.setDescription(algorithm.getDescription());
            comboitem.setValue(algorithm.getPk());
            cbxAlgorithm.appendChild(comboitem);
        }
        cbxAlgorithm.setSelectedIndex(0);

        GUIHelper.fillBooleanCombobox(cbxSkipDLC, true);
        GUIHelper.fillBooleanCombobox(cbxSkipSub, true);
        GUIHelper.fillBooleanCombobox(cbxSkipWishlist, false);
        GUIHelper.fillBooleanCombobox(cbxIsActive, true);
        GUIHelper.fillBooleanCombobox(cbxIsAdmin, false);
    }

    public void onClick$btnOk() {
        if (tbxLoginName.getText().isEmpty()) {
            NotificationManager.showWarning("Enter a login name", tbxLoginName, NotificationManager.RIGHT);
            return;
        }

        if (user == null) {
            user = new UserTO();
            user.setPassword(DigestUtils.sha1Hex(GUISettings.DEFAULT_PASSWORD));
        }

        user.setLoginName(tbxLoginName.getText());
        user.setNotificationEmail(tbxEmail.getText());
        user.setPhpsessionid(tbxSessionId.getText());
        user.setAlgorithmPk(cbxAlgorithm.getSelectedItem().getValue());
        user.setSkipDlc(cbxSkipDLC.getSelectedItem().getValue());
        user.setSkipSub(cbxSkipSub.getSelectedItem().getValue());
        user.setSkipWishlist(cbxSkipWishlist.getSelectedItem().getValue());
        user.setIsActive(cbxIsActive.getSelectedItem().getValue());
        user.setIsAdmin(cbxIsAdmin.getSelectedItem().getValue());

        if (!tbxPassword.getText().isEmpty()) {
            user.setPassword(DigestUtils.sha1Hex(tbxPassword.getText()));
        }

        boolean success = false;

        if (user.getPk() == null) {
            success = new UserDAO().save(user);
        } else {
            success = new UserDAO().update(user);
        }

        if (success) {
            NotificationManager.showInfo("Success", null, NotificationManager.CENTER);
            if (SessionManager.getUser().get().getPk().equals(user.getPk())) {
                SessionManager.setUser(user);
            }
            Events.postEvent(GUISettings.USER_RELOAD_EVENT, this.self, null);
            this.self.detach();
        } else {
            NotificationManager.showWarning("Error saving user", null, NotificationManager.CENTER);
        }
    }
}
