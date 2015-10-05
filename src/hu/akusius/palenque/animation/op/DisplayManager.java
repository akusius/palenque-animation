package hu.akusius.palenque.animation.op;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.event.ChangeListener;
import org.other.ChangeSupport;
import org.other.Matrix;

/**
 * Osztály a megjelenítés kezeléséhez.
 * Automatikus üzemmódban az aktuális képkockához igazítja a megjelenítést (kameramozgatás).
 * @author Bujdosó Ákos
 */
public class DisplayManager {

  private static final CamPoint[] camPoints = new CamPoint[]{
    new CamPoint(0, 0.0, 0.0, 20.0, 0.0, 0.0, .5),
    new CamPoint(0, 75.0),
    new CamPoint(9, 0.0),
    new CamPoint(10, 0.0, 0.0, 0.0, 0.0, -1.0, 1.25),
    new CamPoint(11, 0.0, 0.0, 0.0, 0.0, -1.0, 1.25),
    new CamPoint(12, 30.0, 0.0, 0.0, 0.0, -1.0, 1.75),
    new CamPoint(15, 50.0, 0.0, 0.0, 0.0, -1.0, 1.75),
    new CamPoint(17, 15.0, 0.0, 0.0, 0.0, 0.0, 1.75),
    new CamPoint(18, 40.0, 6.0, -6.0, 0.0, 0.0, 3.0)
  };

