package com.oc.projet3.biblioclient.controller;



import com.oc.projet3.biblioclient.generated.biblio.AccountWS;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountRequest;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountResponse;
import com.oc.projet3.biblioclient.generated.biblio.MemberWS;
import com.oc.projet3.biblioclient.service.AccountService;
import com.oc.projet3.biblioclient.service.SOAPConnector;
import com.sun.xml.internal.ws.fault.ServerSOAPFaultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.lang.model.element.Element;
import javax.validation.Valid;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import java.util.Iterator;

@Controller
public class LoginController {

    final
    SOAPConnector soapConnector;

    @Autowired
    public LoginController(SOAPConnector soapConnector) {
        this.soapConnector = soapConnector;
    }

    @RequestMapping(value={"/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("authentification/login");
        return modelAndView;
    }

//    @RequestMapping(value={"/login"}, method = RequestMethod.POST)
//    public ModelAndView connection(@RequestParam("email") String email, @RequestParam("password") String password){
//        AuthenticationResponse response = biblioClient.toConnect(email,password);
//
//        ModelAndView modelAndView = new ModelAndView();
//
//        if ( response.getEmail()!=null ){
//            modelAndView.addObject("username", response.getEmail());
//            modelAndView.setViewName("home");
//        }else{
//            modelAndView.addObject("isErrorConnection", true);
//            modelAndView.setViewName("authentification/login");
//        }
//
//        return modelAndView;
//    }



    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        AccountWS member = new AccountWS();
        modelAndView.addObject("member", member);
        modelAndView.setViewName("authentification/registration2");
        return modelAndView;
    }


    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid AccountWS accountWS) {
        ModelAndView modelAndView = new ModelAndView();

        CreateAccountRequest request = new CreateAccountRequest();
        request.setFirstname(accountWS.getFirstname());
        request.setLastname(accountWS.getLastname());
        request.setEmail(accountWS.getEmail());
        request.setPassword(accountWS.getPassword());

         CreateAccountResponse response = null;
        try {
            response = (CreateAccountResponse) soapConnector.callWebService("http://localhost:8080/anonymous/createAccount", request);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.setViewName("authentification/login");
        }catch (SoapFaultClientException e){
            System.out.println(e.getFaultStringOrReason());
            modelAndView.addObject("errorMessage", e.getMessage());
            modelAndView.setViewName("authentification/registration2");
            e.printStackTrace();
            System.out.println(e.getMessage());
            try {
                Element element = getDetail(e);

            } catch (TransformerException e1) {
                e1.printStackTrace();
            }


            SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail(); // <soapFaultDetail> node
            // if there is no fault soapFaultDetail ...
            if (soapFaultDetail == null) {
                throw e;
            }
            SoapFaultDetailElement detailElementChild = soapFaultDetail.getDetailEntries().next();
            Source detailSource = detailElementChild.getSource();
            Object detail = soapConnector.unmarshall(detailSource);
//            JAXBElement<serviceException> source = (JAXBElement<serviceException>)detail;
//            System.out.println("Text::"+source.getText()); //prints : Locale is invalid.
        }

                  // modelAndView.setViewName("authentification/registration");
        return modelAndView;
    }

    private Element getDetail(SoapFaultClientException e) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMResult result = new DOMResult();
        transformer.transform(e.getSoapFault().getSource(), result);
        NodeList nl = ((Document)result.getNode()).getElementsByTagName("detail");
        return (Element)nl.item(0);
    }
}

