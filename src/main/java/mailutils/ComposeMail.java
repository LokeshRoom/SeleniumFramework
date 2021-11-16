package mailutils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ComposeMail {

    private Session session;
    private Store store;
    private Folder folder;
    private String protocol = "imaps";
    private String file = "INBOX";

    public static void main(String[] args) throws Exception {
        ComposeMail cm = new ComposeMail();
        cm.loginToGmail("lokeshkumarfortest@gmail.com", "Yesyoucracked");
        String[] mailids = {"lokeshkumarfortest@gmail.com", "nandan.p@ibusinesssoftware.in",};
        cm.sendMailWithAttachments(mailids, "Test Mail", "Test", Arrays.asList("filepath"));
        //cm.logout();
    }

    public boolean isLoggedIn() {
        return store.isConnected();
    }

    /**
     * to login to the mail host server
     */
    public void loginToGmail(String username, String password)
            throws Exception {

        if (session == null) {
            Properties props = null;
            try {
                props = System.getProperties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
            } catch (SecurityException ex) {
                props = new Properties();
            }
            session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, PasswordDecoder.decoder(password));
                        }
                    });
        }
    }

    public void sendMailWithAttachments(String[] recipients, String subject, String body, List<String> attachments) {
        try {
            MimeMessage mail = new MimeMessage(session);
            Address[] addresses = new Address[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                addresses[i] = new InternetAddress(recipients[i]);
            }
            mail.addRecipients(Message.RecipientType.TO, addresses);
            mail.setSubject(subject);
            mail.setText(body);
            Multipart multipart = new MimeMultipart();

            for (String attachment : attachments
            ) {
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                messageBodyPart.setDataHandler(new DataHandler(source));
                attachment=attachment.substring(attachment.lastIndexOf("\\")+1,attachment.length());
                messageBodyPart.setFileName(attachment);
                multipart.addBodyPart(messageBodyPart);
            }

            mail.setContent(multipart);

            Transport.send(mail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * to logout from the mail host server
     */
    public void logout() throws MessagingException, javax.mail.MessagingException {
        folder.close(false);
        store.close();
        store = null;
        session = null;
    }
}