  static {
    Arrays.sort(camPoints, new Comparator<CamPoint>() {
      @Override
      public int compare(CamPoint cp1, CamPoint cp2) {
        if (cp1.frame == cp2.frame) {
          throw new IllegalArgumentException();
        }
        return Integer.compare(cp1.frame, cp2.frame);
      }
    });
    if (camPoints.length == 0 || camPoints[0].frame != 0) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < camPoints.length - 1; i++) {
      CamPoint cp = camPoints[i];
      cp.next = camPoints[i + 1];
      cp.lastFrame = cp.next.frame - 1;
    }
    camPoints[camPoints.length - 1].lastFrame = FrameInfo.getMaxFrameNum();
  }

  private final PlayManager pm;

  private final PropToggle autoToggle;

  private final Matrix rotationMatrix = new Matrix();

  private double translateX = 0d;

  private double translateY = 0d;

  private double zoom = 1d;

  public DisplayManager(PlayManager pm) {
    Matrix.identity(this.rotationMatrix);
    this.pm = pm;
    pm.getFrameSlider().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropSliderFrame.PROP_VALUE.equals(evt.getPropertyName()) && autoToggle.isSelected()) {
          refreshAuto();
        }
      }
    });
    autoToggle = new PropToggle(true);
    autoToggle.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName()) && (boolean) evt.getNewValue()) {
          refreshAuto();
        }
      }
    });
    refreshAuto();
  }

  /**
   * @return Az automatikus megjelenítést vezérlő tulajdonság.
   */
  public PropToggle getAutoToggle() {
    return autoToggle;
  }

  /**
   * @return Az aktuális megjelenítéshez tartozó kameramátrix.
   */
  public Matrix getCameraMatrix() {
    Matrix cam = new Matrix();
    cam.copy(this.rotationMatrix);
    Matrix trans = new Matrix();
    Matrix.identity(trans);
    trans.translate(this.translateX, this.translateY, 0.0);
    cam.postMultiply(trans);
    return cam;
  }

  /**
   * @return A nagyítás aktuális mértéke.
   */
  public double getZoom() {
    return zoom;
  }

  /**
   * A nézet elmozgatása.
   * @param factorX Az elmozgatás mértéke X irányban.
   * @param factorY Az elmozgatás mértéke Y irányban.
   */
  public void translate(double factorX, double factorY) {
    if (factorX == 0d && factorY == 0d) {
      return;
    }
    translateX += factorX / zoom;
    translateY += factorY / zoom;
    autoToggle.setSelected(false);
    changeSupport.fireChange();
  }

  /**
   * Az eltolások alaphelyzetbe állítása.
   */
  public void resetTranslate() {
    if (translateX == 0 && translateY == 0) {
      return;
    }
    translateX = 0;
    translateY = 0;
    autoToggle.setSelected(false);
    changeSupport.fireChange();
  }

  /**
   * A nézet elforgatása.
   * @param theta A vízszintes forgatás mértéke.
   * @param phi A függőleges forgatás mértéke.
   */
  public void rotate(double theta, double phi) {
    if (theta == 0d && phi == 0d) {
      return;
    }
    Matrix tmp = new Matrix();
    Matrix.identity(tmp);
    tmp.rotateY(theta);
    rotationMatrix.postMultiply(tmp);
    Matrix.identity(tmp);
    tmp.rotateX(phi);
    rotationMatrix.postMultiply(tmp);
    autoToggle.setSelected(false);
    changeSupport.fireChange();
  }

  /**
   * Az elforgatások alaphelyzetbe állítása.
   */
  public void resetRotate() {
    if (rotationMatrix.isIdentity()) {
      return;
    }
    rotationMatrix.identity();
    autoToggle.setSelected(false);
    changeSupport.fireChange();
  }

  /**
   * A nagyítás mértékének változtatása.
   * @param factor A változtatás mértéke.
   */
  public void adjustZoom(double factor) {
    if (factor == 1d) {
      return;
    }
    double nz = zoom * factor;
    if (nz > 0 && nz < 20) {
      zoom = nz;
      autoToggle.setSelected(false);
      changeSupport.fireChange();
    }
  }

  /**
   * A nagyítás alaphelyzetbe állítása.
   */
  public void resetZoom() {
    if (zoom == 1.0) {
      return;
    }
    zoom = 1.0;
    autoToggle.setSelected(false);
    changeSupport.fireChange();
  }

  private final ChangeSupport changeSupport = new ChangeSupport(this);

  public void addChangeListener(ChangeListener listener) {
    changeSupport.addChangeListener(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    changeSupport.removeChangeListener(listener);
  }

  private void refreshAuto() {
    FrameInfo fi = pm.getFrameSlider().getCurrentFrameInfo();
    int frame = fi.getFrameNum();

    CamPoint cp = findCamPoint(frame).interpolate(frame);

    translateX = cp.translateX;
    translateY = cp.translateY;

    rotationMatrix.identity();
    Matrix tmp = new Matrix();
    Matrix.identity(tmp);
    tmp.rotateY(cp.theta);
    rotationMatrix.postMultiply(tmp);
    Matrix.identity(tmp);
    tmp.rotateX(cp.phi);
    rotationMatrix.postMultiply(tmp);

    zoom = cp.zoom;

    changeSupport.fireChange();
  }

  private static CamPoint findCamPoint(int frame) {
    int low = 0;
    int high = camPoints.length - 1;

    while (low <= high) {
      int mid = (low + high) >>> 1;
      CamPoint cp = camPoints[mid];
      if (frame < cp.frame) {
        high = mid - 1;
      } else if (frame > cp.lastFrame) {
        low = mid + 1;
      } else {
        return cp;
      }
    }
    return null;
  }

  private static class CamPoint {

    final int frame;

    final double translateX;

    final double translateY;

    final double theta;

    final double phi;

    final double zoom;

    int lastFrame;

    CamPoint next;

    CamPoint(int frame) {
      this(frame, 0.0, 0.0, 0.0, 0.0, 1.0);
    }

    CamPoint(int step, double percent) {
      this(step, percent, 0.0, 0.0, 0.0, 0.0, 1.0);
    }

    CamPoint(int frame, double translateX, double translateY, double theta, double phi, double zoom) {
      this.frame = frame;
      this.translateX = translateX;
      this.translateY = translateY;
      this.theta = theta;
      this.phi = phi;
      this.zoom = zoom;
    }

    CamPoint(int step, double percent, double translateX, double translateY, double theta, double phi, double zoom) {
      this.frame = FrameInfo.getFrameInfo(step, percent).getFrameNum();
      this.translateX = translateX;
      this.translateY = translateY;
      this.theta = theta;
      this.phi = phi;
      this.zoom = zoom;
    }

    CamPoint interpolate(int pos) {
      assert pos >= frame && pos <= lastFrame;

      if (pos == frame || next == null) {
        return this;
      }

      return new CamPoint(frame,
              interpolate(pos, frame, lastFrame, translateX, next.translateX),
              interpolate(pos, frame, lastFrame, translateY, next.translateY),
              interpolate(pos, frame, lastFrame, theta, next.theta),
              interpolate(pos, frame, lastFrame, phi, next.phi),
              interpolate(pos, frame, lastFrame, zoom, next.zoom)
      );
    }

    private static double interpolate(int pos, int start, int end, double startValue, double endValue) {
      assert start < end && pos >= start && pos <= end;

      if (startValue == endValue) {
        return startValue;
      }

      double c = endValue - startValue;
      double d = end - start;
      double t = pos - start;

      // Quadratic easing in/out
      t /= d / 2;
      if (t < 1) {
        return c / 2 * t * t + startValue;
      }
      t--;
      return -c / 2 * (t * (t - 2) - 1) + startValue;
    }
  }
}
