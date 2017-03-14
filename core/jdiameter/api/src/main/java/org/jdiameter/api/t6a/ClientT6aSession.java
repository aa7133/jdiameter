package org.jdiameter.api.t6a;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JReportingInformationRequest;
import org.jdiameter.api.t6a.events.JMO_DataRequest;
import org.jdiameter.api.t6a.events.JMT_DataAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;


/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface ClientT6aSession  extends AppSession, StateMachine {

  /**
   * Both client and server utilize CIR
   * MME to IWK-SCEF Monitoring-Event-Report may be added (also from SCEF to MME in server side)
   * @param request
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendConfigurationInformationRequest(JConfigurationInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * Both Client And Server utilize CIA
   * SCEF to MME and MME to IWK-SCEF
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendConfigurationInformationAnswer(JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * MME to SCEF
   * @param request
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendReportingInformationRequest(JReportingInformationRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param request
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendMODataRequest(JMO_DataRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendMTDataAnswer(JMT_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


  /**
   *
   * @param request
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendConnectionManagementRequest(JConnectionManagementRequest request)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendConnectionManagementAnswer(JConnectionManagementAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
