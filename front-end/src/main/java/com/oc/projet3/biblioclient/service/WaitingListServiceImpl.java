package com.oc.projet3.biblioclient.service;

import com.oc.projet3.biblioclient.entity.User;
import com.oc.projet3.biblioclient.generated.biblio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service

public class WaitingListServiceImpl implements WaitingListService {
    private final
    SOAPConnector soapConnector;

    @Value("${uri_biblio}")
    private String URI_BIBLIO;

    @Autowired
    public WaitingListServiceImpl(SOAPConnector soapConnector){
        this.soapConnector = soapConnector;
    }

    /**
     * {@link LendingBookService#loanBook(long,User)}
     * @param id, id de reservation
     * @param user, utilisateur emprunteur
     */
    @Override
    public void addToWaitingList(long id, User user){
        AddToWaitingListRequest addWaitingListRequest = new AddToWaitingListRequest();

        addWaitingListRequest.setAccountId(user.getIdMember());
        addWaitingListRequest.setBookId(id);

        AddToWaitingListResponse addWaitingListResponse = (AddToWaitingListResponse) soapConnector
                .callWebService(URI_BIBLIO + "/addWaitingList", addWaitingListRequest, user);

    }

    @Override
    public void cancelReservation(long id, User user) {
        CancelToWaitingListRequest cancelToWaitingListRequest = new CancelToWaitingListRequest();
        cancelToWaitingListRequest.setId(id);
        soapConnector.callWebService(URI_BIBLIO + "/cancelToWaitingList",
                cancelToWaitingListRequest, user);
    }

    @Override
    public FindWaitingListResponse findReservation(User user){

        FindWaitingListRequest findWaitingListRequest = new FindWaitingListRequest();
        findWaitingListRequest.setEmail(user.getEmail());


        return  (FindWaitingListResponse) soapConnector.callWebService(
                URI_BIBLIO + "/findWaitingList",
                findWaitingListRequest,
                user);
    }
}
