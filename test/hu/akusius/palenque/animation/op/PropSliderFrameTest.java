package hu.akusius.palenque.animation.op;

import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class PropSliderFrameTest {

  public PropSliderFrameTest() {
  }

  @Test
  public void test1() {
    PropSliderFrame prop = new PropSliderFrame();
    assertThat(prop.getMin(), equalTo(0));
    assertThat(prop.getMax(), equalTo(FrameInfo.getMaxFrameNum()));
    assertThat(prop.getValue(), equalTo(0));

    assertThat(prop.getMinFrameInfo().getFrameNum(), equalTo(0));
    assertThat(prop.getMaxFrameInfo().getFrameNum(), equalTo(FrameInfo.getMaxFrameNum()));
    assertThat(prop.getCurrentFrameInfo().getFrameNum(), equalTo(0));
    assertThat(prop.getMinFrameInfo(), equalTo(prop.getCurrentFrameInfo()));
    assertTrue(prop.isFirstFrame());
    assertFalse(prop.isInternalFrame());
    assertFalse(prop.isLastFrame());
  }

  @Test
  public void test2() {
    PropSliderFrame prop = new PropSliderFrame();
    assertThat(prop.getValue(), equalTo(0));

    EventTester et = new EventTester();
    prop.addPropertyChangeListener(et.propertyChangeListener);

    prop.setValue(1);
    assertTrue(et.hadPropertyChange(PropSlider.PROP_VALUE, 0, 1));
    assertThat(prop.getCurrentFrameInfo().getFrameNum(), equalTo(1));
    assertFalse(prop.isFirstFrame());
    assertTrue(prop.isInternalFrame());
    assertFalse(prop.isLastFrame());
    et.clear();

    prop.setValue(FrameInfo.getMaxFrameNum());
    assertTrue(et.hadPropertyChange(PropSlider.PROP_VALUE, 1, FrameInfo.getMaxFrameNum()));
    assertThat(prop.getCurrentFrameInfo().getFrameNum(), equalTo(FrameInfo.getMaxFrameNum()));
    assertFalse(prop.isFirstFrame());
    assertFalse(prop.isInternalFrame());
    assertTrue(prop.isLastFrame());
  }

  @Test(expected = IllegalArgumentException.class)
  public void test3() {
    PropSliderFrame prop = new PropSliderFrame();
    assertThat(prop.getValue(), equalTo(0));

    prop.setValue(FrameInfo.getMaxFrameNum() + 1);
  }

  @Test
  public void test4() {
    PropSliderFrame prop = new PropSliderFrame();

    int mfn = FrameInfo.getMaxFrameNum();
    for (int fn = 0; fn <= mfn; fn++) {
      prop.setValue(fn);
      assertThat(prop.getValue(), equalTo(fn));
      assertThat(prop.getCurrentFrameInfo().getFrameNum(), equalTo(fn));
      assertThat(prop.isFirstFrame(), equalTo(fn == 0));
      assertThat(prop.isInternalFrame(), equalTo(fn > 0 && fn < mfn));
      assertThat(prop.isLastFrame(), equalTo(fn == mfn));
    }
  }

}
