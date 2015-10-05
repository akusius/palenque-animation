package hu.akusius.palenque.animation.util;

import java.util.EventObject;

/**
 * Osztály a kettős paraméterű események generikus kezeléséhez.
 * @param <T1> Az első paraméter típusa.
 * @param <T2> A második paraméter típusa.
 * @author Bujdosó Ákos
 */
public class Event2<T1, T2> extends EventObject {

  private final T1 param1;

  private final T2 param2;

  /**
   * Egy új példány létrehozása.
   * @param source Az esemény forrása.
   * @param param1 Az első paraméter.
   * @param param2 A második paraméter.
   */
  public Event2(Object source, T1 param1, T2 param2) {
    super(source);
    this.param1 = param1;
    this.param2 = param2;
  }

  /**
   * Visszaadja az esemény első paraméterét.
   * @return Az esemény első paramétere.
   */
  public T1 getParam1() {
    return param1;
  }

  /**
   * Visszaadja az esemény második paraméterét.
   * @return Az esemény második paramétere.
   */
  public T2 getParam2() {
    return param2;
  }
}
