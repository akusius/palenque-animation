package hu.akusius.palenque.animation.ui;

import hu.akusius.palenque.animation.op.*;
import hu.akusius.palenque.animation.util.Event1;
import hu.akusius.palenque.animation.util.EventListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

/**
 * A fő panel.
 * @author Bujdosó Ákos
 */
public class MainPanel extends JPanel {

  private final OperationManager operationManager;

  private final DialogParent dialogParent;

  private DisplayPanel displayPanel;

  private ToolbarPanel toolbarPanel;

  public MainPanel(OperationManager operationManager, DialogParent dp) {
    this.operationManager = operationManager;
    this.dialogParent = dp;
    initComponents();
    initHotkeys();
    hookEvents();

    ToolTipManager.sharedInstance().setInitialDelay(200);
    ToolTipManager.sharedInstance().setReshowDelay(2000);
  }

  private void initComponents() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    displayPanel = new DisplayPanel(this.operationManager);
    displayPanel.setPreferredSize(new Dimension(600, 600));
    add(displayPanel);

    toolbarPanel = new ToolbarPanel(this.operationManager);
    toolbarPanel.setPreferredSize(new Dimension(600, 40));
    toolbarPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
    toolbarPanel.setMinimumSize(new Dimension(Short.MIN_VALUE, 40));
    add(toolbarPanel);
  }

  private void initHotkeys() {
    PlayManager pm = operationManager.getPlayManager();
    addPropHotkey(pm.getPlayingToggle(), "SPACE");
    addPropHotkey(pm.getToStartAction(), "HOME");
    addPropHotkey(pm.getToEndAction(), "END");
    addPropHotkey(pm.getSpeedDownAction(), "DOWN");
    addPropHotkey(pm.getSpeedUpAction(), "UP");
    addPropHotkey(pm.getSkipBackAction(), "LEFT");
    addPropHotkey(pm.getSkipForwardAction(), "RIGHT");
    addPropHotkey(pm.getSkipBackAction(), (Object) "small", "shift LEFT");
    addPropHotkey(pm.getSkipForwardAction(), (Object) "small", "shift RIGHT");
    addPropHotkey(pm.getSkipBackAction(), (Object) "large", "ctrl LEFT", "meta LEFT");
    addPropHotkey(pm.getSkipForwardAction(), (Object) "large", "ctrl RIGHT", "meta RIGHT");
    addPropHotkey(pm.getSkipBackAction(), (Object) "step", "alt LEFT");
    addPropHotkey(pm.getSkipForwardAction(), (Object) "step", "alt RIGHT");

    ModeManager mm = operationManager.getModeManager();
    addPropHotkey(mm.getPlayingToggle(), "P", "1");
    addPropHotkey(mm.getMovingToggle(), "M", "2");
    addPropHotkey(mm.getRotatingToggle(), "R", "3");
    addPropHotkey(mm.getZoomingToggle(), "Z", "4");
    addPropHotkey(mm.getCombinedToggle(), "C", "5");

    DisplayManager dm = operationManager.getDisplayManager();
    addPropHotkey(operationManager.getResetAction(), "BACK_SPACE");
    addPropHotkey(dm.getAutoToggle(), "A");
    addPropHotkey(operationManager.getShowGridSystemToggle(), "G", "6");
    addPropHotkey(operationManager.getScreenshotAction(), "S");
    addPropHotkey(operationManager.getInfoAction(), "alt I");
  }

  private void addPropHotkey(Prop prop, String... keys) {
    addPropHotkey(prop, null, getKeyStrokes(keys));
  }

  private void addPropHotkey(Prop prop, Object param, String... keys) {
    addPropHotkey(prop, param, getKeyStrokes(keys));
  }

  private void addPropHotkey(final Prop prop, final Object param, KeyStroke[] keys) {
    final Action action;
    if (prop instanceof PropAction) {
      action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (prop.isEnabled()) {
            ((PropAction) prop).performAction(param);
          }
        }
      };
    } else if (prop instanceof PropToggle) {
      action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          PropToggle toggle = (PropToggle) prop;
          if (toggle.isEnabled()) {
            if (toggle.isInGroup()) {
              toggle.setSelected(true);
            } else {
              toggle.setSelected(!toggle.isSelected());
            }
          }
        }
      };
    } else {
      throw new IllegalArgumentException();
    }
    addActionHotkey(action, keys);
  }

  private final Set<KeyStroke> usedHotkeys = new HashSet<>(50);

  private void addActionHotkey(Action action, KeyStroke[] keys) {
    if (action == null || keys == null || keys.length <= 0) {
      throw new IllegalArgumentException();
    }

    Object actionObj = new Object();
    for (KeyStroke key : keys) {
      if (usedHotkeys.contains(key)) {
        throw new IllegalArgumentException("Key is already attached: " + key);
      }
      usedHotkeys.add(key);
      getInputMap(WHEN_IN_FOCUSED_WINDOW).put(key, actionObj);
    }
    getActionMap().put(actionObj, action);
  }

  private KeyStroke[] getKeyStrokes(String... keys) {
    KeyStroke[] keyStrokes = new KeyStroke[keys.length];
    for (int i = 0; i < keys.length; i++) {
      keyStrokes[i] = KeyStroke.getKeyStroke(keys[i]);
    }
    return keyStrokes;
  }

  private void hookEvents() {
    operationManager.getScreenshotAction().addActionPerformedListener(new EventListener<Event1<Object>>() {
      @Override
      public void notify(Event1<Object> e) {
        ImageSaver.saveImage(displayPanel.asImageSource(), dialogParent);
      }
    });
    operationManager.getInfoAction().addActionPerformedListener(new EventListener<Event1<Object>>() {
      @Override
      public void notify(Event1<Object> e) {
        new InfoDialog(dialogParent.getWindow()).setVisible(true);
      }
    });
  }
}
