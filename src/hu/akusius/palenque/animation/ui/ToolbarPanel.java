package hu.akusius.palenque.animation.ui;

import hu.akusius.palenque.animation.op.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel a vezérlőgombok és -elemek megjelenítéséhez.
 * @author Bujdosó Ákos
 */
public class ToolbarPanel extends JPanel {

  private final OperationManager om;

  public ToolbarPanel(OperationManager operationManager) {
    this.om = operationManager;
    this.initComponents();
  }

  private void initComponents() {
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.setBorder(new BevelBorder(BevelBorder.LOWERED));

    this.add(Box.createHorizontalStrut(5));

    PlayManager pm = om.getPlayManager();
    this.add(newToggleButton(pm.getPlayingToggle(), "play", "Play / stop (SPACE)"));
    this.add(Box.createHorizontalStrut(10));

    this.add(newActionButton(pm.getToStartAction(), "start", "To start (HOME)"));
    this.add(Box.createHorizontalStrut(3));
    JSlider slFrame = newSlider(pm.getFrameSlider());
    slFrame.setToolTipText("Frame (←/→)");
    this.add(slFrame);
    this.add(Box.createHorizontalStrut(3));
    this.add(newActionButton(pm.getToEndAction(), "end", "To end (END)"));

    this.add(Box.createHorizontalStrut(10));

    ModeManager mm = om.getModeManager();
    JToggleButton btnPlaying = newToggleButton(mm.getPlayingToggle(), "playing", "Play mode (P, 1)");
    this.add(btnPlaying);
    JToggleButton btnMoving = newToggleButton(mm.getMovingToggle(), "move", "Move mode (M, 2)");
    this.add(btnMoving);
    JToggleButton btnRotating = newToggleButton(mm.getRotatingToggle(), "rotate", "Rotate mode (R, 3)");
    this.add(btnRotating);
    JToggleButton btnZooming = newToggleButton(mm.getZoomingToggle(), "zoom", "Zoom mode (Z, 4)");
    this.add(btnZooming);
    JToggleButton btnCombined = newToggleButton(mm.getCombinedToggle(), "combined", "Combined mode: move, rotate, zoom (C, 5)");
    this.add(btnCombined);

    ButtonGroup modes = new ButtonGroup();
    modes.add(btnPlaying);
    modes.add(btnMoving);
    modes.add(btnRotating);
    modes.add(btnZooming);
    modes.add(btnCombined);

    this.add(Box.createHorizontalStrut(10));

    this.add(newActionButton(om.getResetAction(), "reset", "Reset state (BACKSPACE)"));
    DisplayManager dm = om.getDisplayManager();
    this.add(newToggleButton(dm.getAutoToggle(), "auto", "Automatic camera move (A)"));
    this.add(newToggleButton(om.getShowGridSystemToggle(), "grid", "Grid (G, 6)"));

    this.add(Box.createHorizontalStrut(10));

    JSlider slSpeed = newSlider(pm.getSpeedSlider());
    slSpeed.setToolTipText("Speed (↑/↓)");
    slSpeed.setPreferredSize(new Dimension(60, 30));
    slSpeed.setMaximumSize(new Dimension(60, 30));
    this.add(slSpeed);

    this.add(Box.createHorizontalStrut(10));

    this.add(newActionButton(om.getScreenshotAction(), "scshot", "Screenshot (S)"));
    this.add(newActionButton(om.getInfoAction(), "info", "Information (Alt-I)"));

    this.add(Box.createHorizontalStrut(5));
  }

  private static JButton newActionButton(final PropAction prop, String iconName, String tooltipText) {
    final JButton btn = new JButton("");
    setAbstractButton(btn, iconName, tooltipText);
    btn.setEnabled(prop.isEnabled());
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        prop.performAction();
      }
    });
    prop.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
          case PropAction.PROP_ENABLED:
            btn.setEnabled(prop.isEnabled());
            break;
        }
      }
    });
    return btn;
  }

  private static JToggleButton newToggleButton(final PropToggle prop, String iconName, String tooltipText) {
    final JToggleButton btn = new JToggleButton();
    setAbstractButton(btn, iconName, tooltipText);
    btn.setSelected(prop.isSelected());
    btn.setEnabled(prop.isEnabled());
    btn.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (prop.isEnabled()) {
          if (prop.isInGroup()) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              prop.setSelected(true);
            }
          } else {
            prop.setSelected(e.getStateChange() == ItemEvent.SELECTED);
          }
        }
      }
    });
    prop.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
          case PropToggle.PROP_ENABLED:
            btn.setEnabled(prop.isEnabled());
            break;
          case PropToggle.PROP_SELECTED:
            btn.setSelected(prop.isSelected());
            break;
        }
      }
    });
    return btn;
  }

  private static void setAbstractButton(AbstractButton button, String iconName, String tooltipText) {
    button.setMinimumSize(new Dimension(30, 30));
    button.setPreferredSize(new Dimension(30, 30));
    button.setMaximumSize(new Dimension(30, 30));
    button.setFocusable(false);

    if (iconName != null) {
      ImageIcon icon = IconFactory.readIcon(iconName + ".png");
      if (icon != null) {
        button.setIcon(icon);
      }
    }

    if (tooltipText != null) {
      button.setToolTipText(tooltipText);
    }
  }

  private static JSlider newSlider(final PropSlider prop) {
    final JSlider sl = new JSlider(prop.getMin(), prop.getMax(), prop.getValue());
    sl.setFocusable(false);
    sl.setEnabled(prop.isEnabled());
    sl.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (prop.isEnabled()) {
          prop.setValue(sl.getValue());
        }
      }
    });
    prop.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
          case PropSlider.PROP_ENABLED:
            sl.setEnabled(prop.isEnabled());
            break;
          case PropSlider.PROP_VALUE:
            sl.setValue(prop.getValue());
            break;
        }
      }
    });
    return sl;
  }
}
