package com.oc.projet3.biblioclient.controller.error;

import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.xml.soap.Node;
import javax.xml.transform.dom.DOMResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerErrorMessage {

    public static List<String> getListError(SoapFaultClientException soapFaultClientException){

        List<String> list = new ArrayList<>();

        SoapFaultDetail soapFaultDetail = soapFaultClientException.getSoapFault().getFaultDetail();
        if (soapFaultDetail != null){
            Iterator<SoapFaultDetailElement> detailEntries = soapFaultDetail.getDetailEntries();

            while (detailEntries.hasNext()) {
                SoapFaultDetailElement detailElement = detailEntries.next();
                Node node = (Node) ((DOMResult) detailElement.getResult()).getNode();
                list.add(node.getValue());
            }
        }else {
            list.add(soapFaultClientException.getFaultStringOrReason());
        }

        return list;
    }
}
