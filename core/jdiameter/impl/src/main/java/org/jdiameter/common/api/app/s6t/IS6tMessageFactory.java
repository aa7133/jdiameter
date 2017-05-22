package org.jdiameter.common.api.app.s6t;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s6t.events.JConfigurationInformationAnswer;
import org.jdiameter.api.s6t.events.JConfigurationInformationRequest;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;
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
 */
public interface IS6tMessageFactory {
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
  JReportingInformationAnswer  createReportingInformationAnswer(Answer answer);

    /**
     *
     * @param request Request
     * @return JNIDDInformationRequest
     */
  JNIDDInformationRequest createNIDDInformationRequest(Request request);

    /**
     *
     * @param answer Answer
     * @return JNIDDInformationAnswer
     */
  JNIDDInformationAnswer createNIDDInformationAnswer(Answer answer);


    /**
     *
     * @return long
     */
  long getApplicationId();
}
