package Main;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    public Frame() {
        this.setTitle("My Frame");
        this.setSize(new Dimension(500, 700));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ButtonPanel myPanel = new ButtonPanel();
        this.add(myPanel);
    }

    // add your code here

}
