package hu.akusius.palenque.animation.op;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Timer;

/**
 * A műveleteket összefogó és kezelő osztály.
 * @author Bujdosó Ákos
 */
public class OperationManager {

  private final PlayManager playManager;

  private final ModeManager modeManager;

  private final DisplayManager displayManager;

  private final PropAction resetAction;

  private final PropToggle showGridSystemToggle;

  private final PropAction screenshotAction;

  private final PropAction infoAction;

  public OperationManager() {
    this(null);
  }

  /**
   * Létrehozás saját {@link Timer}-rel, főleg tesztelési célból.
   * @param playerTimer A használt időzítő.
   */
  OperationManager(Timer playerTimer) {
    this.playManager = playerTimer == null ? new PlayManager() : new PlayManager(playerTimer);
    this.playManager.getPlayingToggle().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          refreshStates();
        }
      }
    });
    this.modeManager = new ModeManager();
    this.displayManager = new DisplayManager(playManager);
    this.showGridSystemToggle = new PropToggle(false);
    this.screenshotAction = new PropAction();
    this.infoAction = new PropAction();
    this.resetAction = new PropAction() {
      @Override
      protected void action(Object param) {
        OperationMode mode = modeManager.getMode();
        switch (mode) {
          case Playing:
            PropAction tsa = playManager.getToStartAction();
            if (tsa.isEnabled()) {
              tsa.performAction();
            }
            break;
          case Moving:
            displayManager.resetTranslate();
            break;
          case Rotating:
            displayManager.resetRotate();
            break;
          case Zooming:
            displayManager.resetZoom();
            break;
          case Combined:
            displayManager.resetTranslate();
            displayManager.resetRotate();
            displayManager.resetZoom();
            break;
          default:
            throw new AssertionError();
        }

      }
    };
  }

  /**
   * @return
   */
  public PlayManager getPlayManager() {
    return playManager;
  }

  /**
   * @return
   */
  public ModeManager getModeManager() {
    return modeManager;
  }

  /**
   * @return
   */
  public DisplayManager getDisplayManager() {
    return displayManager;
  }

  /**
   * @return Az alaphelyzetekbe állítást kezelő művelet.
   */
  public PropAction getResetAction() {
    return resetAction;
  }

  /**
   * @return A koordinátarendszer megjelenítését vezérlő tulajdonság.
   */
  public PropToggle getShowGridSystemToggle() {
    return showGridSystemToggle;
  }

  /**
   * @return A képernyőkép mentését vezérlő művelet.
   */
  public PropAction getScreenshotAction() {
    return screenshotAction;
  }

  /**
   * @return Az információk megjelenítését kezelő művelet.
   */
  public PropAction getInfoAction() {
    return infoAction;
  }

  private void refreshStates() {
    boolean playing = playManager.getPlayingToggle().isSelected();

    screenshotAction.setEnabled(!playing);
    infoAction.setEnabled(!playing);
  }
}
