package CustomJComponents;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanelBorder extends AbstractBorder {
    private final Color borderColor;
    private final int cornerRadius;

    public RoundedPanelBorder(Color borderColor, int cornerRadius) {
        this.borderColor = borderColor;
        this.cornerRadius = cornerRadius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(borderColor);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arcWidth = cornerRadius * 2;
        int arcHeight = cornerRadius * 2;

        Shape border = new RoundRectangle2D.Double(x, y, width - 1, height - 1, arcWidth, arcHeight);
        g2.draw(border);

        g2.dispose();
    }
}
