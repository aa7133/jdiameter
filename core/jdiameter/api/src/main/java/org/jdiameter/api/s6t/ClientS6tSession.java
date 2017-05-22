package org.jdiameter.api.s6t;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/2/17.
 *
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 *
 */
public interface ClientS6tSession extends AppSession, StateMachine {

  /**
   *
   * @param request Configuration-Information-Request event instance
   * @throws InternalException The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid)
   * @throws RouteException The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException The OverloadException signals that destination host is overloaded.
  */
  void sendConfigurationInformationRequest(JConfigurationInformationRequest request)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     *
     * @param answer JReportingInformationAnswer
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid)
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void sendReportingInformationAnswer(JReportingInformationAnswer answer)
             throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


    /**
     * @apiNote Used when no registration to MME and we get message on T6a that there is a message and we will take it
     *           from HSS
     * @param request JNIDDInformationRequest
     * @throws InternalException  The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid)
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void sendNIDDInformationRequest(JNIDDInformationRequest request)
             throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
