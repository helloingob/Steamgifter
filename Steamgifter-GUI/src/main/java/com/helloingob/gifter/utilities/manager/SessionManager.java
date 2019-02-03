package com.helloingob.gifter.utilities.manager;

import java.util.Optional;

import org.zkoss.zk.ui.Sessions;

import com.helloingob.gifter.to.UserTO;

public class SessionManager {

    @SuppressWarnings("unchecked")
    public static Optional<UserTO> getUser() {
        if (Sessions.getCurrent().getAttribute("user") != null) {
            return (Optional<UserTO>) Sessions.getCurrent().getAttribute("user");
        } else {
            return Optional.empty();
        }
    }

    public static void setUser(UserTO user) {
        Sessions.getCurrent().setAttribute("user", Optional.ofNullable(user));
    }

    @SuppressWarnings("unchecked")
    public static Optional<String> getUserEditNotification() {
        if (Sessions.getCurrent().getAttribute("notification") != null) {
            return (Optional<String>) Sessions.getCurrent().getAttribute("notification");
        } else {
            return Optional.empty();
        }
    }

    public static void setNotification(String string) {
        Sessions.getCurrent().setAttribute("notification", Optional.ofNullable(string));
    }
}
