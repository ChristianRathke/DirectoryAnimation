/*
 * Created on 16.06.2016
 *
 */
package de.hdm.animation;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class DirectoryAnimation {
    
    private DirectoryAnimationPanel dap = new DirectoryAnimationPanel();

    public DirectoryAnimation(String directory) {
        dap.setDirectory(directory);

        JFrame frame = new JFrame("File Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(dap, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // frame.toFront();

        dap.runAnimation();
    }
    
    public static void main(String[] args) {
        new DirectoryAnimation(args.length > 0 ? args[0] : ".\\");
    }
    
    public void shrinkDir() {
        dap.shrinkDir();
    }
    
    public void spreadDir() {
        dap.spreadDir();
    }
}
