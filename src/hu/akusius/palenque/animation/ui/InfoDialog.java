package hu.akusius.palenque.animation.ui;

import hu.akusius.palenque.animation.util.IoUtils;
import hu.akusius.palenque.animation.util.UIUtils;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Dialógus az információk megjelenítéséhez.
 * @author Bujdosó Ákos
 */
public class InfoDialog extends JDialog {

  private JButton btnOK;

  private JEditorPane epText;

  private JScrollPane spText;

  public InfoDialog(Window owner) {
    super(owner, Dialog.ModalityType.APPLICATION_MODAL);
    initComponents();

    this.pack();
    this.setLocationRelativeTo(owner);

    btnOK.requestFocusInWindow();
  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new GridBagLayout());
    setPreferredSize(new Dimension(500, 500));

    spText = new JScrollPane();
    epText = new JEditorPane();
    epText.setEditable(false);
    //epText.setFocusable(false);
    spText.setViewportView(epText);

    epText.setContentType("text/html; charset=UTF-8");
    try (InputStream is = InfoDialog.class.getResourceAsStream("info.html")) {
      epText.setText(IoUtils.readStringFromInputStream(is));
      Object title = epText.getDocument().getProperty("title");
      if (title != null && title instanceof String) {
        setTitle((String) title);
      }
    } catch (IOException ex) {
      epText.setContentType("text/plain");
      epText.setText(ex.getMessage());
    }

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.insets = new Insets(1, 1, 1, 1);
    getContentPane().add(spText, gbc);

    btnOK = new JButton("OK");
    FontUtility.scaleFont(btnOK);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets(5, 5, 5, 5);
    getContentPane().add(btnOK, gbc);
    getRootPane().setDefaultButton(btnOK);

    hookEvents();

    UIUtils.installDialogEscapeCloseOperation(this);
  }

  private void hookEvents() {
    HyperlinkHandler hyperlinkHandler = new HyperlinkHandler();
    epText.addHyperlinkListener(hyperlinkHandler);
    epText.addMouseListener(hyperlinkHandler);

    btnOK.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
  }

  private class HyperlinkHandler extends MouseAdapter implements HyperlinkListener {

    private final JPopupMenu popup;

    private URL currentLink;

    private Desktop browser;

    HyperlinkHandler() {
      if (Desktop.isDesktopSupported()) {
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.BROWSE)) {
          browser = desktop;
        }
      }

      popup = new JPopupMenu();
      JMenuItem item;

      item = new JMenuItem("<html><b>Open</b></html>", IconFactory.readIcon("browse.png"));
      item.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          browse();
        }
      });
      popup.add(item);

      item = new JMenuItem("Copy", IconFactory.readIcon("copy.png"));
      item.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          copyURL();
        }
      });
      popup.add(item);
    }

    private void browse() {
      if (browser != null && currentLink != null) {
        try {
          browser.browse(new URI(currentLink.toString()));
        } catch (IOException | URISyntaxException ex) {
        }
      }
    }

    private void copyURL() {
      if (currentLink != null) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard != null) {
          StringSelection stringSelection = new StringSelection(currentLink.toString());
          clipboard.setContents(stringSelection, null);
        }
      }
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
      if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
        currentLink = e.getURL();
        epText.setToolTipText(currentLink.toString());
      } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
        currentLink = null;
        epText.setToolTipText(null);
      } else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        currentLink = e.getURL();
        browse();
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger() && currentLink != null) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }
}
