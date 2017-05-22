package org.jdiameter.common.impl.app.t6a;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.t6a.events.JMT_DataAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class JMT_DataAnswerImpl extends AppAnswerEventImpl implements JMT_DataAnswer {
  private static final long serialVersionUID = 1L;

  /**
   *
   * @param answer
   */
  public JMT_DataAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   *
   * @param request
   * @param resultCode
   */
  public JMT_DataAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

}
