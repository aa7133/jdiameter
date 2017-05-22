package org.jdiameter.client.impl.app.t6a;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;

/*
 * Copyright (c) 2017. AT&T Intellectual Property. All rights reserved
 */

/**
 * Created by Adi Enzel on 3/13/17.
 *
 * @author <a href="mailto:aa7133@att.com"> Adi Enzel </a>
 */
public class Event implements StateEvent {

  public enum Type {
    SEND_MESSAGE,
    TIMEOUT_EXPIRES,
    RECEIVE_CIA,
    RECEIVE_CIR,
    RECEIVE_RIA,
    RECEIVE_CMR,
    RECEIVE_CMA,
    RECEIVE_ODA,
    RECEIVE_TDR
  }

  AppEvent request;
  AppEvent answer;
  Type type;

  Event(Type type, AppEvent request, AppEvent answer) {
    this.type = type;
    this.request = request;
    this.answer = answer;
  }

  @Override
  public <E> E encodeType(Class<E> eClass) {
    return eClass == Type.class ? (E) type : null;
  }

  @Override
  public Enum getType() {
    return type;
  }

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }

  @Override
  public Object getData() {
    return request != null ? request : answer;
  }

  @Override
  public void setData(Object data) {
    try {
      if (((AppEvent) data).getMessage().isRequest()) {
        request = (AppEvent) data;
      }
      else {
        answer = (AppEvent) data;
      }
    }
    catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
