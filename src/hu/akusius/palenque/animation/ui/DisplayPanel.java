package hu.akusius.palenque.animation.ui;

import hu.akusius.palenque.animation.op.*;
import hu.akusius.palenque.animation.rendering.FrameRenderer;
import hu.akusius.palenque.animation.rendering.GridSystemRenderer;
import hu.akusius.palenque.animation.rendering.Transformer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.other.Matrix;

/**
 * A megjelenítést végző panel.
 * @author Bujdosó Ákos
 */
public class DisplayPanel extends JPanel {

  public static final String FOOTER_TEXT = null;

  private final OperationManager om;

  private final DisplayManager dm;

  private final ModeManager mm;

  private final PlayManager pm;

  private final PropSliderFrame fs;

  private final MouseHandler mouseHandler;

  private final ImageSource imageSource;

  public DisplayPanel(OperationManager operationManager) {
    this.om = operationManager;
    this.dm = this.om.getDisplayManager();
    this.dm.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        repaint();
      }
    });
    this.mm = this.om.getModeManager();
    this.mm.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (ModeManager.PROP_MODE.equals(evt.getPropertyName())) {
          repaint();
        }
      }
    });
    this.pm = this.om.getPlayManager();
    this.fs = pm.getFrameSlider();
    this.fs.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropSliderFrame.PROP_VALUE.equals(evt.getPropertyName())) {
          repaint();
        }
      }
    });
    this.om.getShowGridSystemToggle().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          repaint();
        }
      }
    });

    this.mouseHandler = new MouseHandler();
    this.addMouseListener(mouseHandler);
    this.addMouseMotionListener(mouseHandler);

    this.imageSource = new ImageSource() {

      @Override
      public int getMinSize() {
        return 60;
      }

      @Override
      public int getMaxSize() {
        return 1200;
      }

      @Override
      public int getSuggestedSize() {
        Dimension dim = getSize();
        int size = Math.min(dim.width, dim.height);
        return Math.max(Math.min(size, getMaxSize()), getMinSize());
      }

      @Override
      public BufferedImage generateImage(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        Dimension dim = new Dimension(size, size);
        Transformer.adjustFL(dim);
        redraw((Graphics2D) graphics.create(), dim);
        Transformer.adjustFL(getSize());
        return image;
      }
    };

    this.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        Transformer.adjustFL(getSize());
        repaint();
      }
    });
  }

  ImageSource asImageSource() {
    return imageSource;
  }

  @Override
  protected void paintComponent(Graphics g) {
    redraw((Graphics2D) g, getSize());
  }

  void redraw(Graphics2D g, Dimension d) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, d.width, d.height);

    Matrix cameraMatrix = dm.getCameraMatrix();
    double zoom = dm.getZoom();

    if (om.getShowGridSystemToggle().isSelected()) {
      GridSystemRenderer.render(g, cameraMatrix, zoom, d);
    }

    g.setColor(Color.BLACK);
    FrameRenderer.renderFrame(fs.getCurrentFrameInfo(), g, cameraMatrix, zoom, d);

    if (FOOTER_TEXT != null) {
      drawFooterText(g, d, FOOTER_TEXT);
    }
  }

  private static final Color footerColor = new Color(100, 100, 255, 65);

  private static Font footerFont;

  private static void drawFooterText(Graphics g, Dimension d, String footerText) {
    g = g.create();
    int fontSize = Math.min(d.height, d.width) / 30;
    if (footerFont == null || footerFont.getSize() != fontSize) {
      footerFont = g.getFont().deriveFont(Font.ITALIC, (float) fontSize);
    }
    g.setFont(footerFont);

    FontMetrics fm = g.getFontMetrics();
    int width = fm.stringWidth(footerText);

    g.setColor(footerColor);
    g.drawString(footerText, d.width - width - 5, d.height - 5);
  }

  private final class MouseHandler extends MouseAdapter {

    private DraggingMode draggingMode = null;

    private int mx, my;

    @Override
    public void mousePressed(MouseEvent e) {
      int button = e.getButton();
      OperationMode mode = mm.getMode();

      if (mode == OperationMode.Playing) {
        if (button == MouseEvent.BUTTON1) {
          PropToggle pt = pm.getPlayingToggle();
          if (pt.isEnabled()) {
            pt.setSelected(!pt.isSelected());
          }
        } else {
          PropAction tsa = pm.getToStartAction();
          if (tsa.isEnabled()) {
            tsa.performAction();
          }
        }
      } else if (mode == OperationMode.Moving) {
        if (button == MouseEvent.BUTTON1) {
          startDragging(DraggingMode.Moving, e.getX(), e.getY());
        } else {
          dm.resetTranslate();
        }
      } else if (mode == OperationMode.Zooming) {
        if (button == MouseEvent.BUTTON1) {
          startDragging(DraggingMode.Zooming, e.getX(), e.getY());
        } else {
          dm.resetZoom();
        }
      } else if (mode == OperationMode.Rotating) {
        if (button == MouseEvent.BUTTON1) {
          startDragging(DraggingMode.Rotating, e.getX(), e.getY());
        } else {
          dm.resetRotate();
        }
      } else if (mode == OperationMode.Combined) {
        startDragging(DraggingMode.Combined, e.getX(), e.getY());
      }
    }

    private void startDragging(DraggingMode mode, int x, int y) {
      draggingMode = mode;
      mx = x;
      my = y;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      draggingMode = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      DraggingMode drm = draggingMode;
      if (drm != null) {
        int x = e.getX();
        int y = e.getY();

        if (drm == DraggingMode.Combined) {
          int mex = e.getModifiersEx();
          boolean b1down = (mex & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK;
          boolean b3down = (mex & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK;

          if (b1down && b3down) {
            // Mindkettő le van nyomva
            drm = DraggingMode.Zooming;
          } else if (b1down) {
            // Csak az 1-es van lenyomva
            drm = DraggingMode.Moving;
          } else if (b3down) {
            // Csak a 3-as van lenyomva
            drm = DraggingMode.Rotating;
          }
        }

        if (drm == DraggingMode.Moving) {
          dm.translate(.1 * (double) (x - mx), -.1 * (double) (y - my));
        } else if (drm == DraggingMode.Zooming) {
          dm.adjustZoom(1 - .001 * (double) (y - my));
        } else if (drm == DraggingMode.Rotating) {
          double theta = .003 * (double) (x - mx); // Vízszintes forgatás
          double phi = .003 * (double) (y - my); // Függőleges forgatás
          dm.rotate(theta, phi);
        }

        mx = x;
        my = y;
      }
    }
  }

  private enum DraggingMode {

    Moving,
    Zooming,
    Rotating,
    Combined,
  }
}
