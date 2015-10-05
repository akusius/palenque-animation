package hu.akusius.palenque.animation.op;

/**
 * Egy képkocka adatai.
 * A program lépésekre van osztva, egy lépésen belül a folyamat 0%-tól fut _nem_ teljesen 100%-ig.
 * A teljes program végén található egy extra képkocka, amihez az utolsó lépés 100%-os értéke tartozik.
 * @author Bujdosó Ákos
 */
public class FrameInfo {

  /**
   * Az adott lépések hosszai képkockákban
   */
  private static final int[] stepFramesLength = new int[]{
    2000, // 0: Fedélrajz és kiindulás
    500, // 1: Első forgatás
    500, // 2: Második forgatás
    500, // 3: Harmadik forgatás
    250, // 4: Várakozás
    500, // 5: Tükrözés
    250, // 6: Várakozás
    600, // 7: Sarkok befelé tolása
    1000, // 8: Középső elemek befelé tolása
    750, // 9: Sarkok eltüntetése
    500, // 10: Első behajtás
    500, // 11: Várakozás
    500, // 12: Második behajtás 1.
    500, // 13: Második behajtás 2.
    500, // 14: Második behajtás 3.
    500, // 15: Második behajtás 4.
    500, // 16: Várakozás
    1000, // 17: Ráközelítés egyikre és a többi eltüntetése
    500, // 18: Négyzetek helyett vonalak
    200, // 19: Várakozás
    750, // 20: Összezárás
    500, // 21: Program vége, várakozás
  };

  private final static int maxFrameNum;

  static {
    int mfn = 0;
    int maxStep = getMaxStepNum();
    for (int i = 0; i <= maxStep; i++) {
      mfn += stepFramesLength[i];
    }
    maxFrameNum = mfn + 1;
  }

  /**
   * @return A maximális kockaszám.
   */
  public static int getMaxFrameNum() {
    return maxFrameNum;
  }

  /**
   * @return A maximális lépésszám
   */
  public static int getMaxStepNum() {
    return stepFramesLength.length - 1;
  }

  private int frameNum;

  private int stepNum;

  private double percent;

  private int stepFirstFrame;

  private int stepEndFrame;

  private boolean lastFrame;

  private FrameInfo() {
  }

  /**
   * @return A lépés hossza.
   */
  public int getStepLength() {
    return this.getStepEndFrame() - this.getStepFirstFrame() + 1;
  }

  public static FrameInfo getFrameInfo(int step, double percent) {
    int maxStep = stepFramesLength.length - 1;

    if (step < 0 || step > maxStep || percent < 0.0 || percent > 100.0) {
      throw new IllegalArgumentException();
    }

    int currentStepEndFrame = 0;
    int prevStepEndFrame = -1;
    for (int st = 0; st <= maxStep; st++) {
      int stepLength = stepFramesLength[st];
      currentStepEndFrame += stepLength;
      if (st == step) {
        int stepFirstFrame = prevStepEndFrame + 1;
        int frame = (int) Math.round(percent * (currentStepEndFrame - stepFirstFrame + 1) / 100.0 + stepFirstFrame);
        assert frame >= stepFirstFrame && frame <= currentStepEndFrame;
        return getFrameInfo(frame);
      }
      prevStepEndFrame = currentStepEndFrame;
    }

    return null;
  }

  /**
   * A megadott képkockához az információk összeállítása.
   * @param frame A képkocka.
   * @return Az összeállított információk.
   */
  public static FrameInfo getFrameInfo(int frame) {
    frame = Math.min(getMaxFrameNum(), Math.max(0, frame));

    FrameInfo fi = new FrameInfo();
    fi.frameNum = frame;

    int maxStep = stepFramesLength.length - 1;
    int currentStepEndFrame = 0;
    int prevStepEndFrame = -1;
    for (int step = 0; step <= maxStep; step++) {
      int stepLength = stepFramesLength[step];
      currentStepEndFrame += stepLength;
      if (frame <= currentStepEndFrame) {
        // Megtaláltuk a lépést
        fi.stepNum = step;
        fi.stepEndFrame = currentStepEndFrame;
        fi.stepFirstFrame = prevStepEndFrame + 1;
        fi.percent = ((double) (frame - fi.getStepFirstFrame())) / (double) fi.getStepLength() * 100.0;
        fi.lastFrame = false;
        return fi;
      }
      prevStepEndFrame = currentStepEndFrame;
    }

    // Túl vagyunk már a teljes programon, az utolsó lépést adjuk vissza 100%-kal
    fi.stepNum = maxStep;
    fi.stepEndFrame = currentStepEndFrame;
    int stepLength = stepFramesLength[fi.getStepNum()];
    fi.stepFirstFrame = fi.getStepEndFrame() - stepLength + 1;
    fi.percent = 100.0;
    fi.lastFrame = true;
    return fi;
  }

  /**
   * @return A kockaszám.
   */
  public int getFrameNum() {
    return frameNum;
  }

  /**
   * @return A lépésszám.
   */
  public int getStepNum() {
    return stepNum;
  }

  /**
   * @return A lépésen belüli százalék.
   */
  public double getPercent() {
    return percent;
  }

  /**
   * @return A kocka lépésének első képkockája.
   */
  public int getStepFirstFrame() {
    return stepFirstFrame;
  }

  /**
   * @return A kocka lépésének utolsó képkockája.
   */
  public int getStepEndFrame() {
    return stepEndFrame;
  }

  /**
   * @return {@code true}, ha ez az utolsó képkocka.
   */
  public boolean isLastFrame() {
    return lastFrame;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + this.frameNum;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final FrameInfo other = (FrameInfo) obj;
    return this.frameNum == other.frameNum;
  }

  /**
   * Az objektum klónozása.
   * @return A klónozott új objektum.
   */
  public FrameInfo createClone() {
    return getFrameInfo(this.frameNum);
  }
}
