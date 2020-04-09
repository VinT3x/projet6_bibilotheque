package com.oc.projet3.bibliows.service;


import com.oc.projet3.bibliows.entities.Author;
import com.oc.projet3.bibliows.entities.Book;
import com.oc.projet3.gs_ws.AuthorWS;
import com.oc.projet3.gs_ws.BookWS;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ConvertUtils {


    List<BookWS> convertListBookToListBookWS(List<Book> listBooks){
        List<BookWS> listBooksWS = new ArrayList<>();
        for (Book book : listBooks) {
            listBooksWS.add(convertBookToBookWS(book));
        }
        return listBooksWS;
    }

    AuthorWS convertAuthorToAuthorWS(Author author){
        AuthorWS authorWS = new AuthorWS();
        BeanUtils.copyProperties(author,authorWS);

        // convert calendar to gregorian calendar
        authorWS.setDateOfBirth(convertCalendarToXMLGregorianCalendar(author.getDateOfBirth()));
        authorWS.setDateOfDeath(convertCalendarToXMLGregorianCalendar(author.getDateOfDeath()));
        return authorWS;

    }

    BookWS convertBookToBookWS(Book book){
        BookWS bookWS = new BookWS();
        BeanUtils.copyProperties(book,bookWS);

//        for (Author author:book.getAuthors()) {
//            bookWS.getAuthors().add(convertAuthorToAuthorWS(author));
//        }

//        for (Booking booking:book.getBookings()) {
//            bookWS.getBookings().add(convertBookingToBookingWS(booking));
//        }

        return bookWS;
    }


    static  Calendar convertXMLGregorianCalendarToCalendar(XMLGregorianCalendar xmlGregorianCalendar){
        Calendar calendar = null;
        if (xmlGregorianCalendar != null){
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            calendar = Calendar.getInstance();
            try {
                Date result = df.parse(xmlGregorianCalendar.toString());

                calendar.setTime(result);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return calendar;
    }

    static XMLGregorianCalendar convertCalendarToXMLGregorianCalendar(Calendar calendar){

        if (calendar!=null){
            // convert calendar to gregorian calendar
            XMLGregorianCalendar xmlGregorianCalendar = null;
            final String FORMATER = "yyyy-MM-dd";

            DateFormat format = new SimpleDateFormat(FORMATER);

            try {
                xmlGregorianCalendar =
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(calendar.getTime()));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
            return xmlGregorianCalendar;
        }else{
            return null;
        }
    }

}
