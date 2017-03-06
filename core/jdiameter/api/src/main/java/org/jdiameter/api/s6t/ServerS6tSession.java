package org.jdiameter.api.s6t;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateMachine;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JNIDDInformationAnswer;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;

/**
 * Created by Adi Enzel on 3/2/17.
 *
 *  @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 *
 */
public interface ServerS6tSession extends AppSession, StateMachine {

  /**
   *
   * @param answer JConfigurationInformationAnswer
   * @throws InternalException  The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException  The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException  The OverloadException signals that destination host is overloaded.
   */
  void sendConfigurationInformationAnswer(JConfigurationInformationAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     *
     * @param request JReportingInformationRequest
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void sendReportingInformationRequest(JReportingInformationRequest request)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


    /**
     *
     * @param answer JNIDDInformationAnswer
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void sendNIDDInformationAnswer(JNIDDInformationAnswer answer)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

}
