package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s6t.events.JReportingInformationAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/5/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */

public class JReportingInformationAnswerImpl extends AppAnswerEventImpl implements JReportingInformationAnswer {
  private static final long serialVersionUID = 1L;

  /**
   *
   * @param answer
   */
  public JReportingInformationAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   *
   * @param request
   * @param resultCode
   */
  public JReportingInformationAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }


}
