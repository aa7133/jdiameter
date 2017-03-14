package org.jdiameter.common.impl.app.t6a;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.t6a.events.JConnectionManagementAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class JConnectionManagementAnswerImpl extends AppAnswerEventImpl implements JConnectionManagementAnswer {
  private static final long serialVersionUID = 1L;

  /**
   *
   * @param answer
   */
  public JConnectionManagementAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   *
   * @param request
   * @param resultCode
   */
  public JConnectionManagementAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }


}
