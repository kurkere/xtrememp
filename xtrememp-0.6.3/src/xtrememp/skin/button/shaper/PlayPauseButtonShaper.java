/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2008  Besmir Beqiri
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
package xtrememp.skin.button.shaper;

import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import javax.swing.AbstractButton;

/**
 *
 * @author Besmir Beqiri
 */
public class PlayPauseButtonShaper extends ButtonShaper {

    public String getDisplayName() {
        return "PlayPause";
    }

    public GeneralPath getButtonOutline(AbstractButton button, Insets insets, int w, int h) {
        int width = w - 1;
        int height = h - 1;

        Shape shape = new Ellipse2D.Double(0, 0, width, height);
        GeneralPath generalPath = new GeneralPath(shape);

        return generalPath;
    }

    public GeneralPath getButtonOutline(AbstractButton button, Insets insets) {
        return getButtonOutline(button, insets, button.getWidth(), button.getHeight());
    }

    public GeneralPath getButtonOutline(AbstractButton button) {
        return getButtonOutline(button, button.getInsets());
    }
}
