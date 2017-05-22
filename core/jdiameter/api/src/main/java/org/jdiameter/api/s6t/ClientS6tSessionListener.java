package org.jdiameter.api.s6t;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;
import org.jdiameter.api.s6t.events.JNIDDInformationAnswer;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;


/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/2/17.
 *
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 *
 */
public interface ClientS6tSessionListener {

  /**
   *
   * @param session AppSession
   * @param request AppRequestEvent
   * @param answer AppAnswerEvent
   * @throws InternalException  The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid)
   * @throws RouteException  The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

  /**
   *
   * @param session ClientS6tSession
   * @param request JConfigurationInformationRequest
   * @param answer  JConfigurationInformationAnswer
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid)
   * @throws RouteException  The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
   */
  void doConfigurationInformationAnswerEvent(ClientS6tSession session, JConfigurationInformationRequest request, JConfigurationInformationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     *
     * @param session ClientS6tSession
     * @param request JReportingInformationRequest
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid)
     * @throws RouteException The NoRouteException signals that no route exist for a given realm
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void doReportingInformationRequestEvent(ClientS6tSession session, JReportingInformationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


    /**
     *
     * @param session ClientS6tSession
     * @param request JNIDDInformationRequest
     * @param answer JNIDDInformationAnswer
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid)
     * @throws RouteException The NoRouteException signals that no route exist for a given realm
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void doNIDDInformationAnswerEvent(ClientS6tSession session, JNIDDInformationRequest request, JNIDDInformationAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
