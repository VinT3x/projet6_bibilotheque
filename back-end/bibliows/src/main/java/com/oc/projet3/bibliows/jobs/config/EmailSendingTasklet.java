package com.oc.projet3.bibliows.jobs.config;

import com.oc.projet3.bibliows.config.email.MailContentBuilder;
import com.oc.projet3.bibliows.dao.LendingBookRepository;
import com.oc.projet3.bibliows.entities.LendingBook;
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
import java.util.Date;
import java.util.List;


public class EmailSendingTasklet implements Tasklet {

    @Autowired
    private LendingBookRepository lendingBookRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private MailContentBuilder mailContentBuilder;

    @Value("${app.email.subject}")
    private String subject;


    private static Logger logger = LogManager.getLogger(EmailSendingTasklet.class);

    /**
     * Envoi des mails de relance
     * @throws MessagingException
     */
    private void sendEmail() throws MessagingException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        List<LendingBook> lendingBookList = getListLendingBookToDun();
        for (LendingBook lb: lendingBookList) {

            String to = lb.getMember().getEmail();

            logger.info("relance " + to + " pour " + lb.getBook().getTitle());

            MimeMessage message = emailSender.createMimeMessage();

            boolean multipart = true;

            MimeMessageHelper helper = new MimeMessageHelper(message, multipart, "utf-8");

            String content = mailContentBuilder.build(lb.getMember().getFirstname(),
                    lb.getBook().getTitle(),
                    dateFormat.format(lb.getStartdate().getTime()),
                    dateFormat.format(lb.getDeadlinedate().getTime()));

            message.setContent(content, "text/html");

            helper.setTo(to);

            helper.setSubject(subject);

            this.emailSender.send(message);

        }
    }

    /**
     * Renvoi la liste des prêts devant être relancé par email
     *
     * @return List<LendingBook>
     */
    private List<LendingBook> getListLendingBookToDun(){
        Calendar deadlineCal = Calendar.getInstance();
        deadlineCal.setTime(new Date());
        // ajout de 3 jours à la date courante pour recupérer
        // les prêts devant se terminer 2 jours avant la date de
        // fin de prêt
        deadlineCal.add(Calendar.DATE, 3);
        return lendingBookRepository.findLendingBookByDeliverydateIsNullAndDeadlinedateBeforeOrderByDeadlinedateAsc(deadlineCal);
    }


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        sendEmail();
        return null;
    }
}
