package hu.akusius.palenque.animation.util;

/**
 * Támogatás az {@link Event2} típusú eseményobjektumokat váró eseménykezelőkhöz.
 * @param <T1> Az első paraméter típusa.
 * @param <T2> A második paraméter típusa.
 * @author Bujdosó Ákos
 */
public final class Event2Support<T1, T2> extends EventSupport<Event2<T1, T2>> {

  /**
   * Létrehozás.
   * @param source Az események forrása.
   */
  public Event2Support(Object source) {
    super(source);
  }

  /**
   * Esemény jelzése.
   * @param param1 Az esemény első paramétere.
   * @param param2 Az esemény második paramétere.
   */
  public void fireEvent(T1 param1, T2 param2) {
    fireEvent2(param1, param2);
  }
}
