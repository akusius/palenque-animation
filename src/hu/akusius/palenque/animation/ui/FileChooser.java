package hu.akusius.palenque.animation.ui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Saját, testreszabott {@link JFileChooser}.
 * @author Bujdosó Ákos
 */
public final class FileChooser extends JFileChooser {

  private static File currentDirectory = new File(".");

  private final String extension;

  /**
   * Új példány létrehozása a megadott kiterjesztéssel.
   * @param ext A kezelendő kiterjesztés, vagy {@code null}, ha nincsen ilyen.
   */
  public FileChooser(String ext) {
    super();
    if (ext != null) {
      if (ext.contains(".")) {
        throw new IllegalArgumentException();
      }
      this.extension = ext.toLowerCase();
      this.setAcceptAllFileFilterUsed(false);
      this.addChoosableFileFilter(new FileNameExtensionFilter(
              String.format("%s (*.%s)", this.extension.toUpperCase(), this.extension), this.extension));
    } else {
      this.extension = null;
    }

    if (currentDirectory != null) {
      setCurrentDirectory(currentDirectory);
    }
  }

  @Override
  public void approveSelection() {
    File f = getSelectedFile();
    if (extension != null && !f.getName().toLowerCase().endsWith("." + extension)) {
      f = new File(f.getAbsolutePath() + "." + extension);
      setSelectedFile(f);
    }

    boolean exists = getSelectedFile().exists();
    if (exists && getDialogType() == SAVE_DIALOG) {
      int result = JOptionPane.showConfirmDialog(this,
              "The file already exists, overwrite?", "Existing file", JOptionPane.YES_NO_OPTION);
      switch (result) {
        case JOptionPane.YES_OPTION:
          break;
        case JOptionPane.CANCEL_OPTION:
          cancelSelection();
          return;
        case JOptionPane.NO_OPTION:
        case JOptionPane.CLOSED_OPTION:
        default:
          return;
      }
    } else if (!exists && getDialogType() == OPEN_DIALOG) {
      JOptionPane.showMessageDialog(this,
              "The file does not exist!", "Non-existent file", JOptionPane.WARNING_MESSAGE);
      return;
    }

    currentDirectory = getCurrentDirectory();
    super.approveSelection();
  }

}
