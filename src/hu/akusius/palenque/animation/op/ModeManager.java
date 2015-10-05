package hu.akusius.palenque.animation.op;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Osztály az aktuális működési mód kezeléséhez.
 * @author Bujdosó Ákos
 */
public class ModeManager {

  private final PropToggle playingToggle;

  private final PropToggle movingToggle;

  private final PropToggle rotatingToggle;

  private final PropToggle zoomingToggle;

  private final PropToggle combinedToggle;

  public ModeManager() {
    playingToggle = new PropToggle(false);
    movingToggle = new PropToggle(false);
    rotatingToggle = new PropToggle(false);
    zoomingToggle = new PropToggle(false);
    combinedToggle = new PropToggle(true);
    PropToggle.configGroup(playingToggle, movingToggle, rotatingToggle, zoomingToggle, combinedToggle);
    mode = OperationMode.Combined;

    PropertyChangeListener toggleSelected = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName()) && (boolean) evt.getNewValue()) {
          if (evt.getSource() == playingToggle) {
            setMode(OperationMode.Playing);
          } else if (evt.getSource() == movingToggle) {
            setMode(OperationMode.Moving);
          } else if (evt.getSource() == rotatingToggle) {
            setMode(OperationMode.Rotating);
          } else if (evt.getSource() == zoomingToggle) {
            setMode(OperationMode.Zooming);
          } else if (evt.getSource() == combinedToggle) {
            setMode(OperationMode.Combined);
          }
        }
      }
    };

    playingToggle.addPropertyChangeListener(toggleSelected);
    movingToggle.addPropertyChangeListener(toggleSelected);
    rotatingToggle.addPropertyChangeListener(toggleSelected);
    zoomingToggle.addPropertyChangeListener(toggleSelected);
    combinedToggle.addPropertyChangeListener(toggleSelected);
  }

  public PropToggle getPlayingToggle() {
    return playingToggle;
  }

  public PropToggle getMovingToggle() {
    return movingToggle;
  }

  public PropToggle getRotatingToggle() {
    return rotatingToggle;
  }

  public PropToggle getZoomingToggle() {
    return zoomingToggle;
  }

  public PropToggle getCombinedToggle() {
    return combinedToggle;
  }

  private OperationMode mode;

  public static final String PROP_MODE = "mode";

  public OperationMode getMode() {
    return mode;
  }

  private void setMode(OperationMode mode) {
    OperationMode oldMode = this.mode;
    this.mode = mode;
    propertyChangeSupport.firePropertyChange(PROP_MODE, oldMode, mode);
  }

  private boolean enabled = true;

  /**
   * @return A jelenlegi engedélyezettségi állapot.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Az engedélyezettségi állapot beállítása.
   * @param enabled Az új engedélyezettségi állapot.
   */
  public void setEnabled(boolean enabled) {
    if (this.enabled == enabled) {
      return;
    }
    this.enabled = enabled;
    refreshStates();
  }

  private void refreshStates() {
    this.playingToggle.setEnabledInternal(enabled);
    this.movingToggle.setEnabledInternal(enabled);
    this.rotatingToggle.setEnabledInternal(enabled);
    this.zoomingToggle.setEnabledInternal(enabled);
    this.combinedToggle.setEnabledInternal(enabled);
  }

  private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

}
