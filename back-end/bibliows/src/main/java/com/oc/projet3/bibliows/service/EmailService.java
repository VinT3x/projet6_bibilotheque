package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.config.email.MailContentBuilder;
import com.oc.projet3.bibliows.entities.WaitingList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service
public class EmailService {
    private static Logger logger = LogManager.getLogger(EmailService.class);

    @Autowired
    private WaitingListService waitingListService;

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private MailContentBuilder mailContentBuilder;
    @Value("${app.email.reservation.subject}")
    private String subject;

    @Value("${delayHourForRetrieveBook}")
    private int delayHourForRetrieveBook;

    public void toWarnBookAvailable(WaitingList wl) throws MessagingException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String to = wl.getMember().getEmail();

        logger.info("Alerte réservation disponible {} pour {}.", to, wl.getBook().getTitle());

        MimeMessage message = emailSender.createMimeMessage();

        boolean multipart = true;

        MimeMessageHelper helper = new MimeMessageHelper(message, multipart, "utf-8");

        String content = mailContentBuilder.buildWarnReservation(
                wl.getMember().getFirstname(),
                wl.getBook().getTitle(),
                dateFormat.format(wl.getReservationDate().getTime()),
                String.valueOf(delayHourForRetrieveBook));

        message.setContent(content, "text/html");

        helper.setTo(to);

        String buffer = "Le livre " +
                wl.getBook().getTitle() +
                " est disponible";
        helper.setSubject(buffer);

        this.emailSender.send(message);
    }

    public void toWarnWaitingListCancelled(WaitingList wl) throws MessagingException {
        String to = wl.getMember().getEmail();

        logger.info("Alerte annulation réservation {} pour {}.", to, wl.getBook().getTitle());

        MimeMessage message = emailSender.createMimeMessage();

        boolean multipart = true;

        MimeMessageHelper helper = new MimeMessageHelper(message, multipart, "utf-8");

        String content = mailContentBuilder.buildWarnCancelReservation(
                wl.getMember().getFirstname(),
                wl.getBook().getTitle());

        message.setContent(content, "text/html");

        helper.setTo(to);
        helper.setSubject("Annulation de la réservation du livre " + wl.getBook().getTitle());

        this.emailSender.send(message);
    }
}
