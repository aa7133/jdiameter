package org.jdiameter.api.t6a;


import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.api.t6a.events.JReportingInformationAnswer;
import org.jdiameter.api.t6a.events.JReportingInformationRequest;
import org.jdiameter.api.t6a.events.JMO_DataAnswer;
import org.jdiameter.api.t6a.events.JMO_DataRequest;
import org.jdiameter.api.t6a.events.JMT_DataRequest;

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface ClientT6aSessionListener {
  /**
   *
   * @param session
   * @param request
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param session
   * @param request
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void doConfigurationInformationAnswerEvent(ClientT6aSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


  /**
   *
   * @param session
   * @param request
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void doConfigurationInformationRequestEvent(ClientT6aSession session, JConfigurationInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param session
   * @param request
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void doReportingInformationAnswerEvent(ClientT6aSession session, JReportingInformationRequest request, JReportingInformationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param session
   * @param request
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void doMO_DataAnswerEvent(ClientT6aSession session, JMO_DataRequest request, JMO_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param session
   * @param request
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void doMT_DataRequestEvent(ClientT6aSession session, JMT_DataRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param session
   * @param request
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void doConnectionManagementAnswerEvent(ClientT6aSession session, JConnectionManagementRequest request, JConnectionManagementAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param session
   * @param request
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void doConnectionManagementRequestEvent(ClientT6aSession session, JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


}
