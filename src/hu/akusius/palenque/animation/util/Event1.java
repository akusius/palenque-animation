package hu.akusius.palenque.animation.util;

import java.util.EventObject;

/**
 * Osztály az egyszeres paraméterű események generikus kezeléséhez.
 * @param <T> A paraméter típusa.
 * @author Bujdosó Ákos
 */
public final class Event1<T> extends EventObject {

  private final T param;

  /**
   * Egy új példány létrehozása.
   * @param source Az esemény forrása.
   * @param param Az esemény paramétere.
   */
  public Event1(Object source, T param) {
    super(source);
    this.param = param;
  }

  /**
   * Visszaadja az esemény paraméterét.
   * @return Az esemény paramétere.
   */
  public T getParam() {
    return param;
  }
}
