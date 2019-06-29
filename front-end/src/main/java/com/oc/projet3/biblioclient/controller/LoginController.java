package com.oc.projet3.biblioclient.controller;

import com.oc.projet3.biblioclient.controller.error.ServerErrorMessage;
import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.AuthenticationRequest;
import com.oc.projet3.biblioclient.generated.biblio.CreateAccountRequest;
import com.oc.projet3.biblioclient.generated.biblio.FindAccountsRequest;
import com.oc.projet3.biblioclient.generated.biblio.FindAccountsResponse;
import com.oc.projet3.biblioclient.service.SOAPConnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.validation.Valid;
import java.util.List;

@Controller
@SessionAttributes( value="user", types={User.class} )
public class LoginController {

    @Value("${uri_anonymous}")
    private String URI_ANONYMOUS;

    @Value("${uri_biblio}")
    private String URI_BIBLIO;

    final private
    SOAPConnector soapConnector;

    @Autowired
    public LoginController(SOAPConnector soapConnector) {
        this.soapConnector = soapConnector;
    }

    @ModelAttribute("user")
    public User getUser() {
        return new User();

    }

    @ModelAttribute("user")
    public User addUserToSessionScope(String email, String password) {
        return new User(email, password);
    }


    /**
     * Affichage du formulaire de connexion
     * @return formulaire connexion
     */
    @GetMapping(value={"/login"})
    public String login(Model model){
        return "authentification/login";
    }


    /**
     * connexion au webservice
     * @param model
     * @param request objet pour la requete de connexion
     * @return l'url de redirection home si connexion sinon vers le formulaire login
     */
    @PostMapping(value = "/login")
    public String connexion(@Valid AuthenticationRequest request, Model model) {
        String vue;
        try {

            soapConnector.callWebService(URI_ANONYMOUS + "/authentication", request);

            FindAccountsRequest findAccountsRequest = new FindAccountsRequest();
            findAccountsRequest.setEmail(request.getEmail());

            User user = addUserToSessionScope(request.getEmail(),request.getPassword());
            FindAccountsResponse findAccountsResponse = (FindAccountsResponse) soapConnector.callWebService(URI_BIBLIO + "/findAccount", findAccountsRequest, user);

            user.setFirstname(findAccountsResponse.getMembers().get(0).getFirstname());
            user.setLastname(findAccountsResponse.getMembers().get(0).getLastname());
            user.setIdMember(findAccountsResponse.getMembers().get(0).getId());
            model.addAttribute("user", user);

            vue = "redirect:home";

        }catch (SoapFaultClientException soapFaultClientException){

            List<String> list = ServerErrorMessage.getListError(soapFaultClientException);
            model.addAttribute("errorMessages", list);
            vue = "authentification/login";
        }

        return vue;
    }

    /**
     * Permet de se déconnecter
     * @return formulaire connexion
     */
    @GetMapping(value={"/logout"})
    public String logout(SessionStatus status){
        status.setComplete();
        return "authentification/login";
    }

    /**
     * Affichage du formulaire utilisateur
     * @param accountRequest objet pour la creation d'un compte
     * @return formulaire utilisateur
     */
    @GetMapping(value="/registration")
    public String registration(Model model, CreateAccountRequest accountRequest){
        model.addAttribute("member", accountRequest);
        return "authentification/registration";
    }

    /**
     * Creation d'un utilisateur
     * @param accountRequest objet account généré
     * @return ModelAndView
     */
    @PostMapping(value = "/registration")
    public ModelAndView createNewUser(@Valid CreateAccountRequest accountRequest) {
        ModelAndView modelAndView = new ModelAndView();

        try {

            soapConnector.callWebService(URI_ANONYMOUS + "/createAccount", accountRequest);
            modelAndView.setViewName("authentification/login");

        }catch (SoapFaultClientException soapFaultClientException){

            List<String> list = ServerErrorMessage.getListError(soapFaultClientException);
            modelAndView.addObject("errorMessages", list);
            modelAndView.addObject("member",accountRequest);
            modelAndView.setViewName("authentification/registration");

        }
        return modelAndView;
    }
}

