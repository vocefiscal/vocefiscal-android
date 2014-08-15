/**
 * 
 */
package org.vocefiscal.email;

import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender extends javax.mail.Authenticator 
{   
	 private String user; 
	  private String pass; 

	  private String[] to; 
	  private String from; 

	  private String port; 
	  private String sport; 

	  private String host; 

	  private String subject; 
	  private String body; 

	  private boolean auth; 

	  private boolean debuggable; 

	  private Multipart multipart; 
	  
	  static 
	  {   
	        Security.addProvider(new JSSEProvider());   
	  }  


	  public GMailSender() 
	  { 
	    host = "smtp.gmail.com"; // default smtp server 
	    port = "465"; // default smtp port 
	    sport = "465"; // default socketfactory port 

	    user = ""; // username 
	    pass = ""; // password 
	    from = ""; // email sent from 
	    subject = ""; // email subject 
	    body = ""; // email body 

	    debuggable = false; // debug mode on or off - default off 
	    auth = true; // smtp authentication - default on 

	    multipart = new MimeMultipart(); 

	    // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added. 
	    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
	    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
	    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
	    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
	    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
	    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
	    CommandMap.setDefaultCommandMap(mc); 
	  } 

	  public GMailSender(String user, String pass)
	  { 
	    this(); 

	    this.user = user; 
	    this.pass = pass; 
	  } 

	  public boolean send() throws Exception 
	  { 
	    Properties props = setProperties(); 

	    if(!user.equals("") && !pass.equals("") && to.length > 0 && !from.equals("") && !subject.equals("") && !body.equals("")) 
	    { 
	      Session session = Session.getInstance(props, this); 

	      MimeMessage msg = new MimeMessage(session); 

	      msg.setFrom(new InternetAddress(from)); 

	      InternetAddress[] addressTo = new InternetAddress[to.length]; 
	      for (int i = 0; i < to.length; i++) 
	      { 
	        addressTo[i] = new InternetAddress(to[i]); 
	      } 
	        msg.setRecipients(MimeMessage.RecipientType.TO, addressTo); 

	      msg.setSubject(subject); 
	      msg.setSentDate(new Date()); 

	      // setup message body 
	      BodyPart messageBodyPart = new MimeBodyPart(); 
	      messageBodyPart.setText(body); 
	      multipart.addBodyPart(messageBodyPart); 

	      // Put parts in message 
	      msg.setContent(multipart); 

	      // send email 
	      Transport.send(msg); 

	      return true; 
	    } else 
	    { 
	      return false; 
	    } 
	  } 

	  public void addAttachment(String filename) throws Exception 
	  { 
		  if(filename!=null)
		  {
			  BodyPart messageBodyPart = new MimeBodyPart(); 
			  DataSource source = new FileDataSource(filename); 
			  messageBodyPart.setDataHandler(new DataHandler(source)); 
			  messageBodyPart.setFileName(filename); 

			  multipart.addBodyPart(messageBodyPart); 
		  }

	  } 

	  @Override 
	  public PasswordAuthentication getPasswordAuthentication() 
	  { 
	    return new PasswordAuthentication(user, pass); 
	  } 

	  private Properties setProperties() 
	  { 
	    Properties props = new Properties(); 

	    props.put("mail.smtp.host", host); 

	    if(debuggable) 
	    { 
	      props.put("mail.debug", "true"); 
	    } 

	    if(auth) 
	    { 
	      props.put("mail.smtp.auth", "true"); 
	    } 

	    props.put("mail.smtp.port", port); 
	    props.put("mail.smtp.socketFactory.port", sport); 
	    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
	    props.put("mail.smtp.socketFactory.fallback", "false"); 

	    return props; 
	  }

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass the pass to set
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return the to
	 */
	public String[] getTo() {
		return to;
	}

	/**
	 * @param to the to to set
	 */
	public void setTo(String[] to) {
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the sport
	 */
	public String getSport() {
		return sport;
	}

	/**
	 * @param sport the sport to set
	 */
	public void setSport(String sport) {
		this.sport = sport;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the auth
	 */
	public boolean isAuth() {
		return auth;
	}

	/**
	 * @param auth the auth to set
	 */
	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	/**
	 * @return the debuggable
	 */
	public boolean isDebuggable() {
		return debuggable;
	}

	/**
	 * @param debuggable the debuggable to set
	 */
	public void setDebuggable(boolean debuggable) {
		this.debuggable = debuggable;
	}

	/**
	 * @return the multipart
	 */
	public Multipart getMultipart() {
		return multipart;
	}

	/**
	 * @param multipart the multipart to set
	 */
	public void setMultipart(Multipart multipart) {
		this.multipart = multipart;
	} 
} 
