package org.jdiameter.common.impl.app.t6a;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.t6a.events.JReportingInformationAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

/**
 * Created by Adi Enzel on 3/5/17.
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
