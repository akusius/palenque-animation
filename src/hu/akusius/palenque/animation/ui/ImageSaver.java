package hu.akusius.palenque.animation.ui;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Osztály egy kép elmentéséhez.
 * @author Bujdosó Ákos
 */
public final class ImageSaver {

  private static ImageSaver imageSaver;

  private final DialogParent dialogParent;

  private final FileChooser fcSave;

  private final JSpinner sizeSpinner;

  private final JButton btnClipboard;

  private ImageSource imageSource;

  private int suggestedSize;

  /**
   * A képforrásból származó kép legenerálása és elmentése PNG formátumban.
   * @param imageSource A képet szolgáltató képforrás.
   * @param dialogParent A megjelenítendő dialógusok szülője.
   */
  public static synchronized void saveImage(ImageSource imageSource, DialogParent dialogParent) {
    if (imageSaver == null) {
      imageSaver = new ImageSaver(imageSource, dialogParent);
    }
    imageSaver.imageSource = imageSource;
    imageSaver.suggestedSize = imageSource.getSuggestedSize();
    imageSaver.showDialog();
  }

  private ImageSaver(ImageSource is, DialogParent dialogParent) {
    this.dialogParent = dialogParent;

    fcSave = new FileChooser("png");

    JPanel pnlSpinner = new JPanel();
    sizeSpinner = new JSpinner(new SpinnerNumberModel(is.getSuggestedSize(), is.getMinSize(), is.getMaxSize(), 1));
    pnlSpinner.add(sizeSpinner);
    pnlSpinner.add(new JLabel("px"));

    btnClipboard = new JButton("Clipboard", IconFactory.readIcon("copy.png"));
    btnClipboard.setMnemonic(KeyEvent.VK_C);
    btnClipboard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveCopyImage(null);
        fcSave.cancelSelection();
      }
    });

    JPanel pnlAccessory = new JPanel(new GridBagLayout());
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    pnlAccessory.add(pnlSpinner, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    pnlAccessory.add(btnClipboard, gbc);

    fcSave.setAccessory(pnlAccessory);
  }

  private Integer userSize;

  private void showDialog() {
    sizeSpinner.setValue(userSize != null ? userSize : suggestedSize);

    int result = fcSave.showSaveDialog(dialogParent.getComponent());

    if (result == JFileChooser.APPROVE_OPTION) {
      saveCopyImage(fcSave.getSelectedFile());
    }
  }

  private void saveCopyImage(File dest) {
    try {
      int size = (int) sizeSpinner.getValue();
      if (dest != null) {
        saveImage(dest, size);
      } else {
        copyImage(size);
      }
      if (size != suggestedSize) {
        userSize = size;
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(dialogParent.getComponent(),
              ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void saveImage(File dest, int size) throws Exception {
    BufferedImage image = imageSource.generateImage(size);
    ImageIO.write(image, "png", dest);
  }

  private void copyImage(int size) throws Exception {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    if (clipboard != null) {
      TransferableImage transferable = new TransferableImage(imageSource.generateImage(size));
      clipboard.setContents(transferable, null);
    }
  }

  private static class TransferableImage implements Transferable {

    private final Image image;

    TransferableImage(Image image) {
      this.image = image;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
      if (isDataFlavorSupported(flavor)) {
        return image;
      } else {
        throw new UnsupportedFlavorException(flavor);
      }
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return flavor == DataFlavor.imageFlavor;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{DataFlavor.imageFlavor};
    }
  }
}
