package com.oc.projet3.bibliows.jobs.config;

import com.oc.projet3.bibliows.config.email.MailContentBuilder;
import com.oc.projet3.bibliows.dao.LendingBookRepository;
import com.oc.projet3.bibliows.entities.WaitingList;
import com.oc.projet3.bibliows.service.WaitingListService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class EmailSendingReservationTasklet implements Tasklet {

    private static Logger logger = LogManager.getLogger(EmailSendingReservationTasklet.class);
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

    public void sendEmail(){

    }


    public void warnReservationBookAvailable() {
        WaitingList waitingList = waitingListService.findOlderWaitingListActiveByBookIdAndMemberId();

    }

    public void cancelReservation() throws MessagingException {
        Calendar calendar = Calendar.getInstance();
        List<WaitingList> listWaitingList = waitingListService.findWaitingListToCancel(calendar);

        for (WaitingList wl: listWaitingList) {
            // màj statut cancelled à true et envoi d'un mail
            toWarnWaitingListCancelled(waitingListService.cancel(wl));
        }

    }


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


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        sendEmail();
        return null;
    }
}
