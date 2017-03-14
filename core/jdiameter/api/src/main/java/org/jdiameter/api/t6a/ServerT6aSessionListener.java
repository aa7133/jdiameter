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
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JMO_DataRequest;
import org.jdiameter.api.t6a.events.JMT_DataAnswer;
import org.jdiameter.api.t6a.events.JMT_DataRequest;
import org.jdiameter.api.t6a.events.JReportingInformationRequest;

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface ServerT6aSessionListener {
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
  void doSendConfigurationInformationAnswerEvent(ServerT6aSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer)
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
  void doSendConfigurationInformationRequestEvent(ServerT6aSession session, JConfigurationInformationRequest request)
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
  void doSendReportingInformationRequestEvent(ServerT6aSession session, JReportingInformationRequest request)
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
  void doSendMO_DataRequestEvent(ServerT6aSession session, JMO_DataRequest request)
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
  void doSendMT_DataAnswertEvent(ServerT6aSession session, JMT_DataRequest request, JMT_DataAnswer answer)
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
  void doSendConnectionManagementAnswertEvent(ServerT6aSession session, JConnectionManagementRequest request, JConnectionManagementAnswer answer)
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
  void doSendConnectionManagementRequestEvent(ServerT6aSession session, JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;



}
