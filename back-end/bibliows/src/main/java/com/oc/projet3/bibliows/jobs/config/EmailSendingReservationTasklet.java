package com.oc.projet3.bibliows.jobs.config;

import com.oc.projet3.bibliows.config.email.MailContentBuilder;
import com.oc.projet3.bibliows.entities.WaitingList;
import com.oc.projet3.bibliows.service.EmailService;
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
    private EmailService emailService;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private MailContentBuilder mailContentBuilder;
    @Value("${app.email.reservation.subject}")
    private String subject;

    @Value("${delayHourForRetrieveBook}")
    private int delayHourForRetrieveBook;

    public void reservationWarnBookAvailable() throws MessagingException {

        List<WaitingList> listWaitingList = waitingListService.findWaitingListCandidateToLendingBook();

        for (WaitingList wl: listWaitingList) {
            // les livres sont disponibles, envoi d'un mail au premier de la liste d'attente
            emailService.toWarnBookAvailable(wl);
        }
    }

    public void cancelReservation() throws MessagingException {
        Calendar calendar = Calendar.getInstance();
        List<WaitingList> listWaitingList = waitingListService.findWaitingListToCancel(calendar);

        for (WaitingList wl: listWaitingList) {
            // màj statut cancelled à true et envoi d'un mail
            emailService.toWarnWaitingListCancelled(waitingListService.toCancel(wl));
        }

    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        cancelReservation();
        reservationWarnBookAvailable();
        return null;
    }
}
