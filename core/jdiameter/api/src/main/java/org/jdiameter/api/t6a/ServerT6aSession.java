package org.jdiameter.api.t6a;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JReportingInformationAnswer;
import org.jdiameter.api.t6a.events.JMO_DataAnswer;
import org.jdiameter.api.t6a.events.JMT_DataRequest;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;


/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface ServerT6aSession  extends AppSession, StateMachine {
  /**
   * Both client and server utilize CIR
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
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendConfigurationInformationAnswer(JConfigurationInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   * SCEF to MME
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendReportingInformationAnswer(JReportingInformationAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


  /**
   *
   * @param answer
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendMO_DataAnswer(JMO_DataAnswer answer)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param request
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   */
  void sendMT_DataRequest(JMT_DataRequest request)
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
