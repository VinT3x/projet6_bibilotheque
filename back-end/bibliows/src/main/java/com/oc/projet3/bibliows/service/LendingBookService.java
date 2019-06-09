package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.gs_ws.*;

public interface LendingBookService {
    LendingBookResponse lendingBook(LendingBookRequest request) throws WSException;

    ExtendLendingBookResponse extendLendingBook(ExtendLendingBookRequest request) throws WSException;

    ReturnLendingBookResponse returnLendingBook(ReturnLendingBookRequest request) throws WSException;

    FindLendingBookResponse findLendingBook(FindLendingBookRequest request);

    CancelLendingBookResponse cancelLendingBook(CancelLendingBookRequest request) throws WSException;
}
