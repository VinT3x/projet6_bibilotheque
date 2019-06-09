package com.oc.projet3.bibliows.service;

import com.oc.projet3.gs_ws.ServiceStatus;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

public class Utils {

    protected static ServiceStatus deleteEntity(boolean isSuccess, String entity) {
        ServiceStatus serviceStatus = new ServiceStatus();

        if (isSuccess) {
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage(entity +" : Content deleted Successfully");

        } else {
            serviceStatus.setStatusCode("NOT_FOUND");
            serviceStatus.setMessage(entity +"Exception while deleting Entity");
        }
        return serviceStatus;
    }

    protected static void copyPropertiesEntityToWS(Object entity, Object ws, ServiceStatus serviceStatus, Logger logger) {
        String msg;
        if (entity == null) {
            msg = "Exception while saving Entity";
            logger.error(msg);
            serviceStatus.setStatusCode("CONFLICT");
            serviceStatus.setMessage(msg);
        } else {
            msg = "Content saved Successfully";
            BeanUtils.copyProperties(entity, ws);
            logger.info(msg + entity.toString());
            serviceStatus.setStatusCode("SUCCESS");
            serviceStatus.setMessage(msg);
        }
    }


}
