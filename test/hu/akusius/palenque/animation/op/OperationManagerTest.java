package hu.akusius.palenque.animation.op;

import java.awt.event.ActionEvent;
import javax.swing.Timer;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class OperationManagerTest {

  private static final class TestTimer extends Timer {

    TestTimer() {
      super(50, null);
    }

    private boolean running = false;

    private int tickNum = 0;

    @Override
    public void stop() {
      running = false;
    }

    @Override
    public boolean isRunning() {
      return running;
    }

    @Override
    public void start() {
      running = true;
    }

    public void tick() {
      tick(1);
    }

    public void tick(int n) {
      assert isRunning();
      assert isRepeats();
      for (int i = 0; i < n; i++) {
        tickNum++;
        fireActionPerformed(new ActionEvent(this, 0, getActionCommand(), System.currentTimeMillis(), 0));
      }
    }

    public long getElapsedMsec() {
      return tickNum * getDelay();
    }
  }

  public OperationManagerTest() {
  }

  @Test
  public void test1() {
    TestTimer timer = new TestTimer();
    OperationManager om = new OperationManager(timer);

    PlayManager pm = om.getPlayManager();
    PropAction infoAction = om.getInfoAction();
    PropAction screenshotAction = om.getScreenshotAction();

    assertTrue(infoAction.isEnabled());
    assertTrue(screenshotAction.isEnabled());

    pm.getPlayingToggle().setSelected(true);
    assertFalse(infoAction.isEnabled());
    assertFalse(screenshotAction.isEnabled());
  }

}
