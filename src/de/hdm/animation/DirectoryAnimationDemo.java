/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

public class DirectoryAnimationDemo extends JFrame {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DirectoryAnimationPanel dap = new DirectoryAnimationPanel();

    public DirectoryAnimationDemo(String directory) {

        super(directory);
        dap.setFrame(this);
        dap.setDirectory(directory);

//        frame.addWindowListener(new WindowAdapter() {
//
//            @Override
//            public void windowClosing(WindowEvent we) {
//                setVisible(false);
//            }
//        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(dap, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setBackground(Color.white);
        setVisible(true);
    }
    
    
    
    public static void main(String[] args) {
        new DirectoryAnimationDemo(args.length > 0 ? args[0] : ".\\").runAnimation();
    }
    
    public void runAnimation() {
        dap.runAnimation();
    }
}
