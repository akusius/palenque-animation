package hu.akusius.palenque.animation.op;

import java.awt.event.ActionEvent;
import javax.swing.Timer;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class PlayManagerTest {

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

  public PlayManagerTest() {
  }

  @Test
  public void test1() {
    PlayManager pm = new PlayManager();
    assertTrue(pm.isEnabled());

    PropSliderFrame fs = pm.getFrameSlider();
    PropAction toStart = pm.getToStartAction();
    PropAction toEnd = pm.getToEndAction();

    assertThat(fs.getValue(), equalTo(0));
    assertTrue(fs.isFirstFrame());

    assertTrue(fs.isEnabled());
    assertFalse(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());

    fs.setValue(10);
    assertTrue(fs.isInternalFrame());
    assertTrue(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());

    fs.setValue(fs.getMax());
    assertTrue(fs.isLastFrame());
    assertTrue(toStart.isEnabled());
    assertFalse(toEnd.isEnabled());

    pm.setEnabled(false);
    assertFalse(pm.isEnabled());
    assertFalse(fs.isEnabled());
    assertFalse(toStart.isEnabled());
    assertFalse(toEnd.isEnabled());
  }

  @Test
  public void test2() {
    PlayManager pm = new PlayManager();

    PropSlider speed = pm.getSpeedSlider();
    PropToggle qanim = pm.getQuickAnimationToggle();

    assertTrue(speed.isEnabled());
    assertTrue(qanim.isEnabled());
    assertFalse(qanim.isSelected());

    int svalue = speed.getValue();
    assertThat(speed.getValue(), not(equalTo(speed.getMin())));
    assertThat(speed.getValue(), not(equalTo(speed.getMax())));

    qanim.setSelected(true);
    assertTrue(qanim.isSelected());
    assertThat(speed.getValue(), equalTo(speed.getMax()));
    assertFalse(speed.isEnabled());

    qanim.setSelected(false);
    assertFalse(qanim.isSelected());
    assertThat(speed.getValue(), equalTo(svalue));
    assertTrue(speed.isEnabled());

    speed.setValue(speed.getMax());
    assertTrue(speed.isEnabled());
    assertFalse(qanim.isSelected());

    pm.setEnabled(false);
    assertFalse(pm.isEnabled());
    assertFalse(speed.isEnabled());
    assertFalse(qanim.isEnabled());

    pm.setEnabled(true);
    assertTrue(pm.isEnabled());
    assertTrue(speed.isEnabled());
    assertTrue(qanim.isEnabled());
  }

  @Test
  public void test3() {
    TestTimer timer = new TestTimer();
    PlayManager pm = new PlayManager(timer);

    PropSliderFrame fs = pm.getFrameSlider();
    PropToggle playing = pm.getPlayingToggle();
    PropSlider speed = pm.getSpeedSlider();
    PropToggle qanim = pm.getQuickAnimationToggle();
    PropAction toStart = pm.getToStartAction();
    PropAction toEnd = pm.getToEndAction();

    assertTrue(playing.isEnabled());
    assertFalse(playing.isSelected());

    assertFalse(timer.isRunning());
    playing.setSelected(true);
    assertTrue(timer.isRunning());

    assertThat(fs.getValue(), equalTo(0));
    assertTrue(fs.isFirstFrame());
    assertFalse(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());

    int frame = 0;

    timer.tick();
    frame += Math.pow(2, speed.getValue());
    assertThat(fs.getValue(), equalTo(frame));
    assertFalse(fs.isFirstFrame());
    assertTrue(fs.isInternalFrame());
    assertTrue(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());

    assertTrue(timer.isRunning());
    timer.tick();
    frame += Math.pow(2, speed.getValue());
    assertThat(fs.getValue(), equalTo(frame));

    playing.setSelected(false);
    assertFalse(timer.isRunning());
    assertThat(fs.getValue(), equalTo(frame));

    playing.setSelected(true);
    assertTrue(timer.isRunning());
    assertThat(fs.getValue(), equalTo(frame));

    toStart.performAction();
    frame = 0;
    assertThat(fs.getValue(), equalTo(frame));

    timer.tick(5);
    frame += 5 * Math.pow(2, speed.getValue());
    assertThat(fs.getValue(), equalTo(frame));

    speed.setValue(speed.getValue() + 1);
    timer.tick(2);
    frame += 2 * Math.pow(2, speed.getValue());
    assertThat(fs.getValue(), equalTo(frame));

    qanim.setSelected(true);
    timer.tick();
    frame += Math.pow(2, speed.getValue());
    assertThat(fs.getValue(), equalTo(frame));

    frame = fs.getMax() - 1;
    fs.setValue(frame);
    assertThat(fs.getValue(), equalTo(frame));
    assertTrue(timer.isRunning());
    assertTrue(fs.isInternalFrame());
    assertTrue(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());

    timer.tick();
    frame = fs.getMax();
    assertThat(fs.getValue(), equalTo(frame));
    assertFalse(playing.isSelected());
    assertFalse(timer.isRunning());
    assertTrue(fs.isLastFrame());
    assertTrue(toStart.isEnabled());
    assertFalse(toEnd.isEnabled());

    // Újraindításkor az elejére ugrik
    playing.setSelected(true);
    assertTrue(playing.isSelected());
    frame = 0;
    assertThat(fs.getValue(), equalTo(frame));
    assertTrue(timer.isRunning());
    assertTrue(fs.isFirstFrame());
    assertFalse(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());

    timer.tick();
    frame += Math.pow(2, speed.getValue());
    assertThat(fs.getValue(), equalTo(frame));

    assertTrue(fs.isInternalFrame());
    assertTrue(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());
    toEnd.performAction();

    frame = fs.getMax();
    assertThat(fs.getValue(), equalTo(frame));
    assertFalse(playing.isSelected());
    assertFalse(timer.isRunning());
    assertTrue(fs.isLastFrame());
    assertTrue(toStart.isEnabled());
    assertFalse(toEnd.isEnabled());

    pm.setEnabled(false);
    assertFalse(pm.isEnabled());
    assertFalse(playing.isEnabled());

    pm.setEnabled(true);
    assertTrue(pm.isEnabled());
    assertTrue(playing.isEnabled());
  }

  @Test
  public void test4() {
    TestTimer timer = new TestTimer();
    PlayManager pm = new PlayManager(timer);

    PropSliderFrame fs = pm.getFrameSlider();
    PropToggle playing = pm.getPlayingToggle();
    PropSlider speed = pm.getSpeedSlider();
    PropToggle qanim = pm.getQuickAnimationToggle();
    PropAction toStart = pm.getToStartAction();
    PropAction toEnd = pm.getToEndAction();

    playing.setSelected(true);
    timer.tick(5);

    assertTrue(playing.isSelected());
    assertTrue(timer.isRunning());
    assertTrue(fs.isInternalFrame());
    assertTrue(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());

    qanim.setSelected(true);
    assertTrue(qanim.isSelected());
    assertFalse(speed.isEnabled());
    assertThat(speed.getValue(), equalTo(speed.getMax()));

    pm.reset();
    assertFalse(playing.isSelected());
    assertFalse(timer.isRunning());
    assertThat(fs.getValue(), equalTo(0));
    assertTrue(fs.isFirstFrame());
    assertFalse(toStart.isEnabled());
    assertTrue(toEnd.isEnabled());
    assertTrue(qanim.isSelected());
    assertFalse(speed.isEnabled());
    assertThat(speed.getValue(), equalTo(speed.getMax()));
  }

  @Test
  public void test5() {
    TestTimer timer = new TestTimer();
    PlayManager pm = new PlayManager(timer);

    PropAction sd = pm.getSpeedDownAction();
    PropAction su = pm.getSpeedUpAction();

    PropSlider speedSlider = pm.getSpeedSlider();
    int speed = speedSlider.getValue();

    assertTrue(speed > speedSlider.getMin());
    assertTrue(speed < speedSlider.getMax());
    assertTrue(sd.isEnabled());
    assertTrue(su.isEnabled());

    pm.setEnabled(false);
    assertFalse(sd.isEnabled());
    assertFalse(su.isEnabled());

    pm.setEnabled(true);
    assertTrue(sd.isEnabled());
    assertTrue(su.isEnabled());

    while (speedSlider.getValue() > speedSlider.getMin()) {
      sd.performAction();
      assertTrue(speedSlider.getValue() < speed);
      speed = speedSlider.getValue();
      assertTrue(su.isEnabled());
    }
    assertFalse(sd.isEnabled());

    while (speedSlider.getValue() < speedSlider.getMax()) {
      su.performAction();
      assertTrue(speedSlider.getValue() > speed);
      speed = speedSlider.getValue();
      assertTrue(sd.isEnabled());
    }
    assertFalse(su.isEnabled());
  }

  @Test
  public void test6() {
    TestTimer timer = new TestTimer();
    PlayManager pm = new PlayManager(timer);

    PropAction sb = pm.getSkipBackAction();
    PropAction sf = pm.getSkipForwardAction();

    PropSliderFrame frameSlider = pm.getFrameSlider();
    frameSlider.setValue(frameSlider.getMax() / 2);
    int frame = frameSlider.getValue();

    assertTrue(frame > frameSlider.getMin());
    assertTrue(frame < frameSlider.getMax());
    assertTrue(sb.isEnabled());
    assertTrue(sf.isEnabled());

    pm.setEnabled(false);
    assertFalse(sb.isEnabled());
    assertFalse(sf.isEnabled());

    pm.setEnabled(true);
    assertTrue(sb.isEnabled());
    assertTrue(sf.isEnabled());

    while (frameSlider.getValue() > frameSlider.getMin()) {
      sb.performAction();
      assertTrue(frameSlider.getValue() < frame);
      frame = frameSlider.getValue();
      assertTrue(sf.isEnabled());
    }
    assertFalse(sb.isEnabled());
    assertTrue(frameSlider.isFirstFrame());

    while (frameSlider.getValue() < frameSlider.getMax()) {
      sf.performAction();
      assertTrue(frameSlider.getValue() > frame);
      frame = frameSlider.getValue();
      assertTrue(sb.isEnabled());
    }
    assertFalse(sf.isEnabled());
    assertTrue(frameSlider.isLastFrame());
  }
}
