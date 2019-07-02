package com.oc.projet3.biblioclient.controller;

import com.oc.projet3.biblioclient.controller.error.ServerErrorMessage;
import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.CategoryWS;
import com.oc.projet3.biblioclient.generated.biblio.FindCategoriesResponse;
import com.oc.projet3.biblioclient.service.BookService;
import com.oc.projet3.biblioclient.service.CategoryService;
import com.oc.projet3.biblioclient.service.LendingBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.soap.client.SoapFaultClientException;

import java.util.List;

@Controller
@SessionAttributes("user")
public class SearchController {
    private final
    BookService bookService;

    private final
    LendingBookService lendingBookService;

    private final
    CategoryService categoryService;

    @Autowired
    public SearchController(LendingBookService lendingBookService, BookService bookService, CategoryService categoryService) {
        this.lendingBookService = lendingBookService;
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("user")
    public User getUser() {
        return new User();
    }

    @GetMapping(value={"/home","/"})
    public String home(@ModelAttribute("user") User user, Model model){

        if (user.getEmail() != null){
            model.addAttribute("user", user);
            model.addAttribute("list_loans",
                    lendingBookService.findBook(true, user).getLendingBooks());
            FindCategoriesResponse findCategoriesResponse = categoryService.findCategories(user);
            List<CategoryWS> categoriesWS = findCategoriesResponse.getCategories();

            model.addAttribute("categories", categoriesWS);
            return "home";
        }else{
            return "authentification/login";
        }
    }

    @GetMapping("/search/books")
    public String searchBooks(@ModelAttribute("user") User user, Model model, @RequestParam("title") String title, @RequestParam("fullname") String fullname, @RequestParam("category") int categoryId) {

        if (user.getEmail() != null){
            model.addAttribute("list_books",
                    bookService.findBooks(title, fullname, categoryId, user).getBooks());

            return "home :: booksList";
        }else{
            return "redirect:authentification/login";
        }

    }

    @GetMapping("/loan/findLoan")
    public String findLoanActiveUser(@ModelAttribute("user") User user, Model model) {

        if (user.getEmail() != null){
            String vue;
            try{

                model.addAttribute("list_loans",
                        lendingBookService.findBook(true, user).getLendingBooks());

                vue = "home :: loansList";
            }catch (SoapFaultClientException soapFaultClientException){
                List<String> list = ServerErrorMessage.getListError(soapFaultClientException);
                model.addAttribute("errorMessages", list);

                vue = "home :: loansError";
            }
            return vue;

        }else{
            return "authentification/login";
        }

    }


    @PostMapping(value="/loan/{id}")
    @ResponseBody
    public ResponseEntity<?>  addLoan(@ModelAttribute("user") User user, Model model, @PathVariable Long id){

        try{

            lendingBookService.loanBook(id, user);

            return ResponseEntity.ok().body("Réservation réalisée avec succès.");

        }catch (SoapFaultClientException soapFaultClientException){

            String msg = ServerErrorMessage.getListError(soapFaultClientException).get(0);

            return ResponseEntity.badRequest().body(msg);

        }
    }

    @PostMapping(value="/loan/extend/{id}")
    @ResponseBody
    public ResponseEntity<?>  extendLoan(@ModelAttribute("user") User user, Model model, @PathVariable Long id){

        try{

            lendingBookService.loanBookExtend(id, user);

            return ResponseEntity.ok().body("Prolongation réalisée avec succès.");

        }catch (SoapFaultClientException soapFaultClientException){

            String msg = ServerErrorMessage.getListError(soapFaultClientException).get(0);
            return ResponseEntity.badRequest().body(msg);

        }
    }
}
