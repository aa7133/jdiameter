package org.jdiameter.common.api.app.t6a;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.t6a.events.JConfigurationInformationAnswer;
import org.jdiameter.api.t6a.events.JConfigurationInformationRequest;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.api.t6a.events.JConnectionManagementRequest;
import org.jdiameter.api.t6a.events.JMO_DataAnswer;
import org.jdiameter.api.t6a.events.JMO_DataRequest;
import org.jdiameter.api.t6a.events.JMT_DataAnswer;
import org.jdiameter.api.t6a.events.JMT_DataRequest;
import org.jdiameter.api.t6a.events.JReportingInformationAnswer;
import org.jdiameter.api.t6a.events.JReportingInformationRequest;

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public interface IT6aMessageFactory {
  /**
   *
   * @param request Request
   * @return JConfigurationInformationRequest
   */
  JConfigurationInformationRequest createConfigurationInformationRequest(Request request);

  /**
   *
   * @param answer Answer
   * @return JConfigurationInformationAnswer
   */
  JConfigurationInformationAnswer createConfigurationInformationAnswer(Answer answer);

  /**
   *
   * @param request Request
   * @return JReportingInformationRequest
   */
  JReportingInformationRequest createReportingInformationRequest(Request request);

  /**
   *
   * @param answer Answer
   * @return JReportingInformationAnswer
   */
  JReportingInformationAnswer createReportingInformationAnswer(Answer answer);

  /**
   *
   * @param request
   * @return
   */
  JConnectionManagementRequest createConnectionManagementRequest(Request request);

  /**
   *
   * @param answer
   * @return
   */
  JConnectionManagementAnswer createConnectionManagementAnswer(Answer answer);

  /**
   *
   * @param request
   * @return
   */
  JMO_DataRequest createMO_DataRequest(Request request);

  /**
   *
   * @param answer
   * @return
   */
  JMO_DataAnswer createMO_DataAnswer(Answer answer);

  /**
   *
   * @param request
   * @return
   */
  JMT_DataRequest createMT_DataRequest(Request request);

  /**
   *
   * @param answer
   * @return
   */
  JMT_DataAnswer createMT_DataAnswer(Answer answer);

  /**
   *
   * @return
   */
  long getApplicationId();

}
