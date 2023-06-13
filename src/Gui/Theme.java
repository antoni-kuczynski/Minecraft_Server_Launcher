package Gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;

import com.formdev.flatlaf.ui.FlatRoundBorder;
import mdlaf.shadows.DropShadowBorder;
import mdlaf.themes.AbstractMaterialTheme;
import mdlaf.utils.MaterialBorders;
import mdlaf.utils.MaterialColors;

public class Theme extends AbstractMaterialTheme {

    @Override
    protected void installBorders() {
        super.installBorders();

        this.borderTable = MaterialBorders.LIGHT_LINE_BORDER;
        this.borderTableHeader =
                new BorderUIResource(
                        new DropShadowBorder(this.backgroundPrimary, 5, 3, 0.4f, 12, true, true, true, true));

        super.borderTitledBorder = MaterialBorders.LIGHT_LINE_BORDER;
        this.buttonBorder = new BorderUIResource(new FlatRoundBorder());
    }

    @Override
    protected void installDefaultColor() {
        super.installDefaultColor();
        this.buttonDisabledForeground = this.disableTextColor;

        super.titleColorTaskPane = this.textColor;
        super.backgroundToolTip = this.disableTextColor;
    }

    @Override
    protected void installColor() {
        ColorUIResource secondBackground = new ColorUIResource(54, 54, 54); // Dark gray background
        ColorUIResource disableBackground = new ColorUIResource(80, 80, 80); // Dark gray disabled background
        ColorUIResource accentColor = new ColorUIResource(38, 38, 51); // Dark gray accent color
        ColorUIResource selectedForeground = new ColorUIResource(220, 220, 220); // Light gray selected foreground
        ColorUIResource selectedBackground = new ColorUIResource(60, 60, 60); // Dark gray selected background
        this.backgroundPrimary = new ColorUIResource(50, 53, 68); // Very dark gray background
        this.highlightBackgroundPrimary = new ColorUIResource(0, 150, 136); // Teal highlight color

        this.textColor = new ColorUIResource(234, 234, 234); // Light gray text color
        this.disableTextColor = new ColorUIResource(150, 150, 150); // Gray disabled text color

        this.buttonBackgroundColor = secondBackground;
        this.buttonBackgroundColorMouseHover = new ColorUIResource(70, 70, 70); // Dark gray button hover background
        this.buttonDefaultBackgroundColorMouseHover = this.buttonBackgroundColorMouseHover;
        this.buttonDefaultBackgroundColor = secondBackground;
        this.buttonDisabledBackground = disableBackground;
        this.buttonFocusColor = this.textColor;
        this.buttonDefaultFocusColor = this.buttonFocusColor;
        this.buttonBorderColor = new ColorUIResource(80, 80, 80); // Dark gray button border color
        this.buttonColorHighlight = selectedBackground;

        this.selectedInDropDownBackgroundComboBox = this.buttonBackgroundColorMouseHover;
        this.selectedForegroundComboBox = this.textColor;

        this.menuBackground = this.backgroundPrimary;
        this.menuBackgroundMouseHover = this.buttonBackgroundColorMouseHover;

        this.arrowButtonColorScrollBar = secondBackground;
        this.trackColorScrollBar = accentColor;
        this.thumbColorScrollBar = disableBackground;

        this.trackColorSlider = this.textColor;
        this.haloColorSlider = MaterialColors.bleach(this.highlightBackgroundPrimary, 0.5f);

        this.highlightColorTabbedPane = this.buttonColorHighlight;
        this.borderHighlightColorTabbedPane = this.buttonColorHighlight;
        this.focusColorLineTabbedPane = this.highlightBackgroundPrimary;
        this.disableColorTabTabbedPane = disableBackground;

        this.backgroundTable = this.backgroundPrimary;
        this.backgroundTableHeader = this.backgroundPrimary;
        this.selectionBackgroundTable = this.buttonBackgroundColorMouseHover;
        this.gridColorTable = this.backgroundPrimary;
        this.alternateRowBackgroundTable = this.backgroundPrimary;

        this.backgroundTextField = accentColor;
        this.inactiveForegroundTextField = this.textColor;
        this.inactiveBackgroundTextField = accentColor;
        this.selectionBackgroundTextField = selectedBackground;
        this.selectionForegroundTextField = selectedForeground;
        super.disabledBackgroudnTextField = disableBackground;
        super.disabledForegroundTextField = this.disableTextColor;
        this.inactiveColorLineTextField = this.textColor;
        this.activeColorLineTextField = this.highlightBackgroundPrimary;

        this.mouseHoverButtonColorSpinner = this.buttonBackgroundColorMouseHover;
        this.titleBackgroundGradientStartTaskPane = secondBackground;
        this.titleBackgroundGradientEndTaskPane = secondBackground;
        this.titleOverTaskPane = selectedForeground;
        this.specialTitleOverTaskPane = selectedForeground;
        this.backgroundTaskPane = this.backgroundPrimary;
        this.borderColorTaskPane = new ColorUIResource(80, 80, 80); // Dark gray task pane border color
        this.contentBackgroundTaskPane = secondBackground;

        this.selectionBackgroundList = selectedBackground;
        this.selectionForegroundList = selectedForeground;

        this.backgroundProgressBar = disableBackground;
        this.foregroundProgressBar = this.highlightBackgroundPrimary;

        this.withoutIconSelectedBackgroundToggleButton = MaterialColors.COSMO_DARK_GRAY;
        this.withoutIconSelectedForegoundToggleButton = MaterialColors.BLACK;
        this.withoutIconBackgroundToggleButton = MaterialColors.GRAY_300;
        this.withoutIconForegroundToggleButton = MaterialColors.BLACK;

        this.colorDividierSplitPane = MaterialColors.COSMO_DARK_GRAY;
        this.colorDividierFocusSplitPane = selectedBackground;

        super.backgroundSeparator = MaterialColors.GRAY_300;
        super.foregroundSeparator = MaterialColors.GRAY_300;
    }

    @Override
    public void installUIDefault(UIDefaults table) {
        super.installUIDefault(table);
    }

    @Override
    public String getName() {
        return "Dark Theme";
    }

    @Override
    public boolean getButtonBorderEnableToAll() {
        return true;
    }

    @Override
    public boolean getButtonBorderEnable() {
        return true;
    }
}
