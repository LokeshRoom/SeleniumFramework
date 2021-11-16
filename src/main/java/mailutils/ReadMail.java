package mailutils;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class ReadMail {

    private Session session;
    private Store store;
    private Folder folder;
    private String protocol = "imaps";
    private String file = "INBOX";

    public boolean isLoggedIn() {
        return store.isConnected();
    }

    /**
     * to login to the mail host server
     */
    public Session loginToGmail(String username, String password)
            throws Exception {
        URLName url = new URLName(protocol, "imap.gmail.com", 993, file, username, PasswordDecoder.decoder(password));

        if (session == null) {
            Properties props = null;
            try {
                props = System.getProperties();
            } catch (SecurityException ex) {
                props = new Properties();
            }
            session = Session.getInstance(props, null);
        }
        store = session.getStore(url);
        store.connect();
        folder = store.getFolder(url);

        folder.open(Folder.READ_ONLY);
        return session;
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

    public int getMessageCount() throws Exception {
        int messageCount = 0;
        messageCount = folder.getMessageCount();
        return messageCount;
    }

    public Message[] getMessages() throws MessagingException, javax.mail.MessagingException {
        return folder.getMessages();
    }

    public List<Message> getMessagesReceivedAfterGivenTime(Date date) throws Exception {
        Message[] messages = folder.getMessages();
        List<Message> filteredMessages = new ArrayList<>();
        for (int i = messages.length - 1; i > 0; i--) {
            if (messages[i].getReceivedDate().compareTo(date) >= 0) {
                filteredMessages.add(messages[i]);
            } else break;
        }
        return filteredMessages;
    }

    public String getLatestMessagesWIthSubjectAndGivenTime(String subject, Date date) throws Exception {
        List<Message> messages = getMessagesReceivedAfterGivenTime(date);
        for (Message message : messages) {
            if (message.getSubject().contains(subject))
                if (message.getContentType().toLowerCase().contains("multipart"))
                    return getTextFromMimeMultipart((MimeMultipart) message.getContent());
                else return message.getContent().toString();
        }
        return null;
    }

    public String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        String result = "";
        int partCount = mimeMultipart.getCount();
        for (int i = 0; i < partCount; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                // break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
                result = html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    /*public static void main(String[] args) throws Exception {
        App app=new App();
        app.login("imap.gmail.com","lokeshkumarfortest@gmail.com","Yesyoucracked");
        System.out.println(app.isLoggedIn());
       // System.out.println(app.getLatestMessagesWIthSubjectAndGivenTime("Critical security alert",);
        System.out.println(app.getMessages()[0].getContent().toString());
        MimeMultipart mimeMessage= (MimeMultipart) app.getMessages()[0].getContent();
        System.out.println(app.getTextFromMimeMultipart(mimeMessage));
    }*/

}
