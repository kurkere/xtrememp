/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2010 Besmir Beqiri
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package xtrememp.ui.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 */
public class SearchTextField extends JPanel {

    private JTextField textField;

    public SearchTextField(int columns) {
        super(new BorderLayout());
        textField = new ExtendedTextField(columns);
        textField.setBackground(new Color(0, 0, 0, 64));
        textField.setFont(getFont().deriveFont(Font.BOLD | Font.ITALIC));
        textField.setFocusAccelerator('S');
        this.add(textField, BorderLayout.CENTER);
    }

    public JTextField getTextField() {
        return textField;
    }

    private class ExtendedTextField extends JTextField {

        private final char faChar = 'S';
        private final String searchText = tr("MainFrame.PlaylistManager.Filter") + "  (Alt+" + faChar + ")";
        private Map desktopHints = null;

        public ExtendedTextField(int columns) {
            super(columns);
            setBackground(new Color(0, 0, 0, 64));
            setFont(getFont().deriveFont(Font.BOLD));
            setFocusAccelerator(faChar);
            addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        if (EventQueue.isDispatchThread()) {
                            setText("");
                        } else {
                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    setText("");
                                }
                            });

                        }
                    }
                }
            });
        }

        @Override
        public Insets getInsets() {
            return new Insets(3, 5, 3, 0);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (!isFocusOwner() && getText().isEmpty()) {
                Insets insets = getInsets();
                Graphics2D g2d = (Graphics2D) g.create();

                if (desktopHints == null) {
                    Toolkit tk = Toolkit.getDefaultToolkit();
                    desktopHints = (Map) (tk.getDesktopProperty("awt.font.desktophints"));
                }

                if (desktopHints != null) {
                    g2d.addRenderingHints(desktopHints);
                }

                if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
                    SubstanceColorScheme enabledColorScheme = SubstanceLookAndFeel.getCurrentSkin().getEnabledColorScheme(DecorationAreaType.TOOLBAR);
                    SubstanceColorScheme activeColorScheme = SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.TOOLBAR);
                    SubstanceColorScheme colorScheme = enabledColorScheme.isDark() ? enabledColorScheme : activeColorScheme;
                    Color fgColor = colorScheme.getForegroundColor();
                    g2d.setColor(fgColor);
                    setForeground(fgColor);
                }
                g2d.drawString(searchText, insets.left, getHeight() - (insets.top + insets.bottom));
                g2d.dispose();
            }
        }
    }
}
