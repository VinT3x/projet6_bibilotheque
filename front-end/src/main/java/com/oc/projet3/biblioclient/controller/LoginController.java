package com.oc.projet3.biblioclient.controller;



import com.oc.projet3.biblioclient.generated.biblio.AccountWS;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountRequest;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountResponse;
import com.oc.projet3.biblioclient.generated.biblio.MemberWS;
import com.oc.projet3.biblioclient.service.AccountService;
import com.oc.projet3.biblioclient.service.SOAPConnector;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.soap.Node;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        request.setFirstname("xsxsx");
        request.setLastname("xsxsx");
        request.setEmail("xsxsx@ss.ss");
        request.setPassword("x");

        CreateAccountResponse response = null;
        Object object = new String("123");
        try {
            // externaliser la config clirnt (mettre uri dans un fichier de param)

            object = soapConnector.callWebService("http://localhost:8080/anonymous/createAccount", request);
            response = (CreateAccountResponse) object;

            System.out.println("response 1 "+ object);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.setViewName("authentification/login");
        }catch (SoapFaultClientException e){
            List<String> list = new ArrayList<>();

            SoapFaultDetail soapFaultDetail = e.getSoapFault().getFaultDetail();
            if (soapFaultDetail != null){
                Iterator<SoapFaultDetailElement> detailEntries = soapFaultDetail.getDetailEntries();

                while (detailEntries.hasNext()) {
                    SoapFaultDetailElement detailElement = detailEntries.next();
                    Node node = (Node) ((DOMResult) detailElement.getResult()).getNode();
                    list.add(node.getValue());
                }
            }else {
                list.add(e.getFaultStringOrReason());
            }
            modelAndView.addObject("errorMessages", list);
            modelAndView.setViewName("authentification/registration2");
            e.printStackTrace();
        }

                  // modelAndView.setViewName("authentification/registration");
        return modelAndView;
    }
}

