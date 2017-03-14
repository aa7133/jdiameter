package org.jdiameter.common.impl.app.t6a;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.t6a.events.JMO_DataAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class JMO_DataAnswerImpl  extends AppAnswerEventImpl implements JMO_DataAnswer {
  private static final long serialVersionUID = 1L;

  /**
   *
   * @param answer
   */
  public JMO_DataAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   *
   * @param request
   * @param resultCode
   */
  public JMO_DataAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

}
