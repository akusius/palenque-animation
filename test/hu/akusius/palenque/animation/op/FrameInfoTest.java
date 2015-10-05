package hu.akusius.palenque.animation.op;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class FrameInfoTest {

  public FrameInfoTest() {
  }

  @Test
  public void test1() {
    FrameInfo fi = FrameInfo.getFrameInfo(0);
    assertThat(fi.getFrameNum(), equalTo(0));
    assertThat(fi.getStepNum(), equalTo(0));
    assertThat(fi.getStepFirstFrame(), equalTo(0));
    assertThat(fi.getPercent(), equalTo(0.0));
    assertThat(fi.isLastFrame(), is(false));

    FrameInfo fi2 = FrameInfo.getFrameInfo(-1);
    assertThat(fi2.getFrameNum(), equalTo(0));
    assertThat(fi2, equalTo(fi));
    assertThat(fi2.equals(fi), is(true));

    FrameInfo fic = fi.createClone();
    assertThat(fic, not(sameInstance(fi)));
    assertThat(fic, equalTo(fi));
    assertThat(fic, equalTo(fi2));
  }

  @Test
  public void test2() {
    int maxFrameNum = FrameInfo.getMaxFrameNum();
    FrameInfo fi = FrameInfo.getFrameInfo(maxFrameNum);
    assertThat(fi.getFrameNum(), equalTo(maxFrameNum));
    assertThat(fi.getStepNum(), equalTo(FrameInfo.getMaxStepNum()));
    assertThat(fi.getStepEndFrame(), equalTo(maxFrameNum - 1));
    assertThat(fi.getPercent(), equalTo(100.0));
    assertThat(fi.isLastFrame(), is(true));

    FrameInfo fi2 = FrameInfo.getFrameInfo(maxFrameNum + 1);
    assertThat(fi2.getFrameNum(), equalTo(maxFrameNum));
    assertThat(fi2, equalTo(fi));
    assertThat(fi2.equals(fi), is(true));

    FrameInfo fic = fi.createClone();
    assertThat(fic, not(sameInstance(fi)));
    assertThat(fic, equalTo(fi));
    assertThat(fic, equalTo(fi2));
  }

  @Test
  public void test3() {
    int lastStepFirstFrame = -1;
    int maxFrameNum = FrameInfo.getMaxFrameNum();
    for (int frame = 0; frame < maxFrameNum; frame++) {
      FrameInfo fi = FrameInfo.getFrameInfo(frame);
      assertThat(fi.getFrameNum(), equalTo(frame));
      assertThat(fi.getStepNum() >= 0, is(true));
      assertThat(fi.getStepNum() <= FrameInfo.getMaxStepNum(), is(true));
      assertThat(fi.isLastFrame(), is(false));
      if (fi.getStepFirstFrame() == frame) {
        lastStepFirstFrame = frame;
        if (fi.getStepNum() > 0) {
          FrameInfo fiLast = FrameInfo.getFrameInfo(frame - 1);
          assertThat(fiLast.getFrameNum(), equalTo(frame - 1));
          assertThat(fiLast.getStepNum(), equalTo(fi.getStepNum() - 1));
          assertThat(fiLast.getStepEndFrame(), equalTo(fiLast.getFrameNum()));
          assertThat(fiLast.getPercent() < 100.0, is(true));
          assertThat(fiLast, not(equalTo(fi)));
          assertThat(fiLast.hashCode(), not(equalTo(fi.hashCode())));
        }
      }
      assertThat(fi.getStepFirstFrame(), equalTo(lastStepFirstFrame));
      assertThat(fi.getStepLength(), equalTo(fi.getStepEndFrame() - fi.getStepFirstFrame() + 1));

      FrameInfo fic = fi.createClone();
      assertThat(fic, not(sameInstance(fi)));
      assertThat(fic, equalTo(fi));

      FrameInfo fi2 = FrameInfo.getFrameInfo(fi.getStepNum(), fi.getPercent());
      assertThat(String.format("Eltérő képkockák: %d %d", fi.getFrameNum(), fi2.getFrameNum()), fi2, equalTo(fi));
      assertThat(fi2.getFrameNum(), equalTo(fi.getFrameNum()));
    }
  }

}
