package JavaObjects;

import java.sql.Date;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.*;

@Named(value = "email")
@RequestScoped
public class Email {

    public void send() {
        System.out.println("Before try");

        try{
            System.out.println("IN Try");
            
            String host ="smtp.gmail.com" ;
            String user = "Vircoms1@gmail.com";
            String pass = "VircomsOne";
            String to = "showell2121@gmail.com";
            String from = "Vircoms1@gmail.com";
            String subject = "This is confirmation number for your expertprogramming account. Please insert this number to activate your account.";
            String messageText = "Your Is Test Email :";
            boolean sessionDebug = false;

            Properties props = System.getProperties();

            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");

            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            
            Session mailSession = Session.getDefaultInstance(props, null);
            mailSession.setDebug(sessionDebug);
            
            Message msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from));
            
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
           // msg.setSubject(subject); msg.setSentDate(new Date());
            msg.setText(messageText);

           Transport transport=mailSession.getTransport("smtp");
           transport.connect(host, user, pass);
           transport.sendMessage(msg, msg.getAllRecipients());
           transport.close();
           System.out.println("message send successfully");
        }catch(Exception ex)
        {
            System.out.println(ex);
        }

    

        System.out.println("End of Send");
    }

    @PostConstruct
    public void Email() {
    }

}
