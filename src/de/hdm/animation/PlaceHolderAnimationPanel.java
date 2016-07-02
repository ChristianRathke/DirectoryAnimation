/*
 * Created on 02.07.2016
 *
 */
package de.hdm.animation;

import java.io.File;

public class PlaceHolderAnimationPanel extends DirectoryAnimationPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final PlaceHolderAnimationPanel thePanel = new PlaceHolderAnimationPanel();
    
    private PlaceHolderAnimationPanel(){}
    
    public void addFile(File targetFile) {}
    public void shrinkFile(File file) {}
}
