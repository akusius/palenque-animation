package hu.akusius.palenque.animation.util;

/**
 * Támogatás az {@link Event1} típusú eseményobjektumokat váró eseménykezelőkhöz.
 * @param <T>
 * @author Bujdosó Ákos
 */
public final class Event1Support<T> extends EventSupport<Event1<T>> {

  /**
   * Létrehozás.
   * @param source Az események forrása.
   */
  public Event1Support(Object source) {
    super(source);
  }

  /**
   * Esemény jelzése.
   * @param param Az esemény paramétere.
   */
  public void fireEvent(T param) {
    fireEvent1(param);
  }
}
