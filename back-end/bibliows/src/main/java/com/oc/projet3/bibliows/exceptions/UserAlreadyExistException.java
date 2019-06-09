package com.oc.projet3.bibliows.exceptions;


import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 * This Exception is thrown when the user is trying to register with an already used email address
 *
 *
 */


@SoapFault(faultCode = FaultCode.SERVER, faultStringOrReason = "No Member Found - From @SoapFault annotation")
public class UserAlreadyExistException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String faultDetail;
	
	public UserAlreadyExistException(String faultDetail){
		this.faultDetail = faultDetail;
	}

	public String getFaultDetail() {
		return faultDetail;
	}

	public void setFaultDetail(String faultDetail) {
		this.faultDetail = faultDetail;
	}

	@Override
	public String toString() {
		return faultDetail;
	}
}
