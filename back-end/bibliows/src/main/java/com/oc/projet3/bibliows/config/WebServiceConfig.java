package com.oc.projet3.bibliows.config;

import com.oc.projet3.bibliows.exceptions.DetailSoapFaultDefinitionExceptionResolver;
import com.oc.projet3.bibliows.exceptions.ServiceFaultException;

import lombok.var;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;

import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.validation.XmlValidatorFactory;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;

import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    // Permet de valider si la saisie respecte le type attendu (string, int, etc ...)
    // ajouter l'email
    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        PayloadValidatingInterceptor validatingInterceptor = new CustomValidatingInterceptor();
        validatingInterceptor.setValidateRequest(true);
        validatingInterceptor.setValidateResponse(true);
        //validatingInterceptor.setXsdSchema(biblioSchema());
        CommonsXsdSchemaCollection schemaCollection = new CommonsXsdSchemaCollection(
                getSchemas()
        );
        try {
            schemaCollection.afterPropertiesSet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        validatingInterceptor.setXsdSchemaCollection(schemaCollection);
        try {
            validatingInterceptor.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        validatingInterceptor.setXsdSchemaCollection(schemaCollection);
        interceptors.add(validatingInterceptor);
    }


    private Resource[] getSchemas() {
        return new Resource[]{
                new ClassPathResource("xsd/anonymous.xsd"),
                new ClassPathResource("xsd/biblio.xsd")
        };
    }


    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        //return new ServletRegistrationBean(servlet, "/ws/*");
        return new ServletRegistrationBean(servlet, "/ws/*","/anonymous/*");

    }

    @Bean(name = "wsBooks")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema biblioSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("booksPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://oc.com/projet3/bibliows/biblio-producing-web-service");
        wsdl11Definition.setSchema(biblioSchema());
        return wsdl11Definition;
    }

    @Bean(name = "wsAnonymous")
    public DefaultWsdl11Definition anonymousWsdl11Definition(XsdSchema anonymousSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("anonymousPort");
        wsdl11Definition.setLocationUri("/anonymous");
        wsdl11Definition.setTargetNamespace("http://oc.com/projet3/bibliows/anonymous");
        wsdl11Definition.setSchema(anonymousSchema);
        // fix for adding soapAction to the dynamic generated wsdl
//        Properties soapActions = new Properties();
//        soapActions.setProperty("createAccount", "http://oc.com/projet3/bibliows/biblio-producing-web-service/createAccountRequest");
//        wsdl11Definition.setSoapActions(soapActions);
        return wsdl11Definition;
    }

//    @Bean(name = "wsBooks")
//    public Wsdl11Definition defaultWsdl11Definition() {
//        SimpleWsdl11Definition wsdl11Definition =
//                new SimpleWsdl11Definition();
//        wsdl11Definition
//                .setWsdl(new ClassPathResource("/wsBooks.wsdl"));
//
//        return wsdl11Definition;
//    }



    @Bean
    public XsdSchema biblioSchema() {

        return new SimpleXsdSchema(new ClassPathResource("xsd/biblio.xsd"));
    }

    @Bean
    public XsdSchema anonymousSchema() {

        return new SimpleXsdSchema(new ClassPathResource("xsd/anonymous.xsd"));
    }

    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver() {
        SoapFaultMappingExceptionResolver exceptionResolver = new DetailSoapFaultDefinitionExceptionResolver();
        SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
        faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
        exceptionResolver.setDefaultFault(faultDefinition);

        Properties errorMappings = new Properties();
        errorMappings.setProperty(Exception.class.getName(), SoapFaultDefinition.SERVER.toString());
        errorMappings.setProperty(ServiceFaultException.class.getName(), SoapFaultDefinition.SERVER.toString());
        exceptionResolver.setExceptionMappings(errorMappings);
        exceptionResolver.setOrder(1);
        return exceptionResolver;
    }

    //start Thymeleaf specific configuration
    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public ClassLoaderTemplateResolver templateResolver() {

        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }
    @Bean(name ="templateEngine")
    public SpringTemplateEngine getTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }
    @Bean(name="viewResolver")
    public ThymeleafViewResolver getViewResolver(){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(getTemplateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }
    //end Thymeleaf specific configuration

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}