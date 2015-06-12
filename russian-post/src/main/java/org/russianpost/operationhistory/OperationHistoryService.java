
package org.russianpost.operationhistory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.russianpost.operationhistory.data.AuthorizationHeader;
import org.russianpost.operationhistory.data.LanguageData;
import org.russianpost.operationhistory.data.OperationHistoryData;
import org.russianpost.operationhistory.data.OperationHistoryRequest;
import org.russianpost.operationhistory.data.UpdateOperationRequest;
import org.russianpost.rtm.dataexchangeespp.data.PostalOrderEventsForMaiOutput;
import org.russianpost.rtm.dataexchangeespp.data.PostalOrderEventsForMailInput;
import org.russianpost.sms_info.data.SmsHistoryData;
import org.russianpost.sms_info.data.SmsHistoryRequest;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "OperationHistoryInterface", targetNamespace = "http://russianpost.org/operationhistory")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    org.russianpost.operationhistory.data.ObjectFactory.class,
    org.russianpost.sms_info.data.ObjectFactory.class,
    org.russianpost.rtm.dataexchangeespp.data.ObjectFactory.class
})
public interface OperationHistoryService {


    /**
     * Метод получения истории операции над РПО.
     * 
     * @param historyRequest
     * @param authorizationHeader
     * @return
     *     returns org.russianpost.operationhistory.data.OperationHistoryData
     * @throws OperationHistoryFault
     * @throws LanguageFault
     * @throws AuthorizationFault
     */
    @WebMethod(operationName = "GetOperationHistory")
    @WebResult(name = "OperationHistoryData", targetNamespace = "http://russianpost.org/operationhistory/data", partName = "messagePart")
    public OperationHistoryData getOperationHistory(
        @WebParam(name = "OperationHistoryRequest", targetNamespace = "http://russianpost.org/operationhistory/data", partName = "historyRequest")
        OperationHistoryRequest historyRequest,
        @WebParam(name = "AuthorizationHeader", targetNamespace = "http://russianpost.org/operationhistory/data", header = true, partName = "AuthorizationHeader")
        AuthorizationHeader authorizationHeader)
        throws AuthorizationFault, LanguageFault, OperationHistoryFault
    ;

    /**
     * Метод редактирования операционных данных.
     * 
     * @param updateRequest
     * @param authorizationHeader
     * @return
     *     returns org.russianpost.operationhistory.data.OperationHistoryData
     * @throws OperationHistoryFault
     * @throws LanguageFault
     * @throws AuthorizationFault
     */
    @WebMethod(operationName = "UpdateOperationData")
    @WebResult(name = "OperationHistoryData", targetNamespace = "http://russianpost.org/operationhistory/data", partName = "messagePart")
    public OperationHistoryData updateOperationData(
        @WebParam(name = "UpdateOperationRequest", targetNamespace = "http://russianpost.org/operationhistory/data", partName = "UpdateRequest")
        UpdateOperationRequest updateRequest,
        @WebParam(name = "AuthorizationHeader", targetNamespace = "http://russianpost.org/operationhistory/data", header = true, partName = "AuthorizationHeader")
        AuthorizationHeader authorizationHeader)
        throws AuthorizationFault, LanguageFault, OperationHistoryFault
    ;

    /**
     * Метод получения списка доступных языков предоставления данных об РПО.
     * 
     * @param authorizationHeader
     * @return
     *     returns org.russianpost.operationhistory.data.LanguageData
     * @throws OperationHistoryFault
     * @throws LanguageFault
     * @throws AuthorizationFault
     */
    @WebMethod
    @WebResult(name = "LanguageData", targetNamespace = "http://russianpost.org/operationhistory/data", partName = "messagePart")
    public LanguageData getLanguages(
        @WebParam(name = "AuthorizationHeader", targetNamespace = "http://russianpost.org/operationhistory/data", header = true, partName = "AuthorizationHeader")
        AuthorizationHeader authorizationHeader)
        throws AuthorizationFault, LanguageFault, OperationHistoryFault
    ;

    /**
     * Метод получения данных о наложенном платеже. 
     * 
     * @param inputParam
     * @param authorizationHeader
     * @return
     *     returns org.russianpost.rtm.dataexchangeespp.data.PostalOrderEventsForMaiOutput
     * @throws PostalOrderEventsForMailFault
     */
    @WebMethod(operationName = "PostalOrderEventsForMail")
    @WebResult(name = "PostalOrderEventsForMaiOutput", targetNamespace = "http://www.russianpost.org/RTM/DataExchangeESPP/Data", partName = "ResponseParam")
    public PostalOrderEventsForMaiOutput postalOrderEventsForMail(
        @WebParam(name = "AuthorizationHeader", targetNamespace = "http://russianpost.org/operationhistory/data", header = true, partName = "AuthorizationHeader")
        AuthorizationHeader authorizationHeader,
        @WebParam(name = "PostalOrderEventsForMailInput", targetNamespace = "http://www.russianpost.org/RTM/DataExchangeESPP/Data", partName = "InputParam")
        PostalOrderEventsForMailInput inputParam)
        throws PostalOrderEventsForMailFault
    ;

    /**
     * Метод получения данных о переданных по отправлению СМС. 
     * 
     * @param smsRequest
     * @param authorizationHeader
     * @return
     *     returns org.russianpost.sms_info.data.SmsHistoryData
     * @throws LanguageFault
     * @throws SmsHistoryFault
     * @throws AuthorizationFault
     */
    @WebMethod
    @WebResult(name = "SmsHistoryData", targetNamespace = "http://russianpost.org/sms-info/data", partName = "SmsHistoryResponse")
    public SmsHistoryData getSmsHistory(
        @WebParam(name = "AuthorizationHeader", targetNamespace = "http://russianpost.org/operationhistory/data", header = true, partName = "AuthorizationHeader")
        AuthorizationHeader authorizationHeader,
        @WebParam(name = "SmsHistoryRequest", targetNamespace = "http://russianpost.org/sms-info/data", partName = "SmsRequest")
        SmsHistoryRequest smsRequest)
        throws AuthorizationFault, LanguageFault, SmsHistoryFault
    ;

}
