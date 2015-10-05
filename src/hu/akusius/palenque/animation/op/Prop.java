package hu.akusius.palenque.animation.op;

import java.beans.*;

/**
 * Egy összetett tulajdonság alaposztálya.
 * @author Bujdosó Ákos
 */
public abstract class Prop {

  private boolean enabled = true;

  public static final String PROP_ENABLED = "enabled";

  /**
   * @return {@code true}, ha engedélyezve van.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Engedélyezettség váltása. Csak a package-en belül érhető el.
   * @param enabled Az új engedélyezettségi érték.
   */
  void setEnabled(boolean enabled) {
    this.setEnabledInternal(enabled);
  }

  /**
   * Engedélyezettség váltása.
   * @param enabled Az új engedélyezettségi érték.
   */
  void setEnabledInternal(boolean enabled) {
    boolean oldEnabled = this.enabled;
    this.enabled = enabled;
    propertyChangeSupport.firePropertyChange(PROP_ENABLED, oldEnabled, enabled);
  }

  protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

}
