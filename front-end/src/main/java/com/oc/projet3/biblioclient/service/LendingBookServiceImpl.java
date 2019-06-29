package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LendingBookServiceImpl implements LendingBookService {

    private final
    SOAPConnector soapConnector;

    @Value("${uri_biblio}")
    private String URI_BIBLIO;

    @Autowired
    public LendingBookServiceImpl(SOAPConnector soapConnector){
        this.soapConnector = soapConnector;
    }

    @Override
    /**
     * {@link LendingBookService#findBook(boolean,User)}
     */
    public FindLendingBookResponse findBook(boolean isCurrent, User user){
        FindLendingBookRequest findLendingBookRequest = new FindLendingBookRequest();
        findLendingBookRequest.setCurrent(isCurrent);
        findLendingBookRequest.setEmail(user.getEmail());

        return  (FindLendingBookResponse) soapConnector.callWebService(
                URI_BIBLIO + "/findLendingBook",
                findLendingBookRequest,
                user);
    }

    /**
     * {@link LendingBookService#loanBook(long,User)}
     * @param id, id de reservation
     * @param user, utilisateur emprunteur
     */
    @Override
    public void loanBook(long id, User user){
        LendingBookRequest lendingBookRequest = new LendingBookRequest();

        lendingBookRequest.setAccountId(user.getIdMember());
        lendingBookRequest.setBookId(id);

        LendingBookResponse lendingBookResponse = (LendingBookResponse) soapConnector
                .callWebService(URI_BIBLIO + "/lendingBook", lendingBookRequest, user);

    }

    @Override
    public void loanBookExtend(long id, User user){
        ExtendLendingBookRequest extendLendingBookRequest = new ExtendLendingBookRequest();
        extendLendingBookRequest.setId(id);

        ExtendLendingBookResponse response = (ExtendLendingBookResponse)  soapConnector
                .callWebService(URI_BIBLIO + "/extendLendingBook", extendLendingBookRequest, user);
    }

}
