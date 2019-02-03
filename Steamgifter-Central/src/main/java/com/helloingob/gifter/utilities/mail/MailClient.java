package com.helloingob.gifter.utilities.mail;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.to.WonGiveawayTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.SharedSettings;

public class MailClient {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private Properties mailProperties = null;
    private AdvUserTO user;

    private final static String MSG_TEMP_DEACTIVATED = "Your gifter account (gifter.helloingob.com) has been temporarily deactivated.";
    private final static String MSG_INACTIVITY = " of inactivity your gifter account will be temporarily deactivated.";

    public MailClient(AdvUserTO user) {
        this.user = user;
        mailProperties = new Properties();
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(CentralSettings.Files.MAIL));
            mailProperties.load(bis);

            mailProperties.put("mail.transport.protocol", "smtp");
            mailProperties.put("mail.smtp.auth", "true");
            mailProperties.put("mail.smtp.starttls.enable", "true");

            bis.close();
        } catch (Exception e) {
            new ErrorLogHandler().error(e, CentralSettings.Files.MAIL, logger);
        }
    }

    public boolean sendMailToWinner(String title, WonGiveawayTO wonGiveaway) {
        return sendEmail(user.getNotificationEmail(), title, prepareWinnerMailBody(wonGiveaway), false);
    }

    private String prepareWinnerMailBody(WonGiveawayTO wonGiveaway) {
        String body = "Giveaway: " + wonGiveaway.getGiveawayLink();
        //Steam Link
        if (wonGiveaway.getSteamLink().isPresent()) {
            body += "\nSteam Store: " + wonGiveaway.getSteamLink().get();
        }
        body += "\n";
        //Key
        if (wonGiveaway.getSteamKey().isPresent()) {
            body += "\nSteam Activation: " + wonGiveaway.getSteamKey().get();
        }
        //Win Chance
        body += "\nChance: " + String.format("%.2f", wonGiveaway.getWinChance()) + "%";
        //Points
        body += "\nPoints: " + wonGiveaway.getPoints();
        //Entries
        body += "\nEntries: " + wonGiveaway.getEntries();
        return body;
    }

    public boolean sendSuspensionEmail(String suspensionNotification) {
        String subject = "*SUSPENSION* steamgifts.com";
        String body = "You got suspended!\n-> " + suspensionNotification + "\n\nPlease visit http://www.steamgifts.com/suspensions for details.\n\n" + MSG_TEMP_DEACTIVATED;
        return sendEmail(user.getNotificationEmail(), subject, body, true);
    }

    public boolean sendNotActivatedBanEmail(String body) {
        String subject = "*BAN* steamgifter";
        body = "Please activate your won games in the steamclient.\n\n" + body + "\n" + MSG_TEMP_DEACTIVATED;
        return sendEmail(user.getNotificationEmail(), subject, body, true);
    }

    public boolean sendNotActivatedWarningEmail(String body) {
        String subject = "*ACTION REQUIRED* steamclient";
        body = "Your action is required!\nPlease activate your won games in the steamclient.\n\n" + body + "\n\nAfter two days" + MSG_INACTIVITY;
        return sendEmail(user.getNotificationEmail(), subject, body, true);
    }

    public boolean sendNotAcknowledgedBanEmail(String body) {
        String subject = "*BAN* steamgifter";
        body = "Please visit http://www.steamgifts.com/giveaways/won, acknowledge won giveaway(s).\n\n" + body + "\n" + MSG_TEMP_DEACTIVATED;
        return sendEmail(user.getNotificationEmail(), subject, body, true);
    }

    public boolean sendNotAcknowledgedReminderEmail(String body) {
        String subject = "*ACTION REQUIRED* steamgifts.com";
        body = "Your action is required!\nPlease visit http://www.steamgifts.com/giveaways/won and acknowledge won giveaway(s).\n\n" + body + "\n\nAfter three days" + MSG_INACTIVITY;
        return sendEmail(user.getNotificationEmail(), subject, body, true);
    }

    public boolean sendSyncRequiredEmail() {
        String subject = "*SYNC REQUIRED* steamgifts.com";
        String body = "Your steamgifts.com account is out of date! Please switch to a public steam profile, visit http://www.steamgifts.com/account/profile/sync and sync your account.\n" + MSG_TEMP_DEACTIVATED;
        return sendEmail(user.getNotificationEmail(), subject, body, true);
    }

    public void notifyAdmins(String notification) {
        List<UserTO> admins = new UserDAO().get().stream().filter(user -> user.getIsAdmin()).collect(Collectors.toList());
        for (UserTO admin : admins) {
            sendEmail(admin.getNotificationEmail(), "*ADMIN* steamgifter", notification, false);
        }
    }

    private boolean isEnabled() {
        if (!mailProperties.containsKey("mail.disabled")) {
            return true;
        } else {
            return !Boolean.parseBoolean(mailProperties.getProperty("mail.disabled"));
        }
    }

    private boolean sendEmail(String emailAddress, String subject, String body, boolean logBody) {
        if (mailProperties == null) {
            new ErrorLogHandler().error("No mail.properties found! (" + CentralSettings.Files.MAIL + ")", logger);
            return false;
        }
        if (emailAddress != null && !emailAddress.isEmpty() && isEnabled()) {
            try {
                String logLine = subject;
                if (logBody) {
                    logLine += ": " + body.replaceAll("\n", " / ");
                }
                logger.info("[" + user.getLoginName() + "] " + user.getNotificationEmail() + " got Mail -> " + logLine);

                Authenticator authenticator = new SMTPAuthenticator();
                Session mailSession = Session.getDefaultInstance(mailProperties, authenticator);
                Transport transport = mailSession.getTransport();

                MimeMessage mimeMessage = new MimeMessage(mailSession);
                mimeMessage.setSubject(subject, "utf-8");
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
                mimeMessage.setContent(body, "text/plain; charset=utf-8");

                transport.connect();
                transport.sendMessage(mimeMessage, mimeMessage.getRecipients(Message.RecipientType.TO));
                transport.close();

                return true;
            } catch (Exception e) {
                new ErrorLogHandler(user).error(e, logger);
            }
        }
        return false;
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = mailProperties.getProperty("mail.smtp.user");
            String password = mailProperties.getProperty("mail.smtp.password");
            return new PasswordAuthentication(username, password);
        }
    }
}