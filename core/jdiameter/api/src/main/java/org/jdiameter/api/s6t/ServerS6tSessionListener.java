package org.jdiameter.api.s6t;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.api.s6t.events.JNIDDInformationRequest;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;
import org.jdiameter.api.s6t.events.JReportingInformationRequest;

/**
 * Created by Adi Enzel on 3/2/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 *
 */
public interface ServerS6tSessionListener {

  /**
   *
   * @param session
   * @param request
   * @param answer
   * @throws InternalException  The InternalException signals that internal error is occurred.
   * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
   * @throws RouteException  The NoRouteException signals that no route exist for a given realm.
   * @throws OverloadException  The OverloadException signals that destination host is overloaded.
     */
  void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     *
     * @param session ServerS6tSession
     * @param request JConfigurationInformationRequest
     * @throws InternalException  The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void doConfigurationInformationRequestEvent(ServerS6tSession session, JConfigurationInformationRequest request)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     *
     * @param session ServerS6tSession
     * @param request JReportingInformationRequest
     * @param answer JReportingInformationAnswer
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void doReportingInformationAnswerEvent(ServerS6tSession session, JReportingInformationRequest request, JReportingInformationAnswer answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;

    /**
     *
     * @param session ServerS6tSession
     * @param request JNIDDInformationRequest
     * @throws InternalException The InternalException signals that internal error is occurred.
     * @throws IllegalDiameterStateException The IllegalStateException signals that session has incorrect state (invalid).
     * @throws RouteException The NoRouteException signals that no route exist for a given realm.
     * @throws OverloadException The OverloadException signals that destination host is overloaded.
     */
  void doNIDDInformationRequestEvent(ServerS6tSession session, JNIDDInformationRequest request)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException;


}
