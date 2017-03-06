package org.jdiameter.common.impl.app.s6t;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.s6t.events.JNIDDInformationAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

/**
 * Created by Adi Enzel on 3/5/17.
 */
public class JNIDDInformationAnswerImpl extends AppAnswerEventImpl implements JNIDDInformationAnswer {
  private static final long serialVersionUID = 1L;

  /**
   *
   * @param answer
   */
  public JNIDDInformationAnswerImpl(Answer answer) {
    super(answer);
  }

  /**
   *
   * @param request
   * @param resultCode
   */
  public JNIDDInformationAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }


}
