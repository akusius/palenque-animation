package hu.akusius.palenque.animation.op;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Egy nyomógomb típusú tulajdonság.
 * @author Bujdosó Ákos
 */
public class PropToggle extends Prop {

  private boolean selected;

  public static final String PROP_SELECTED = "selected";

  /**
   * Létrehozás.
   * @param selected A kiválasztottság.
   */
  public PropToggle(boolean selected) {
    this.selected = selected;
  }

  /**
   * @return {@code true}, ha ki van választva a tulajdonság.
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * A kiválasztottság váltása.
   * @param selected Az új kiválasztottsági érték.
   * @throws IllegalStateException Nincs engedélyezve a művelet.
   */
  public void setSelected(boolean selected) throws IllegalStateException {
    if (!this.isEnabled() || (inGroup && selected == false)) {
      throw new IllegalStateException();
    }
    this.setSelectedInternal(selected);
  }

  /**
   * A kiválasztottság váltása.
   * @param selected Az új kiválasztottsági érték.
   */
  void setSelectedInternal(boolean selected) {
    boolean oldSelected = this.selected;
    this.selected = selected;
    propertyChangeSupport.firePropertyChange(PROP_SELECTED, oldSelected, selected);
  }

  private boolean inGroup = false;

  public boolean isInGroup() {
    return inGroup;
  }

  static final void configGroup(final PropToggle... toggles) {
    PropertyChangeListener listener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PROP_SELECTED.equals(evt.getPropertyName()) && (boolean) evt.getNewValue()) {
          for (PropToggle toggle : toggles) {
            if (evt.getSource() != toggle) {
              toggle.setSelectedInternal(false);
            }
          }
        }
      }
    };
    for (PropToggle toggle : toggles) {
      if (toggle.isInGroup()) {
        throw new IllegalArgumentException();
      }
      toggle.inGroup = true;
      toggle.addPropertyChangeListener(listener);
    }
  }
}
