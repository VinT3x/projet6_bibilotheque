package com.oc.projet3.biblioclient.controller;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.FindBooksRequest;
import com.oc.projet3.biblioclient.generated.biblio.FindBooksResponse;
import com.oc.projet3.biblioclient.service.SOAPConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("user")
public class SearchController {

    private final
    SOAPConnector soapConnector;

    @Value("${uri_biblio}")
    private String URI_BIBLIO;

    @Autowired
    public SearchController(SOAPConnector soapConnector) {
        this.soapConnector = soapConnector;
    }

    @ModelAttribute("user")
    public User getUser() {
        return new User();

    }

    @GetMapping(value={"/home","/"})
    public String home(@ModelAttribute("user") User user, Model model){
        //connecté ? @MustAuthentified sinon voir si il faut pas installer spring security

        if (user.getEmail() != null){
            model.addAttribute("username", user.getEmail());
            return "home";
        }else{
            return "authentification/login";
        }
    }

    @GetMapping("/search/books")
    public String searchBooks(@ModelAttribute("user") User user, Model model, @RequestParam("title") String title, @RequestParam("fullname") String fullname) {

        if (user.getEmail() != null){
            Long id = null;
            FindBooksRequest findBooksRequest = new FindBooksRequest();
            findBooksRequest.setFullname(fullname);
            findBooksRequest.setTitle(title);

            FindBooksResponse findBooksResponse = (FindBooksResponse) soapConnector.callWebService(URI_BIBLIO + "/findBooks", findBooksRequest, user);
            model.addAttribute("list_books", findBooksResponse.getBooks());

            return "home :: booksList";
        }else{
            return "authentification/login";
        }

    }
}
