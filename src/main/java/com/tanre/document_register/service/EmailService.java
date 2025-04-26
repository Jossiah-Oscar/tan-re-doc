package com.tanre.document_register.service;

import com.tanre.document_register.model.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${notification.recipient}")
    private String to; // you can also inject a list

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendDocumentSubmissionEmail(Document doc) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("New Document Submitted: ID " + doc.getId());
        msg.setText(buildBody(doc));
        mailSender.send(msg);
    }

    private String buildBody(Document doc) {
        return new StringBuilder()
                .append("Hello,\n\n")
                .append("This is an automated message from Document Management System\n\n")
                .append("A new document group has been submitted:\n\n")
                .append("ID: ").append(doc.getId()).append("\n")
                .append("Cedant: ").append(doc.getCedantName()).append("\n")
                .append("Type: ").append(doc.getDocumentType()).append("\n")
                .append("Group Name: ").append(doc.getFileName()).append("\n")
                .append("Status: ").append(doc.getStatus()).append("\n")
                .append("Submitted At: ").append(doc.getDateCreated()).append("\n\n")
                .append("You can review it here: ")
//                .append("http://your-app/documents/").append(doc.getId())
                .toString();
    }
}