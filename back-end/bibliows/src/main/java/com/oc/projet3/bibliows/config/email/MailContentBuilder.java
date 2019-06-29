package com.oc.projet3.bibliows.config.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(String firstname, String title, String startdate, String deadlinedate) {
        Context context = new Context();
        context.setVariable("firstname", firstname);
        context.setVariable("title", title);
        context.setVariable("startdate", startdate);
        context.setVariable("deadlinedate", deadlinedate);
        return templateEngine.process("mailTemplate", context);
    }

}
