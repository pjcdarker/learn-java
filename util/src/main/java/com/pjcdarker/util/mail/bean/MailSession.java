package com.pjcdarker.util.mail.bean;

import com.pjcdarker.util.PropertiesUtil;

import javax.mail.Authenticator;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author pjc
 * @create 2016-10-10
 */
public class MailSession {

    private static final String mailConfigPath = "/mail.properties";

    private Session session;
    private MimeMessage message;
    private Properties props;

    public static MailSession getInstance() {
        return new MailSession();
    }

    private MailSession() {
        props = PropertiesUtil.getProperties(mailConfigPath);
        this.session = createSession();
        this.message = new MimeMessage(this.session);
    }

    public Transport getTransport() throws NoSuchProviderException {
        String transportProtocol = this.props.getProperty("mail.transport.protocol");
        Transport transport = this.session.getTransport(transportProtocol);
        return transport;
    }

    public Session createSession() {
        String username = this.props.getProperty("mail.username");
        String password = this.props.getProperty("mail.password");
        Authenticator authenticator = new SMTPAuthenticator(username, password);
        return Session.getInstance(props, authenticator);
    }

    public MimeMessage getMessage() {
        return message;
    }

    public Session getSession() {
        return session;
    }

    public Properties getProps() {
        return props;
    }
}
