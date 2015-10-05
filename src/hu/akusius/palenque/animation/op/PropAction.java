package hu.akusius.palenque.animation.op;

import hu.akusius.palenque.animation.util.Event1;
import hu.akusius.palenque.animation.util.Event1Support;
import hu.akusius.palenque.animation.util.EventListener;

/**
 * Egy művelettulajdonság.
 * @author Bujdosó Ákos
 */
public class PropAction extends Prop {

  /**
   * A ténylegesen végrehajtandó művelet.
   * Alapesetben értesít a művelet végrehajtásáról.
   * @param param A művelethez csatolt paraméter.
   */
  protected void action(Object param) {
    actionPerformedSupport.fireEvent(param);
  }

  /**
   * A művelet végrehajtása.
   * @throws IllegalStateException A művelet nem engedélyezett.
   */
  public void performAction() throws IllegalStateException {
    this.performAction(null);
  }

  /**
   * A művelet végrehajtása.
   * @param param A művelethez csatolt paraméter.
   * @throws IllegalStateException A művelet nem engedélyezett.
   */
  public void performAction(Object param) throws IllegalStateException {
    if (!this.isEnabled()) {
      throw new IllegalStateException();
    }
    this.action(param);
  }

  private final Event1Support<Object> actionPerformedSupport = new Event1Support<>(this);

  public void addActionPerformedListener(EventListener<Event1<Object>> listener) {
    this.actionPerformedSupport.addEventListener(listener);
  }

  public void removeActionPerformedListener(EventListener<Event1<Object>> listener) {
    this.actionPerformedSupport.removeEventListener(listener);
  }
}
